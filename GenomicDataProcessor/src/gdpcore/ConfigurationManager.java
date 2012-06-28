/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import gdpcore.BinList.IntensityCalculation;
import gdpgui.GdpBinListTrack.GraphicsType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Julien Lajugie
 * @version 0.1
 * The ConfigurationManager class manages the parameters of the components of gdpgui.
 */
public final class ConfigurationManager implements Cloneable {
	private static final String CONFIG_FILE_PATH = "config.cfg";// Path of the config file

	private String 	chromoConfigFile = 
		"human_chromosomes.cfg";								// Chromosome configuration file
	private int 	windowSize = 5000;							// Default size of the bin list for the extraction
	private String 	lookAndFeel = 
		"javax.swing.plaf.metal.MetalLookAndFeel";				// Default look and feel
	private String	solexaLogFile = "log.txt";					// Default Solexa log file
	private String	solidLogFile = "log.txt";					// Default Solid log file
	private String	nimbleLogFile = "log.txt";					// Default NimbleGene log file
	private String	bedGraphLogFile = "log.txt";				// Default bedGraph log file
	private IntensityCalculation criterionOfCalculation =
		IntensityCalculation.average; 							// Default way of calculating the intensity of a NimbleGene file
	private int 	gdpMainFramePreferredWidth = 800;			// Preferred width of the main frame
	private int 	gdpMainFramePreferredHeight = 600;			// Preferred height of the main frame
	private int 	gdpMainFrameMinWidth = 600;					// Minimum width of the main frame
	private int 	gdpMainFrameMinHeight = 400;				// Minimum height of the main frame
	private int 	gdpConfigPreferredWidth = 600;				// Preferred width of the config frame
	private int 	gdpConfigPreferredHeight = 400;				// Preferred height of the config frame
	private int 	gdpConfigMinimumSplitWidth = 100;			// Minimum size with the splitter of the config frame
	private int 	gdpCurveContainerPreferredWidth = 500;		// Default preferred width of a CurveContainer
	private int 	gdpCurveContainerPreferredHeight = 150;		// Default preferred height of a CurveContainer
	private int 	gdpCurveContainerMinWidth = 500;			// Minimum width of a CurveContainer
	private int		gdpCurveContainerMinHeight = 120;			// Minimum height of a CurveContainer
	private float 	gdpCurveGraphicStrokeWidth = 1.0f;			// Stroke size of the graphics on a CurveGraphics
	private int 	gdpCurveGraphicVerticalLinesCount = 10;		// Number of X lines displayed on a CurveGraphics
	private int 	gdpCurveGraphicHorizontalLinesCount = 10;	// Type of graphics displayed on a CurveGraphics
	private GraphicsType gdpCurveGraphicGraphicsType = 
		GraphicsType.points;									// Number of X lines displayed on a CurveGraphics
	private int 	gdpGeneContainerPreferredHeight = 200;		// Preferred height of the gene container
	private float 	gdpGenesGraphicsStrokeWidth = 1.0f;			// Stroke size of the graphics on a GenesGraphics
	private int 	gdpGenesGraphicsVerticalLinesCount = 10;	// Number of vertical lines on a GenesGraphics
	private int 	gdpTrackConfigDefaultWidth = 340;			// Default width of the trackConf dialog
	private int 	gdpTrackConfigDefaultHeight = 220;			// Default height of the trackConf dialog
	private int 	gdpTrackConfigMaxPreferredHeight = 1000;	// Maximum value for preferredHeight in the trackConf dialog
	private int 	gdpMainPanelDefaultMin = 0;					// Default minimum position
	private int 	gdpMainPanelDefaultMax = 500000; 			// Default maximum position
	private short 	gdpMainPanelDefaultChromo = 0;				// Default chromosome
	private int 	gdpMainPanelNbXScrollIncrement = 50; 		// Number of increment with jsbXAxis
	private int 	gdpMainPanelMinZoom = 6; 					// Minimum zoom = 2^MIN_ZOOM bases 	


