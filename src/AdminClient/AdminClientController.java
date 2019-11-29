package AdminClient;

import util.*;
import util.sockethandler.ClientSocketHandler;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * This class is responsible for communicating with the server
 * and holding the LoginController
 * Overall the client controller is used for communication with
 * the server
 */
public class AdminClientController implements MessageListener {

    private Socket socket;

    /**
     * BufferedReader to read in user input
     */
    BufferedReader inFromUser;

    private long startTime;

    private long endTime;

    private ClientSocketHandler clientSocketHandler;

    /**
     * Constructs a Client controller object
     *
     * @param serverName name of server
     * @param portNumber port number
     */
    public AdminClientController(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);

            clientSocketHandler = new ClientSocketHandler(socket, this, true);
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Communicates with the server, by reading the user name
     *
     */
    private void communicateWithServer() {
        System.out.println("Welcome to the administrator client. Type help to show all commands.");
        clientSocketHandler.setNextMsgOut(getMessageFromAdminInput());
        clientSocketHandler.communicate();
        System.out.println();
    }

    public Message handleMessage(Message msg) {
        Message msgOut = new Message("quit");
        switch (msg.getAction()) {
            case "sendCalculationResponse":
                CalculationResponse CR = (CalculationResponse) msg;
                displayCorrelations(CR);
                msgOut = getMessageFromAdminInput();
                break;
            case "sendHistoricalCalculationResponse":
                ListHistoricalCalculationsResponse HCR = (ListHistoricalCalculationsResponse) msg;
                endTime = System.currentTimeMillis();
                System.out.println(HCR.getListOfHistoricalCalculations());

                displayTimingInfo(HCR);

                msgOut = getMessageFromAdminInput();
                break;
            case "viewHistoricalCalculation":
                CalculationResponse historicalCR = (CalculationResponse) msg;
                displayCorrelations(historicalCR);
                msgOut = getMessageFromAdminInput();

                break;
            case "FileReadingError":
                System.out.println("Error reading file. Please ensure that the file name is spelt correctly.");
                msgOut = getMessageFromAdminInput();
                break;
            case "terminate":
                clientSocketHandler.stop(); // Server finally said to stop
                System.exit(-1);
                break;
            default:
                msgOut.setAction("terminate");
                System.err.println("Error: ClientController does not recognize message with action " + msg.getAction() + ", terminating");
                break;
        }

        return msgOut;
    }


    /**
     * Gets the next message to send based on commands from the admin.
     *
     * @return the message
     */
    private Message getMessageFromAdminInput() {
        boolean invalidResponse = true;
        Message msgOut = new Message("quit");
        while (invalidResponse) {
	        System.out.println("\nPlease enter a command:");
            try {
                invalidResponse = false;
                String userInput = inFromUser.readLine().trim();
                if (userInput != null && !userInput.isEmpty()) {
                    String[] inputArgs = userInput.split(" ");

                    switch (inputArgs[0].toLowerCase()) {
                        case "help": case "0":
                            displayAllCommands();
                            invalidResponse = true;
                            break;
                        case "calculate": case "1":
                            startTime = System.currentTimeMillis();
                            msgOut = createCalculationReqeust(inputArgs);
                            break;
                        case "list": case "2":
                            startTime = System.currentTimeMillis();
                            msgOut = new Message("listHistoricalCalculations");
                            break;
                        case "get": case "3":
                            startTime = System.currentTimeMillis();
                            System.out.println("Please enter the filename of a previous calculation:");
                            msgOut = new ViewHistoricalCalculationRequest(inFromUser.readLine());
                            break;
                        case "quit": case "4":
                            msgOut.setAction("quit");
                            break;
                        default:
                            System.err.println("Invalid Input");
                            invalidResponse = true;
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                invalidResponse = true;
            }

        }
        return msgOut;
    }

    /**
     * Display all admin client commands and their functionality.
     */
    private void displayAllCommands() {
        System.out.println("calculate: Calculate and display the current keyword group correlations.");
        System.out.println("list: List the file names for historical calculations.");
        System.out.println("get: Show the result for a historical calculation.");
        System.out.println("quit: Terminate the current session.");
        System.out.println("help: Display a list of all commands and their functionality.");
    }

    private CalculationRequest createCalculationReqeust(String[] inputArgs) {
        int keywordGroupSize;
        if (inputArgs.length > 1) {
            try {
                keywordGroupSize = Integer.parseInt(inputArgs[1]);
            } catch (NumberFormatException e) {
                System.out.println("Error: " + inputArgs[1] + " cannot be parsed into an int, assuming value is 2");
                keywordGroupSize = 2;
            }
        } else {
            keywordGroupSize = 2;
        }
        return new CalculationRequest(keywordGroupSize);
    }

    /**
     * Display all correlations.
     *
     */
    private void displayCorrelations(CalculationResponse calcResponse) {
        List<RulesCorrelation> correlations = calcResponse.getCorrelations();
        long elapsedTime = calcResponse.getElapsedTime();
        for (RulesCorrelation correlation : correlations) {
            System.out.println(correlation);
        }
        System.out.println("\nCalculation elapsed time: " + formatMilliTime(elapsedTime));

        if(calcResponse.getMasterPart1Time() != 0) {
            System.out.println("Master Part 1 elapsed time: " + formatMilliTime(calcResponse.getMasterPart1Time()));
            System.out.println("Master Part 2 elapsed time: " + formatMilliTime(calcResponse.getMasterPart2Time()));
            System.out.println("Slave Part 1 elapsed time: " + formatMilliTime(calcResponse.getSlavePart1Time()));
            System.out.println("Slave Part 2 elapsed time: " + formatMilliTime(calcResponse.getSlavePart2Time()));

            System.out.println("The file transfer time between Master and Slave (Part 1) is: " + formatMilliTime(calcResponse.getMasterPart1Time() - calcResponse.getSlavePart1Time()));
            System.out.println("The file transfer time between Master and Slave (Part 2) is: " + formatMilliTime(calcResponse.getMasterPart2Time() - calcResponse.getSlavePart2Time()));
        }
    }

    private void displayTimingInfo(Message msg) {
        long adminElapsedTime = endTime - startTime;
        long masterElapsedTime = msg.getElapsedTime();

        System.out.println("The admin elapsed time is: " + formatMilliTime(adminElapsedTime));
        System.out.println("The master elapsed time is: " + formatMilliTime(masterElapsedTime));
        System.out.println("The file transfer time from admin to network is: " + formatMilliTime(adminElapsedTime - masterElapsedTime));
    }

    private String formatMilliTime(long milliTime) {
        return String.format("%,.3f ms", (double) milliTime);
    }

    /**
     * Connect to localhost of no input. Connect to IP if user enters it
     * @param args command line arguments
     * @return IP string
     */
    public static String getDesiredIP(String[] args) {
        if(args.length < 1) {
            System.out.println("Returning localhost...");
            return "localhost";
        }
        System.out.println("Returning your IP...");
        return args[0];
    }

    /**
     * Runs the admin client side.
     *
     * @param args command line arguments
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        AdminClientController cc = new AdminClientController(getDesiredIP(args), 9000);
        cc.communicateWithServer();
    }
}