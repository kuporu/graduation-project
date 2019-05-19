<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<style>
a {
	text-decoration: none;
	font-size: 20px;
	color: black;
}
</style>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
	<p style="font-size: 66px">登陆(注册)失败: (</p>
	<!-- 跳转（加了namespace,EL表达式取出当前项目名，这里取出"/graduationProject"） -->
	<a href="${pageContext.request.contextPath}/index.jsp">返回登陆</a>
</body>
</html>