package br.ufc.davi;

public class Consumidor extends Thread{

	int time;
	Deposito dep;
	public Consumidor(Deposito dep, int i) {
		this.time = i;
		this.dep = dep;
	}
	@Override
	public void run() {
		for(int i = 0; i < 20; i ++ ) {
			dep.retirar();
			try {
				sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 

	
}
