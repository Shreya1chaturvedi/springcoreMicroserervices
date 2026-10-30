<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>

<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
</head>
<body>

<h2>Something went wrong</h2>

<p>${exception.message}</p>

<a href="${pageContext.request.contextPath}/">Back to Home</a>

</body>
</html>