package de.unikoblenz.west.ldim.sampling;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.eval.density.Distribution;

public class RandomSampleComposition extends SampleComposition {

	@Override
	public Set<String> composeSample(Distribution base, int maxSize) {
		// if all samples fit into the maxSize just return them all.
		if (this.getSampleSizeSum() <= maxSize)  {
			return this.getSampleReferences();
		}
		TreeSet<String> result = new TreeSet<String>();
		int currentSize = 0;
		ArrayList<String> remaining = new ArrayList<String>(); 
		remaining.addAll(this.getSampleReferences());
		// remove all samples which are anyway too big
		this.cleanTooBig(remaining, maxSize);
		while ((remaining.size() > 0) && (currentSize <= maxSize))  {
			int upperIndex = remaining.size();
			int choice = (int) Math.max(upperIndex-1, Math.floor(Math.random()* upperIndex));
			String sampled = remaining.get(choice);
			int size = this.getSampleSize(sampled);
			result.add(sampled);
			remaining.remove(choice);
			currentSize += size;
			this.cleanTooBig(remaining, maxSize-currentSize);
		}
		return result;
	}

}
