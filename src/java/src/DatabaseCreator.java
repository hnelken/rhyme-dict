import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DatabaseCreator {
	
	public static void main(String... args) throws Exception {
		
		Connection c = null;
		Statement stmt = null;
		
	    try {
	    	// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("DB Connection Successful");
	    	
	    	// Begin executing the pared-down Princeton WordNet SQL script
	    	stmt = c.createStatement();
	    	boolean assembling = false;
	    	StringBuilder builder = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("lib/dict.sql"), StandardCharsets.UTF_8));
	    	for(String line; (line = br.readLine()) != null; ) {
	    		
	    		// Check if this is a multiline SQL command
	    		if (line.length() != 0 && line.charAt(line.length() - 1) == '(') {
	    			assembling = true;
	    		}
	    		
	    		// If its multi-line, build the whole thing
	    		if (assembling) {
	    			// Multi-lines delimited by blank line, 
	    			if (line.length() == 0) {
		    			// End of command reached
	    				assembling = false;
	    				stmt.executeUpdate(builder.toString());
	    				builder = new StringBuilder();
	    			}
	    			else {
	    				// Append line to command
	    				builder.append(line);
	    				builder.append('\n');
	    			}
	    		}
	    		else {
	    			// Execute single line command
	    			executeLine(line, stmt);
	    		}
	    	}
	    	
		    // Close up shop
	    	br.close();
	    	stmt.close();
	    	c.close();
	    	System.out.println("Tables created successfully");
	    	
	    }
	    catch ( Exception ex ) {
	    	c.close();
	    	System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
	    	System.exit(0);	
	    }
    }
	
	// Parse and execute a single line command of SQL
	private static void executeLine(String line, Statement stmt) throws Exception {
		// Insert lines take special attention
		if (line.contains("INSERT")) {
			if (line.contains("wn_gloss") && line.contains("\\")) {
				// Remove escape characters from the definitions
				line = line.replace("\\", "");
			}
			else if (line.contains("wn_synset")) {
				// Clean the word inserts thoroughly
				line = cleanSQLWordInsert(line);
			}
		}

		// Execute the SQL statement
		stmt.executeUpdate(line);
	}
	
	// Thoroughly clean the word inserts
	private static String cleanSQLWordInsert(String line) {
		String pre = null;
		String word = null;
		
		// Split the line where the word attribute starts
		if (line.charAt(43) == ',') {
			pre = line.substring(0, 44);
			word = line.substring(45);
		}
		else {
			pre = line.substring(0, 45);
			word = line.substring(46);
		}

		// Split at the end of the word attribute as well
		int endWord = word.indexOf('\'');
		boolean searching = true;
		while (searching) {
			if (word.charAt(endWord + 1) != ',') {
				endWord = word.indexOf('\'', endWord + 1);
			}
			else {
				searching = false;
			}
		}
		word = word.substring(0, endWord);
		
		// Clean the word section of the string
		word = word.replace("\\", "");
		word = word.replaceAll("\\(.*\\)", "");
		word = "\"" + word.toUpperCase() + "\"";
		
		// Return a truncated entry with cleaned word attribute
		return pre + word + ", 0);";
	}
}
