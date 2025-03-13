package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.SearchHistory;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.SearchHistoryService;
import com.law.law_qa_system.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/search-history")
public class SearchHistoryController {
    @Autowired
    private SearchHistoryService searchHistoryService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getUserSearchHistory(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/";
        }

        User user = userService.getUserByEmail(account.getEmail());
        List<SearchHistory> searchHistoryList = searchHistoryService.getUserSearchHistory(user.getId());

        model.addAttribute("searchHistoryList", searchHistoryList);
        return "fragments/search-history :: searchHistoryList";
    }
}
