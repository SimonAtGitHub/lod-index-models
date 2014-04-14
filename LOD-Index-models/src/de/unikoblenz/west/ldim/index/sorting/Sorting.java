package de.unikoblenz.west.ldim.index.sorting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import de.unikoblenz.west.ldim.io.BufferedReaderHandler;
import de.unikoblenz.west.ldim.io.PrintStreamHandler;

/**
 * This abstract class provides a general (merge) sorting functionality for large files.
 * In the large files it assumes data items to be stored one per line.
 * It loads batches of data, sorts the entire batch and stores it to a temporary file.
 * Then, in a second step, the procedure merges all the temporary files into one single result file.
 * The last step is applied repeatedly if it is not possible to merge all temporary files in one step.
 * 
 * The methods in this abstract base class provide all the functionality needed to sort the data items, create, manage and merge the temporary files.
 * A subclass needs to provide functionality on how render a line into a concrete data object and vice versa, how to compare data object and how to manage input and output streams.
 * 
 * 
 * @author Thomas Gottron
 *
 * @param <T> The type of data items which are being sorted
 */
public abstract class Sorting<T> {

	/**
	 * Suffix used for temporary files.
	 */
	public final static String TMP_FILE_SUFFIX = ".tmp";
	/**
	 * Prefix used for the temporary files in which batches of sorted data are persisted
	 */
	public final static String TMP_SORT_FILE_PREFIX = "sort-";
	/**
	 * Prefix used for the temporary files used for merging the results
	 */
	public final static String TMP_MERGE_FILE_PREFIX = "merge-";
	
	/**
	 * This methods provides a Comparator object used to compare the data items. 
	 * Essentially this comparator dictates the overall sorting.
	 *  
	 * @return Comparator defining the sorting order of data items
	 */
	protected abstract Comparator<T> getComparator(); 
	
	/**
	 * Provides a {@link BufferedReaderHandler} for a given file.
	 * The idea behind this construct is to transparently allow for using different encoding schemes based on the filenames.
	 * A typical example would be to identify when to read from a zip-file or a an uncompressed file.
	 * The sorting methods will always access files via these handlers and the {@link BufferedReader} they provide.
	 * See {@link BufferedReaderHandler} for details on BufferedReaderHandlers.
	 *  
	 * @param inputFile File for which to generate a {@link BufferedReaderHandler}
	 * @return BufferedReaderHandler giving a transparent {@link BufferedReader} access to the file
	 * @throws IOException
	 */
	protected abstract BufferedReaderHandler getBufferedReaderHandler(File inputFile) throws IOException;
	
	/**
	 * Provides a {@link PrintStreamHandler} for a given file.
	 * The idea behind this construct is to transparently allow for using different encoding schemes based on the filenames.
	 * A typical example would be to identify when to write to a zip-file or a an uncompressed file.
	 * The sorting methods will always write to files via these handlers and the {@link PrintStream} they provide.
	 * See {@link PrintStreamHandler} for details on PrintStreamHandlers.
	 * 
	 * @param outputFile File for which to generate a {@link PrintStreamHandler}
	 * @return PrintStreamHandler giving a transparent {@link PrintStream} access to the file
	 * @throws IOException
	 */
	protected abstract PrintStreamHandler getPrintStreamHandler(File outputFile) throws IOException;
	
	/**
	 * Decodes a data item from a single line read from the input files.
	 * In this way the general sorting methods can be ignorant about the String serialization used for encoding data items.
	 * All data items are read using this method.
	 * 
	 * @param line the String from which to construct a data item
	 * @return Data item
	 */
	protected abstract T fromLine(String line);
	
	/**
	 * Encodes a data item into a single line which can be written to an output file.
	 * In this way the general sorting methods can be ignorant about the String serialization used for encoding data items.
	 * All data items are written using this method.
	 * 
	 * @param object Data item to serialize into a String
	 * @return String serialization of the data item
	 */
	protected abstract String toLine(T object); 
	
