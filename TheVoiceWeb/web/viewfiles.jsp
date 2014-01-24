<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html><head>
    <link href="${pageContext.request.contextPath}/style.css" type="text/css" rel="stylesheet">
    <title>View jobs</title></head><body>
<h1>Recorded videos</h1>
<p>These are the videos found:</p>
<table cellspacing="0"><tr><th>Video</th></tr>
<c:forEach varStatus="i" items="${fileModel.videos}" var="video">
    ${video}

</c:forEach>
    </table>
<br />
<a href="showjobs.do">Back to overview.</a>
<br />

<p>There is ${fileModel.space}% = ${fileModel.timespace} hours left on the disk</p>
</body></html>
