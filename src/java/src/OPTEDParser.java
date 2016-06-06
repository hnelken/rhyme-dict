import java.io.FileInputStream;
import java.sql.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

public class OPTEDParser {

	public static void main(String... args) throws Exception {
	
		Connection c = null;
		Statement stmt = null;
		
	    try {
	    	// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("DB Connection Successful");
	    	
	    	// Setup for parsing
	    	OPTEDParser opted = new OPTEDParser();
		    LineHandler handler = opted.new LineHandler(c);
		    SAXParserImpl parser = SAXParserImpl.newInstance(null);
		    
		    // Parse the dictionary, making DB inserts as we go
		    parser.parse(
		        new FileInputStream("lib/opted/wb1913_z.html"),
		        handler);
	    	
		    // Close the DB connection
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	c.close();
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
	    }
    }
	
	/**
	 * A class for parsing the plain-text dictionary.
	 * Parses each line for the word and definition and
	 * commits these to the database, checking for duplicates.
	 */
	private class LineHandler extends DefaultHandler {
	
		private int count = 500;
		private String word = null;
		private String definition = null;
    	private StringBuilder builder;
    	private Connection c;
    	
    	public LineHandler(Connection conn) {
    		this.c = conn;
    	}
    	
    	public String getWord() {
    		return word;
    	}
    	
    	public String getDefinition() {
    		return definition;
    	}
    	
    	public void characters(char[] ch, int start, int length) {
    		// append any characters to the string builder
    		if (builder != null) {
    			builder.append(new String(ch, start, length));
    		}
    	}
    	
		public void startElement(String uri, String localName,
                String name, Attributes a) {
			if (name.equalsIgnoreCase("b")) {
				builder = new StringBuilder();
			}
    	}

    	public void endElement(String uri, String localName, String name) {
    		if (name.equalsIgnoreCase("b")) {
    			word = builder.toString();
    			builder = new StringBuilder();
    		}
    		else if (name.equalsIgnoreCase("i")) {
    			builder = new StringBuilder();
    		}
    		else if (name.equalsIgnoreCase("p")) {
    			definition = builder.toString().substring(2);
    			commitWord();
    		}
    	}
    	
    	private void commitWord() {
    		
    		System.out.println(
    				"Word: " + word + 
    				"\nDefinition: " + definition + '\n');
    		
    		int wordID = getWordID(word);
    		if (wordID != -1) {
	    		// Word exists, insert definition only
    			insertWord(word);
	    	}
	    	else {
	    		// Word doesn't exist, insert new word and its definition
	    		insertDefinition(definition, wordID);
	    	}
    		
    	}
    	
    	private void insertWord(String word) {
    		try {
	    		// Check DB if word is contained
		    	Statement stmt = c.createStatement();
		    	String sql = "INSERT INTO words(text) VALUES(text='" + word + "')";
		    	
		    	// Word exists if this query returns any results
		    	stmt.executeUpdate(sql);
		    	
		    	// Close up this shop
		    	stmt.close();
		    	
		    	// Insert definition
		    	int wordID = getWordID(word);
		    	insertDefinition(definition, wordID);
    		}
    		catch (Exception e) {
    	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		}
    	}
    	
    	private void insertDefinition(String definition, int wordID) {
    		try {
	    		// Check DB if word is contained
		    	Statement stmt = c.createStatement();
		    	String sql = "INSERT INTO definitions(word_id, text) VALUES(word_id='" + wordID + "', text='" + word + "')";
		    	
		    	// Word exists if this query returns any results
		    	stmt.executeUpdate(sql);
		    	
		    	// Close up shop
		    	stmt.close();
    		}
    		catch (Exception e) {
    	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		}
    	}
    	
    	private int getWordID(String word) {
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
		    	
		    	// Close up shop
		    	results.close();
		    	stmt.close();
		    	return wordID;
    		}
    		catch (Exception e) {
    			return wordID;
    		}
    	}
	}
}
