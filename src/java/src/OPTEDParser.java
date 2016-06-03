import java.io.FileInputStream;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

public class OPTEDParser {

	public static void main(String... args) throws Exception {
		
		// print the 'src' attributes of <img> tags
	    // from http://www.yahoo.com/
	    // using the TagSoup parser
	
		OPTEDParser opted = new OPTEDParser();
	    SAXParserImpl parser = SAXParserImpl.newInstance(null);
	    
	    parser.parse(
	        new FileInputStream("dictionary filename here"),
	        new DefaultHandler() {

	        	String line;
	        	StringBuilder builder;
	        	
	        	public void startElement(String uri, String localName,
	                    String name, Attributes a) {
	        		if (name.equalsIgnoreCase("p")) {
	        			builder = new StringBuilder();
	        		}
	        	}
	        	
	        	public void characters(char[] ch, int start, int length) {
	        		builder.append(new String(ch, start, length));
	        	}

	        	public void endElement(String uri, String localName, String name) {
	        		if (name.equalsIgnoreCase("p")) {
	        			line = builder.toString();

	        			LineHandler handler = opted.new LineHandler(line);
	        			InputStream stream = 
	        			parser.parse()
	        		}
	        	}
	        });
    
	    
    }
	
	private class LineHandler extends DefaultHandler {
	
		private String line = null;
		private String word = null;
		private String definition = null;
    	private boolean wordFound = false;
    	private StringBuilder builder;
    	
    	public LineHandler(String line) {
    		this.line = line;
    	}
    	
    	public String getWord() {
    		return word;
    	}
    	
    	public String getDefinition() {
    		return definition;
    	}
    	
		public void startElement(String uri, String localName,
                String name, Attributes a) {
    		if (name.equalsIgnoreCase("b")) {
    			builder = new StringBuilder();
    		}
    	}
    	
    	public void characters(char[] ch, int start, int length) {
    		
    		if (!wordFound) {
    			// append any characters to the string builder
    			builder.append(new String(ch, start, length));
    		}
    	}

    	public void endElement(String uri, String localName, String name) {
    		if (name.equalsIgnoreCase("b")) {
    			word = builder.toString();
    			wordFound = true;
    		}
    	}
		
	}
}
