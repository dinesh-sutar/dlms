package com.example.guruxdlmsserver.service;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXReplyData;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;
import gurux.dlms.internal.GXByteBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DlmsService {

    @Autowired
    private GXDLMSClient client;

    public Map<String, String> fetchMeterData() throws Exception {
        Map<String, String> meterData = new HashMap<>();
        GXReplyData reply = new GXReplyData();

        try {
            // Connect to the meter
            byte[] snrmRequest = client.snrmRequest();
            if (snrmRequest != null) {
                processDLMSPacket(snrmRequest, reply);
                client.parseUAResponse(reply.getData());
            }

            // Authenticate and establish connection
            for (byte[] request : client.aarqRequest()) {
                reply.clear();
                processDLMSPacket(request, reply);
            }
            client.parseAareResponse(reply.getData());

            // Fetch Meter Object List
            GXDLMSObjectCollection objects = client.getObjects();
            meterData.put("Total Energy (kWh)", readMeterObject(objects, ObjectType.DATA, "1.0.1.8.0.255"));
            meterData.put("Voltage (V)", readMeterObject(objects, ObjectType.REGISTER, "1.0.32.7.0.255"));
            meterData.put("Current (A)", readMeterObject(objects, ObjectType.REGISTER, "1.0.31.7.0.255"));

            // Close Connection
            reply.clear();
            processDLMSPacket(client.disconnectRequest(), reply);
        } catch (Exception e) {
            throw new Exception("Error reading meter data: " + e.getMessage());
        }
        return meterData;
    }

    private String readMeterObject(GXDLMSObjectCollection objects, ObjectType objectType, String logicalName) {
        GXDLMSObject obj = objects.findByLN(objectType, logicalName);
        return (obj != null) ? obj.toString() : "N/A";
    }

    private void processDLMSPacket(byte[] data, GXReplyData reply) throws Exception {
        if (data != null && data.length > 0) {
            // Convert byte[] to GXByteBuffer
            GXByteBuffer buffer = new GXByteBuffer(data);
            reply.setData(buffer);
        }
    }
}
