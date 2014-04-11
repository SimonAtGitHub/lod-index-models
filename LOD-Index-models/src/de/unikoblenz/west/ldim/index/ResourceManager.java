package de.unikoblenz.west.ldim.index;

import java.io.File;
import java.util.HashMap;

import de.unikoblenz.west.ldim.index.models.ContextHashIndex;
import de.unikoblenz.west.ldim.index.models.EcsContextLookupIndex;
import de.unikoblenz.west.ldim.index.models.ExtendedCharacteristicSetIndexing;
import de.unikoblenz.west.ldim.index.models.IncomingPropertySetIndexing;
import de.unikoblenz.west.ldim.index.models.KeyWordIndex;
import de.unikoblenz.west.ldim.index.models.ObjectHashIndex;
import de.unikoblenz.west.ldim.index.models.PldHashIndex;
import de.unikoblenz.west.ldim.index.models.PredicateHashIndex;
import de.unikoblenz.west.ldim.index.models.PropertySetIndexing;
import de.unikoblenz.west.ldim.index.models.SchemEXJoinIndexing;
import de.unikoblenz.west.ldim.index.models.SchemExIndexing;
import de.unikoblenz.west.ldim.index.models.SubjectHashIndex;
import de.unikoblenz.west.ldim.index.models.TypeIndex;
import de.unikoblenz.west.ldim.index.models.TypeSetIndexing;
import de.unikoblenz.west.ldim.index.sorting.IndexSorting;
import de.unikoblenz.west.ldim.index.sorting.NQuadSorting;

public class ResourceManager {

	/** 
	 * Constant for denoting the file related to a list of nquads sorted by subject
	 */
	public static final byte SORTED_SUBJECT = 0;
	/** 
	 * Constant for denoting the file related to a list of nquads sorted by predicate
	 */
	public static final byte SORTED_PREDICATE = 1;
	/** 
	 * Constant for denoting the file related to a list of nquads sorted by object
	 */
	public static final byte SORTED_OBJECT = 2;
	/** 
	 * Constant for denoting the file related to a list of nquads sorted by context
	 */
	public static final byte SORTED_CONTEXT = 3;
	/** 
	 * Constant for denoting the file related to a type set (TS) Index sorted by the key entry
	 */
	public static final byte INDEX_K_TS = 4;
	/** 
	 * Constant for denoting the file related to a type set (TS) Index sorted by the value entry
	 */
	public static final byte INDEX_V_TS = 5;
	/** 
	 * Constant for denoting the file related to a property set (PS) Index sorted by the key entry
	 */
	public static final byte INDEX_K_PS = 6;
	/** 
	 * Constant for denoting the file related to a property set (PS) Index sorted by the value entry
	 */
	public static final byte INDEX_V_PS = 7;
	/** 
	 * Constant for denoting the file related to an extended characteristic set (ECS) Index sorted by the key entry
	 */
	public static final byte INDEX_K_ECS = 8;
	/** 
	 * Constant for denoting the file related to an extended characteristic set (ECS) Index sorted by the value entry
	 */
	public static final byte INDEX_V_ECS = 9;
	/** 
	 * Constant for denoting the file related to an incoming property set (IPS) Index sorted by the key entry
	 */
	public static final byte INDEX_K_IPS = 10;
	/** 
	 * Constant for denoting the file related to an incoming property set (IPS) Index sorted by the value entry
	 */
	public static final byte INDEX_V_IPS = 11;
	/** 
	 * Constant for denoting the file related to a SchemEX Index sorted by the key entry
	 */
	public static final byte INDEX_K_SCHEMEX = 12;
	/** 
	 * Constant for denoting the file related to a SchemEX Index sorted by the value entry
	 */
	public static final byte INDEX_V_SCHEMEX = 13;
	/** 
	 * Constant for denoting the file related to a subject value index sorted by the key entry
	 */
	public static final byte INDEX_K_S_HASH = 14;
	/** 
	 * Constant for denoting the file related to a subject value index sorted by the value entry
	 */
	public static final byte INDEX_V_S_HASH = 15;
	/** 
	 * Constant for denoting the file related to a predicate value index sorted by the key entry
	 */
	public static final byte INDEX_K_P_HASH = 16;
	/** 
	 * Constant for denoting the file related to a predicate value index sorted by the value entry
	 */
	public static final byte INDEX_V_P_HASH = 17;
	/** 
	 * Constant for denoting the file related to a object value index sorted by the key entry
	 */
	public static final byte INDEX_K_O_HASH = 18;
	/** 
	 * Constant for denoting the file related to a object value index sorted by the value entry
	 */
	public static final byte INDEX_V_O_HASH = 19;
	/** 
	 * Constant for denoting the file related to a context value index sorted by the key entry
	 */
	public static final byte INDEX_K_C_HASH = 20;
	/** 
	 * Constant for denoting the file related to a context value index sorted by the value entry
	 */
	public static final byte INDEX_V_C_HASH = 21;
	/** 
	 * Constant for denoting the file related to a pay level domain (PLD) index sorted by the key entry
	 */
	public static final byte INDEX_K_PLD_HASH = 22;
	/** 
	 * Constant for denoting the file related to a pay level domain (PLD) index sorted by the value entry
	 */
	public static final byte INDEX_V_PLD_HASH = 23;
	/** 
	 * Constant for denoting the file related to a rdf:type index index sorted by the key entry
	 */
	public static final byte INDEX_K_TYPE = 24;
	/** 
	 * Constant for denoting the file related to a rdf:type index index sorted by the value entry
	 */
	public static final byte INDEX_V_TYPE = 25;
	/** 
	 * Constant for denoting the file related to a keyword based index sorted by the key entry
	 */
	public static final byte INDEX_K_KEYWORD = 26;
	/** 
	 * Constant for denoting the file related to a keyword based index sorted by the value entry
	 */
	public static final byte INDEX_V_KEYWORD = 27;
	/** 
	 * Constant for denoting the file related to a SchemEX Index generated using a JOIN sorted by the key entry
	 */
	public static final byte INDEX_K_SCHEMEX_JOIN = 28;
	/** 
	 * Constant for denoting the file related to a SchemEX Index generated using a JOIN sorted by the value entry
	 */
	public static final byte INDEX_V_SCHEMEX_JOIN = 29;
	/** 
	 * Constant for denoting the file related to a ECS index referring to contexts (instead of USUs) sorted by the key entry
	 */
	public static final byte INDEX_K_ECS_CONTEXT = 30;
	/** 
	 * Constant for denoting the file related to a ECS index referring to contexts (instead of USUs) sorted by the value entry
	 */
	public static final byte INDEX_V_ECS_CONTEXT = 31;

	
	
