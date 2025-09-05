<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Trang Chủ</title>
    <meta charset="UTF-8" />
    <style>
        .card { border:1px solid #ddd; border-radius:6px; padding:12px; margin:12px 0; }
        .links a { display:inline-block; margin-right:8px; padding:6px 10px; background:#1976d2; color:#fff; text-decoration:none; border-radius:4px; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content">
            <h1>${message}</h1>
           
            <p>
                <a href="${pageContext.request.contextPath}/rooms">Quản lý phòng</a>
            </p>
            <c:if test="${sessionScope.authUser != null}">
                <div class="card">
                    <h3>Tài khoản của tôi</h3>
                    <div class="links">
                        <a href="${pageContext.request.contextPath}/account/change-password">Cập nhật mật khẩu</a>
                    </div>
                </div>
            </c:if>

            <c:if test="${sessionScope.authUser != null && sessionScope.authUser.role == 'ADMIN'}">
                <div class="card">
                    <h3>Quản lý tài khoản người dùng</h3>
                    <div class="links">
                        <a href="${pageContext.request.contextPath}/users?status=pending">Duyệt tài khoản chờ</a>
                        <a href="${pageContext.request.contextPath}/users?status=active">Tài khoản đang hoạt động</a>
                        <a href="${pageContext.request.contextPath}/users">Tất cả tài khoản</a>
                        <a href="${pageContext.request.contextPath}/users/new">+ Tạo tài khoản</a>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
