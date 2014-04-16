package de.unikoblenz.west.ldim.eval;

import java.util.TreeSet;

import de.unikoblenz.west.ldim.distributions.Distribution;

/**
 * Various methods for comparing two distributions.
 * 
 * @author Thomas Gottron
 */
public class DistributionCompare {

	/**
	 * Basis to be used for all logarithms.
	 */
	public final static int ENTROPY_BASE = 2;

	/**
	 * Compute the perplexity of using the base distribution to approximate the actual distribution
	 * 
	 * @param base
	 * @param actual
	 * @return
	 */
	public double perplexity(Distribution base, Distribution actual) {
		double result = 0;
		double crossEntropy = this.crossEntropy(base, actual);
		result = Math.pow(ENTROPY_BASE, crossEntropy);
		return result;
	}
	
	/**
	 * Computes the cross entropy H(P,Q) = sum_x (q(x) log(p(x))) where P is this distribution and Q is the other distribution
	 * @param actual
	 * @return
	 */
	public double crossEntropy(Distribution base, Distribution actual) {
		double result = 0;
		for (String x : actual.getEvents()) {
			double p = base.probability(x);
			double q = actual.probability(x);
			result += -q * this.logLocalBase(p);
		}
		return result;
	}
	
	/**
	 * Compute the entropy of a distribution
	 * @param dist
	 * @return
	 */
	public double entropy(Distribution dist) {
		double result = 0;
		for (String x : dist.getEvents()) {
			double p = dist.probability(x);
			result +=  -p * this.logLocalBase(p);
		}
		return result;
	}
	
	/**
	 * Compute the normalized entropy
	 * @param dist
	 * @return
	 */
	public double entropyNorm(Distribution dist) {
		double result = this.entropy(dist);
		result /= -this.logLocalBase( 1./dist.getEvents().size());
		return result;
	}
	
	/**
	 * Internal logarithm function to consistently use the pre-set log base.
	 * @param x
	 * @return
	 */
	public double logLocalBase(double x) {
		return (Math.log(x) / Math.log(ENTROPY_BASE));
	}
	
	/**
	 * Compute the Kullback Leibler divergence from the base to the actual distribution, i.e. using the base distribution to approximate the actual distribution.
	 * @param base
	 * @param actual
	 * @return
	 */
	public double kullbackLeibler(Distribution base, Distribution actual) {
		double result = 0;
		result = this.crossEntropy(base,actual) - this.entropy(actual);
		return result;
	}
	
	/**
	 * Returns the number of events covered (in total) by the other or this
	 * distribution, i.e. set size of the union of the vent sets
	 * 
	 * @param actual
	 * @return
	 */
	public int jointEventSize(Distribution base, Distribution actual) {
		int result = 0;
		TreeSet<String> events = new TreeSet<String>();
		events.addAll(base.getEvents());
		events.addAll(actual.getEvents());
		result = events.size();
		return result;
	}

	/**
	 * Return the number of events which are covered in the OTHER distribution
	 * but not in this one.
	 * 
	 * @param actual
	 * @return
	 */
	public int unknownEvents(Distribution base, Distribution actual) {
		int result = 0;
		TreeSet<String> events = new TreeSet<String>();
		events.addAll(actual.getEvents());
		events.removeAll(base.getEvents());
		result = events.size();
		return result;
	}

	/**
	 * Jacard similarity over the sets of events between the other and this
	 * distribution.
	 * 
	 * @param actual
	 * @return
	 */
	public double jacardSimilarityEvents(Distribution base, Distribution actual) {
		double result = 0;
		TreeSet<String> intersection = new TreeSet<String>();
		TreeSet<String> union  = new TreeSet<String>();
		intersection.addAll(actual.getEvents());
		union.addAll(actual.getEvents());
		intersection.retainAll(base.getEvents());
		union.addAll(base.getEvents());
		result = intersection.size();
		result = result / union.size();
		return result;
	}

	
	
}
