package de.unikoblenz.west.ldim.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class RandomAccessIndexReader {

	public final static String MISSING = "MISS";

	private File indexFile = null;
	private Environment myDbEnvironment = null;
	private EntityStore dictStore = null;
	private PrimaryIndex<String, KeyValueEntry> pidx = null;
	
	
	public RandomAccessIndexReader(File indexFile, File dbDir) throws FileNotFoundException {
		this.indexFile= indexFile;
		this.setup(dbDir);
	}

	public String get(String key) {
		KeyValueEntry pair = this.pidx.get(key);
		if (pair == null) {
			return RandomAccessIndexReader.MISSING; 
		}
		return pair.value;
	}
	
	
	public void close() {
		try {
			if (this.myDbEnvironment != null) {
				this.myDbEnvironment.cleanLog(); // Clean the log before closing
				dictStore.close();
				this.myDbEnvironment.close();
			}

			this.myDbEnvironment = null;
			this.dictStore = null;
			this.pidx = null;
		} catch (DatabaseException dbe) {
		}

	}

	public void setup(File dbDir) {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			StoreConfig storeConfig = new StoreConfig();
			envConfig.setAllowCreate(true);
			envConfig.setCachePercent(80);
			storeConfig.setAllowCreate(true);
			this.myDbEnvironment = new Environment(dbDir,
					envConfig);
			this.dictStore = new EntityStore(myDbEnvironment, "EntityStore", storeConfig);
			this.pidx = dictStore.getPrimaryIndex(String.class, KeyValueEntry.class);

			if (this.pidx.count()==0) {
				// prep the index
				try{
					BufferedReader reader = new BufferedReader(new FileReader(this.indexFile));
					String line = null;
					while ((line = reader.readLine()) != null) {
//						String[] result = line.split(IndexWriter.SEPARATOR,2);
//						KeyValueEntry kvp = new KeyValueEntry(result[0], result[1]);
						KeyValueEntry kvp = KeyValueEntry.fromString(line);
						this.pidx.put(kvp);
					}
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
		} catch (DatabaseException dbe) {
			this.myDbEnvironment = null;
			this.dictStore = null;
			this.pidx = null;
		}
		
	}

	
}
