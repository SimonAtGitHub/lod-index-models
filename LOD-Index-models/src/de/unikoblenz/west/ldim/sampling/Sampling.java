package de.unikoblenz.west.ldim.sampling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;
import de.unikoblenz.west.ldim.io.NQuadWriter;
import de.unikoblenz.west.ldim.io.sorting.DefaultSorting;
import de.unikoblenz.west.ldim.tools.Utilities;

public class Sampling {

	public static void main(String[] args) throws IOException {
		File main = new File("data/data-seedlist.nq");
		File tripleHash = new File ("data/sample/dyldo-spo-hash.txt");
		File usuHash = new File ("data/sample/dyldo-usu-hash.txt");
		File contextHash = new File ("data/sample/dyldo-context-hash.txt");
		Sampling s = new Sampling();
		System.out.println("Creating Hashes...");
		if (! tripleHash.exists()) {
			s.createHashes(main, tripleHash, usuHash, contextHash);
		}
		String i = "100";
		double[] rates = {0.6, 0.7, 0.8, 0.9};
		if (args.length > 0) {
			i = args[0];
			if (args.length > 1) {
				double r = Double.parseDouble(args[1]);
				rates = new double[1];
				rates[0] =r;
			}
		}
//		double[] rates = {0.5, 0.25, 0.1, 0.05, 0.01, 0.005};
//		for (int i = 100; i <120; i++) {
			for (double rate : rates) {
				System.out.println("Sampling triple hashes at rate "+rate+" ...");
				HashSet<String> tripleRetain = s.sampleHashes(tripleHash, rate);

				System.out.println("Sampling USUs hashes at rate "+rate+" ...");
				HashSet<String> usuRetain = s.sampleHashes(usuHash, rate);

				System.out.println("Sampling contexts hashes at rate "+rate+" ...");
				HashSet<String> contextRetain = s.sampleHashes(contextHash, rate);

				File tsOut = new File("data/sample/dyldo-spo-s"+i+"-r"+rate+".nq");
				File usOut = new File("data/sample/dyldo-usu-s"+i+"-r"+rate+".nq");
				File csOut = new File("data/sample/dyldo-context-s"+i+"-r"+rate+".nq");
				System.out.println("Filtering data based on  hashes ...");
				s.filterParallelData(main, tsOut, usOut, csOut, tripleRetain, usuRetain, contextRetain);
			}
//		}
	}
	
	/**
	 * Creates from a file of RDF Tripels in NQ format two files containing unique hashes over: (1) the triples (spo) and (2) the USUs (subjects).
	 * @param nquadIn file to read to original data from
	 * @param tripleHashesOut File to serialize the hashes over triples
	 * @param usuHashesOut File to serializes to hashes over USUs
	 * @throws IOException IO error
	 */
	public void createHashes(File nquadIn, File tripleHashesOut, File usuHashesOut, File contextHashOut) throws IOException {
		NQuadParser nqIn = new NQuadParser(nquadIn);
		PrintStream tripleOut = new PrintStream(tripleHashesOut);
		PrintStream usuOut = new PrintStream(usuHashesOut);
		PrintStream contextOut = new PrintStream(contextHashOut);
		
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			tripleOut.println(this.hashTriple(quad));
			usuOut.println(this.hashUsu(quad));
			contextOut.println(this.hashContext(quad));
		}
		
