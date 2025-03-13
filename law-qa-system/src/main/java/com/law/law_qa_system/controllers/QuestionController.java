package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/luat-su-tu-van")
public class QuestionController {

    @GetMapping({"", "/"})
    public String showQuestion(Model model, Principal principal) {
        return "LuatSuTuVan";
    }
    @GetMapping( "/add")
    public String addQuestion(Model model, Principal principal) {
        return "TaoCauHoi";
    }
}