	/**
	 * This class encapsulates a {@link Comparator} for data items to operate on pairs of objects and a reader ({@link ObjectReaderTuple}) from which to obtain more objects.
	 * It is simply a helper class to implement a priority queue of data items, in which it is possible to poll the top data item and read the next data item from the same source.
	 * 
	 * @author Thomas Gottron
	 *
	 */
	public class ObjectReaderTupleComparator implements Comparator<ObjectReaderTuple<T>> {

		/**
		 * Internal reference to the Comparator used for comparing the actual data items.
		 */
		private Comparator<T> internalComparator = null;
		
		/**
		 * The class is initialized with a Comparator which will be used for comparing data items.
		 * @param baseComparator
		 */
		public ObjectReaderTupleComparator(Comparator<T> baseComparator) {
			this.internalComparator = baseComparator;
		}
		
		/**
		 * Comparison of {@link ObjectReaderTuple} is actually based solely on the data items stored in the tuples
		 */
		@Override
		public int compare(ObjectReaderTuple<T> arg0, ObjectReaderTuple<T> arg1) {
			return this.internalComparator.compare(arg0.getObject(), arg1.getObject());
		}
	}

	/**
	 * This helper class groups together a data item and the source from which it was read.
	 * It is used to implement a priority queue of data items, in which it is possible to poll the top data item and read the next data item from the same source.
	 * 
	 * @author Thomas Gottron
	 *
	 * @param <S>  The type of data items which are being being read from the source
	 */
	public class ObjectReaderTuple<S extends T> {
		
		/**
		 * Internal reference to the {@link BufferedReaderHandler} from which the current data item has been read.
		 */
		private BufferedReaderHandler sourceHandler = null;
		/**
		 * The actual data item
		 */
		private S dataItem = null;
		/**
		 * A reference to the file on which the sourceHandler is operating.
		 * This reference is used to be able to delete temporary files after they have been completely processed and merged into a final, sorted file.
		 */
		private File input = null;

		/**
		 * The object is initialized with data item, the source from which more data items can be read and the file which corresponds to this source.
		 * 
		 * @param item The data item
		 * @param sourceReader source from which to read more data items
		 * @param file File from which the source is reading
		 * @throws FileNotFoundException
		 */
		public ObjectReaderTuple(S item, BufferedReaderHandler sourceReader, File file) throws FileNotFoundException {
			this.dataItem = item;
			this.input = file;
			this.sourceHandler = sourceReader;
		}
		
		/**
		 * Provides the source ({@link BufferedReaderHandler}) from which the next data item can be read.
		 * @return {@link BufferedReaderHandler} from which more data items can be read.
		 */
		public BufferedReaderHandler getSource() {
			return this.sourceHandler;
		}

		/**
		 * Provides the {@link File} from which the source is reading data items
		 * @return {@link File} behind the source.
		 */
		public File getFile() {
			return this.input;
		}
		
		/**
		 * Gives the actual data item.
		 * @return The data item
		 */
		public T getObject() {
			return this.dataItem;
		}
	}
	
	
	/**
	 * Size of the buffer used for caching data items.
	 * This buffer indicates how many data items are stored in main memory and are flushed to each individual temporary file
	 * 
	 */
	private final long bufferSize = 5000000;

