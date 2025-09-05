<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Thanh toán hóa đơn</title>
    <style>
        .wrap { max-width: 640px; margin: 20px auto; text-align: center; }
        img.qr { width: 360px; height: 360px; border:1px solid #ddd; }
        .info { margin: 12px 0; }
        a.button, button { display:inline-block; padding:8px 12px; border-radius:4px; text-decoration:none; border:none; }
        a.button.back { background:#616161; color:#fff; }
        button.confirm { background:#2e7d32; color:#fff; }
    </style>
</head>
<body>
<div class="wrap">
    <h1>Thanh toán hóa đơn #${item.id}</h1>
    <p class="info">Ngân hàng: <b>${bankCode}</b> — STK: <b>${accountNo}</b><br/>Chủ TK: <b>${accountName}</b></p>
    <p class="info">Số tiền: <b>${item.tongTien}</b> | Kỳ: <b>${item.thangNam}</b></p>
    <p><img class="qr" src="${qrUrl}" alt="VietQR"/></p>
    <form method="post" action="${pageContext.request.contextPath}/hoadon/${item.id}/confirm" onsubmit="return confirm('Xác nhận đã chuyển khoản?');">
        <button class="confirm" type="submit">Tôi đã chuyển khoản</button>
        <a class="button back" href="${pageContext.request.contextPath}/hoadon/mine">Quay lại danh sách</a>
    </form>
</div>
</body>
</html>
