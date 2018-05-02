package br.ufc.davi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
	public boolean checkConnection() throws RemoteException;
	public void update() throws RemoteException;
	
}
