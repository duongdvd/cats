<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<c:set value="${pageContext.request.contextPath}" var="ctx"/>
<div class="modal fade" id="operatorCallModal" role="dialog">
    <div class="modal-dialog modal-sm">
        <div class="modal-body hold-transition bg-navy color-palette" style="padding-bottom: 60px;">
            <div class="row">
                <div class="frames__main_timer invisible" id="timer"></div>
                <div class="text-center">
                    <i class="fa fa-user fa-5x"></i>
                    <br>
                    <h3 class="drive-name profile-username text-center"></h3>
                    <h4 class="plate-number profile-username text-center"></h4>

                    <div id="callStatus" class="text-muted text-center">Calling...</div>
                    <br>
                    <div class="btn-center nav in-call">
                        <a href="#" class="btn-call btn-call-right bg-green-active accept-call j-accept">
                            <i class="fa fa-fw fa-phone"></i>
                        </a>
                        <a href="#" data-call="audio"
                           class="btn-call btn-call-left bg-red-active j-decline reject-call caller__ctrl_btn j-actions m-audio_call end-call">
                            <i class="fa fa-fw fa-phone" style="transform: rotate(135deg);"></i>
                        </a>
                    </div>
                    <div class="btn-center nav out-call">
                        <a href="#" id="endCall" data-call="audio"
                           class="btn-call btn-call-center bg-red-active j-decline reject-call caller__ctrl_btn m-audio_call end-call">
                            <i class="fa fa-fw fa-phone" style="transform: rotate(135deg);"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="wrapper j-wrapper hide">
                <main class="app">
                    <div class="page">
                        <!-- JOIN -->
                        <div class="dashboard j-dashboard">
                        </div>
                    </div>
                </main>

                <footer class="footer j-footer invisible">
                    <p class="j-config footer-config"></p>
                </footer>
            </div>

            <div class="modal fade" id="error_no_calles" tabindex="-1">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title">Error</h4>
                        </div>

                        <div class="modal-body">
                            <p class="text-danger">No user to call</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- SOUNDS -->
            <audio id="endCallSignal" preload="auto">
                <source src="${ctx}/resources/js/call/audio/end_of_call.ogg" type="audio/ogg"/>
                <source src="${ctx}/resources/js/call/audio/end_of_call.mp3" type="audio/mp3"/>
            </audio>

            <audio id="callingSignal" loop preload="auto">
                <source src="${ctx}/resources/js/call/audio/calling.ogg" type="audio/ogg"/>
                <source src="${ctx}/resources/js/call/audio/calling.mp3" type="audio/mp3"/>
            </audio>

            <audio id="ringtoneSignal" loop preload="auto">
                <source src="${ctx}/resources/js/call/audio/ringtone.ogg" type="audio/ogg"/>
                <source src="${ctx}/resources/js/call/audio/ringtone.mp3" type="audio/mp3"/>
            </audio>

            <div class="frames_callee callees__callee j-callee hidden">
                <div class="frames_callee__inner">
                    <p class="frames_callee__status j-callee_status_53304849"></p>
                    <div class="qb-video">
                        <audio class="j-callees__callee__video qb-video_source"
                               id="remote_video"
                               data-user="">
                        </audio>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>