import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Vector;

public class PImpl implements P {

	String pid;
	Vector<P> nodes;
	Registry reg;
	public PImpl(){
		pid = ManagementFactory.getRuntimeMXBean().getName();
		nodes = new Vector<P>();
		try {
			register();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void register() throws RemoteException{ //registra o nó no Register
		try{
			reg = LocateRegistry.getRegistry(8001);
		}
		catch(Exception e)
		{
			reg = LocateRegistry.createRegistry(8001);
		}
		reg.rebind(pid, this);
		
		
	}
	private void getNodesList(){
		String[] nameList = reg.list();
		for(String name : nameList){
			P node = (P)reg.lookup(name);
		}
	}
	@Override
	public void startElection() throws RemoteException {
		

	}

	@Override
	public void setLeader() throws RemoteException {
		

	}
	
	public static void main(String[] args){
		PImpl pimpl = new PImpl();
		Thread timer = new Thread(new Runnable(){
			@Override
			public void run() {
				int millis = new Random().nextInt(1000) + 30 * 1000;
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
	}

}
