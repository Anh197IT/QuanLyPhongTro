<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Danh sách phòng</title>
    <meta charset="UTF-8" />
    <style>
        .muted { color:#6c757d; font-size:13px; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content container-fluid py-3">
            <h1 class="h4 mb-3">Danh sách phòng</h1>
            <div class="d-flex gap-2 align-items-center flex-wrap mb-3">
                <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/rooms/new" title="Thêm phòng" data-bs-toggle="tooltip">➕</a>
                <span class="ms-auto"></span>
                <div class="d-flex gap-2 align-items-center flex-wrap">
                    <select id="statusFilter" class="form-select form-select-sm" style="width: 160px;">
                        <option value="">- Trạng thái -</option>
                        <option value="TRONG">Trống</option>
                        <option value="DANG_THUE">Đang thuê</option>
                    </select>
                    <input id="searchRoom" class="form-control form-control-sm" type="text" placeholder="Số phòng" style="width: 180px;"/>
                    <button class="btn btn-outline-primary btn-sm" type="button" onclick="applyFilters()">Tìm kiếm</button>
                </div>
            </div>
            <div id="stats" class="muted mb-2"></div>
            <div class="row g-3" id="roomGrid">
                <c:forEach items="${rooms}" var="r">
                    <c:set var="statusLower" value="${r.trangThai == 'TRONG' ? 'trong' : 'dang-thue'}"/>
                    <div class="col-12 col-sm-6 col-md-4 col-lg-3" data-status="${r.trangThai}" data-room="${r.soPhong}">
                        <div class="card h-100">
                            <div class="card-header d-flex justify-content-between align-items-center ${statusLower == 'trong' ? 'bg-success-subtle' : 'bg-primary-subtle'}">
                                <span>🏠 Phòng ${r.soPhong}</span>
                                <span class="badge ${statusLower == 'trong' ? 'text-bg-success' : 'text-bg-primary'}">${r.trangThai == 'TRONG' ? 'Trống' : 'Đang thuê'}</span>
                            </div>
                            <div class="card-body">
                                <div class="fw-bold">
                                    <c:set var="v" value="${r.giaPhong}"/>
                                    <c:set var="m" value="${(v - (v mod 1000000)) / 1000000}"/>
                                    <c:set var="y" value="${((v mod 1000000) - ((v mod 1000000) mod 100000)) / 100000}"/>
                                    <span title="${v} đ">${m}tr<c:if test="${y gt 0}">${y}</c:if></span>
                                </div>
                                <div class="muted">Diện tích: ${r.dienTich}</div>
                                <c:if test="${not empty r.ghiChu}"><div class="muted">${r.ghiChu}</div></c:if>
                            </div>
                            <div class="card-footer d-flex gap-2 align-items-center flex-wrap">
                                <form class="d-inline" method="post" action="${pageContext.request.contextPath}/rooms/${r.id}/status">
                                    <c:choose>
                                        <c:when test="${r.trangThai == 'DANG_THUE'}">
                                            <input type="hidden" name="value" value="TRONG"/>
                                            <button class="btn btn-outline-success btn-sm" type="submit" title="Trả phòng" data-bs-toggle="tooltip">🔄</button>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="hidden" name="value" value="DANG_THUE"/>
                                            <button class="btn btn-outline-warning btn-sm" type="submit" title="Cho thuê" data-bs-toggle="tooltip">🔄</button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/rooms/${r.id}/edit" title="Sửa" data-bs-toggle="tooltip">✏️</a>
                                <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/rooms/${r.id}/delete" title="Xóa" data-bs-toggle="tooltip" onclick="return confirm('Xóa phòng này?');">🗑️</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
<script>
    function applyFilters() {
        var status = document.getElementById('statusFilter').value.trim();
        var q = (document.getElementById('searchRoom').value || '').toLowerCase();
        var grid = document.getElementById('roomGrid');
        var cards = grid.querySelectorAll('.room-card');
        var total = 0, trong = 0, thue = 0, visible = 0;
        cards.forEach(function(card){
            total++;
            var st = card.getAttribute('data-status');
            if (st === 'TRONG') trong++; else if (st === 'DANG_THUE') thue++;
            var room = (card.getAttribute('data-room')||'').toLowerCase();
            var ok = true;
            if (status && st !== status) ok = false;
            if (q && room.indexOf(q) === -1) ok = false;
            card.style.display = ok ? '' : 'none';
            if (ok) visible++;
        });
        var stats = document.getElementById('stats');
        stats.textContent = 'Tổng ' + total + ' | Trống ' + trong + ' | Đang thuê ' + thue + ' | Hiển thị ' + visible;
    }
    document.addEventListener('DOMContentLoaded', applyFilters);
</script>
</body>
</html>

