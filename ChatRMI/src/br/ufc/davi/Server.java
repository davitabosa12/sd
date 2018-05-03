package br.ufc.davi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteStub;

public interface Server extends Remote{
	public void acceptClient(Remote remote) throws RemoteException;
	public void pushMessage(Remote client, String message) throws RemoteException;
	public String getLastMessage()throws RemoteException;
	public boolean removeClient(Remote theclient) throws RemoteException;

}
