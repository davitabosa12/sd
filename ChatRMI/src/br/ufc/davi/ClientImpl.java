package br.ufc.davi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Scanner;

public class ClientImpl extends UnicastRemoteObject implements Client{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	Server server;
	public ClientImpl(String name, Server server) throws RemoteException{
		super();
		this.name = name;
		this.server = server;
	}
	
	public void say(String s) throws RemoteException {
		server.pushMessage(name, s);
		
	}
	@Override
	public void update() throws RemoteException {
		String message  = server.getLastMessage();
		System.out.println(message);
		
	}

	@Override
	public boolean checkConnection() throws RemoteException {
		return true;
		
	}
	
	public static void main(String[] args){
		Scanner teclado = new Scanner(System.in);
		String serverHost = "localhost";
		
		try{
			Registry reg = LocateRegistry.getRegistry(serverHost);
			Server s = (Server) reg.lookup("MessageService");
			
			if(s != null){
				ClientImpl cliente = new ClientImpl(new Random().nextInt() + "", s);
				
				s.acceptClient(cliente);
				new Thread(new Runnable(){ //thread de input.
					@Override
					public void run() {
						while(true){
							String input = teclado.nextLine();
							if(!input.isEmpty()){
								try {
									cliente.say(input);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}).start();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
