public class Line {

	private String pre;
	private String word;
	
	public Line(String line) {
		
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
		word = word.toUpperCase();
	}

	public String getWord() {
		return word;
	}
	
	public String getSQL() {
		return pre + "\"" + word + "\");";
	}
	
	public int getSynsetID() {
		// Separate this words synset ID from the SQL insert
		String[] parts = pre.split(",");
		String id = parts[0].substring(parts[0].length() - 9);
		return Integer.parseInt(id);
	}
}
