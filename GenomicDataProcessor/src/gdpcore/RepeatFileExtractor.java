/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The RepeatFileExtractor class provides tools to extract data from a Repeat file
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFileExtractor extends GenomicFileExtractor {

	private ArrayList<RepeatFamily> repeatFamilyList; 		// List of RepeatFamily
	private ArrayList<RepeatFamily> fittedRepeatFamilyList; // list of RepeatFamily fitted to the screen resolution
	private double 					currentFactor = -1;		// Ratio between the screen width (in pixels) and the number of bases to show 
	private short					currentChromosome = -1;	// Chromosome currently display					
	
	/**
	 * Creates a RepeatFileExtractor
	 * @param chromoConfig {@link ChromosomeList}
	 * @param addressFile address of the file to extract
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public RepeatFileExtractor(ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
		super(chromoConfig);
		repeatFamilyList = new ArrayList<RepeatFamily>();
		extractFile(addressFile);
	}


	@Override
	protected void extractLine(String extractedLine) {
		if (extractedLine.charAt(0) != '#') {
			String[] splitedLine = null;

			splitedLine = extractedLine.split("\t");
			short chromosome = chromoConfig.getIndex(splitedLine[0]);

			if ((chromosome != -1) && (splitedLine.length == 4)) {
				int start = Integer.parseInt(splitedLine[1]);
				int stop = Integer.parseInt(splitedLine[2]);
				String family = splitedLine[3];

				if (repeatFamilyList.size() == 0) {
					repeatFamilyList.add(new RepeatFamily(family, chromoConfig));
					repeatFamilyList.get(0).add(chromosome, start, stop);
				} else {
					int i = 0;
					boolean familyFound = false;
					while ((i < repeatFamilyList.size()) && (!familyFound)) {
						familyFound = repeatFamilyList.get(i).getName().equals(family);
						i++;
					}
					if (familyFound) {
						repeatFamilyList.get(i - 1).add(chromosome, start, stop);
					} else { 
						repeatFamilyList.add(new RepeatFamily(family, chromoConfig));
						repeatFamilyList.get(repeatFamilyList.size() - 1).add(chromosome, start, stop);
					}
				}
			}
		}
	}
	
	
	/**
	 * Merges the repeats when the gap between two repeats is too small to be display on the screen.
	 * @param factor ratio between the screen width (in pixels) and the number of bases to show
	 * @param chromosome current chromosome
	 */
	private void fitListToScreen(double factor, short chromosome) {
		if (factor > 1) {
			fittedRepeatFamilyList = repeatFamilyList;
		} else {
			fittedRepeatFamilyList = new ArrayList<RepeatFamily>();
			for (RepeatFamily currentFamily : repeatFamilyList) {
				if (currentFamily.get(chromosome).size() > 1) {
					RepeatFamily fittedFamily = new RepeatFamily(currentFamily.getName(), chromoConfig);
					fittedFamily.add(chromosome, currentFamily.get(chromosome, 0).clone());
					int i = 1;
					int j = 0;
					while (i < currentFamily.get(chromosome).size()) {
						double distance = (currentFamily.get(chromosome, i).getStart() - fittedFamily.get(chromosome, j).getStop()) * factor;
						while ((distance < 1) && (i + 1 < currentFamily.get(chromosome).size())) {
							i++;
							distance = (currentFamily.get(chromosome, i).getStart() - fittedFamily.get(chromosome, j).getStop()) * factor;
						}
						fittedFamily.get(chromosome, j).setStop(currentFamily.get(chromosome, i - 1).getStop());
						fittedFamily.add(chromosome, currentFamily.get(chromosome, i).clone());
						i++;
						j++;						
					}
					fittedRepeatFamilyList.add(fittedFamily);
				}
			}
		}		
	}

	
	/**
	 * @param chromosome
	 * @param start
	 * @param stop
	 * @return all the repeat organized by family with a position 
	 * between <i>start</i> and <i>stop</i> on chromosome <i>chromosome</i>
	 */
	public ArrayList<RepeatFamily> get(short chromosome, int start, int stop, double factor) {
		ArrayList<RepeatFamily> resultList = new ArrayList<RepeatFamily>();
		
		if ((chromosome != currentChromosome) || (factor != currentFactor)) {
			currentChromosome = chromosome;
			currentFactor = factor;
			fitListToScreen(currentFactor, currentChromosome);
		}

		for (RepeatFamily currentFamily : fittedRepeatFamilyList) {
			int indexStart = findStart(currentFamily.get(chromosome), start, 0, currentFamily.get(chromosome).size());
			int indexStop = findStop(currentFamily.get(chromosome), stop, 0, currentFamily.get(chromosome).size());
			if ((indexStart > 0) && (currentFamily.get(chromosome).get(indexStart - 1).getStop() > start)) {
				indexStart--;
			}
			resultList.add(new RepeatFamily(currentFamily.getName(), chromoConfig));
			for (int i = indexStart; i <= indexStop; i++) {
				resultList.get(resultList.size() - 1).add(chromosome, currentFamily.get(chromosome, i));
			}			
		}
		return resultList;
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a Repeat with a position start equals to value. 
	 * Index of the first Repeat with a start position superior to value if nothing found.
	 */
	private int findStart(ArrayList<Repeat> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a Repeat with a position stop equals to value. 
	 * Index of the first Repeat with a stop position superior to value if nothing found.
	 */
	private int findStop(ArrayList<Repeat> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}
}
