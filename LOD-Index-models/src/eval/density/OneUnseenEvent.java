package eval.density;

public class OneUnseenEvent extends UnseenEventEstimator {

	@Override
	public int estimateUnseenEvents(MLEDistribution mle) {
		return 1;
	}

}
