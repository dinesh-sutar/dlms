package com.example.guruxdlmsserver.service;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXReplyData;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GXCommunicator {

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public GXCommunicator(Socket socket) throws Exception {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    public void sendData(byte[] data, GXReplyData reply) throws Exception {
        if (data == null || data.length == 0) {
            throw new Exception("Invalid DLMS packet.");
        }

        output.write(data);
        output.flush();

        // Read response
        byte[] responseBuffer = new byte[1024];
        int bytesRead = input.read(responseBuffer);
        if (bytesRead == -1) {
            throw new Exception("No response from meter.");
        }

        // Process response
        GXByteBuffer buffer = new GXByteBuffer(responseBuffer);
        reply.setData(buffer);
    }

    public void close() throws Exception {
        input.close();
        output.close();
        socket.close();
    }
}
