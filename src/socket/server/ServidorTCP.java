package socket.server;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTCP {

	private static final HashMap<String, ClientResource> CONNECTIONS = new HashMap<>();
	private static final ExecutorService pool = Executors.newCachedThreadPool();
	private static AtomicBoolean online = new AtomicBoolean(true);

	public static void main(String... args) {
		try {
			int portaServidor = 7000;

			System.out.println("Server running @ 7000");
			ServerSocket socktServ = new ServerSocket(portaServidor);

			while (online.get()) {
				Future<Optional<ClientResource>> clientFuture = pool
						.submit(ClientResource.of(socktServ.accept()));
				pool.execute(ServidorTCP.connect(clientFuture));
			}

			System.out.println("Server is full");
			System.out.println(" -S- Conexao finalizada...");
			socktServ.close();
		} catch (Exception e) {
			System.out.println(
					" -S- O seguinte problema ocorreu : \n" + e.toString());
		}
	}

	public static Map<String, ClientResource> getConnections() {
		return CONNECTIONS;
	}

	private static Runnable connect(
			Future<Optional<ClientResource>> clientFuture) {
		return () -> {
			try {
				clientFuture.get().ifPresent(ServidorTCP::connect);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error connecting");
			}
		};
	}

	private static void connect(ClientResource client) {
		try {
			System.out.println(client.getNome() + " has connected!");
			CONNECTIONS.put(client.getNome(), client);
			pool.submit(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}