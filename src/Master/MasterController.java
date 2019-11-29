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
//    private final String[] CLIENT_IPS = {"127.0.0.1"};
    private ArrayList<String> CLIENT_IPS;
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
    private ArrayList<ClientSocketHandler> clientSocketHandlers;
	/**
	 * A list of future objects used to ensure full completion
	 */
	private Map<ClientSocketHandler, Future> clientFutures;

	/**
	 * File handler to manage file I/O.
	 */
	private FileHandler fileHandler;

	private long startTime;

	private long endTime;

	private long masterPart1StartTime;
	private long masterPart1Time;

	private long masterPart2StartTime;
	private long masterPart2Time;

	private long slavePart1Time;
	private long slavePart2Time;

    public MasterController() {
        try {
			fileHandler = new FileHandler();
			CLIENT_IPS = fileHandler.readIPsFromConfig();

			initializeClientSocketHandlers();

            serverSocket = new ServerSocket(SERVER_PORT);
            threadCount = CLIENT_IPS.size();
            pool = Executors.newFixedThreadPool(threadCount + 3);
            latch = new ResettableCountDownLatch(threadCount);
            System.out.println("Server is running");
//            printIPInfo();
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
		clientSocketHandlers = new ArrayList<>();
		clientFutures = new HashMap<>();
	    for (int i = 0; i < CLIENT_IPS.size(); i++) {
	    	String clientIP = CLIENT_IPS.get(i);
	    	int clientPort = CLIENT_PORTS[i];
		    clientSocketHandlers.add(new ClientSocketHandler(new Socket(clientIP, clientPort), this));
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
				msgOut = calculateCorrelation((CalculationRequest) msg); // Run the really big calculation
			    break;
		    case "associationRulesResponse":
				slavePart1Time += msg.getElapsedTime();
		    	break;
		    case "ruleCorrelationResponse":
				slavePart2Time += msg.getElapsedTime();
		    	break;
		    case "listHistoricalCalculations":
		    	startTime = System.currentTimeMillis();
			    String HCList = fileHandler.getListOfHistoricalCorrelation();
			    System.out.println(HCList);
			    msgOut = new ListHistoricalCalculationsResponse(HCList);
			    msgOut.setElapsedTime(System.currentTimeMillis() - startTime);
			    break;
		    case "viewHistoricalCalculation":
			    startTime = System.currentTimeMillis();
			    String filename = ((ViewHistoricalCalculationRequest) msg).getCalculationFilename();
			    msgOut = getHistoricalCalculationResponse(filename);
			    msgOut.setElapsedTime(System.currentTimeMillis() - startTime);
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
//		endTime = System.currentTimeMillis();
//	    msgOut.setElapsedTime(endTime-startTime);
	    return msgOut;
    }

    private Message getHistoricalCalculationResponse(String filename) {
        try {
            ArrayList<RulesCorrelation> correlations = fileHandler.getHistoricalCorrelations(filename);
            return new CalculationResponse(correlations, masterPart1Time, masterPart2Time, slavePart1Time, slavePart2Time);
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
	public ArrayList<AssociationRuleRequest> prepareAssociationRuleRequests(int keywordGroupSize) {
		ArrayList<SurveyEntry> entries = null;
		try {
			entries = fileHandler.ReadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<Integer,ArrayList<SurveyEntry>> entriesByQuestion = orderEntriesByQuestion(entries);
		HashMap<Integer,ArrayList<KeywordGroup>> keywordsByQuestion = getKeywordGroupsByQuestion(keywordGroupSize);
		return createAssociationRuleRequests(entriesByQuestion, keywordsByQuestion);
	}

	/**
	 * Order the survey entries by question into a hashmap
	 * @param entries the arraylist that holds the entry information
	 * @return hashmap with all the ordered information
	 */
	HashMap<Integer,ArrayList<SurveyEntry>> orderEntriesByQuestion(ArrayList<SurveyEntry> entries) {
		HashMap<Integer,ArrayList<SurveyEntry>> orderedEntries = new HashMap<>();
		for (SurveyEntry entry : entries) {
			if (!orderedEntries.containsKey(entry.getQuestion())) {
				orderedEntries.put(entry.getQuestion(), new ArrayList<SurveyEntry>());
			}
			orderedEntries.get(entry.getQuestion()).add(entry);
		}
		return orderedEntries;
	}

	/**
	 * Gets the keyword groups per question, and sorts in a hash map
	 * @return Hashmap that has all the keyword groups ordered by question
	 */
	HashMap<Integer,ArrayList<KeywordGroup>> getKeywordGroupsByQuestion(int groupSize) {
		HashMap<Integer,ArrayList<KeywordGroup>> keywordGroups = new HashMap<>();
		SurveyQuestions surveyQuestions = new SurveyQuestions();
		for(int i = 0; i < surveyQuestions.getSurveyAnswersLists().size(); i++) {
			ArrayList<String> keywords = surveyQuestions.getSurveyAnswersLists().get(i);
			int questionID = i + 1;
			ArrayList<KeywordGroup> groupPerQuestion = new ArrayList<>();
			calculateKeywordGroups(groupPerQuestion, new ArrayList<>(), keywords, 0, Math.min(groupSize, keywords.size()));
			keywordGroups.put(questionID, groupPerQuestion);
		}
		System.out.println("HashMap keywordGroups: " + keywordGroups.keySet());
		return keywordGroups;
	}

	void calculateKeywordGroups(ArrayList<KeywordGroup> allGroups, ArrayList<String> groupedKeywords, ArrayList<String> allKeywords, int keywordIndex, int groupSize) {
		for (int i = keywordIndex; i < allKeywords.size(); i++) {
			ArrayList<String> keywordsCopy = new ArrayList<>(groupedKeywords);
			keywordsCopy.add(allKeywords.get(i));

			if (keywordsCopy.size() < groupSize) {
				calculateKeywordGroups(allGroups, keywordsCopy, allKeywords, i + 1, groupSize); // RECURSIVE CALL
			} else {
				allGroups.add(new KeywordGroup(keywordsCopy));
			}
		}
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
		return associationRulePackage;
	}

    // TODO Assign return type message
    private synchronized CalculationResponse calculateCorrelation(CalculationRequest request) {
	    startTime = System.currentTimeMillis();

		List<AssociationRuleRequest> requests = prepareAssociationRuleRequests(request.getKeywordGroupSize());

	    clientFutures.clear();
	    // Custom reset function to make latch re-usable
	    latch.reset();

	    if (latch.getCount() != threadCount) {
		    System.err.println("Error, expected latch to have count " + threadCount + " but count was only " + latch.getCount());
		    System.exit(-1);
	    }

	    slavePart1Time = 0;
	    slavePart2Time = 0;

	    // Clean off old instances before starting anything.
	    rulesController.clearRuleResponses();
	    rulesController.clearCorrelationResponses();

	    rulesController.setRuleRequests(requests);

	    // Send first tasks to slaves
		rulesController.batchStartRuleRequests(threadCount, clientSocketHandlers);

	    masterPart1StartTime = System.currentTimeMillis();
		// Record slave tasks as futures
	    for (ClientSocketHandler clientSocketHandler : clientSocketHandlers) {
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
		masterPart1Time = System.currentTimeMillis() - masterPart1StartTime;

	    clientFutures.clear();
	    // Custom reset function to make latch re-usable
	    latch.reset();

	    // Creates and populates the correlation requests
	    rulesController.createRuleCorrelationRequests();

	    rulesController.batchStartCorrelationRequests(threadCount, clientSocketHandlers);

	    masterPart2StartTime = System.currentTimeMillis();
	    for (ClientSocketHandler clientSocketHandler : clientSocketHandlers) {
		    clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));
	    }

	    waitForSlaveCorrelationExecution();

	    // Wait to truly exhaust list
	    try {
		    latch.await();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

	    masterPart2Time = System.currentTimeMillis() - masterPart2StartTime;

	    List<RulesCorrelation> topCorrelations = rulesController.getTopCorrelations();
	    fileHandler.writeCorrelationsToFile(topCorrelations);

	    CalculationResponse output = createCalculationResponse(topCorrelations);
	    endTime = System.currentTimeMillis();
	    output.setElapsedTime(endTime - startTime);
	    return output;
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
				    future.get(10, TimeUnit.MILLISECONDS);
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
					future.get(10, TimeUnit.MILLISECONDS);
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
		// If any other messages need to be sent...
		if (rulesController.addRuleResponse(response, clientSocketHandler)) {
			clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));
			waitForSlaveRuleExecution();
		} else {
			latchCountDown();
		}
	}

	// TODO Merge with addRuleResponseAndSendNext
    private synchronized void addCorrelationResponseAndSendNext(ClientSocketHandler clientSocketHandler, RuleCorrelationResponse response) {
    	// If any other messages need to be sent...
    	if (rulesController.addCorrelationResponse(response, clientSocketHandler)) {
    		clientFutures.put(clientSocketHandler, pool.submit(clientSocketHandler));
		    waitForSlaveCorrelationExecution();
	    } else {
    		latchCountDown();
	    }
    }

    private CalculationResponse createCalculationResponse(List<RulesCorrelation> correlations) {
		return new CalculationResponse(correlations, masterPart1Time, masterPart2Time, slavePart1Time, slavePart2Time);
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }
}