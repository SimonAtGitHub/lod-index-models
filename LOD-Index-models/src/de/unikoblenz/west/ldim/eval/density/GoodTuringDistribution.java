package de.unikoblenz.west.ldim.eval.density;

import java.util.TreeMap;

public class GoodTuringDistribution extends FrequencyAdjustedDistribution {

//	private final static int MAX_SMOOTHED_FREQ_CLASS = 5;
	
	@Override
	protected void onDataLoaded() {
		int es = this.getEvents().size();
		int freqs = this.getCounts().size();
		double expFreq = (1.*es)/freqs;
		int lastWithFollowUp = 0;
		int lastFreq = 0;
		for (int i : this.getCounts()) {
			if ( (lastFreq < i-1) || (this.getFrequencyCount(i) < expFreq)) {
				lastWithFollowUp = i-2;
				break;
			}
			lastFreq = i;
		}
		
		for (int i : this.getCounts()) {
			double r = i;
			if ( (i <= lastWithFollowUp) && (this.getFrequencyCount(i+1)*this.getFrequencyCount(i)>0) ) {
				r = ((i+1.) * this.getFrequencyCount(i+1))/this.getFrequencyCount(i);
			} 
			this.setAdjustedFrequency(i, r);
			this.incAdjustedTotalCount(r*this.getFrequencyCount(i));
		}
		this.setZeroCount(this.getFrequencyCount(1));
		double r0 = this.getFrequencyCount(1)*1./this.getTotalCount();
		this.setAdjustedFrequency(0, r0);
		this.incAdjustedTotalCount(r0*this.getFrequencyCount(1));
	}

}
