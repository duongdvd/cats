<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="messageList">
    <c:if test="${not empty Messages}">
        <c:forEach var="message" items="${Messages}">
            <div class="alert alert-${message.getMessageTypeString()}">
                <b><c:out value="${message.content}"/></b>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:forEach>
    </c:if>
</div>