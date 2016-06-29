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
	    			executeLine(line, stmt, c);
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
	private static void executeLine(String line, Statement stmt, Connection c) throws Exception {
		Line cleanLine = null;
		// Insert lines take special attention
		if (line.contains("INSERT")) {
			if (line.contains("wn_gloss") && line.contains("\\")) {
				// Remove escape characters from the definitions
				line = line.replace("\\", "");
			}
			else if (line.contains("wn_synset")) {
				// Clean the word inserts thoroughly
				cleanLine = new Line(line);	//cleanSQLWordInsert(line, c);
				line = cleanLine.getSQL();
			}
		}

		// Execute the SQL statement
		try {
			stmt.executeUpdate(line);
		}
		catch (SQLException ex) {
	    	if (ex.getMessage().contains("UNIQUE")) {
	    		//System.out.println("rerouting: " + cleanLine.getWord());
	    		rerouteWord(cleanLine, c);
	    	}
	    	else {
		    	System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
		    	System.exit(0);	
	    	}
		}
	}
	
	// Reroute the definitions of previously entered words so words are unique in the DB
	private static void rerouteWord(Line line, Connection c) throws Exception {
		
		// Check if word already exists
		String sql = "SELECT * FROM wn_synset WHERE word=? GROUP BY word";
		PreparedStatement query = c.prepareStatement(sql);
		query.setString(1, line.getWord());
		ResultSet results = query.executeQuery();
		
		// Words are unique, one result at most
		if (results.next()) {
			// Get existing words id to reroute definition
			int rerouteID = results.getInt("synset_id");
			
			// Insert a row that reroutes this word's definition to the existing word
			sql = "INSERT INTO mult_def(synset_id, rrt_id) VALUES(?, ?)";
			PreparedStatement insert = c.prepareStatement(sql);
			insert.setInt(1, line.getSynsetID());
			insert.setInt(2, rerouteID);
			insert.executeUpdate();
		}
	}
}
