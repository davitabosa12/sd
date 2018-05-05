package br.ufc.davi;

public class Racer extends Thread{

	private int id;
	public Racer(int id) {
		this.id = id;
		
	}
	
	@Override
	public void run() {
		for(int i = 0; i < 1000; i++) {
			try {
				sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Racer - " + id + " imprimindo");
		}
	}
}
