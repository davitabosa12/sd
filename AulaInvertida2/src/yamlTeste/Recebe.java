package yamlTeste;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

public class Recebe {

	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(8001);
		System.out.println("Aguardando rx..");
		Socket client = server.accept();
		System.out.println("ok");
		BufferedOutputStream out = new BufferedOutputStream( client.getOutputStream());
		BufferedInputStream in = new BufferedInputStream( client.getInputStream());
		byte[] buffer = new byte[4096];
		
		
			System.out.println("Reading..");
			in.read(buffer);	
		//System.out.println(new String(buffer));
		String resp = new String(buffer);
		resp = resp.trim();
		//System.out.println(resp);
		Filme recebido = new Gson().fromJson(resp, Filme.class);
		System.out.println(recebido.ano);
		System.out.println(recebido.titulo);
		System.out.println(recebido.Avaliacao_IMDB);

	}

}
