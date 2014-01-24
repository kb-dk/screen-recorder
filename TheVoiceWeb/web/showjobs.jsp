<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html><head>
    <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script type="text/javascript">
        displayTime = function() {
            $.ajax({
                url: "time.do",
                cache: false
            }).done(function( html ) {
                        $("p#time").text(html);
                    });
        }

        $(document).ready(function() {
            setInterval(function(){displayTime();}, 1000);
        });
    </script>
    <link href="${pageContext.request.contextPath}/style.css" type="text/css" rel="stylesheet">
    <title>View jobs</title></head><body>
<h1>Current jobs</h1>
<p>These are the job files found:</p>
<table cellspacing="0"><tr><th>Job name</th> <th>Date</th><th>Colission</th><th>Delete</th></tr>
    <c:forEach varStatus="i" items="${fileModel.files}" var="file">
        ${file}

    </c:forEach>
</table>
<br />
<a href="new.do">New job</a>
<br />
<br />
<a href="viewfiles.do">Recorded videos</a>
<br />
<p>There is ${fileModel.space}% = ${fileModel.timespace} hours left on the disk</p>
<p id="time">${fileModel.time}</p>
</body></html>
