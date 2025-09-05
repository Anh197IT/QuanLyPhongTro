<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title><c:choose><c:when test="${item.id != null}">Sửa hợp đồng</c:when><c:otherwise>Tạo hợp đồng</c:otherwise></c:choose></title>
    <meta charset="UTF-8" />
    <style>
        label { display:block; margin-top:10px; }
        input, select { width: 320px; padding:6px; }
        .actions { margin-top: 12px; }
        button, a.button { padding: 6px 12px; background:#1976d2; color:#fff; border:none; border-radius:4px; text-decoration:none; }
        a.button { background:#616161; }
    </style>
    <script>
        function recalcEndDate() {
            var startEl = document.querySelector('input[name="ngayBatDau"]');
            var endEl = document.querySelector('input[name="ngayKetThuc"]');
            var durEl = document.querySelector('select[name="durationMonths"]');
            if (!startEl || !endEl || !durEl) return;
            var start = startEl.value; // yyyy-MM-dd
            var months = parseInt(durEl.value || '0');
            if (!start || !months || months <= 0) return; // Custom/no preset -> don't override
            var d = new Date(start + 'T00:00:00');
            d.setMonth(d.getMonth() + months);
            var yyyy = d.getFullYear();
            var mm = String(d.getMonth() + 1).padStart(2, '0');
            var dd = String(d.getDate()).padStart(2, '0');
            endEl.value = yyyy + '-' + mm + '-' + dd;
        }
        document.addEventListener('DOMContentLoaded', function() {
            var startEl = document.querySelector('input[name="ngayBatDau"]');
            var durEl = document.querySelector('select[name="durationMonths"]');
            if (startEl) startEl.addEventListener('change', recalcEndDate);
            if (durEl) durEl.addEventListener('change', recalcEndDate);
        });
    </script>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content">
            <h1><c:choose><c:when test="${item.id != null}">Sửa hợp đồng</c:when><c:otherwise>Tạo hợp đồng</c:otherwise></c:choose></h1>
            <form method="post" action="${pageContext.request.contextPath}/hopdong<c:if test='${item.id != null}'>/${item.id}/update</c:if>">
                <label>Phòng</label>
                <select name="roomId" required>
                    <c:forEach items="${rooms}" var="r">
                        <option value="${r.id}" <c:if test='${item.phong != null && item.phong.id == r.id}'>selected</c:if>>
                            ${r.soPhong} - ${r.trangThai}
                        </option>
                    </c:forEach>
                </select>

                <label>Khách thuê</label>
                <select name="khachId" required>
                    <c:forEach items="${khachs}" var="k">
                        <option value="${k.id}" <c:if test='${item.khach != null && item.khach.id == k.id}'>selected</c:if>>
                            ${k.hoTen} - ${k.cccd}
                        </option>
                    </c:forEach>
                </select>

                <label>Tài khoản người dùng (nếu khách chưa có)</label>
                <select name="userId">
                    <option value="">-- Không gán tài khoản --</option>
                    <c:forEach items="${users}" var="u">
                        <option value="${u.id}">${u.username}</option>
                    </c:forEach>
                </select>

                <label>Ngày bắt đầu</label>
                <input type="date" name="ngayBatDau" value="${item.ngayBatDau}" required />

                <label>Kỳ hạn</label>
                <select name="durationMonths">
                    <option value="">Tùy chọn (nhập ngày kết thúc)</option>
                    <option value="3">3 tháng</option>
                    <option value="6">6 tháng</option>
                    <option value="12">12 tháng</option>
                </select>

                <label>Ngày kết thúc</label>
                <input type="date" name="ngayKetThuc" value="${item.ngayKetThuc}" />

                <label>Giá phòng</label>
                <input type="number" step="0.01" name="giaPhong" value="${item.giaPhong}" required />

                <label>Tiền cọc</label>
                <input type="number" step="0.01" name="tienCoc" value="${item.tienCoc}" />

                <label>Trạng thái</label>
                <select name="trangThai">
                    <option value="DANG_THUE" <c:if test='${item.trangThai == "DANG_THUE"}'>selected</c:if>>DANG_THUE</option>
                    <option value="DA_KET_THUC" <c:if test='${item.trangThai == "DA_KET_THUC"}'>selected</c:if>>DA_KET_THUC</option>
                </select>

                <div class="actions">
                    <button type="submit">Lưu</button>
                    <a class="button" href="${pageContext.request.contextPath}/hopdong">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>

