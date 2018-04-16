/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Timer;

/**
 *
 * @author davi
 */
public class Discoverer {

    Thread t;
    ArrayList<JsonObject> devicesDiscovered;

    public Discoverer(int times, int timeout) {
        devicesDiscovered = new ArrayList<>();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MulticastSocket discoverSocket = new MulticastSocket();
                    InetAddress group = InetAddress.getByName("239.2.3.9");
                    discoverSocket.joinGroup(group);
                    discoverSocket.setTimeToLive(16);
                    discoverSocket.setSoTimeout(timeout);
                    byte[] buffer = new byte[2000];
                    String s = "HADOUKEN";
                    buffer = s.getBytes();
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length, group, 50135);
                    discoverSocket.send(dp);
                    JsonObject obj = new JsonObject();
                    for (int i = 0; i < times; i++) {
                        buffer = new byte[2000];
                        dp = new DatagramPacket(buffer, buffer.length);
                        try{
                        discoverSocket.receive(dp);

                        buffer = dp.getData();
                        String received = new String(buffer);
                        received = received.trim();
                        obj = new Gson().fromJson(received, JsonObject.class);
                        devicesDiscovered.add(obj);
                        
                        System.out.println(received);
                        } catch(SocketTimeoutException e){
                            System.out.println("Datagram timed out.");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Discoverer.class.getName()).log(Level.SEVERE, null, ex);
                }
                t.interrupt();
            }
        });
    }

    public ArrayList<JsonObject> discover() {
        t.start();
        while(t.isAlive());
        return devicesDiscovered;
    }

}
