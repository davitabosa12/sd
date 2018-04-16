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
public class Message implements Serializable{
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    String statusCode;
    String message;

    public Message(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
    
}
