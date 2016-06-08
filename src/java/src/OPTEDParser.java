import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class OPTEDParser {

	public static void main(String... args) {
	
		Connection c = null;
		
	    try {
	    	System.out.println("Starting up...");
	    	
	    	// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("...connected to DB");
	    	
	    	// Setup for parsing
		    LineHandler handler = new LineHandler(c);
		    SAXParserImpl parser = SAXParserImpl.newInstance(null);
		    
		    // Parse the dictionary
		    char index = 'a';
		    while (index <= 'z'){
		    	parseFile("lib/opted/wb1913_" + index + ".html");
		    	 
		    }
		   
//		    char index = 'a';
//		    while (index <= 'z'){
//		    	System.out.println("...parsing letter '" + index + "'");
//		    	parser.parse(
//		    			new FileInputStream("lib/opted/wb1913_" + index + ".html"), 
//		    			handler);
//		    	index++;
//		    }
//		    System.out.println("...parsing recent additions");
//		    parser.parse(
//	    			new FileInputStream("lib/opted/wb1913_new.html"), 
//	    			handler);
//		    
		    // Close the DB connection
	    	c.close();
	    	System.out.println("...done.");
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
	    }
    }
	
	private static void parseFile(String file) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(""), StandardCharsets.UTF_8))) {
			for(String line; (line = br.readLine()) != null; ) {
				// parse the line here
			}
		}
		catch (Exception e) {
			
		}
	}
	
	private static String cleanString(String string) {
		String clean = string.replace("'", "\'");
		//clean = string.replace("–", "-");
		clean = string.replace("\"", "\\\"");
		//clean = string.replace("�", "-");
		return clean;
	}
	
	private static void commitWord(String word, String definition, Connection c) {
		// Clean the word/definition for the database
		word = cleanString(word);
		definition = cleanString(definition);
		//System.out.println(word + ":\n" + definition + '\n');
		
//		int wordID = getWordID(word, c);
//		if (wordID == -1) {
//    		// Word exists, insert definition only
//			insertWord(word, c);
//    	}
//    	else {
//    		// Word doesn't exist, insert new word and its definition
//    		insertDefinition(definition, wordID, c);
//    	}
		
	}
	
	private static void insertWord(String word, String definition, Connection c) {
		try {
    		// Insert the given word into the dictionary
	    	String sql = "INSERT INTO words(text) VALUES(?)";
	    	PreparedStatement stmt = c.prepareStatement(sql);
	    	stmt.setString(1, word);
	    	//String sql = "INSERT INTO words(text) VALUES('" + word + "')";
	    	stmt.executeUpdate();
	    	
	    	// Close up this shop
	    	stmt.close();
	    	
	    	// Get the ID of this word to insert its definition
	    	int wordID = getWordID(word, c);
	    	insertDefinition(definition, wordID, c);
		}
		catch (Exception e) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	private static void insertDefinition(String definition, int wordID, Connection c) {
		try {
    		// Insert definition with associated word ID
	    	String sql = "INSERT INTO definitions(word_id, text) VALUES(?, ?)";
	    	PreparedStatement stmt = c.prepareStatement(sql);
	    	stmt.setInt(1, wordID);
	    	stmt.setString(2, definition);
	    	//String sql = "INSERT INTO definitions(word_id, text) VALUES(" + wordID + ", '" + definition + "')";
	    	stmt.executeUpdate();
	    	
	    	// Close up shop
	    	stmt.close();
		}
		catch (Exception e) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
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
