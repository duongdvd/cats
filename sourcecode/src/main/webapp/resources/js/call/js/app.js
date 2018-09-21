;(function(window, QB, app, CONFIG, $, Backbone) {
    'use strict';

    $(function() {
        var sounds = {
            call: 'callingSignal',
            end: 'endCallSignal',
            rington: 'ringtoneSignal'
        };

        var recorder = null;
        var recorderTimeoutID;
        var isAudio = false;
        var isMissCall = true;

        var ui = {
            'income_call': '#income_call',
            'filterSelect': '.j-filter',
            'sourceFilter': '.j-source',
            'bandwidthSelect': '.j-bandwidth',
            'insertOccupants': function() {
                var $occupantsCont = $('.j-users');

                function cb($cont, res) {
                    $cont.empty()
                        .append(res)
                        .removeClass('wait');
                }

                return new Promise(function(resolve, reject) {
                    $occupantsCont.addClass('wait');

                    app.helpers.renderUsers().then(function(res) {
                        cb($occupantsCont, res.usersHTML);
                        resolve(res.users);
                    }, function(error) {
                        cb($occupantsCont, error.message);
                        reject('Not found users by tag');
                    });
                });
            }
        };

        var call = {
            callTime: 0,
            callTimer: null,
            updTimer: function() {
                this.callTime += 1000;

                $('#timer').removeClass('invisible')
                    .text( new Date(this.callTime).toUTCString().split(/ /)[4] );
            }
        };

        var callLogInfo = {};

        var remoteStreamCounter = 0;
        app.callees = {};

        var Router = Backbone.Router.extend({
            'routes': {
                'join': 'join',
                'dashboard': 'dashboard',
                '*query': 'relocated'
            },
            'container': $('.page'),
            'relocated': function() {
                var path = app.caller ? 'dashboard' : 'join';

                app.router.navigate(path, {'trigger': true});
            },
            'join': function() {
                /** Before use WebRTC checking WebRTC is available */
                if (!QB.webrtc) {
                    alert('Error: ' + CONFIG.MESSAGES.webrtc_not_avaible);
                    return;
                }

                if (!_.isEmpty(app.caller)) {
                    app.router.navigate('dashboard');
                    return false;
                }

                this.container
                    .removeClass('page-dashboard')
                    .addClass('page-join');

                app.helpers.setFooterPosition();

                app.caller = {};
                app.callees = {};
                app.calleesAnwered = [];
                app.calleesRejected = [];
                app.users = [];
            }
        });


        /**
         * Define all methods
         */
        app.startCall = startCall;
        app.onCallStatsReport = onCallStatsReport;
        app.onCallListener = onCallListener;
        app.onUpdateCallListener = onUpdateCallListener;
        app.onAcceptCallListener = onAcceptCallListener;
        app.onRejectCallListener = onRejectCallListener;
        app.onStopCallListener = onStopCallListener;

        app.onUserNotAnswerListener = onUserNotAnswerListener;
        app.onRemoteStreamListener = onRemoteStreamListener;

        app.onSessionCloseListener = onSessionCloseListener;
        app.onSessionConnectionStateChangedListener = onSessionConnectionStateChangedListener;
        app.onDisconnectedListener = onDisconnectedListener;


        /**
         * INIT
         */
        QB.init(
            CONFIG.CREDENTIALS.appId,
            CONFIG.CREDENTIALS.authKey,
            CONFIG.CREDENTIALS.authSecret,
            CONFIG.APP_CONFIG
        );
        var statesPeerConn = _.invert(QB.webrtc.PeerConnectionState);

        app.router = new Router();
        Backbone.history.start();

        $(document).on('click', '.end-call', function() {
            hangup();
        });

        function startCall(user) {
            callLogInfo.startTime = new Date();
            callLogInfo.isFromDevice = false;

            app.callees[user.id] = user.name;
            var
                $videoSourceFilter = $(ui.videoSourceFilter),
                $audioSourceFilter = $(ui.audioSourceFilter),
                $bandwidthSelect = $(ui.bandwidthSelect),
                bandwidth = $.trim($(ui.bandwidthSelect).val()),
                videoElems = '',
                mediaParams = {
                    'audio': {
                        deviceId: $audioSourceFilter.val() ? $audioSourceFilter.val() : undefined
                    },
                    'video': {
                        deviceId: $videoSourceFilter.val() ? $videoSourceFilter.val() : undefined
                    },
                    'options': {
                        'muted': true,
                        'mirror': true
                    },
                    'elemId': 'localVideo'
                };

            /** Check internet connection */
            if(!window.navigator.onLine) {
                console.log("No Internet Connection");
                return false;
            }

            /** Check callee */
            if(_.isEmpty(app.callees)) {
                $('#error_no_calles').modal();
                return false;
            }

            isAudio = true;

            app.currentSession = QB.webrtc.createNewSession(Object.keys(app.callees), isAudio ? QB.webrtc.CallType.AUDIO : QB.webrtc.CallType.VIDEO, null, {'bandwidth': bandwidth});

            mediaParams = {
                audio: true,
                video: false
            };
            app.currentSession.getUserMedia(mediaParams, function(err, stream) {
                if (err || !stream.getAudioTracks().length || (isAudio ? false : !stream.getVideoTracks().length)) {
                    var errorMsg = '';

                    app.currentSession.stop({});
                    alert('マイクとスピーカーを利用しようとする時エラーが発生しました。マイクやスピーカーが正しく接続されているかをご確認ください。');
                } else {
                    var callParameters = {};

                    if(isAudio){
                        callParameters.callType = 2;
                    }

                    // Call to users
                    //
                    var pushRecipients = [];
                    app.currentSession.call({}, function() {
                        if (!window.navigator.onLine) {
                            app.currentSession.stop({});
                            console.log("Connection error");
                        } else {

                            document.getElementById(sounds.call).play();

                            Object.keys(app.callees).forEach(function(id, i, arr) {

                                pushRecipients.push(id);
                            });


                        }
                    });

                    // and also send push notification about incoming call
                    // (corrently only iOS/Android users will receive it)
                    //
                    var params = {
                        notification_type: 'push',
                        user: {ids: pushRecipients},
                        environment: 'development', // environment, can be 'production' as well.
                        message: QB.pushnotifications.base64Encode('undefined' + ' is calling you')
                    };
                    //
                    QB.pushnotifications.events.create(params, function(err, response) {
                        if (err) {
                            console.log(err);
                        } else {
                            // success
                            console.log("Push Notification is sent.");
                        }
                    });
                }
            });
        }



        function hangup() {
            if(!_.isEmpty(app.currentSession)) {
                if(recorder && recorderTimeoutID) {
                    recorder.stop();
                }

                app.currentSession.stop({});
                app.currentSession = {};

                app.helpers.setFooterPosition();
                $('#operatorCallModal').modal('hide');

                return false;
            }
        }

        /** DECLINE */
        $(document).on('click', '.j-decline', function() {
            if (!_.isEmpty(app.currentSession)) {
                app.currentSession.reject({});

                $('#operatorCallModal').modal('hide');
                document.getElementById(sounds.rington).pause();
            }
        });

        /** ACCEPT */
        $(document).on('click', '.j-accept', function() {
            isAudio = app.currentSession.callType === QB.webrtc.CallType.AUDIO;

            var $videoSourceFilter = $(ui.sourceFilter),
                mediaParams;

            if(isAudio){
                mediaParams = {
                    audio: true,
                    video: false
                };
            } else {
                mediaParams = {
                    audio: true,
                    video: {
                        deviceId: $videoSourceFilter.val() ? $videoSourceFilter.val() : undefined
                    },
                    elemId: 'localVideo',
                    options: {
                        muted: true,
                        mirror: true
                    }
                };
            }

            document.getElementById(sounds.rington).pause();
            app.currentSession.getUserMedia(mediaParams, function(err, stream) {
                if (err || !stream.getAudioTracks().length || (isAudio ? false : !stream.getVideoTracks().length)) {
                    app.currentSession.stop({});

                } else {
                    var opponents = [app.currentSession.initiatorID];

                    /** get all opponents */
                    app.currentSession.opponentsIDs.forEach(function(userID, i, arr) {
                        if(userID != app.currentSession.currentUserID){
                            opponents.push(userID);
                        }
                    });
                    opponents.forEach(function(userID, i, arr) {

                        var peerState = app.currentSession.connectionStateForUser(userID),
                            userInfo = _.findWhere(app.users, {'id': +userID});
                    });

                    app.helpers.setFooterPosition();
                    app.currentSession.accept({});
                    $('#operatorCallModal .out-call').show();
                    $('#operatorCallModal .in-call').hide();
                }
            });
        });

        /** CHANGE FILTER */
        $(document).on('change', ui.filterSelect, function() {
            var filterName = $.trim( $(this).val() );

            app.helpers.changeFilter('#localVideo', filterName);

            if(!_.isEmpty(app.currentSession)) {
                app.currentSession.update({'filter': filterName});
            }
        });

        $(document).on('click', '.j-callees__callee__video', function() {
            var $that = $(this),
                userId = +($(this).data('user')),
                activeClass = [];

            if( app.currentSession.peerConnections[userId].stream && !$that.srcObject ) {
                if( $that.hasClass('active') ) {
                    $that.removeClass('active');

                    app.currentSession.detachMediaStream('main_video');
                    app.helpers.changeFilter('#main_video', 'no');
                    app.mainVideo = 0;
                    remoteStreamCounter = 0;
                } else {
                    $('.j-callees__callee_video').removeClass('active');
                    $that.addClass('active');

                    app.helpers.changeFilter('#main_video', 'no');

                    activeClass = _.intersection($that.attr('class').split(/\s+/), app.filter.names.split(/\s+/) );

                    /** set filter to main video if exist */
                    if(activeClass.length) {
                        app.helpers.changeFilter('#main_video', activeClass[0]);
                    }

                    app.currentSession.attachMediaStream('main_video', app.currentSession.peerConnections[userId].stream);
                    app.mainVideo = userId;
                }
            }
        });

        $(document).on('click', '.j-caller__ctrl', function() {
            var $btn = $(this),
                isActive = $btn.hasClass('active');

            if( _.isEmpty( app.currentSession)) {
                return false;
            } else {
                if(isActive) {
                    $btn.removeClass('active');
                    app.currentSession.unmute( $btn.data('target') );
                } else {
                    $btn.addClass('active');
                    app.currentSession.mute( $btn.data('target') );
                }
            }
        });

        /** Close tab or browser */
        // $( window ).unload(function() {
        //     localStorage.removeItem('isAuth');
        // });

        /**
         * QB Event listener.
         *
         * [Recommendation]
         * We recomend use Function Declaration
         * that SDK could identify what function(listener) has error
         *
         * Chat:
         * - onDisconnectedListener
         * WebRTC:
         * - onCallListener
         * - onCallStatsReport
         * - onUpdateCallListener
         *
         * - onAcceptCallListener
         * - onRejectCallListener
         * - onUserNotAnswerListener
         *
         * - onRemoteStreamListener
         *
         * - onStopCallListener
         * - onSessionCloseListener
         * - onSessionConnectionStateChangedListener
         */

        function onDisconnectedListener() {
            console.log('onDisconnectedListener.');
        }

        function onCallStatsReport(session, userId, stats, error) {
            console.group('onCallStatsReport');
            console.log('userId: ', userId);
            console.log('session: ', session);
            console.log('stats: ', stats);
            console.log('start Date:' + (new Date()));
            console.groupEnd();
        }

        function onSessionCloseListener(session){
            console.log('onSessionCloseListener: ', session);

            document.getElementById(sounds.call).pause();
            document.getElementById(sounds.end).play();

            $('.j-caller__ctrl').removeClass('active');
            $(ui.sourceFilter).attr('disabled', false);
            $(ui.bandwidthSelect).attr('disabled', false);
            $('.j-callees').empty();
            $('.frames_callee__bitrate').hide();

            app.currentSession.detachMediaStream('main_video');
            app.currentSession.detachMediaStream('localVideo');

            remoteStreamCounter = 0;

            if(document.querySelector('.j-actions[hidden]')){
                document.querySelector('.j-actions[hidden]').removeAttribute('hidden');
            }
            if(document.querySelector('.j-caller__ctrl')){
                document.querySelector('.j-caller__ctrl').removeAttribute('hidden');
            }
            $("#operatorCallModal").modal('hide');
            $("#timer").hide();
        }

        function onUserNotAnswerListener(session, userId) {
            console.group('onUserNotAnswerListener.');
            console.log('UserId: ', userId);
            console.log('Session: ', session);
            console.groupEnd();

            var opponent = _.findWhere(app.users, {'id': +userId});

            console.log('No Answer');
        }

        function onCallListener(session, extension) {
            console.group('onCallListener.');
            console.log('Session: ', session);
            console.log('Extension: ', extension);
            console.groupEnd();

            app.currentSession = session;
            $('#operatorCallModal').modal('hide');

            callLogInfo.isFromDevice = true;
            callLogInfo.fromCallId = session.initiatorID;
            var carMarker = getCarCall(callLogInfo.fromCallId);
            callLogInfo.latitude = carMarker.getLatLon().lat;
            callLogInfo.longitude = carMarker.getLatLon().lon;
            callLogInfo.deviceId = carMarker.deviceId;
            callLogInfo.toCallName = carMarker.toCallName;
            callLogInfo.userTags = carMarker.userTags;
            callLogInfo.routeDetailId = carMarker.routeDetailId;
            callLogInfo.startTime = new Date();

            // check the current session state
            if (app.currentSession.state !== QB.webrtc.SessionConnectionState.CLOSED){
                $('#callStatus').text("Calling...");
                $('#operatorCallModal').modal({backdrop: 'static', keyboard: false});
                $('#operatorCallModal .out-call').hide();
                $('#operatorCallModal .in-call').show();
                document.getElementById(sounds.rington).play();
            }
        }

        function onRejectCallListener(session, userId, extension) {
            console.group('onRejectCallListener.');
            console.log('UserId: ' + userId);
            console.log('Session: ' + session);
            console.log('Extension: ' + JSON.stringify(extension));
            console.groupEnd();

            var user = _.findWhere(app.users, {'id': +userId}),
                userCurrent = _.findWhere(app.users, {'id': +session.currentUserID});

            /** It's for p2p call */
            if(session.opponentsIDs.length === 1) {
            } else {
                var userInfo = _.findWhere(app.users, {'id': +userId});
                app.calleesRejected.push(userInfo);
            }
        }

        function onStopCallListener(session, userId, extension) {
            console.group('onStopCallListener.');
            console.log('UserId: ', userId);
            console.log('Session: ', session);
            console.log('Extension: ', extension);
            console.groupEnd();

            app.helpers.notifyIfUserLeaveCall(session, userId, 'hung up the call', 'Hung Up');

            if(recorder) {
                recorder.stop();
            }
        }

        function onAcceptCallListener(session, userId, extension) {
            console.group('onAcceptCallListener.');
            console.log('UserId: ', userId);
            console.log('Session: ', session);
            console.log('Extension: ', extension);
            console.groupEnd();
            document.getElementById(sounds.rington).pause();
            var userInfo = _.findWhere(app.users, {'id': +userId}),
                filterName = $.trim( $(ui.filterSelect).val() );

            document.getElementById(sounds.call).pause();
            app.currentSession.update({'filter': filterName});

            /** update list of callee who take call */
            app.calleesAnwered.push(userInfo);
            $('#timer').show();
        }

        function onRemoteStreamListener(session, userId, stream) {
            console.group('onRemoteStreamListener.');
            console.log('userId: ', userId);
            console.log('Session: ', session);
            console.log('Stream: ', stream);
            console.groupEnd();

            var state = app.currentSession.connectionStateForUser(userId),
                peerConnList = QB.webrtc.PeerConnectionState;

            console.log("Call State:" + state);
            if(state === peerConnList.DISCONNECTED || state === peerConnList.FAILED || state === peerConnList.CLOSED) {
                return false;
            }

            app.currentSession.peerConnections[userId].stream = stream;

            console.info('onRemoteStreamListener add video to the video element', stream);

            app.currentSession.attachMediaStream('remote_video', stream);

            if( remoteStreamCounter === 0) {
                $('#remote_video_' + userId).click();

                app.mainVideo = userId;
                ++remoteStreamCounter;
            }

            if(!call.callTimer) {
                call.callTimer = setInterval( function(){ call.updTimer.call(call); }, 1000);
            }

            $('.frames_callee__bitrate').show();
        }

        function onUpdateCallListener(session, userId, extension) {
            console.group('onUpdateCallListener.');
            console.log('UserId: ' + userId);
            console.log('Session: ' + session);
            console.log('Extension: ' + JSON.stringify(extension));
            console.groupEnd();

            app.helpers.changeFilter('#remote_video_' + userId, extension.filter);

            if (+(app.mainVideo) === userId) {
                app.helpers.changeFilter('#main_video', extension.filter);
            }
        }

        function onSessionConnectionStateChangedListener(session, userId, connectionState) {
            console.group('onSessionConnectionStateChangedListener.');
            console.log('UserID:', userId);
            console.log('Session:', session);
            console.log('Сonnection state:', connectionState, statesPeerConn[connectionState]);
            console.groupEnd();

            var connectionStateName = _.invert(QB.webrtc.SessionConnectionState)[connectionState],
                $calleeStatus = $('#callStatus'),
                isCallEnded = false;

            if (connectionState === QB.webrtc.SessionConnectionState.CONNECTING) {
                isMissCall = false;
            }

            $calleeStatus.text(connectionStateName);
            if(connectionState === QB.webrtc.SessionConnectionState.CLOSED){
                app.helpers.toggleRemoteVideoView(userId, 'clear');

                if(app.mainVideo === userId) {
                    $('#remote_video_' + userId).removeClass('active');

                    app.helpers.changeFilter('#main_video', 'no');
                    app.mainVideo = 0;
                }

                if( !_.isEmpty(app.currentSession) ) {
                    if ( Object.keys(app.currentSession.peerConnections).length === 1 || userId === app.currentSession.initiatorID) {
                        $(ui.income_call).modal('hide');
                        document.getElementById(sounds.rington).pause();
                    }
                }

                isCallEnded = _.every(app.currentSession.peerConnections, function(i) {
                    return i.iceConnectionState === 'closed';
                });

                /** remove filters */
                if(isCallEnded) {
                    console.log("isMissCall:" + isMissCall);
                    document.getElementById(sounds.rington).pause();
                    app.helpers.changeFilter('#localVideo', 'no');
                    app.helpers.changeFilter('#main_video', 'no');
                    $(ui.filterSelect).val('no');

                    app.calleesAnwered = [];
                    app.calleesRejected = [];
                    app.network[userId] = null;

                    callLogInfo.endTime = new Date();
                    callLogInfo.callType = isMissCall ? "MISS" : "NORMAL";
                    saveCallLogInfo(callLogInfo);

                    isMissCall = true;
                    $("#operatorCallModal").modal('hide');
                }

                if (app.currentSession.currentUserID === app.currentSession.initiatorID && !isCallEnded) {
                    var userInfo = _.findWhere(app.users, {'id': +userId});

                    /** get array if users without user who ends call */
                    app.calleesAnwered = _.reject(app.calleesAnwered, function(num){ return num.id === +userId; });
                    app.calleesRejected.push(userInfo);
                }

                if( _.isEmpty(app.currentSession) || isCallEnded ) {
                    if(call.callTimer) {
                        $('#timer').addClass('invisible');
                        clearInterval(call.callTimer);
                        call.callTimer = null;
                        call.callTime = 0;
                        app.helpers.network = {};
                    }
                }
            }
        }
    });
}(window, window.QB, window.app, window.CONFIG,  jQuery, Backbone));
