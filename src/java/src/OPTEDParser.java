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
		   // LineHandler handler = new LineHandler(c);
		   // SAXParserImpl parser = SAXParserImpl.newInstance(null);
		    
		    // Parse the dictionary
		    for (char index = 'c'; index <= 'c'; index++){
		    	parseFile("lib/opted/wb1913_" + index + ".html", c);
		    }
		    //parseFile("lib/opted/wb1913_new.html", c);
		    
		    // Close the DB connection
	    	c.close();
	    	System.out.println("...done.");
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
	    }
    }
	
	private static void parseFile(String file, Connection c) {
		System.out.println("parsing " + file);
		
		// Begin reading the given filename
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

			boolean body = false;
			for(String line; (line = br.readLine()) != null; ) {

				// Ignore everything but the HTML body
				if (body) {
					body = parseLine(line, c);
				}
				else if (line.equals("<BODY>")) {
					body = true;
				}
			}
		}
		catch (Exception e) {
			
		}
	}
	
	private static boolean parseLine(String line, Connection c) {
		// Stop and signal when the body ends
		if (line.equals("</BODY>")) { return false; }
		
		// Otherwise parse the line
		// Lines of the form: 
		//		<P><B>word</B> (<I>part of speech</I>) definition</P>
		
		// Remove unnecessary HTML text
		line = line.replaceAll("</P>|<P>|<B>| \\(<I>(.*)</I>\\) ", "");
		
		// Separate word and definition
		String[] parts = line.split("</B>");
		String word = parts[0];
		String definition = parts[1];
		
		// Insert word and definition in database
		commitWord(word, definition, c);
		
		return true;
	}
	
	private static void commitWord(String word, String definition, Connection c) {
		// Clean the word/definition for the database
		word = cleanString(word);
		definition = cleanString(definition);
		
		int wordID = getWordID(word, c);
		if (wordID == -1) {
    		// Word doesn't exist, insert new word and its definition
			insertWord(word, definition, c);
    	}
    	else {
    		// Word exists, insert definition only
    		insertDefinition(definition, wordID, c);
    	}
		
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
	    	System.out.println(word);
	    	int wordID = getWordID(word, c);
	    	if (wordID == -1) {
	    		System.out.println("FUCK--" + word);
	    		System.exit(0);
	    	}
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
	    	//stmt.setString(1, word);
	    	String sql = "SELECT * FROM words WHERE text='" + word + "'";
	    	
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
	
	private static String cleanString(String string) {
		String clean = string.replace("'", "\\'");
		//clean = string.replace("–", "-");
		clean = string.replace("\"", "\\\"");
		//clean = string.replace("�", "-");
		return clean;
	}
}
