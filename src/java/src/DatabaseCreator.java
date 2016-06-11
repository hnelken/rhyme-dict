import java.io.BufferedReader;
import java.io.FileInputStream;
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
	    	stmt = c.createStatement();

	    	boolean assembling = false;
	    	StringBuilder builder = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("lib/dict.sql"), StandardCharsets.UTF_8));
	    	for(String line; (line = br.readLine()) != null; ) {
	    		
	    		
		    		if (line.length() != 0 && line.charAt(line.length() - 1) == '(') {
		    			assembling = true;
		    		}
		    		
		    		if (assembling) {
		    			if (line.length() == 0) {
		    				assembling = false;
		    				stmt.executeUpdate(builder.toString());
		    				builder = new StringBuilder();
		    			}
		    			else {
			    			builder.append(line);
			    			builder.append('\n');
		    			}
		    		}
		    		else {
		    			if (line.contains("INSERT") && line.contains("\\")) {
		    				if (line.contains("wn_gloss")) {
		    					line = line.replace("\\", "");
		    				}
		    				else if (line.contains("wn_synset")) {
		    					String pre = line.substring(0, 44);
		    					String word = line.substring(45, line.length() - 11);
		    					String post = line.substring(line.length() - 10);
		    					word = "\"" + line.substring(45, line.length() - 11).replace("\\", "") + "\"";
		    					line = pre + word + post;
		    				}
			    		}
		    			stmt.executeUpdate(line);
		    		}
	    		
	    	}
	    	br.close();
	    	
	    	// Create the tables
	    	/*String wordsTable = "CREATE TABLE words("
	    			+ "id	INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "text	TEXT	NOT NULL)";
	    	
	    	String defsTable = "CREATE TABLE definitions("
	    			+ "id		INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "word_id	INTEGER	NOT NULL, "
	    			+ "text		TEXT	NOT NULL, "
	    			+ "FOREIGN KEY(word_id) REFERENCES words(id))";
	    	
	    	String pronsTable = "CREATE TABLE pronounciations("
	    			+ "id		INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "word_id	INTEGER NOT NULL, "
	    			+ "text		TEXT	NOT NULL, "
	    			+ "FOREIGN KEY(word_id) REFERENCES words(id))";
	    	
	    	stmt.executeUpdate(wordsTable);
	    	stmt.executeUpdate(defsTable);
	    	stmt.executeUpdate(pronsTable);
	    	*/
	    	
		    // Close up shop
	    	stmt.close();
	    	c.close();
	    	
	    	System.out.println("Tables created successfully");
	    	
	    } catch ( Exception e ) {
	    	c.close();
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
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
