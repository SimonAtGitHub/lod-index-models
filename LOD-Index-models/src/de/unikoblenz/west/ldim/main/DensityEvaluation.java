package de.unikoblenz.west.ldim.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.distributions.DeletedInterpolationUnseenEstimator;
import de.unikoblenz.west.ldim.distributions.Distribution;
import de.unikoblenz.west.ldim.distributions.GoodTuringDistribution;
import de.unikoblenz.west.ldim.distributions.GoodTuringUnseenEstimator;
import de.unikoblenz.west.ldim.distributions.HeldOutDistribution;
import de.unikoblenz.west.ldim.distributions.LaplaceDistribution;
import de.unikoblenz.west.ldim.distributions.MLEDistribution;
import de.unikoblenz.west.ldim.distributions.OneUnseenEvent;
import de.unikoblenz.west.ldim.distributions.UniformDistribution;
import de.unikoblenz.west.ldim.distributions.UnseenEventEstimator;
import de.unikoblenz.west.ldim.eval.DistributionCompare;

public class DensityEvaluation {

	public static final byte LAPLACE_ESTIMATOR = 0;
	public static final byte LIDSTONE_05_ESTIMATOR = 1;
	public static final byte LIDSTONE_01_ESTIMATOR = 2;
	public static final byte LIDSTONE_001_ESTIMATOR = 3;
	public static final byte HELDOUT_ESTIMATOR = 4;
	public static final byte DELETED_INTERPOLATION_ESTIMATOR = 5;
	public static final byte GOOD_TURING_ESTIMATOR = 6;
	public static final byte UNIFORM_ESTIMATOR = 7;
	
	public static final String[] MODE_LABELS = {"laplace", "lid.5", "lid.1", "lid.01", "heldout", "delint", "gt", "uniform"};
	
	public static final String TAB = "\t";
	public static final String AXIS = "File";
	
	public static void main(String[] args) throws IOException {
		if (args.length >= 3) {
			String baseFile = args[0];
			String newFile = args[1];
			String resultFile = args[2];
			DensityEvaluation eval = new DensityEvaluation();
			byte mode = LIDSTONE_001_ESTIMATOR;
			if (args.length >= 4) {
				if (args[3].equals(MODE_LABELS[LAPLACE_ESTIMATOR])) {
					mode = LAPLACE_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[LIDSTONE_05_ESTIMATOR])) {
					mode = LIDSTONE_05_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[LIDSTONE_01_ESTIMATOR])) {
					mode = LIDSTONE_01_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[LIDSTONE_001_ESTIMATOR])) {
					mode = LIDSTONE_001_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[HELDOUT_ESTIMATOR])) {
					mode = HELDOUT_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[DELETED_INTERPOLATION_ESTIMATOR])) {
					mode = DELETED_INTERPOLATION_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[GOOD_TURING_ESTIMATOR])) {
					mode = GOOD_TURING_ESTIMATOR;
				} else if (args[3].equals(MODE_LABELS[UNIFORM_ESTIMATOR])) {
					mode = UNIFORM_ESTIMATOR;
				} 
			}
			File resultF = new File(resultFile);
			if (! resultF.exists()) {
				eval.createResultsFile(resultF);
			}
			eval.appendResults(baseFile, newFile, resultF, mode);
		} else {
			System.out.println("Usage:");
			System.out.println("  java DensityEvaluation <baseindex> <newindex> <resultfile> [<mode>]");
			System.out.println("where:");
			System.out.println("  <baseindex>  The frequency file used for the base distribution, i.e. using this distribution to explain the new distribution.");
			System.out.println("  <newindex>   The frequency file used for the new distribution, i.e. using this distribution which is approximated by the base distribution.");
			System.out.println("  <resultfile> File to write the results to. The file will be created if it does not exist. Otherwsie the results are appended to the end of the file (new entry in a CSV table).");
			System.out.println("  <mode>       Type of smoothing technique to use on the base distribution. (The new distribution is not smoothed and uses MLE). Options are");
			for (String mlabel: MODE_LABELS) {
				System.out.println("               "+mlabel);
			}
		}
