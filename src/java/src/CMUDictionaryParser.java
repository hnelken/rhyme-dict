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
		    		if (wordExistsInDictionary(word, c)) {
		    			commitPronounciation(word, prnc, c);
		    		}
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
	
	private static boolean wordExistsInDictionary(String word, Connection c) throws Exception {
		// Query word list for matching CMU word
    	PreparedStatement stmt = c.prepareStatement("SELECT * FROM wn_synset WHERE word=?");
    	stmt.setString(1, word);
    	
    	// Word exists if this query returns any results
    	ResultSet results = stmt.executeQuery();
    	if (results.next()) {
    		results.close();
    		stmt.close();
    		return true;
    	}
    	
    	// Close up shop and return pronunciation ID
    	results.close();
    	stmt.close();
    	
    	return false;
	}
	
	// Inserts a pronunciation record and the links it with all matching words
	private static void commitPronounciation(String word, String prnc, Connection c) throws Exception {
		// Insert pronunciation into the database
		String sql = "INSERT INTO prncs(word, prnc) VALUES(?, ?)";
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, word);
		stmt.setString(2, prnc);
		stmt.executeUpdate();
					
		// Get the pronunciation's id
		int prncID = getPronounciationID(word, c);
					
		linkWord(word, prncID, c);
	}
	
	// Adds an entry into a word-pronunciation relation table
	private static void linkWord(String word, int prncID, Connection c) throws Exception {
		// Strip CMU dictionary word of potential "(*)" suffix
		word = word.replaceAll("\\(.*\\)", "");
		
		// Query words in database for matches to CMU word, then insert record in relation table
    	PreparedStatement insert = c.prepareStatement("INSERT INTO word_prnc VALUES(?, ?, ?)");
    	PreparedStatement query = c.prepareStatement("SELECT * FROM wn_synset WHERE word=?");
    	query.setString(1, word);
    	
    	ResultSet results = query.executeQuery();
    	while (results.next()) {
    		// Get IDs from matched entry
    		int synsetID = results.getInt("synset_id");
    		int wordNum = results.getInt("w_num");
    		
    		// For each result, add an entry in the word_prnc relation table
    		insert.setInt(1, synsetID);
    		insert.setInt(2, wordNum);
    		insert.setInt(3, prncID);
    		
    		insert.executeUpdate();
    	}
    	
    	// Close up shop and return word ID
    	results.close();
    	insert.close();
    	query.close();
	}
	
	// Returns an ID for a word in the CMU pronouncing dictionary
	private static int getPronounciationID(String word, Connection c) throws Exception {
		// Query pronunciations for matching CMU word
    	PreparedStatement stmt = c.prepareStatement("SELECT prnc_id FROM prncs WHERE word=?");
    	stmt.setString(1, word);
		int prncID = -1;
    	
    	// Word exists if this query returns any results
    	ResultSet results = stmt.executeQuery();
    	while (results.next()) {
    		prncID = results.getInt("prnc_id");
    	}
    	
    	// Close up shop and return pronunciation ID
    	results.close();
    	stmt.close();
    	return prncID;
	}
}
