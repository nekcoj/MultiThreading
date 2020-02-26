package com.company;

public class Main {

    public static void main(String[] args) {
        Thread server = new Thread(NetworkServer.getInstance(), "Server");
        server.start();
        NetworkClient client = new NetworkClient();
        client.run();

    }
}
