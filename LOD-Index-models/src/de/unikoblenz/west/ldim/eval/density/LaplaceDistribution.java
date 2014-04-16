package de.unikoblenz.west.ldim.eval.density;

/**
 * A distribution estimating probabilities on the basis of MLE and Laplace or
 * Lidstone smoothing. The parameter lambda gives the constant value which is
 * added to all observation counts for the purpose of smoothing. A value of
 * lambda=1 corresponds to Laplace smoothing any other value to the Lidstone
 * generalization of Laplace.
 * 
 * @author Thomas Gottron
 * 
 */
public class LaplaceDistribution extends MLEDistribution {

	/**
	 * Smoothing constant. The count of each observation is increased by this
	 * value.
	 */
	public double lambda = 0.01;

	/**
	 * An artificial value for the size of the event set. This allows to
	 * consider a given number of unobserved events in the event space.
	 */
	public int eventSize = 0;

	/**
	 * Initializes the distribution with the smoothing parameter lambda
	 * 
	 * @param lambda
	 */
	public LaplaceDistribution(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Sets the size of the event space. This allows for artificially
	 * introducing unobserved events. Setting this value to the size of the
	 * actual (observed) event set and adding 1 corresponds to introducing a
	 * single artificial "unobserved" event.
	 * 
	 * @param size
	 */
	public void setEventsSpaceSize(int size) {
		this.eventSize = size;
	}

	/**
	 * Laplace or Lidstone smoothed probability for event x.
	 */
	public double probability(String x) {
		double result = this.getCount(x) + this.lambda;
		result /= (this.getTotalCount() + this.eventSize * this.lambda);
		return result;
	}

	@Override
	public double aggregateProbabilities() {
		int observedEventSize = this.getEvents().size();
		double pSum = super.aggregateProbabilities();
		// obtain a probability of the unobserved event
		double zeroP = this.probability("__________________");
		pSum += zeroP * (this.eventSize - observedEventSize);
		return pSum;
	}

}
