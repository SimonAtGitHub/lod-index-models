package eval.density;


public class MLEDistribution extends Distribution {

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
