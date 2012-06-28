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
 * The GdpFileExtractor class provides tool to extract data from a Gdp file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpFileExtractor extends GenomicPositionIntensityFileExtractor {
	private int windowSize; // size of the window	


	/**
	 * Extracts a Gdp file.
	 * @param addressFile Path to a Gdp file.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public GdpFileExtractor(String logFile, ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
		super(chromoConfig);		
		extractFile(addressFile);
		if(logFile != null) {
			// display statistics
			File configFile = new File(logFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			writer.write("Gdp extraction - " + dateFormat.format(date));
			writer.newLine();
			writer.write("File: " + addressFile);
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
	 * A window size is also calculated.
	 * @param extractedLine Line read from the file being extracted. 
	 */
	@Override
	protected void extractLine(String extractedLine) {
		int chromosomeNumber;

		if(extractedLine.charAt(0) == 'c') {
			String[] splitedLine = extractedLine.split("\t");
			if(lineCount == 0){
				windowSize = Integer.parseInt(splitedLine[2]) - Integer.parseInt(splitedLine[1]); 
			}
			chromosomeNumber = chromoConfig.getIndex(splitedLine[0]);
			if(chromosomeNumber != -1)  {
				positionList.get(chromosomeNumber).add(Integer.parseInt(splitedLine[1]));
				intensityList.get(chromosomeNumber).add(Double.parseDouble(splitedLine[3]));
				lineCount++;
			}
		}
	}


	/**
	 * Creates a BinList object from the data extracted.
	 * @return A BinList object generated from the extracted data.
	 */
	public BinList getBinList() {
		return new BinList(chromoConfig, windowSize, positionList, intensityList);		
	}
}
