/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The GenomicFileExtractor abstract class defines the common methods and 
 * parameters for the different kind of genomic file extractors.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GenomicFileExtractor {
	protected ChromosomeList chromoConfig; // maximum number of chromosomes
	protected int lineCount; // number of lines extracted from the input file
	
	/**
	 * Sets the chromosome configuration.
	 * @param chromoConfig A ChromosomeConfiguration.
	 */
	public GenomicFileExtractor(ChromosomeList chromoConfig) {
		this.chromoConfig = chromoConfig;
	}
	
	
	/**
	 * @return Number of chromosomes. 
	 */
	public short getMaxChromo() {
		return chromoConfig.size();
	}
	
	
	/**
	 * Opens and extracts the data from a file.
	 * @param addressFile Address of the file to extract.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void extractFile(String addressFile) throws FileNotFoundException, IOException {
		// initialize the number of read
		lineCount = 0;
		// try to open the input file
		 BufferedReader reader = new BufferedReader(new FileReader(new File(addressFile)));
		// extract data
		String line = null;
		while((line = reader.readLine()) != null) {
			extractLine(line);
		}
		reader.close();
	}
	

	/**
	 * Extracts the data from one line of a genomic file.
	 * @param extractedLine of the file being extracted.  
	 */
	protected abstract void extractLine(String extractedLine);
}
