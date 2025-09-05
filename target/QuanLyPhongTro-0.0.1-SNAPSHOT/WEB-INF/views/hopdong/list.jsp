<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Danh sách hợp đồng</title>
    <meta charset="UTF-8" />
    <style>
        .toolbar { display:flex; align-items:center; gap:8px; flex-wrap: wrap; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/_shared/header.jspf" %>
    <div class="layout">
        <%@ include file="/WEB-INF/views/_shared/sidebar.jspf" %>
        <div class="content container-fluid py-3">
            <div class="card shadow-sm">
            <h1 class="h4 mb-3">Danh sách hợp đồng</h1>
            <c:if test="${overdueCount gt 0}">
                <div class="alert alert-danger d-flex justify-content-between align-items-center" id="banner-overdue">
                    <span>⚠️ Có <strong>${overdueCount}</strong> hợp đồng <strong>đã quá hạn</strong>. <a class="text-white text-decoration-underline" href="${pageContext.request.contextPath}/hopdong?filter=overdue">Xem ngay</a></span>
                    <button class="btn-close btn-close-white" onclick="document.getElementById('banner-overdue').style.display='none'" aria-label="Đóng"></button>
                </div>
            </c:if>
            <c:if test="${expiringCount gt 0}">
                <div class="alert alert-warning d-flex justify-content-between align-items-center" id="banner-expiring">
                    <span>⏰ Có <strong>${expiringCount}</strong> hợp đồng <strong>sắp hết hạn</strong> trong ${warnDays} ngày. <a class="text-dark fw-semibold" href="${pageContext.request.contextPath}/hopdong?filter=expiring">Xem danh sách</a></span>
                    <button class="btn-close" onclick="document.getElementById('banner-expiring').style.display='none'" aria-label="Đóng"></button>
                </div>
            </c:if>
            <p class="toolbar mb-2">
                <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/hopdong/new" title="Tạo hợp đồng" data-bs-toggle="tooltip">➕</a>
                </c:if>
                <span class="ms-auto"></span>
                <a class="btn btn-outline-primary btn-sm ${empty filter ? 'active' : ''}" href="${pageContext.request.contextPath}/hopdong">Tất cả</a>
                <a class="btn btn-outline-warning btn-sm ${filter == 'expiring' ? 'active' : ''}" href="${pageContext.request.contextPath}/hopdong?filter=expiring">⏰ Sắp hết hạn (<c:out value="${expiringCount}" default="0"/>)</a>
                <a class="btn btn-outline-danger btn-sm ${filter == 'overdue' ? 'active' : ''}" href="${pageContext.request.contextPath}/hopdong?filter=overdue">⚠️ Đã quá hạn (<c:out value="${overdueCount}" default="0"/>)</a>
            </p>
            <table class="table table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th>Phòng</th>
                    <th>Khách thuê</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Giá phòng</th>
                    <th>Tiền cọc</th>
                    <th>Trạng thái</th>
                    <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
                        <th>Hành động</th>
                    </c:if>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${items}" var="it">
                    <c:set var="daysLeft" value="${daysLeftMap[it.id]}"/>
                    <c:set var="rowClass" value=""/>
                    <c:if test="${it.trangThai == 'DANG_THUE' && daysLeft ne null}">
                        <c:choose>
                            <c:when test="${daysLeft lt 0}"><c:set var="rowClass" value="row-overdue"/></c:when>
                            <c:when test="${daysLeft le warnDays}"><c:set var="rowClass" value="row-expiring"/></c:when>
                        </c:choose>
                    </c:if>
                    <tr class="${rowClass}">
                        <td><c:out value="${it.phong.soPhong}"/></td>
                        <td><c:out value="${it.khach.hoTen}"/></td>
                        <td>${it.ngayBatDau}</td>
                        <td>
                            ${it.ngayKetThuc}
                            <c:if test="${it.trangThai == 'DANG_THUE' && daysLeft ne null}">
                                <c:choose>
                                    <c:when test="${daysLeft lt 0}">
                                        <span class="badge text-bg-danger ms-1">ĐÃ HẾT HẠN ${-daysLeft} ngày</span>
                                    </c:when>
                                    <c:when test="${daysLeft le warnDays}">
                                        <span class="badge text-bg-warning ms-1">Còn ${daysLeft} ngày</span>
                                    </c:when>
                                </c:choose>
                            </c:if>
                        </td>
                        <td>
                            <c:set var="v1" value="${it.giaPhong}"/>
                            <c:set var="m1" value="${(v1 - (v1 mod 1000000)) / 1000000}"/>
                            <c:set var="y1" value="${((v1 mod 1000000) - ((v1 mod 1000000) mod 100000)) / 100000}"/>
                            <span title="${v1} đ">${m1}tr<c:if test="${y1 gt 0}">${y1}</c:if></span>
                        </td>
                        <td>
                            <c:set var="v2" value="${it.tienCoc}"/>
                            <c:set var="m2" value="${(v2 - (v2 mod 1000000)) / 1000000}"/>
                            <c:set var="y2" value="${((v2 mod 1000000) - ((v2 mod 1000000) mod 100000)) / 100000}"/>
                            <span title="${v2} đ">${m2}tr<c:if test="${y2 gt 0}">${y2}</c:if></span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${it.trangThai == 'DANG_THUE'}">
                                    <span class="badge text-bg-success">Đang thuê</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-bg-secondary">Đã kết thúc</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test='${sessionScope.authUser != null && sessionScope.authUser.role == "ADMIN" && empty mine}'>
                            <td>
                                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/hopdong/${it.id}/edit" title="Sửa" data-bs-toggle="tooltip">✏️</a>
                                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/hoadon/new?hopDongId=${it.id}" title="Tạo hóa đơn" data-bs-toggle="tooltip">🧾</a>
                                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/hopdong/${it.id}/end" title="Kết thúc" data-bs-toggle="tooltip" onclick="return confirm('Kết thúc hợp đồng này?');">⛔</a>
                                <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/hopdong/${it.id}/delete" title="Xóa" data-bs-toggle="tooltip" onclick="return confirm('Xóa hợp đồng này?');">🗑️</a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <!-- Pagination -->
            <c:if test="${totalPages gt 1}">
                <c:set var="basePath" value="${empty mine ? '/hopdong' : '/hopdong/mine'}"/>
                <div class="toolbar" style="margin-top:12px; justify-content: space-between;">
                    <div>
                        <c:set var="fromIdx" value="${totalItems == 0 ? 0 : currentPage * size + 1}"/>
                        <c:set var="toIdx" value="${(currentPage + 1) * size gt totalItems ? totalItems : (currentPage + 1) * size}"/>
                        Hiển thị ${fromIdx}-${toIdx} trên tổng ${totalItems}
                    </div>
                    <div style="display:flex; gap:10px; align-items:center; flex-wrap: wrap;">
                        <!-- Page size selector -->
                        <form method="get" action="${pageContext.request.contextPath}${basePath}" style="display:flex; gap:6px; align-items:center;">
                            <label for="sizeSelect">Mỗi trang:</label>
                            <select id="sizeSelect" name="size" class=""
                                    onchange="this.form.page.value=0; this.form.submit();">
                                <option value="5" ${size == 5 ? 'selected' : ''}>5</option>
                                <option value="10" ${size == 10 ? 'selected' : ''}>10</option>
                                <option value="20" ${size == 20 ? 'selected' : ''}>20</option>
                                <option value="50" ${size == 50 ? 'selected' : ''}>50</option>
                            </select>
                            <input type="hidden" name="page" value="${currentPage}" />
                            <c:if test="${not empty filter}"><input type="hidden" name="filter" value="${filter}" /></c:if>
                        </form>
                        <!-- Goto page (1-based) -->
                        <form method="get" action="${pageContext.request.contextPath}${basePath}" style="display:flex; gap:6px; align-items:center;" data-max="${totalPages}" onsubmit="return submitGoto(this);">
                            <label for="gotoPage">Đến trang:</label>
                            <input id="gotoPage" type="number" min="1" max="${totalPages}" value="${currentPage + 1}" name="page1" style="width:72px;"/>
                            <input type="hidden" name="page" value="${currentPage}" />
                            <input type="hidden" name="size" value="${size}" />
                            <c:if test="${not empty filter}"><input type="hidden" name="filter" value="${filter}" /></c:if>
                            <button type="submit" class="button ghost">Đi</button>
                        </form>
                        <c:set var="prevPage" value="${currentPage - 1}"/>
                        <c:set var="nextPage" value="${currentPage + 1}"/>
                        <c:url var="prevUrl" value="${basePath}">
                            <c:param name="page" value="${prevPage}"/>
                            <c:param name="size" value="${size}"/>
                            <c:if test="${not empty filter}"><c:param name="filter" value="${filter}"/></c:if>
                        </c:url>
                        <c:url var="nextUrl" value="${basePath}">
                            <c:param name="page" value="${nextPage}"/>
                            <c:param name="size" value="${size}"/>
                            <c:if test="${not empty filter}"><c:param name="filter" value="${filter}"/></c:if>
                        </c:url>
                        <a class="button ghost ${currentPage == 0 ? 'disabled' : ''}"
                           href="${pageContext.request.contextPath}${prevUrl}">← Trước</a>
                        <c:forEach var="p" begin="0" end="${totalPages - 1}">
                            <c:url var="pUrl" value="${basePath}">
                                <c:param name="page" value="${p}"/>
                                <c:param name="size" value="${size}"/>
                                <c:if test="${not empty filter}"><c:param name="filter" value="${filter}"/></c:if>
                            </c:url>
                            <a class="button ghost ${p == currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pUrl}">${p + 1}</a>
                        </c:forEach>
                        <a class="button ghost ${currentPage + 1 >= totalPages ? 'disabled' : ''}"
                           href="${pageContext.request.contextPath}${nextUrl}">Sau →</a>
                    </div>
                </div>
            </c:if>
            </div>
        </div>
<script>
    function submitGoto(form) {
        try {
            var max = parseInt(form.getAttribute('data-max') || '1', 10);
            var p1 = parseInt(form.page1.value || '1', 10);
            if (isNaN(p1) || p1 < 1) p1 = 1;
            if (p1 > max) p1 = max;
            var p0 = p1 - 1;
            form.page.value = String(p0);
        } catch (e) {
            // fallback
            form.page.value = '0';
        }
        return true;
    }
</script>
</body>
</html>
