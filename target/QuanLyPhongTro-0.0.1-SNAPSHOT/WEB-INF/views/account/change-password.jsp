<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi mật khẩu</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        .container { max-width: 480px; margin: auto; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 20px; }
        .form-row { margin-bottom: 12px; }
        label { display: block; font-weight: bold; margin-bottom: 6px; }
        input[type=password] { width: 100%; padding: 10px; box-sizing: border-box; }
        .btn { padding: 10px 16px; background: #1976d2; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
        .btn:hover { background: #145ca6; }
        .alert { padding: 10px 12px; border-radius: 4px; margin-bottom: 12px; }
        .alert.error { background: #fdecea; color: #b71c1c; border: 1px solid #f5c6cb; }
        .alert.success { background: #e8f5e9; color: #1b5e20; border: 1px solid #c8e6c9; }
        a { color: #1976d2; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <h2>Đổi mật khẩu</h2>
    <div class="card">
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert success">${success}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/account/change-password">
            <div class="form-row">
                <label for="oldPassword">Mật khẩu hiện tại</label>
                <input id="oldPassword" name="oldPassword" type="password" required>
            </div>
            <div class="form-row">
                <label for="newPassword">Mật khẩu mới</label>
                <input id="newPassword" name="newPassword" type="password" minlength="6" required>
            </div>
            <div class="form-row">
                <label for="confirmPassword">Nhập lại mật khẩu mới</label>
                <input id="confirmPassword" name="confirmPassword" type="password" minlength="6" required>
            </div>
            <button class="btn" type="submit">Cập nhật</button>
        </form>
        <div style="margin-top:12px">
            <a href="${pageContext.request.contextPath}/">Về trang chủ</a>
        </div>
    </div>
</div>
</body>
</html>
