package com.pda.portfolioservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {
    @GetMapping("/test")
    public String test2() {
        return "Portfolio test";
    }
}
