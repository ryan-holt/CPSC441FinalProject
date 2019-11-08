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
import java.util.concurrent.*;

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
	 * A list of future objects used to ensure full completion
	 */
	private Map<ClientSocketHandler, Future> clientFutures;

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
		clientFutures = new HashMap<>();
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
				//TODO Uncomment when we get correlations from calculateCorrelations -- this function works already =)
				//fileHandler.writeCorrelationsToFile(correlations);
				//TODO DUMMY OUTPUT to be deleted and replaced
				msgOut = getHistoricalCalculationResponse("test.txt");
			    break;
		    case "associationRulesResponse":
//		    	addRuleResponseAndSendNext((AssociationRuleResponse) msg); // TODO clean out and delete, bypass error message - handle with futures instead
		    	break;
		    case "ruleCorrelationResponse":
		    	// TODO clean out and delete, bypass error message
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

	/**
	 * Prepares the association rule request package to send to slave
	 * @throws IOException
	 */
	public ArrayList<AssociationRuleRequest> prepareAssociationRuleRequests() {
		ArrayList<SurveyEntry> entries = null;
		try {
			entries = fileHandler.ReadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<Integer,ArrayList<SurveyEntry>> entriesByQuestion = orderEntriesByQuestion(entries);
		HashMap<Integer,ArrayList<KeywordGroup>> keywordsByQuestion = getKeywordGroupsByQuestion();
		return createAssociationRuleRequests(entriesByQuestion, keywordsByQuestion);
	}

	/**
	 * Order the survey entries by question into a hashmap
	 * @param entries the arraylist that holds the entry information
	 * @return hashmap with all the ordered information
	 */
	HashMap<Integer,ArrayList<SurveyEntry>> orderEntriesByQuestion(ArrayList<SurveyEntry> entries) {
		HashMap<Integer,ArrayList<SurveyEntry>> orderedEntries = new HashMap<>();
		for(int i = 0; i < entries.size(); i++) {
			if(!orderedEntries.containsKey(entries.get(i).getQuestion())) {
				orderedEntries.put(entries.get(i).getQuestion(),new ArrayList<SurveyEntry>());
			}
			orderedEntries.get(entries.get(i).getQuestion()).add(entries.get(i));
		}
		System.out.println("HashMap orderedEntriesByQuestion: " + orderedEntries.keySet());
		return orderedEntries;
	}

	/**
	 * Gets the keyword groups per question, and sorts in a hash map
	 * @return Hashmap that has all the keyword groups ordered by question
	 */
	HashMap<Integer,ArrayList<KeywordGroup>> getKeywordGroupsByQuestion() {
		HashMap<Integer,ArrayList<KeywordGroup>> keywordGroups = new HashMap<>();
		SurveyQuestions surveyQuestions = new SurveyQuestions();
		for(int i = 0; i < surveyQuestions.getSurveyAnswersLists().size(); i++) {
			for(int j = 0; j < surveyQuestions.getSurveyAnswersLists().get(i).size(); j++) {
				for(int k = j+1; k < surveyQuestions.getSurveyAnswersLists().get(i).size(); k++) {
					if (!keywordGroups.containsKey(i + 1)) {
						keywordGroups.put(i + 1, new ArrayList<KeywordGroup>());
					}
					ArrayList<String> tempCombo = new ArrayList<>();
					tempCombo.add(surveyQuestions.getSurveyAnswersLists().get(i).get(j));
					tempCombo.add(surveyQuestions.getSurveyAnswersLists().get(i).get(k));
					KeywordGroup tempGroup = new KeywordGroup(tempCombo);
					keywordGroups.get(i + 1).add(tempGroup);
				}
			}
		}
		System.out.println("HashMap keywordGroups: " + keywordGroups.keySet());
		return keywordGroups;
	}

	/**
	 * Creates association rule request objects to be sent to the slave for calculations
	 * @param entriesByQuestion object that holds survey entries ordered by question
	 * @param keywordsByQuestion object that holds keyword combinations ordered by question
	 * @return the association rule request object
	 */
	ArrayList<AssociationRuleRequest> createAssociationRuleRequests(HashMap<Integer,ArrayList<SurveyEntry>> entriesByQuestion, HashMap<Integer,ArrayList<KeywordGroup>> keywordsByQuestion) {
		ArrayList<AssociationRuleRequest> associationRulePackage = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			associationRulePackage.add(new AssociationRuleRequest(i+1,keywordsByQuestion.get(i+1), entriesByQuestion.get(i+1)));
		}
		System.out.println("ArrayList associationRulePackage: " + associationRulePackage);
		return associationRulePackage;
	}

    // TODO Assign return type message
    private synchronized void calculateCorrelation() {
	    List<AssociationRuleRequest> requests = prepareAssociationRuleRequests();

	    if (latch.getCount() != threadCount) {
		    System.err.println("Error, expected latch to have count " + threadCount + " but count was only" + latch.getCount());
		    System.exit(-1);
	    }

	    // Clean off old instances before starting anything.
	    rulesController.clearRuleResponses();
	    rulesController.clearCorrelationResponses();

	    rulesController.setRuleRequests(requests);

	    // Send first tasks to slaves
		rulesController.batchStartRuleRequests(threadCount, clientSocketHandlers);

		// Record slave tasks as futures
	    for (ClientSocketHandler clientSocketHandler : clientSocketHandlers.values()) {
	    	clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));
	    }

	    // Wait for slaves and automatically kick off more rule requests until list is exhausted
	    waitForSlaveRuleExecution();

	    // Wait to truly exhaust list
	    try {
		    latch.await();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

	    clientFutures.clear();
	    // Custom reset function to make latch re-usable
	    latch.reset();

	    // Creates and populates the correlation requests
	    rulesController.createRuleCorrelationRequests();

	    rulesController.batchStartCorrelationRequests(threadCount, clientSocketHandlers);

	    for (ClientSocketHandler clientSocketHandler : clientSocketHandlers.values()) {
		    clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));
	    }

	    waitForSlaveCorrelationExecution();

	    // Wait to truly exhaust list
	    try {
		    latch.await();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

	    List<RulesCorrelation> topCorrelations = rulesController.getTopCorrelations();
	    System.out.println("!!! Done calculating correlations"); // FIXME delete
    }

    private synchronized void waitForSlaveRuleExecution() {
	    boolean isWaiting = true;
	    Iterator<Map.Entry<ClientSocketHandler, Future>> iterator;
	    while (isWaiting) {
		    isWaiting = false;

		    iterator = clientFutures.entrySet().iterator();
		    while (iterator.hasNext()) {
			    Map.Entry<ClientSocketHandler, Future> entry = iterator.next();
			    ClientSocketHandler clientSocketHandler = entry.getKey();
			    Future future = entry.getValue();

			    try {
				    future.get(100, TimeUnit.MILLISECONDS);
			    } catch (InterruptedException e) {
				    e.printStackTrace();
			    } catch (ExecutionException e) {
				    e.printStackTrace();
			    } catch (TimeoutException e) {
				    isWaiting = true;
				    continue; // Not ready yet, go back to waiting...
			    }

			    iterator.remove(); // Done with this entry, remove

			    // Recursive call
			    addRuleResponseAndSendNext(clientSocketHandler, (AssociationRuleResponse) clientSocketHandler.getLastMsgIn());

		    }
	    }
    }

	private synchronized void waitForSlaveCorrelationExecution() {
		boolean isWaiting = true;
		Iterator<Map.Entry<ClientSocketHandler, Future>> iterator;
		while (isWaiting) {
			isWaiting = false;

			iterator = clientFutures.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<ClientSocketHandler, Future> entry = iterator.next();
				ClientSocketHandler clientSocketHandler = entry.getKey();
				Future future = entry.getValue();

				try {
					future.get(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					isWaiting = true;
					continue; // Not ready yet, go back to waiting...
				}

				iterator.remove(); // Done with this entry, remove

				// Recursive call
				addCorrelationResponseAndSendNext(clientSocketHandler, (RuleCorrelationResponse) clientSocketHandler.getLastMsgIn());
			}
		}
	}

	private synchronized void addRuleResponseAndSendNext(ClientSocketHandler clientSocketHandler, AssociationRuleResponse response) {
		clientSocketHandler = rulesController.addRuleResponse(response, clientSocketHandlers);

		// If any other messages need to be sent...
		if (clientSocketHandler != null) {
			System.out.println("!!! clientSocketHandler at " + clientSocketHandler.getServerIp()); // FIXME delete
			clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));

			waitForSlaveRuleExecution();
		} else {
			latchCountDown();
		}
	}

	// TODO Merge with addRuleResponseAndSendNext
    private synchronized void addCorrelationResponseAndSendNext(ClientSocketHandler clientSocketHandler, RuleCorrelationResponse response) {
		clientSocketHandler = rulesController.addCorrelationResponse(response, clientSocketHandlers);

    	// If any other messages need to be sent...
    	if (clientSocketHandler != null) {
		    System.out.println("!!! clientSocketHandler at " + clientSocketHandler.getServerIp()); // FIXME delete
		    clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));

		    waitForSlaveCorrelationExecution();
	    } else {
    		latchCountDown();
	    }
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }
}