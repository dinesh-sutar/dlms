package com.example.guruxdlmsserver.config;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.enums.InterfaceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlmsConfig {

    @Bean
    public GXDLMSClient dlmsClient() {
        GXDLMSClient client = new GXDLMSClient();

        // DLMS Client Configuration
        client.setUseLogicalNameReferencing(true);
        client.setInterfaceType(InterfaceType.HDLC); // Can be WRAPPER for TCP
        client.setClientAddress(16);
        client.setServerAddress(1);

        return client;
    }
}
