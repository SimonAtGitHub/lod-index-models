package de.unikoblenz.west.ldim.sampling;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.distributions.Distribution;

/**
 * Composes a random sample up to the maximal size. To this end the process
 * iteratively selects randomly one of the un-used atomic samples. The process
 * tries to approximate the size as good as possible. This means that when
 * getting close to the maximal size, those atomic samples are excluded that
 * would lead to a too large sample violating the maxSize constraint. At each
 * step, however, all still small enough samples are equally likely to be
 * selected.
 * 
 * @author Thomas Gottron
 * 
 */
public class RandomSampleComposition extends SampleComposition {

	@Override
	public Set<String> composeSample(Distribution base, int maxSize) {
		// if all samples fit into the maxSize just return them all.
		if (this.getSampleSizeSum() <= maxSize) {
			return this.getSampleReferences();
		}
		TreeSet<String> result = new TreeSet<String>();
		int currentSize = 0;
		ArrayList<String> remaining = new ArrayList<String>();
		remaining.addAll(this.getSampleReferences());
		// remove all samples which are anyway too big
		this.cleanTooBig(remaining, maxSize);
		// iteratively select one atomic sample until the maxsize is reached or no more (suitable) atomic samples are available 
		while ((remaining.size() > 0) && (currentSize <= maxSize)) {
			int upperIndex = remaining.size();
			int choice = (int) Math.max(upperIndex - 1,
					Math.floor(Math.random() * upperIndex));
			String sampled = remaining.get(choice);
			int size = this.getSampleSize(sampled);
			result.add(sampled);
			remaining.remove(choice);
			currentSize += size;
			// Remove samples which are too big
			this.cleanTooBig(remaining, maxSize - currentSize);
		}
		return result;
	}

}
