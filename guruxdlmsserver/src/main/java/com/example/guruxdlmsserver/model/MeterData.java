package com.example.guruxdlmsserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeterData {
    private String energy;
    private String voltage;
    private String current;
}
