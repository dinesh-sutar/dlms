package com.example.guruxdlmsserver.controller;

import com.example.guruxdlmsserver.model.MeterData;
import com.example.guruxdlmsserver.service.DlmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dlms")
public class DlmsController {

    @Autowired
    private DlmsService dlmsService;

    @GetMapping("/data")
    public ResponseEntity<?> getMeterData() {
        try {
            MeterData meterData = dlmsService.fetchMeterData();
            return ResponseEntity.ok(meterData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
