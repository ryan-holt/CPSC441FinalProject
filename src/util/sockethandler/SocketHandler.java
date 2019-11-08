package util.sockethandler;

import util.Message;
import util.MessageListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

abstract class SocketHandler implements Runnable {
	protected Socket socket;

	/**
	 * The output socket used for sending messages to the socket
	 */
	protected ObjectOutputStream socketOut;

	/**
	 * The input socket used for receiving messages from the socket
	 */
	protected ObjectInputStream socketIn;


	protected MessageListener listener;

	protected boolean shouldRun;

	public SocketHandler(Socket socket, MessageListener listener) {
		try {
			this.socket = socket;
			socketOut = new ObjectOutputStream(socket.getOutputStream());
			socketIn = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("ServerSocketHandler: Create socketOut/socketIn failed");
			e.printStackTrace();
		}

		this.listener = listener;
		shouldRun = true;
	}


	protected Message notifyListener(Message msg) {
		return listener.handleMessage(msg);
	}

	@Override
	public void run() {
//        createInputStream();
		communicate();
	}

	public abstract void communicate();

	/**
	 * Creates an input socket stream from server
	 */
//	public void createInputStream() {
//		try {
//			socketIn = new ObjectInputStream(socket.getInputStream());
//		} catch (IOException e) {
//			System.out.println("Error creating server output stream");
//			e.printStackTrace();
//		}
//	}
	public void printIPInfo() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			System.out.println("You current IP address: " + ip);
		} catch (UnknownHostException e) {
			System.out.println("IP Print error");
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			shouldRun = false;
			socket.close();
			socketIn.close();
			socketOut.close();
		} catch (IOException e) {
			System.err.println("Error: Failed to stop SocketHandler");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected Message readMessage() throws IOException, ClassNotFoundException {
		return (Message) socketIn.readObject();
	}

	protected void writeMessage(Message msg) throws IOException {
		writeObject(msg);
	}

	private void writeObject(Object obj) throws IOException {
		socketOut.writeObject(obj);
		socketOut.reset();
	}
}
