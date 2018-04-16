/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotbroker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import iotpackage.Command;
import iotpackage.IoTAction;
import iotpackage.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davi
 */
public class GatewayServer {

    private int port;
    private int nodeServerPort;
    ServerSocket myServer;
    ServerSocket nodeServer;
    Thread serverThread;
    Thread nodeServerThread;

    HashMap<String, ObjectOutputStream> outToDevices;
    HashMap<String, ObjectInputStream> inFromDevices;
    HashMap<String, Socket> deviceSockets;
    HashMap<String, IoTDevice> deviceInfo;

    public GatewayServer(int serverPort, int nodeServerPort, ArrayList<String> hosts, ArrayList<Integer> ports) {
        outToDevices = new HashMap<>();
        inFromDevices = new HashMap<>();
        deviceSockets = new HashMap();
        deviceInfo = new HashMap<>();
        this.port = serverPort;
        this.nodeServerPort = nodeServerPort;

        try {
            myServer = new ServerSocket(serverPort);
            nodeServer = new ServerSocket(this.nodeServerPort);
        } catch (IOException ex) {
            System.err.println("SERVER CANNOT START");
            Logger.getLogger(GatewayServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverStuff();
            }
        });
        nodeServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nodeInfoServerStuff();
                } catch (IOException ex) {
                    Logger.getLogger(GatewayServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        nodeServerThread.setPriority(7);
        findDevices();

    }

    public void start() {
        serverThread.start();
        nodeServerThread.start();
    }

    public void stop() {
        serverThread.interrupt();
        nodeServerThread.interrupt();
    }

    private void findDevices() {

        Discoverer d = new Discoverer(5, 800);
        System.out.println("Discovering objects..");
        ArrayList<JsonObject> objs = d.discover();
        System.out.println("Discover finished.");
        for (JsonObject obj : objs) {
            String discoveredHost = obj.get("host").getAsString();
            int discoveredPort = obj.get("port").getAsInt();
            IoTDevice device = new IoTDevice(0, "", "");
            device.setHost(discoveredHost);
            device.setPort(discoveredPort);

            Message m = sendActionToDevice(new IoTAction(IoTAction.REPORT), device);
            if (m.getStatusCode().equals(Message.OK)) {
                String json = m.getMessage();
                JsonObject received = new Gson().fromJson(json, JsonObject.class);
                String deviceName = received.get("deviceName").getAsString();
                String deviceDesc = received.get("deviceDescription").getAsString();
                //insert device into lists
                //create new device by provided type
                switch (received.get("deviceType").getAsInt()) {
                    case IoTDevice.AC:
                        device = new AC(deviceName, deviceDesc);
                        device.setHost(discoveredHost);
                        device.setPort(discoveredPort);
                        deviceInfo.put(deviceName, device);
                        System.out.println("Put a AC into list");
                        break;
                    case IoTDevice.Light:
                        device = new LightDevice(deviceName, deviceDesc);
                        device.setHost(discoveredHost);
                        device.setPort(discoveredPort);
                        deviceInfo.put(deviceName, device);
                        System.out.println("Put a Light into list");
                        break;
                    case IoTDevice.Television:
                        device = new Television(deviceName, deviceDesc);
                        device.setHost(discoveredHost);
                        device.setPort(discoveredPort);
                        deviceInfo.put(deviceName, device);
                        System.out.println("Put a Television into list");
                        break;
                    case IoTDevice.Sensor:
                        device = new LumSensor(deviceName, deviceDesc);
                        device.setHost(discoveredHost);
                        device.setPort(discoveredPort);
                        deviceInfo.put(deviceName, device);
                        System.out.println("Put a Sensor into list");
                        break;
                    default:
                        break;
                }
            }

        }

    }

    private void serverStuff() {
        System.out.println("Gateway server started");
        while (true) { //Server accept loop
            try {
                System.out.println("Listening..");
                Socket clientSocket = myServer.accept();
                ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());

                System.out.println("Client accepted!");

                Command gotCommand = getRequest(clientIn);
                Message response = doOperation(gotCommand);
                sendReply(clientOut, response);
                System.out.println("Closing socket, bye!");
                clientSocket.close();

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(GatewayServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Servidor dedicado a pegar request dos nos e passar para os clientes
     * (telefone)
     */
    private void nodeInfoServerStuff() throws IOException {
        System.out.println("Node info server started.");
        while (true) {
            final Socket nodeSocket = nodeServer.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectOutputStream nodeOut = null;
                    try {

                        nodeOut = new ObjectOutputStream(nodeSocket.getOutputStream());
                        ObjectInputStream nodeIn = new ObjectInputStream(nodeSocket.getInputStream());
                        System.out.println("Client accepted!");
                        Command gotCommand = getRequest(nodeIn);
                        Message response = doOperation(gotCommand);
                        sendReply(nodeOut, response);
                        System.out.println("Closing socket, bye!");
                        nodeSocket.close();
                        Thread.currentThread().interrupt();

                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(GatewayServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }
    }

    private Command getRequest(ObjectInputStream clientIn) throws IOException, ClassNotFoundException {

        System.out.println("Getting request..");
        //lê comando do cliente..
        Command c = (Command) clientIn.readObject();
        System.out.println("Got request");
        return c;

    }

    private Message doOperation(Command c) throws IOException {
        System.out.println("Doing operation..");
        IoTAction action = c.getAction();
        if (c.getDeviceName().equalsIgnoreCase("GATEWAY")) {

            switch (c.getAction().getAction()) {
                case IoTAction.SEND_STATE:
                    return sendState();
                case IoTAction.SEND_DEVICE_INFO:
                    update(c.getDeviceName());
                    return new Message(Message.OK,"ok");
                default:
                    return new Message(Message.ERROR, "Action not supported for this device: GATEWAY");
            }
        }
        IoTDevice device = deviceInfo.get(c.getDeviceName());
        return sendActionToDevice(action, device);
    }

    private void sendReply(ObjectOutputStream out, Message message) throws IOException {
        System.out.println("Sending reply..");
        out.writeObject(message);
        System.out.println("Sent.");
    }

    private Message sendActionToDevice(IoTAction action, IoTDevice device) {
        String host = device.getHost();
        int devicePort = device.getPort();
        Message m = null;
        try {
            Socket deviceSocket = new Socket(host, devicePort);
            ObjectOutputStream outToTargetDevice = new ObjectOutputStream(deviceSocket.getOutputStream()); // output stream of target device
            ObjectInputStream inFromTargetDevice = new ObjectInputStream(deviceSocket.getInputStream()); // input stream of target device
            Command c = new Command("", action);
            outToTargetDevice.writeObject(c); //sends action to iot device
            System.out.println("Sent action to device: " + device.getDeviceName());
            System.out.println("Waiting for " + device.getDeviceName() + "'s response.");
            m = (Message) inFromTargetDevice.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(GatewayServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.err.println("Device doesn't exist!");
            m = new Message(Message.ERROR, "Device doesn't exist");
        }
        return m;
    }

    public HashMap<String, IoTDevice> getDeviceInfo() {
        return deviceInfo;
    }

    private Message sendState() {
        System.out.println("Doing send state");
        JsonObject obj = new JsonObject();
        Set<String> keys = deviceInfo.keySet();
        for (String key : keys) {
            obj.addProperty(key, key);
        }
        String resp = new Gson().toJson(obj);
        Message m = new Message(Message.OK, resp);
        return m;
    }

    private void update(String deviceName) {
        synchronized (deviceInfo) {
            IoTDevice d = deviceInfo.get(deviceName);

            Message m = sendActionToDevice(new IoTAction(IoTAction.REPORT), d);
            JsonObject obj = new Gson().fromJson(m.getMessage(), JsonObject.class);
            int type = obj.get("deviceType").getAsInt();
            String name = obj.get("deviceName").getAsString();
            String desc = obj.get("deviceDescription").getAsString();
            int temp = obj.get("temperature").getAsInt();
            int channel = obj.get("channel").getAsInt();
            int volume = obj.get("volume").getAsInt();
            boolean isDeviceOn = obj.get("isDeviceOn").getAsBoolean();
            String host = obj.get("host").getAsString();
            int dPort = obj.get("channel").getAsInt();
            int value = obj.get("value").getAsInt();

            switch (type) {
                case IoTDevice.AC:
                    AC ac = new AC(name, desc);
                    ac.setHost(host);
                    ac.setPort(dPort);
                    ac.setDeviceOn(isDeviceOn);
                    ac.setDesiredTemp(temp);

                    deviceInfo.remove(deviceName);
                    deviceInfo.put(deviceName, ac);
                    System.out.println("Updated " + deviceName);
                    break;
                case IoTDevice.Light:
                    LightDevice light = new LightDevice(name, desc);
                    light.setHost(host);
                    light.setPort(dPort);
                    light.setDeviceOn(isDeviceOn);
                    deviceInfo.remove(deviceName);
                    deviceInfo.put(deviceName, light);
                    System.out.println("Updated " + deviceName);
                    break;
                case IoTDevice.Television:
                    Television tv = new Television(name, desc);
                    tv.setHost(host);
                    tv.setPort(dPort);
                    tv.setDeviceOn(isDeviceOn);
                    tv.setChannel(channel);
                    tv.setVolume(volume);
                    deviceInfo.remove(deviceName);
                    deviceInfo.put(deviceName, tv);
                    System.out.println("Updated " + deviceName);
                    break;
                case IoTDevice.Sensor:
                    LumSensor sensor = new LumSensor(name, desc);
                    sensor.setHost(host);
                    sensor.setPort(dPort);
                    sensor.setDeviceOn(isDeviceOn);
                    sensor.setValue(value);
                    deviceInfo.remove(deviceName);
                    deviceInfo.put(deviceName, sensor);
                    System.out.println("Updated " + deviceName);
                    break;
                default:
                    break;
            }

        }
    }

}
