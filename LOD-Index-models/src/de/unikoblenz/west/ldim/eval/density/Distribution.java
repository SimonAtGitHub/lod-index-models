package de.unikoblenz.west.ldim.eval.density;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.ZipBufferedReaderHandler;

/**
 * Basic class for modeling distributions. Keeps track of all relevant
 * information, such as counts of observations, total number of observations and
 * counts of counts (i.e. a distribution of the frequency values). The class is
 * abstract and does not provide any implementation of a concrete probability
 * computation. This is in the responsibility of the implementing subclasses.
 * 
 * @author Thomas Gottron
 * 
 */
public abstract class Distribution {

	/**
	 * A map to store the frequency of events (observed index entries). Directly
	 * maps a String representation of the event onto the count of observations.
	 */
	private TreeMap<String, Integer> frequencies = new TreeMap<String, Integer>();
	/**
	 * The count of count map provides a distribution of the frequencies, i.e.
	 * how often did each frequency occur. Thus, it stores how many events have
	 * occurred exactly n times for all values of n.
	 */
	private TreeMap<Integer, Integer> countOfCounts = new TreeMap<Integer, Integer>();

	/**
	 * Total count of observations. Needed for computing relative frequencies.
	 */
	private int totalCount = 0;

	/**
	 * Abstract method called after data has been loaded from a file. Allows for
	 * post-processing of distribution data in implementing classes.
	 */
	protected abstract void onDataLoaded();

	/**
	 * Computes the probability to observe a concrete event x (i.e. index
	 * entry). The event is given via a String representation.
	 * 
	 * @param x
	 *            String representation of the event for which the probability
	 *            is to be computed
	 * @return Probability of x
	 */
	public abstract double probability(String x);

	/**
	 * Method to aggregate all probabilities. Can be used to check for the
	 * values to sum up to 1. Needs to be implemented by the sub-classes as only
	 * they are aware of the entire event-space they cover (e.g. due to
	 * unobserved events which are not covered in the map of observations)
	 * 
	 * @return Sum of all probabilities
	 */
	public abstract double aggregateProbabilities();

	/**
	 * Reads the frequency distribution from an input file (zip compressed).
	 * Builds the map of event frequencies,
	 * 
	 * @param fin
	 *            Input file to read the frequencies from. See
	 *            SortedCountAggregator for details on format.
	 * @throws IOException
	 */
	public void readFreqFile(File fin) throws IOException {
		ZipBufferedReaderHandler zIn = new ZipBufferedReaderHandler(fin);
		String line = null;
		while ((line = zIn.getBufferedReader().readLine()) != null) {
			// entries are line based. each line contains the String
			// representation of the event, followed by a separator, followed by
			// the number of observations
			String[] parts = line.split(IndexWriter.SEPARATOR, 2);
			String key = parts[0];
			int freq = Integer.parseInt(parts[1].trim());
			// update totalcount by one
			this.totalCount += freq;
			if (frequencies.containsKey(key)) {
				// This actually should not happen!
				System.err.println("Duplicate key! " + key);
			}
			// set frequency for this observation
			this.frequencies.put(key, freq);
			// adjust the count of counts, i.e. the distribution of frequencies
			int freqCount = 1;
			if (this.countOfCounts.containsKey(freq)) {
				freqCount = 1 + this.countOfCounts.get(freq);
			}
			this.countOfCounts.put(freq, freqCount);
		}
		zIn.close();
		// call the postprocessing function
		this.onDataLoaded();
	}

	/**
	 * Aggregates the count information from two distribution objects into a single object.
	 * 
	 * @param dist1 First distribution from which to merge information
	 * @param dist2 Second distribution from which to merge information
	 */
	public void mergeFromDistributions(Distribution dist1, Distribution dist2) {
		TreeSet<String> events = new TreeSet<String>();
		// collect the events from both distributions.
		events.addAll(dist1.getEvents());
		events.addAll(dist2.getEvents());
		for (String event: events) {
			// determine sum of counts from both distributions
			int freq = dist1.getCount(event) + dist2.getCount(event);
			this.totalCount += freq;
			// set frequency for this observation
			this.frequencies.put(event, freq);
			// adjust the count of counts, i.e. the distribution of frequencies
			int freqCount = 1;
			if (this.countOfCounts.containsKey(freq)) {
				freqCount = 1 + this.countOfCounts.get(freq);
			}
			this.countOfCounts.put(freq, freqCount);
		}
		// call the postprocessing function
		this.onDataLoaded();
	}
	
	/**
	 * Set the value for an observation. Adjusts the frequencies data of
	 * observations and the count of count, i.e. frequency distribution
	 * 
	 * @param key
	 *            String representation of the event for which to update the
	 *            number of observation.
	 * @param freq
	 *            New number of observations.
	 */
	protected void setCount(String key, int freq) {
		if (this.frequencies.containsKey(key)) {
			// check if observation present and get old frequency
			int oldFreq = this.frequencies.get(key);
			if (oldFreq == freq) {
				// Frequency has not changed -- nothing to update
				return;
			}
			// take away old frequency
			this.totalCount -= oldFreq;
			// adjust count of counts, i.e. remove the count of the old
			// frequency by one
			int oldFreqCount = this.countOfCounts.get(oldFreq);
			oldFreqCount--;
			this.countOfCounts.put(oldFreq, oldFreqCount);
			if (freq == 0) {
				this.countOfCounts.remove(freq);
			}
		}
		// still need to update the data (only if new frequency > 0)
		if (freq > 0) {
			// frequency count
			this.frequencies.put(key, freq);
			// total count
			this.totalCount += freq;
			// count of counts
			int freqCount = 1;
			if (this.countOfCounts.containsKey(freq)) {
				freqCount = 1 + this.countOfCounts.get(freq);
			}
			this.countOfCounts.put(freq, freqCount);
		}
	}

	/**
	 * Returns the frequency count information for one particular event.
	 * 
	 * @param x
	 *            String representation of the event for which the frequency
	 *            count is to be returned
	 * @return absolute frequency (number of observations) of the event
	 */
	public int getCount(String x) {
		int result = 0;
		if (this.frequencies.containsKey(x)) {
			result = this.frequencies.get(x);
		} else {
		}
		return result;
	}

	/**
	 * Provides the total number of observations
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * Returns the set of events on which this distribution is defined. Note,
	 * that this set only contains events for which there are observations.
	 * 
	 * @return Set of events, i.e. String representations of index entries.
	 */
	public Set<String> getEvents() {
		return this.frequencies.keySet();
	}

	/**
	 * Returns the count of counts information of a frequency. This means, the
	 * method returns how many events in this distribution have exactly a
	 * frequency of freq.
	 * 
	 * @param freq
	 *            Frequency for which to obtain count information
	 * @return Count of events which occurred exactly freq times.
	 */
	public int getFrequencyCount(int freq) {
		int result = 0;
		if (this.countOfCounts.containsKey(freq)) {
			result = this.countOfCounts.get(freq);
		}
		return result;
	}

	/**
	 * Returns the set of counts, i.e. the frequencies observed in this
	 * distribution.
	 * 
	 * @return
	 */
	public Set<Integer> getCounts() {
		return this.countOfCounts.keySet();
	}

}
