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
public class IoTAction implements Serializable {
    
    /*GLOBAL ACTIONS*/
    public static final int TURN_OFF = 0;
    public static final int TURN_ON = 1;
    public static final int TOGGLE = 2;
    public static final int REPORT = 3;
    public static final int SEND_DEVICE_INFO = 7;
    
    
    //AC ACTIONS
    public static final int SET_DESIRE_TEMPERATURE = 4;
    
    //Television Actions
    public static final int SET_CHANNEL = 5;
    public static final int SET_VOLUME = 6;
    
    //Gateway Actions

    public static final int SEND_STATE = 615;
    public static final int DISCONNECT = 21;
    
    //fields
    private int action;
    private int param;
    
    public IoTAction(int action){
        this.action = action;
    }
    
    public IoTAction(int action, int param){
        this.action = action;
        this.param = param;
    }

    public int getAction() {
        return action;
    }

    public int getParam() {
        return param;
    }
    
    public static String getActionName(int actionCode){
        switch(actionCode){
            case IoTAction.TURN_ON:
               return "TURN_ON";
            case IoTAction.TURN_OFF:
               return "TURN_OFF";
            case IoTAction.SET_DESIRE_TEMPERATURE:
                return "SET_DESIRE_TEMPERATURE";
            case IoTAction.REPORT:
                return "REPORT";
            case IoTAction.DISCONNECT:
                return "DISCONNECT";
            case IoTAction.SEND_DEVICE_INFO:
                return "SEND_DEVICE_INFO";
            case IoTAction.SEND_STATE:
                return "SEND_STATE";
            case IoTAction.SET_CHANNEL:
                return "SET_CHANNEL";
            case IoTAction.SET_VOLUME:
                return "SET_VOLUME";
            case IoTAction.TOGGLE:                
                return "TOGGLE";
            default:
                return "UNKNOWN_ACTION";
            
        }
    }
    
    
    
    
    
}
