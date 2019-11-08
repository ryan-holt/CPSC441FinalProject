package Master;
/**
 * Handles the incoming file coming in, including reading, writing, etc.
 * @author Ryan Holt, Richard Lee, Gary Wu, Tyler Lam
 * @since November 1st 2019
 * @version 1.0
 */
import util.KeywordGroup;
import util.RulesCorrelation;
import util.SurveyEntry;
import java.time.*;

import java.io.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {

    /**
     * Filepath for the text-based database
     */
    String surveyEntriesFilePath;
    String scorePath;
    DateTimeFormatter dtf;
    LocalDateTime now;

    public FileHandler() {
        surveyEntriesFilePath = System.getProperty("user.dir") + "\\SurveyEntries.txt";
        scorePath = System.getProperty("user.dir") + "\\correlationScores.txt";
    }

    /**
     * Read the text-based database and writes to an arraylist
     * @return ArrayList responses
     * @throws IOException
     */
    public ArrayList<SurveyEntry> ReadFromFile() throws IOException {
        ArrayList<SurveyEntry> responses = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(surveyEntriesFilePath));
        String line;
        while((line = reader.readLine()) != null) {
            String[] lineSplit = line.split("\t");
            String[] entrySplit = lineSplit[2].split(",");
            ArrayList<String> entryList = new ArrayList<>(Arrays.asList(entrySplit));
            SurveyEntry tempEntry = new SurveyEntry(lineSplit[1], Integer.parseInt(lineSplit[0]),entryList);
            responses.add(tempEntry);
        }
        reader.close();
        return responses;
    }

    /**
     * Writes the arraylist into a text-based database
     * @param list the list of survey answer
     * @throws IOException
     */
    public void writeArrayToFile(ArrayList<SurveyEntry> list) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(surveyEntriesFilePath, true));
        for (SurveyEntry aList : list) {
            String tempEntry = Integer.toString(aList.getQuestion()) + "\t" + aList.getUser() + "\t" + String.join(",", aList.getSelections());
            outputWriter.write(tempEntry + "\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

    /**
     * writes the correlation scores to a file
     * @param correlationScores ArrayList that holds all the correlation scores
     * @throws IOException
     */
    public void writeScoresToFile(ArrayList<RulesCorrelation> correlationScores) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(scorePath, true));
        for(RulesCorrelation correlationInfo: correlationScores) {
            outputWriter.write(correlationInfo.toString() + "\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

    /**
     * writes the correlation scores to a file
     * @param correlationScores ArrayList that holds all the correlation scores
     * @throws IOException
     */
    public String getListOfHistoricalCorrelation(){
        File[] files = new File(System.getProperty("user.dir") + File.separator + "HistoricalCorrelations").listFiles();
        StringBuilder stringList = new StringBuilder();
        int i = 0;
        for (File file : files) {
            if (file.isFile()) {
                if(i != 0){
                    stringList.append("\n");
                }
                stringList.append(file.getName());
            }
            i++;
        }
        return stringList.toString();
    }

    /**
     * Reads the correlation scores from a file
     * @return returns a arraylist with the score information
     * @throws IOException
     */
    public ArrayList<RulesCorrelation> getHistoricalCorrelations(String filename) throws IOException {
        String scorePath = System.getProperty("user.dir") + File.separator + "HistoricalCorrelations" + File.separator + filename;
        BufferedReader reader = new BufferedReader(new FileReader(scorePath));
        ArrayList<RulesCorrelation> correlationScoresList = new ArrayList<>();
        String line;
        String[] lineSplit;
        while((line = reader.readLine()) != null) {
            ArrayList<KeywordGroup> keywordList = new ArrayList<>();
            lineSplit = line.split(":");
            String[] keywordPairSplit = lineSplit[0].split("\\), \\(");
            for(String keyword: keywordPairSplit) {
                keyword = keyword.replace("(","").replace(")", "");
                String[] keywordSplit = keyword.split(",");
                keywordList.add(new KeywordGroup(keywordSplit[0].trim(), keywordSplit[1].trim()));
            }
            correlationScoresList.add(new RulesCorrelation(keywordList, Double.parseDouble(lineSplit[1].trim())));
        }
        return correlationScoresList;
    }
}
