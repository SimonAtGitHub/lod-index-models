package de.unikoblenz.west.ldim.eval;

import java.io.File;
import java.io.IOException;

import de.unikoblenz.west.ldim.tools.Utilities;

public class SamplingRecompose {

	public static void main(String[] args) throws IOException {
		if (args.length >= 1) {
			File dir = new File(args[0]);
			Utilities.samplingCompose(dir);
		}
	}
}
