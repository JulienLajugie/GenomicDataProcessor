/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The GeneFileExtractor class provides tool to extract data containing information about genes.
 * The file must respect the following format:
 * name\tchrom\tstrand\ttxStart\ttxEnd\texonStarts\texonEnds\n
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneFileExtractor extends GenomicFileExtractor {

	private ArrayList<ArrayList<Gene>> geneList;	// List of gene per chromosome

	/**
	 * Extract a GeneFile.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @param addressFile Path to a file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public GeneFileExtractor(ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
		super(chromoConfig);
		geneList = new ArrayList<ArrayList<Gene>>();
		for (int i = 0; i < chromoConfig.size(); i++) {
			geneList.add(new ArrayList<Gene>());
		}
		extractFile(addressFile);
	}


	/**
	 * Receives one line from the input file and tries
	 * to create a Gene object from this data.
	 * The gene is added to the list of genes.
	 * @param extractedLine Line read from the file being extracted. 
	 */
	@Override
	protected void extractLine(String extractedLine) {
		if (extractedLine.charAt(0) != '#') {
			String[] splitedLine = null;

			splitedLine = extractedLine.split("\t");
			int chromosomeNumber = chromoConfig.getIndex(splitedLine[1]);

			if (chromosomeNumber != -1) {
				String[] splitedExonStartsStr = null;
				String[] splitedExonStopsStr = null;
				int[] splitedExonStarts = null;
				int[] splitedExonStops = null;

				splitedExonStartsStr = splitedLine[5].split(",");
				splitedExonStarts = new int[splitedExonStartsStr.length];
				for (int i = 0; i < splitedExonStarts.length; i++) {
					splitedExonStarts[i] = Integer.parseInt(splitedExonStartsStr[i]);
				}
				splitedExonStopsStr= splitedLine[6].split(",");
				splitedExonStops = new int[splitedExonStopsStr.length];
				for (int i = 0; i < splitedExonStops.length; i++) {
					splitedExonStops[i] = Integer.parseInt(splitedExonStopsStr[i]);
				}
				if (splitedLine.length == 7) {
				geneList.get(chromosomeNumber).add(
						new Gene(splitedLine[0], (short)chromosomeNumber, splitedLine[2], Integer.parseInt(splitedLine[3]),  
								Integer.parseInt(splitedLine[4]), splitedExonStarts, splitedExonStops, null));
				} else if (splitedLine.length == 8) {
					String[] splitedExonScoresStr= splitedLine[7].split(",");
					double[] splitedExonScores = new double[splitedExonStopsStr.length];
					for (int i = 0; i < splitedExonStops.length; i++) {
						splitedExonScores[i] = Double.parseDouble(splitedExonScoresStr[i]);
					}
					geneList.get(chromosomeNumber).add(
							new Gene(splitedLine[0], (short)chromosomeNumber, splitedLine[2], Integer.parseInt(splitedLine[3]),  
									Integer.parseInt(splitedLine[4]), splitedExonStarts, splitedExonStops, splitedExonScores));
				}
				lineCount++;
			}
		}
	}


	/**
	 * @param chromosome A chromosome
	 * @return The list of genes for one chromosome.
	 */
	public ArrayList<Gene> get(short chromosome) {
		if ((geneList != null) && (chromosome < geneList.size())) {
			return geneList.get(chromosome);
		} else {
			return null;
		}
	}

	
	/**
	 * Returns the first gene of the gene list called <i>name</i>.
	 * Returns null if there is no gene with this name.
	 * @param name Name of the gene.
	 * @return A gene called <i>name</i>. Return null if not found.
	 */
	public Gene search(String name) {
		if (geneList == null) {
			return null;
		} else {
			boolean found = false;
			Gene geneFound = null;
			int i = 0;
			while ((i < chromoConfig.size()) && (!found)) {
				if (geneList.get(i) != null) {
					int j = 0;
					while ((j < geneList.get(i).size()) && (!found)) {
						if (geneList.get(i).get(j).equals(name)) {
							geneFound = geneList.get(i).get(j);
							found = true;
						}
						j++;
					}					
				}
				i++;
			}
			return geneFound;
		}
	}

}
