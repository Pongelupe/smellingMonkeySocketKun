package socket.client;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTCP {

	private static Socket socket;

	public static void main(String args[]) {
		try {
			String iPServidor = "127.0.0.1";
			int portaServidor = 7000;

			socket = new Socket(iPServidor, portaServidor);
			System.out.println("****** Envie comandos ao servidor *******");

			Thread sendThread = prepareSender();
			sendThread.join();
			sendThread.start();
			prepareReciver().start();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					" -C- O seguinte problema ocorreu : \n" + e.toString());
		}
	}

	private static Thread prepareSender() {
		return new Thread(() -> {
			try {
				PrintStream out = new PrintStream(socket.getOutputStream());
				Scanner keyboard = new Scanner(System.in);

				while (keyboard.hasNextLine()) {
					String line = keyboard.nextLine();
					if (line.equals("bye"))
						break;
					out.println(line);
				}

				keyboard.close();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static Thread prepareReciver() {
		return new Thread(() -> {
			try {
				Scanner scanner = new Scanner(socket.getInputStream());

				while (scanner.hasNextLine())
					System.out.println("SERVER: " + scanner.nextLine());

				scanner.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}