package com.quanlyphongtro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @RequestMapping("/")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home"); // home.jsp
        mav.addObject("message", "Chào mừng đến với Quản Lý Phòng Trọ!");
        return mav;
    }
}
