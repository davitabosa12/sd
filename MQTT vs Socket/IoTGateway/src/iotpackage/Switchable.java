/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotpackage;

/**
 *
 * @author davi
 * Interface que permite equipamentos ligarem e desligarem
 */
public interface Switchable {
    abstract void turnOn();
    abstract void turnOff();
    abstract boolean toggle();
    abstract boolean isDeviceOn();
    
}

