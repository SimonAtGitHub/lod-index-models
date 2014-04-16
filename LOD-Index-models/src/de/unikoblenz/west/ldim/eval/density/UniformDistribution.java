package de.unikoblenz.west.ldim.eval.density;

/**
 * A distribution which actually ignores all observations and models a uniform
 * distribution of events. Thus, it merely uses the number of events from the
 * observations and equally distributes probability mass over all events.
 * 
 * One additional event is assumed to model unobserved events. In this sense
 * this distribution class performs some smoothing as well.
 * 
 * @author Thomas Gottron
 * 
 */
public class UniformDistribution extends MLEDistribution {

	/**
	 * Internal reference to the number of events.
	 */
	private int eventCount = 0;

	@Override
	protected void onDataLoaded() {
		super.onDataLoaded();
		// check the number of events
		this.eventCount = this.getEvents().size();
	}

	@Override
	public double probability(String x) {
		double cnt = 1;
		// model a uniform distribution, i.e. assume to have observed each event
		// once + one unobserved event.
		double result = cnt / (this.eventCount + 1);
		return result;
	}

}
