package yamlTeste;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) throws IOException {
		Filme filme = new Filme();
		filme.ano = 1998;
		filme.Avaliacao_IMDB = 10;
		filme.linkparaTraillerNoYoutube = new URL("http://youtube.com");
		filme.titulo = "Filme";
		filme.tres_filmes_mais_relacionados = new ArrayList<Filme>();
		
		String json = new Gson().toJson(filme);
		System.out.println(json);
		
		Socket envia = new Socket("localhost", 8001);
		BufferedOutputStream out = new BufferedOutputStream( envia.getOutputStream());
		BufferedInputStream in = new BufferedInputStream( envia.getInputStream());
		System.out.println(new String(json.getBytes()));
		byte[] b = json.getBytes();
		out.write(b);
		out.flush();
		System.out.println("Enviado");
		
	}

}
