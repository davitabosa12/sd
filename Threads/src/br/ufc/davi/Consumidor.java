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
		int i = 0;
		while(i < 20) {
			if(!dep.retirar()) {

				try {
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				i++;
			}
			try {
				sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 


}
