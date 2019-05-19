<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>register</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/login-register.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/bubble.css" />
<link href="${pageContext.request.contextPath}/CSS/bootstrap.min.css"
	rel="stylesheet">
</head>
<!-- <body background="${pageContext.request.contextPath}/PICTURE/background-register.jpg"> -->

<body>
	<div class="bubbles">
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
		<div class="bubble"></div>
	</div>
</body>

<form action="${pageContext.request.contextPath}/login/register"
	method="post">
	<div style="margin-top: 200px">
		<div>
			<!-- <div class="sp">username:&nbsp</div><input class="txt" type="text" name="reusername"> -->
			<div class="sp">username:&nbsp</div>
			<input name="reusername" type="text" class="form-control"
				style="height: 96px; font-size: 46px; width: 59%;">
		</div>
		<br />
		<div>
			<!-- <div class="sp">password:&nbsp</div><input class="txt" type="password" name="repassword"> -->
			<div class="sp">password:&nbsp</div>
			<input name="repassword" type="password" class="form-control"
				style="height: 96px; font-size: 46px; width: 59%;">
		</div>
		<!-- <input class="sub" type="submit" value="submit"/> -->
		<input class="btn btn-default" type="submit" value="Submit"
			style="margin-top: 2%; height: 106px; font-size: 76px; width: 99%;">
</form>
</body>
</html>