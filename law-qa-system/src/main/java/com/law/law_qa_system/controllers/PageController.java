package com.law.law_qa_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
//    @GetMapping("/home")
//    public String showHomePage() {
//        return "dashboard";
//    }


    @GetMapping("/user")
    public String showUserPage() {
        return "userPage";
    }

    @GetMapping("/bai")
    public String showBaiViet() {
        return "BaiViet";
    }
}
