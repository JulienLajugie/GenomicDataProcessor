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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The SolexaFileExtractor class provides tools to extract data from a Solexa file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SolexaFileExtractor extends GenomicPositionFileExtractor {

	private int[][] matchTypeCount; // number of lines with 0,1,2 mistakes per chromosome
	private int totalCount = 0;
	private int NMCount = 0;
	private int QCCount = 0;
	private int multiMatchCount = 0;
		
	/**
	 * Extracts the Solexa data from a file.
	 * @param addressFile Address of a solexa file.
	 * @param chromoConfig A ChromosomeConfiguration.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SolexaFileExtractor(String logFile, ChromosomeList chromoConfig, String addressFile) throws FileNotFoundException, IOException {
		super(chromoConfig);
		// initialize the number of read per chromosome and the data for statistics
		int total0M = 0, total1M = 0, total2M = 0;
		matchTypeCount = new int[chromoConfig.size()][3];		
		for(short i = 0; i < chromoConfig.size(); i++) {
			for(short j = 0; j < 3; j++)
				matchTypeCount[i][j] = 0;
		}
		// extract the data file
		extractFile(addressFile);
		// display statistics
		if(logFile != null) {
			// display statistics
				File configFile = new File(logFile);
				BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		        Date date = new Date();
		        DecimalFormat df = new DecimalFormat("##.#");
				writer.write("Solexa extraction - " + dateFormat.format(date));
				writer.newLine();
				writer.write("File: " + addressFile);
				writer.newLine();
				writer.write("Number of lines extracted: " + lineCount);
				writer.newLine();
				writer.write("Number lines in the file: " + totalCount);
				writer.newLine();
				writer.write("Percentage of lines extracted: " + df.format((double)lineCount / totalCount * 100) + "%");
				writer.newLine();
				writer.write("NM: " + NMCount);
				writer.newLine();
				writer.write("Percentage of NM: " + df.format((double)NMCount / totalCount * 100) + "%");
				writer.newLine();
				writer.write("QC: " + QCCount);
				writer.newLine();
				writer.write("Percentage of QC: " + df.format((double)QCCount / totalCount * 100) + "%");
				writer.newLine();
				writer.write("Multi match: " + multiMatchCount);
				writer.newLine();
				writer.write("Percentage of multimatch: " + df.format((double)multiMatchCount / totalCount * 100) + "%");				
				writer.newLine();
				writer.write("Chromosome\t0MM\t1MM\t2MM\tTotal");
				writer.newLine();
		    for(short i = 0; i < chromoConfig.size(); i++) {
		    	writer.write(chromoConfig.get(i) + "\t\t" + df.format((double)matchTypeCount[i][0]/lineCount*100) + "%\t" + df.format((double)matchTypeCount[i][1]/lineCount*100) + "%\t" + df.format((double)matchTypeCount[i][2]/lineCount*100) + "%\t" + df.format((double)(matchTypeCount[i][0]+matchTypeCount[i][1]+matchTypeCount[i][2])/lineCount*100) + "%");
		    	writer.newLine();
		    	total0M+=matchTypeCount[i][0];
		    	total1M+=matchTypeCount[i][1];
		    	total2M+=matchTypeCount[i][2];
		    }
		    writer.write("Total:\t" + df.format((double)total0M/lineCount*100) + "%\t" + df.format((double)total1M/lineCount*100) + "%\t" + df.format((double)total2M/lineCount*100) + "%\t\t100%");
			writer.newLine();
			writer.write("-------------------------------------------------------------------");
			writer.newLine();
			writer.close();
		}
	}
			
	
	/**
	 * Receives one line from the input file and tries
	 * to extract the position number if the sequence 
	 * correspond to the current criteria.
	 * The position extracted is used to build
	 * a list of position per chromosome.
	 * @param extractedLine a line read from the file being extracted. 
	 */
	@Override
	protected void extractLine(String extractedLine) {
		byte[] line = extractedLine.getBytes();
		byte[] matchChar = new byte[4]; 
		byte[] chromoChar = new byte[64];
		byte[] positionChar = new byte[10];
		short match0MNumber, match1MNumber, match2MNumber, chromoNumber;
		int positionNumber;
		
		totalCount++;

		if (line[0] == '\0')
			return;
		// skip first field
		int i = 0;
		while (line[i] != '\t')
	    	i++;
	    // skip second field
	   	i++;
	    while (line[i] != '\t')
	    	i++;
		// try to extract the number of match 0M
		i++;
		int j = 0;
	    while ((line[i] != '\t') && (line[i] != ':')) {
	    	matchChar[j] = line[i];
	    	i++;
	    	j++;
	    }
	    // case where we don't found a match
	    if (line[i] == '\t') {
	    	if (matchChar[0] == 'N') {
	    		NMCount++;
	    	} else if (matchChar[0] == 'Q') {
	    		QCCount++;
	    	}
	    	return;
	    }
	    match0MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 1M
	    i++;
	    j = 0;
	    while (line[i] != ':') {
	    	matchChar[j] = line[i];
	    	i++;
	    	j++;
	    }
	    match1MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 2M
	    i++;
	    j = 0;
	    while (line[i] != '\t') {
	    	matchChar[j] = line[i];
	    	i++;
	    	j++;
	    }
	    match2MNumber = Short.parseShort(new String(matchChar, 0, j));
		// we only want lines that correspond to our criteria
	    if (match0MNumber + match1MNumber + match2MNumber != 1) {
	    	multiMatchCount++;
	    	return;
	    }
	    
	    while(line[i] != '.' )  {
			chromoChar[j] = line[i];
			i++;
			j++;
		}
		chromoNumber = chromoConfig.getIndex(new String(chromoChar, 0, j));
	    
		// Case of the chromosome is not accepted in the current configuration
		if(chromoNumber == -1)
	    	return;	    
	    
		// try to extract the position number
	    i+=4;  // we want to get rid of 'fa:'
	    j = 0;
	    while ((line[i] != 'F') && (line[i] != 'R')) {
	    	positionChar[j] = line[i];
	    	i++;
	    	j++;
	    }
	    positionNumber = Integer.parseInt(new String(positionChar, 0, j));
	    // add data for the statistics
    	matchTypeCount[chromoNumber][0] += match0MNumber;
    	matchTypeCount[chromoNumber][1] += match1MNumber;
    	matchTypeCount[chromoNumber][2] += match2MNumber;
	    // add the data
	    positionList.get(chromoNumber).add(positionNumber);
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
