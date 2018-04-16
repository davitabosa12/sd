/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker.devices;

import iotpackage.AC;
import iotpackage.Command;
import iotpackage.IoTAction;
import iotpackage.IoTDevice;
import iotpackage.Message;
import iotpackage.OnACValueChange;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davi
 */
public class ACServer {
    
    private int serverPort;
    private String host;
    private Thread serverThread;
    private ServerSocket myServer;
    private String acName;
    private String acDesc;
    private AC ac;
    private OnACValueChange callback;
    private DiscoverService discover;
    
    
    public ACServer(String acName, String acDesc, int port){
        try {
            this.serverPort = port;
            this.acName = acName;
            this.acDesc = acDesc;
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
            Logger.getLogger(ACServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private void serverStuff(){
        ac = new AC(acName, acDesc);
        ac.setHost(host);
        ac.setPort(serverPort);
        call();
        ac.report();
        System.out.println("AC server started");
        while (true) { //Server accept loop
            try {

                System.out.println("Listening...");
                Socket clientSocket = myServer.accept();
                ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
                
                System.out.println("Client accepted!");
                
                Command gotCommand = getRequest(clientIn);
                System.out.println("Got request");
                Message response = doOperation(gotCommand);
                System.out.println("Done");
                System.out.println("Sending reply.");
                sendReply(clientOut, response);
                clientSocket.close();
                System.out.println("Closing socket.. Bye");
                
                
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ACServer.class.getName()).log(Level.SEVERE, null, ex);
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
                ac.turnOn();
                m = new Message(Message.OK, ac.getDeviceName() + " is now ON");
                System.out.println("Operation completed. Turn on");
                call();
                break;
            case IoTAction.TURN_OFF:
                ac.turnOff();
                m = new Message(Message.OK, ac.getDeviceName() + " is now OFF");
                System.out.println("Operation completed. Turn off");
                call();
                break;
            case IoTAction.SET_DESIRE_TEMPERATURE:
                ac.setDesiredTemp(iotaction.getParam());
                m = new Message(Message.OK, ac.getDeviceName() + " Set temperature OK");
                System.out.println("Operation completed. Set temperature");
                call();
                break;
            case IoTAction.REPORT:
                String s = ac.report().getMessage();
                System.out.println("Operation completed. Report");
                m = new Message(Message.OK, s);
                break;
            default:
                m = new Message(Message.ERROR, ac.getDeviceName() + " doesn't support this action: " + IoTAction.getActionName(action));
                break;
         
        }
        
        return m;
    }

    private void sendReply(ObjectOutputStream out, Message message) throws IOException {
        System.out.println("Sending reply..");
        out.writeObject(message);
        System.out.println("Sent.");
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
            callback.deviceStateChanged(ac.isDeviceOn());
            callback.temperatureChanged(ac.getDesiredTemp());
        } catch(NullPointerException e){
            
        }
    }

    public void setCallback(OnACValueChange callback) {
        this.callback = callback;
    }
    
    
    
}
