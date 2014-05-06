package de.unikoblenz.west.ldim.sampling;

import java.util.Set;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.distributions.Distribution;
import de.unikoblenz.west.ldim.distributions.LaplaceDistribution;
import de.unikoblenz.west.ldim.eval.DistributionCompare;

/**
 * Uses a greedy approach to approximate the distribution with a composition of
 * atomic samples. The quality is measured by a low cross entropy between the
 * approximation and the given base distribution. The process is iterative and
 * always selects the atomic sample which after adding to the current composed
 * sample leads to the lowest cross entropy value. Furthermore the process
 * considers only those atomic samples which would not violate the maxsize
 * constraint.
 * 
 * @author Thomas Gottron
 * 
 */
public class GreedySampleComposition extends SampleComposition {

	private static final double LAPLACE_LAMBDA = 1;

	@Override
	public Set<String> composeSample(Distribution base, int maxSize) {
		// if all samples fit into the maxSize just return them all.
		if (this.getSampleSizeSum() <= maxSize) {
			return this.getSampleReferences();
		}
		TreeSet<String> result = new TreeSet<String>();

		Distribution approximateDistribution = new LaplaceDistribution(
				GreedySampleComposition.LAPLACE_LAMBDA);
		DistributionCompare distCompare = new DistributionCompare();

		int currentSize = 0;
		TreeSet<String> remaining = new TreeSet<String>();
		remaining.addAll(this.getSampleReferences());
		// remove all samples which are anyway too big
		System.out.println("Cleaning too big distribs");
		this.cleanTooBig(remaining, maxSize);
		// iteratively select one atomic sample until the maxsize is reached or
		// no more (suitable) atomic samples are available
		while ((remaining.size() > 0) && (currentSize <= maxSize)) {
			String selectedSample = null;
			Distribution bestApprox = null;
			double minCrossEntropy = Double.POSITIVE_INFINITY;
			System.out.println("Searching "+remaining.size()+" options");
			int cnt = 0;
			for (String sample : remaining) {
				cnt++;
				if ( (cnt % 100) ==0) {
					System.out.print(".");
					if ( (cnt % 2000) ==0) {
						System.out.println(" "+cnt);
					}
				}
				Distribution sDist = this.getSampleDistribution(sample);
				LaplaceDistribution composedDist = new LaplaceDistribution(
						GreedySampleComposition.LAPLACE_LAMBDA);
				composedDist.mergeFromDistributions(approximateDistribution,
						sDist);
				double crossEntropy = distCompare.crossEntropy(composedDist,
						base);
				if ((bestApprox == null) || (crossEntropy < minCrossEntropy)) {
					bestApprox = composedDist;
					minCrossEntropy = crossEntropy;
					selectedSample = sample;
				}
			}
			// update the approximation by taking the best approximation from
			// the previous attempt
			approximateDistribution = bestApprox;
			int size = this.getSampleSize(selectedSample);
			result.add(selectedSample);
			remaining.remove(selectedSample);
			currentSize += size;
			System.out.println(size+"\t"+currentSize+" / "+maxSize+"\t"+minCrossEntropy+"\t"+selectedSample);
			// Remove samples which are too big
			this.cleanTooBig(remaining, maxSize - currentSize);
		}
		return result;
	}

}
