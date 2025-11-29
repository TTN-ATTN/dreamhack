package com.acsc2025.controller;

import freemarker.template.TemplateException;
import java.io.IOException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/controller/GlobalExceptionHandler.class */
public class GlobalExceptionHandler {
    @ExceptionHandler({Exception.class})
    public String handleException(Exception e) {
        return "redirect:/images/error.png";
    }

    @ExceptionHandler({IllegalStateException.class})
    public String handleIllegalStateException(IllegalStateException e) {
        return "redirect:/image/session_error.png";
    }

    @ExceptionHandler({TemplateException.class, IOException.class})
    public String handleTemplateException(Exception e) {
        return "redirect:/images/nono.png";
    }
}
