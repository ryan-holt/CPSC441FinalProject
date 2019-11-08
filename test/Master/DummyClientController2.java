package Master;

import util.*;
import util.sockethandler.ClientSocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This version (compared to DummyClient) toys around with multithreading with sockets
 */
public class DummyClientController2 implements MessageListener {

	/**
	 * Pool of threads used for multithreading
	 */
	private ExecutorService pool;
	private ResettableCountDownLatch latch;

	private Socket socket;

    /**
     * BufferedReader to read in user input
     */
    BufferedReader inFromUser;

    private ClientSocketHandler clientSocketHandler;

    /**
     * Constructs a Client controller object
     *
     * @param serverName name of server
     * @param portNumber port number
     */
    public DummyClientController2(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);


            inFromUser = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * runs the client side
     *
     * @param args command line arguments
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DummyClientController2 cc = new DummyClientController2("localhost", 9001);
        cc.communicateWithServer();
    }

	public Message handleMessage(Message msg) {

		System.out.println("Dummy2: received from server " + msg.getAction());


		switch (msg.getAction()) {
			case "slaveControllerTestResponse":
				latch.countDown();
				System.out.println("Latch has decremented to " + latch.getCount());
				break;
			case "slaveControllerTestResponse2":
				System.out.println("Not decrementing latch, sending test instead");
				clientSocketHandler.setNextMsgOut(new Message("test"));
				pool.execute(clientSocketHandler);
				break;
		}

	    return new Message("test");
	}

    /**
     * Communicates with the server by reading in name, survey questions, and sending out survey answers
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void communicateWithServer() throws IOException, ClassNotFoundException {
	    int threadCount = 1;
    	pool = Executors.newFixedThreadPool(threadCount);
    	latch = new ResettableCountDownLatch(threadCount);

    	clientSocketHandler = new ClientSocketHandler(socket, this);
	    System.out.println("Client Connected to server");

//	    System.out.println("Dummy2: sending test");
//	    clientSocketHandler.setNextMsgOut(new Message("test"));
//	    pool.execute(clientSocketHandler);

	    System.out.println("Dummy2: rush sending test2");
	    clientSocketHandler.setNextMsgOut(new Message("test2"));
	    pool.execute(clientSocketHandler);

	    try {
		    latch.await();
		    latch.reset();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

//	    System.out.println("Dummy2: sending test #2");
//	    clientSocketHandler.setNextMsgOut(new Message("test"));
//	    pool.execute(clientSocketHandler);
//
//	    try {
//		    if (pool.awaitTermination(5, TimeUnit.SECONDS)) {
//			    System.out.println("termination finished");
//		    } else {
//			    System.err.println("Error - dummy2 has expired timer");
//			    pool.shutdownNow();
//		    }
//	    } catch (InterruptedException e) {
//		    System.err.println("Error - dummy client2 has expired timer");
//		    e.printStackTrace();
//	    }

	    System.out.println("Dummy2: Shutting down pool");
	    pool.shutdown();
    }

}