package main;

import index.ResourceManager;
import index.aggregator.CountAggregator;
import index.aggregator.IndexAggregator;
import index.aggregator.InvertedIndexAggregator;
import index.aggregator.SortedCountAggregator;
import index.aggregator.SortedInvertedIndexAggregator;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

public class PerplexityAnalysis {

	/**
	 * Default configuration of which indices will be generated and evaluated. Default is all 12 indices.
	 */
	public static final byte[] defaultConfig = {
		ResourceManager.INDEX_V_TS,
		ResourceManager.INDEX_V_PS,
		ResourceManager.INDEX_V_ECS, 
//		ResourceManager.INDEX_V_ECS_CONTEXT, 
		ResourceManager.INDEX_V_IPS,
//		ResourceManager.INDEX_V_SCHEMEX,
		ResourceManager.INDEX_V_SCHEMEX_JOIN,
		ResourceManager.INDEX_V_S_HASH,
		ResourceManager.INDEX_V_P_HASH,
		ResourceManager.INDEX_V_O_HASH ,
		ResourceManager.INDEX_V_C_HASH ,
		ResourceManager.INDEX_V_PLD_HASH,
		ResourceManager.INDEX_V_TYPE,
		ResourceManager.INDEX_V_KEYWORD
	};
	
	public static final byte AGGREGATION_COUNT = 0;
	public static final byte AGGREGATION_INVERT = 1;
	public static final byte AGGREGATION_ALL = 2;
	
	private static byte aggregationStyle = AGGREGATION_COUNT;
	
