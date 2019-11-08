package Master;

import util.*;
import util.sockethandler.ServerSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for managing the thread pool as well as
 * the ServerCommunicationController. Everytime, a new client connects,
 * this class makes an instance of the ServerCommunicationController
 * for the client in a new thread hi
 */
public class MasterController implements MessageListener {

    /**
     * The socket port used for communication
     */
    private static final int PORT = 9000;

    /**
     * Server socket used for socket communication
     */
    private ServerSocket serverSocket;

    /**
     * Pool of threads used for multithreading
     */
    private ExecutorService pool;

    /**
     * File handler to manage file I/O.
     */
    private FileHandler fileHandler;

    public MasterController() {
        try {
            serverSocket = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(10);
            fileHandler = new FileHandler();
            System.out.println("Server is running");
            printIPInfo();
            System.out.println("********");
        } catch (IOException e) {
            System.out.println("ServerController: Create a new socket error");
            e.printStackTrace();
        }
    }

    /**
     * Continously checks for new clients to add to the thread pool
     */
    private void communicateWithClient() {
        try {
            while (true) {
//                OLD_SocketHandler_again scc = new OLD_SocketHandler_again(serverSocket.accept(), this); // TODO delete
	            ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSocket.accept(), this);

                System.out.println("New Client Connected");

//                pool.execute(scc); // TODO delete
                pool.execute(serverSocketHandler);
            }
        } catch (IOException e) {
            System.out.println("ServerController: CommunicateWithClient error");
            e.printStackTrace();
        }
    }

    /**
     * Prints all the IP address information
     */
    private void printIPInfo() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("You current IP address: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP Print error");
            e.printStackTrace();
        }
    }

    public Message handleMessage(Message msg) {
    	Message msgOut = new Message("");
	    switch (msg.getAction()) {
		    case "requestSurvey":
				msgOut = createSurveyQuestions();
		    	break;
		    case "sendSurveyAnswers":
		    	writeSurveyAnswerToFile((SurveyAnswer) msg);
		    	msgOut.setAction("finishedSurvey");
		    	break;
		    case "calculateCorrelation":
                //TODO Insert code to calculate correlations and remove test case below
                msgOut = getHistoricalCalculationResponse("test.txt");
			    break;
		    case "listHistoricalCalculations":
		        String HCList = fileHandler.getListOfHistoricalCorrelation();
                System.out.println(HCList);
                msgOut = new ListHistoricalCalculationsResponse(HCList);
			    break;
		    case "viewHistoricalCalculation":
		        String filename = ((ViewHistoricalCalculationRequest) msg).getCalculationFilename();
		        msgOut = getHistoricalCalculationResponse(filename);
                break;
		    case "quit":
			    msgOut.setAction("terminate");
		    	break;
	        case "terminate":
				msgOut.setAction("terminate"); // TODO Handle quit (client dies) vs terminate (server dies) commands
			    break;
		    case "test":
		    	msgOut.setAction("masterControllerTestResponse");
		    	break;
		    default:
			    System.err.println("Error, unknown message action " + msg.getAction() + ", terminating");
			    msgOut.setAction("terminate");
			    break;
	    }

	    return msgOut;
    }

    private Message getHistoricalCalculationResponse(String filename) {
        try {
            ArrayList<RulesCorrelation> correlations = fileHandler.getHistoricalCorrelations(filename);
            return new CalculationResponse(correlations);
        } catch (IOException e) {
            e.printStackTrace();
            return new Message("FileReadingError");
        }
    }

    private SurveyQuestions createSurveyQuestions() {
    	return new SurveyQuestions();
    }

    private void writeSurveyAnswerToFile(SurveyAnswer surveyAnswer) {
	    try {
		    fileHandler.writeArrayToFile(surveyAnswer.getAnswer());
	    } catch (IOException e) {
		    System.err.println("Error: FileHandler failed to write survey answers to file");
	    	e.printStackTrace();
	    }
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }
}