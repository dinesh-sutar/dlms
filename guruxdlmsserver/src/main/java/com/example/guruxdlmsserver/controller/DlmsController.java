package com.example.guruxdlmsserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.guruxdlmsserver.service.DlmsService;

import java.util.Map;

@RestController
@RequestMapping("/api/dlms")
public class DlmsController {

    @Autowired
    private DlmsService dlmsService;

    @GetMapping("/data")
    public Map<String, String> getMeterData() {
        try {
            return dlmsService.fetchMeterData();
        } catch (Exception e) {
            return Map.of("Error", e.getMessage());
        }
    }
}
