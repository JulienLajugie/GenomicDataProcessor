/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The NimbleFileExtractor class provides tool to extract data from a nimbleGene file.
 * @author Julien Lajugie
 * @version 0.1
 */
public class NimbleFileExtractor extends GenomicPositionIntensityFileExtractor {

	/**
	 * Extracts a bedGraph file.
	 * @param addressNimbleFile Path to a nimbleGene file.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public NimbleFileExtractor(String logFile, ChromosomeList chromoConfig, String addressNimbleFile) throws FileNotFoundException, IOException{
		super(chromoConfig);
		extractFile(addressNimbleFile);
		if(logFile != null) {
			// display statistics
			File configFile = new File(logFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			writer.write("Nimble extraction - " + dateFormat.format(date));
			writer.newLine();
			writer.write("File: " + addressNimbleFile);
			writer.newLine();
			writer.write("Number of lines extracted: " + lineCount);
			writer.newLine();
			writer.write("-------------------------------------------------------------------");
			writer.newLine();
			writer.close();
		}
	}

	/**
	 * Receives one line from the input file and tries
	 * to extract a chromosome number, a position and an intensity.
	 * @param extractedLine Line read from the file being extracted. 
	 */
	@Override
	protected void extractLine(String extractedLine) {
		int chromosomeNumber;
		if (extractedLine.trim().length() == 0) {
			return;
		}
		// We don't want to extract the header lines
		// So we extract only if the line starts with a number
		try {
			Integer.parseInt(extractedLine.substring(0, 1));
		} catch (Exception e){
			return;
		}

		String[] splitedLine = extractedLine.split("\t");
		if (!splitedLine[1].equals("RANDOM")) {
			String chromosomeChar = splitedLine[2];
			if (chromosomeChar.trim().length() == 0) {
				return;
			}
			char c = chromosomeChar.charAt(0);
			int endIndex = 0;
			while ((c != ':') && (endIndex< chromosomeChar.length())) {
				endIndex++;
				if ((endIndex < chromosomeChar.length())) {
					c = chromosomeChar.charAt(endIndex);
				}
			}
			chromosomeNumber = chromoConfig.getIndex(chromosomeChar.substring(0, endIndex));
			if(chromosomeNumber != -1)  {
				positionList.get(chromosomeNumber).add(Integer.parseInt(splitedLine[4]));
				intensityList.get(chromosomeNumber).add(Double.parseDouble(splitedLine[9]));
				lineCount++;
			}			
		}
	}


	/**
	 * Creates a BinList object from the extracted data.
	 * @param windowSize Size of the windows in base pair.
	 * @param criterion Specifies how to calculate the intensity.
	 * @return A BinList Object generated from the extracted data.
	 */
	public BinList getBinList(int windowSize, BinList.IntensityCalculation criterion) {
		return new BinList(chromoConfig, windowSize, positionList, intensityList, criterion);
	}

}
