<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<script src="${pageContext.request.contextPath}/JS/jQuery.js"></script>
<style>
.loading {
	width: 100%;
	height: 100%;
	position: fixed;
	top: 0;
	left: 0;
	z-index: 100;
	background-color: #fff;
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
	<div class="loading">
		<div class="pic"></div>
	</div>
	<script type="text/javascript">

</script>
</body>
</html>