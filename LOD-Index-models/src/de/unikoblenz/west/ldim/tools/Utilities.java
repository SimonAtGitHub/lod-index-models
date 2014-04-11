package de.unikoblenz.west.ldim.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.main.DensityEvaluation;

public class Utilities {

	public static final String SPLIT = "\\t";
	public static final char CONCATENATION_TOKEN = ' ';
	public static final char CONCATENATION_SUBSTITUTE = '_';
	
	public static final byte PLAIN_HASH = 0;
	public static final byte MD5_HASH = 1;
	
	public static byte hashSetting = MD5_HASH;
	
	public static String staticHash(TreeSet<String> in) {
		String result = Utilities.concatenate(in);
		switch (hashSetting) {
		case MD5_HASH:
			result = Utilities.md5Hash(result);
			break;
		case PLAIN_HASH:
		default:
			// leave result as plain.
			try {
				result = URLEncoder.encode(result, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
				
		return result;
	}
	
	public static String concatenateHashMD5(TreeSet<String> in) {
		String result = null;
		TreeSet<String> hashes = new TreeSet<String>();
		for (String entry : in) {
			hashes.add(Utilities.md5Hash(entry));
		}
		result = Utilities.concatenate(hashes);
		return result;
	}
	
	public static String concatenate(TreeSet<String> in) {
		StringBuffer buffer = new StringBuffer();
		for (String element : in) {
			buffer.append(element.trim().replaceAll(CONCATENATION_TOKEN+"", CONCATENATION_SUBSTITUTE+""));
			buffer.append(CONCATENATION_TOKEN);
		}
		String result = buffer.toString();
		return result;
	}

	public static TreeSet<String> decompoase(String in) {
		TreeSet<String> result = new TreeSet<String>();
		String[] entries = in.split(" ");
		for (String entry : entries) {
			result.add(entry);
		}
		return result;
	}
	

	
	public static String md5Hash(String in) {
		byte[] bytesOfMessage = null;
	    try {
	        bytesOfMessage = in.getBytes("UTF-8");
	    } catch (UnsupportedEncodingException e1) {
	        e1.printStackTrace();
	    }

	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("MD5");
	    } catch (NoSuchAlgorithmException e1) {
	        e1.printStackTrace();
	    }
		byte[] digest = md.digest(bytesOfMessage);
		BigInteger bigInt = new BigInteger(1,digest);
		String result = bigInt.toString(16);
		// fill up with zeros to get a string with length 32
		while(result.length() < 32 ){
			result = "0"+result;
		}	    
		return result;
	}
	
	public static void recompose(File[] indexOriented, File outDir) throws IOException {
		BufferedReader[] in = new BufferedReader[indexOriented.length];
		// Map: metric to indices to arrays of values
		TreeMap<String,TreeMap<String, ArrayList<String>>> map = new TreeMap<String,TreeMap<String, ArrayList<String>>>();
		for (int i = 0; i < in.length; i++) {
			in[i] = new BufferedReader(new FileReader(indexOriented[i]));
			String header = in[i].readLine();
			String[] metrics = header.split(SPLIT);
			for (int j = 0; j < metrics.length; j++) {
				if (! map.containsKey(metrics[j])) {
					map.put(metrics[j], new TreeMap<String, ArrayList<String>>());
				}
			}
			String line = null;
			while ( (line = in[i].readLine()) != null) {
				String[] values = line.split(SPLIT);
				if (values.length == metrics.length) {
					for (int j = 0; j < metrics.length; j++) {
						ArrayList<String> results = new ArrayList<String>();
						if (map.get(metrics[j]).containsKey(indexOriented[i].getName())) {
							results = map.get(metrics[j]).get(indexOriented[i].getName());
						}
						results.add(values[j]);
						map.get(metrics[j]).put(indexOriented[i].getName(),results);
					}
				}
			}
			in[i].close();
		}
		TreeMap<String,ArrayList<String>> axis = map.get(DensityEvaluation.AXIS);
		for (String metric : map.keySet()) {
			if (! metric.equals(DensityEvaluation.AXIS)) {
				File fout = new File(outDir, "results--"+metric+".csv");
				PrintStream out = new PrintStream(fout);
				TreeMap<String, ArrayList<String>> indexValues = map.get(metric);
				out.print(DensityEvaluation.AXIS);
				for (String index : indexValues.keySet()) {
					out.print(DensityEvaluation.TAB+index);
				}
				out.println();
				ArrayList<String> axisEntries = axis.get(axis.keySet().iterator().next());
				for (int i = 0 ; i < axisEntries.size(); i++) {
					out.print(axisEntries.get(i));
					for (String index : indexValues.keySet()) {
						out.print(DensityEvaluation.TAB+indexValues.get(index).get(i));
					}
					out.println();
				}
				out.close();
			}
		}
	}
	
	public static void samplingCompose(File inDir) throws IOException {
//		String[] SAMPLES= "s100 s101 s102 s103 s104 s105 s106 s107 s108 s109 s110 s111 s112 s113 s114 s115 s116 s117 s118 s119"
		String[] MODES = {"context", "spo",  "usu"};
//		String[] MODES = {"context", "spo"};
//		String[] RATES= {"0.5", "0.25", "0.1", "0.05", "0.01", "0.005"};
		String[] RATES= {"0.05", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9"};
		String[] ESTS= {"laplace", "lid.5", "lid.1", "lid.01", "gt"};
//		String[] ESTS= {"laplace", "lid.01", "gt", "uniform"};
		String[] INDICES={"ps", "ts", "ecs", "p", "type"};
//		String[] INDICES={"ps", "ts", "type"};
		String[] METRICS_LABEL = {"Entropy (New)", "Entropy (Base)", "Entropy-norm (New)", "Entropy-norm (Base)", "Cross Entropy", "Perplexity", "KL Divergence", "Jacard", "Events in Base", "Events in New", "unknown New Events in Base"};
		String[] METRICS = {"entropy-new", "entropy-base", "entropy-norm-new", "entropy-norm-base", "cross-entropy", "perplexity", "kl-div", "jaccard", "events-base", "events-new", "unknown-events"};
		double[] VAL_1 = { -1, -1, -1, -1, -1, -1, 0, 1, -1, -1, 0};
		
		
		for (String index : INDICES) {
			System.out.println(index+"...");
			for (String mode : MODES) {
				System.out.println(mode+"...");
				for (int i = 0; i < METRICS.length; i++) {
					String metric = METRICS[i];
					PrintStream out = new PrintStream(new File(inDir,"sampling-"+index+"-"+mode+"-"+metric+".csv"));
					out.print("Rate");
					for (String est : ESTS) {
						out.print("\t"+est);
					}
					out.println();
					for (String rate : RATES) {
						out.print(rate);
						for (String est : ESTS) {
							BufferedReader in = new BufferedReader(new FileReader(new File(inDir,"d-result-"+index+"-"+mode+"-"+rate+"-"+est+".csv")));
							in.readLine();
							String line = null;
							int sampleCnt = 0;
							double sum = 0;
							while ( (line = in.readLine())!= null) {
								String[] entries = line.split(SPLIT);
								double value = Double.parseDouble(entries[i+2]);
								sum+= value;
								sampleCnt++;
							}
							double mean = sum / sampleCnt; 
							out.print("\t"+mean);
							in.close();
						}
						out.println();
					}
					if (VAL_1[i] >= 0) {
						out.print("1.0");
						for (String est : ESTS) {
							out.print("\t"+VAL_1[i]);
						}						
						out.println();
					}
					out.close();
				}
			}
		}


	}
	
}
