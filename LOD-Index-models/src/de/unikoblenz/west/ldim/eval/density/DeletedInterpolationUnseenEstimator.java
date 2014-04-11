package de.unikoblenz.west.ldim.eval.density;

import java.util.TreeMap;

public class DeletedInterpolationUnseenEstimator extends UnseenEventEstimator {

	@Override
	public int estimateUnseenEvents(MLEDistribution mle) {
		Distribution S = new MLEDistribution();
		Distribution T = new MLEDistribution();
		int sZeroFreq = 0;
		int tZeroFreq = 0;
		for (String event : mle.getEvents()) {
			int cnt = mle.getCount(event);
			int cntS = 0;
			int cntT = 0;
			for (int i = 0; i < cnt; i++) {
				if (Math.random()>=0.5) {
					cntS++;
				} else {
					cntT++;
				}
			}
			S.setCount(event, cntS);
			T.setCount(event, cntT);
			if (cntS == 0) {
				sZeroFreq++;
			}
			if (cntT == 0) {
				tZeroFreq++;
			}
		}
		int tTotal = T.getFrequencyCount(1);
		int sTotal = S.getFrequencyCount(1);
		double tZeroRatio = ( (double) tZeroFreq) / tTotal;
		double sZeroRatio = ( (double) sZeroFreq) / sTotal;
		double interpolatedZeroRatio = (tZeroRatio + sZeroRatio) / 2;
		int result =  (int) (interpolatedZeroRatio*mle.getTotalCount());
		return result;
	}

}
