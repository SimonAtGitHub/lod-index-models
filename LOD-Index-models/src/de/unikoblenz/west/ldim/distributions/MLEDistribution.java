package de.unikoblenz.west.ldim.distributions;

/**
 * A distribution estimating probabilities on the basis of maximum likelihood
 * estimator (MLE). This means, that the relative frequency is directly used as
 * probability without any smoothing technique.
 * 
 * @author Thomas Gottron
 * 
 */
public class MLEDistribution extends Distribution {

	/**
	 * MLE estimation of the probability for event X
	 */
	public double probability(String x) {
		double result = this.getCount(x);
		result /= this.getTotalCount();
		return result;
	}

	@Override
	protected void onDataLoaded() {
		// nothing to do for MLE

	}

	@Override
	public double aggregateProbabilities() {
		double pSum = 0;
		for (String e : this.getEvents()) {
			pSum += this.probability(e);
		}
		return pSum;
	}

}
