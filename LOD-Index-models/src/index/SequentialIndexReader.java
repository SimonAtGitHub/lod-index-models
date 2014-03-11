package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SequentialIndexReader {
	
	private BufferedReader in = null;
	private boolean EOF = false;
	
	
	public SequentialIndexReader(File indexFile) throws FileNotFoundException {
		this.in = new BufferedReader(new FileReader(indexFile));
	}
	
	public KeyValueEntry nextEntry() {
		KeyValueEntry result = null;
		if (! EOF) {
			try {
				String line = in.readLine();
				if (line == null) {
					EOF = true;
				} else {
					result = KeyValueEntry.fromString(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return result;
	}
	
	public void close() {
		try {
			this.in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
