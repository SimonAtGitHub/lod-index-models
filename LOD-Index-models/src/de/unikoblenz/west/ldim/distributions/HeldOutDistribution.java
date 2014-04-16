package de.unikoblenz.west.ldim.distributions;

import java.util.TreeMap;

/**
 * @deprecated
 * @author gottron
 *
 */
public class HeldOutDistribution extends FrequencyAdjustedDistribution {

	@Override
	protected void onDataLoaded() {
		Distribution S = new MLEDistribution();
		Distribution T = new MLEDistribution();
		int sZeroFreq = 0;
		int tZeroFreq = 0;
		for (String event : this.getEvents()) {
			int cnt = this.getCount(event);
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
		TreeMap<Integer,Integer> totalFreqsCountInT = new TreeMap<Integer, Integer>();
		for (String event : this.getEvents()) {
			// iterate over all events in S and T, so 
			int sFreq = S.getCount(event);
			int tFreq = T.getCount(event);
			if (totalFreqsCountInT.containsKey(tFreq)) {
				tFreq += totalFreqsCountInT.get(tFreq);
			}
			totalFreqsCountInT.put(sFreq, tFreq);
		}		
		for (int cnt : totalFreqsCountInT.keySet()) {
			double Tr = totalFreqsCountInT.get(cnt);
			double Nr = S.getFrequencyCount(cnt);
			if (cnt == 0) {
				Nr = sZeroFreq;
			}
			double empiricalFreq = Tr;
			if (Nr > 0) {
				empiricalFreq /= Nr;
			}
			this.setAdjustedFrequency(cnt, empiricalFreq);
			this.incAdjustedTotalCount(empiricalFreq*Nr);
		}
	}

}
