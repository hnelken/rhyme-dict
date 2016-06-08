import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A helper class for parsing the plain-text dictionary.
 * Handles parsing each line for a word and definition, then
 * commits these to the database, unifying duplicates.
 */
public class LineHandler extends DefaultHandler {

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
			
			// Add word and definition to database
			commitWord();
		}
	}
	
	private String cleanString(String string) {
		String clean = string.replace("'", "\'");
		clean = string.replace("–", "-");
		clean = string.replace("\"", "\\\"");
		clean = string.replace("�", "-");
		return clean;
	}
	
	private void commitWord() {
		// Clean the word/definition for the database
		word = cleanString(word);
		definition = cleanString(definition);
		System.out.println(word + ":\n" + definition + '\n');
		
//		int wordID = getWordID(word);
//		if (wordID == -1) {
//    		// Word exists, insert definition only
//			insertWord(word);
//    	}
//    	else {
//    		// Word doesn't exist, insert new word and its definition
//    		insertDefinition(definition, wordID);
//    	}
		
	}
	
	private void insertWord(String word) {
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
	    	int wordID = getWordID(word);
	    	insertDefinition(definition, wordID);
		}
		catch (Exception e) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	private void insertDefinition(String definition, int wordID) {
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
