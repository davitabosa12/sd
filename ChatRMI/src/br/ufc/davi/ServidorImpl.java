package br.ufc.davi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class ServidorImpl implements Server {

	private Vector<String> messages = null;
	private Vector<Remote> clients = null;
	public ServidorImpl(){
		messages = new Vector<String>();
		clients = new Vector<Remote>();
	}


	public void notifyClients() {
		for(Remote r : clients){
			try {
				Client c = (Client) r;
				c.update();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				clients.remove(r);
			}
		}

	}

	@Override
	public void pushMessage(String client, String message) throws RemoteException {
		System.out.println("Received message: " + message + client);
		
		messages.add(client +  ": " + message);
		notifyClients();

	}
	@Override
	public String getLastMessage() throws RemoteException {
		return messages.lastElement();
	}
	
	public static void main(String[] args){
		ServidorImpl myServer = new ServidorImpl();
		try {
			Server stub = (Server) UnicastRemoteObject.exportObject(myServer,0);
			Registry reg=null;
			 try {
				 System.out.println("Creating registry...");
				 reg = LocateRegistry.createRegistry(1099);
				 System.out.println("Created.");
			 } catch(Exception e){ try {
				 reg = LocateRegistry.getRegistry(1099);
				 System.out.println("Got registry.");
			 } catch(Exception ee){ System.exit(0); } }
			 	reg.rebind("MessageService", stub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void acceptClient(Remote remote) throws RemoteException {
		System.out.println("Remote accepted");
		clients.add(remote);
		
	}
	

}
