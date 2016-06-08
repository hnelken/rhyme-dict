import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CMUDictionaryParser {

	public static void main(String[] args) {
		
		Connection c = null;
		
		try {
			// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("DB Connection Successful");
			
			// Parse the CMU Pronouncing Dictionary line by line
			BufferedReader br = new BufferedReader(new FileReader("lib/opted/"));
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

	}
	
	private static void commitPronounciation(String word, String prnc, Connection c) {
		try {
			int wordID = getWordID(word, c);
			if (wordID != -1) {
				String sql = "INSERT INTO pronounces(word_id, text) VALUES(?, ?)";
				PreparedStatement stmt = c.prepareStatement(sql);
				stmt.setInt(1, wordID);
				stmt.setString(2, prnc);
				stmt.executeUpdate();
			}
		}
		catch (Exception E) {
			System.out.println("Failed to add " + word + " to DB.");
		}
	}

	private static int getWordID(String word, Connection c) {
		// Return -1 if word doesn't exist
		int wordID = -1;
		
		try {
    		// Check DB if word is contained
	    	Statement stmt = c.createStatement();
	    	String sql = "SELECT id FROM words WHERE text='" + word + "'";
	    	
	    	// Word exists if this query returns any results
	    	ResultSet results = stmt.executeQuery(sql);
	    	if (results.next()) {
	    		wordID = results.getInt("id");
	    	}
	    	
	    	// Close up shop and return word ID
	    	results.close();
	    	stmt.close();
	    	return wordID;
		}
		catch (Exception e) {
			return wordID;
		}
	}
}
