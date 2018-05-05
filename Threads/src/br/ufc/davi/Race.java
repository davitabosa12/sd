package br.ufc.davi;

public class Race {

	public Race() {
		Racer r1 = new Racer(1);		
		Racer r2 = new Racer(2);		
		Racer r3 = new Racer(3);		
		Racer r4 = new Racer(4);		
		Racer r5 = new Racer(5);		
		Racer r6 = new Racer(6);		
		Racer r7 = new Racer(7);
		Racer r8 = new Racer(8);
		Racer r9 = new Racer(9);
		Racer r10 = new Racer(10);
		
		
		
		r2.start();
		r4.start();
		r6.start();
		r8.start();
		r10.start();
		
		try {
			r2.join();
			r4.join();
			r6.join();
			r8.join();
			r10.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		r1.start();
		
		r3.start();
		
		r5.start();
		
		r7.start();
		
		r9.start();
		
		
	}
}
