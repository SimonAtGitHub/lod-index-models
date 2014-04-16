package de.unikoblenz.west.ldim.main;

import java.io.File;
import java.io.IOException;

import de.unikoblenz.west.ldim.io.NQuadFileSplitter;

public class FileSplit {

	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			File inFile = new File(args[0]);
			File outDir = new File(args[1]);
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			NQuadFileSplitter nqSplitter = new NQuadFileSplitter(inFile, outDir);
			nqSplitter.run();
		} else {
			System.out.println("Missing parameters ... ");
			FileSplit.printHelp();
		}
	}

	public static void printHelp() {
		System.out.println("Usage:");
		System.out.println("  java de.unikoblenz.west.ldim.main.FileSplit <inputfile> <outputdir>");
		System.out.println("where:");
		System.out.println("  <inputfile>  Name of RDF input file in nquad format (may be in gzip or zip format).");
		System.out.println("  <outputdir>  Name of output dir, where split files will be stored (in gzip format). Will be created if does not exists");
	}

}