	/**
	 * Number of files which will be opened at most in parallel for merging temporary files.
	 * This number should definitely be smaller than bufferSize, as it  indicates that also as many data items are considered at the same time when merging the temporary files.
	 */
	private int maxOpenFiles = 100;

	
	/**
	 * This method performs the actual merge sort operation.
	 * Data items are read from an input file and are finally written to an output file in a sorted way.
	 * 
	 * @param input Input file from which to read the data items
	 * @param output Output file to which write the sorted data items
	 */
	public void run(File input, File output) {
		File inputFile = input;
		File outputFile = output;
		Comparator<T> comparator = this.getComparator();
		// The file counter is used to enumerate the temporary files
		int fileCounter = 1;
		// create the first temporary file
		File tmp = new File(TMP_SORT_FILE_PREFIX+fileCounter+TMP_FILE_SUFFIX);
		// the queue used for mergeing the final files
		ArrayBlockingQueue<File> mergeFiles = new ArrayBlockingQueue<File>(this.maxOpenFiles);
		mergeFiles.add(tmp);
		try {
			// Sort into blocks of buffersize
			PrintStreamHandler outHandler = this.getPrintStreamHandler(tmp);
			PrintStream writerOut = outHandler.getPrintStream();
			BufferedReaderHandler inHandler = this.getBufferedReaderHandler(inputFile);
			BufferedReader readerIn = inHandler.getBufferedReader();
			TreeSet<T> buffer = new TreeSet<T>(comparator);
			String line = null;
//			int cnt = 0;
			while ( (line = readerIn.readLine()) != null) {
				T object = this.fromLine(line);
//				cnt++;
//				if (cnt%100000 == 0) {
//					System.out.print(".");
//					if (cnt%2000000 == 0) {
//						System.out.println(cnt);
//					}
//				}
				buffer.add(object);
				if (buffer.size() == this.bufferSize) {
					// buffer full - flush to file and clean buffer
					for (T objectOut : buffer) {
						writerOut.println(this.toLine(objectOut));
					}
					outHandler.close();
					fileCounter++;
					buffer = new TreeSet<T>(comparator);
					tmp = new File(TMP_SORT_FILE_PREFIX+fileCounter+TMP_FILE_SUFFIX);
					mergeFiles.add(tmp);
					outHandler = this.getPrintStreamHandler(tmp);
					writerOut = outHandler.getPrintStream();
				}
			}
			readerIn.close();
			// Flush rest of buffer to tmp file
			if (buffer.size()>0) {
				for (T objectOut : buffer) {
					writerOut.println(this.toLine(objectOut));
				}
				writerOut.close();
			}
			// Start merging the presorted files
			while (mergeFiles.size() > 1) {
				fileCounter++;
				tmp = new File(TMP_MERGE_FILE_PREFIX+fileCounter+TMP_FILE_SUFFIX);
				PrintStreamHandler aggregateHandler = this.getPrintStreamHandler(tmp);
				PrintStream aggregateWriter = aggregateHandler.getPrintStream();
				// Initialize priority queue
				PriorityQueue<ObjectReaderTuple<T>> queue = new PriorityQueue<ObjectReaderTuple<T>>(this.maxOpenFiles, new ObjectReaderTupleComparator(comparator));
				while ( (queue.size()< this.maxOpenFiles) && (mergeFiles.size()>0)) {
					File nextFile = mergeFiles.poll();
					BufferedReaderHandler inAggregationHandler = this.getBufferedReaderHandler(nextFile);
					if ((line = inAggregationHandler.getBufferedReader().readLine()) != null) {
						T object = this.fromLine(line);
						ObjectReaderTuple<T> tuple = new ObjectReaderTuple<T>(object,inAggregationHandler,nextFile);
						queue.add(tuple);
					}
				}
				// Starting processing and writing;
				while (queue.size()>0) {
					ObjectReaderTuple<T> nextTuple = queue.poll();
					aggregateWriter.println(this.toLine(nextTuple.getObject()));
					if ( (line = nextTuple.getSource().getBufferedReader().readLine()) != null) {
						T object = this.fromLine(line);
						ObjectReaderTuple<T> tuple = new ObjectReaderTuple<T>(object,nextTuple.getSource(),nextTuple.getFile());
						queue.add(tuple);
					} else {
						nextTuple.getSource().close();
						nextTuple.getFile().delete();
					}
				}
				aggregateHandler.close();
				mergeFiles.add(tmp);
			}
			File last = mergeFiles.poll();
			// simply rename the last merge file into the output file.
			// !!!! Careful: a cleaner approach would be to open a new printOutputStream and transcribe the data. Only in this way we can be sure the right output stream, encoding is used 
			last.renameTo(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
