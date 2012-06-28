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
 * The SolidFileExtractor class provides tools to extract data from a Solid file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SolidFileExtractor extends GenomicPositionFileExtractor {


	/**
	 * Extracts the Solid data from a file.
	 * @param addressFile Address of a solid file.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */	
	public SolidFileExtractor(String logFile, ChromosomeList chromoConfig, String addressFile) throws IOException, FileNotFoundException {
		super(chromoConfig);
		// extract the data file
		extractFile(addressFile);
		if(logFile != null) {
			// display statistics
			File configFile = new File(logFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			writer.write("Solid extraction - " + dateFormat.format(date));
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
	 * to extract the position number. 
	 * The position extracted is used to build
	 * a list of position per chromosome.
	 * @param extractedLine a line read from the file being extracted. 
	 */
	@Override
	protected void extractLine(String extractedLine) {
		byte[] line = extractedLine.getBytes();
		byte[] chromoChar = new byte[3];
		byte[] positionChar = new byte[10]; 
		int i = 0, tabCpt = 0, positionNumber;
		short chromoNumber;

		if ((line[0] == '\0') || (line[0] == '#'))
			return;
		// loop until reaching the 4rd field 
		while(tabCpt < 3) {
			if (line[i] == '\t')
				tabCpt++;
			i++;
		}		    
		int j = 0;
		// try to extract the position number
		while (line[i] != '\t') {
			positionChar[j] = line[i];
			i++;
			j++;
		}		
		positionNumber = Integer.parseInt(new String(positionChar, 0, j));
		// loop until reaching the 9th field */
		while(tabCpt < 8) {
			if (line[i] == '\t')
				tabCpt++;
			i++;
		}
		// loop until reaching the chromosome field that starts with i= */
		while(line[i] != 'i')
			i++; 
		// We want to get rid of "i="
		i+=2;		     
		j = 0;
		// try to extract the chromosome number
		while (line[i] != ';') {  
			chromoChar[j] = line[i];
			i++;
			j++;
		}
		chromoNumber = Short.parseShort(new String(chromoChar, 0, j));
		if(chromoNumber > chromoConfig.size())  
			return;
		// add the data
		positionList.get(chromoNumber - 1).add(positionNumber);
		lineCount++;
	}


	/**
	 * Creates a BinList object from the extracted data.
	 * @param windowSize Size of the windows in base pair.
	 * @return A BinList Object generated from the extracted data.
	 */
	public BinList getBinList(int windowSize) {
		return new BinList(chromoConfig, windowSize, positionList);
	}
}
