<!-- end of .wrapper -->
</div>
<footer id="footer" class="div_left-230">
    <div class="container">
        <p class="text-muted credit">
            <spring:message code="company.contentFooter"/>
        </p>
    </div>
</footer>
    <!-- Select2 -->
    <script src="${ctx}/resources/bower_components/select2/dist/js/select2.full.min.js"></script>
    <script>
        $(function () {
            //Initialize Select2 Elements
            $('.select2').select2()
        });
    </script>
</body>
</html>
