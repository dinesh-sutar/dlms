package com.example.guruxdlmsserver.service;

import com.example.guruxdlmsserver.model.MeterData;
import gurux.dlms.*;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.logging.Logger;

@Service
public class DlmsService {

    @Autowired
    private GXDLMSClient client;

    @Autowired
    private String meterIp;

    @Autowired
    private Integer meterPort;

    private static final Logger LOGGER = Logger.getLogger(DlmsService.class.getName());

    public MeterData fetchMeterData() throws Exception {
        GXReplyData reply = new GXReplyData();
        MeterData meterData = new MeterData();

        try (Socket socket = new Socket(meterIp, meterPort)) {
            GXCommunicator communicator = new GXCommunicator(socket);

            connectToMeter(communicator, reply);

            GXDLMSObjectCollection objects = client.getObjects();
            meterData.setEnergy(readMeterValue(communicator, objects, ObjectType.DATA, "1.0.1.8.0.255"));
            meterData.setVoltage(readMeterValue(communicator, objects, ObjectType.REGISTER, "1.0.32.7.0.255"));
            meterData.setCurrent(readMeterValue(communicator, objects, ObjectType.REGISTER, "1.0.31.7.0.255"));

        } catch (Exception e) {
            System.out.println("**************************Error reading meter data: " + e);
            LOGGER.severe("Error reading meter data: " + e.getMessage());
            throw new Exception("Failed to communicate with the meter.");
        } finally {
            disconnectFromMeter(reply);
        }
        return meterData;
    }

    private void connectToMeter(GXCommunicator communicator, GXReplyData reply) throws Exception {
        byte[] snrmRequest = client.snrmRequest();
        if (snrmRequest != null) {
            LOGGER.info("Sending SNRM request...");
            communicator.sendData(snrmRequest, reply);
            LOGGER.info("SNRM Response: " + GXCommon.toHex(reply.getData().array(), false));
            client.parseUAResponse(reply.getData());
        }

        for (byte[] request : client.aarqRequest()) {
            reply.clear();
            LOGGER.info("Sending AARQ request...");
            communicator.sendData(request, reply);
            LOGGER.info("AARE Response: " + GXCommon.toHex(reply.getData().array(), false));
        }
        client.parseAareResponse(reply.getData());
    }

    private String readMeterValue(GXCommunicator communicator, GXDLMSObjectCollection objects, ObjectType objectType,
            String logicalName) throws Exception {
        GXDLMSObject obj = objects.findByLN(objectType, logicalName);
        if (obj == null)
            return "N/A";

        GXReplyData reply = new GXReplyData();
        byte[][] data = client.read(obj, 2);
        for (byte[] packet : data) {
            communicator.sendData(packet, reply);
        }

        Object value = client.updateValue(obj, 2, reply.getValue());

        return value != null ? value.toString() : "N/A";
    }

    private void disconnectFromMeter(GXReplyData reply) throws Exception {
        reply.clear();
        byte[] disconnectRequest = client.disconnectRequest();
        if (disconnectRequest != null) {
            LOGGER.info("Sending disconnect request...");
        }
    }
}
