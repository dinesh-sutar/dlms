
package com.example.guruxdlmsserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.guruxdlmsserver.model.MeterData;
import com.example.guruxdlmsserver.service.DlmsService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dlms")
@RequiredArgsConstructor
public class DlmsController {

    private final DlmsService dlmsService;

    @GetMapping("/data")
    public ResponseEntity<?> getMeterData() {
        try {
            MeterData data = dlmsService.fetchMeterData();

            // Convert MeterData to Map
            Map<String, String> response = new HashMap<>();
            response.put("energy", data.getEnergy());
            response.put("voltage", data.getVoltage());
            response.put("current", data.getCurrent());

            return ResponseEntity.ok(response); // 200 OK
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage())); // 500 Error
        }
    }
}
