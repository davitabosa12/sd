/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker.devices;

import iotpackage.Command;
import iotpackage.IoTAction;
import iotpackage.LightDevice;
import iotpackage.Message;
import iotpackage.OnDeviceStateChange;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davi
 */
public class LightServer {
    
    private int serverPort;
    private String host;
    private Thread serverThread;
    private ServerSocket myServer;
    private String lightName;
    private String lightDesc;
    private LightDevice light;
    private OnDeviceStateChange callback;
    private DiscoverService discover;
    
    public LightServer(String lightName, String lightDesc, int port){
        try {
            this.serverPort = port;
            this.lightName = lightName;
            this.lightDesc = lightDesc;
            myServer = new ServerSocket(serverPort);
            this.host = InetAddress.getLocalHost().getHostAddress();
            discover = new DiscoverService(host, port);
            discover.start();
            serverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    serverStuff();
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(LightServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void serverStuff(){
        light = new LightDevice(lightName, lightDesc);
        light.setHost(host);
        light.setPort(serverPort);
        call();
        light.report();
        System.out.println("LightDevice server started");
        while (true) { //Server accept loop
            try {

                Socket clientSocket = myServer.accept();
                ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
                
                System.out.println("Client accepted!");
                
                Command gotCommand = getRequest(clientIn);
                Message response = doOperation(gotCommand);
                sendReply(clientOut, response);
                clientSocket.close();
                
                
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(LightServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Command getRequest(ObjectInputStream clientIn) throws IOException, ClassNotFoundException {
        System.out.println("Getting request..");
        //lê comando do cliente..
        Command c = (Command) clientIn.readObject();
        System.out.println("Got request");
        return c;
    }

    private Message doOperation(Command c) {
        System.out.println("Doing operation..");
        IoTAction iotaction = c.getAction();
        int action = iotaction.getAction();
        Message m;
        switch(action){
            case IoTAction.TURN_ON:
                light.turnOn();
                m = new Message(Message.OK, light.getDeviceName() + " is now ON");
                call();
                break;
            case IoTAction.TURN_OFF:
                light.turnOff();
                m = new Message(Message.OK, light.getDeviceName() + " is now OFF");
                call();
                break;
            case IoTAction.REPORT:
                String s = light.report().getMessage();
                m = new Message(Message.OK, s);
                break;
            default:
                m = new Message(Message.ERROR, light.getDeviceName() + " doesn't support this action: " + IoTAction.getActionName(action));
                break;
         
        }
        
        return m;
    }

    private void sendReply(ObjectOutputStream out, Message message) throws IOException {
        out.writeObject(message);
    }
    
    public void start(){
        serverThread.start();
    }
    public void stop(){
        serverThread.interrupt();
    }
    
    //funcao que avisa as GUIs que algo mudou
    private void call(){
        try{
            callback.deviceStateChanged(light.isDeviceOn());
        } catch(NullPointerException e){
            
        }
    }

    public void setCallback(OnDeviceStateChange callback) {
        this.callback = callback;
    }
}
