<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
    <title>Quản lý tài khoản</title>
    <meta charset="UTF-8" />
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content container-fluid py-3">
            <div class="card shadow-sm p-3">
            <div class="d-flex align-items-center gap-2 flex-wrap mb-2">
                <h1 class="h4 mb-0">Quản lý tài khoản</h1>
                <span class="ms-auto"></span>
                <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/users/new" title="Tạo tài khoản" data-bs-toggle="tooltip">➕</a>
                <c:if test='${sessionScope.authUser != null}'>
                    <a class="btn btn-dark btn-sm" href="${pageContext.request.contextPath}/account/change-password">Thay đổi mật khẩu</a>
                </c:if>
            </div>
            <form class="row g-2 align-items-end mb-3" method="get" action="${pageContext.request.contextPath}/users">
                <div class="col-auto">
                    <label class="form-label mb-1">Trạng thái</label>
                    <select name="status" class="form-select form-select-sm">
                        <option value="all" <c:if test='${status == "all"}'>selected</c:if>>Tất cả</option>
                        <option value="pending" <c:if test='${status == "pending"}'>selected</c:if>>Chờ duyệt</option>
                        <option value="active" <c:if test='${status == "active"}'>selected</c:if>>Đang hoạt động</option>
                    </select>
                </div>
                <div class="col-auto">
                    <label class="form-label mb-1">Tìm kiếm</label>
                    <input class="form-control form-control-sm" type="text" name="q" value="${q}" placeholder="username/email" />
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-outline-primary btn-sm">Lọc</button>
                    <c:if test='${not empty status && status != "all"}'>
                        <a class="btn btn-link btn-sm" href='${pageContext.request.contextPath}/users'>Xóa bộ lọc</a>
                    </c:if>
                </div>
                <div class="col-auto ms-auto">
                    <span class="text-muted">Số lượng: <strong><c:out value='${fn:length(items)}'/></strong></span>
                </div>
            </form>
            <table class="table table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Họ tên</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Kích hoạt</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${items}" var="it">
                    <tr>
                        <td>${it.username}</td>
                        <td>${it.fullName}</td>
                        <td>${it.email}</td>
                        <td>
                            <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN"}'>
                                <form class="d-inline" method="post" action="${pageContext.request.contextPath}/users/${it.id}/role">
                                    <select name="role" class="form-select form-select-sm d-inline-block" style="width: auto;">
                                        <c:forEach items='${roles}' var='r'>
                                            <option value='${r}' <c:if test='${it.role == r}'>selected</c:if>>${r}</option>
                                        </c:forEach>
                                    </select>
                                    <button class="btn btn-outline-secondary btn-sm" type="submit">Đổi</button>
                                </form>
                            </c:if>
                            <c:if test='${!(sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN") }'>${it.role}</c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test='${it.enabled}'>
                                    <span class="badge text-bg-success">Đang hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-bg-secondary">Chờ duyệt/Bị khóa</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN"}'>
                                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/users/${it.id}/edit" title="Sửa" data-bs-toggle="tooltip">✏️</a>
                                <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/users/${it.id}/delete" title="Xóa" data-bs-toggle="tooltip" onclick="return confirm('Xóa tài khoản này?');">🗑️</a>
                                <c:choose>
                                    <c:when test='${!it.enabled}'>
                                        <form class="d-inline" method="post" action="${pageContext.request.contextPath}/users/${it.id}/enable">
                                            <button class="btn btn-success btn-sm" type="submit">Duyệt/Mở khóa</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form class="d-inline" method="post" action="${pageContext.request.contextPath}/users/${it.id}/disable">
                                            <button class="btn btn-warning btn-sm" type="submit">Vô hiệu hóa</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </div>
    </div>
</body>
</html>
