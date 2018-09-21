<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="company.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <div class="content-wrapper">
        <section class="content-header">
            <h1><spring:message code="company.title.list"/></h1>
        </section>
        <section id="content" class="content">
            <p>
                <a href="${ctx}/addCompanyView" class="btn btn-primary tooltips"
                   id="create" title="<spring:message code="company.addNew"/>" data-toggle="tooltip">
                    <span class="glyphicon glyphicon-plus"></span>
                    <spring:message code="company.addNew"/>
                </a>
            </p>
            <spring:htmlEscape defaultHtmlEscape="true"/>
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchCompanies" modelAttribute="searchCompanyForm">
                            <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="route.id"
                                           orderby="desc">
                                        <thead>
                                        <tr>
                                            <th id="id_title" class="text-left">
                                                <spring:message code="company.title.id"/>
                                            </th>
                                            <th id="name_title" class="text-left">
                                                <spring:message code="company.title.companyName"/>
                                            </th>
                                            <th id="email_title" class="text-left">
                                                <spring:message code="company.title.companyMail"/>
                                            </th>
                                            <th id="description_title" class="text-left">
                                                <spring:message code="company.title.companyDescription"/>
                                            </th>
                                            <th id="create_date_title" class="text-left">
                                                <spring:message code="company.title.createDate"/>
                                            </th>
                                            <th id="update_date_title" class="text-left">
                                                <spring:message code="company.title.updateDate"/>
                                            </th>
                                            <th id="status" class="text-left">
                                                <spring:message code="company.title.status"/>
                                            </th>
                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips"
                                                        id="apply"
                                                        data-toggle="tooltip" title="<spring:message code="company.btn.search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span><spring:message code="company.btn.search"/>
                                                </button>
                                                <a class="btn btn-default tooltips reset_filter"
                                                   data-toggle="tooltip" title="<spring:message code="company.reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span><spring:message code="company.reset"/>
                                                </a>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td>
                                                <select name="id" class="form-control input-sm select2" style="width: 100%">
                                                    <c:choose>
                                                        <c:when test="${empty divisionIdList}">
                                                            <option value="" selected><spring:message
                                                                    code="company.allId"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <option value="${division}">${division}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                    code="company.allId"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <c:choose>
                                                                        <c:when test="${division eq searchCompanyForm.id}">
                                                                            <option value="${division}"
                                                                                    selected>${division}</option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option value="${division}">${division}</option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>


                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="divisionName"
                                                       value="${searchCompanyForm.divisionName}"
                                                       id="divisionName"
                                                       class="form-control input-sm" title="<spring:message code="company.title.companyName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="divisionMail"
                                                       value="${searchCompanyForm.divisionMail}"
                                                       id="divisionMail"
                                                       class="form-control input-sm" title="<spring:message code="company.title.companyMail"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="description"
                                                       value="${searchCompanyForm.description}"
                                                       id="description"
                                                       class="form-control input-sm" title="<spring:message code="company.title.companyDescription"/>">
                                            </td>
                                            <td>
                                                <div class="input-group input-daterange">
                                                    <input id="fromCreateDate" type="date" name="fromCreateDate"
                                                           data-date-format="yyyy-MM-dd"
                                                           class="form-control input-sm form-search-from"
                                                           value="${searchCompanyForm.fromCreateDate}"
                                                           data-date-format="yyyy-MM-dd"
                                                           title="<spring:message code="company.title.fromCreateDate"/>">
                                                    <span class="input-group-addon form-search-middle">~</span>
                                                    <input id="toCreateDate" type="date" title="<spring:message code="company.title.toCreateDate"/>"
                                                           data-date-format="yyyy-MM-dd"
                                                           class="form-control input-sm form-search-to"
                                                           name="toCreateDate"
                                                           value="${searchCompanyForm.toCreateDate}">
                                                </div>
                                            </td>
                                            <td>
                                                <div class="input-group input-daterange">
                                                    <input id="fromUpdateDate" type="date"
                                                           class="form-control input-sm form-search-from"
                                                           data-date-format="yyyy-MM-dd"
                                                           name="fromUpdateDate"
                                                           value="${searchCompanyForm.fromUpdateDate}"
                                                           title="<spring:message code="company.title.fromUpdateDate"/>">
                                                    <span class="input-group-addon form-search-middle">~</span>
                                                    <input id="toUpdateDate" type="date" data-date-format="yyyy-MM-dd"
                                                           class="form-control input-sm form-search-to"
                                                           name="toUpdateDate" title="<spring:message code="company.title.toUpdateDate"/>"
                                                           value="${searchCompanyForm.toUpdateDate}">
                                                </div>
                                            </td>
                                            <td>
                                                <select name="status" class="form-control input-sm">
                                                    <c:choose>
                                                        <c:when test="${empty searchCompanyForm.status}">
                                                            <option value="" selected><spring:message
                                                                    code="company.allStatus"/></option>
                                                            <option value="1"><spring:message
                                                                    code="company.active"/></option>
                                                            <option value="0"><spring:message
                                                                    code="company.inActive"/></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                    code="company.allStatus"/></option>
                                                            <c:choose>
                                                                <c:when test="${searchCompanyForm.status == 1}">
                                                                    <option value="1" selected><spring:message
                                                                            code="company.active"/></option>
                                                                    <option value="0"><spring:message
                                                                            code="company.inActive"/></option>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <option value="1"><spring:message
                                                                            code="company.active"/></option>
                                                                    <option value="0" selected><spring:message
                                                                            code="company.inActive"/></option>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </select>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:if test="${not empty companyList}">
                                            <c:forEach var="company" items="${companyList}">
                                                <tr>
                                                    <td class="text-center"><c:out value="${company.id}"/></td>
                                                    <td class="text-center"><c:out
                                                            value="${company.divisionName}"/></td>
                                                    <td class="text-center"><c:out
                                                            value="${company.divisionMail}"/></td>
                                                    <td class="text-center"><c:out value="${company.description}"/></td>
                                                    <td class="text-center"><fmt:formatDate
                                                            value="${company.createDate}" pattern="yyyy-MM-dd"/></td>
                                                    <td class="text-center"><fmt:formatDate
                                                            value="${company.updateDate}" pattern="yyyy-MM-dd"/></td>
                                                    <td class="text-center">
                                                        <c:choose>
                                                            <c:when test="${company.status == 1}"><spring:message
                                                                    code="company.title.activeStatus"/></c:when>
                                                            <c:otherwise><spring:message
                                                                    code="company.title.inactiveStatus"/></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-right">
                                                        <span class="">
                                                            <a class="btn btn-warning submit regist"
                                                                href="${ctx}/editCompany/${company.id}">
                                                                <span class="glyphicon glyphicon-pencil"></span>
                                                                <spring:message code="company.btn.edit"/>
                                                            </a>
                                                        </span>
                                                        <span class="">
                                                            <a class="btn bg-red-active delete tooltips"
                                                               href="${ctx}/deleteCompany?id=${company.id}" title="<spring:message code="company.btn.delete"/>"
                                                               data-toggle="tooltip">
                                                                    <span class="glyphicon glyphicon-trash"></span>
                                                                <spring:message code="company.btn.delete"/>
                                                            </a>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>

                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </s:form>
                            <div class="row">
                                <div class="col-sm-5">
                                    <div class="dataTables_info"><spring:message code="company.total"/>: ${count}</div>
                                </div>
                                <div class="col-sm-7 text-right">
                                    <tag:paginate max="10" offset="${offset}" count="${count}"
                                                  uri="searchCompanyPaging"
                                                  next="&raquo;"
                                                  previous="&laquo;" steps="10"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
<%@include file="/resources/layout/footer.jsp" %>
