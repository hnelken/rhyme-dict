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

	    	// Create the tables
	    	String wordsTable = "CREATE TABLE words("
	    			+ "id	INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "text	TEXT	NOT NULL)";
	    	
	    	String defsTable = "CREATE TABLE definitions("
	    			+ "id		INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "word_id	INTEGER	NOT NULL, "
	    			+ "text		TEXT	NOT NULL, "
	    			+ "FOREIGN KEY(word_id) REFERENCES words(id))";
	    	
	    	String pronsTable = "CREATE TABLE pronounces("
	    			+ "id		INTEGER	PRIMARY KEY	NOT NULL, "
	    			+ "word_id	INTEGER NOT NULL, "
	    			+ "def_id	INTEGER	NOT NULL, "
	    			+ "text		TEXT	NOT NULL, "
	    			+ "FOREIGN KEY(word_id) REFERENCES words(id), "
	    			+ "FOREIGN KEY(def_id) REFERENCES definitions(id))";
	    	
	    	stmt.executeUpdate(wordsTable);
	    	stmt.executeUpdate(defsTable);
	    	stmt.executeUpdate(pronsTable);
	    	
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
}
