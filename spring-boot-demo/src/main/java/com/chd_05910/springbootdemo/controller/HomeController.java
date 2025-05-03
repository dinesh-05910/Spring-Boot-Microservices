package com.chd_05910.springbootdemo.controller;

import com.chd_05910.springbootdemo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    @GetMapping("/user")
    public User user() {
        User user = new User();
        user.setId("1");
        user.setEmailId("chd@gmail.com");
        user.setName("Dinesh");

        return user;
    }

    @GetMapping("/{id}/{id2}")
    public String getParams(@PathVariable String id, @PathVariable("id2") String name) {
        return "The Path Variable from URL :" + id + " Second " + name;
    }

    @GetMapping("/requestParams")
    public String requestParams(@RequestParam String name, @RequestParam(value = "email", required = false, defaultValue = " ") String emailId) {
        return "Your name is : " + name + " and emailId is " + emailId;
    }
}