//		String[] fileNames = {"data-1.txt","data-2.txt","data-3.txt","data-4.txt","data-5.txt","data-6.txt"};
//		Distribution[] dists = new Distribution[fileNames.length];
//		for (int i = 0; i<fileNames.length; i++) {
//			dists[i]  = new Distribution();
//			dists[i].readFreqFile(new File("data/perplexity-example/"+fileNames[i]));
//		}
//		System.out.println("Dist "+fileNames[0]+" : ");
//		System.out.println("\t Entropy       : "+dists[0].entropy());
//		for (int i = 1; i < dists.length; i++) {
//			System.out.println("Dist "+fileNames[i]+" : ");
//			System.out.println("\t Entropy        : "+dists[i].entropy());
//			System.out.println("\t Cross Entropy  : "+dists[i].crossEntropy(dists[0]));
//			System.out.println("\t Perplexity     : "+dists[i].perplexity(dists[0]));
//			System.out.println("\t KL Divergence  : "+dists[i].kullbackLeibler(dists[0]));
//			System.out.println("\t Jacard         : "+dists[i].jacardSimilarityEvents(dists[0]));
//			System.out.println("\t unknown Events : "+dists[i].unknownEvents(dists[0]));
//		}
	}
	
	private Distribution[] loadDists(String baseFreq, String newFreq, byte mode) throws IOException {
		Distribution[] result = new Distribution[2];
		Distribution distNew = new MLEDistribution();
		distNew.readFreqFile(new File(newFreq));
		result[1] = distNew;
		Distribution distBase = null;
		// create right type of object
		switch (mode) {
		case LAPLACE_ESTIMATOR:
			distBase = new LaplaceDistribution(1);
			break;
		case LIDSTONE_05_ESTIMATOR:
			distBase = new LaplaceDistribution(0.5);
			break;
		case LIDSTONE_01_ESTIMATOR:
			distBase = new LaplaceDistribution(0.1);
			break;
		case LIDSTONE_001_ESTIMATOR:
			distBase = new LaplaceDistribution(0.01);
			break;
		case HELDOUT_ESTIMATOR:
			distBase = new HeldOutDistribution();
			break;
		case DELETED_INTERPOLATION_ESTIMATOR:
			distBase = new HeldOutDistribution();
			break;
		case GOOD_TURING_ESTIMATOR:
			distBase = new GoodTuringDistribution();
			break;
		case UNIFORM_ESTIMATOR:
			distBase = new UniformDistribution();
			break;
		default:
			System.err.println("Unknown Estimator type: "+mode);
		}
		distBase.readFreqFile(new File(baseFreq));
		result[0] = distBase;
		// additional settings ...
		switch (mode) {
		case LAPLACE_ESTIMATOR:
		case LIDSTONE_05_ESTIMATOR:
		case LIDSTONE_01_ESTIMATOR:
		case LIDSTONE_001_ESTIMATOR:
			TreeSet<String> eventUnion = new TreeSet<String>();
//			eventUnion.addAll(distNew.getEvents());
			eventUnion.addAll(distBase.getEvents());
			( (LaplaceDistribution) distBase).setEventsSpaceSize(eventUnion.size()+1);
			distBase = new LaplaceDistribution(0.01);
			break;
		default:
			// nothing to do
		}
		return result;
	}
	
	public void appendResults(String baseFreq, String newFreq, File resultFile, byte mode) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		DistributionCompare distCompare = new DistributionCompare();
		Distribution[] loadedDists = this.loadDists(baseFreq, newFreq, mode);
		Distribution distBase = loadedDists[0];
		Distribution distNew = loadedDists[1];
		out.print(newFreq);
		out.print(TAB+MODE_LABELS[mode]);
		out.print(TAB+distCompare.entropy(distNew));
		out.print(TAB+distCompare.entropy(distBase));
		out.print(TAB+distCompare.entropyNorm(distNew));
		out.print(TAB+distCompare.entropyNorm(distBase));
		out.print(TAB+distCompare.crossEntropy(distBase, distNew));
		out.print(TAB+distCompare.perplexity(distBase, distNew));
		out.print(TAB+distCompare.kullbackLeibler(distBase, distNew));
		out.print(TAB+distCompare.jacardSimilarityEvents(distBase, distNew));
		out.print(TAB+distBase.getEvents().size());
		out.print(TAB+distNew.getEvents().size());
		out.print(TAB+distCompare.unknownEvents(distBase, distNew));
		out.println();
		out.close();
	}
	
	public void createResultsFile(File resultFile) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		out.print(AXIS);
		out.print(TAB+"Estimator");
		out.print(TAB+"Entropy (New)");
		out.print(TAB+"Entropy (Base)");
		out.print(TAB+"Entropy-norm (New)");
		out.print(TAB+"Entropy-norm (Base)");
		out.print(TAB+"Cross Entropy");
		out.print(TAB+"Perplexity");
		out.print(TAB+"KL Divergence");
		out.print(TAB+"Jacard");
		out.print(TAB+"Events in Base");
		out.print(TAB+"Events in New");
		out.print(TAB+"unknown New Events in Base");
		out.println();
		out.close();		
	}
	
}
