package index.aggregator;


import index.IndexWriter;
import index.KeyValueEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class IndexAggregator {
	
	protected abstract void initialize(PrintStream out);

	protected abstract void notify(KeyValueEntry entry, PrintStream out);
	
	protected abstract void finalize(PrintStream out);
	
	public void run(File indexFile, File aggregationFile) {
		try{
			BufferedReader reader = new BufferedReader(new FileReader(indexFile));
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
//			out.close();
			zos.closeEntry();
			zos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
}