	/**
	 * Main class for indexing and generating counts. Takes at least one argument: the file (nq format) which to process. Further arguments enumerate explicitly which indices to build using acronyms. 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		File inFile = new File("data/TimBL-1k.nq");
		if (args.length >= 1) {
			File inFile = new File(args[0]);
			byte[] configs = defaultConfig;
			if (args.length > 1) {
				String aggrModeString = args[1];
				byte aggregationMode = AGGREGATION_COUNT;
				if (aggrModeString.equalsIgnoreCase("freq")) {
					aggregationMode = AGGREGATION_COUNT;
				} else if (aggrModeString.equalsIgnoreCase("inv")) {
					aggregationMode = AGGREGATION_INVERT;
				} else if (aggrModeString.equalsIgnoreCase("all")) {
					aggregationMode = AGGREGATION_ALL;
				} 
				if (args.length > 2) {
					TreeSet<Byte> parsed = new TreeSet<Byte>();
					for (int i = 2; i< args.length; i++) {
						if (args[i].equalsIgnoreCase("s")) {
							parsed.add(ResourceManager.INDEX_V_S_HASH);
						} else if (args[i].equalsIgnoreCase("p")) {
							parsed.add(ResourceManager.INDEX_V_P_HASH);
						} else if (args[i].equalsIgnoreCase("o")) {
							parsed.add(ResourceManager.INDEX_V_O_HASH);
						} else if (args[i].equalsIgnoreCase("c")) {
							parsed.add(ResourceManager.INDEX_V_C_HASH);
						} else if (args[i].equalsIgnoreCase("pld")) {
							parsed.add(ResourceManager.INDEX_V_PLD_HASH);
						} else if (args[i].equalsIgnoreCase("ps")) {
							parsed.add(ResourceManager.INDEX_V_PS);
						} else if (args[i].equalsIgnoreCase("ips")) {
							parsed.add(ResourceManager.INDEX_V_IPS);
						} else if (args[i].equalsIgnoreCase("ts")) {
							parsed.add(ResourceManager.INDEX_V_TS);
						} else if (args[i].equalsIgnoreCase("ecs")) {
							parsed.add(ResourceManager.INDEX_V_ECS);
						} else if (args[i].equalsIgnoreCase("schemex")) {
							parsed.add(ResourceManager.INDEX_V_SCHEMEX);
						} else if (args[i].equalsIgnoreCase("schemexj")) {
							parsed.add(ResourceManager.INDEX_V_SCHEMEX_JOIN);
						} else if (args[i].equalsIgnoreCase("keyword")) {
							parsed.add(ResourceManager.INDEX_V_KEYWORD);
						} else if (args[i].equalsIgnoreCase("type")) {
							parsed.add(ResourceManager.INDEX_V_TYPE);
						} else if (args[i].equalsIgnoreCase("ecscontext")) {
							parsed.add(ResourceManager.INDEX_V_ECS_CONTEXT);
						} else {
							System.out.print("Unknown index type: "+args[i]);
						}
					}
					configs = new byte[parsed.size()];
					int pos = 0;
					for (byte value : parsed) {
						configs[pos] = value;
						pos++;
					}
				}
				PerplexityAnalysis.buildFrequencies(inFile, configs, aggregationMode);
			} else {
				System.out.println("Missing an aggregation mode parameter ... ");
				PerplexityAnalysis.printHelp();
			} 
		} else {
			System.out.println("Unknown arguments: ");
			for (String arg : args) {
				System.out.print(arg+" ");
			}
			System.out.println();
			PerplexityAnalysis.printHelp();
		}
	}
	
	public static void printHelp() {
		System.out.println("Usage:");
		System.out.println("  java PerplexityAnalysis <inputfile> [<aggregate> <index> ...]");
		System.out.println("where:");
		System.out.println("  <inputfile>  Name of RDF input file in nquad format.");
		System.out.println("  <aggegrate>  One or several inicators for aggregating indices (optional):");
		System.out.println("               freq     : frequency aggregation");
		System.out.println("               inv      : inverted list aggregation");
		System.out.println("               all      : all available aggregations");
		System.out.println("  <index>      One or several inicators for indices to be evaluated (optional):");
		System.out.println("               s        : subject index");
		System.out.println("               p        : predicate index");
		System.out.println("               o        : object index");
		System.out.println("               c        : context index");
		System.out.println("               pld      : PLD (context) index");
		System.out.println("               ps       : property set index");
		System.out.println("               ips      : incoming property set index");
		System.out.println("               ts       : type set index");
		System.out.println("               ecs      : extended characteristic set index");
		System.out.println("               schemex  : schemex index");
		System.out.println("               schemexj : schemex index (via JOIN) ");
		System.out.println("               keyword  : keyword (literals) index");
		System.out.println("               type     : RDF type index");
		System.out.println("               ecscontext : extended characteristic set index for looking up contexts");
	}
	
	
	public static void buildFrequencies(File raw, byte[] configs, byte aggregationMode) {

		
		ResourceManager rm = new ResourceManager();
		
		long t_start = System.currentTimeMillis();
		for (byte setting : configs) {
			try {
				File index = rm.get(setting, raw);
				System.out.print("Aggregating ... ");
				long t1 = System.currentTimeMillis();
				PerplexityAnalysis.aggregate(index, aggregationMode);
				long t2 = System.currentTimeMillis();
				System.out.println(((t2-t1)/1000)+" sec");
			} catch (Exception e) {
				System.err.println("Oups -- something went wrong here");
				e.printStackTrace();
			}
		}
		rm.cleanupFiles();
		long t_stop = System.currentTimeMillis();
		System.out.println("Total: "+((t_stop-t_start)/1000)+" sec");
		
	}
	
//	private static void aggregateIndex2(File indexFile, File aggregateFile) {
//		IndexAggregator aggr = new CountAggregator();
//		aggr.run(indexFile, aggregateFile);
//	}
	
	private static void aggregate(File indexFile, byte mode) {
		boolean runInvert = (mode == AGGREGATION_INVERT) || (mode == AGGREGATION_ALL);
		boolean runCount = (mode == AGGREGATION_COUNT) || (mode == AGGREGATION_ALL);
		if (runInvert) {
			File invFile = new File(indexFile.getName()+"-inv.txt.zip");

			invertIndex(indexFile, invFile);
		}
		if (runCount) {
			File freqFile = new File(indexFile.getName()+"-freq.txt.zip");
			aggregateIndex(indexFile, freqFile);
		}
	}

	private static void aggregateIndex(File indexFile, File aggregateFile) {
		IndexAggregator aggr = new SortedCountAggregator();
		System.out.print("  Aggregating frequencies... ("+aggregateFile.getName()+")");
		aggr.run(indexFile, aggregateFile);
	}
	
//	private static void invertIndex2(File indexFile, File invertFile) {
//		IndexAggregator aggr = new InvertedIndexAggregator();
//		aggr.run(indexFile, invertFile);
//	}
	
	private static void invertIndex(File indexFile, File invertFile) {
		IndexAggregator aggr = new SortedInvertedIndexAggregator();
		System.out.print("  Inverted index construction... ("+invertFile.getName()+")");
		aggr.run(indexFile, invertFile);
	}
	
}

