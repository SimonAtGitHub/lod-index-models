package io;



public class NQuad implements Comparable<NQuad> {

	/**
	 * Subject of the represented triple. Can be a URI or a blank node.
	 */
	public String subject = null;
	/**
	 * Predicate of the represented triple. Must be a URI.
	 */
	public String predicate = null;
	/**
	 * Object of the represented triple. Can be a URI or a blank node or a literal value.
	 */
	public String object = null;
	/**
	 * Context of the represented triple. Must be a URI.
	 */
	public String context = null;

	
	/**
	 * Parses a String for reading a triple and its context in nquad format.
	 * 
	 * @param line Line to parse from
	 * @return NQuad object obtained in the parsing process
	 */
	public static NQuad fromString(String line) {
		NQuad result = new NQuad();
		line = line.trim();
		// pop subject
		if (line.startsWith("<")) {
			// uri
			result.subject = NQuad.popUri(line);
		} else if (line.startsWith("_:")) {
			// blank node
			result.subject = NQuad.popBNode(line);
		} else  {
			// Hu?
			System.err.println("unknown subject type");
		}
		line = line.substring(result.subject.length()).trim();
		// pop predicate
		if (line.startsWith("<")) {
			// uri
			result.predicate = NQuad.popUri(line);
		} else  {
			// Hu?
			System.err.println("unknown predicate type");
		}
		line = line.substring(result.predicate.length()).trim();
		// pop object
		if (line.startsWith("<")) {
			// uri
			result.object = NQuad.popUri(line);
		} else if (line.startsWith("_:")) {
			// blank node
			result.object = NQuad.popBNode(line);
		} else if (line.startsWith("\"")) {
			// literal
			result.object = NQuad.popLiteral(line);
		} else  {
			// Hu?
			System.err.println("unknown object type");
		}
		line = line.substring(result.object.length()).trim();
		// pop context
		if (line.startsWith("<")) {
			// uri
			result.context = NQuad.popUri(line);
		} else if (line.startsWith("_:")) {
			// blank node
			result.context = NQuad.popBNode(line);
		} else if (line.startsWith("\"")) {
			// literal
			result.context = NQuad.popLiteral(line);
		} else  {
			// Hu?
			System.err.println("unknown context type");
		}
		line = line.substring(result.context.length()).trim();
		if (! line.equals(".")) {
			// Hu?
			System.err.println("wrong line end / no \".\"");
		}
		return result;
	}
	
	/**
	 * Subroutine for reading an URI from a String (encapsulated in < and >)
	 * 
	 * @param input String to read URI from
	 * @return URI read from the beginning of the String
	 */
	private static String popUri(String input) {
		String result = null;
		if (input.charAt(0) == '<') {
			int pos = input.indexOf(">");
			if (pos > 0) {
				result = input.substring(0,pos+1);
			} else {
				result = null;
			}
		}
		return result;
	}
	
	/**
	 * Subroutine for reading a blank node from a String (starting with _:)
	 * 
	 * @param input String to read blank node from
	 * @return blank node read from the beginning of the String
	 */
	private static String popBNode(String input) {
		String result = null;
		if (input.startsWith("_:")) {
			String[] parts = input.split("\\s",2);
			result = parts[0];
		}
		return result;
	}
	
	/**
	 * Subroutine for reading a literal value from a String (encapsulated in "). Methods keeps track of escaped symbols
	 * 
	 * @param input String to read literal from
	 * @return literal value read from beginning of the String
	 */
	private static String popLiteral(String input) {
		String result = null;
		if (input.charAt(0) == '"') {
			int pos = 0;
			boolean escaped = false;
			do {
				pos = input.indexOf("\"",pos+1);
				if (pos > 1) {
					escaped = input.charAt(pos-1) == '\\';
					if (escaped) {
						int rew = pos-1;
						while (rew>0 && input.charAt(rew) == '\\') {
							rew--;
						}
						// check for odd number of escape symbols
						escaped = (pos-1-rew) % 2 == 1;
					}
				}
			} while (escaped && pos > 0);
			if (pos > 0) {
				result = input.substring(0,pos+1);
				if (input.length()>pos) {
					if (input.charAt(pos+1)=='@') {
						input = input.substring(pos+1);
						String[] parts = input.split("\\s",2);
						result += parts[0];
					} else if (input.charAt(pos+1)=='^') {
						if (input.charAt(pos+2)=='^') {
							// get uriRef
							input = input.substring(pos+3);
							String uri = NQuad.popUri(input);
							result += "^^"+uri;
						}
					}
				}
			} else {
				result = null;
			}
		}
		return result;
	}

	/**
	 * Creates a tab-separated String representation of the tripel and context in nquad format (i.e. ending in .)
	 */
	public String toString() {
//		String result = this.subject + "\t" + this.predicate + "\t" + this.object + "\t" + this.context+" . ";
		String result = this.subject + " " + this.predicate + " " + this.object + " " + this.context+" .";
		return result;
	}

	/**
	 * Comparison based on first the context, then the subject, predicate and object. On each level comparison is implemented on a string level. A sorting based on this comparison leads to nquads to be sorted first by context, then by subject, predicate and object.  
	 */
	@Override
	public int compareTo(NQuad other) {
		int result = 0;
		result = this.context.compareTo(other.context);
		if (result == 0) {
			result = this.subject.compareTo(other.subject);
			if (result == 0) {
				result = this.predicate.compareTo(other.predicate);
				if (result == 0) {
					result = this.object.compareTo(other.object);
				}
			}
		}
		return result;
	}


}
