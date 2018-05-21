import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class PImpl extends UnicastRemoteObject implements P {

	String pid;
	String leaderPid;
	HashMap<String, P> nodes;
	Registry reg;
	boolean isElecting = false;
	private boolean ok = false;

	public PImpl() throws RemoteException {
		pid = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println("Este é o Nó ");
		nodes = new HashMap<String, P>();
		try {
			System.out.println("Registrando..");
			try {
				register();
			} catch (AlreadyBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Dormir por 20 segundos..");
			Thread.sleep(20000); // sleep for a bit
			System.out.println("Descobrindo nós..");
			nodes = discover();
			int random = new Random().nextInt(30000);
			System.out.println("Dormir por " + (30000 + random) / 1000 + " segundos..");
			Thread.sleep(30000 + random);
			startElection(pid);

		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HashMap<String, P> discover() throws AccessException, RemoteException, NotBoundException {
		HashMap<String, P> ret = new HashMap<String, P>();
		String[] nameList = reg.list();
		for (String name : nameList) {
			P node = (P) reg.lookup(name);
			ret.put(name, node);
		}
		return ret;

	}

	private void register() throws RemoteException, AlreadyBoundException { // registra o pid no Register
		try {
			reg = LocateRegistry.createRegistry(9001);

		} catch (Exception e) {
			System.err.println("ha registro..");
			reg = LocateRegistry.getRegistry(9001);
		}

		reg.rebind(pid, this);

	}

	@Override
	public void startElection(String challengerPid) throws RemoteException {
		ok = false;
		if (!challengerPid.equals(pid))
			// verificar quem mandou eh menor que este no
			if (challengerPid.compareToIgnoreCase(pid) < 0) {
				P challengerNode;
				try {
					challengerNode = (P) reg.lookup(challengerPid);
					System.out.println("Enviando OK para " + challengerPid);
					challengerNode.ok();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// faca nada
				return;
			}

		System.out.println(pid + " iniciou Eleicao");
		for (String nodePid : nodes.keySet()) {
			if (nodePid.compareToIgnoreCase(pid) > 0) {// o PID do node e' maior do que o do obj
				System.out.println("Chamando " + nodePid);
				nodes.get(nodePid).startElection(pid);
			}

		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!ok) {
			for(String nodePid : nodes.keySet()) {
				nodes.get(nodePid).setLeader(pid);
			}
			setLeader(pid);
			System.out.println("Fim " + leaderPid);
		}
		

	}

	@Override
	public void setLeader(String nodePid) throws RemoteException {
		leaderPid = nodePid;
		System.out.println("Nosso coordenador é: " + leaderPid);

	}

	public void ok() throws RemoteException {
		// parar a thread de aguardo de OK..

		ok = true;
	}

	public static void main(String[] args) throws UnknownHostException {
		// PImpl pimpl = new PImpl();

	}

}
