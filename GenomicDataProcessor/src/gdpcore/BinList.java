/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The BinList class provides a representation of a list of genomic positions grouped by bins for each chromosome of a genome.
 * An intensity is associated to each bin.
 * This class offers some tools allowing to gauss the values or to print a bedgraph file for example.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinList {

	/**
	 * The enumeration intensityCalculation is used to specify how to calculate the intensity.
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	public enum IntensityCalculation {
		sum,
		maximum,
		average;
	};


	private ChromosomeList chromoConfig; // chromosome configuration
	private short maxChromo; // greatest index of chromosome
	private double[][] binList; // one list of bins for each chromosome
	private int windowSize; // size of the window
	private double intensityCount; // number of matches


	/**
	 * @param aChromoConfig A ChromosomeConfiguration.
	 * @param positionList List of positions.
	 * @param aWindowSize Size of the bins in base pair.
	 */
	public BinList(ChromosomeList aChromoConfig, int aWindowSize, ArrayList<ArrayList<Integer>> positionList)  {
		chromoConfig = aChromoConfig;
		maxChromo = (short) (chromoConfig.size());
		windowSize = aWindowSize;
		intensityCount = 0;
		binList = new double[maxChromo][];
		createMatchList(positionList);
	}


	/**
	 * @param aChromoConfig A ChromosomeConfiguration.
	 * @param aWindowSize Size of the bins in base pair.
	 * @param anIntensityCount Sum of the intensities.
	 */
	public BinList(ChromosomeList aChromoConfig, int aWindowSize, double anIntensityCount)  {
		chromoConfig = aChromoConfig;
		maxChromo = (short) (chromoConfig.size());
		windowSize = aWindowSize;
		intensityCount = anIntensityCount;
		binList = new double[maxChromo][];
	}


	/**
	 * @param aChromoConfig A ChromosomeConfiguration.
	 * @param aWindowSize Size of the bins in base pair.
	 * @param positionList List of positions.
	 * @param intensityList List of intensities.
	 */
	public BinList(ChromosomeList aChromoConfig, int aWindowSize, ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList)  {
		chromoConfig = aChromoConfig;
		maxChromo = (short) (chromoConfig.size());
		windowSize = aWindowSize;
		intensityCount = 0;
		binList = new double[maxChromo][];
		createIntensityList(positionList, intensityList);
	}


	/**
	 * @param aChromoConfig A ChromosomeConfiguration.
	 * @param positionList List of positions.
	 * @param aWindowSize Size of the bins in base pair.
	 * @param intensityList List of intensities.
	 * @param criterion Specifies how the intensity is calculated.
	 */
	public BinList(ChromosomeList aChromoConfig, int aWindowSize, ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList, IntensityCalculation criterion)  {
		chromoConfig = aChromoConfig;
		maxChromo = (short) (chromoConfig.size());
		windowSize = aWindowSize;
		intensityCount = 0;
		binList = new double[maxChromo][];
		switch (criterion) {
		case average: 
			createIntensityListAvg(positionList, intensityList);
			break;
		case maximum:
			createIntensityListMax(positionList, intensityList);
			break;
		case sum:
			createIntensityListSum(positionList, intensityList);
			break;
		}		
	}


	/**
	 * @param aChromoConfig A ChromosomeConfiguration.
	 * @param startList List of positions.
	 * @param aWindowSize Size of the bins in base pair.
	 * @param intensityList List of intensities.
	 * @param criterion Specifies how the intensity is calculated.
	 */
	public BinList(ChromosomeList aChromoConfig, int aWindowSize, ArrayList<ArrayList<Integer>> startList, ArrayList<ArrayList<Integer>> stopList, ArrayList<ArrayList<Double>> intensityList, IntensityCalculation criterion) {
		chromoConfig = aChromoConfig;
		maxChromo = (short) (chromoConfig.size());
		windowSize = aWindowSize;
		intensityCount = 0;
		binList = new double[maxChromo][];
		createIntensityList(startList, stopList, intensityList, criterion);
	}


	/**
	 * Returns the biggest element of an ArrayList of Integers.
	 * @param list ArrayList of Integers. 
	 * @return The biggest element of a list of Integers.
	 */
	private int max(ArrayList<Integer> list) {
		int currentMax = 0;

		if(list == null)
			return 0;

		for (int i = 0; i < list.size(); i++) {
			if (currentMax < list.get(i).intValue())
				currentMax =  list.get(i).intValue();
		}
		return currentMax;
	}


	/**
	 * Returns a list containing how many matches there is for each bin.
	 * Goes through the list of positions and increment the intensity of 
	 * a bin every time a new matching sequence in this bin is found.
	 * @param positionList List of positions.
	 */
	private void createMatchList(ArrayList<ArrayList<Integer>> positionList) {
		for(int i = 0; i < maxChromo; i++)  {
			int tabLength = max(positionList.get(i)) / windowSize + 1;
			if(tabLength > 1) {
				binList[i] = new double[tabLength];
				for(int j = 0; j < positionList.get(i).size(); j++) {
					int windowTmp = positionList.get(i).get(j) / windowSize;
					binList[i][windowTmp]+=1 ;
					intensityCount+=1;
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities
	 * and load the BinList from this data.
	 * @param positionList List of positions.
	 * @param intensityList List of intensities.
	 */
	private void createIntensityList(ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList) {
		for(int i = 0; i < maxChromo; i++)  {
			int tabLength = max(positionList.get(i)) / windowSize + 1;
			if(tabLength > 1) {
				binList[i] = new double[tabLength];
				for(int j = 0; j < positionList.get(i).size(); j++) {
					int windowTmp = positionList.get(i).get(j) / windowSize;
					binList[i][windowTmp] = intensityList.get(i).get(j);
					intensityCount+=intensityList.get(i).get(j);
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities
	 * and load the BinList from this data. 
	 * If more than one position is found for one bin, the intensity of the bin
	 * is the maximum of the intensities of the positions. 
	 * @param positionList List of positions.
	 * @param intensityList List of intensities.
	 */
	private void createIntensityListMax(ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList) {
		for(int i = 0; i < maxChromo; i++)  {
			int tabLength = max(positionList.get(i)) / windowSize + 1;
			if(tabLength > 1) {
				binList[i] = new double[tabLength];
				for(int j = 0; j < positionList.get(i).size(); j++) {
					int windowTmp = positionList.get(i).get(j) / windowSize;
					if(intensityList.get(i).get(j) > binList[i][windowTmp]) {
						binList[i][windowTmp] = intensityList.get(i).get(j);
						intensityCount+=intensityList.get(i).get(j);
					}
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities
	 * and load the BinList from this data.
	 * If more than one position is found for one bin, the intensity of the bin
	 * is the sum of the intensities of the positions
	 * @param positionList List of positions.
	 * @param intensityList List of intensities.
	 */
	private void createIntensityListSum(ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList) {
		for(int i = 0; i < maxChromo; i++)  {
			int tabLength = max(positionList.get(i)) / windowSize + 1;
			if(tabLength > 1) {
				binList[i] = new double[tabLength];
				for(int j = 0; j < positionList.get(i).size(); j++) {
					int windowTmp = positionList.get(i).get(j) / windowSize;
					binList[i][windowTmp]+=intensityList.get(i).get(j);
					intensityCount+=intensityList.get(i).get(j);
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities
	 * and load the BinList from this data.
	 * @param positionList List of positions.
	 * @param intensityList List of intensities.
	 */
	private void createIntensityListAvg(ArrayList<ArrayList<Integer>> positionList, ArrayList<ArrayList<Double>> intensityList) {
		int[][] countList = new int[maxChromo][];
		for(int i = 0; i < maxChromo; i++)  {
			int tabLength = max(positionList.get(i)) / windowSize + 1;
			if(tabLength > 1) {
				binList[i] = new double[tabLength];
				countList[i] = new int[tabLength];
				for(int j = 0; j < positionList.get(i).size(); j++) {
					int windowTmp = positionList.get(i).get(j) / windowSize;
					binList[i][windowTmp] = intensityList.get(i).get(j);
					countList[i][windowTmp]++;
				}
			}
		}
		for(int i = 0; i < maxChromo; i++)  {
			if(binList[i] != null) {
				for(int j = 0; j < binList[i].length; j++) {
					if(countList[i][j] == 0)
						binList[i][j] = 0;
					else {
						binList[i][j]/=countList[i][j];
						intensityCount+=binList[i][j];
					}
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of start positions, a list of stop 
	 * positions and a list of intensities and load the BinList from this data. 
	 * @param startList list of start positions
	 * @param stopList list of stop positions
	 * @param intensityList list of intensities
	 * @param criterion criterion used to create the intensity list 
	 */
	private void createIntensityList(ArrayList<ArrayList<Integer>> startList, ArrayList<ArrayList<Integer>> stopList, ArrayList<ArrayList<Double>> intensityList, IntensityCalculation criterion) {
		for(int i = 0; i < maxChromo; i++)  {
			if (startList.get(i) != null) {
				int tabLength = max(stopList.get(i)) / windowSize + 1;
				if(tabLength > 1) {
					binList[i] = new double[tabLength];
					int k = 0;
					int previousStop = 0;
					for (int j = 0; j < binList[i].length; j++) {
						k = previousStop;
						ArrayList<Double> currentBinIntensities = new ArrayList<Double>();
						while ((k < stopList.get(i).size()) && (stopList.get(i).get(k) < j * windowSize)) {
							k++;
						}
						while ((k < startList.get(i).size()) && (startList.get(i).get(k) < j * windowSize)) {
							if (stopList.get(i).get(k) > j * windowSize) {
								double intensity = intensityList.get(i).get(k) * (stopList.get(i).get(k) - (j * windowSize)) / (stopList.get(i).get(k) - startList.get(i).get(k));
								currentBinIntensities.add(intensity);							
							}
							k++;
						} 
						while ((k < startList.get(i).size()) && (startList.get(i).get(k) < (j + 1) * windowSize)) {
							if (stopList.get(i).get(k) > (j + 1) * windowSize) {
								double intensity = intensityList.get(i).get(k) * (((j + 1) * windowSize) - startList.get(i).get(k)) / (stopList.get(i).get(k) - startList.get(i).get(k));
								currentBinIntensities.add(intensity);								
							} else {
								double intensity = intensityList.get(i).get(k);
								currentBinIntensities.add(intensity);
								previousStop = k;
							}
							k++;
						}
						switch (criterion) {
						case average: 
							binList[i][j] = getAverage(currentBinIntensities);
							break;
						case maximum:
							binList[i][j] = getMax(currentBinIntensities);
							break;
						case sum:
							binList[i][j] = getSum(currentBinIntensities);
							break;
						}
						intensityCount += binList[i][j];
					}
					startList.get(i).clear();
					stopList.get(i).clear();
					intensityList.get(i).clear();					
					System.gc();
				}
			}
		}
	}


	/**
	 * @param list {@link ArrayList} of Double
	 * @return the average of the list
	 */
	private double getAverage(ArrayList<Double> list) {
		double result = 0;
		int n = 0; 
		for (Double currentValue : list) {
			if (currentValue != 0) {
				result += currentValue;
				n++;
			}
		}
		return result / n;
	}


	/**
	 * @param list {@link ArrayList} of Double 
	 * @return the greatest value of the list 
	 */
	private double getMax(ArrayList<Double> list) {
		double max = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			max = Math.max(max, list.get(i));
		}
		return max;
	}


	/**
	 * @param list {@link ArrayList} of Double
	 * @return the sum of the values of the list
	 */
	private double getSum(ArrayList<Double> list) {
		double result = 0;
		for (Double currentValue : list) {
			result += currentValue;
		}
		return result;
	}


	/**
	 * Returns the biggest Double of an ArrayList of Double. 
	 * @param slopeArray ArrayList of double.
	 * @return The biggest element of the ArrayList.
	 */
	private double averageArrayListOfDouble(ArrayList<Double> slopeArray) {
		double sum = 0;
		int count = 0;

		if(slopeArray == null)
			return 0;

		for(int i = 0; i < slopeArray.size(); i++) {
			sum+=slopeArray.get(i);
			count++;
		}
		if(count > 0)
			return sum / count;
		else 
			return 0;
	}


	/**
	 * @return The data array associate to the BinList.
	 */
	public double[][] getData() {
		return binList;
	}

	/**
	 * @param chromo A chromosome.
	 * @return The data array of the chromosome <i>chromo</i> of the BinList.
	 */
	public double[] getData(short chromo) {
		if ((binList == null) || (chromo > binList.length) || (binList[chromo] == null)) {
			return null;
		}
		return binList[chromo];
	}	


	/**
	 * @param chromo A chromosome.
	 * @param coeff 
	 * @return The data array of the chromosome <i>chromo</i> of the BinList and where the 
	 * windowSize has been multiplied by coeff. The data are averaged.
	 */
	public double[] getData(short chromo, int coeff) {
		if ((binList == null) || (chromo > binList.length) || (binList[chromo] == null)) {
			return null;
		}

		if (coeff == 1) {
			return getData(chromo);
		}

		double[] returnArray = new double[binList[chromo].length / coeff + 1];
		int newIndex = 0;
		for(int i = 0; i < binList[chromo].length; i+=coeff) {
			double sum = 0;
			int n = 0;
			for(int j = 0; j < coeff; j ++) {
				if ((i + j < binList[chromo].length) && (binList[chromo][i + j] != 0)){
					sum += binList[chromo][i + j];
					n++;					
				}				
			}
			if (n > 0) {
				returnArray[newIndex] = sum / n;
			}
			else {
				returnArray[newIndex] = 0;
			}
			newIndex++;
		}		
		return returnArray;
	}


	/**
	 * @return The size of the bins in bp.
	 */
	public int getWindowSize() {
		return windowSize;
	}


	/**
	 * Clones the current BinList.
	 * @return A new BinList.
	 */
	public BinList clone() {
		BinList binList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		binList.binList = this.binList.clone();
		return binList;
	}


	/**
	 * Subtracts the current list by the list in parameter.
	 * @param aBinList BinList we are subtracting.
	 * @return New BinList resulting from the subtraction.
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	public BinList minus(BinList aBinList) throws BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException {
		if(aBinList == null)
			throw new BinListNullException();
		if((this.binList == null) || (aBinList.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != aBinList.windowSize)
			throw new BinListDifferentWindowSizeException();

		BinList listResult = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		for(short i = 0; i < this.maxChromo; i++)  {
			if((this.binList[i] != null) && (aBinList.binList[i] != null)) {
				listResult.binList[i] = new double[this.binList[i].length];
				for(int j = 0; j < this.binList[i].length; j++) {
					if(j < aBinList.binList[i].length)
						listResult.binList[i][j] = this.binList[i][j] - aBinList.binList[i][j];
					else
						listResult.binList[i][j] = 0;
				}
			}
		}
		return listResult;
	}


	/**
	 * Divides the current list by another BinList
	 * for every bin where the value of at least one   
	 * of the two list is higher than the value of filter.
	 * The result is normalized by the length of each list 
	 * if the parameter normalized is set to true.
	 * @param controlList BinList used as control.
	 * @param filter Value used as a filter.
	 * @param normalized Set this parameter to true in order to normalize the result.
	 * @return New BinList resulting from the division.
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	public BinList divideBy(BinList controlList, int filter, boolean normalized) throws Exception, BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException {
		if(controlList == null) {
			throw new BinListNullException();
		} else if ((this.binList == null) || (controlList.binList == null)) {
			throw new BinListNoDataException();
		} else if(this.windowSize != controlList.windowSize) {
			throw new BinListDifferentWindowSizeException();
		} else if(filter % 2 !=0) { // The filter has to be an even number
			throw new Exception("Error: unable to divide, the filter must be an even number.");
		} else if(filter < 0) { // The filter has to be a positive value
			throw new Exception("Error: unable to divide, the filter must be positive.");
		}	

		// 'halfFilter' is used for filtering the chromosomes X and Y 
		int halfFilter = filter / 2;
		// The variable 'coeff' is used for the normalization of the division
		double coeff;
		if(normalized)
			coeff = controlList.intensityCount / this.intensityCount;
		else 
			coeff = 1;
		// Build a new list a compute the normalized division for every values of the control list above the filter 
		BinList listResult = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		for(short i = 0; i < this.maxChromo; i++)  {
			if((this.binList[i] != null) && (controlList.binList[i] != null)) {
				listResult.binList[i] = new double[this.binList[i].length];
				for(int j = 0; j < this.binList[i].length; j++) {
					if((j < controlList.binList[i].length) && (controlList.binList[i][j] > 0)) {
						if ((controlList.binList[i][j] >= filter) || (binList[i][j] >= filter) 
								|| (((i == chromoConfig.getIndex("chrX")) || (i == chromoConfig.getIndex("chrY"))) 
										&& ((controlList.binList[i][j] >= halfFilter) || (binList[i][j] >= halfFilter)))) { 
							listResult.binList[i][j] = coeff * this.binList[i][j] / controlList.binList[i][j];
						} else {
							listResult.binList[i][j] = 0;
						}
					}
				}
			}
		}
		return listResult;
	}


	/**
	 * Prints the BinList on the standard output.
	 * @throws BinListNoDataException
	 */
	public void print() throws BinListNoDataException {
		if (binList == null)
			throw new BinListNoDataException();

		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++)
					System.out.println(chromoConfig.get(i).getName() + "\t" + (j * windowSize) + "\t" + ((j + 1) * windowSize) + "\t" + binList[i][j]);
			}
		}
	}


	/**
	 * Prints the BinList of matches in a file with the Wiggle format.
	 * @param filePath Address of the file.
	 * @param wiggleName Name of the bedGraph
	 * @throws IOException
	 * @throws BinListNoDataException
	 */
	public void printWiggleFile(String filePath, String wiggleName) throws IOException, BinListNoDataException {
		BufferedWriter writer;

		if (binList == null)
			throw new BinListNoDataException();

		// try to create a output file
		writer = new BufferedWriter(new FileWriter(new File(filePath)));
		// print the title of the graph
		writer.write("track type=bedGraph name=" + wiggleName);
		writer.newLine();
		// print the data
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					writer.write(chromoConfig.get(i).getName() + "\t" + (j * windowSize) + "\t" + ((j + 1) * windowSize) + "\t" + binList[i][j]);
					writer.newLine();
				}
			}
		}
		writer.close();
	}


	/**
	 * Prints the BinList of matches in a file with the CSV format.
	 * @param filePath Address of the file.
	 * @throws IOException
	 * @throws BinListNoDataException
	 */
	public void printCSVFile(String filePath) throws IOException, BinListNoDataException {
		BufferedWriter writer;

		if (binList == null)
			throw new BinListNoDataException();

		// try to create a output file
		writer = new BufferedWriter(new FileWriter(new File(filePath)));
		// print the data
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					writer.write(chromoConfig.get(i).getName() + ", " + (j * windowSize) + ", " + ((j + 1) * windowSize) + ", " + binList[i][j]);
					writer.newLine();
				}
			}
		}
		writer.close();
	}


	/**
	 * Divides each bin by the total of reads in the file.
	 * The result is multiply by the parameter 'factor' in order to have values easier to read.
	 * The result is returned in a new BinList
	 * @param factor
	 * @return New BinList resulting from the normalization.
	 * @throws BinListNoDataException
	 */
	public BinList normalize(int factor) throws BinListNoDataException {
		double[][] resultTab;	

		if (binList == null)
			throw new BinListNoDataException();

		// We create a new BinList
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);		
		resultTab = new double[maxChromo][];
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null)
				resultTab[i] = new double[binList[i].length];
		}
		// We normalize
		double normalizerFactor = (double)factor / intensityCount;
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					resultTab[i][j] = binList[i][j] * normalizerFactor;
				}
			}
		}
		resultList.binList = resultTab;
		return resultList;
	}	


	/**
	 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
	 * Sigma is used to configure the gaussian filter.
	 * @param sigma Configure the gaussian filter.
	 * @return New BinList resulting from the gaussian filter.
	 * @throws BinListNoDataException
	 */
	public BinList gauss(int sigma) throws BinListNoDataException {
		int distance;
		int halfWidth = 2 * sigma / windowSize;
		double[] coefTab;
		double[][] resultTab;
		double SumCoef, SumNormSignalCoef;

		if (binList == null)
			throw new BinListNoDataException();	

		// We create an array of coefficient. The index correspond to a distance and for each distance we calculate a coefficient 
		coefTab = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++)
			coefTab[i] = Math.exp(-(Math.pow(((double) (i * windowSize)), 2) / (2.0 * Math.pow((double) sigma, 2))));

		resultTab = new double[maxChromo][];
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				resultTab[i] = new double[binList[i].length];
				for(int j = 0; j < binList[i].length; j++) {
					if(binList[i][j] != 0)  {
						SumCoef = 0;
						SumNormSignalCoef = 0;
						for(int k = -halfWidth; k <= halfWidth; k++) {
							if((j + k >= 0) && ((j + k) < binList[i].length))  {
								distance = Math.abs(k);
								if(binList[i][j + k] != 0)  {
									SumCoef+=coefTab[distance];
									SumNormSignalCoef+=coefTab[distance] * binList[i][j+k];
								}
							}
						}
						if(SumCoef == 0)
							resultTab[i][j] = 0;
						else
							resultTab[i][j] = SumNormSignalCoef / SumCoef;
					}
					else
						resultTab[i][j] = 0.0;
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;
	}


	/**
	 * Indexes the intensities between <i>indexDown</i> and <i>indexUp</i> based
	 * on the highest and the lowest value of each chromosome.
	 * @param saturation percentage of the highest and lowest value saturated.
	 * @param indexDown Smallest value of the indexed data.
	 * @param indexUp Greatest value of the indexed data.
	 * @return New BinList resulting from the indexing.
	 * @throws BinListNoDataException
	 */
	public BinList indexByChromo(double saturation, double indexDown, double indexUp) throws BinListNoDataException {
		double percentUp = (100 - saturation) / 100;
		double percentDown = saturation / 100;
		double[][] resultTab;	
		double distanceIndexUpDown = indexUp - indexDown;

		if (binList == null) {
			throw new BinListNoDataException();
		}
		resultTab = new double[maxChromo][];
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				double[] listTmp = new double[binList[i].length];
				resultTab[i] = new double[binList[i].length];
				int k = 0;
				for (int j = 0; j < binList[i].length; j++) {
					if(binList[i][j] != 0) {
						listTmp[k] = binList[i][j];
						k++;
					}
				}
				if(k > 0) {

					// We create a new array containing all the values different from 0 
					double[] listTmpBis = new double[k];
					for(int j = 0; j < listTmpBis.length; j++) {
						listTmpBis[j] = listTmp[j];
					}
					// We want to have the values of intensities sorted for each chromosome
					Arrays.sort(listTmpBis);
					// We research the highest and the lowest value 
					int rankUp = (int)(percentUp * (listTmpBis.length - 1)); 
					int rankDown = (int)(percentDown * (listTmpBis.length - 1));

					double valueUp = listTmpBis[rankUp];
					double valueDown = listTmpBis[rankDown];

					// We calculate the difference between the highest and the lowest value
					double distanceValueUpDown = valueUp - valueDown;
					// We index the intensities 
					for (int j = 0; j < binList[i].length; j++) {
						if(binList[i][j] == 0) {
							resultTab[i][j] = 0;
						} else if(binList[i][j] < valueDown) {
							resultTab[i][j] = indexDown;
						} else if(binList[i][j] > valueUp) {
							resultTab[i][j] = indexUp;
						} else { 
							resultTab[i][j] = distanceIndexUpDown * (binList[i][j] - valueDown) / distanceValueUpDown + indexDown;
						}
					}
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;
	}


	/**
	 * Indexes the intensities between <i>indexDown</i> and <i>indexUp</i> 
	 * based on the highest and the lowest value of the whole genome.
	 * @param saturation percentage of the highest and lowest value saturated.
	 * @param indexDown Smallest value of the indexed data.
	 * @param indexUp Greatest value of the indexed data.
	 * @return New BinList resulting from the indexing.
	 * @throws BinListNoDataException
	 */
	public BinList index(double saturation, double indexDown, double indexUp) throws BinListNoDataException {
		double percentUp = (100 - saturation) / 100;
		double percentDown = saturation / 100;
		double[] listTmp;
		int k = 0, totalLength = 0;

		if (binList == null)
			throw new BinListNoDataException();	

		// We create an array containing all the intensities of all chromosomes
		for (short i = 0; i < maxChromo; i++) {
			if(binList[i] != null)
				totalLength+=binList[i].length;
		}
		listTmp = new double[totalLength];			
		for (short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					if(binList[i][j] != 0) {
						listTmp[k] = binList[i][j];
						k++;
					}
				}
			}
		}
		if (k > 0) {
			// We create a new array containing all the values different from 0 
			double[] listTmpBis = new double[k];
			for(int i = 0; i < listTmpBis.length; i++) {
				listTmpBis[i] = listTmp[i];		
			}
			// We want to have the values of intensities sorted for the whole genome
			Arrays.sort(listTmpBis);
			// We research the highest and the lowest value 
			int rankUp = (int)(percentUp * (listTmpBis.length - 1));
			int rankDown = (int)(percentDown * (listTmpBis.length - 1));
			double valueUp = listTmpBis[rankUp];
			double valueDown = listTmpBis[rankDown];
			// We calculate the difference between the highest and the lowest value
			double distanceValueUpDown = valueUp - valueDown;
			double distanceIndexUpDown = indexUp - indexDown;
			double[][] resultTab = new double[maxChromo][];
			for (short i = 0; i < maxChromo; i++) {
				if (binList[i] != null) {
					// We index the intensities
					resultTab[i] = new double[binList[i].length];
					for (int j = 0; j < binList[i].length; j++) {
						if (binList[i][j] == 0) {
							resultTab[i][j] = 0;
						} else if(binList[i][j] < valueDown) {
							resultTab[i][j] = indexDown;
						} else if(binList[i][j] > valueUp) {
							resultTab[i][j] = indexUp;
						} else { 
							resultTab[i][j] = distanceIndexUpDown * (binList[i][j] - valueDown) / distanceValueUpDown + indexDown;
						}
					}
				}
			}
			BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
			resultList.binList = resultTab;
			return resultList;
		} else {
			return null;
		}
	}


	/**
	 * Computes the coefficient of correlation between the current BinList
	 * and another BinList.
	 * @param list BinList we want to compare to the current BinList.
	 * @return The coefficient of correlation between the two lists.
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException 
	 */
	public double correlation(BinList list) throws BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException  {
		int j, n = 0;
		double meanX = 0, meanY = 0, stdDevX = 0, stdDevY = 0, correlationCoef = 0;

		if(list == null)
			throw new BinListNullException();
		if((this.binList == null) || (list.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != list.windowSize)
			throw new BinListDifferentWindowSizeException();

		// We compute means
		for(short i = 0; i < this.maxChromo; i++)  {
			if((this.binList[i] != null) && (list.binList[i] != null)) {
				j = 0;
				while((j < this.binList[i].length) && (j < list.binList[i].length)) {
					if((this.binList[i][j] != 0) && (list.binList[i][j] != 0)) {
						meanX+=this.binList[i][j];
						meanY+=list.binList[i][j];
						n++;
					}
					j++;
				}
			}
		}
		// Case where there is no value
		if(n == 0)
			return 0;	
		meanX/=n;
		meanY/=n;
		// We compute standard deviations
		for(short i = 0; i < this.maxChromo; i++) {
			if((this.binList[i] != null) && (list.binList[i] != null)) {
				j = 0;
				while((j < this.binList[i].length) && (j < list.binList[i].length)) {
					if((this.binList[i][j] != 0) && (list.binList[i][j] != 0)) {
						stdDevX+=Math.pow(this.binList[i][j] - meanX, 2);
						stdDevY+=Math.pow(list.binList[i][j] - meanY, 2);
						correlationCoef+=(this.binList[i][j] * list.binList[i][j]);
					}
					j++;
				}
			}
		}
		stdDevX=Math.sqrt(stdDevX / n);
		stdDevY=Math.sqrt(stdDevY / n);
		// We compute the correlation 
		correlationCoef=(correlationCoef - (n * meanX * meanY)) / ((n - 1) * stdDevX * stdDevY);
		return correlationCoef;
	}


	/**
	 * Computes the coefficient of correlation between the current BinList
	 * and another BinList. Only the chromosomes set to <i>true</i> in chromoList
	 * will be used in the calculation. 
	 * @param list BinList we want to compare to the current BinList.
	 * @param chromoList Set to true each chromosome of this list that you want to use in the calculation.
	 * @return The coefficient of correlation between the two lists. 
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	public double correlation(BinList list, boolean[] chromoList) throws BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException {
		int j, n = 0;
		double meanX = 0, meanY = 0, stdDevX = 0, stdDevY = 0, correlationCoef = 0;

		if(list == null)
			throw new BinListNullException();
		if((this.binList == null) || (list.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != list.windowSize)
			throw new BinListDifferentWindowSizeException();

		// We compute means
		for(short i = 0; i < this.maxChromo; i++)  {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if((i < chromoList.length) && (chromoList[i]) && (this.binList[i] != null) && (list.binList[i] != null)) {
				j = 0;
				while((j < this.binList[i].length) && (j < list.binList[i].length)) {
					if((this.binList[i][j] != 0) && (list.binList[i][j] != 0)) {
						meanX+=this.binList[i][j];
						meanY+=list.binList[i][j];
						n++;
					}
					j++;
				}
			}
		}
		// Case where there is no value
		if(n == 0)
			return 0;		
		meanX/=n;
		meanY/=n;
		// We compute standard deviations
		for(short i = 0; i < this.maxChromo; i++) {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if((i < chromoList.length) && (chromoList[i]) && (this.binList[i] != null) && (list.binList[i] != null)) {
				j = 0;
				while((j < this.binList[i].length) && (j < list.binList[i].length)) {
					if((this.binList[i][j] != 0) && (list.binList[i][j] != 0)) {
						stdDevX+=Math.pow(this.binList[i][j] - meanX, 2);
						stdDevY+=Math.pow(list.binList[i][j] - meanY, 2);
						correlationCoef+=(this.binList[i][j] * list.binList[i][j]);
					}
					j++;
				}
			}
		}
		stdDevX=Math.sqrt(stdDevX / n);
		stdDevY=Math.sqrt(stdDevY / n);
		// We compute the correlation 
		correlationCoef=(correlationCoef - (n * meanX * meanY)) / ((n - 1) * stdDevX * stdDevY);
		return correlationCoef;
	}


	/**
	 * Applies the function f(x) = log2(x + damper) - log2(average + damper) to each element x 
	 * of the current BinList. Returns the result in a new BinList.
	 * @param damper This parameter could be used to damp the signal.
	 * @return A new binList resulting of the calculation.
	 * @throws BinListNoDataException
	 */
	public BinList log(double damper) throws BinListNoDataException {
		double[][] resultTab;

		if (binList == null)
			throw new BinListNoDataException();

		resultTab = new double[maxChromo][];
		double mean = Math.log(average() + damper) / Math.log(2);
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				resultTab[i] = new double[binList[i].length];
				// We want to calculate the log2 for each element
				for (int j = 0; j < binList[i].length; j++) {
					if(this.binList[i][j] != 0)
						resultTab[i][j] = Math.log(binList[i][j] + damper) / Math.log(2) - mean;
					else
						resultTab[i][j] = 0;
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;
	}


	/**
	 * Adds <i>damper</i> to every value of the current BinList.
	 * @param damper Value to add.
	 * @return A new BinList.
	 * @throws BinListNoDataException
	 */
	public BinList addDumper(double damper) throws BinListNoDataException {
		double[][] resultTab;

		if (binList == null)
			throw new BinListNoDataException();

		resultTab = new double[maxChromo][];

		for (int i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				resultTab[i] = new double[binList[i].length];
				// We add dumper to each element
				for (int j = 0; j < binList[i].length; j++) {
					resultTab[i][j] = binList[i][j] + damper;
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;		
	}


	/**
	 * Applies the function f(x) = log2(x) to each element x 
	 * of the current BinList. Returns the result in a new BinList.
	 * @return A new binList resulting of the calculation.
	 * @throws BinListNoDataException
	 */
	public BinList log() throws BinListNoDataException {
		double[][] resultTab;

		if (binList == null)
			throw new BinListNoDataException();

		resultTab = new double[maxChromo][];
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				resultTab[i] = new double[binList[i].length];
				// We want to calculate the log2 for each element
				for (int j = 0; j < binList[i].length; j++) {
					if(this.binList[i][j] != 0)
						resultTab[i][j] = Math.log(binList[i][j]) / Math.log(2);
					else
						resultTab[i][j] = 0;
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;
	}


	/**
	 * Returns the smallest value of the current bin list.
	 * @return The smallest value of the current bin list.
	 * @throws BinListNoDataException
	 */
	public Double min() throws BinListNoDataException {
		Double min = null;

		if (binList == null)
			throw new BinListNoDataException();

		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					if((min == null) || (min > binList[i][j]))
						min = binList[i][j];
				}
			}
		}
		return min;
	}


	/**
	 * Returns the biggest value of the current bin list.
	 * @return The biggest value of the current bin list.
	 * @throws BinListNoDataException
	 */
	public Double max() throws BinListNoDataException {
		Double max = null;

		if (binList == null)
			throw new BinListNoDataException();

		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					if((max == null) || (max < binList[i][j]))
						max = binList[i][j];
				}
			}
		}
		return max;
	}


	/**
	 * Computes the slope of a BinList. To be accepted a slope can't change more 
	 * than +/- <i>allowedGap</i> on at least <i>allowedLength</i> bins. 
	 * We accept a maximum of <i>allowedKO</i> consecutive slopes that doesn't fit 
	 * in the +/- <i>allowedGap</i> criterion.
	 * The result is written in a file.
	 * @param allowedGap Maximum gap allowed between two slopes.
	 * @param allowedKO Maximum consecutive slopes that doesn't fit in the '+/- allowedGap' criterion.
	 * @param allowedLength Minimum length of a slope.
	 * @param filePath Path of the output file containing the result.
	 * @throws IOException
	 * @throws BinListNoDataException
	 */
	public void slope(double allowedGap, int allowedKO, int allowedLength, String filePath) throws IOException, BinListNoDataException {
		int j, x1, x2, slopeLength, KOCount, firstKOPosition = 0;
		double y1, y2, currentSlope;
		ArrayList<Double> slopeArray = new ArrayList<Double>();

		if (binList == null)
			throw new BinListNoDataException();

		BufferedWriter writer;
		// try to create a output file
		writer = new BufferedWriter(new FileWriter(new File(filePath)));
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				slopeLength = 0; 
				KOCount = 0;
				j = 0;
				x1 = 0; 
				y1 = binList[i][0];
				while (j < binList[i].length - 1) {
					j++;
					x2 = j;
					y2 = binList[i][j];
					// We calculate the slope
					currentSlope = Math.abs(0.96 * (x2 - x1) / (y2 - y1));
					if(slopeLength == 0) {
						slopeArray.add(currentSlope);
						slopeLength++;
					}
					else {
						// Case where the current slope corresponds to our criterion
						if(Math.abs(slopeArray.get(0) - currentSlope) <= allowedGap) {
							slopeArray.add(currentSlope);
							slopeLength++;
							KOCount = 0;
						}
						else {
							// Case where the current slope doesn't correspond to our criterion  
							KOCount++;
							if(KOCount == 1)
								firstKOPosition = x2;
							else if (KOCount > allowedKO) {
								// Case the slope can be selected
								if (slopeLength > allowedLength) {
									// print the slope value in the file
									writer.write(Double.toString(averageArrayListOfDouble(slopeArray)));
									writer.newLine();		
								}									
								slopeArray.clear();
								x1 = firstKOPosition;
								y1 = binList[i][x1];
								j = x1;
								slopeLength = 0; 
								KOCount = 0;								
							}
						}							
					}
				}
			}
		}
		writer.close();
	}





	/**
	 * Creates bins of intensity with a size of <i>intensityBinsSize</i>, 
	 * and computes how many bins of the BinList there is in each bin of intensity.
	 * Writes the result in a file. 
	 * @param intensityBinsSize Size of the bins of intensities.
	 * @param filePath Output file containing the result. 
	 * @throws IOException
	 * @throws BinListNoDataException
	 */
	public void repartition(double intensityBinsSize, String filePath) throws IOException, BinListNoDataException {
		if (binList == null)
			throw new BinListNoDataException();
		if(intensityBinsSize <= 0)
			return;

		double max = max();
		double min = min();
		double distanceMinMax = max - min;

		int result[] = new int[(int)(distanceMinMax / intensityBinsSize) + 1];
		for (short i = 0; i < maxChromo; i++) {
			if (binList[i] != null) {
				for(int j = 0; j < binList[i].length; j++) 
					result[(int)((binList[i][j] - min) / intensityBinsSize)]++;
			}
		}	
		BufferedWriter writer;
		// try to create a output file
		writer = new BufferedWriter(new FileWriter(new File(filePath)));
		for(int i = 0; i < result.length; i++) {
			double position = i * intensityBinsSize + min; 
			writer.write(Double.toString(position) + ", " + Double.toString(position + intensityBinsSize) + ", " + Integer.toString(result[i]));
			writer.newLine();		
		}
		writer.close();		
	}


	/**
	 * Calculates for every genomic position the timing of replication and the distance 
	 * to the closest gene expressed between <i>thresholdDown</i>  and <i>thresholdUp</i>
	 * and at least <i>distanceNoInfluence</i> bins far from more expressed genes.
	 * The current BinList indicates the copy number of genomic position.
	 * The result is written in a file.
	 * @param listDistance BinList containing information about gene expression.
	 * @param thresholdDown High threshold.
	 * @param thresholdUp Low threshold.
	 * @param distanceNoInfluence Distance to more expressed genes where we don't want to do the calculation.
	 * @param filePath Output file for the result.
	 * @throws IOException
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	public void distanceOnIntensity(BinList listDistance, double thresholdDown, double thresholdUp, int distanceNoInfluence, String filePath) 
	throws IOException, BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException {
		if((thresholdDown > thresholdUp) || (thresholdDown < 0) || (thresholdUp > 100) || (distanceNoInfluence < 0))
			return;
		if(listDistance == null)
			throw new BinListNullException();
		if((this.binList == null) || (listDistance.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != listDistance.windowSize)
			throw new BinListDifferentWindowSizeException();

		BinList listThreshold = listDistance.selectFromThreshold(thresholdDown, thresholdUp);
		BinList listDistanceLowGenes = listThreshold.computeDistance();

		if (thresholdUp < 100)  {
			BinList listThresholdHighGenes = listDistance.selectFromThreshold(thresholdUp, 100);
			listDistance = listDistanceLowGenes.computeDistanceNoInfluence(listThresholdHighGenes, distanceNoInfluence);
		}
		else 
			listDistance = listDistanceLowGenes;

		printDistanceOnIntensity(listDistance, filePath);
	}


	/**
	 * Returns a list with values set to <i>1</i> when for the values of the current list 
	 * between <i>thresholdDown</i> and <i>thresholdUp</i> and <i>0</i> otherwise.
	 * @param thresholdDown High threshold.
	 * @param thresholdUp Low threshold.
	 * @return A new BinList.
	 * @throws BinListNoDataException
	 */
	private BinList selectFromThreshold(double thresholdDown, double thresholdUp) throws BinListNoDataException {
		int totalLength = 0, k = 0;

		if (binList == null)
			throw new BinListNoDataException();

		// We create an array containing all the intensities of all chromosomes
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null)
				totalLength+=binList[i].length;
		}
		double[] listTmp = new double[totalLength];			
		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				for (int j = 0; j < binList[i].length; j++) {
					if(binList[i][j] != 0) {
						listTmp[k] = binList[i][j];
						k++;
					}
				}
			}
		}
		if(k > 0) {
			// We create a new array containing all the values different from 0 
			double[] listTmpBis = new double[k];
			for(int i = 0; i < listTmpBis.length; i++)
				listTmpBis[i] = listTmp[i];		
			// We want to have the values of intensities sorted for the whole genome
			Arrays.sort(listTmpBis);
			// We research the highest and the lowest value 
			int rankUp = (int)((thresholdUp / 100) * (listTmpBis.length - 1));
			int rankDown = (int)((thresholdDown / 100) * (listTmpBis.length - 1));
			double valueUp = listTmpBis[rankUp];
			double valueDown = listTmpBis[rankDown];
			// We calculate the difference between the highest and the lowest value
			double[][] resultTab = new double[maxChromo][];
			for(short i = 0; i < maxChromo; i++) {
				if(binList[i] != null) {
					resultTab[i] = new double[binList[i].length];
					for (int j = 0; j < binList[i].length; j++) {
						if((binList[i][j] == 0) || (binList[i][j] < valueDown) || (binList[i][j] > valueUp))
							resultTab[i][j] = 0;
						else
							resultTab[i][j] = 1;
					}
				}
			}
			BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
			resultList.binList = resultTab;
			return resultList;
		}
		else
			return null;
	}


	/**
	 * Computes the distance to the expressed genes of the current list.
	 * @return A list of distance.
	 * @throws BinListNoDataException
	 */
	private BinList computeDistance() throws BinListNoDataException {

		if (binList == null)
			throw new BinListNoDataException();

		double[][] resultTabA = new double[maxChromo][]; 
		double[][] resultTabB = new double[maxChromo][]; 

		for(short i = 0; i < maxChromo; i++) {
			if(this.binList[i] != null) {
				resultTabA[i] = new double[binList[i].length];
				resultTabB[i] = new double[binList[i].length];
				int count = -1;
				for(int j = 0; j < this.binList[i].length; j++) {
					if (this.binList[i][j] > 0)
						count = 0;
					else if(count != -1)
						count++;
					resultTabA[i][j] = count;
				}
				count = -1; 
				for(int j = this.binList[i].length - 1; j >= 0; j--) {
					if (binList[i][j] > 0)
						count = 0;
					else if(count != -1)
						count++;
					resultTabB[i][j] = count;
				}
				for(int j = 0; j < this.binList[i].length; j++)  {
					if (((resultTabA[i][j] > resultTabB[i][j]) && (resultTabB[i][j] != -1)) || (resultTabA[i][j] == -1))
						resultTabA[i][j] = resultTabB[i][j];
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTabA;
		return resultList;
	}


	/**
	 * Deletes the values of the current list under influence of genes more expressed of the <i>listThresholdHighGenes</i>.
	 * The influence zone is controlled by the parameter <i>distanceNoInfluence</i>.
	 * @param listThresholdHighGenes List of more expressed genes.
	 * @param distanceNoInfluence Distance to more expressed genes where we don't want to do the calculation.
	 * @return A BinList containing the current list without values for the positions under influence.
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	private  BinList computeDistanceNoInfluence(BinList listThresholdHighGenes, int distanceNoInfluence) 
	throws BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException  {
		if(distanceNoInfluence < 0)
			return null;

		if(listThresholdHighGenes == null)
			throw new BinListNullException();
		if((this.binList == null) || (listThresholdHighGenes.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != listThresholdHighGenes.windowSize)
			throw new BinListDifferentWindowSizeException();

		double[][] resultTab = new double[maxChromo][];
		for(short i = 0; i < maxChromo; i++) {
			if(this.binList[i] != null) {
				resultTab[i] = new double[this.binList[i].length];
				for (int j = 0; j < this.binList[i].length; j++) {
					resultTab[i][j] = this.binList[i][j];
					for(int k = j - distanceNoInfluence; k < j + distanceNoInfluence; k++) {
						if((j - distanceNoInfluence >= 0) && ( j + distanceNoInfluence < this.binList[i].length) && (listThresholdHighGenes.binList[i][k] > 0))
							resultTab[i][j] = -1.0;
					}
				}
			}
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;
	}


	/**
	 * Prints the values of distances on intensities in a file.
	 * The current list contains the values of intensities.
	 * @param listDistance BinList containing the values of distance. 
	 * @param filePath Path to the output result file.
	 * @throws IOException
	 * @throws BinListNullException
	 * @throws BinListNoDataException
	 * @throws BinListDifferentWindowSizeException
	 */
	private void printDistanceOnIntensity(BinList listDistance, String filePath) 
	throws IOException, BinListNullException, BinListNoDataException, BinListDifferentWindowSizeException {

		if(listDistance == null)
			throw new BinListNullException();
		if((this.binList == null) || (listDistance.binList == null))
			throw new BinListNoDataException();
		if(this.windowSize != listDistance.windowSize)
			throw new BinListDifferentWindowSizeException();

		BufferedWriter writer;
		// try to create a output file
		writer = new BufferedWriter(new FileWriter(new File(filePath)));
		writer.write("DISTANCE, TIMEX");
		writer.newLine();

		for(short i = 0; i < this.maxChromo; i++) {
			if((this.binList[i] != null) && (listDistance.binList[i] != null)) {
				for(int j = 0; j < listDistance.binList[i].length; j++) {
					if(j < this.binList[i].length) {
						if((listDistance.binList[i][j] >= 0) && (this.binList[i][j] > 0)) {
							writer.write(Double.toString(listDistance.binList[i][j]) + ", " + Double.toString(this.binList[i][j]));
							writer.newLine();
						}
					}
				}
			}
		}
		writer.close();
	}


	/**
	 * Computes the average of the current BinList.
	 * @return The average.
	 * @throws BinListNoDataException 
	 */
	private double average() throws BinListNoDataException {
		int n = 0;
		double mean = 0;

		if(binList == null) {
			throw new BinListNoDataException();
		}

		// Compute mean
		for (int i = 0; i < maxChromo; i++) {
			if (binList[i] != null) {
				for(int j = 0; j < binList[i].length; j++) {
					if(binList[i][j] != 0) {
						mean += binList[i][j];
						n++;
					}
				}
			}
		}
		if (n == 0) {
			return 0;
		} else {
			return (mean / n);
		}
	}


	/**
	 * Computes the average in an array of doubles between indexStart and indexStop.
	 * @param data Array of double.
	 * @param indexStart Index where to start in the array.
	 * @param indexStop Index where to stop in the array.
	 * @return The average.
	 */
	private double average(double[] data, int indexStart, int indexStop) {
		int n = 0;
		double mean = 0;

		// Compute mean
		for (int i = indexStart; i <= indexStop; i++) {
			if ((i < data.length) && (data[i] != 0)) {
				mean += data[i];
				n++;
			}
		}
		if (n == 0) {
			return 0;
		} else {
			mean /= n;
			return mean;
		}
	}	


	/**
	 * Computes the standard deviation in an array of doubles between indexStart and indexStop.
	 * @param data Array of double.
	 * @param indexStart Index where to start in the array.
	 * @param indexStop Index where to stop in the array.
	 * @return The standard deviation
	 */
	private double standardDeviation(double[] data, int indexStart, int indexStop) {
		int n = 0;
		double mean = 0;
		double sd = 0;				

		mean = average(data, indexStart, indexStop);
		if (mean != 0) {
			// We compute the standard deviation for the current chromosome				
			for (int i = indexStart; i < indexStop; i++){
				if (data[i] != 0) {
					sd += Math.pow(data[i] - mean, 2);
					n++;
				}
			}
			// We know n is different from 0 otherwise mean would equal 0 too
			sd /= n;			
			return sd;
		} else {
			return 0;
		}
	}


	/**
	 * Searches the peaks of a BinList. We consider a point as a peak when the 
	 * moving standard deviation = <i>nbSDAccepted</i> * global standard deviation.
	 * @param sizeMovingSD Width (in bp) of the moving standard deviation.
	 * @param nbSDAccepted  
	 * @return A BinList with only peaks.
	 * @throws BinListNoDataException
	 */
	public BinList searchPeaks(int sizeMovingSD, double nbSDAccepted) throws BinListNoDataException {
		if (binList == null)
			throw new BinListNoDataException();

		int halfWidth = sizeMovingSD / windowSize;
		double[][] resultTab = new double[maxChromo][]; 

		for(short i = 0; i < maxChromo; i++) {
			if(binList[i] != null) {
				double sd = standardDeviation(binList[i], 0, binList[i].length - 1);
				resultTab[i] = new double[binList[i].length];
				if (sd != 0) {
					double minAcceptedSD = nbSDAccepted * sd;
					for (int j = 0; j < binList[i].length; j++) {
						if (binList[i][j] != 0) {
							int indexStart = j - halfWidth;
							int indexStop = j + halfWidth;
							if (indexStart < 0) {
								indexStart = 0;
							}
							if (indexStop > binList[i].length - 1) {
								indexStop = binList[i].length - 1;
							}
							double localSd = standardDeviation(binList[i], indexStart, indexStop);
							if ((localSd != 0) && (localSd > minAcceptedSD)) {
								resultTab[i][j] = binList[i][j];
							} else {
								resultTab[i][j] = 0;
							}
						}
					}
				}
			}			
		}
		BinList resultList = new BinList(this.chromoConfig, this.windowSize, this.intensityCount);
		resultList.binList = resultTab;
		return resultList;		
	}
}