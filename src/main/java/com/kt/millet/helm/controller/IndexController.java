package com.kt.millet.helm.test_dev;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/index")
    public String index() {
        return "forward:/index.html";
    }
}