package de.unikoblenz.west.ldim.eval.density;

public class LaplaceDistribution extends MLEDistribution {

	public double lambda = 0.01;
	
	public int eventSize = 0;
	
	public LaplaceDistribution(double lambda) {
		this.lambda = lambda;
	}
	
	public void setEventsSpaceSize(int size) {
		this.eventSize = size;
	}

	public double probability(String x) {
		double result = this.getCount(x) + this.lambda;
		result /= (this.getTotalCount() + this.eventSize*this.lambda);
		return result;
	}

	@Override
	public double aggregateProbabilities() {
		int evSize = this.getEvents().size();
		double pSum = super.aggregateProbabilities(); 
		double zeroP = this.probability("__________________");
		pSum += zeroP*(this.eventSize-evSize);
		return pSum;
	}


}
