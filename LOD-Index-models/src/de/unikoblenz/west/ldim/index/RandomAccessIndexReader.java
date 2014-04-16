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

/**
 * Class providing random access to the key value pairs from an index. Uses a
 * persistent hash table from the Berkeley DB Java Edition.
 * 
 * @author Thomas Gottron
 * 
 */
public class RandomAccessIndexReader {

	/**
	 * Value indicating a missing entry in an index.
	 */
	public final static String MISSING = "MISS";

	/**
	 * Internal reference to the file from which to read the index data.
	 */
	private File indexFile = null;
	/**
	 * DB Environment for the persistent hash table.
	 */
	private Environment myDbEnvironment = null;
	/**
	 * EntityStore for the persistent hash table
	 */
	private EntityStore dictStore = null;
	/**
	 * PrimaryIndex of the persistent hash table
	 */
	private PrimaryIndex<String, KeyValueEntry> pidx = null;

	/**
	 * Initializes the Index Access by reading the index data form the given
	 * input file, generating a persistent hash table stored in the dbDir
	 * folder.
	 * 
	 * @param indexFile file from which the index data is to be read
	 * @param dbDir directory in which to store files of the persistent hash table
	 * @throws FileNotFoundException
	 */
	public RandomAccessIndexReader(File indexFile, File dbDir)
			throws FileNotFoundException {
		this.indexFile = indexFile;
		this.setup(dbDir);
	}

	/**
	 * Look up the value for a specific key.
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		KeyValueEntry pair = this.pidx.get(key);
		if (pair == null) {
			return RandomAccessIndexReader.MISSING;
		}
		return pair.value;
	}

	/**
	 * Closes all files and mediator classes related to the persistent hash table
	 */
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

	/**
	 * Load the index data from the input file and transfer it to the persistent hash table
	 * @param dbDir
	 */
	public void setup(File dbDir) {
		try {
			// Setup needed classes for interacting with Berkeley DNB Java Edition
			EnvironmentConfig envConfig = new EnvironmentConfig();
			StoreConfig storeConfig = new StoreConfig();
			envConfig.setAllowCreate(true);
			// Try to keep a high amount of data in the cache.
			envConfig.setCachePercent(80);
			storeConfig.setAllowCreate(true);
			this.myDbEnvironment = new Environment(dbDir, envConfig);
			this.dictStore = new EntityStore(myDbEnvironment, "EntityStore",
					storeConfig);
			this.pidx = dictStore.getPrimaryIndex(String.class,
					KeyValueEntry.class);

			if (this.pidx.count() == 0) {
				// prep the index
				try {
					BufferedReader reader = new BufferedReader(new FileReader(
							this.indexFile));
					String line = null;
					while ((line = reader.readLine()) != null) {
						// de-serialize object from input file
						KeyValueEntry kvp = KeyValueEntry.fromString(line);
						// store in persistent hash table
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
