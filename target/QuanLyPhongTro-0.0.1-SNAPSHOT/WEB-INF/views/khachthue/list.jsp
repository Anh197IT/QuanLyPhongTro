<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Danh sách khách thuê</title>
    <meta charset="UTF-8" />
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content container-fluid py-3">
            <div class="card shadow-sm p-3">
            <div class="d-flex align-items-center mb-2">
                <h1 class="h4 mb-0">Danh sách khách thuê</h1>
                <span class="ms-auto"></span>
                <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/khachthue/new" title="Thêm khách thuê" data-bs-toggle="tooltip">➕</a>
            </div>
            <table class="table table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th>Họ tên</th>
                    <th>SĐT</th>
                    <th>CCCD</th>
                    <th>Địa chỉ</th>
                    <th>Ngày sinh</th>
                    <th>Tài khoản</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${items}" var="it">
                    <tr>
                        <td>${it.hoTen}</td>
                        <td>${it.soDienThoai}</td>
                        <td>${it.cccd}</td>
                        <td>${it.diaChi}</td>
                        <td>${it.ngaySinh}</td>
                        <td><c:out value="${it.user.username}" default="-"/></td>
                        <td>
                            <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/khachthue/${it.id}/edit" title="Sửa" data-bs-toggle="tooltip">✏️</a>
                            <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/khachthue/${it.id}/delete" title="Xóa" data-bs-toggle="tooltip" onclick="return confirm('Xóa khách thuê này?');">🗑️</a>
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

