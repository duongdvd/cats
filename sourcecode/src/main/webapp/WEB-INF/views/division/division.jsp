<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="division.division"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>

    <script>
        function performClick(elemId) {
            var elem = document.getElementById(elemId);
            if (elem && document.createEvent) {
                var evt = document.createEvent("MouseEvents");
                evt.initEvent("click", true, false);
                elem.dispatchEvent(evt);
            }
        }
    </script>
    <!-- jsTree -->
    <link rel="stylesheet" href="${ctx}/resources/jstree/dist/themes/default/style.min.css"/>
    <script src="${ctx}/resources/jstree/dist/jstree.min.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/division.js"></script>
    <div class="content-wrapper">
        <section class="content-header">

        </section>

        <section id="content" class="content">
            <div class="col-md-4">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title col-md-9"><spring:message code="division.titleSearch"/></h3>
                    </div>
                    <div class="box-body">
                        <div id="alertDeleteDivision" class="bootstrap-alert"></div>
                        <form id="s">
                            <div class="input-group input-group-sm">
                                <input class="form-control" type="text" id="q">
                                <span class="input-group-btn">
                                    <button type="submit" class="btn btn-info btn-flat"><i class="fa fa-search"></i>
                                        <spring:message code="division.search"/>
                                    </button>
                                </span>
                            </div>
                        </form>
                        <div id="jsTree"></div>
                    </div>
                </div>
            </div>
            <div id="divisionInfo" class="col-md-8" style="display: none">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title col-md-9"><spring:message code="division.information"/></h3>
                    </div>
                    <div class="form-horizontal">
                    <div class="box-body">
                        <div id="formDivision">
                            <input type="text" hidden name="parentDivisionsId"
                                   id="parentDivisionsId">
                            <input type="text" hidden name="id" id="id">
                            <div class="col-md-12 mt-1">
                                <%@ include file="/resources/layout/messageList.jsp" %>
                            </div>
                            <div class="col-md-12">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="divisionName" class="control-label col-md-4 required-field"><spring:message
                                                code="division.divisionName"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="divisionName"
                                                   name="divisionName">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="divisionAddress" class="control-label col-md-4 required-field"><spring:message
                                                code="division.divisionAddress"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="divisionAddress"
                                                   name="divisionAddress">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="parentDivisionsId" class="control-label col-md-4"><spring:message
                                                code="division.parentDivision"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" disabled class="form-control" name="parentDivisionName">
                                        </div>
                                    </div>
                                    <div id="userSelect" class="form-group" style="display: none">
                                        <label for="usersId" class="control-label col-md-4"><spring:message
                                                code="division.usersManaged"/>:</label>
                                        <div class="col-md-8">
                                            <select name="usersId" id="usersId" class="form-control">
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="control-label col-md-4 required-field"><spring:message
                                                code="division.divisionStatus"/>:</label>
                                        <div class="col-md-8">
                                            <label class="radio-inline">
                                                <input type="radio" value="1" name="divisionStatus">
                                                <spring:message code="route.active"/>
                                            </label>
                                            <label class="radio-inline">
                                                <input type="radio" value="0" name="divisionStatus">
                                                <spring:message code="route.inActive"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div id="usersForm" style="display: none" class="col-md-12">
                                <h4><spring:message code="division.userInfo"/></h4>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="loginId" class="control-label col-md-4 required-field"><spring:message
                                                code="division.user.LoginId"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="loginId" name="loginId">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="userEmail" class="control-label col-md-4"><spring:message
                                                code="division.user.email"/>:</label>
                                        <div class="col-md-8">
                                            <input type="email" class="form-control" id="userEmail" name="userEmail">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="mobilePhone" class="control-label col-md-4"><spring:message
                                                code="division.user.mobilePhone"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="mobilePhone" name="mobilePhone">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="firstName" class="control-label col-md-4 required-field"><spring:message
                                                code="division.user.firstName"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="firstName" name="firstName">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="lastName" class="control-label col-md-4 required-field"><spring:message
                                                code="division.user.lastName"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="lastName" name="lastName">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="fixedPhone" class="control-label col-md-4"><spring:message
                                                code="division.user.fixedPhone"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="fixedPhone" name="fixedPhone">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="faxNumber" class="control-label col-md-4"><spring:message
                                                code="division.user.faxNumber"/>:</label>
                                        <div class="col-md-8">
                                            <input type="text" class="form-control" id="faxNumber" name="faxNumber">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-12 mt-1 text-right">
                                <button id="butUpdateDivision" class="btn btn-primary"><spring:message
                                        code="division.update"/></button>
                                <button class="btn btn-primary" id="butAddDivision"><spring:message
                                        code="division.create"/></button>
                            </div>
                        </div>
                    </div>
                    </div>
                </div>
            </div>

            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
<%@ include file="/resources/layout/footer.jsp" %>