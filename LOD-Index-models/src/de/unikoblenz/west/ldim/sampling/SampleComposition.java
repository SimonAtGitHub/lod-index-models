package de.unikoblenz.west.ldim.sampling;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.distributions.Distribution;

/**
 * Abstract class for constructing a larger sample out of smaller samples. The
 * larger sample has to satisfy some size constraint, where size can be defined
 * as an arbitrary integer, e.g. to denote byte size or number triple
 * statements.
 * 
 * @author Thomas Gottron
 * 
 */
public abstract class SampleComposition {

	/**
	 * Stores the size values for all samples.
	 */
	private TreeMap<String, Integer> sampleSizes = new TreeMap<String, Integer>();

	/**
	 * Stores the distributions of all samples.
	 */
	private TreeMap<String, Distribution> sampleDistributions = new TreeMap<String, Distribution>();

	/**
	 * Internal reference to the sum of the sample sizes
	 */
	private int sampleSizeSum = 0;

	/**
	 * Internal reference to the largest sample.
	 */
	private String largestSample = null;

	/**
	 * Adds one sample to the pool of usable samples. The method is provided
	 * with a distribution of the sample as well as a figure indicating the
	 * original size of the sample (e.g. number of triples, context, byte size).
	 * After calling this
	 * 
	 * @param sampleReference
	 *            String used to refer to a particular sample.
	 * @param sampleDistribution
	 *            File from which to load frequency information of a data
	 *            sample.
	 * @param sampleSize
	 *            Size of the sample measured by the used indicator. Must be a
	 *            positive value
	 */
	public void addSample(String sampleReference,
			Distribution sampleDistribution, int sampleSize) {
		if ((sampleSize > 0) && (sampleDistribution != null)) {
			// if the sample is already contained in the list, we need to
			// remove it first and subtract its size from the sum of sizes prior
			// to replacing it.
			if (this.sampleSizes.containsKey(sampleReference)) {
				int oldsize = this.sampleSizes.get(sampleReference);
				this.sampleSizes.remove(sampleReference);
				this.sampleDistributions.remove(sampleReference);
				this.sampleSizeSum -= oldsize;
				if (this.largestSample.equals(sampleReference)
						&& (oldsize > sampleSize)) {
					// the largest sample is replaced by a smaller sample. So we
					// have to search for a newest largest sample among all
					// others
					String currentLargest = null;
					int currentsize = 0;
					for (String sample : this.getSampleReferences()) {
						int size = this.getSampleSize(sample);
						if (size > currentsize) {
							currentLargest = sample;
						}
					}
					this.largestSample = currentLargest;
				}
			}
			// update reference to largest sample if necessary
			if ((this.largestSample == null)
					|| (this.getSampleSize(this.largestSample) < sampleSize)) {
				this.largestSample = sampleReference;
			}
			this.sampleDistributions.put(sampleReference, sampleDistribution);
			this.sampleSizes.put(sampleReference, sampleSize);
			this.sampleSizeSum += sampleSize;
		} else {
			throw new IllegalArgumentException("Sample size must be positive");
		}
	}

	/**
	 * Returns the set of all Strings used a reference names for samples
	 * 
	 * @return Set of Strings denoting the sample references.
	 */
	protected Set<String> getSampleReferences() {
		return this.sampleSizes.keySet();
	}

	/**
	 * Returns the size of s specific sample.
	 * 
	 * @param reference
	 *            Reference name for the sample
	 * @return size of the sample.
	 */
	protected int getSampleSize(String reference) {
		int result = 0;
		if (this.sampleSizes.containsKey(reference)) {
			result = this.sampleSizes.get(reference);
		}
		return result;
	}

	/**
	 * Returns the distribution of a specific sample.
	 * 
	 * @param reference
	 *            Reference name for the sample.
	 * @return Distribution of the sample.
	 */
	protected Distribution getSampleDistribution(String reference) {
		Distribution result = null;
		if (this.sampleDistributions.containsKey(reference)) {
			result = this.sampleDistributions.get(reference);
		}
		return result;
	}

	/**
	 * Returns the sum of the sizes of all samples.
	 * 
	 * @return sum of all sample sizes.
	 */
	public int getSampleSizeSum() {
		return this.sampleSizeSum;
	}

	/**
	 * Compose a sample from smaller samples which satisfies the maximum size
	 * constraints and approximates the provided base distribution. The result
	 * is given by list of reference String to identify the atomic small
	 * samples.
	 * 
	 * @param base
	 *            Base distribution to be approximated.
	 * @return Set of smaller samples from which the larger sample is composed.
	 */
	public abstract Set<String> composeSample(Distribution base, int maxSize);

	/**
	 * Removes all sample (references) for which the samples exceed a certain
	 * size.
	 * 
	 * @param remaining
	 *            Collection of sample references.
	 * @param maxSize
	 *            maximal size of an individual sample.
	 */
	protected void cleanTooBig(Collection<String> remaining, int maxSize) {
		if (this.getSampleSize(this.largestSample) > maxSize) {
			// we only need to filter if the largest sample exceeds the maximum size.
			TreeSet<String> tooBig = new TreeSet<String>();
			for (String sampleRef : remaining) {
				if (this.getSampleSize(sampleRef) > maxSize) {
					tooBig.add(sampleRef);
				}
			}
			remaining.removeAll(tooBig);
		}
	}

}
