package de.unikoblenz.west.ldim.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import de.unikoblenz.west.ldim.eval.QueryCompare;

public class QueryEvaluation {

	public static final String TAB = "\t";
	public static final String AXIS = "File";
	
	public static void main(String[] args) throws IOException {
		if (args.length == 3) {
			String baseFile = args[0];
			String newFile = args[1];
			String resultFile = args[2];
			QueryEvaluation eval = new QueryEvaluation();
			File resultF = new File(resultFile);
			if (! resultF.exists()) {
				eval.createResultsFile(resultF);
			}
			eval.appendResults(baseFile, newFile, resultF);
		} else {
			System.out.println("Usage:");
			System.out.println("  java QueryEvaluation <baseindex> <newindex> <resultfile>");
			System.out.println("where:");
			System.out.println("  <baseindex>  The inverted index file used for the base index, i.e. using this index as approximation.");
			System.out.println("  <newindex>   The inverted index file used for the new index, i.e. using this index as gold standard.");
			System.out.println("  <resultfile> File to write the results to. The file will be created if it does not exist. Otherwsie the results are appended to the end of the file (new entry in a CSV table).");
		}
	}

	public void appendResults(String baseIndex, String newIndex, File resultFile) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		QueryCompare qCompare = new QueryCompare();
		qCompare.compare(baseIndex, newIndex);
		
		out.print(newIndex);
		out.print(TAB+qCompare.macroRecall());
		out.print(TAB+qCompare.macroPrecision());
		out.print(TAB+qCompare.macroF1());
		out.print(TAB+qCompare.microRecall());
		out.print(TAB+qCompare.microPrecision());
		out.print(TAB+qCompare.microF1());
		out.print(TAB+qCompare.missedEntries);
		out.println();
		out.close();
	}
	
	public void createResultsFile(File resultFile) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(resultFile, true));
		out.print(AXIS);
		out.print(TAB+"Macro Avg Recall");
		out.print(TAB+"Macro Avg Precision");
		out.print(TAB+"Macro Avg F1");
		out.print(TAB+"Micro Avg Recall");
		out.print(TAB+"Micro Avg Precision");
		out.print(TAB+"Micro Avg F1");
		out.print(TAB+"unknown New Events in Base");
		out.println();
		out.close();		
	}
	
}

