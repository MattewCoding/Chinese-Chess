package outOfGameScreens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class OptimalPositions {

	private HashMap<Integer[], Integer> redGeneral = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redAdvisor = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redCannon = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redChariot = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redElephant = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redHorse = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> redSoldier = new HashMap<Integer[], Integer>();

	private HashMap<Integer[], Integer> blackGeneral = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackAdvisor = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackCannon = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackChariot = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackElephant = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackHorse = new HashMap<Integer[], Integer>();
	private HashMap<Integer[], Integer> blackSoldier = new HashMap<Integer[], Integer>();
	
	private HashMap<Integer[], Integer[]> board = new HashMap<Integer[], Integer[]>();
	
	public OptimalPositions() throws IOException {
		csvToHash(redGeneral, "redGeneral.csv");
		csvToHash(redAdvisor, "redAdvisor.csv");
		csvToHash(redCannon, "redCannon.csv");
		csvToHash(redChariot, "redChariot.csv");
		csvToHash(redElephant, "redElephant.csv");
		csvToHash(redHorse, "redHorse.csv");
		csvToHash(redSoldier, "redSoldier.csv");
		
		csvToHash(blackGeneral, "blackGeneral.csv");
		csvToHash(blackAdvisor, "blackAdvisor.csv");
		csvToHash(blackCannon, "blackCannon.csv");
		csvToHash(blackChariot, "blackChariot.csv");
		csvToHash(blackElephant, "blackElephant.csv");
		csvToHash(blackHorse, "blackHorse.csv");
		csvToHash(blackSoldier, "blackSoldier.csv");
	}
	
	public void csvToHash(HashMap<Integer[], Integer> map, String filePath) throws IOException {
        // Change "filename.csv" to the name of your CSV file
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Loop through each line of the CSV file
        String line;
        while ((line = reader.readLine()) != null) {
            // Split the line into comma-separated values using a StringTokenizer
            StringTokenizer tokenizer = new StringTokenizer(line, ",");

            // Parse the first value into an Integer[]
            String firstValue = tokenizer.nextToken().trim().replace("[", "").replace("]", "");
            String[] firstValueParts = firstValue.split(";");
            Integer[] key = new Integer[firstValueParts.length];
            for (int i = 0; i < firstValueParts.length; i++) {
                key[i] = Integer.parseInt(firstValueParts[i].trim());
            }

            // Parse the second value into an Integer
            int value = Integer.parseInt(tokenizer.nextToken().trim());

            // Add the key-value pair to the HashMap
            map.put(key, value);
        }

        // Close the BufferedReader
        reader.close();
    }
}
