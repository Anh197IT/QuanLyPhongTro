<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Tài khoản</title>
    <style>
        label { display:block; margin: 8px 0 4px; }
        input, select { width: 100%; padding: 8px; }
        .row { max-width: 520px; margin: 20px auto; }
        a.button, button { display:inline-block; padding:8px 12px; background:#1976d2; color:#fff; text-decoration:none; border-radius:4px; border:none; cursor:pointer; }
        a.button { margin-right: 8px; }
    </style>
</head>
<body>
<div class="row">
    <h2>Tài khoản</h2>
    <form method="post" action="${pageContext.request.contextPath}<c:choose><c:when test='${empty item.id}'>/users</c:when><c:otherwise>/users/${item.id}/update</c:otherwise></c:choose>">
        <c:if test='${empty item.id}'>
            <label>Username</label>
            <input name="username" value="${item.username}" required />
        </c:if>
        <label>Họ tên</label>
        <input name="fullName" value="${item.fullName}" />
        <label>Email</label>
        <input name="email" value="${item.email}" />
        <label>Role</label>
        <select name="role">
            <c:forEach items="${roles}" var="r">
                <option value="${r}" <c:if test='${item.role == r}'>selected</c:if>>${r}</option>
            </c:forEach>
        </select>
        <label>Kích hoạt</label>
        <select name="enabled">
            <option value="true" <c:if test='${item.enabled}'>selected</c:if>>Đang hoạt động</option>
            <option value="false" <c:if test='${!item.enabled}'>selected</c:if>>Bị khóa</option>
        </select>
        <label>Mật khẩu <small>(để trống để giữ nguyên)</small></label>
        <input name="passwordHash" type="password" />
        <p>
            <a class="button" href="${pageContext.request.contextPath}/users">Hủy</a>
            <button type="submit">Lưu</button>
            <c:if test='${not empty item.id && sessionScope.authUser != null && sessionScope.authUser.id == item.id}'>
                <a class="button" href="${pageContext.request.contextPath}/account/change-password" style="background:#455a64;">Thay đổi mật khẩu</a>
            </c:if>
        </p>
    </form>
</div>
</body>
</html>
