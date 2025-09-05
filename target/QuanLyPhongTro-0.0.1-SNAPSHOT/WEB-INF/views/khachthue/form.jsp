<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title><c:choose><c:when test="${item.id != null}">Sửa khách thuê</c:when><c:otherwise>Thêm khách thuê</c:otherwise></c:choose></title>
    <meta charset="UTF-8" />
    <style>
        label { display:block; margin-top:10px; }
        input, textarea { width: 320px; padding:6px; }
        .actions { margin-top: 12px; }
        button, a.button { padding: 6px 12px; background:#1976d2; color:#fff; border:none; border-radius:4px; text-decoration:none; }
        a.button { background:#616161; }
        .error { color:#d32f2f; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content">
            <h1><c:choose><c:when test="${item.id != null}">Sửa khách thuê</c:when><c:otherwise>Thêm khách thuê</c:otherwise></c:choose></h1>
            <form method="post" action="${pageContext.request.contextPath}/khachthue<c:if test='${item.id != null}'>/${item.id}/update</c:if>">
                <label>Họ tên *</label>
                <input type="text" name="hoTen" value="${item.hoTen}" required />

                <label>Số điện thoại *</label>
                <input type="text" name="soDienThoai" value="${item.soDienThoai}" required />

                <label>CMND/CCCD *</label>
                <input type="text" name="cccd" value="${item.cccd}" required />

                <label>Địa chỉ</label>
                <textarea name="diaChi" rows="3">${item.diaChi}</textarea>

                <label>Ngày sinh</label>
                <input type="date" name="ngaySinh" value="${item.ngaySinh}" />

                <label>Phòng thuê</label>
                <select name="roomId">
                    <option value="">-- Không gán phòng --</option>
                    <c:forEach items="${rooms}" var="r">
                        <option value="${r.id}" <c:if test='${item.room != null && item.room.id == r.id}'>selected</c:if>>
                            ${r.soPhong} - ${r.trangThai}
                        </option>
                    </c:forEach>
                </select>

                <label>Tài khoản người dùng (để xem hợp đồng/hóa đơn của mình)</label>
                <select name="userId">
                    <option value="">-- Không gán tài khoản --</option>
                    <c:forEach items="${users}" var="u">
                        <option value="${u.id}" <c:if test='${item.user != null && item.user.id == u.id}'>selected</c:if>>
                            ${u.username}
                        </option>
                    </c:forEach>
                </select>

                <div class="actions">
                    <button type="submit">Lưu</button>
                    <a class="button" href="${pageContext.request.contextPath}/khachthue">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>

