/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotpackage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

/**
 *
 * @author davi
 */
public class LightDevice extends IoTDevice{
 
    
    public LightDevice(String deviceName, String deviceDescription) {
        super(IoTDevice.Light, deviceName, deviceDescription);
    }

    public void setCallback(OnDeviceStateChange lightCallback) {
        this.deviceCallback = lightCallback;
    }
    @Override
    public boolean performAction(IoTAction action, ObjectOutputStream out) throws IOException{
        return super.performAction(action, out);
    }
     /**
     * Informa sobre todos os atributos do objeto de forma serializavel
     * Na ordem: host, port, tipo de equipamento, nome, descricao e status.
     * @return Message com informacoes
     */
    @Override
    public Message report(){
        JsonObject obj = new JsonObject();
        obj.addProperty("host", host);
        obj.addProperty("port", port);
        obj.addProperty("deviceType", deviceType);
        obj.addProperty("deviceName", deviceName);
        obj.addProperty("deviceDescription", deviceDescription);
        obj.addProperty("isDeviceOn", isDeviceOn);
        String resp =  new Gson().toJson(obj);
        System.out.println(resp);
        return new Message(Message.OK, resp);
        
    }
    
}

