package eval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import eval.density.DeletedInterpolationUnseenEstimator;
import eval.density.GoodTuringUnseenEstimator;
import eval.density.MLEDistribution;
import eval.density.OneUnseenEvent;
import eval.density.UnseenEventEstimator;

public class UnseenEstimatorEvaluation {

	public final static UnseenEventEstimator[] ests = {
			new OneUnseenEvent(),
			new GoodTuringUnseenEstimator(),
			new DeletedInterpolationUnseenEstimator()
	};
	public final static String[] labels = {
		"One event   : ",
		"Good Turing : ",
		"Del interp  : "
	};

	
	public static void main(String[] args) throws IOException {
			if (args.length >= 3) {
				String baseFile = args[0];
				String newFile = args[1];
				String resultFile = args[2];
				UnseenEstimatorEvaluation eval = new UnseenEstimatorEvaluation();
				File resultF = new File(resultFile);
				if (! resultF.exists()) {
					eval.initHeadlines(resultF);
				}
				eval.eventSpaceEstimator(baseFile, newFile,  resultF);
			} else {
				System.out.println("Usage:");
				System.out.println("  java DensityEvaluation <baseindex> <newindex> <resultfile> [<mode>]");
				System.out.println("where:");
				System.out.println("  <baseindex>  The frequency file used for the base distribution, i.e. using this distribution to explain the new distribution.");
				System.out.println("  <newindex>   The frequency file used for the new distribution, i.e. using this distribution which is approximated by the base distribution.");
				System.out.println("  <resultfile> File to write the results to. The file will be created if it does not exist. Otherwsie the results are appended to the end of the file (new entry in a CSV table).");
			}
	}
	
	private void initHeadlines(File resultFile)  throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		out.print("\"Data\"");
		out.print("\"Sample\"");
		out.print("\t\"True size\"");
		out.print("\t\"sample size\"");
		for (String label: labels) {
			out.print("\t\""+label+"\"");
		}
		out.println();
		out.close();
	}

	private void eventSpaceEstimator(String baseFreq, String newFreq, File resultFile)  throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		MLEDistribution distNew = new MLEDistribution();
		distNew.readFreqFile(new File(newFreq));
		MLEDistribution distBase = new MLEDistribution();
		distBase.readFreqFile(new File(baseFreq));
		out.print(newFreq);
		out.print("\t"+baseFreq);
		out.print("\t"+distNew.getEvents().size());
		out.print("\t"+distBase.getEvents().size());
		for (int i = 0; i < ests.length; i++) {
			int estSize = ests[i].estimateUnseenEvents(distBase)+distBase.getEvents().size();
			out.print("\t"+ estSize );
		}
		out.println();
		out.close();
	}
	
}
