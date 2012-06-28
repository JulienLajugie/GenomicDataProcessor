///**
// * @author Julien Lajugie
// * @version 0.1
// */
//package gdpcore;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * The GeneFileExtractor class provides tool to extract data containing information about genes.
// * The file must respect the following format:
// * name\tchrom\tstrand\ttxStart\ttxEnd\texonStarts\texonEnds\n
// * @author Julien Lajugie
// * @version 0.1
// */
//public final class GeneFileExtractor extends GenomicFileExtractor {
//
//	private static final int INDEX_STEP = 100000;
//	private ArrayList<ArrayList<Gene>> geneList;
//	private int[][] indexArray;
//
//	/**
//	 * Extract a GeneFile.
//	 * @param chromoConfig A ChromosomeConfiguration.
//	 * @param addressFile Path to a file.
//	 * @throws FileNotFoundException
//	 * @throws IOException
//	 */
//	public GeneFileExtractor(ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
//		super(chromoConfig);
//		geneList = new ArrayList<ArrayList<Gene>>();
//		for (int i = 0; i < chromoConfig.size(); i++) {
//			geneList.add(new ArrayList<Gene>());
//		}
//		extractFile(addressFile);
//		createIndexArray();
//	}
//
//
//	/**
//	 * Receives one line from the input file and tries
//	 * to create a Gene object from this data.
//	 * The gene is added to the list of genes.
//	 * @param Extractedline Line read from the file being extracted. 
//	 */
//	@Override
//	protected void extractLine(String extractedLine) {
//		if (extractedLine.charAt(0) != '#') {
//			String[] splitedLine = null;
//
//			splitedLine = extractedLine.split("\t");
//			int chromosomeNumber = chromoConfig.getIndex(splitedLine[1]);
//
//			if (chromosomeNumber != -1) {
//				String[] splitedExonStartsStr = null;
//				String[] splitedExonStopsStr = null;
//				int[] splitedExonStarts = null;
//				int[] splitedExonStops = null;
//
//				splitedExonStartsStr = splitedLine[5].split(",");
//				splitedExonStarts = new int[splitedExonStartsStr.length];
//				for (int i = 0; i < splitedExonStarts.length; i++) {
//					splitedExonStarts[i] = Integer.parseInt(splitedExonStartsStr[i]);
//				}
//				splitedExonStopsStr= splitedLine[6].split(",");
//				splitedExonStops = new int[splitedExonStopsStr.length];
//				for (int i = 0; i < splitedExonStops.length; i++) {
//					splitedExonStops[i] = Integer.parseInt(splitedExonStopsStr[i]);
//				}
//
//				geneList.get(chromosomeNumber).add(
//						new Gene(splitedLine[0], (short)chromosomeNumber, splitedLine[2], Integer.parseInt(splitedLine[3]),  
//								Integer.parseInt(splitedLine[4]), splitedExonStarts, splitedExonStops));
//				lineCount++;
//			}
//		}
//	}
//
//
//	/**
//	 * Indexes the data.
//	 */
//	private void createIndexArray() {
//		indexArray = new int[chromoConfig.size()][];
//		for (int i = 0; i < chromoConfig.size(); i++) {
//			if (geneList.get(i).size() > 0) {
//				int arrayLength = geneList.get(i).get(geneList.get(i).size() - 1).getTxStart() / INDEX_STEP;
//				indexArray[i] = new int[arrayLength + 1]; 
//				for (int j = 0; j < indexArray[i].length; j++) {
//					indexArray[i][j] = -1;
//				}
//				for (int j = 1; j < geneList.get(i).size(); j++) {
//					int index = geneList.get(i).get(j).getTxStart() / INDEX_STEP;
//					if (indexArray[i][index] == -1) {
//						indexArray[i][index] = j;
//					}
//				}
//				int previous = indexArray[i][indexArray[i].length - 1];
//				for (int j = indexArray[i].length - 2; j >= 0; j--) {
//					if (indexArray[i][j] == -1) {
//						indexArray[i][j] = previous;
//					} else {
//						previous = indexArray[i][j];
//					}
//				}
//			}
//		}
//	}
//
//
//	/**
//	 * Return the list of all the genes on chomosome <i>chromo</i> having a 
//	 * position between <i>posStart</i> and <i>posStop</i>.
//	 * @param chromo
//	 * @param posStart
//	 * @param posStop
//	 * @return A list of genes.
//	 */
//	public ArrayList<Gene> get(short chromo, int posStart, int posStop) {
//		int index;
//		int indexOfIndex = (posStop / INDEX_STEP) + 1; 
//		if ((indexArray == null) || (indexArray[chromo] == null)) {
//			return null;
//		} else if ((indexArray[chromo].length - 1 < indexOfIndex) || (indexArray[chromo][indexOfIndex] == -1)) {
//			index = geneList.get(chromo).size() - 1;
//		} else {
//			index = indexArray[chromo][indexOfIndex];
//		}
//		ArrayList<Gene> returnList = new ArrayList<Gene>();
//		for (int j = index ; j >= 0; j--) {
//			int txStart = geneList.get(chromo).get(j).getTxStart();
//			int txStop = geneList.get(chromo).get(j).getTxStop();
//			if (((txStart >= posStart) && (txStart <= posStop))
//					|| ((txStop >= posStart) && (txStop <= posStop)) 
//					|| ((txStart < posStart) && (txStop > posStop))) {
//				returnList.add(geneList.get(chromo).get(j));
//			}
//		}
//		return returnList;			
//	}
//
//
//	/**
//	 * Return the first gene of the gene list called <i>name</i>.
//	 * Return null if there is no gene with this name.
//	 * @param name Name of the gene.
//	 * @return A gene called <i>name</i>. Return null if not found.
//	 */
//	public Gene search(String name) {
//		if (geneList == null) {
//			return null;
//		} else {
//			boolean found = false;
//			Gene geneFound = null;
//			int i = 0;
//			while ((i < chromoConfig.size()) && (!found)) {
//				if (geneList.get(i) != null) {
//					int j = 0;
//					while ((j < geneList.get(i).size()) && (!found)) {
//						if (geneList.get(i).get(j).equals(name)) {
//							geneFound = geneList.get(i).get(j);
//							found = true;
//						}
//						j++;
//					}					
//				}
//				i++;
//			}
//			return geneFound;
//		}
//	}
//
//}
