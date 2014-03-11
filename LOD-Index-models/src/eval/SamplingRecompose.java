package eval;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import tools.Utilities;

public class SamplingRecompose {

	public static void main(String[] args) throws IOException {
		if (args.length >= 1) {
			File dir = new File(args[0]);
			Utilities.samplingCompose(dir);
		}
	}
}
