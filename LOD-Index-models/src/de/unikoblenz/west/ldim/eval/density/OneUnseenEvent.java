package de.unikoblenz.west.ldim.eval.density;

/**
 * @deprecated
 * @author gottron
 *
 */
public class OneUnseenEvent extends UnseenEventEstimator {

	@Override
	public int estimateUnseenEvents(MLEDistribution mle) {
		return 1;
	}

}
