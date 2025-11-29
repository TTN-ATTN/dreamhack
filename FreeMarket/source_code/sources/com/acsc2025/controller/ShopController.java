package com.acsc2025.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.apache.tomcat.jni.Status;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/controller/ShopController.class */
public class ShopController {
    private static final List<Map<String, Object>> ITEMS = Arrays.asList(Map.of("id", 1, "name", "Light Stick", "price", Integer.valueOf(Status.APR_OS_ERRSPACE_SIZE), "image", "/images/lightstick.png"), Map.of("id", 2, "name", "Photo Card", "price", 12000, "image", "/images/photo_card.png"), Map.of("id", 3, "name", "Album", "price", 35000, "image", "/images/album.png"), Map.of("id", 4, "name", "T-Shirts", "price", 25000, "image", "/images/shirts.png"), Map.of("id", 5, "name", "Concert Ticket", "price", 80000, "image", "/images/concert_ticket.png"), Map.of("id", 6, "name", "FLAG", "price", 110000, "image", "/images/flag.png"));

    @GetMapping({"/"})
    public String shop(Map<String, Object> m, HttpSession s) {
        int balance = ((Integer) Optional.ofNullable((Integer) s.getAttribute("balance")).orElse(100000)).intValue();
        int points = ((Integer) Optional.ofNullable((Integer) s.getAttribute("points")).orElse(0)).intValue();
        s.setAttribute("balance", Integer.valueOf(balance));
        s.setAttribute("points", Integer.valueOf(points));
        m.put("items", ITEMS);
        m.put("balance", Integer.valueOf(balance));
        m.put("points", Integer.valueOf(points));
        return "shop";
    }

    @GetMapping({"/purchase"})
    public String buy(@RequestParam int id, @RequestParam(name = "_", required = false) Integer sp, HttpSession s) throws Throwable {
        int balance;
        if (s == null) {
            throw new IllegalStateException("Session is invalid");
        }
        Map<String, Object> item = ITEMS.stream().filter(i -> {
            return i.get("id").equals(Integer.valueOf(id));
        }).findFirst().orElseThrow(() -> {
            return new IllegalArgumentException("Invalid item ID");
        });
        Integer price = (Integer) item.get("price");
        if (price == null) {
            throw new IllegalStateException("Item price is not set");
        }
        int balance2 = ((Integer) Optional.ofNullable((Integer) s.getAttribute("balance")).orElse(100000)).intValue();
        int oldPoints = ((Integer) Optional.ofNullable((Integer) s.getAttribute("points")).orElse(0)).intValue();
        if (balance2 + oldPoints < price.intValue()) {
            return "redirect:/images/hehe.png";
        }
        int usePoints = ((Integer) Optional.ofNullable(sp).orElse(Integer.valueOf(oldPoints))).intValue();
        int usePoints2 = Math.max(0, Math.min(usePoints, oldPoints));
        int points = oldPoints + price.intValue();
        s.setAttribute("points", Integer.valueOf(points));
        if (balance2 + usePoints2 < price.intValue()) {
            return "redirect:/images/hehe.png";
        }
        if (balance2 >= price.intValue()) {
            balance = balance2 - price.intValue();
        } else {
            int need = price.intValue() - balance2;
            balance = 0;
            points -= need;
        }
        List<Map<String, Object>> history = (List) s.getAttribute("purchasedItems");
        if (history == null) {
            history = new ArrayList<>();
        }
        history.add(item);
        s.setAttribute("purchasedItems", history);
        s.setAttribute("balance", Integer.valueOf(balance));
        s.setAttribute("points", Integer.valueOf(points));
        return "redirect:/";
    }
}
