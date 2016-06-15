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
		    
		    
		    /**
		     * New process for parse
		     * 	- Parse CMU dict
		     * 		- Insert entry with both texts
		     * 	- Run through all entries
		     * 		- If pronounce text matches a wn_synset word
		     * 			- insert the pronounce id in the wn_synset entry
		     */
		    
		    // Close up shop
		    br.close();
	    	c.close();
		}
		catch (Exception e) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(-1);	
		}

	}
	
	private static void commitPronounciation(String word, String prnc, Connection c) {
		try {
			List<Integer> wordIDs = getMatchIDs(word, c);
			for (int i = 0; i < wordIDs.size(); i++) {
				String sql = "INSERT INTO pronounces(word_id, text) VALUES(?, ?)";
				PreparedStatement stmt = c.prepareStatement(sql);
				stmt.setInt(1, wordIDs.get(i));
				stmt.setString(2, prnc);
				stmt.executeUpdate();
			}
		}
		catch (Exception E) {
			System.out.println("Failed to add " + word + " to DB.");
		}
	}

	private static List<Integer> getMatchIDs(String word, Connection c) {
		List<Integer> matches = new ArrayList<Integer>();
		
		try {
    		// Check DB if word is contained
	    	PreparedStatement stmt = c.prepareStatement("SELECT * FROM wn_synset WHERE word=?");
	    	stmt.setString(1, word);
	    	
	    	// Word exists if this query returns any results
	    	ResultSet results = stmt.executeQuery();
	    	boolean stop = false;
	    	while (results.next() && !stop) {
	    		int wordID = results.getInt("synset_id");
	    		String text = results.getString("word");
	    		
	    		if (text.equals(word)) {
	    			matches.add(wordID);
		    		System.out.println(text + " " + wordID);
		    		System.exit(0);
	    		}
	    	}
	    	
	    	// Close up shop and return word ID
	    	results.close();
	    	stmt.close();
	    	return matches;
		}
		catch (Exception e) {
			return matches;
		}
	}
}
