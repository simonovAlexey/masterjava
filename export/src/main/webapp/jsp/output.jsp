<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User output</title>
</head>
<body>
<h1>Users from file: ${fileName}</h1>
<table border="1">
    <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Flag</th>
    </tr>
    <c:forEach items="${list}" var="user">
        <tr>
            <td>${user.value}</td>
            <td>${user.email}</td>
            <td>${user.flag}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>

