package socket.server.protocol;

import java.util.Map.Entry;
import java.util.function.BiFunction;

import socket.server.ClientResource;
import socket.server.ServidorTCP;

public enum ProtocolType {
	HELLO(null), ALL((who, params) -> {
		ServidorTCP.getConnections().entrySet().stream().filter(user -> !who.equals(user.getKey()))
				.map(Entry<String, ClientResource>::getValue)
				.forEach(user -> user.getOutputStream().println(who + " -> to all: " + params));
		return true;
	}), DIRECT((who, params) -> {
		String to = params.split(" ")[0];
		ServidorTCP.getConnections().entrySet().stream().filter(user -> to.equals(user.getKey())).findFirst()
				.ifPresent(user -> user.getValue().getOutputStream()
						.println(who + " -> direct to you: " + params.substring(to.length() + 1)));

		return true;
	}), LIST((who, params) -> {
		ClientResource resource = ServidorTCP.getConnections().get(who);
		resource.getOutputStream().println("Online users:");
		ServidorTCP.getConnections().entrySet().stream().filter(user -> !who.equals(user.getKey()))
				.map(Entry<String, ClientResource>::getValue)
				.forEach(user -> resource.getOutputStream().println(user.getNome()));
		return true;
	}), BYE((who, params) -> {
		System.out.println(who + " has disconnected");
		return ServidorTCP.getConnections().remove(who) != null;
	}), LIST((who,params) -> {
		String resposta = null;
		for(ClientResource c -> ServidorTCP.getConnections()){
			resposta = resposta + c.getNome() + "\n";
		}
		ServidorTCP.getConnections().entrySet().stream().filter(user -> who.equals(user.getKey())).findFirst()
				.ifPresent(user -> user.getValue().getOutputStream()
						.println(resposta));
		return true;
	});

	/**
	 * 1 - Who sent 2 - command itself
	 */
	private BiFunction<String, String, Boolean> command;

	ProtocolType(BiFunction<String, String, Boolean> command) {
		this.command = command;
	}

	public Boolean apply(String who, String command) {
		return this.command.apply(who, command);
	}

}
