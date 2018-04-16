/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotpackage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 *
 * @author davi
 */
public class LumSensor extends IoTDevice{
    
    private int value;
    private OnSensorValueChange sensorCallback;
    public LumSensor(String deviceName, String deviceDescription) {
        super(IoTDevice.Sensor, deviceName, deviceDescription);
        this.value = 50;
        sensorCallback = new OnSensorValueChange() {
            @Override
            public void valueChanged(int newValue) {
                
            }

            @Override
            public void deviceStateChanged(boolean newValue) {
                
            }
        };
        
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        sensorCallback.valueChanged(value);
        System.out.println(value);
    }

    public void setSensorCallback(OnSensorValueChange sensorCallback) {
        this.sensorCallback = sensorCallback;
    }
    
    @Override
    public Message report(){
        Gson g = new Gson();
        JsonObject params = new JsonObject();
        params.addProperty("host", host);
        params.addProperty("port", port);
        params.addProperty("deviceType", deviceType);
        params.addProperty("deviceName", deviceName);
        params.addProperty("deviceDescription", deviceDescription);
        params.addProperty("isDeviceOn", isDeviceOn);
        params.addProperty("value", value);
     
        
         String resp =  g.toJson(params);
         System.out.println(resp);
         return new Message(Message.OK,resp);
    }
    
    
}
