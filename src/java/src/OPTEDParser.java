import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

public class OPTEDParser {

	public static void main(String... args) throws Exception {
	
		OPTEDParser opted = new OPTEDParser();
	    LineHandler handler = opted.new LineHandler();
	    SAXParserImpl parser = SAXParserImpl.newInstance(null);
	    
	    // Parse the dictionary
	    parser.parse(
	        new FileInputStream("lib/opted/wb1913_a.html"),
	        handler);
	    
    }
	
	/**
	 * A class for parsing the plain-text dictionary.
	 * Parses each line for the word and definition and
	 * commits these to the database, checking for duplicates.
	 */
	private class LineHandler extends DefaultHandler {
	
		private int count = 100;
		private String word = null;
		private String definition = null;
    	private StringBuilder builder;
    	
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
    		System.out.println("Word: " + word + "\nDefinition: " + definition + '\n');
    		// Check DB if word is contained
    		// If yes:
    			// Add definition only
    		// If no:
    			// Add full new word entry
    		if (count-- == 0) {
    			System.exit(0);
    		}
    	}
	}
}
