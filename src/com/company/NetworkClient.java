package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkClient implements Runnable, Observer<String>{
    private final String SERVER_IP = "10.152.190.63";
    private final int MSG_SIZE = 512;
    private final int SLEEP_MS = 100;
    static AtomicBoolean isRunning = new AtomicBoolean(true);

    private DatagramSocket socket;
    private InetAddress serverAddress;

    private Thread inputThread;

    public NetworkClient(){
        try {
            serverAddress = InetAddress.getByName(SERVER_IP);
            socket = new DatagramSocket(0);
            socket.setSoTimeout(SLEEP_MS);
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    public void sendMsgToServer(String msg) {
        byte[] buffer = msg.getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, this.serverAddress, NetworkServer.PORT);
        try {
            socket.send(request);
        } catch (Exception ignored) {}
    }

    private void receiveMessageFromServer() {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(response);
            var serverMsg = new String(buffer, 0, response.getLength());
            System.out.println("Message recieved.");
            System.out.println(response.getSocketAddress() + " -> " + serverMsg); // debugging purpose only!

        } catch (Exception ex) {
            try { Thread.sleep(SLEEP_MS); }
            catch (Exception ignored) {}
        }
    }

    private void runThreads(){
        var clientInput = new ClientInput();
        clientInput.subscribe(this);
        inputThread = new Thread(clientInput, "ClientInput");
        inputThread.start();
    }

    @Override
    public void run() {
        System.out.print("Write message> ");
        while (isRunning.get()) {
            runThreads();
            receiveMessageFromServer();
        }
    }

    @Override
    public void updateObserver(String data) {
//        System.out.println("Message sent through observer: " + data);
        sendMsgToServer(data);
    }
}
