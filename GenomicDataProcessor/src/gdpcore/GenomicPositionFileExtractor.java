/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.util.ArrayList;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The GenomicPositionFileExtractor abstract class represents any genomic files that contains 
 * only positions. For example the Solid and the Solexa files. 
 */
public abstract class GenomicPositionFileExtractor extends GenomicFileExtractor {
	protected ArrayList<ArrayList<Integer>> positionList; // array extracted positions for each chromosome
	
	/**
	 * Sets the chromosome configuration and initializes the lists of positions.
	 * @param chromoConfig A ChromosomeConfiguration.
	 */
	public GenomicPositionFileExtractor(ChromosomeList chromoConfig) {
		super(chromoConfig);
		positionList = new ArrayList<ArrayList<Integer>>();
		for(short i = 0; i < chromoConfig.size(); i++)
			positionList.add(new ArrayList<Integer>());
	}
	
	/**
	 * @see gdpcore.GenomicFileExtractor#extractLine(java.lang.String)
	 */
	@Override
	abstract protected void extractLine(String extractedLine);

}
