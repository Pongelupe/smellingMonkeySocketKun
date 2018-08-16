package socket.server;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;

import socket.server.protocol.ProtocolType;

public class ClientResource implements Callable<Void> {

	private final Socket socketConnection;
	private String nome;
	private Scanner inputStream;
	private PrintStream outputStream;

	private ClientResource(Socket socketConnection, String nome, Scanner inputStream, PrintStream outputStream) {
		this.socketConnection = socketConnection;
		this.nome = nome;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public static Callable<Optional<ClientResource>> of(Socket socketConnection) {
		return () -> {
			try {
				PrintStream serverOutput = new PrintStream(socketConnection.getOutputStream());
				Scanner serverInput = new Scanner(socketConnection.getInputStream());

				serverOutput.println("who is there?");
				String msgIn = serverInput.nextLine();

				String[] mesage = msgIn.split(" ");
				if (mesage[0].equals(ProtocolType.HELLO.toString())) {

					ClientResource client = new ClientResource(socketConnection, mesage[1], serverInput, serverOutput);

					serverOutput.println(client.getNome() + " is connected! Welcome!");
					serverOutput.flush();

					return Optional.of(client);
				} else {
					serverOutput.println("User not authenticated! HELLO expected! Connection closed");
					socketConnection.close();
					serverInput.close();
					return Optional.empty();
				}
			} catch (Exception e) {
				return Optional.empty();
			}
		};
	}

	public Socket getSocketConnection() {
		return socketConnection;
	}

	public String getNome() {
		return nome;
	}

	public Scanner getInputStream() {
		return inputStream;
	}

	public PrintStream getOutputStream() {
		return outputStream;
	}

	@Override
	public Void call() throws Exception {
		while (this.inputStream.hasNextLine()) {
			try {
				String line = this.inputStream.nextLine();
				System.out.println(this.nome + ": " + line);

				String command = line.split(" ")[0];
				Boolean sucess = ProtocolType.valueOf(command).apply(nome, line.substring(command.length()).trim());

				this.outputStream.println(sucess ? "OK" : "Not good");
			} catch (Exception e) {
				this.outputStream.println("Command not fond");
			}
		}
		return null;
	}

}
