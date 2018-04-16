/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker.devices;

import iotpackage.AC;
import iotpackage.Command;
import iotpackage.IoTAction;
import iotpackage.LumSensor;
import iotpackage.Message;
import iotpackage.OnSensorValueChange;
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
public class SensorServer {

    private int serverPort;
    private String host;
    private Thread serverThread;
    private ServerSocket myServer;
    private String sensorName;
    private String sensorDesc;
    private LumSensor sensor;
    private OnSensorValueChange callback;
    private DiscoverService discover;

    public SensorServer(String sensorName, String sensorDesc, int port) {
        try {
            
            this.sensorName = sensorName;
            this.sensorDesc = sensorDesc;
            this.serverPort = port;
            myServer = new ServerSocket(port);
            this.sensor = new LumSensor(sensorName,sensorDesc);
            this.host = InetAddress.getLocalHost().getHostAddress();
            discover = new DiscoverService(host, serverPort);
            discover.start();

            serverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    serverStuff();
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(SensorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LumSensor getSensor() {
        return sensor;
    }

    private void serverStuff() {
        sensor = new LumSensor(sensorName, sensorDesc);
        sensor.setHost(host);
        sensor.setPort(serverPort);
        sensor.report();
        System.out.println("Lum sensor started");
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
                Logger.getLogger(SensorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void start(){
        serverThread.start();
    }
    
    public void stop(){
        serverThread.interrupt();
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
        switch (action) {
            case IoTAction.TURN_ON:
                sensor.turnOn();
                m = new Message(Message.OK, sensor.getDeviceName() + " is now ON");
                System.out.println("Operation completed. Turn on");
                break;
            case IoTAction.TURN_OFF:
                sensor.turnOff();
                m = new Message(Message.OK, sensor.getDeviceName() + " is now OFF");
                System.out.println("Operation completed. Turn off");
                break;
            case IoTAction.REPORT:
                String s = sensor.report().getMessage();
                System.out.println("Operation completed. Report");
                m = new Message(Message.OK, s);
                break;
            default:
                m = new Message(Message.ERROR, sensor.getDeviceName() + " doesn't support this action: " + IoTAction.getActionName(action));
                break;

        }

        return m;
    }

    private void sendReply(ObjectOutputStream out, Message message) throws IOException {
        System.out.println("Sending reply..");
        out.writeObject(message);
        System.out.println("Sent.");
    }
    
    public void sendToGateway(String gatewayHost, int gatewayPort) throws IOException, ClassNotFoundException{
        Socket mySocket = new Socket(gatewayHost,gatewayPort);
        ObjectOutputStream out = new ObjectOutputStream(mySocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(mySocket.getInputStream());
        Command c = new Command("GATEWAY",new IoTAction(IoTAction.SEND_DEVICE_INFO));
        out.writeObject(c);
        Message m = (Message) in.readObject();
        System.out.println(m.getMessage());
    }

}
