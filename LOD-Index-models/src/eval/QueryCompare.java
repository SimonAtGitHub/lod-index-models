package eval;

import index.IndexWriter;
import index.aggregator.InvertedIndexAggregator;
import io.ZipBufferedReaderHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import tools.Utilities;

public class QueryCompare {
	
//	public double macroRecall = 0;
//	public double macroPrecision = 0;
//	public double macroF1 = 0;
	public double sumRecall = 0;
	public double sumPrecision = 0;
	public int queryCountNew = 0;
	public long microMatchSetCount = 0;
	public long microGoldSetCount = 0;
	public long microRetrieveSetCount = 0;
	public int missedEntries = 0;

	
	public double macroRecall() {
		return sumRecall / queryCountNew;
	}
	
	public double macroPrecision() {
		return sumPrecision / queryCountNew;
	}
	
	public double macroF1() {
		return this.f1(this.macroRecall(), this.macroPrecision());
	}
	
	public double f1 (double r, double p) {
		return (2*r*p)/(r+p);
	}
	
	public double microRecall() {
		return ( 1.0*microMatchSetCount) / microGoldSetCount;
	}

	public double microPrecision() {
		return ( 1.0*microMatchSetCount) / microRetrieveSetCount;
	}
	
	public double microF1() {
		return this.f1(this.microRecall(), this.microPrecision());
	}
	public void compare (String baseIndex, String newIndex) throws IOException {
		ZipBufferedReaderHandler zBaseIn = new ZipBufferedReaderHandler(new File(baseIndex));
		ZipBufferedReaderHandler zNewIn = new ZipBufferedReaderHandler(new File(newIndex));
		BufferedReader baseIn = zBaseIn.getBufferedReader();
		BufferedReader newIn = zNewIn.getBufferedReader();
		String baseKey = null;
		String newKey = null;
		baseKey = QueryCompare.readKey(baseIn);
		newKey = QueryCompare.readKey(newIn);
		// now comes the tricky part
		boolean endOfBase = (baseKey == null);
		boolean endOfNew = (newKey == null);
		while (! endOfNew) {
			// outer loop : fetch keys until matching
			if ( (!endOfBase) && (baseKey.compareTo(newKey) < 0)) {
				// catch up with indices in base
				QueryCompare.skipRemainingEntries(baseIn);
				try {
					baseKey = QueryCompare.readKey(baseIn);
					endOfBase = (baseKey == null);
				} catch (IOException ioe) {
					endOfBase = true;
				}
			} else if ( (endOfBase) || (baseKey.compareTo(newKey) > 0)) {
				// missing entry in base -- add a 0 recall entry
				this.queryCountNew++;
				this.missedEntries++;
				String entry = null;
				int listLength = 0;
				do {
					entry = QueryCompare.readNextEntry(newIn);
					listLength++;
				} while (! entry.equals(InvertedIndexAggregator.EOL_TOKEN));
				// subtract last item as it was the identifier for the end of the list
				listLength--;
				microGoldSetCount+=listLength;
				// Add a precision of one to make sure the division for the average works correctly in the end (we count no result as hihgly precise)
				sumPrecision += 1;
				try {
					newKey = QueryCompare.readKey(newIn);
					endOfNew = (newKey == null);
				} catch (IOException ioe) {
					endOfNew = true;
				}
			} else {
				// Match of keys !!! Count the same entries
				// inner loop
				this.queryCountNew++;
				String baseEntry = QueryCompare.readNextEntry(baseIn);
				String newEntry = QueryCompare.readNextEntry(newIn);
				int baseListLength = 1;
				int newListLength = 1;
				int matchCount = 0;
				while ( (!baseEntry.equals(InvertedIndexAggregator.EOL_TOKEN)) || (!newEntry.equals(InvertedIndexAggregator.EOL_TOKEN) )) {
					if (entryCompare(baseEntry, newEntry) < 0) {
						if ( !baseEntry.equals(InvertedIndexAggregator.EOL_TOKEN) ) {
							baseListLength++;
							baseEntry = QueryCompare.readNextEntry(baseIn);
						}
					} else if (entryCompare(baseEntry, newEntry) > 0) {
						if ( !newEntry.equals(InvertedIndexAggregator.EOL_TOKEN) ) {
							newListLength++;
							newEntry = QueryCompare.readNextEntry(newIn);
						}						
					} else {
						// match!
						matchCount++;
						if ( !baseEntry.equals(InvertedIndexAggregator.EOL_TOKEN) ) {
							baseListLength++;
							baseEntry = QueryCompare.readNextEntry(baseIn);
						}
						if ( !newEntry.equals(InvertedIndexAggregator.EOL_TOKEN) ) {
							newListLength++;
							newEntry = QueryCompare.readNextEntry(newIn);
						}						
					}
				}
				// subtract EOL items from listlength
				baseListLength--;
				newListLength--;
				
				double recall = (1.0 * matchCount) / newListLength;
				double precision = (1.0 * matchCount) / baseListLength;
				sumRecall += recall;
				sumPrecision += precision;
				// update micro stats
				microGoldSetCount+=newListLength;
				microMatchSetCount+=matchCount;
				microRetrieveSetCount+=baseListLength;
				try {
					newKey = QueryCompare.readKey(newIn);
					endOfNew = (newKey == null);
				} catch (IOException ioe) {
					endOfNew = true;
				}
				try {
					baseKey = QueryCompare.readKey(baseIn);
					endOfBase = (baseKey == null);
				} catch (IOException ioe) {
					endOfBase = true;
				}
			}
		}
		zBaseIn.close();
		zNewIn.close();
	}
	
	public static String readKey(BufferedReader in) throws IOException {
		StringBuffer buffer = new StringBuffer();
		do {
			int i = in.read();
			if (i == -1) {
				return null;
			}
			char c = (char) i;
			buffer.append(c);
		} while (!buffer.toString().endsWith(IndexWriter.SEPARATOR));
		
		String result = buffer.toString();
		result = result.substring(0,result.length() -IndexWriter.SEPARATOR.length());
		return result;
	}
	
	public static String readNextEntry(BufferedReader in) throws IOException {
		StringBuffer buffer = new StringBuffer();
		char c;
		do {
			int i = in.read();
			if (i == -1) {
				return null;
			}
			c = (char) i;
			buffer.append(c);
		} while (! (c == '\n' || c == Utilities.CONCATENATION_TOKEN) );
		String result = buffer.toString();
		result = result.substring(0,result.length() -1);
		return result;
	}
	
	public static void skipRemainingEntries(BufferedReader in) throws IOException {
		in.readLine();
	}
	
	public static int entryCompare(String entry1, String entry2) {
		int result = 0;
		if (! entry1.equals(InvertedIndexAggregator.EOL_TOKEN)) {
			if (! entry2.equals(InvertedIndexAggregator.EOL_TOKEN)) {
				result = entry1.compareTo(entry2);
			} else {
				result = -1;
			}
		} else {
			if (! entry2.equals(InvertedIndexAggregator.EOL_TOKEN)) {
				result = 1;
			} else {
				result = 0;
			}
		}
		return result;
	}
	
}
