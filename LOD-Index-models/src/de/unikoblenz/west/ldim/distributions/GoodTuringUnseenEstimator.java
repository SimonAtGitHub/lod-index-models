package de.unikoblenz.west.ldim.distributions;

/**
 * @deprecated
 * @author gottron
 *
 */

public class GoodTuringUnseenEstimator extends UnseenEventEstimator {

	@Override
	public int estimateUnseenEvents(MLEDistribution mle) {
		int n1 = mle.getFrequencyCount(1);
		return n1;
	}

	

}
