package Master;

import util.*;
import util.sockethandler.ClientSocketHandler;
import util.sockethandler.ServerSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
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
    private static final int SERVER_PORT = 9000;
    private int threadCount;
    private final String[] CLIENT_IPS = {"127.0.0.1"};
    private final int[] CLIENT_PORTS = {9001};

    private RulesController rulesController;

    /**
     * Server socket used for socket communication between the master and the client
     */
    private ServerSocket serverSocket;

    /**
     * Pool of threads used for multithreading
     */
    private ExecutorService pool;
    private ResettableCountDownLatch latch;

    private ServerSocketHandler serverSocketHandler;
    private LinkedHashMap<String, ClientSocketHandler> clientSocketHandlers;

    /**
     * File handler to manage file I/O.
     */
    private FileHandler fileHandler;

    public MasterController() {
        try {
			initializeClientSocketHandlers();

            serverSocket = new ServerSocket(SERVER_PORT);
            threadCount = CLIENT_IPS.length;
            pool = Executors.newFixedThreadPool(5);
            latch = new ResettableCountDownLatch(threadCount);
			fileHandler = new FileHandler();
            System.out.println("Server is running");
            printIPInfo();
            System.out.println("********");
        } catch (IOException e) {
            System.out.println("ServerController: Create a new socket error");
            e.printStackTrace();
        }

        rulesController = new RulesController(this);
    }

	/**
	 * Create new client sockets to connect to slave computers in the cluster
	 * @throws IOException
	 */
	private void initializeClientSocketHandlers() throws IOException {
		clientSocketHandlers = new LinkedHashMap<>();
	    for (int i = 0; i < CLIENT_IPS.length; i++) {
	    	String clientIP = CLIENT_IPS[i];
	    	int clientPort = CLIENT_PORTS[i];
		    clientSocketHandlers.put(clientIP, new ClientSocketHandler(new Socket(clientIP, clientPort), this));
	    }
    }

    /**
     * Continuously checks for new clients to add to the thread pool
     */
    private void communicateWithClient() {
        try {
            while (true) {
	            ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSocket.accept(), this);

                System.out.println("New Client Connected");

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
            System.out.println("Your current IP address: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP Print error");
            e.printStackTrace();
        }
    }

    private void latchCountDown() {
    	latch.countDown();
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
				calculateCorrelation();
				//TODO DUMMY OUTPUT to be deleted and replaced
				msgOut = getHistoricalCalculationResponse("test.txt");
			    break;
			case "associationRulesResponse":
				addRuleResponseAndSendNext((AssociationRuleResponse) msg);
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

    // TODO Assign return type message
    private synchronized void calculateCorrelation() {
    	// TODO get the associationRulesRequests
	    ArrayList<KeywordGroup> testKeywordGroup = new ArrayList<KeywordGroup>(Arrays.asList(
			    new KeywordGroup("python", "java"),
			    new KeywordGroup("c++", "python"),
			    new KeywordGroup("java", "c++")
	    ));

	    ArrayList<SurveyEntry> testEntries = new ArrayList<SurveyEntry>(Arrays.asList(
			    new SurveyEntry("Jim", 1, "python", "java", "c++"),
			    new SurveyEntry("Bob", 1, "python", "java"),
			    new SurveyEntry("Frank", 1, "python", "c++")
	    ));

	    AssociationRuleRequest testRequest = new AssociationRuleRequest(1, testKeywordGroup, testEntries);

	    List<AssociationRuleRequest> requests = new ArrayList<>();
	    requests.add(testRequest); // FIXME delete
//	    requests.add(new AssociationRuleRequest(2, testKeywordGroup, testEntries)); // TODO Fix multithreading issues

	    if (latch.getCount() != threadCount) {
		    System.err.println("Error, expected latch to have count " + threadCount + " but count was only" + latch.getCount());
		    System.exit(-1);
	    }
	    System.out.println("!!! latch is at: " + latch.getCount()); // FIXME delete

	    rulesController.setRuleRequests(requests);

		rulesController.batchStartRuleRequests(threadCount, clientSocketHandlers);

	    for (ClientSocketHandler clientSocketHandler : clientSocketHandlers.values()) {
	    	pool.execute(clientSocketHandler);
	    }

	    try {
		    latch.await();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }


	    System.out.println("!!! latch is no longer waiting!"); // FIXME delete
	    rulesController.createRuleCorrelationRequests();
    }

    private synchronized void addRuleResponseAndSendNext(AssociationRuleResponse response) {
    	ClientSocketHandler nextSocketHandler = rulesController.addRuleResponse(response, clientSocketHandlers);

    	// If any other messages need to be sent...
    	if (nextSocketHandler != null) {
		    System.out.println("!!! clientSocketHandler at " + nextSocketHandler.getServerIp());
		    pool.execute(nextSocketHandler);
	    } else {
    		latchCountDown();
		    System.out.println("!!! latch decremented to " + latch.getCount()); // FIXME delete
	    }
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }
}