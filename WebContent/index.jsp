<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/login-register.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/bubble.css" />
<link href="${pageContext.request.contextPath}/CSS/bootstrap.min.css"
	rel="stylesheet">
<script src="${pageContext.request.contextPath}/JS/jQuery.js"></script>
<style>
.loading {
	width: 100%;
	height: 100%;
	position: fixed;
	top: 0;
	left: 0;
	background-color: #fff;
	z-index: -1;
}

.loading .pic {
	width: 160px;
	height: 22px;
	background: url(${pageContext.request.contextPath}/PICTURE/load.gif);
	position: absolute;
	top: 0;
	left: 0;
	bottom: 0;
	right: 0;
	margin: auto;
}
</style>
</head>
<body>
	<!-- loading animation -->
	<div class="loading">
		<div class="pic"></div>
	</div>

	<div style="z-index: 100;" class="form">
		<!-- 气泡动图 -->
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

		<form action="${pageContext.request.contextPath}/login/login"
			method="post" onsubmit="load()">
			<div style="margin-top: 200px">
				<!-- 姓名输入框，需要注意的是name属性和后面的action属性相对应 -->
				<div>
					<div class="sp">username:&nbsp</div>
					<input name="username" type="text" class="form-control"
						value="${ sessionScope.username}"
						style="height: 96px; font-size: 46px; width: 59%;">
				</div>
				<br />
				<!-- 密码输入框 -->
				<div>

					<div class="sp">password:&nbsp</div>
					<input name="password" type="password" class="form-control"
						value="${ sessionScope.userpassword}"
						style="height: 96px; font-size: 46px; width: 59%;">
				</div>

				<input id="reg" class="btn btn-default" type="button"
					value="Register"
					style="margin-top: 2%; height: 106px; font-size: 76px; width: 99%;">

				<input class="btn btn-default" type="submit" value="Submit"
					style="margin-top: 2%; height: 106px; font-size: 76px; width: 99%;">
		</form>
	</div>
</body>
<script type="text/javascript">
	//注册界面
	var btn=document.getElementById("reg");
	btn.onclick=function(){
		window.location.href='${pageContext.request.contextPath}/register.jsp';
	}
	
	//点击登录等待动画
	function load(){
		$(".form").fadeOut();
	}
</script>
</body>
</html>