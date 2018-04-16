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

/**
 *
 * @author davi
 */
public class AC extends IoTDevice{

    private int desiredTemp; //The desired temperature
    OnACValueChange ACcallback;

    public AC(String deviceName, String deviceDescription) {
        super(IoTDevice.AC ,deviceName, deviceDescription);
        desiredTemp = 22;
        ACcallback = new OnACValueChange() {
            @Override
            public void temperatureChanged(int newValue) {
                
            }

            @Override
            public void deviceStateChanged(boolean newValue) {
                
            }
        };
        
    }  

    
    public int getDesiredTemp() {
        return desiredTemp;
    }

    public void setDesiredTemp(int desiredTemp) {
        this.desiredTemp = desiredTemp;
        ACcallback.temperatureChanged(desiredTemp);
    }
    
    @Override
    public boolean performAction(IoTAction action, ObjectOutputStream out ) throws IOException {
        int actionCode = action.getAction();
        int params = action.getParam();
        switch(actionCode){
            case IoTAction.SET_DESIRE_TEMPERATURE:{
                setDesiredTemp(params);
                out.writeObject(new Message("SET_DESIRE_TEMPERATURE","Set to " + params));
                return true;
            }
            case IoTAction.REPORT:{
                out.writeObject(report());
                return true;
            }
            default:{
                return super.performAction(action, out);
            }
        }
    }
   
                
    @Override
    /**
     * Informa sobre todos os atributos do objeto de forma serializavel
     * Na ordem: host, port, tipo de equipamento, nome, descricao, status e temperatura.
     * @return Message com informacoes
     */
    public Message report(){
        
       Gson g = new Gson();
        JsonObject params = new JsonObject();
        params.addProperty("host", host);
        params.addProperty("port", port);
        params.addProperty("deviceType", deviceType);
        params.addProperty("deviceName", deviceName);
        params.addProperty("deviceDescription", deviceDescription);
        params.addProperty("isDeviceOn", isDeviceOn);
        params.addProperty("temperature", desiredTemp);
        
         String resp =  g.toJson(params);
         System.out.println(resp);
         return new Message(Message.OK,resp);
    }

    public void setCallback(OnACValueChange callback) {
        this.ACcallback = callback;
        super.deviceCallback = callback;
        
    }
    

}
