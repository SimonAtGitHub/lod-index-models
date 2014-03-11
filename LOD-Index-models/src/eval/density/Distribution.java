package eval.density;

import index.IndexWriter;
import io.ZipBufferedReaderHandler;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

public abstract class Distribution {


	private TreeMap<String, Integer> frequencies = new TreeMap<String, Integer>();
	private TreeMap<Integer,Integer> countOfCounts = new TreeMap<Integer,Integer>();
	private int totalCount = 0;

	private int hitCount = 0;
	private int missCount = 0;
	
	protected abstract void onDataLoaded();

	public abstract double probability(String x);

	public abstract double aggregateProbabilities();
	
	public void readFreqFile(File fin) throws IOException {
		ZipBufferedReaderHandler zIn = new ZipBufferedReaderHandler(fin);
		String line = null;
		while ((line = zIn.getBufferedReader().readLine()) != null) {
			String[] parts = line.split(IndexWriter.SEPARATOR, 2);
			String key = parts[0];
			int freq = Integer.parseInt(parts[1].trim());
			this.totalCount += freq;
			if (frequencies.containsKey(key)) {
				System.out.println("Duplicate key! " + key);
			}
			this.frequencies.put(key, freq);
			int freqCount = 1;
			if (this.countOfCounts.containsKey(freq)) {
				freqCount = 1 + this.countOfCounts.get(freq);
			}
			this.countOfCounts.put(freq, freqCount);
		}
		zIn.close();
		this.onDataLoaded();
//		System.out.println("Prob Sum ("+(this.getClass().toString())+") = "+this.aggregateProbabilities());
		this.resetHitMissCount();
	}
	
	public void setCount(String key, int freq) {
		if (this.frequencies.containsKey(key)) {
			int oldFreq = this.frequencies.get(key);
			if (oldFreq == freq) {
				// Frequency has not changed -- nothing to update
				return;
			}
			this.totalCount -= oldFreq;
			int oldFreqCount = this.countOfCounts.get(oldFreq);
			oldFreqCount--;
			this.countOfCounts.put(oldFreq, oldFreqCount);
			if (freq == 0) {
				this.countOfCounts.remove(freq);
			}
		}
		if (freq > 0) {
			this.frequencies.put(key, freq);
			this.totalCount += freq;
			int freqCount = 1;
			if (this.countOfCounts.containsKey(freq)) {
				freqCount = 1 + this.countOfCounts.get(freq);
			}
			this.countOfCounts.put(freq, freqCount);
		}
	}

	
	public int getCount(String x) {
		int result = 0;
		if (this.frequencies.containsKey(x)) {
			result = this.frequencies.get(x);
			this.hitCount++;
		} else {
			this.missCount++;
		}
		return result;
	}
	
	public void resetHitMissCount() {
		this.hitCount = 0;
		this.missCount = 0;
	}
	
	public void printHitMissStats() {
//		System.out.println("Hit:  "+this.hitCount);
//		System.out.println("Miss: "+this.missCount);
	}
	
	public int getTotalCount() {
		return this.totalCount;
	}

	public Set<String> getEvents() {
		return this.frequencies.keySet();
	}
	
	public int getFrequencyCount(int freq) {
		int result = 0;
		if (this.countOfCounts.containsKey(freq)) {
			result = this.countOfCounts.get(freq);
		}
		return result;
	}
	
	public Set<Integer> getCounts() {
		return this.countOfCounts.keySet();
	}

}
