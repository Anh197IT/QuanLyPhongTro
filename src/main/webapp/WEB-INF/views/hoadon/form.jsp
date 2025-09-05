<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title><c:choose><c:when test="${item.id != null}">Sửa hóa đơn</c:when><c:otherwise>Tạo hóa đơn</c:otherwise></c:choose></title>
    <style>
        label { display:block; margin-top:10px; }
        input, select { width: 320px; padding:6px; }
        .actions { margin-top: 12px; }
        button, a.button { padding: 6px 12px; background:#1976d2; color:#fff; border:none; border-radius:4px; text-decoration:none; }
        a.button { background:#616161; }
    </style>
</head>
<body>
<h1><c:choose><c:when test="${item.id != null}">Sửa hóa đơn</c:when><c:otherwise>Tạo hóa đơn</c:otherwise></c:choose></h1>
<form method="post" action="${pageContext.request.contextPath}/hoadon<c:if test='${item.id != null}'>/${item.id}/update</c:if>">

    <label>Hợp đồng</label>
    <select name="hopDongId" required>
        <c:forEach items="${contracts}" var="h">
            <option value="${h.id}" <c:if test='${item.hopDong != null && item.hopDong.id == h.id}'>selected</c:if>>
                HD#${h.id} - Phòng ${h.phong.soPhong} - ${h.khach.hoTen}
            </option>
        </c:forEach>
    </select>

    <label>Tháng (YYYY-MM)</label>
    <input type="text" name="thangNam" value="${item.thangNam}" required />

    <label>Chỉ số điện cũ</label>
    <input type="number" name="chiSoDienCu" value="${item.chiSoDienCu}" />

    <label>Chỉ số điện mới</label>
    <input type="number" name="chiSoDienMoi" value="${item.chiSoDienMoi}" />

    <label>Đơn giá điện</label>
    <input type="number" step="0.01" name="donGiaDien" value="${item.donGiaDien}" />

    <label>Chỉ số nước cũ</label>
    <input type="number" name="chiSoNuocCu" value="${item.chiSoNuocCu}" />

    <label>Chỉ số nước mới</label>
    <input type="number" name="chiSoNuocMoi" value="${item.chiSoNuocMoi}" />

    <label>Đơn giá nước</label>
    <input type="number" step="0.01" name="donGiaNuoc" value="${item.donGiaNuoc}" />

    <label>Tiền phòng</label>
    <input type="number" step="0.01" name="tienPhong" value="${item.tienPhong}" required />

    <label>Phí dịch vụ</label>
    <input type="number" step="0.01" name="phiDichVu" value="${item.phiDichVu}" />

    <label>Trạng thái</label>
    <select name="trangThai">
        <option value="CHUA_THANH_TOAN" <c:if test='${item.trangThai == "CHUA_THANH_TOAN"}'>selected</c:if>>CHUA_THANH_TOAN</option>
        <option value="DA_THANH_TOAN" <c:if test='${item.trangThai == "DA_THANH_TOAN"}'>selected</c:if>>DA_THANH_TOAN</option>
    </select>

    <div class="actions">
        <button type="submit">Lưu</button>
        <a class="button" href="${pageContext.request.contextPath}/hoadon">Hủy</a>
    </div>
</form>
</body>
</html>
