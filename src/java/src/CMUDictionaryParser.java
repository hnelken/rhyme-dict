import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CMUDictionaryParser {

	public static void main(String[] args) {
		
		Connection c = null;
		
		try {
			// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("DB Connection Successful");
			
			// Parse the CMU Pronouncing Dictionary line by line
			BufferedReader br = new BufferedReader(new FileReader("lib/cmudict-0-7b"));
		    for(String line; (line = br.readLine()) != null; ) {
		        // Ignore comments beginning with ";"
		    	if (line.charAt(0) != ';') {
		    		// Split the line and commit it to the database
		    		String[] lines = line.split("  ");
		    		String word = lines[0];
		    		String prnc = lines[1];
		    		commitPronounciation(word, prnc, c);
		    	}
		    }
		    
		    // Close up shop
		    br.close();
	    	c.close();
		}
		catch (Exception e) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(-1);	
		}

    	System.out.println("Parse complete");

	}
	
	// Inserts a pronunciation record and the links it with all matching words
	private static void commitPronounciation(String word, String prnc, Connection c) throws Exception {
    	int prncID = -1;
    	boolean insertedPrnc = false;
    	
    	// Strip CMU dictionary word of potential "(*)" suffix
    	String cleanWord = word.replaceAll("\\(.*\\)", "");
    	
		// Query word list for matches with cleaned CMU word
    	PreparedStatement stmt = c.prepareStatement("SELECT * FROM wn_synset WHERE word=?");
    	stmt.setString(1, cleanWord);
    	
    	// All results must be linked to this pronunciation
    	ResultSet results = stmt.executeQuery();
    	while (results.next()) {
    		// Insert the pronunciation first if it hasn't been done
    		if (!insertedPrnc) {
    			prncID = insertPronunciation(word, prnc, c);
    			insertedPrnc = prncID != -1;
    		}
    		
    		// Link this word with the pronunciation
    		int synsetID = results.getInt("synset_id");
    		int wordNum = results.getInt("w_num");
			linkWord(synsetID, wordNum, prncID, c);
    	}
    	
    	// Close up shop and return pronunciation ID
    	results.close();
    	stmt.close();	
	}

	// Inserts a pronunciation into the database
	private static int insertPronunciation(String word, String prnc, Connection c) throws Exception {
		PreparedStatement stmt = c.prepareStatement("INSERT INTO prncs(word, prnc) VALUES(?, ?)");
		stmt.setString(1, word);
		stmt.setString(2, prnc);
		stmt.executeUpdate();
		stmt.close();
		
		// Return the pronunciation's id
		return getPronounciationID(word, c);
	}
	
	// Adds an entry into a word-pronunciation relation table
	private static void linkWord(int synsetID, int wordNum, int prncID, Connection c) throws Exception {
		// Entry consists of word's (synset_id, w_num) key and pronunciation's id
    	PreparedStatement stmt = c.prepareStatement("INSERT INTO word_prnc VALUES(?, ?, ?)");
    	stmt.setInt(1, synsetID);
    	stmt.setInt(2, wordNum);
    	stmt.setInt(3, prncID);
    	stmt.executeUpdate();
    	stmt.close();
	}
	
	// Returns an ID for a word in the CMU pronouncing dictionary
	private static int getPronounciationID(String word, Connection c) throws Exception {
		// Query pronunciations for matching unique CMU word
    	PreparedStatement stmt = c.prepareStatement("SELECT prnc_id FROM prncs WHERE word=?");
    	stmt.setString(1, word);
		int prncID = -1;
    	
    	// Pronunciation exists if this query returns ANY results
    	ResultSet results = stmt.executeQuery();
    	if (results.next()) {
    		prncID = results.getInt("prnc_id");
    	}
    	
    	// Close up shop and return pronunciation ID
    	results.close();
    	stmt.close();
    	return prncID;
	}
}
