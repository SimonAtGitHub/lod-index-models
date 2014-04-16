package de.unikoblenz.west.ldim.distributions;

import java.util.TreeMap;

/**
 * @deprecated
 * @author gottron
 *
 */
public abstract class FrequencyAdjustedDistribution extends Distribution {

	@Override
	public double aggregateProbabilities() {
		double pSum = 0;
		for (String e : this.getEvents()) {
			pSum += this.probability(e);
		}
		pSum += zeroCount * this.probability("_____________________");
		return pSum;
	}

	private TreeMap<Integer,Double> freqsAdjust = new TreeMap<Integer,Double>();
	private double adjustedTotalCount = 0;
	private int zeroCount = 0;

	protected  void setZeroCount(int cnt) {
		this.zeroCount = cnt;
	}

	protected void setAdjustedFrequency(int freq, double adjust) {
		this.freqsAdjust.put(freq, adjust);
	}

	protected double getAdjustedFrequency(int freq) {
		double result = 0;
		if (this.freqsAdjust.containsKey(freq)) {
			result = this.getAdjustedFrequency(freq);
		}
		return result;
	}

	protected void setAdjustedTotalCount(double adjustTotal) {
		this.adjustedTotalCount = adjustTotal;
	}

	protected void incAdjustedTotalCount(double increment) {
		this.adjustedTotalCount += increment;
	}

	protected double getAdjustedTotalCount() {
		return this.adjustedTotalCount;
	}

	@Override
	public double probability(String x) {
		int freq = this.getCount(x);
		double result = this.freqsAdjust.get(freq);
		result /= this.adjustedTotalCount;
		return result;
	}

}
