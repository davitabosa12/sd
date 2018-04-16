/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker.devices;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davi
 */
public class DiscoverService {

    Thread t;
    MulticastSocket discoverSocket;
    String hostToSend;
    int portToSend;
    int bufferSize = 2000;
    OnDiscoverCallback callback;

    public DiscoverService(String hostToSend, int portToSend) {

        try {
            discoverSocket = new MulticastSocket(50135);
            discoverSocket.joinGroup(InetAddress.getByName("239.2.3.9"));
            discoverSocket.setTimeToLive(16);
        } catch (IOException ex) {
            Logger.getLogger(DiscoverService.class.getName()).log(Level.SEVERE, null, ex);
        }
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Discover service enabled.");
                    while (true) {
                        byte[] buffer = new byte[bufferSize];
                        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                        discoverSocket.receive(dp);
                        System.out.println("Discover service received datagram");    
                        String received = new String(buffer);
                        if ("HADOUKEN".equalsIgnoreCase(received.trim())) {
                            if(callback != null)
                                callback.onDiscover();
                            Gson g = new Gson();
                            JsonObject json = new JsonObject();
                            json.addProperty("host", hostToSend);
                            json.addProperty("port", portToSend);
                            String resp = g.toJson(json);
                            System.err.println(resp);
                            buffer= new byte[2000];
                            buffer = resp.getBytes();
                            dp = new DatagramPacket(buffer, buffer.length, dp.getAddress(), dp.getPort());
                            discoverSocket.send(dp);
                        }
                        else{
                            String resp = "BOO";
                            buffer = resp.getBytes();
                            dp = new DatagramPacket(buffer, buffer.length, dp.getAddress(), dp.getPort());
                            discoverSocket.send(dp);
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(DiscoverService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
    
    public void start(){
        t.start();
    }
    
    public void stop(){
        t.interrupt();
    }

    public void setCallback(OnDiscoverCallback discoverable) {
        this.callback = discoverable;
    }
    
    

}
