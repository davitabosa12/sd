package yamlTeste;

import java.io.FileWriter;
import java.io.IOException;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import yamlTeste.Contato;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Contato davi = new Contato();
		davi.nome = "Davi Tabosa";
		davi.idade = 22;
		
		YamlWriter writer = new YamlWriter(new FileWriter("output.yml"));
		
		writer.write(davi);
		writer.close();
		System.out.println("Fim.");
	}

}
