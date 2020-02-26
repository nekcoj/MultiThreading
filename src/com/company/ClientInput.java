package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class ClientInput implements Runnable{
    private Scanner scan;
    private ArrayList<Observer<String>> observers;

    public ClientInput(){
        observers = new ArrayList<>();
        scan = new Scanner(System.in);
    }

    public void subscribe(Observer<String> observer) {
        observers.add(observer);
    }

    @Override
    public void run(){
        while(NetworkClient.isRunning.get()){
            var userInput = scan.nextLine();
            for(var observer: observers) {
                observer.updateObserver(userInput);
            }
        }
    }
}
