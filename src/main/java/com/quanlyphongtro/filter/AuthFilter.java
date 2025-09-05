package com.quanlyphongtro.filter;

import com.quanlyphongtro.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        // Public paths
        if (path.startsWith("/login") || path.startsWith("/register") || path.startsWith("/logout") || path.equals("/") || path.startsWith("/resources/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("authUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // If user must change password, force redirect to change-password except for that path and logout
        if (user.isMustChangePassword()) {
            boolean onChangePwd = path.startsWith("/account/change-password");
            boolean onLogout = path.startsWith("/logout");
            if (!(onChangePwd || onLogout)) {
                resp.sendRedirect(req.getContextPath() + "/account/change-password");
                return;
            }
        }

        // Role-based authorization
        User.Role role = user.getRole();
        boolean allowed = false;
        // Allow all authenticated users to change their own password
        if (path.startsWith("/account/change-password")) {
            allowed = true;
        } else if (role == User.Role.ADMIN) {
            allowed = true; // all access for landlord/admin
        } else if (role == User.Role.USER) {
            // tenant: read-only limited to personal pages
            if ("GET".equalsIgnoreCase(req.getMethod())) {
                if (path.startsWith("/hoadon/mine") || path.startsWith("/hopdong/mine")) {
                    allowed = true;
                }
            }
        }

        if (!allowed) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("403 Forbidden");
            return;
        }

        chain.doFilter(request, response);
    }
}

