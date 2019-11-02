package Master;
/**
 * Handles the incoming file coming in, including reading, writing, etc.
 * @author Ryan Holt, Richard Lee, Gary Wu, Tyler Lam
 * @since November 1st 2019
 * @version 1.0
 */
import java.io.*;
import java.io.IOException;

public class FileHandler {

    public ArrayList<String> ReadFromFile(String directory) throws IOException {
        ArrayList<String> responses = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(directory));
        String line;
        while((line = reader.readLine()) != null) {
            responses.add(responses);
        }
        reader.close();
        return responses;
    }

    public String writeArrayToFile(ArrayList<String> list) {
        Filepath = "C:\\Users\\Tyler\\Documents\\Database.txt";
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Filepath));
        for(int i = 0; i < list.size(); i++) {
            outputWriter.write(list.get(i) + "\n");
        }
        outputWriter.flush();
        outputWriter.close();
        return Filepath;
    }
}
