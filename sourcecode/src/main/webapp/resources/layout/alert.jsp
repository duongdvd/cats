<div id="alertList">
    <c:if test="${not empty deleteResult}">
        <c:if test="${deleteResult eq true}">
            <div class="alert alert-success">
                <strong><spring:message code="delete.success"/></strong>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        <c:if test="${deleteResult eq false}">
            <div class="alert alert-danger">
                <strong><spring:message code="delete.error"/></strong>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
    </c:if>

    <c:if test="${not empty addEditResult}">
        <c:if test="${addEditResult eq true}">
            <div class="alert alert-success">
                <strong><spring:message code="edit.success"/></strong>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        <c:if test="${addEditResult eq false}">
            <div class="alert alert-danger">
                <strong><spring:message code="edit.error"/></strong>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
    </c:if>
</div>