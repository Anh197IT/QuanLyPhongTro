<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Đăng ký tài khoản</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { max-width: 420px; margin: 60px auto; padding: 20px; border: 1px solid #ddd; border-radius: 6px; }
        label { display:block; margin: 10px 0 4px; }
        input[type=text], input[type=email], input[type=password] { width: 100%; padding: 8px; }
        button { margin-top: 12px; padding: 8px 12px; background:#1976d2; color:#fff; border:none; border-radius:4px; cursor:pointer; }
        .msg { margin-top:10px; color:#d32f2f; }
        .ok { color:#2e7d32; }
        a { color: #1976d2; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <h2>Đăng ký tài khoản</h2>
    <c:if test="${not empty error}"><div class="msg">${error}</div></c:if>
    <c:if test="${not empty success}"><div class="msg ok">${success}</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <label for="username">Tài khoản</label>
        <input id="username" name="username" type="text" required>
        <label for="email">Email</label>
        <input id="email" name="email" type="email">
        <label for="password">Mật khẩu</label>
        <input id="password" name="password" type="password" required>
        <button type="submit">Đăng ký</button>
    </form>
    <p style="margin-top:12px;">
        <a href="${pageContext.request.contextPath}/login">Đã có tài khoản? Đăng nhập</a>
    </p>
</div>
</body>
</html>
