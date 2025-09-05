<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Danh sách hóa đơn</title>
    <style>
        .qr-modal { display:none; position:fixed; inset:0; background:rgba(0,0,0,.5); align-items:center; justify-content:center; }
        .qr-modal .box { background:#fff; padding:16px; border-radius:6px; width:420px; max-width:90%; text-align:center; }
        .qr-modal img { width:320px; height:320px; border:1px solid #ddd; }
        .qr-modal .row { margin:6px 0; }
        .qr-modal .actions { margin-top:10px; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content container-fluid py-3">
            <div class="card shadow-sm p-3">
                <div class="d-flex align-items-center flex-wrap gap-2 mb-2">
                    <h1 class="h4 mb-0">Danh sách hóa đơn</h1>
                    <span class="ms-auto"></span>
                    <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
                        <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/hoadon/new" title="Tạo hóa đơn" data-bs-toggle="tooltip">➕</a>
                        <form class="d-inline" method="post" action="${pageContext.request.contextPath}/hoadon/generate">
                            <input class="form-control form-control-sm d-inline-block" style="width: 140px;" type="text" name="thang" placeholder="YYYY-MM">
                            <button class="btn btn-dark btn-sm" type="submit" onclick="return confirm('Tạo hóa đơn cho tất cả hợp đồng đang thuê?');">Tạo hóa đơn hàng loạt</button>
                        </form>
                    </c:if>
                </div>
                <table class="table table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th>Hợp đồng</th>
                    <th>Phòng</th>
                    <th>Khách thuê</th>
                    <th>Tháng</th>
                    <th>Tiền phòng</th>
                    <th>Tiền điện</th>
                    <th>Tiền nước</th>
                    <th>Tổng</th>
                    <th>Trạng thái</th>
                    <th>Xác nhận lúc</th>
                    <th>Xác nhận bởi</th>
                    <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
            <th>Hành động</th>
                    </c:if>
                    <c:if test='${!empty mine || (sessionScope.authUser != null && sessionScope.authUser.role == "USER")}'>
            <th>Thanh toán</th>
                    </c:if>
                </tr>
                </thead>
                <tbody>
    <c:forEach items="${items}" var="it">
        <tr>
            <td>${it.hopDong.id}</td>
            <td>${it.hopDong.phong.soPhong}</td>
            <td>${it.hopDong.khach.hoTen}</td>
            <td>${it.thangNam}</td>
            <td>
                <c:set var="vpf" value="${it.tienPhong}"/>
                <c:set var="mpf" value="${(vpf - (vpf mod 1000000)) / 1000000}"/>
                <c:set var="ypf" value="${((vpf mod 1000000) - ((vpf mod 1000000) mod 100000)) / 100000}"/>
                <span title="${vpf} đ">${mpf}tr<c:if test='${ypf gt 0}'>${ypf}</c:if></span>
            </td>
            <td>
                <c:set var="vde" value="${it.tienDien}"/>
                <c:set var="mde" value="${(vde - (vde mod 1000000)) / 1000000}"/>
                <c:set var="yde" value="${((vde mod 1000000) - ((vde mod 1000000) mod 100000)) / 100000}"/>
                <span title="${vde} đ">${mde}tr<c:if test='${yde gt 0}'>${yde}</c:if></span>
            </td>
            <td>
                <c:set var="vnu" value="${it.tienNuoc}"/>
                <c:set var="mnu" value="${(vnu - (vnu mod 1000000)) / 1000000}"/>
                <c:set var="ynu" value="${((vnu mod 1000000) - ((vnu mod 1000000) mod 100000)) / 100000}"/>
                <span title="${vnu} đ">${mnu}tr<c:if test='${ynu gt 0}'>${ynu}</c:if></span>
            </td>
            <td>
                <c:set var="vtt" value="${it.tongTien}"/>
                <c:set var="mtt" value="${(vtt - (vtt mod 1000000)) / 1000000}"/>
                <c:set var="ytt" value="${((vtt mod 1000000) - ((vtt mod 1000000) mod 100000)) / 100000}"/>
                <span title="${vtt} đ">${mtt}tr<c:if test='${ytt gt 0}'>${ytt}</c:if></span>
            </td>
            <td>
                <c:choose>
                    <c:when test='${it.trangThai == "CHUA_THANH_TOAN"}'>
                        <span class="badge text-bg-warning">Chưa thanh toán</span>
                    </c:when>
                    <c:when test='${it.trangThai == "DA_THANH_TOAN"}'>
                        <span class="badge text-bg-success">Đã thanh toán</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge text-bg-secondary">${it.trangThai}</span>
                    </c:otherwise>
                </c:choose>
            </td>
            <td><c:out value='${it.xacNhanLuc}' default='-' /></td>
            <td><c:out value='${it.xacNhanBoi}' default='-' /></td>
            <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
                <td>
                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/hoadon/${it.id}/edit" title="Sửa" data-bs-toggle="tooltip">✏️</a>
                    <c:if test="${it.trangThai == 'CHUA_THANH_TOAN'}">
                        <form class="d-inline" method="post" action='${pageContext.request.contextPath}/hoadon/${it.id}/admin-confirm' onsubmit="return confirm('Xác nhận đã nhận tiền?');">
                            <button type="submit" class="btn btn-success btn-sm">Xác nhận đã nhận tiền</button>
                        </form>
                    </c:if>
                    <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/hoadon/${it.id}/delete" title="Xóa" data-bs-toggle="tooltip" onclick="return confirm('Xóa hóa đơn này?');">🗑️</a>
                </td>
            </c:if>
            <c:if test='${!empty mine || (sessionScope.authUser != null && sessionScope.authUser.role == "USER" )}'>
                <td>
                    <c:if test='${it.trangThai == "CHUA_THANH_TOAN"}'>
                        <a class="btn btn-success btn-sm" href="#" data-id="${it.id}" onclick="return openQr(Number(this.dataset.id));">Thanh toán QR</a>
                    </c:if>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
                </table>
            </div>
        </div>
    </div>

<!-- Modal VietQR -->
<div id="qrModal" class="qr-modal" onclick="if(event.target===this) closeQr();">
    <div class="box">
        <h3>Thanh toán hóa đơn <span id="qrBillId"></span></h3>
        <div class="row" id="qrInfo">Đang tải...</div>
        <div class="row"><img id="qrImg" alt="QR"/></div>
        <div class="actions">
            <form id="qrConfirmForm" method="post" onsubmit="return confirm('Xác nhận đã chuyển khoản?');">
                <button class="btn btn-success" type="submit">Tôi đã chuyển khoản</button>
                <button class="btn btn-secondary" type="button" onclick="closeQr()">Đóng</button>
            </form>
        </div>
    </div>
    </div>

<script>
    function openQr(id){
        const modal = document.getElementById('qrModal');
        document.getElementById('qrBillId').textContent = '#' + id;
        document.getElementById('qrInfo').textContent = 'Đang tải...';
        document.getElementById('qrImg').src = '';
        document.getElementById('qrConfirmForm').action = '${pageContext.request.contextPath}/hoadon/' + id + '/confirm';
        modal.style.display = 'flex';
        fetch('${pageContext.request.contextPath}/hoadon/' + id + '/vietqr-info')
            .then(r => r.json())
            .then(d => {
                if(!d.ok){
                    document.getElementById('qrInfo').textContent = d.message || 'Lỗi tải QR';
                    return;
                }
                document.getElementById('qrInfo').innerHTML =
                    'Ngân hàng: <b>' + d.bankCode + '</b> — STK: <b>' + d.accountNo + '</b><br/>' +
                    'Chủ TK: <b>' + d.accountName + '</b><br/>' +
                    'Số tiền: <b>' + d.amount + '</b> | Kỳ: <b>' + d.thangNam + '</b>';
                document.getElementById('qrImg').src = d.qrUrl;
            }).catch(() => {
                document.getElementById('qrInfo').textContent = 'Không thể tải QR';
            });
        return false;
    }
    function closeQr(){
        document.getElementById('qrModal').style.display = 'none';
    }
</script>
</body>
</html>

