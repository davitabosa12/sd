/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotpackage;

import java.io.Serializable;

/**
 *
 * @author davi
 */
public class Command implements Serializable{
    String deviceName;
    IoTAction action;

    public Command(String deviceName, IoTAction action) {
        this.deviceName = deviceName;
        this.action = action;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public IoTAction getAction() {
        return action;
    }
    
    
}
