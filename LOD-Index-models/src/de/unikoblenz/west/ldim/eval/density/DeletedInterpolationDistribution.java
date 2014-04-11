package de.unikoblenz.west.ldim.eval.density;

import java.util.TreeMap;

public class DeletedInterpolationDistribution extends FrequencyAdjustedDistribution {

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
		TreeMap<Integer,Integer> totalFreqsCountInS = new TreeMap<Integer, Integer>();
		for (String event : this.getEvents()) {
			// iterate over all events in S and T, so 
			int sFreq = S.getCount(event);
			int tFreq = T.getCount(event);
			if (totalFreqsCountInT.containsKey(tFreq)) {
				tFreq += totalFreqsCountInT.get(tFreq);
			}
			totalFreqsCountInT.put(sFreq, tFreq);
			if (totalFreqsCountInS.containsKey(sFreq)) {
				sFreq += totalFreqsCountInS.get(sFreq);
			}
			totalFreqsCountInS.put(tFreq, sFreq);
		}		
		
		for (int cnt : totalFreqsCountInT.keySet()) {
			double Tr = totalFreqsCountInT.get(cnt);
			double NSr = S.getFrequencyCount(cnt);
			double Sr = totalFreqsCountInS.get(cnt);
			double NTr = T.getFrequencyCount(cnt);
			if (cnt == 0) {
				NSr = sZeroFreq;
				NTr = tZeroFreq;
			}
			double empiricalFreqT = Tr;
			double empiricalFreqS = Sr;
			if (NSr > 0) {
				empiricalFreqT /= NSr;
			}
			if (NTr > 0) {
				empiricalFreqS /= NTr;
			}
			double empiricalFreq = (S.getTotalCount()*empiricalFreqS+T.getTotalCount()*empiricalFreqT)/(S.getTotalCount()+T.getTotalCount());
			this.setAdjustedFrequency(cnt, empiricalFreq);
			this.incAdjustedTotalCount(empiricalFreq*NSr);
		}

	}

}
