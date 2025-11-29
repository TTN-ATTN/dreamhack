package com.acsc2025.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/controller/MyPageController.class */
public class MyPageController {
    @GetMapping({"/mypage"})
    public String mypage(Model m, HttpSession s) {
        if (s == null) {
            throw new IllegalStateException();
        }
        List<Map<String, Object>> history = (List) s.getAttribute("purchasedItems");
        int balance = ((Integer) Optional.ofNullable((Integer) s.getAttribute("balance")).orElse(100000)).intValue();
        int points = ((Integer) Optional.ofNullable((Integer) s.getAttribute("points")).orElse(0)).intValue();
        m.addAttribute("balance", Integer.valueOf(balance));
        m.addAttribute("points", Integer.valueOf(points));
        if (history == null) {
            m.addAttribute("none", true);
            return "mypage";
        }
        m.addAttribute("history", history);
        return "mypage";
    }
}
