package com.example.demo;

import com.example.demo.service.GeniusApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    private GeniusApiService geniusApiService;

    @GetMapping("/")
    public String hello() {
        return "index";
    }

    @PostMapping("/search")
    @ResponseBody
    public Map<String, String> search(@RequestParam String artist, @RequestParam String title) {
        Map<String, String> response = new HashMap<>();
        String lyrics = geniusApiService.searchSong(artist, title);
        response.put("result", lyrics);
        return response;
    }
}

