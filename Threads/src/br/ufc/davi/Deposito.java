package br.ufc.davi;

public class Deposito {
	private int items = 0;
	private final int capacidade = 100;
    
	public int getNumItens(){
		return items;
		
	} 
	
	public boolean retirar() {
		if(items > 0) {
			items=getNumItens() - 1;
			return true;
		}
		else
			return false;
	}	

	public boolean colocar() {
			items=getNumItens() +1;
			return true;
			}

	public static void main(String[] args) throws InterruptedException {
		Deposito dep = new Deposito();
		Produtor p = new Produtor(dep, 50);
		Consumidor c1 = new Consumidor(dep, 150);
		Consumidor c2 = new Consumidor(dep, 100);
		Consumidor c3 = new Consumidor(dep, 150);
		Consumidor c4 = new Consumidor(dep, 100);
		Consumidor c5 = new Consumidor(dep, 150);

		//Startar o produtor
		//...
		System.out.println("Inicio da producao");
		p.start();
		
		
				
		//Startar o consumidor
		//...
		System.out.println("Inicio do consumo");
		c1.start();
		c2.start();
		c3.start();
		c4.start();
		c5.start();
		
		
		
		System.out.println("Execucao do main da classe Deposito terminada");
		c1.join();
		c2.join();
		c3.join();
		c4.join();
		c5.join();
		System.out.println("Fim do consumo. Saldo: " + dep.getNumItens());
		}

}
