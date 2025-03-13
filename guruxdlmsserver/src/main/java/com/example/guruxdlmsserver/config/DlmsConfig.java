package com.example.guruxdlmsserver.config;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlmsConfig {

    // Define meter connection details
    private static final String METER_IP = "2401:4900:9807:327b::2"; // Meter IPv6 Address
    private static final int METER_PORT = 4059; // Meter communication port
    private static final int CLIENT_ADDRESS = 48; // Client Address
    private static final int SERVER_ADDRESS = 1; // Server Address (Meter Address)
    private static final Authentication AUTHENTICATION = Authentication.HIGH; // Authentication Level
    private static final String PASSWORD = "1A2B3C4D5E6F7G8H"; // Meter Password
    private static final InterfaceType INTERFACE_TYPE = InterfaceType.WRAPPER; // Communication Interface
    private static final String SYSTEM_TITLE = "4953453137323330"; // System Title (Hex)
    private static final String AUTHENTICATION_KEY = "32323232323232323232323232323232"; // Authentication Key (16-byte
                                                                                         // Hex)
    private static final String ENCRYPTION_KEY = "32323232323232323232323232323232"; // Encryption Key (16-byte Hex)

    @Bean
    public GXDLMSClient dlmsClient() {
        return new GXDLMSClient(
                true, // Use Logical Name Referencing
                CLIENT_ADDRESS,
                SERVER_ADDRESS,
                AUTHENTICATION,
                PASSWORD,
                INTERFACE_TYPE);
    }

    @Bean
    public String meterIp() {
        return METER_IP;
    }

    @Bean
    public Integer meterPort() {
        return METER_PORT;
    }

    @Bean
    public String authenticationKey() {
        return AUTHENTICATION_KEY;
    }

    @Bean
    public String encryptionKey() {
        return ENCRYPTION_KEY;
    }

    @Bean
    public String systemTitle() {
        return SYSTEM_TITLE;
    }
}
