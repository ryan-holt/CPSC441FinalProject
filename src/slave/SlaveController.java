package slave;

import util.*;
import util.sockethandler.ServerSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlaveController implements MessageListener {
	RulesController rulesController;

	private ServerSocket serverSocket;
	private ExecutorService pool; // TODO Delete multithreading, not needed
	private int PORT = 9001;

	public SlaveController() {
		rulesController = new RulesController();

		try {
			serverSocket = new ServerSocket(PORT);
			pool = Executors.newFixedThreadPool(10);
			System.out.println("Slave server is running");
			System.out.println("********");
		} catch (IOException e) {
			System.err.println("SlaveController: Create a new socket error");
			e.printStackTrace();
		}

		communicateWithClient(); // TODO Move to elsewhere
	}

	public void communicateWithClient() {
		try {
			while (true) {
				ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSocket.accept(), this);
				System.out.println("New Master Client Connected");
				pool.execute(serverSocketHandler);

//				serverSocketHandler.run(); // FIXME remove this multithreading stuff that we don't need
			}
		} catch (IOException e) {
			System.err.println("SlaveController: CommunicateWithClient error");
			e.printStackTrace();
		}
	}
	
	public Message handleMessage(Message msg) {
		Message msgOut = null;
		switch (msg.getAction()) {
			case "requestAssociationRules":
				msgOut = rulesController.calculateAssociationRules((AssociationRuleRequest) msg);
				break;
			case "test": // FIXME delete
				return new Message("slaveControllerTestResponse");
			case "test2":
				return new Message("slaveControllerTestResponse2");
			default:
				msgOut = new Message("terminate");
				System.err.println("Error: Message with action " + msg.getAction() + " is not recognized");
				break;
		}

		return msgOut;
	}


}
