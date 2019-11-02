package util;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class OLD_SocketHandler {
	private ObjectOutputStream objectWriter;
	private ObjectInputStream objectReader;

	public OLD_SocketHandler(Socket socket) {
		try {
			//Create the IO object streams. objectWriter MUST come before objectReader
			objectWriter = new ObjectOutputStream(socket.getOutputStream());
			objectReader = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Error: Could not create OLD_SocketHandler");
			e.printStackTrace();
		}
	}

	public Message read() {
		Message msg = null;

		try {
			msg = (Message) objectReader.readObject();
		} catch (SocketException e) {
			//e.printStackTrace();
			System.out.println("SocketException: Connection must have reset. Bye!");
		} catch (EOFException e) {
			//Ignore. Be quiet my child
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Could not read message from socket");
			e.printStackTrace();
		}

		return msg;
	}

	public void write(Message msg) {
		try {
			objectWriter.writeObject(msg);
		} catch (IOException e) {
			System.err.println("Error: OLD_SocketHandler failed to write to socket");
			e.printStackTrace();
		}
	}
}
