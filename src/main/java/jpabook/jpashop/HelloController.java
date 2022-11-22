package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){ // Model에다가 data를 실어서 controller에서 view로 데이터를 넘길 수 있음
        model.addAttribute("data", "hello!!!"); // key = data, value = hello!!!
        return "hello"; // resources/templates/hello.html
    }
}
