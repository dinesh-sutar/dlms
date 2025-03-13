package com.example.guruxdlmsserver.config;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlmsConfig {

    private static final String METER_IP = "2401:4900:9807:327b::2"; 
    private static final int METER_PORT = 4059;
    private static final int CLIENT_ADDRESS = 48;
    private static final int SERVER_ADDRESS = 1;
    private static final Authentication AUTHENTICATION = Authentication.HIGH;
    private static final String PASSWORD = "SMVAKS5rgocCE66QM+s/xC3QTN90FcB3ADoBWSOuhMU=";
    private static final InterfaceType INTERFACE_TYPE = InterfaceType.WRAPPER;
    private static final String SYSTEM_TITLE = "494E4553482D4433";
    private static final String AUTHENTICATION_KEY = "32323232323232323232323232323232";
    private static final String ENCRYPTION_KEY = "32323232323232323232323232323232";

    @Bean
    public GXDLMSClient dlmsClient() {
        return new GXDLMSClient(
                true, 
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
