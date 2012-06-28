/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.util.ArrayList;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The GenomicPositionIntensityFileExtractor abstract class represents any genomic files that contains 
 * positions and intensity. For example the BedGraph files. 
 */
public abstract class GenomicPositionIntensityFileExtractor extends GenomicPositionFileExtractor{
	protected ArrayList<ArrayList<Double>> intensityList; // array extracted intensities for each chromosome
	
	/**
	 * Sets the chromosome configuration and initializes the lists of positions and of intensities. 
	 * @param chromoConfig A ChromosomeConfiguration.
	 */
	public GenomicPositionIntensityFileExtractor(ChromosomeList chromoConfig) {
		super(chromoConfig);
		intensityList = new ArrayList<ArrayList<Double>>();
		for(short i = 0; i < chromoConfig.size(); i++)
			intensityList.add(new ArrayList<Double>());
	}

	/**
	 * @see gdpcore.GenomicFileExtractor#extractLine(java.lang.String)
	 */
	abstract protected void extractLine(String extractedLine);
}
