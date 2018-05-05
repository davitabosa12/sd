package br.ufc.davi;

public class Produtor extends Thread{

	int time;
	Deposito dep;
	public Produtor(Deposito dep, int i) {
		this.time = i;
		this.dep = dep;
	}
	@Override
	public void run() {
	for(int i = 0; i < 100; i ++ ) {
		dep.colocar();
		try {
			sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	}

}
