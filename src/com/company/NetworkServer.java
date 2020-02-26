package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class NetworkServer implements Runnable{

    private static final NetworkServer INSTANCE = new NetworkServer();
    public static final int PORT = 3001;
    private final int SLEEP_MS = 100;
    private final int MSG_SIZE = 512;
    private DatagramSocket socket;
    boolean isRunning = true;

    private ArrayList<SocketAddress> clientSocketAddressList;

    private NetworkServer(){
        try {
            clientSocketAddressList = new ArrayList<>();
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SLEEP_MS);
        } catch(SocketException e){ System.out.println(e.getMessage()); }
    }

    public static NetworkServer getInstance(){
        return INSTANCE;
    }

    public void sendMsgToClient(String msg, SocketAddress clientSocketAddress) {
        byte[] buffer = msg.getBytes();
        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientSocketAddress);
        try {
            socket.send(response);
            System.out.println("Message sent.");
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void run() {
        while (isRunning) {
            DatagramPacket clientRequest = new DatagramPacket(new byte[MSG_SIZE], MSG_SIZE);;

            if (!receiveMsgFromAnyClient(clientRequest)) {
                continue;
            }

            String clientMsg = new String(clientRequest.getData(), 0, clientRequest.getLength());
//            System.out.println(clientMsg);
            for (var address : clientSocketAddressList) {
//                if (!clientRequest.getSocketAddress().equals(address)) {
                    sendMsgToClient(clientMsg, address);
//                }
            }
        }
    }

    private boolean receiveMsgFromAnyClient(DatagramPacket clientRequest){
        try {
            socket.receive(clientRequest);
            if(!clientSocketAddressList.contains(clientRequest.getSocketAddress())){
                clientSocketAddressList.add(clientRequest.getSocketAddress());
            }
        }
        catch (Exception ex) { return false; }
        return true;
    }

}
