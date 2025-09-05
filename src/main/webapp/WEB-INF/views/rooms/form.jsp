<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Thêm phòng</title>
    <meta charset="UTF-8" />
    <style>
        label { display:block; margin-top:10px; }
        input, select, textarea { width: 300px; padding:6px; }
        .error { color:#d32f2f; }
        .actions { margin-top: 12px; }
        button, a.button { padding: 6px 12px; background:#1976d2; color:#fff; border:none; border-radius:4px; text-decoration:none; }
        a.button { background:#616161; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content">
            <c:set var="formAction" value="${pageContext.request.contextPath}/rooms"/>
            <c:set var="title" value="Thêm phòng"/>
            <c:if test="${room.id != null}">
                <c:set var="formAction" value="${pageContext.request.contextPath}/rooms/${room.id}/update"/>
                <c:set var="title" value="Sửa phòng"/>
            </c:if>
            <h1>${title}</h1>
            <form method="post" action="${formAction}">
                <label>Số phòng *</label>
                <input type="text" name="soPhong" value="${room.soPhong}" required />

                <label>Giá phòng (VND) *</label>
                <input type="number" name="giaPhong" step="0.01" min="0.01" value="${room.giaPhong}" required />

                <label>Diện tích (m²) *</label>
                <input type="number" name="dienTich" step="0.01" min="0.01" value="${room.dienTich}" required />

                <label>Trạng thái</label>
                <select name="trangThai">
                    <c:forEach items="${statuses}" var="s">
                        <option value="${s}" ${room.trangThai == s ? 'selected' : ''}>${s}</option>
                    </c:forEach>
                </select>

                <label>Ghi chú</label>
                <textarea name="ghiChu" rows="3">${room.ghiChu}</textarea>

                <div class="actions">
                    <button type="submit">Lưu</button>
                    <a class="button" href="${pageContext.request.contextPath}/rooms">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>

