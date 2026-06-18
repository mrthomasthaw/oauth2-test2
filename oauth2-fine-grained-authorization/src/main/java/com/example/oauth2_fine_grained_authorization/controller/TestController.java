package com.example.oauth2_fine_grained_authorization.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String hello() {
        System.out.println("hello-ddd");
        return "hello-boy";
    }

    @GetMapping("/protected-page")
    public String protectedPage() {
        System.out.println("protected-page");
        return "protected-page";
    }

    @GetMapping("/students")
    public String students() {
        System.out.println("student");
        return "students";
    }

    @GetMapping("/professors")
    public String professors() {
        System.out.println("professors");
        return "professors";
    }

    @GetMapping("/employees")
    public String employees() {
        System.out.println("employees");
        return "employees";
    }
}
