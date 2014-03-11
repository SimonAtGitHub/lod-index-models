package eval;

import index.IndexWriter;
import io.ZipBufferedReaderHandler;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eval.density.Distribution;

public class DistributionCompare {

	public final static int ENTROPY_BASE = 2;
	

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
		base.resetHitMissCount();
		double pSum = 0;
		double qSum = 0;
		for (String x : actual.getEvents()) {
			double p = base.probability(x);
			pSum += p;
			double q = actual.probability(x);
			qSum += q;
			result += -q * this.logLocalBase(p);
		}
		base.printHitMissStats();
		return result;
	}
	
	public double entropy(Distribution dist) {
		double result = 0;
		for (String x : dist.getEvents()) {
			double p = dist.probability(x);
			result +=  -p * this.logLocalBase(p);
		}
		return result;
	}
	
	public double entropyNorm(Distribution dist) {
		double result = this.entropy(dist);
		result /= -this.logLocalBase( 1./dist.getEvents().size());
		return result;
	}
	
	public double logLocalBase(double x) {
		return (Math.log(x) / Math.log(ENTROPY_BASE));
	}
	
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
	 * @param atual
	 * @return
	 */
	public double jacardSimilarityEvents(Distribution base, Distribution atual) {
		double result = 0;
		TreeSet<String> intersection = new TreeSet<String>();
		TreeSet<String> union  = new TreeSet<String>();
		intersection.addAll(atual.getEvents());
		union.addAll(atual.getEvents());
		intersection.retainAll(base.getEvents());
		union.addAll(base.getEvents());
		result = intersection.size();
		result = result / union.size();
		return result;
	}

	
	
}
