package br.ufc.davi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote{
	public void acceptClient(Client c) throws RemoteException;
	public void pushMessage(String client, String message) throws RemoteException;
	public String getLastMessage()throws RemoteException;

}
