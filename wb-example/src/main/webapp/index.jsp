<%@ page import="com.cqth.utils.storage.StorageFactory" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List s = StorageFactory.getStorage(List.class).get("userList");
    session.setAttribute("adasdasd", "aaaa");
%>
<html>
<head>
    <title></title>
</head>
<body>
<%=s%>
<%= session.getAttribute("adasdasd")%>
</body>
</html>
