package gdpcore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BedGraphFileExtractor extends GenomicWindowFileExtractor{

	/**
	 * Extracts a bedGraph file.
	 * @param addressBedGraphFile Path to a bedGraph file.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public BedGraphFileExtractor(String logFile, ChromosomeList chromoConfig, String addressBedGraphFile) throws FileNotFoundException, IOException {
		super(chromoConfig);		
		extractFile(addressBedGraphFile);
		if(logFile != null) {
			// display statistics
			File configFile = new File(logFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			writer.write("BedGraph extraction - " + dateFormat.format(date));
			writer.newLine();
			writer.write("File: " + addressBedGraphFile);
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
		if ((extractedLine.length() != 0) && (extractedLine.charAt(0) == 'c')) {
			String[] splitedLine = extractedLine.split("\t");
			chromosomeNumber = chromoConfig.getIndex(splitedLine[0]);
			if(chromosomeNumber != -1)  {
				startList.get(chromosomeNumber).add(Integer.parseInt(splitedLine[1]));
				stopList.get(chromosomeNumber).add(Integer.parseInt(splitedLine[2]));
				intensityList.get(chromosomeNumber).add(Double.parseDouble(splitedLine[3]));
				lineCount++;
			}
		}
	}


	/**
	 * Creates a BinList object from the data extracted.
	 * @return A BinList object generated from the extracted data.
	 */
	public BinList getBinList(int windowSize, BinList.IntensityCalculation criterion) {
		return new BinList(chromoConfig, windowSize, startList, stopList, intensityList, criterion);		
	}	
}
