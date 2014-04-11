package de.unikoblenz.west.ldim.eval;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import de.unikoblenz.west.ldim.tools.Utilities;

public class Recompose {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			File inDir = new File(args[0]);
			File outDir = new File(args[1]);
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			File[] fList = inDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					return (arg0.getName().startsWith("d-") && arg0.getName().endsWith(".txt.csv"));
				}
			});
			Utilities.recompose(fList, outDir);
		}

	}

}
