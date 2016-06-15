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
		    			if (line.contains("INSERT")) {
		    				if (line.contains("wn_gloss") && line.contains("\\")) {
	    						line = line.replace("\\", "");
		    				}
		    				else if (line.contains("wn_synset")) {
		    					String pre = null;
		    					String word = null;
		    					if (line.charAt(43) == ',') {
			    					pre = line.substring(0, 44);
			    					word = line.substring(45);
		    					}
		    					else {
		    						pre = line.substring(0, 45);
		    						word = line.substring(46);
		    					}

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
		    					word = "\"" + word.substring(0, endWord).replace("\\", "") + "\"";
		    					
		    					line = pre + word + ");";
		    					//System.out.println(line);
		    				}
			    		}

		    			// Execute the SQL statement
		    			stmt.executeUpdate(line);
		    		}
	    	}
	    	
		    // Close up shop
	    	br.close();
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
