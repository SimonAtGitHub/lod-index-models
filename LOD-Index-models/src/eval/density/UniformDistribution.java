package eval.density;

public class UniformDistribution extends MLEDistribution {

	private int eventCount = 0;
	
	@Override
	protected void onDataLoaded() {		
		super.onDataLoaded();
		this.eventCount = this.getEvents().size();
	}

	@Override
	public double probability(String x) {
		double cnt = 1;
		double result = cnt / (this.eventCount +1); 
		return result;
	}

}
