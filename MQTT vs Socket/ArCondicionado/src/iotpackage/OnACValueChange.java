/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotpackage;

/**
 *
 * @author davi
 */
public interface OnACValueChange extends OnDeviceStateChange{
    abstract public void temperatureChanged(int newValue);
    
}
