package de.unikoblenz.west.ldim.index.aggregator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.unikoblenz.west.ldim.index.KeyValueEntry;

/**
 * Index aggregators take data from an index file and aggregate it into a
 * secondary form. This abstract base class takes care of reading from the input
 * file (uncompressed) and write the aggregated information to a zip compressed
 * output file.
 * 
 * The actual aggregation needs to be done by sub-classes. These are notified
 * about an initialization step, each individual key-value pair and a
 * finalization step.
 * 
 * This class reads the key-value pairs in the order as they are provided in the
 * input file.
 * 
 * @author Thomas Gottron
 * 
 */
public abstract class IndexAggregator {

	/**
	 * Method called internally before reading the first key-value pair. Can be
	 * used to setup some initial data structures or to flush some preliminary
	 * information to the output file.
	 * 
	 * @param out
	 *            PrintStream to which data can be written.
	 */
	protected abstract void initialize(PrintStream out);

	/**
	 * Passes a single key-value pair as read from the input. The implementing
	 * class may or may not react by writing data to the output.
	 * 
	 * @param entry
	 *            single key-value pair
	 * @param out
	 *            PrintStream to which data can be written.
	 */
	protected abstract void notify(KeyValueEntry entry, PrintStream out);

	/**
	 * Method is called after the last key-value pair has been read. Can be used
	 * to flush last pending data to the output file or to write some summary.
	 * 
	 * @param out   PrintStream to which data can be written.
	 */
	protected abstract void finalize(PrintStream out);

	public void run(File indexFile, File aggregationFile) {
		try {
			// Todo: replace the direct access via a buffered reader to the
			// input file with a SequentialIndexReader
			BufferedReader reader = new BufferedReader(
					new FileReader(indexFile));
			FileOutputStream fos = new FileOutputStream(aggregationFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			zos.putNextEntry(new ZipEntry("data.txt"));
			PrintStream out = new PrintStream(zos);
			this.initialize(out);
			String line = null;
			while ((line = reader.readLine()) != null) {
				KeyValueEntry entry = KeyValueEntry.fromString(line);
				this.notify(entry, out);
			}
			reader.close();
			this.finalize(out);
			zos.closeEntry();
			zos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

}
