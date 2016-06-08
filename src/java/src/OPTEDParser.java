import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import java.io.FileInputStream;
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
		    	System.out.println("...parsing letter '" + index + "'");
		    	parser.parse(
		    			new FileInputStream("lib/opted/wb1913_" + index + ".html"), 
		    			handler);
		    	index++;
		    }
		    System.out.println("...parsing recent additions");
		    parser.parse(
	    			new FileInputStream("lib/opted/wb1913_new.html"), 
	    			handler);
		    
		    // Close the DB connection
	    	c.close();
	    	System.out.println("...done.");
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
	    }
    }
}
