/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The StripeFileExtractor class provides tools to load a stripe file.
 * A stripe file is showed as a succession of colored stipes. 
 * The color represent the score (or intensity) of the stripe. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class StripeFileExtractor extends GenomicFileExtractor {

	private ArrayList<ArrayList<Stripe>> 	stripeList;			// Contains one list of stripes per chromsome
	private ArrayList<Stripe> 				fittedStripeList; 	// StripeList adapted to the screen resolution
	private Double 							min;				// Min value of the list
	private Double 							max;				// Max value of the list
	private double 							currentFactor = -1;	// Ratio between the screen width (in pixels) and the number of bases to show 
	private short							currentChromosome = -1;	// Chromosome currently display		

	/**
	 * Public constructor.
	 * @param chromoConfig A ChromosomeList
	 * @param addressFile Address of the stripe file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public StripeFileExtractor(ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
		super(chromoConfig);
		stripeList = new ArrayList<ArrayList<Stripe>>();
		for (int i = 0; i < chromoConfig.size(); i++) {
			stripeList.add(new ArrayList<Stripe>());
		}
		extractFile(addressFile);
		setColors();
	}


	@Override
	protected void extractLine(String extractedLine) {
		if (extractedLine.charAt(0) != '#') {
			String[] splitedLine = null;

			splitedLine = extractedLine.split("\t");

			int chromosomeNumber = chromoConfig.getIndex(splitedLine[0]);

			if (chromosomeNumber != -1) {
				int start = Integer.parseInt(splitedLine[1]);
				int stop = Integer.parseInt(splitedLine[2]);
				double score = 0;
				if (splitedLine.length > 3) {
					score = Double.parseDouble(splitedLine[3]);
				}	
				stripeList.get(chromosomeNumber).add(new Stripe(start, stop, score));
				if (lineCount == 0) {
					min = score;
					max = score;
				} else if (score > max) {
					max = score; 
				} else if (score < min) {
					min = score;
				}
				lineCount++;
			}
		}		
	}
	
	
	/**
	 * Sets the color of every stripe depending on their on score,
	 * and on the minimum and maximum value of the list.
	 */
	private void setColors() {
		for (ArrayList<Stripe> list : stripeList) {
			for (Stripe stripe : list) {
				stripe.setColor(min, max);
			}
		}		
	}


	/**
	 * Merges the stripes when the gap between two stripes is too small  to be display on the screen.
	 * @param factor ratio between the number of pixels displayable and the number of bases to show
	 * @param chromosome current chromosome
	 */
	private void fitListToScreen(double factor, short chromosome) {
		if (factor > 1) {
			fittedStripeList = stripeList.get(chromosome);
		} else {
			fittedStripeList = new ArrayList<Stripe>();
			if (stripeList.get(chromosome).size() > 1) {
				fittedStripeList.add(stripeList.get(chromosome).get(0).clone());
				int i = 1;
				int j = 0;
				while (i < stripeList.get(chromosome).size()) {
					double distance = (stripeList.get(chromosome).get(i).getStart() - fittedStripeList.get(j).getStop()) * factor;
					while ((distance < 1) && (i + 1 < stripeList.get(chromosome).size())) {
						i++;
						distance = (stripeList.get(chromosome).get(i).getStart() - fittedStripeList.get(j).getStop()) * factor;
					}
					fittedStripeList.get(j).setStop(stripeList.get(chromosome).get(i - 1).getStop());
					fittedStripeList.add(stripeList.get(chromosome).get(i).clone());
					i++;
					j++;						
				}
			}
		}
	}	


	/**
	 * @param chromo
	 * @param posStart
	 * @param posStop
	 * @return The stripes on the chromosome <i>chromo</i> that are between posStart and posStop. 
	 */
	public ArrayList<Stripe> get(short chromo, int posStart, int posStop, double factor) {
		ArrayList<Stripe> resultList = new ArrayList<Stripe>();
		if ((stripeList.get(chromo) == null) || (stripeList.get(chromo).size() == 0)) {
			return null;
		}
		
		if ((chromo != currentChromosome) || (factor != currentFactor)) {
			currentChromosome = chromo;
			currentFactor = factor;
			fitListToScreen(currentFactor, currentChromosome);
		}
		
		int indexStart = findStart(fittedStripeList, posStart, 0, fittedStripeList.size() - 1);
		int indexStop = findStop(fittedStripeList, posStop, 0, fittedStripeList.size() - 1);
		if (indexStart > 0) {
			if (fittedStripeList.get(indexStart - 1).getStop() > posStart) {
				Stripe currentStripe = fittedStripeList.get(indexStart - 1); 
				Stripe newFirstStripe = new Stripe(posStart, currentStripe.getStop(), currentStripe.getScore(), currentStripe.getColor());
				resultList.add(newFirstStripe);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedStripeList.get(i));
		}
		if (indexStop + 1 < fittedStripeList.size()) {
			if (fittedStripeList.get(indexStop + 1).getStart() < posStop) {
				Stripe currentStripe = fittedStripeList.get(indexStop + 1); 
				Stripe newLastStripe = new Stripe(currentStripe.getStart(), posStop, currentStripe.getScore(), currentStripe.getColor());
				resultList.add(newLastStripe);
			}
		}
		return resultList;
	}


	/**
	 * Recursive function. Returns the index where the start value of the stripe is found
	 * or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return
	 */
	private int findStart(ArrayList<Stripe> list, int value, int indexStart, int indexStop) {
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
	 * Recursive function. Returns the index where the stop value of the stripe is found
	 * or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return
	 */
	private int findStop(ArrayList<Stripe> list, int value, int indexStart, int indexStop) {
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
