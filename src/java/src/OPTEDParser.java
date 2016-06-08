import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import java.io.FileInputStream;
import java.sql.*;

public class OPTEDParser {

	public static void main(String... args) throws Exception {
	
		Connection c = null;
		
	    try {
	    	// Open the DB connection
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
	    	System.out.println("DB Connection Successful");
	    	
	    	// Setup for parsing
		    LineHandler handler = new LineHandler(c);
		    SAXParserImpl parser = SAXParserImpl.newInstance(null);
		    
		    // Parse the dictionary
		    char index = 'a';
		    while (index <= 'z'){
		    	parser.parse(
		    			new FileInputStream("lib/opted/wb1913_" + index + ".html"), 
		    			handler);
		    	index++;
		    }
		    parser.parse(
	    			new FileInputStream("lib/opted/wb1913_new.html"), 
	    			handler);
		    
		    // Close the DB connection
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	c.close();
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);	
	    }
    }
}
