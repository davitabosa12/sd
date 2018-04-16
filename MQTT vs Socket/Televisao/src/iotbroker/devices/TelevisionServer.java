/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker.devices;

import iotpackage.Command;
import iotpackage.IoTAction;
import iotpackage.Message;
import iotpackage.OnTVValueChange;
import iotpackage.Television;
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
public class TelevisionServer {
    private int serverPort;
    private String host;
    private Thread serverThread;
    private ServerSocket myServer;
    private String tvName;
    private String tvDesc;
    private Television tv;
    private OnTVValueChange callback;
    private DiscoverService discover;
    
    
    public TelevisionServer(String tvName, String tvDesc, int port){
        try {
            this.serverPort = port;
            this.tvName = tvName;
            this.tvDesc = tvDesc;
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
            Logger.getLogger(TelevisionServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private void serverStuff(){
        tv = new Television(tvName, tvDesc);
        tv.setHost(host);
        tv.setPort(serverPort);
        call();
        tv.report();
        System.out.println("TV server started");
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
                Logger.getLogger(TelevisionServer.class.getName()).log(Level.SEVERE, null, ex);
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
                tv.turnOn();
                m = new Message(Message.OK, tv.getDeviceName() + " is now ON");
                call();
                break;
            case IoTAction.TURN_OFF:
                tv.turnOff();
                m = new Message(Message.OK, tv.getDeviceName() + " is now OFF");
                call();
                break;
            case IoTAction.SET_VOLUME:
                tv.setVolume(iotaction.getParam());
                m = new Message(Message.OK, tv.getDeviceName() + " Set volume OK");
                call();
                break;
            case IoTAction.SET_CHANNEL:
                tv.setChannel(iotaction.getParam());
                m = new Message(Message.OK, tv.getDeviceName() + " Set channel OK");
                call();
                break;
            case IoTAction.REPORT:
                String s = tv.report().getMessage();
                m = new Message(Message.OK, s);
                break;
            default:
                m = new Message(Message.ERROR, tv.getDeviceName() + " doesn't support this action: " + IoTAction.getActionName(action));
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
            callback.deviceStateChanged(tv.isDeviceOn());
            callback.channelChanged(tv.getChannel());
            callback.volumeChanged(tv.getVolume());
        } catch(NullPointerException e){
            
        }
    }

    public void setCallback(OnTVValueChange callback) {
        this.callback = callback;
    }
    
}
