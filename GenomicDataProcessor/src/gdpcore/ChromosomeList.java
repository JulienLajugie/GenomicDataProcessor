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
import java.util.ArrayList;


/**
 * The ChromosomeList class provides tools to configure the chromosomes.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeList {

	private ArrayList<Chromosome> chromosomeArray;	// List of chromosomes

	/**
	 * Constructor. Creates a list of chromosomes from a configuration file.
	 * @param configFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ChromosomeList (String configFilePath) throws FileNotFoundException, IOException {
		// try to open the input file
		BufferedReader reader = new BufferedReader(new FileReader(new File(configFilePath)));
		// extract data
		chromosomeArray = new ArrayList<Chromosome>();
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] splitedLine = line.split("\t");
			String name = splitedLine[0].trim();
			int length = Integer.parseInt(splitedLine[1].trim());
			chromosomeArray.add(new Chromosome(name, length));			
		}
		reader.close();
	}


	/**
	 * @param chromosomeName String representing a chromosome.
	 * @return The index of this chromosome. -1 if not found.
	 */
	public short getIndex(String chromosomeName) {
		if ((chromosomeName != null) && (chromosomeName.length() > 0) && (chromosomeArray != null)) {
			for (short i = 0; i < chromosomeArray.size(); i++) {
				if (chromosomeArray.get(i).getName().equalsIgnoreCase(chromosomeName.trim())) {
					return i;
				}
			}
		}
		// If nothing has been found
		return -1;
	}


	/**
	 * @param index Index of a chromomsome.
	 * @return The Name of a chromosome. null if not found.
	 */
	public Chromosome get(short index) {
		if ((chromosomeArray != null) && (index >= 0) && (index < chromosomeArray.size())) {
			return chromosomeArray.get(index);
		}
		// If nothing has been found
		return null;
	}


	/**
	 * @return The value of the greatest chromosome index.
	 */
	public short size() {
		return (short)chromosomeArray.size();
	}
	
	
	/**
	 * @return The list with all the chromosomes.
	 */
	public Chromosome[] getAllChromosomes() {
		Chromosome[] res = new Chromosome[chromosomeArray.size()];
		for (int i = 0; i < chromosomeArray.size(); i++) {
			res[i] = chromosomeArray.get(i);  
		}
		return res;
	}
}