	private static final String SEPARATOR = "___";
	private static final boolean OVERRIDE = false;
	
	/**
	 * Cache of files already available, i.e. which have already been created.  
	 */
	private HashMap<String,File> available = new HashMap<String,File>();
	
	public File get(byte type, File raw) {
		String key = this.genKey(type, raw);
		File result = null;
		if (available.containsKey(key)) {
			result = available.get(key);
		} else {
			File outFile = this.composeFile(type, raw);
			result = outFile;
			if ( (!outFile.exists()) || ResourceManager.OVERRIDE) {
				switch (type) {
				case ResourceManager.SORTED_SUBJECT:
					{
						NQuadSorting subjectSort = new NQuadSorting(NQuadSorting.SUBJECT_SORT);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting by Subject... ");
						subjectSort.run(raw, outFile);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.SORTED_PREDICATE:
					{
						NQuadSorting predicateSort = new NQuadSorting(NQuadSorting.PREDICATE_SORT);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting by Predicate... ");
						predicateSort.run(raw, outFile);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.SORTED_OBJECT:
					{
						NQuadSorting objectSort = new NQuadSorting(NQuadSorting.OBJECT_SORT);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting by Object... ");
						objectSort.run(raw, outFile);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.SORTED_CONTEXT:
					{
						NQuadSorting contextSort = new NQuadSorting(NQuadSorting.CONTEXT_SORT);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting by Context... ");
						contextSort.run(raw, outFile);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_TS:
					{

						File tsIndexFile = this.get(INDEX_V_TS, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting TS index by key entry... ");
						this.sortIndex(tsIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_TS:
					{
						TypeSetIndexing tsIndex = new TypeSetIndexing();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing TS... ");
						tsIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting index by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_PS:
					{
						File psIndexFile = this.get(INDEX_V_PS, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting PS index by key entry... ");
						this.sortIndex(psIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_PS:
					{
						PropertySetIndexing psIndex = new PropertySetIndexing();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing PS... ");
						psIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");

					}
					break;
				case ResourceManager.INDEX_K_ECS:
					{
						File ecsIndexFile =  this.get(INDEX_V_ECS, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting ECS index by key entry... ");
						this.sortIndex(ecsIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_ECS:
					{
						ExtendedCharacteristicSetIndexing ecsIndex = new ExtendedCharacteristicSetIndexing();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing ECS... ");
						ecsIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
	
					}
					break;
				case ResourceManager.INDEX_K_IPS:
					{
						File ipsIndexFile =  this.get(INDEX_V_IPS, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting IPS index by key entry... ");
						this.sortIndex(ipsIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_IPS:
					{
						IncomingPropertySetIndexing ipsIndex = new IncomingPropertySetIndexing ();
						File objectSortedFile = this.get(SORTED_OBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing IPS... ");
						ipsIndex.run(objectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
	
					}
					break;
				case ResourceManager.INDEX_K_SCHEMEX:
					{
						File schemexIndexFile =  this.get(INDEX_V_SCHEMEX, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting SchemEX index by key entry... ");
						this.sortIndex(schemexIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_SCHEMEX:
					{
						SchemExIndexing schemexIndex = new SchemExIndexing();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tsIndexFile = this.get(INDEX_K_TS, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing SchemEX... ");
						schemexIndex.run(subjectSortedFile, tsIndexFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
	
					}
					break;
				case ResourceManager.INDEX_K_SCHEMEX_JOIN:
					{
						File schemexJIndexFile =  this.get(INDEX_V_SCHEMEX_JOIN, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting SchemEX (Join) index by key entry... ");
						this.sortIndex(schemexJIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_SCHEMEX_JOIN:
					{
						SchemEXJoinIndexing schemexJIndex = new SchemEXJoinIndexing();
						File objectSortedFile = this.get(SORTED_OBJECT, raw);
						File tsIndexFile = this.get(INDEX_K_TS, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing SchemEX (Join) ... ");
						schemexJIndex.run(objectSortedFile, tsIndexFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
		
					}
					break;
				case ResourceManager.INDEX_K_S_HASH:
					{
						File sIndexFile =  this.get(INDEX_V_S_HASH, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Subjects index by key entry... ");
						this.sortIndex(sIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_S_HASH:
					{
						SubjectHashIndex sIndex = new SubjectHashIndex();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Subjects... ");
						sIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
	
					}
					break;
				case ResourceManager.INDEX_K_P_HASH:
					{
						File pIndexFile =  this.get(INDEX_V_P_HASH, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Predicate index by key entry... ");
						this.sortIndex(pIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_P_HASH:
					{
						PredicateHashIndex pIndex = new PredicateHashIndex();
						File predicateSortedFile = this.get(SORTED_PREDICATE, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Predicates... ");
						pIndex.run(predicateSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_O_HASH:
					{
						File oIndexFile =  this.get(INDEX_V_O_HASH, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Objects index by key entry... ");
						this.sortIndex(oIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_O_HASH:
					{
						ObjectHashIndex oIndex = new ObjectHashIndex();
						File objectSortedFile = this.get(SORTED_OBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Objects... ");
						oIndex.run(objectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_C_HASH:
					{
						File cIndexFile =  this.get(INDEX_V_C_HASH, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Contexts index by key entry... ");
						this.sortIndex(cIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_C_HASH:
					{
						ContextHashIndex cIndex = new ContextHashIndex();
						File contextSortedFile = this.get(SORTED_CONTEXT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Contexts... ");
						cIndex.run(contextSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_PLD_HASH:
					{
						File pldIndexFile =  this.get(INDEX_V_PLD_HASH, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting PLD index by key entry... ");
						this.sortIndex(pldIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_PLD_HASH:
					{
						PldHashIndex pldIndex = new PldHashIndex(new File("data/effective_tld_names.dat"));
						File contextSortedFile = this.get(SORTED_CONTEXT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing PLDs... ");
						pldIndex.run(contextSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_TYPE:
					{
						File typeIndexFile =  this.get(INDEX_V_TYPE, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Types index by key entry... ");
						this.sortIndex(typeIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_TYPE:
					{
						TypeIndex typeIndex = new TypeIndex();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Types... ");
						typeIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_KEYWORD:
					{
						File keywordIndexFile =  this.get(INDEX_V_KEYWORD, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting Keyword index by key entry... ");
						this.sortIndex(keywordIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_KEYWORD:
					{
						KeyWordIndex keywordIndex = new KeyWordIndex();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing Keywords... ");
						keywordIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_K_ECS_CONTEXT:
					{
						File ecsContextIndexFile =  this.get(INDEX_V_ECS_CONTEXT, raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Sorting ECS (context lookup) index by key entry... ");
						this.sortIndex(ecsContextIndexFile, outFile, IndexSorting.KEY_SORT);
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
					}
					break;
				case ResourceManager.INDEX_V_ECS_CONTEXT:
					{
						EcsContextLookupIndex ecsContextIndex = new EcsContextLookupIndex();
						File subjectSortedFile = this.get(SORTED_SUBJECT, raw);
						File tmpFile = this.composeTmpFile(raw);
						long t1 = System.currentTimeMillis();
						System.out.print("Indexing ECS (context lookup)... ");
						ecsContextIndex.run(subjectSortedFile, tmpFile);
						System.out.print("Sorting by value entry... ");
						this.sortIndex(tmpFile, outFile, IndexSorting.VALUE_SORT);
						System.out.print("Removing temporary files... ");
						tmpFile.delete();
						long t2 = System.currentTimeMillis();
						System.out.println(((t2-t1)/1000)+" sec");
	
					}
					break;
				}
			}
			available.put(key,result);
		}
		return result;
	}
	
	private String genKey(byte type, File raw) {
		String result = null;
		result = type+ResourceManager.SEPARATOR+raw.getName();
		return result;
	}
	
	private void sortIndex(File in, File out, byte mode) {
		IndexSorting indexSorter = new IndexSorting(mode);
		indexSorter.run(in, out);
	}
	
	public File composeTmpFile(File raw) {
		File result = null;
		String fname = raw.getName()+"-"+System.currentTimeMillis()+"-"+((int) (Math.random()*10000))+".tmp";
		result = new File(fname);
		return result;
	}
	
	public File composeFile(byte type, File raw) {
		File result = null;
		String fname = null;
		switch (type) {
		case ResourceManager.SORTED_SUBJECT:
			fname = raw.getName()+"--s-sort.nq";
			break;
		case ResourceManager.SORTED_PREDICATE:
			fname = raw.getName()+"--p-sort.nq";
			break;
		case ResourceManager.SORTED_OBJECT:
			fname = raw.getName()+"--o-sort.nq";
			break;
		case ResourceManager.SORTED_CONTEXT:
			fname = raw.getName()+"--c-sort.nq";
			break;
		case ResourceManager.INDEX_K_TS:
			fname = raw.getName()+"--ts-index.txt";
			break;
		case ResourceManager.INDEX_V_TS:
			fname = raw.getName()+"--ts-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_PS:
			fname = raw.getName()+"--ps-index.txt";
			break;
		case ResourceManager.INDEX_V_PS:
			fname = raw.getName()+"--ps-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_ECS:
			fname = raw.getName()+"--ecs-index.txt";
			break;
		case ResourceManager.INDEX_V_ECS:
			fname = raw.getName()+"--ecs-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_IPS:
			fname = raw.getName()+"--ips-index.txt";
			break;
		case ResourceManager.INDEX_V_IPS:
			fname = raw.getName()+"--ips-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_SCHEMEX:
			fname = raw.getName()+"--schemex-index.txt";
			break;
		case ResourceManager.INDEX_V_SCHEMEX:
			fname = raw.getName()+"--schemex-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_SCHEMEX_JOIN:
			fname = raw.getName()+"--schemex-join-index.txt";
			break;
		case ResourceManager.INDEX_V_SCHEMEX_JOIN:
			fname = raw.getName()+"--schemex-join-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_S_HASH:
			fname = raw.getName()+"--s-index.txt";
			break;
		case ResourceManager.INDEX_V_S_HASH:
			fname = raw.getName()+"--s-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_P_HASH:
			fname = raw.getName()+"--p-index.txt";
			break;
		case ResourceManager.INDEX_V_P_HASH:
			fname = raw.getName()+"--p-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_O_HASH:
			fname = raw.getName()+"--o-index.txt";
			break;
		case ResourceManager.INDEX_V_O_HASH:
			fname = raw.getName()+"--o-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_C_HASH:
			fname = raw.getName()+"--c-index.txt";
			break;
		case ResourceManager.INDEX_V_C_HASH:
			fname = raw.getName()+"--c-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_PLD_HASH:
			fname = raw.getName()+"--pld-index.txt";
			break;
		case ResourceManager.INDEX_V_PLD_HASH:
			fname = raw.getName()+"--pld-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_TYPE:
			fname = raw.getName()+"--type-index.txt";
			break;
		case ResourceManager.INDEX_V_TYPE:
			fname = raw.getName()+"--type-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_KEYWORD:
			fname = raw.getName()+"--keyword-index.txt";
			break;
		case ResourceManager.INDEX_V_KEYWORD:
			fname = raw.getName()+"--keyword-valsort-index.txt";
			break;
		case ResourceManager.INDEX_K_ECS_CONTEXT:
			fname = raw.getName()+"--ecs-context-index.txt";
			break;
		case ResourceManager.INDEX_V_ECS_CONTEXT:
			fname = raw.getName()+"--ecs-context-valsort-index.txt";
			break;
		}
		if (fname != null) {
			result = new File(fname);
		}
		return result;
	}
	
	
	public void cleanupFiles() {
		for (File f : this.available.values()) {
			f.delete();
		}
		this.available = new HashMap<String,File>();
	}
}
