package de.unikoblenz.west.ldim.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeMap;

import de.unikoblenz.west.ldim.distributions.MLEDistribution;
import de.unikoblenz.west.ldim.sampling.GreedySampleComposition;
import de.unikoblenz.west.ldim.sampling.RandomSampleComposition;
import de.unikoblenz.west.ldim.sampling.SampleComposition;

public class ComposeSample {

	public static final byte RANDOM_SAMPLE = 1;
	public static final byte GREEDY_SAMPLE = 2;
	
	
	public static void main(String[] args) throws IOException {
//		File inFile = new File("data/TimBL-1k.nq");
		if (args.length >= 5) {
			File baseFreqFile = new File(args[0]);
			File sampleFreqDir = new File(args[1]);
			File sizeFile = new File(args[2]);
			File outFile = new File(args[3]);
			double rate = Double.parseDouble(args[4]);
			byte mode = GREEDY_SAMPLE;
			if (args.length >= 6) { 
				String modeString = args[5];
				if (modeString.equalsIgnoreCase("random")) {
					mode = RANDOM_SAMPLE;
				} else if (modeString.equalsIgnoreCase("greedy")) {
					mode = GREEDY_SAMPLE;
				}
			}
			ComposeSample composer = new ComposeSample();
			composer.doSample(baseFreqFile, sampleFreqDir, sizeFile, outFile, rate, mode);
		} else {
			System.out.println("Missing parameters ... ");
			IndexBuilder.printHelp();
		}
	}
	
	public static void printHelp() {
		System.out.println("Usage:");
		System.out.println("  java de.unikoblenz.west.ldim.ComposeSample <baseFile> <samplesDir> <sizeFile> <outFile> <rate> [<mode>]");
		System.out.println("where:");
		System.out.println("  <baseFile>   Frequency file of the base distribution (to be approximated).");
		System.out.println("  <samplesDir> directory containing frequency files of the samples (used to compose the approximation)");
		System.out.println("  <sizeFile>   File specifying the size of sample");
		System.out.println("  <outFile>    Text file to write the names of the selected samples to");
		System.out.println("  <rate>       sampling rate, value in the interval (0,1]");
		System.out.println("  <mode>       One of the following modes:");
		System.out.println("               random   : random sample");
		System.out.println("               greedy   : greedy approximation");
	}
	
	
	
	public void doSample(File baseFreqFile, File sampleFreqDir, File sizeFile, File outFile, double rate, byte mode) throws IOException {
		TreeMap<String, Integer> sizes  = this.readSizeFile(sizeFile);
		SampleComposition composition = null;
		if (mode == RANDOM_SAMPLE) {
			composition = new RandomSampleComposition();
		} else if (mode == GREEDY_SAMPLE) {
			composition = new GreedySampleComposition();
		} else {
			// hu?
		}
		MLEDistribution mleBase = new MLEDistribution();
		mleBase.readFreqFile(baseFreqFile);
		File[] samples = sampleFreqDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("-freq.txt.zip");
			}
		});
		for (File sample : samples) {
			String name = sample.getName().substring(0,sample.getName().indexOf(".gz"));
			if (sizes.containsKey(name)) {
				MLEDistribution sampleDist = new MLEDistribution();
				sampleDist.readFreqFile(sample);
				int sampleSize = sizes.get(name);
				composition.addSample(name, sampleDist, sampleSize);
			} else {
				System.err.println("Size for file "+name+" not known!");
			}
		}
		int maxSize = (int) (rate*sizes.get("base"));
		System.out.println("Sampling using "+(mode == RANDOM_SAMPLE?"RANDOM":"GREEDY")+" mode and maxsize of"+maxSize);
		Set<String> selection = composition.composeSample(mleBase, maxSize);
		PrintStream out = new PrintStream(outFile);
		for (String selected: selection) {
			out.println(selected);
		}
		out.close();
	}
	
	public TreeMap<String, Integer> readSizeFile(File sizeFile) throws IOException {
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		BufferedReader in = new BufferedReader(new FileReader(sizeFile));
		String line = null;
		int totSum = 0; 
		while ((line = in.readLine()) != null) {
			String name = line;
			int size = Integer.parseInt(in.readLine());
			totSum += size;
			result.put(name, size);
		}
		in.close();
		System.out.println("Loaded "+result.keySet().size()+" entries with a total size of "+totSum);
		return result;
	}
	
	
}
