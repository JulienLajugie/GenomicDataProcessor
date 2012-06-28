/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.util.ArrayList;


/**
 * @author Julien Lajugie
 * @version 0.1
 * The GenomicWindowFileExtractor abstract class represents any genomic files that contains 
 * start and stop positions associated with a score. 
 */
public abstract class GenomicWindowFileExtractor extends GenomicFileExtractor {
	protected ArrayList<ArrayList<Integer>> startList; 		// extracted start position organized per chromosome
	protected ArrayList<ArrayList<Integer>> stopList;		// extracted stop positions organized per chromosome
	protected ArrayList<ArrayList<Double>> intensityList;	// extracted intensities organized per chromosome
	
	
	/**
	 * Sets the chromosome configuration and initializes the data lists.
	 * @param chromoConfig A ChromosomeConfiguration.
	 */
	public GenomicWindowFileExtractor(ChromosomeList chromoConfig) {
		super(chromoConfig);
		startList = new ArrayList<ArrayList<Integer>>();
		stopList = new ArrayList<ArrayList<Integer>>();
		intensityList = new ArrayList<ArrayList<Double>>();
		for(short i = 0; i < chromoConfig.size(); i++) {
			startList.add(new ArrayList<Integer>());
			stopList.add(new ArrayList<Integer>());
			intensityList.add(new ArrayList<Double>());
		}
	}

	/**
	 * @see gdpcore.GenomicFileExtractor#extractLine(java.lang.String)
	 */
	@Override
	abstract protected void extractLine(String extractedLine);

}
