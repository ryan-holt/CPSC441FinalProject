package Master;
/**
 * Handles the incoming file coming in, including reading, writing, etc.
 * @author Ryan Holt, Richard Lee, Gary Wu, Tyler Lam
 * @since November 1st 2019
 * @version 1.0
 */
import util.SurveyEntry;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {

    String filepath;

    public FileHandler() {
        filepath = System.getProperty("user.dir") + "\\SurveyEntries.txt";
    }

    public ArrayList<SurveyEntry> ReadFromFile() throws IOException {
        ArrayList<SurveyEntry> responses = new ArrayList<SurveyEntry>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line;
        while((line = reader.readLine()) != null) {
            String[] lineSplit = line.split("\t");
            String[] entrySplit = lineSplit[2].split(",");
            ArrayList<String> entryList = new ArrayList<String>(Arrays.asList(entrySplit));
            SurveyEntry tempEntry = new SurveyEntry(lineSplit[1], Integer.parseInt(lineSplit[0]),entryList);
            responses.add(tempEntry);
        }
        reader.close();
        return responses;
    }

    public String writeArrayToFile(ArrayList<SurveyEntry> list) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filepath, true));
        for(int i = 0; i < list.size(); i++) {
            String tempEntry = new String(Integer.toString(list.get(i).getQuestion()) + "\t" + list.get(i).getName() + "\t" + String.join(",",list.get(i).getSelections()));
            outputWriter.write(tempEntry + "\n");
        }
        outputWriter.flush();
        outputWriter.close();
        return filepath;
    }
}