	/**
	 * Constructor. Creates an instance of ConfigurationManager.
	 */
	public ConfigurationManager() {
		super();
	}

	
	public void reset() {
		new ConfigurationManager();
	}
	
	
	@Override
	public ConfigurationManager clone(){
		try {
			return (ConfigurationManager)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bedGraphLogFile == null) ? 0 : bedGraphLogFile.hashCode());
		result = prime
				* result
				+ ((chromoConfigFile == null) ? 0 : chromoConfigFile.hashCode());
		result = prime
				* result
				+ ((criterionOfCalculation == null) ? 0
						: criterionOfCalculation.hashCode());
		result = prime * result + gdpConfigMinimumSplitWidth;
		result = prime * result + gdpConfigPreferredHeight;
		result = prime * result + gdpConfigPreferredWidth;
		result = prime * result + gdpCurveContainerMinHeight;
		result = prime * result + gdpCurveContainerMinWidth;
		result = prime * result + gdpCurveContainerPreferredHeight;
		result = prime * result + gdpCurveContainerPreferredWidth;
		result = prime
				* result
				+ ((gdpCurveGraphicGraphicsType == null) ? 0
						: gdpCurveGraphicGraphicsType.hashCode());
		result = prime * result + gdpCurveGraphicHorizontalLinesCount;
		result = prime * result
				+ Float.floatToIntBits(gdpCurveGraphicStrokeWidth);
		result = prime * result + gdpCurveGraphicVerticalLinesCount;
		result = prime * result + gdpGeneContainerPreferredHeight;
		result = prime * result
				+ Float.floatToIntBits(gdpGenesGraphicsStrokeWidth);
		result = prime * result + gdpGenesGraphicsVerticalLinesCount;
		result = prime * result + gdpMainFrameMinHeight;
		result = prime * result + gdpMainFrameMinWidth;
		result = prime * result + gdpMainFramePreferredHeight;
		result = prime * result + gdpMainFramePreferredWidth;
		result = prime * result + gdpMainPanelDefaultChromo;
		result = prime * result + gdpMainPanelDefaultMax;
		result = prime * result + gdpMainPanelDefaultMin;
		result = prime * result + gdpMainPanelMinZoom;
		result = prime * result + gdpMainPanelNbXScrollIncrement;
		result = prime * result + gdpTrackConfigDefaultHeight;
		result = prime * result + gdpTrackConfigDefaultWidth;
		result = prime * result + gdpTrackConfigMaxPreferredHeight;
		result = prime * result
				+ ((lookAndFeel == null) ? 0 : lookAndFeel.hashCode());
		result = prime * result
				+ ((nimbleLogFile == null) ? 0 : nimbleLogFile.hashCode());
		result = prime * result
				+ ((solexaLogFile == null) ? 0 : solexaLogFile.hashCode());
		result = prime * result
				+ ((solidLogFile == null) ? 0 : solidLogFile.hashCode());
		result = prime * result + windowSize;
		return result;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurationManager other = (ConfigurationManager) obj;
		if (bedGraphLogFile == null) {
			if (other.bedGraphLogFile != null)
				return false;
		} else if (!bedGraphLogFile.equals(other.bedGraphLogFile))
			return false;
		if (chromoConfigFile == null) {
			if (other.chromoConfigFile != null)
				return false;
		} else if (!chromoConfigFile.equals(other.chromoConfigFile))
			return false;
		if (criterionOfCalculation == null) {
			if (other.criterionOfCalculation != null)
				return false;
		} else if (!criterionOfCalculation.equals(other.criterionOfCalculation))
			return false;
		if (gdpConfigMinimumSplitWidth != other.gdpConfigMinimumSplitWidth)
			return false;
		if (gdpConfigPreferredHeight != other.gdpConfigPreferredHeight)
			return false;
		if (gdpConfigPreferredWidth != other.gdpConfigPreferredWidth)
			return false;
		if (gdpCurveContainerMinHeight != other.gdpCurveContainerMinHeight)
			return false;
		if (gdpCurveContainerMinWidth != other.gdpCurveContainerMinWidth)
			return false;
		if (gdpCurveContainerPreferredHeight != other.gdpCurveContainerPreferredHeight)
			return false;
		if (gdpCurveContainerPreferredWidth != other.gdpCurveContainerPreferredWidth)
			return false;
		if (gdpCurveGraphicGraphicsType == null) {
			if (other.gdpCurveGraphicGraphicsType != null)
				return false;
		} else if (!gdpCurveGraphicGraphicsType
				.equals(other.gdpCurveGraphicGraphicsType))
			return false;
		if (gdpCurveGraphicHorizontalLinesCount != other.gdpCurveGraphicHorizontalLinesCount)
			return false;
		if (Float.floatToIntBits(gdpCurveGraphicStrokeWidth) != Float
				.floatToIntBits(other.gdpCurveGraphicStrokeWidth))
			return false;
		if (gdpCurveGraphicVerticalLinesCount != other.gdpCurveGraphicVerticalLinesCount)
			return false;
		if (gdpGeneContainerPreferredHeight != other.gdpGeneContainerPreferredHeight)
			return false;
		if (Float.floatToIntBits(gdpGenesGraphicsStrokeWidth) != Float
				.floatToIntBits(other.gdpGenesGraphicsStrokeWidth))
			return false;
		if (gdpGenesGraphicsVerticalLinesCount != other.gdpGenesGraphicsVerticalLinesCount)
			return false;
		if (gdpMainFrameMinHeight != other.gdpMainFrameMinHeight)
			return false;
		if (gdpMainFrameMinWidth != other.gdpMainFrameMinWidth)
			return false;
		if (gdpMainFramePreferredHeight != other.gdpMainFramePreferredHeight)
			return false;
		if (gdpMainFramePreferredWidth != other.gdpMainFramePreferredWidth)
			return false;
		if (gdpMainPanelDefaultChromo != other.gdpMainPanelDefaultChromo)
			return false;
		if (gdpMainPanelDefaultMax != other.gdpMainPanelDefaultMax)
			return false;
		if (gdpMainPanelDefaultMin != other.gdpMainPanelDefaultMin)
			return false;
		if (gdpMainPanelMinZoom != other.gdpMainPanelMinZoom)
			return false;
		if (gdpMainPanelNbXScrollIncrement != other.gdpMainPanelNbXScrollIncrement)
			return false;
		if (gdpTrackConfigDefaultHeight != other.gdpTrackConfigDefaultHeight)
			return false;
		if (gdpTrackConfigDefaultWidth != other.gdpTrackConfigDefaultWidth)
			return false;
		if (gdpTrackConfigMaxPreferredHeight != other.gdpTrackConfigMaxPreferredHeight)
			return false;
		if (lookAndFeel == null) {
			if (other.lookAndFeel != null)
				return false;
		} else if (!lookAndFeel.equals(other.lookAndFeel))
			return false;
		if (nimbleLogFile == null) {
			if (other.nimbleLogFile != null)
				return false;
		} else if (!nimbleLogFile.equals(other.nimbleLogFile))
			return false;
		if (solexaLogFile == null) {
			if (other.solexaLogFile != null)
				return false;
		} else if (!solexaLogFile.equals(other.solexaLogFile))
			return false;
		if (solidLogFile == null) {
			if (other.solidLogFile != null)
				return false;
		} else if (!solidLogFile.equals(other.solidLogFile))
			return false;
		if (windowSize != other.windowSize)
			return false;
		return true;
	}


	/**
	 * Writes the configuration in a file.
	 * @throws IOException
	 */
	public void writeConfig() throws IOException {
		File configFile = new File(CONFIG_FILE_PATH);
		BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

		writer.write("chromoConfigFile\t" + chromoConfigFile);
		writer.newLine();
		writer.write("windowSize\t" + windowSize);
		writer.newLine();
		writer.write("lookAndFeel\t" + lookAndFeel);
		writer.newLine();
		writer.write("solexaLogFile\t" + solexaLogFile);
		writer.newLine();
		writer.write("solidLogFile\t" + solidLogFile);
		writer.newLine();
		writer.write("nimbleLogFile\t" + nimbleLogFile);
		writer.newLine();
		writer.write("bedGraphLogFile\t" + bedGraphLogFile);
		writer.newLine();
		writer.write("criterionOfCalculation\t" + criterionOfCalculation);
		writer.newLine();
		writer.write("gdpMainFramePreferredWidth\t" + gdpMainFramePreferredWidth);
		writer.newLine();
		writer.write("gdpMainFramePreferredHeight\t" + gdpMainFramePreferredHeight);
		writer.newLine();
		writer.write("gdpMainFrameMinWidth\t" + gdpMainFrameMinWidth);
		writer.newLine();
		writer.write("gdpMainFrameMinHeight\t" + gdpMainFrameMinHeight);
		writer.newLine();
		writer.write("gdpConfigPreferredWidth\t" + gdpConfigPreferredWidth);
		writer.newLine();
		writer.write("gdpConfigPreferredHeight\t" + gdpConfigPreferredHeight);
		writer.newLine();
		writer.write("gdpConfigMinimumSplitWidth\t" + gdpConfigMinimumSplitWidth);
		writer.newLine();
		writer.write("gdpCurveContainerPreferredWidth\t" + gdpCurveContainerPreferredWidth);
		writer.newLine();
		writer.write("gdpCurveContainerPreferredHeight\t" + gdpCurveContainerPreferredHeight);
		writer.newLine();
		writer.write("gdpCurveContainerMinWidth\t" + gdpCurveContainerMinWidth);
		writer.newLine();
		writer.write("gdpCurveContainerMinHeight\t" + gdpCurveContainerMinHeight);
		writer.newLine();
		writer.write("gdpCurveGraphicStrokeWidth\t" + gdpCurveGraphicStrokeWidth);
		writer.newLine();
		writer.write("gdpCurveGraphicVerticalLinesCount\t" + gdpCurveGraphicVerticalLinesCount);
		writer.newLine();		
		writer.write("gdpCurveGraphicHorizontalLinesCount\t" + gdpCurveGraphicHorizontalLinesCount);
		writer.newLine();		
		writer.write("gdpCurveGraphicGraphicsType\t" + gdpCurveGraphicGraphicsType);
		writer.newLine();		
		writer.write("gdpGeneContainerPreferredHeight\t" + gdpGeneContainerPreferredHeight);
		writer.newLine();
		writer.write("gdpGenesGraphicsStrokeWidth\t" + gdpGenesGraphicsStrokeWidth);
		writer.newLine();
		writer.write("gdpGenesGraphicsVerticalLinesCount\t" + gdpGenesGraphicsVerticalLinesCount);
		writer.newLine();
		writer.write("gdpTrackConfigDefaultWidth\t" + gdpTrackConfigDefaultWidth);
		writer.newLine();
		writer.write("gdpTrackConfigDefaultHeight\t" + gdpTrackConfigDefaultHeight);
		writer.newLine();
		writer.write("gdpTrackConfigMaxPreferredHeight\t" + gdpTrackConfigMaxPreferredHeight);
		writer.newLine();
		writer.write("gdpMainPanelDefaultMin\t" + gdpMainPanelDefaultMin);
		writer.newLine();
		writer.write("gdpMainPanelDefaultMax\t" + gdpMainPanelDefaultMax);
		writer.newLine();
		writer.write("gdpMainPanelDefaultChromo\t" + gdpMainPanelDefaultChromo);
		writer.newLine();
		writer.write("gdpMainPanelNbXScrollIncrement\t" + gdpMainPanelNbXScrollIncrement);
		writer.newLine();
		writer.write("gdpMainPanelMinZoom\t" + gdpMainPanelMinZoom);
		writer.newLine();

		writer.close();
	}


	/**
	 * Reads the configuration from a file.
	 * @throws IOException
	 */
	public void readConfig() throws IOException {
		File configFile = new File(CONFIG_FILE_PATH);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(configFile));

			// extract data
			String line = null;
			while((line = reader.readLine()) != null) {
				extractLine(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		} finally {
			reader.close();
		}
	}

	
	/**
	 * Reads a line from the config file and extracts the data.
	 * @param line A line from the config file.
	 */
	private void extractLine(String line) {
		String[] splitedLine = line.split("\t");
		String field = splitedLine[0];
		String value = splitedLine[1];
		if (field.equals("chromoConfigFile")) {
			chromoConfigFile = value;
		} else if (field.equals("windowSize")) {
			windowSize = Integer.parseInt(value);
		} else if (field.equals("lookAndFeel")) {
			lookAndFeel = value;
		} else if (field.equals("solexaLogFile")) {
			solexaLogFile = value;
		} else if (field.equals("solidLogFile")) {
			solidLogFile = value;
		} else if (field.equals("solexaLogFile")) {
			solexaLogFile = value;
		} else if (field.equals("nimbleLogFile")) {
			nimbleLogFile = value;
		} else if (field.equals("bedGraphLogFile")) {
			bedGraphLogFile = value;
		} else if (field.equals("criterionOfCalculation")) {
			if (value.equals(BinList.IntensityCalculation.average.toString())) {
				criterionOfCalculation = IntensityCalculation.average;
			} else if (value.equals(BinList.IntensityCalculation.maximum.toString())) {
				criterionOfCalculation = IntensityCalculation.maximum;
			} else if (value.equals(BinList.IntensityCalculation.sum.toString())) {
				criterionOfCalculation = IntensityCalculation.sum;
			}
		} else if (field.equals("gdpMainFramePreferredWidth")) {
			gdpMainFramePreferredWidth = Integer.parseInt(value);
		} else if (field.equals("gdpMainFramePreferredHeight")) {
			gdpMainFramePreferredHeight = Integer.parseInt(value);
		} else if (field.equals("gdpMainFrameMinWidth")) {
			gdpMainFrameMinWidth = Integer.parseInt(value);
		} else if (field.equals("gdpMainFrameMinHeight")) {
			gdpMainFrameMinHeight = Integer.parseInt(value);
		} else if (field.equals("gdpConfigPreferredWidth")) {
			gdpConfigPreferredWidth = Integer.parseInt(value);
		} else if (field.equals("gdpConfigPreferredHeight")) {
			gdpConfigPreferredHeight = Integer.parseInt(value);
		} else if (field.equals("gdpConfigMinimumSplitWidth")) {
			gdpConfigMinimumSplitWidth = Integer.parseInt(value);			
		} else if (field.equals("gdpCurveContainerPreferredWidth")) {
			gdpCurveContainerPreferredWidth = Integer.parseInt(value);
		} else if (field.equals("gdpCurveContainerPreferredHeight")) {
			gdpCurveContainerPreferredHeight = Integer.parseInt(value);
		} else if (field.equals("gdpCurveContainerMinWidth")) {
			gdpCurveContainerMinWidth = Integer.parseInt(value);
		} else if (field.equals("gdpCurveContainerMinHeight")) {
			gdpCurveContainerMinHeight = Integer.parseInt(value);
		} else if (field.equals("gdpCurveGraphicStrokeWidth")) {
			gdpCurveGraphicStrokeWidth = Float.parseFloat(value);
		} else if (field.equals("gdpCurveGraphicVerticalLinesCount")) {
			gdpCurveGraphicVerticalLinesCount = Integer.parseInt(value);
		} else if (field.equals("gdpCurveGraphicHorizontalLinesCount")) {
			gdpCurveGraphicHorizontalLinesCount = Integer.parseInt(value);			
		} else if (field.equals("gdpCurveGraphicGraphicsType")) {
			if (value.equals(GraphicsType.bar.toString())) {
				gdpCurveGraphicGraphicsType = GraphicsType.bar;
			} else if (value.equals(GraphicsType.curve.toString())) {
				gdpCurveGraphicGraphicsType = GraphicsType.curve;
			} else if (value.equals(GraphicsType.points.toString())) {
				gdpCurveGraphicGraphicsType = GraphicsType.points;
			} else if (value.equals(GraphicsType.dense.toString())) {
				gdpCurveGraphicGraphicsType = GraphicsType.dense;
			}			
		} else if (field.equals("gdpGeneContainerPreferredHeight")) {
			gdpGeneContainerPreferredHeight = Integer.parseInt(value);
		} else if (field.equals("gdpGenesGraphicsStrokeWidth")) {
			gdpGenesGraphicsStrokeWidth = Float.parseFloat(value);
		} else if (field.equals("gdpGenesGraphicsVerticalLinesCount")) {
			gdpGenesGraphicsVerticalLinesCount = Integer.parseInt(value);
		} else if (field.equals("gdpTrackConfigDefaultWidth")) {
			gdpTrackConfigDefaultWidth = Integer.parseInt(value);
		} else if (field.equals("gdpTrackConfigDefaultHeight")) {
			gdpTrackConfigDefaultHeight = Integer.parseInt(value);
		} else if (field.equals("gdpTrackConfigMaxPreferredHeight")) {
			gdpTrackConfigMaxPreferredHeight = Integer.parseInt(value);
		} else if (field.equals("gdpMainPanelDefaultMin")) {
			gdpMainPanelDefaultMin = Integer.parseInt(value);
		} else if (field.equals("gdpMainPanelDefaultMax")) {
			gdpMainPanelDefaultMax = Integer.parseInt(value);
		} else if (field.equals("gdpMainPanelDefaultChromo")) {
			gdpMainPanelDefaultChromo = Short.parseShort(value);
		} else if (field.equals("gdpMainPanelNbXScrollIncrement")) {
			gdpMainPanelNbXScrollIncrement = Integer.parseInt(value);
		} else if (field.equals("gdpMainPanelMinZoom")) {
			gdpMainPanelMinZoom = Integer.parseInt(value);
		}
	}
	

	/**
	 * @return the chromoConfigFile
	 */
	public final String getChromoConfigFile() {
		return chromoConfigFile;
	}
	
	
	/**
	 * @param chromoConfigFile the chromoConfigFile to set
	 */
	public final void setChromoConfigFile(String chromoConfigFile) {
		this.chromoConfigFile = chromoConfigFile;
	}
	
	
	/**
	 * @return the windowSize
	 */
	public final int getWindowSize() {		
		return windowSize;
	}
	
	
	/**
	 * @param windowSize the windowSize to set
	 */
	public final void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	
	
	/**
	 * @return the lookAndFeel
	 */
	public final String getLookAndFeel() {
		return lookAndFeel;
	}
	
	
	/**
	 * @param lookAndFeel the lookAndFeel to set
	 */
	public final void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}
	
	
	/**
	 * @return the solexaLogFile
	 */
	public final String getSolexaLogFile() {
		return solexaLogFile;
	}
	
	
	/**
	 * @param solexaLogFile the solexaLogFile to set
	 */
	public final void setSolexaLogFile(String solexaLogFile) {
		this.solexaLogFile = solexaLogFile;
	}
	
	
	/**
	 * @return the solidLogFile
	 */
	public final String getSolidLogFile() {
		return solidLogFile;
	}
	
	
	/**
	 * @param solidLogFile the solidLogFile to set
	 */
	public final void setSolidLogFile(String solidLogFile) {
		this.solidLogFile = solidLogFile;
	}
	
	
	/**
	 * @return the nimbleLogFile
	 */
	public final String getNimbleLogFile() {
		return nimbleLogFile;
	}
	
	
	/**
	 * @param nimbleLogFile the nimbleLogFile to set
	 */
	public final void setNimbleLogFile(String nimbleLogFile) {
		this.nimbleLogFile = nimbleLogFile;
	}
	
	
	/**
	 * @return the bedGraphLogFile
	 */
	public final String getBedGraphLogFile() {
		return bedGraphLogFile;
	}
	
	
	/**
	 * @param bedGraphLogFile the bedGraphLogFile to set
	 */
	public final void setBedGraphLogFile(String bedGraphLogFile) {
		this.bedGraphLogFile = bedGraphLogFile;
	}
	
	
	/**
	 * @return the criterionOfCalculation
	 */
	public final BinList.IntensityCalculation getCriterionOfCalculation() {
		return criterionOfCalculation;
	}
	
	
	/**
	 * @param criterionOfCalculation the criterionOfCalculation to set
	 */
	public final void setCriterionOfCalculation(
			BinList.IntensityCalculation criterionOfCalculation) {
		this.criterionOfCalculation = criterionOfCalculation;
	}
	
	
	/**
	 * @return the gdpMainFramePreferredWidth
	 */
	public final int getGdpMainFramePreferredWidth() {
		return gdpMainFramePreferredWidth;
	}
	
	
	/**
	 * @param gdpMainFramePreferredWidth the gdpMainFramePreferredWidth to set
	 */
	public final void setGdpMainFramePreferredWidth(int gdpMainFramePreferredWidth) {
		this.gdpMainFramePreferredWidth = gdpMainFramePreferredWidth;
	}
	
	
	/**
	 * @return the gdpMainFramePreferredHeight
	 */
	public final int getGdpMainFramePreferredHeight() {
		return gdpMainFramePreferredHeight;
	}
	
	
	/**
	 * @param gdpMainFramePreferredHeight the gdpMainFramePreferredHeight to set
	 */
	public final void setGdpMainFramePreferredHeight(int gdpMainFramePreferredHeight) {
		this.gdpMainFramePreferredHeight = gdpMainFramePreferredHeight;
	}
	
	
	/**
	 * @return the gdpMainFrameMinWidth
	 */
	public final int getGdpMainFrameMinWidth() {
		return gdpMainFrameMinWidth;
	}
	
	
	/**
	 * @param gdpMainFrameMinWidth the gdpMainFrameMinWidth to set
	 */
	public final void setGdpMainFrameMinWidth(int gdpMainFrameMinWidth) {
		this.gdpMainFrameMinWidth = gdpMainFrameMinWidth;
	}
	
	
	/**
	 * @return the gdpMainFrameMinHeight
	 */
	public final int getGdpMainFrameMinHeight() {
		return gdpMainFrameMinHeight;
	}
	
	
	/**
	 * @param gdpMainFrameMinHeight the gdpMainFrameMinHeight to set
	 */
	public final void setGdpMainFrameMinHeight(int gdpMainFrameMinHeight) {
		this.gdpMainFrameMinHeight = gdpMainFrameMinHeight;
	}
	
	
	/**
	 * @return the gdpConfigPreferredWidth
	 */
	public final int getGdpConfigPreferredWidth() {
		return gdpConfigPreferredWidth;
	}
	
	
	/**
	 * @param gdpConfigPreferredWidth the gdpConfigPreferredWidth to set
	 */
	public final void setGdpConfigPreferredWidth(int gdpConfigPreferredWidth) {
		this.gdpConfigPreferredWidth = gdpConfigPreferredWidth;
	}
	
	
	/**
	 * @return the gdpConfigPreferredHeight
	 */
	public final int getGdpConfigPreferredHeight() {
		return gdpConfigPreferredHeight;
	}
	
	
	/**
	 * @param gdpConfigPreferredHeight the gdpConfigPreferredHeight to set
	 */
	public final void setGdpConfigPreferredHeight(int gdpConfigPreferredHeight) {
		this.gdpConfigPreferredHeight = gdpConfigPreferredHeight;
	}
	
	
	/**
	 * @param gdpConfigMinimumSplitWidth the gdpConfigMinimumSplitWidth to set
	 */
	public void setGdpConfigMinimumSplitWidth(int gdpConfigMinimumSplitWidth) {
		this.gdpConfigMinimumSplitWidth = gdpConfigMinimumSplitWidth;
	}


	/**
	 * @return the gdpConfigMinimumSplitWidth
	 */
	public int getGdpConfigMinimumSplitWidth() {
		return gdpConfigMinimumSplitWidth;
	}


	/**
	 * @return the gdpCurveContainerPreferredwidth
	 */
	public final int getGdpCurveContainerPreferredWidth() {
		return gdpCurveContainerPreferredWidth;
	}
	
	
	/**
	 * @param gdpCurveContainerPreferredwidth the gdpCurveContainerPreferredwidth to set
	 */
	public final void setGdpCurveContainerPreferredWidth(
			int gdpCurveContainerPreferredwidth) {
		this.gdpCurveContainerPreferredWidth = gdpCurveContainerPreferredwidth;
	}
	
	
	/**
	 * @return the gdpCurveContainerPreferredHeight
	 */
	public final int getGdpCurveContainerPreferredHeight() {
		return gdpCurveContainerPreferredHeight;
	}
	
	
	/**
	 * @param gdpCurveContainerPreferredHeight the gdpCurveContainerPreferredHeight to set
	 */
	public final void setGdpCurveContainerPreferredHeight(
			int gdpCurveContainerPreferredHeight) {
		this.gdpCurveContainerPreferredHeight = gdpCurveContainerPreferredHeight;
	}
	
	
	/**
	 * @return the gdpCurveContainerminWidth
	 */
	public final int getGdpCurveContainerMinWidth() {
		return gdpCurveContainerMinWidth;
	}
	
	
	/**
	 * @param gdpCurveContainerminWidth the gdpCurveContainerminWidth to set
	 */
	public final void setGdpCurveContainerMinWidth(int gdpCurveContainerminWidth) {
		this.gdpCurveContainerMinWidth = gdpCurveContainerminWidth;
	}
	
	
	/**
	 * @return the gdpCurveContainerminHeght
	 */
	public final int getGdpCurveContainerMinHeight() {
		return gdpCurveContainerMinHeight;
	}
	
	
	/**
	 * @param gdpCurveContainerminHeght the gdpCurveContainerminHeght to set
	 */
	public final void setGdpCurveContainerMinHeight(int gdpCurveContainerminHeght) {
		this.gdpCurveContainerMinHeight = gdpCurveContainerminHeght;
	}
	
	
	/**
	 * @return the gdpCurveGraphicStrokeWidth
	 */
	public final float getGdpCurveGraphicStrokeWidth() {
		return gdpCurveGraphicStrokeWidth;
	}
	
	
	/**
	 * @param gdpCurveGraphicStrokeWidth the gdpCurveGraphicStrokeWidth to set
	 */
	public final void setGdpCurveGraphicStrokeWidth(float gdpCurveGraphicStrokeWidth) {
		this.gdpCurveGraphicStrokeWidth = gdpCurveGraphicStrokeWidth;
	}
	
	
	/**
	 * @return the gdpCurveGraphicVerticalLinesCount
	 */
	public final int getGdpCurveGraphicVerticalLinesCount() {
		return gdpCurveGraphicVerticalLinesCount;
	}
	
	
	/**
	 * @param gdpCurveGraphicVerticalLinesCount the gdpCurveGraphicVerticalLinesCount to set
	 */
	public final void setGdpCurveGraphicVerticalLinesCount(
			int gdpCurveGraphicVerticalLinesCount) {
		this.gdpCurveGraphicVerticalLinesCount = gdpCurveGraphicVerticalLinesCount;
	}
	
	
	/**
	 * @param gdpCurveGraphicHorizontalLinesCount the gdpCurveGraphicHorizontalLinesCount to set
	 */
	public void setGdpCurveGraphicHorizontalLinesCount(
			int gdpCurveGraphicHorizontalLinesCount) {
		this.gdpCurveGraphicHorizontalLinesCount = gdpCurveGraphicHorizontalLinesCount;
	}


	/**
	 * @return the gdpCurveGraphicHorizontalLinesCount
	 */
	public int getGdpCurveGraphicHorizontalLinesCount() {
		return gdpCurveGraphicHorizontalLinesCount;
	}


	/**
	 * @param gdpCurveGraphicGraphicsType the gdpCurveGraphicGraphicsType to set
	 */
	public void setGdpCurveGraphicGraphicsType(
			GraphicsType gdpCurveGraphicGraphicsType) {
		this.gdpCurveGraphicGraphicsType = gdpCurveGraphicGraphicsType;
	}


	/**
	 * @return the gdpCurveGraphicGraphicsType
	 */
	public GraphicsType getGdpCurveGraphicGraphicsType() {
		return gdpCurveGraphicGraphicsType;
	}


	/**
	 * @return the gdpGeneContainerPreferredHeight
	 */
	public final int getGdpGeneContainerPreferredHeight() {
		return gdpGeneContainerPreferredHeight;
	}
	
	
	/**
	 * @param gdpGeneContainerPreferredHeight the gdpGeneContainerPreferredHeight to set
	 */
	public final void setGdpGeneContainerPreferredHeight(
			int gdpGeneContainerPreferredHeight) {
		this.gdpGeneContainerPreferredHeight = gdpGeneContainerPreferredHeight;
	}
	
	
	/**
	 * @return the gdpGenesGraphicsStrokeWidth
	 */
	public final float getGdpGenesGraphicsStrokeWidth() {
		return gdpGenesGraphicsStrokeWidth;
	}
	
	
	/**
	 * @param gdpGenesGraphicsStrokeWidth the gdpGenesGraphicsStrokeWidth to set
	 */
	public final void setGdpGenesGraphicsStrokeWidth(
			float gdpGenesGraphicsStrokeWidth) {
		this.gdpGenesGraphicsStrokeWidth = gdpGenesGraphicsStrokeWidth;
	}
	
	/**
	 * @return the gdpGenesGraphicsVerticalLinesCount
	 */
	public final int getGdpGenesGraphicsVerticalLinesCount() {
		return gdpGenesGraphicsVerticalLinesCount;
	}
	
	
	/**
	 * @param gdpGenesGraphicsVerticalLinesCount the gdpGenesGraphicsVerticalLinesCount to set
	 */
	public final void setGdpGenesGraphicsVerticalLinesCount(
			int gdpGenesGraphicsVerticalLinesCount) {
		this.gdpGenesGraphicsVerticalLinesCount = gdpGenesGraphicsVerticalLinesCount;
	}
	
	
	/**
	 * @return the gdpTrackConfigDefaultWidth
	 */
	public final int getGdpTrackConfigDefaultWidth() {
		return gdpTrackConfigDefaultWidth;
	}
	
	
	/**
	 * @param gdpTrackConfigDefaultWidth the gdpTrackConfigDefaultWidth to set
	 */
	public final void setGdpTrackConfigDefaultWidth(int gdpTrackConfigDefaultWidth) {
		this.gdpTrackConfigDefaultWidth = gdpTrackConfigDefaultWidth;
	}
	
	
	/**
	 * @return the gdpTrackConfigDefaultHeight
	 */
	public final int getGdpTrackConfigDefaultHeight() {
		return gdpTrackConfigDefaultHeight;
	}
	
	
	/**
	 * @param gdpTrackConfigDefaultHeight the gdpTrackConfigDefaultHeight to set
	 */
	public final void setGdpTrackConfigDefaultHeight(int gdpTrackConfigDefaultHeight) {
		this.gdpTrackConfigDefaultHeight = gdpTrackConfigDefaultHeight;
	}
	
	
	/**
	 * @return the gdpTrackConfigMaxPreferredHeight
	 */
	public final int getGdpTrackConfigMaxPreferredHeight() {
		return gdpTrackConfigMaxPreferredHeight;
	}
	
	
	/**
	 * @param gdpTrackConfigMaxPreferredHeight the gdpTrackConfigMaxPreferredHeight to set
	 */
	public final void setGdpTrackConfigMaxPreferredHeight(
			int gdpTrackConfigMaxPreferredHeight) {
		this.gdpTrackConfigMaxPreferredHeight = gdpTrackConfigMaxPreferredHeight;
	}
	
	
	/**
	 * @return the gdpMainPanelDefaultMin
	 */
	public final int getGdpMainPanelDefaultMin() {
		return gdpMainPanelDefaultMin;
	}
	
	
	/**
	 * @param gdpMainPanelDefaultMin the gdpMainPanelDefaultMin to set
	 */
	public final void setGdpMainPanelDefaultMin(int gdpMainPanelDefaultMin) {
		this.gdpMainPanelDefaultMin = gdpMainPanelDefaultMin;
	}
	
	
	/**
	 * @return the gdpMainPanelDefaultMax
	 */
	public final int getGdpMainPanelDefaultMax() {
		return gdpMainPanelDefaultMax;
	}
	
	
	/**
	 * @param gdpMainPanelDefaultMax the gdpMainPanelDefaultMax to set
	 */
	public final void setGdpMainPanelDefaultMax(int gdpMainPanelDefaultMax) {
		this.gdpMainPanelDefaultMax = gdpMainPanelDefaultMax;
	}
	
	
	/**
	 * @return the gdpMainPanelDefaultChromo
	 */
	public final short getGdpMainPanelDefaultChromo() {
		return gdpMainPanelDefaultChromo;
	}
	
	
	/**
	 * @param gdpMainPanelDefaultChromo the gdpMainPanelDefaultChromo to set
	 */
	public final void setGdpMainPanelDefaultChromo(short gdpMainPanelDefaultChromo) {
		this.gdpMainPanelDefaultChromo = gdpMainPanelDefaultChromo;
	}
	
	
	/**
	 * @return the gdpMainPanelNbXScrollIncrement
	 */
	public final int getGdpMainPanelNbXScrollIncrement() {
		return gdpMainPanelNbXScrollIncrement;
	}
	
	
	/**
	 * @param gdpMainPanelNbXScrollIncrement the gdpMainPanelNbXScrollIncrement to set
	 */
	public final void setGdpMainPanelNbXScrollIncrement(
			int gdpMainPanelNbXScrollIncrement) {
		this.gdpMainPanelNbXScrollIncrement = gdpMainPanelNbXScrollIncrement;
	}
	
	
	/**
	 * @return the gdpMainPanelMinZoom
	 */
	public final int getGdpMainPanelMinZoom() {
		return gdpMainPanelMinZoom;
	}
	
	
	/**
	 * @param gdpMainPanelMinZoom the gdpMainPanelMinZoom to set
	 */
	public final void setGdpMainPanelMinZoom(int gdpMainPanelMinZoom) {
		this.gdpMainPanelMinZoom = gdpMainPanelMinZoom;
	}
	

	public static void main(String[] arg) {

		try {
			new ConfigurationManager().writeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