		tripleOut.close();
		usuOut.close();
		contextOut.close();
		// Call uniq function to remove duplicates
		this.uniq(tripleHashesOut);
		this.uniq(usuHashesOut);
		this.uniq(contextHashOut);
	}
	
	/**
	 * Filter the triples in an NQ file format based on the hashes over triples. The triples to retain are provided by a set of hash values.
	 * @param nquadIn 
	 * @param filterOut
	 * @param retainHashes
	 * @throws IOException
	 */
	public void filterTripleData(File nquadIn, File filterOut, HashSet<String> retainHashes) throws IOException {
		NQuadParser nqIn = new NQuadParser(nquadIn);
		NQuadWriter nqOut = new NQuadWriter(filterOut);
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			if (retainHashes.contains(this.hashTriple(quad))) {
				nqOut.write(quad);
			}
		}
		nqOut.close();
	}

	public void filterUsuData(File nquadIn, File filterOut, HashSet<String> retainHashes) throws IOException {
		NQuadParser nqIn = new NQuadParser(nquadIn);
		NQuadWriter nqOut = new NQuadWriter(filterOut);
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			if (retainHashes.contains(this.hashUsu(quad))) {
				nqOut.write(quad);
			}
		}
		nqOut.close();
	}

	public void filterContextData(File nquadIn, File filterOut, HashSet<String> retainHashes) throws IOException {
		NQuadParser nqIn = new NQuadParser(nquadIn);
		NQuadWriter nqOut = new NQuadWriter(filterOut);
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			if (retainHashes.contains(this.hashContext(quad))) {
				nqOut.write(quad);
			}
		}
		nqOut.close();
	}

	public void filterParallelData(File nquadIn, File filterTripleOut, File filterUsuOut, File filterContextOut, HashSet<String> retainTriple, HashSet<String> retainUsu, HashSet<String> retainContext) throws IOException {
		NQuadParser nqIn = new NQuadParser(nquadIn);
		NQuadWriter nqTripleOut = new NQuadWriter(filterTripleOut);
		NQuadWriter nqUsuOut = new NQuadWriter(filterUsuOut);
		NQuadWriter nqContextOut = new NQuadWriter(filterContextOut);
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			if (retainTriple.contains(this.hashTriple(quad))) {
				nqTripleOut.write(quad);
			}
			if (retainUsu.contains(this.hashUsu(quad))) {
				nqUsuOut.write(quad);
			}
			if (retainContext.contains(this.hashContext(quad))) {
				nqContextOut.write(quad);
			}
		}
		nqTripleOut.close();
		nqUsuOut.close();
		nqContextOut.close();
	}

	public HashSet<String> sampleHashes(File hashFile, double p) throws IOException {
		HashSet<String> result = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(hashFile));
		String hash = null;
		while ( (hash = in.readLine()) != null ) {
			double random = Math.random();
			if (random <= p) {
				result.add(hash);
			}
		}
		in.close();
		return result;
	}
	
	public String hashTriple(NQuad quad) {
		String result = null;
		TreeSet<String> collect = new TreeSet<String>();
		// Add prefixes to assure a consistent order of subject, predicate and object in the treeset and the subsequent serialization.
		collect.add("0_"+quad.subject);
		collect.add("1_"+quad.predicate);
		collect.add("2_"+quad.object);
		result = Utilities.md5Hash(Utilities.concatenate(collect));
		return result;
	}
	
	public String hashUsu(NQuad quad) {
		String result = null;
		TreeSet<String> collect = new TreeSet<String>();
		collect.add(quad.subject);
		result = Utilities.md5Hash(Utilities.concatenate(collect));
		return result;
	}
	
	public String hashContext(NQuad quad) {
		String result = null;
		TreeSet<String> collect = new TreeSet<String>();
		collect.add(quad.context);
		result = Utilities.md5Hash(Utilities.concatenate(collect));
		return result;
	}
	
	public void uniq(File hashFile) throws IOException {
		File tmp = new File ("tmp.txt");
		DefaultSorting sort = new DefaultSorting();
		sort.run(hashFile, tmp);
		BufferedReader in = new BufferedReader(new FileReader(tmp));
		PrintStream out = new PrintStream(hashFile);
		String last = null;
		String line = null;
		while ( (line = in.readLine()) != null) {
			if ( (last == null) || (! line.equals(last))) {
				out.println(line);
				last = line;
			}
		}
		tmp.delete();
		in.close();
		out.close();
		
	}
	
	
	
}

