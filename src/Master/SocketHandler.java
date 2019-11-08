package Master;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.*;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class SocketHandler implements Runnable {

    /**
     * The socket used for general input and output communication
     */
    private Socket aSocket;

    /**
     * The input socket used for reading in messages from slave and client
     */
    private ObjectInputStream socketIn;

    /**
     * the output socket used for sending out messages to slave and client
     */
    private ObjectOutputStream socketOut;

    /**
     * Master Controller
     */
    private MasterController masterController;

    /**
     * File Handler used to handle files writing and reading into database
     */
    private FileHandler fileHandler;

    public SocketHandler(Socket s, MasterController masterController) {
        fileHandler = new FileHandler();
        try {
            aSocket = s;
            setMasterController(masterController);
            socketOut = new ObjectOutputStream(aSocket.getOutputStream());


            printIPInfo();
        } catch (IOException e) {
            System.out.println("ServerCommController: Create ServerCommController Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        createInputStream();
        communicate();
    }

    /**
     * Continously reads in and sends out messages to the client and slaves.
     * Depending on the messages receives, different choices will be made
     */
    public void communicate() {
        ArrayList<String> responses = new ArrayList<String>();
        Message messageToSend = new Message("Please enter your name");
        Message responseMessage;
        try {
            writeObject(messageToSend);
            responseMessage = (Message) socketIn.readObject();
            boolean endWhile = true;
            SurveyQuestions surQues = new SurveyQuestions();
            if (responseMessage.getAction().equalsIgnoreCase("admin")) {
                while (endWhile) {
                    messageToSend.setAction("Please enter one of the following actions: calculateCorrelation, " +
                            "listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                    try {
                        writeObject(messageToSend);
                        responseMessage = (Message)socketIn.readObject();
                        switch (responseMessage.getAction()) {
                            case "calculateCorrelation":
                                //Insert code to calculate correlations
                                prepareAssociationRuleRequests();
                                break;
                            case "listHistoricalCorrelation":
                                //Insert code to do that
                                break;
                            case "viewHistoricalCorrelation":
                                //Insert code to do that
                                break;
                            case "quit":
                                endWhile = false;
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {

                    }
                }
            } else {
                createAndSendSurvey();
            }
            socketIn.close();
            socketOut.close();
            aSocket.close();
            System.exit(1);
        } catch (Exception e) {
        }
    }

    /**
     * Creates the survey questions object and sends to the survey
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void createAndSendSurvey() throws IOException, ClassNotFoundException {
        SurveyQuestions newSurvey = new SurveyQuestions();
        writeObject(newSurvey);
        SurveyAnswer clientAnswer = (SurveyAnswer) socketIn.readObject();
        fileHandler.writeArrayToFile(clientAnswer.getAnswer());
    }

    /**
     * Prepares the association rule request package to send to slave
     * @throws IOException
     */
    public void prepareAssociationRuleRequests() throws IOException {
        ArrayList<SurveyEntry> entries = fileHandler.ReadFromFile();
        HashMap<Integer,ArrayList<SurveyEntry>> entriesByQuestion = orderEntriesByQuestion(entries);
        HashMap<Integer,ArrayList<KeywordGroup>> keywordsByQuestion = getKeywordGroupsByQuestion();
        ArrayList<AssociationRuleRequest> ruleRequests = createAssociationRuleRequests(entriesByQuestion, keywordsByQuestion);

        //TESTING ASSOCIATIONSTUFF HERE
        //TODO: REMOVE ONCE DONE
        Map<Integer, AssociationRuleResponse> testMap = new HashMap<>();
        ArrayList<Rule> testArray = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Bob"); tempList.add("Frank"); tempList.add("Jane");
        testArray.add(new Rule(new KeywordGroup("Python", "Java"), 0.97, tempList));
        AssociationRuleResponse testResponse = new AssociationRuleResponse(1, testArray);
        testMap.put(1, testResponse);
        ArrayList<Rule> testArray2 = new ArrayList<>();
        ArrayList<String> tempList2 = new ArrayList<>();
        tempList2.add("Alice"); tempList2.add( "Jane"); tempList2.add("Kevin");
        testArray2.add(new Rule(new KeywordGroup("Superstore", "H&M"), 0.92, tempList2));
        ArrayList<String> tempList3 = new ArrayList<>();
        tempList3.add("Frank"); tempList3.add("Uaran"); tempList3.add("Reacat");
        testArray2.add(new Rule(new KeywordGroup("Superstore", "Walmart"), 0.87, tempList3));
        AssociationRuleResponse testResponse2 = new AssociationRuleResponse(2, testArray2);
        testMap.put(2, testResponse2);
        ArrayList<RuleCorrelationRequest> correlationRequestList = createRuleCorrelationRequests(testMap);

        //TODO: REMOVE ONCE DONE
        ArrayList<RulesCorrelation> correlationScoresList = new ArrayList<>();
        ArrayList<KeywordGroup> keywordList = new ArrayList<>();
        keywordList.add(new KeywordGroup("Ryan", "Holt"));
        keywordList.add(new KeywordGroup("Tyler", "Lam"));
        correlationScoresList.add(new RulesCorrelation(keywordList, 0.69));
        fileHandler.writeScoresToFile(correlationScoresList);
        ArrayList<RulesCorrelation> result = fileHandler.readScoresFromFile();

        sendRuleRequestsToSlaves();
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

    /**
     * sends the association rule package to the slaves
     */
    void sendRuleRequestsToSlaves() {
        System.out.println("Sending rule requests to slaves.");
    }

    /**
     * Returns an arraylist of rule correlation requests
     * @param ruleResponses the rule responses
     * @return the arraylist of rule correlation requests
     */
    ArrayList<RuleCorrelationRequest> createRuleCorrelationRequests(Map<Integer, AssociationRuleResponse> ruleResponses) {
        ArrayList<RuleCorrelationRequest> outputList = new ArrayList<RuleCorrelationRequest>();
        for(int i = 1; i <= ruleResponses.size(); i++) {
            for(int j=0; j < ruleResponses.get(i).getResult().size(); j++) {
                if(i+1 <= ruleResponses.size()) {
                    ArrayList<Rule> ruleCorrelationArray = new ArrayList<>();
                    ruleCorrelationArray.add(ruleResponses.get(i).getResult().get(j));
                    outputList.add(new RuleCorrelationRequest("baseRule", ruleCorrelationArray));
                    for (int k = i + 1; k <= ruleResponses.size(); k++) {
                        ruleCorrelationArray = new ArrayList<>();
                        for (int l = 0; l < ruleResponses.get(k).getResult().size(); l++) {
                            ruleCorrelationArray.add(ruleResponses.get(k).getResult().get(l));
                        }
                        outputList.add(new RuleCorrelationRequest("rule", ruleCorrelationArray));
                    }
                }
            }
        }
        return outputList;
    }

    /**
     * Creates an input socket stream from server
     */
    public void createInputStream() {
        try {
            socketIn = new ObjectInputStream(aSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating server output stream");
            e.printStackTrace();
        }
    }

    /**
     * Prints the IP information (current IP address)
     */
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

    /**
     * Sets the master controller
     * @param sc master controller object
     */
    public void setMasterController(MasterController sc) {
        masterController = sc; // 2-way association
    }

    /**
     * Closes the socket
     * @throws IOException
     */
    public void stop() throws IOException {
        aSocket.close();
    }

    /**
     * Writes the corresponding object to the output socket
     * @param obj The output object
     * @throws IOException
     */
    private void writeObject(Object obj) throws IOException {
        socketOut.writeObject(obj);
        socketOut.reset();
    }
}