package com.example.guruxdlmsserver.service;

import com.example.guruxdlmsserver.model.MeterData;
import gurux.dlms.*;
import gurux.dlms.enums.ObjectType;
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

    /**
     * Fetches meter data using DLMS communication.
     */
    public MeterData fetchMeterData() throws Exception {
        GXReplyData reply = new GXReplyData();
        MeterData meterData = new MeterData();

        try (Socket socket = new Socket(meterIp, meterPort)) {
            GXCommunicator communicator = new GXCommunicator(socket);

            // Step 1: Connect to the meter
            connectToMeter(communicator, reply);

            // Step 2: Fetch Meter Object List and Read Required Data
            GXDLMSObjectCollection objects = client.getObjects();
            meterData.setEnergy(readMeterValue(communicator, objects, ObjectType.DATA, "1.0.1.8.0.255"));
            meterData.setVoltage(readMeterValue(communicator, objects, ObjectType.REGISTER, "1.0.32.7.0.255"));
            meterData.setCurrent(readMeterValue(communicator, objects, ObjectType.REGISTER, "1.0.31.7.0.255"));

        } catch (Exception e) {
            LOGGER.severe("Error reading meter data: " + e.getMessage());
            throw new Exception("Failed to communicate with the meter.");
        } finally {
            // Step 3: Disconnect from the meter
            disconnectFromMeter(reply);
        }
        return meterData;
    }

    /**
     * Establishes a connection to the meter and authenticates the session.
     */
    private void connectToMeter(GXCommunicator communicator, GXReplyData reply) throws Exception {
        byte[] snrmRequest = client.snrmRequest();
        if (snrmRequest != null) {
            communicator.sendData(snrmRequest, reply);
            client.parseUAResponse(reply.getData());
        }

        // Perform authentication using AARQ request
        for (byte[] request : client.aarqRequest()) {
            reply.clear();
            communicator.sendData(request, reply);
        }
        client.parseAareResponse(reply.getData());
    }

    /**
     * Reads an object value from the meter.
     */
    private String readMeterValue(GXCommunicator communicator, GXDLMSObjectCollection objects, ObjectType objectType,
            String logicalName) throws Exception {
        GXDLMSObject obj = objects.findByLN(objectType, logicalName);
        if (obj == null)
            return "N/A";

        // Send read request
        GXReplyData reply = new GXReplyData();
        byte[][] data = client.read(obj, 2); // Attribute 2 (value)
        for (byte[] packet : data) {
            communicator.sendData(packet, reply);
        }

        // Extract value from the response and update the object
        Object value = client.updateValue(obj, 2, reply.getValue());

        return value != null ? value.toString() : "N/A";
    }

    /**
     * Disconnects the meter session.
     */
    private void disconnectFromMeter(GXReplyData reply) throws Exception {
        reply.clear();
        byte[] disconnectRequest = client.disconnectRequest();
        if (disconnectRequest != null) {
            // Here, you should send the disconnect request using your communicator
        }
    }
}
