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
		server.pushMessage(this, s);
		
	}
	@Override
	public void update() throws RemoteException {
		String message  = server.getLastMessage();
		System.out.println(message);
		
	}
	@Override
	public String getNickname() throws RemoteException {
		
		return name;
	}

	@Override
	public boolean checkConnection() throws RemoteException {
		return true;
		
	}
	public static void main(String[] args){
		Scanner teclado = new Scanner(System.in);
		String serverHost = "localhost";
		
		System.out.print("Nickname: ");
		String nick = teclado.next();
		
		try{
			Registry reg = LocateRegistry.getRegistry(serverHost);
			Server s = (Server) reg.lookup("MessageService");
			
			if(s != null){
				ClientImpl cliente = new ClientImpl(nick, s);
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						String result;
						try {
							result = s.removeClient(cliente) + "";
							System.out.println(result);

						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}));
				
				s.acceptClient(cliente);
				new Thread(new Runnable(){ //thread de input.
					@Override
					public void run() {
						while(true){
							String input = teclado.nextLine();
							if(!input.isEmpty()){
								if(input.equals("/quit") || input.equals("/q")) {
								System.exit(0);
								}
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
