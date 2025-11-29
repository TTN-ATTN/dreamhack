package com.acsc2025.controller;

import com.acsc2025.Present;
import com.acsc2025.config.FreemarkerConfig;
import freemarker.template.Template;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/controller/GiftController.class */
public class GiftController {
    private static final int FLAG_ITEM_ID = 6;
    private static final Pattern FORBIDDEN = Pattern.compile("(?i)getClass|forName|getMethod|invoke|exec|lower_abc|upper_abc|\\bnew\\b|\\beval\\b|\\bapi\\b|\\bfile\\b|include|import|macro|java\\.lang|javax\\.|com\\.|freemarker|Template|\"|'|\\?|<#");

    @GetMapping({"/gift"})
    public String giftForm(Model m, HttpSession s) {
        if (s == null) {
            throw new IllegalStateException();
        }
        List<Map<String, Object>> history = (List) s.getAttribute("purchasedItems");
        int balance = ((Integer) Optional.ofNullable((Integer) s.getAttribute("balance")).orElse(100000)).intValue();
        int points = ((Integer) Optional.ofNullable((Integer) s.getAttribute("points")).orElse(0)).intValue();
        m.addAttribute("balance", Integer.valueOf(balance));
        m.addAttribute("points", Integer.valueOf(points));
        boolean hasFlag = history != null && history.stream().anyMatch(item -> {
            return 6 == ((Integer) item.get("id")).intValue();
        });
        if (!hasFlag) {
            return "gift-denied";
        }
        return "gift";
    }

    @PostMapping({"/gift"})
    public String giftPreview(@RequestParam String message, Model m, HttpSession s) throws Exception {
        if (s == null) {
            throw new IllegalStateException();
        }
        List<Map<String, Object>> history = (List) s.getAttribute("purchasedItems");
        boolean hasFlag = history != null && history.stream().anyMatch(item -> {
            return 6 == ((Integer) item.get("id")).intValue();
        });
        int balance = ((Integer) Optional.ofNullable((Integer) s.getAttribute("balance")).orElse(100000)).intValue();
        int points = ((Integer) Optional.ofNullable((Integer) s.getAttribute("points")).orElse(0)).intValue();
        m.addAttribute("balance", Integer.valueOf(balance));
        m.addAttribute("points", Integer.valueOf(points));
        if (!hasFlag) {
            return "redirect:/images/nohack.png";
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (FORBIDDEN.matcher(message).find()) {
            return "redirect:/images/nohack.png";
        }
        m.addAttribute("present", new Present());
        Template t = new Template("giftPreview", message, FreemarkerConfig.getConfiguration());
        String rendered = FreeMarkerTemplateUtils.processTemplateIntoString(t, m.asMap());
        m.addAttribute("preview", rendered);
        return "gift";
    }
}
