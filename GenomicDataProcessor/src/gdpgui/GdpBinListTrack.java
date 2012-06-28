/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BinList;
import gdpcore.BinListDifferentWindowSizeException;
import gdpcore.ConfigurationManager;
import gdpcore.History;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


/**
 * A BinList track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpBinListTrack extends GdpTrack {

	public enum GraphicsType {
		curve,
		points,
		bar,
		dense;
	};

	private static final long 	serialVersionUID = -5653077382638122719L;	// Generated serial number
	private GraphicsType 		typeOfGraph;					// Type graphics
	private float			 	strokeWidth;					// Stroke size of the graphics
	private static final int	HORIZONTAL_LINES_COUNT = 10;	// Number of Y lines displayed 
	private static final Color	DEFAULT_COLOR = Color.black;	// Default color
	private BinList 			initialBinList;					// Value of the BinList when the track is created
	private BinList 			binList;						// Value of the displayed BinList
	private BinList				undoBinList = null;				// BinList used to restore when undo
	private BinList				redoBinList = null;				// BinList used to restore when redo
	private double 				minY;							// Minimum intensity
	private double 				maxY;							// Maximum intensity	
	private double 				currentMinY;					// Minimum displayed intensity
	private double 				currentMaxY;					// Maximum displayed intensity
	private Double 				lineY = null;					// Value of the Y line
	private Integer 			lineX = null;					// Gap between two X lines
	private double				yFactor;						// Factor between the displayed intensity range and the screen height
	private boolean				showHorizontalGrid = false;		// Shows horizontal grid if true 
	private double[] 			data = null;					// Data to display
	private Integer 			windowData = null;				// Size of the window of the data to display
	private Short 				chromoData = null;				// Chromosome to display
	private History				history = null;					// History containing a description of the actions done
	private boolean 			retrieveData = false;			// True if the data needs to be retrived
	private Color				trackColor = DEFAULT_COLOR;		// Color of the graphics

	/**
	 * Constructor.
	 * @param aBinList	BinList containing the data to display.
	 * @param chromo Chromosome to display.
	 * @param minX Minimum position to display.
	 * @param maxX Maximum position to display.
	 * @param minY Minimum intensity to display.
	 * @param maxY Maximum intensity to display.
	 */
	public GdpBinListTrack(ConfigurationManager cm, BinList aBinList, short chromo, int minX, int maxX, double minY, double maxY) {
		super(chromo, minX, maxX, cm.getGdpCurveGraphicVerticalLinesCount());
		initialBinList = aBinList;
		binList = initialBinList.clone();
		this.minY = minY;
		this.maxY = maxY;
		currentMinY = minY;
		currentMaxY = maxY;
		strokeWidth = cm.getGdpCurveGraphicStrokeWidth();
		typeOfGraph = cm.getGdpCurveGraphicGraphicsType();
		history = new History();
		history.add("load track");
	}


	/**
	 * Draws the graphics.
	 * @param g Graphics
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		yFactor = getHeight() / (currentMaxY - currentMinY);
		setBackground(Color.WHITE);
		Graphics2D g2D = (Graphics2D)g;		
		g2D.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
		drawHorizontalLines(g2D);
		drawVerticalLines(g2D, lineX);
		// We check if the displayed data changed 
		checkData();
		switch(typeOfGraph) {
		case bar:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawBarGraphics(g2D);
			break;
		case curve:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawCurveGraphics(g2D);
			break;
		case points:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawPointGraphics(g2D);
			break;
		case dense:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawDenseGraphics(g2D);
			break;
		}
		drawCoordinate(g2D);
		drawOneHorizontalLine(g2D);
		drawVerticalLineCentered(g2D);
		drawBorder(g2D);
	}


	/**
	 * Check if the data to display changed.
	 * Change the size of window if there is more data than pixels(width). 
	 */
	private void checkData() {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		short currentChromosome = displayedXWindow.getChromosome();
		int windowSize = binList.getWindowSize();
		int newWindowSize = windowSize;
		int width = getWidth();
		//boolean retrieveData = false;

		if (windowData == null) {
			windowData = windowSize;
			retrieveData = true;
		}
		if ((chromoData == null) || (chromoData != currentChromosome)) {
			chromoData = currentChromosome;
			retrieveData = true;
		}
		if (data == null) {
			retrieveData = true;
		}		
		// We calculate how many point we have to draw
		double pointCount = (currentMaxX - currentMinX) / windowSize;
		// We draw half when it's a curve graph because it takes two pixels to draw one line
		if (typeOfGraph == GraphicsType.curve) {
			newWindowSize = ((int)(2 * pointCount / width) + 1) * windowSize;
		} else {
			newWindowSize = ((int)(pointCount / width) + 1) * windowSize;
		}
		
		if (newWindowSize != windowData) {
			retrieveData = true;
			windowData = newWindowSize;
		}
		if (retrieveData) {
			data = binList.getData(chromoData, newWindowSize / windowSize);
		}
		retrieveData = false;
	}
	
	
	/**
	 * Draws a bar graphics.
	 * @param g2D
	 */
	private void drawBarGraphics(Graphics2D g2D) {
		if (data != null) {
			// Compute the reverse color
			Color reverseCurveColor = Color.gray;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}
			int currentMinX = displayedXWindow.getStart();
			int currentMaxX = displayedXWindow.getStop();
			// Compute the Y = 0 position 
			int screenY0 = intensityToScreenPos(0);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					int screenXWidth = twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
					int screenYPosition = intensityToScreenPos(currentIntensity);
					int rectHeight = screenYPosition - screenY0;
					if (currentIntensity > 0) {
						g2D.setColor(trackColor);
						g2D.fillRect(screenXPosition, screenYPosition, screenXWidth, -rectHeight);
					} else {
						g2D.setColor(reverseCurveColor);
						g2D.fillRect(screenXPosition, screenY0, screenXWidth, rectHeight);

					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}
	}


	/**
	 * Draws a point graphics.
	 * @param g2D
	 */
	private void drawPointGraphics(Graphics2D g2D) {
		if (data != null) {
			int currentMinX = displayedXWindow.getStart();
			int currentMaxX = displayedXWindow.getStop();
			g2D.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX2Position = genomePosToScreenPos(currentGenomePosition + windowData);
					int screenYPosition = intensityToScreenPos(currentIntensity);				
					g2D.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}	
		}
	}



	/**
	 * Draws the curve from BinList data.
	 * @param g2D Graphics2D
	 */
	private void drawCurveGraphics(Graphics2D g2D) {
		if (data != null) {
			int currentMinX = displayedXWindow.getStart();
			int currentMaxX = displayedXWindow.getStop();
			g2D.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				int nextIndex = (currentGenomePosition + windowData) / windowData;
				if ((currentGenomePosition >= 0) && (nextIndex < data.length)){
					double currentIntensity = data[currentIndex];
					double nextIntensity = data[nextIndex];
					int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX2Position = genomePosToScreenPos(currentGenomePosition + windowData);
					int screenY1Position = intensityToScreenPos(currentIntensity);
					int screenY2Position = intensityToScreenPos(nextIntensity);
					if ((currentIntensity == 0) && (nextIntensity != 0)) {
						g2D.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity == 0)) {
						g2D.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY1Position);
						g2D.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);					
					} else if ((currentIntensity != 0) && (nextIntensity != 0)) {
						g2D.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY2Position);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}
		}
	}	

	/**
	 * Draws a dense graphics.
	 * @param g2D Graphics2D
	 */
	private void drawDenseGraphics(Graphics2D g2D) {
		if (data != null) {
			int currentMinX = displayedXWindow.getStart();
			int currentMaxX = displayedXWindow.getStop();
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					int screenXWidth = twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
					g2D.setColor(intensityToColor(currentIntensity, minY, maxY));
					g2D.fillRect(screenXPosition, 0, screenXWidth, getHeight());
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}		
	}
	
	
	/**
	 * Draws a horizontal line where intensity equals <i>lineY</i> if <i>lineY</i> is not null.
	 * @param g2D Graphics2D
	 */
	private void drawOneHorizontalLine(Graphics2D g2D) {
		// If lineY is not null we draw a horizontal line	
		if(lineY != null) {
			g2D.setColor(Color.BLUE);
			int screenLineY = intensityToScreenPos(lineY);
			if((screenLineY > 0) && (screenLineY < getHeight())) {
				g2D.drawLine(0, screenLineY, getWidth(), screenLineY);
				DecimalFormat formatter = new DecimalFormat("#.#");
				formatter.setRoundingMode(RoundingMode.DOWN);
				String positionStr = formatter.format(lineY);
				g2D.drawString(positionStr, 2, screenLineY);
			}
		}
	}


	/**
	 * Draws <i>HORIZONTAL_LINES_COUNT</i> horizontal lines.
	 * @param g2D
	 */
	private void drawHorizontalLines(Graphics2D g2D) {
		// We draw the default number of horizontal lines
		if (showHorizontalGrid) {
			g2D.setColor(Color.LIGHT_GRAY);
			double intensityGapBetweenLineY = (currentMaxY - currentMinY) / (double)HORIZONTAL_LINES_COUNT;
			int lineYCount = HORIZONTAL_LINES_COUNT;
			// position of the first xline
			double intensityFirstLineY = currentMinY - (currentMinY % intensityGapBetweenLineY);
			for(int i = 0; i <= lineYCount; i++) {
				double intensityLineY = i * intensityGapBetweenLineY + intensityFirstLineY;
				int screenLineY = intensityToScreenPos(intensityLineY);
				if((screenLineY > 0) && (screenLineY < getHeight())) {
					g2D.drawLine(0, screenLineY, getWidth(), screenLineY);
					DecimalFormat formatter = new DecimalFormat("#.#");
					formatter.setRoundingMode(RoundingMode.DOWN);
					String positionStr = formatter.format(intensityLineY);
					g2D.drawString(positionStr, 2, screenLineY);
				}
			}
		}
	}


	/**
	 * Draws a vertical line in the middle of the screen and print 
	 * the coordinate of the point crossing this line.  
	 * @param g2D
	 */
	private void drawCoordinate(Graphics2D g2D) {
		int currentChromosome = displayedXWindow.getChromosome();
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();

		g2D.setColor(Color.RED);
		Integer Xmid = (currentMaxX + currentMinX) / 2;
		Double Ymid = new Double(0);
		if ((binList.getData()[currentChromosome] != null) && ((Xmid / binList.getWindowSize()) < binList.getData()[currentChromosome].length)){ 
			Ymid = binList.getData()[currentChromosome][Xmid / binList.getWindowSize()];
		}
		DecimalFormat dc = new DecimalFormat("###,###,###");
		g2D.drawString("x=" + dc.format(Xmid), 2, getHeight() - 24);
		g2D.drawString("y=" + Ymid.toString(), 2, getHeight() - 12);
	}





	/**
	 * Shows or hides the horizontal grid. 
	 * @param b True to show, false to hide.
	 */
	public void setShowHorizontalGrid(boolean b) {
		showHorizontalGrid = b;
	}


	/**
	 * @return True if the grid is showed, false otherwise.
	 */
	public Boolean getShowHorizontalGrid() {
		return showHorizontalGrid;
	}


	/**
	 * @param intensity An intensity
	 * @return A Y position on the screen.
	 */
	private int intensityToScreenPos(double intensity) {
		if (intensity < currentMinY) {
			return getHeight();
		} else if (intensity > currentMaxY) {
			return 0;
		} else {
			return (int)((getHeight() - (double)((intensity - currentMinY) * yFactor)));
		}
	}


	/**
	 * Changes the minimum intensity of the track. 
	 * @param min New minimum intensity value.
	 */
	public void setMinY(double min) {
		currentMinY = min;
		repaint();
	}


	/**
	 * Changes the maximum intensity of the track.
	 * @param max New maximum intensity value.
	 */
	public void setMaxY(double max) {
		currentMaxY = max;
		repaint();
	}	


	/**
	 * Changes the current minimum and maximum intensity of the track.
	 * @param min New minimum intensity.
	 * @param max New maximum intensity.
	 */
	public void setMinMaxY(double min, double max) {
		currentMinY = min;
		currentMaxY = max;
		repaint();
	}


	/**
	 * Returns the BinList of the track.
	 * @return A binList.
	 */
	public BinList getBinList() {
		return binList;
	}


	/**
	 * Sets a BinList that will be used as input data for the track.   
	 * @param aBinList A BinList.
	 */
	public void setBinList(BinList aBinList) {
		binList = aBinList;
	}


	/**
	 * Resets the BinList. Copies the value of the original BinList into the current value. 
	 */
	public void resetBinList() {
	try {
		undoBinList = binList;
		redoBinList = null;
		binList = initialBinList.clone();
		minY = binList.min();
		maxY = binList.max();
		retrieveData = true;
		repaint();
		history.reset();
		history.add("Reset track");
	}
	catch (Exception e) {
		handleException(e, "Error while reseting");
		history.setLastAsError();
		}
	}


	/**
	 * Changes the gap between two consecutive X lines.
	 * @param value New gap between two X lines in bases.
	 */
	public void setXLine(Integer value) {
		lineX = value;
		repaint();		
	}


	/**
	 * Changes the position of a Y line.
	 * @param value New value for the Y line.
	 */
	public void setYLine(Double value) {
		lineY = value;
		repaint();		
	}


	/**
	 * @return the gap between two consecutive X lines.
	 */
	public Integer getLineX() {
		return lineX;
	}


	/**
	 * @return the position of a Y line.
	 */
	public Double getLineY() {
		return lineY;
	}


	/**
	 * Gausses the BinList.
	 * @param sigma Value of the parameter sigma of the gaussian filter.
	 * @see BinList#gauss(int)
	 */
	public void gauss(int sigma) {
		try {
			history.add("Gauss(sigma = " + sigma + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.gauss(sigma);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		}
		catch (Exception e) {
			handleException(e, "Error while gaussing");
			history.setLastAsError();
		}
	}


	/**
	 * Indexes the BinList.
	 * @param saturation Value of the saturation.
	 * @param indexMin Value of the bottom index.
	 * @param indexMax Value of the top index.
	 * @see BinList#index(double, double, double)
	 */
	public void index(double saturation, double indexMin, double indexMax) {
		try {
			history.add("Index(saturation = " + saturation + ", index min = " + indexMin + ", " + "index max = " + indexMax + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.index(saturation, indexMin, indexMax);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while indexing");
			history.setLastAsError();
		}
	}


	/**
	 * Indexes the BinList by chromosome.
	 * @param saturation Value of the saturation.
	 * @param indexMin Value of the bottom index.
	 * @param indexMax Value of the top index.
	 * @see BinList#indexByChromo(double, double, double)
	 */
	public void indexByChromo(double saturation, double indexMin, double indexMax) {
		try {
			history.add("Index by chromosome(saturation = " + saturation + ", index min = " + indexMin + ", " + "index max = " + indexMax + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.indexByChromo(saturation, indexMin, indexMax);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while indexing by chromosome");
			history.setLastAsError();
		}
	}


	/**
	 * Normalizes the BinList.
	 * @param factor Multiplication factor of the normalization.
	 * @see BinList#normalize(int)
	 */
	public void normalize(int factor) {
		try {
			history.add("normalize(factor = " + factor + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.normalize(factor);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while normalizing");
			history.setLastAsError();
		}
	}


	/**
	 * Logs the BinList with a damper and adds the average.
	 * @param damper Damper of the log.
	 * @see BinList#log(double)
	 */
	public void log(double damper) {
		try {
			history.add("log(damper = " + damper + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.log(damper);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while computing the log");
			history.setLastAsError();
		}
	}


	/**
	 * Logs the BinList.
	 * @see BinList#log()
	 */
	public void log() {
		try {
			history.add("log");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.log();
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while computing the log");
			history.setLastAsError();
		}
	}


	/**
	 * Adds a damper to the BinList.
	 * @param damper Value of the damper to add.
	 * @see BinList#addDumper(double)
	 */
	public void addDamper(double damper) {
		try {
			history.add("add damper(damper = " + damper + ")");
			undoBinList = binList;
			redoBinList = null;
			binList = binList.addDumper(damper);
			minY = binList.min();
			maxY = binList.max();
			retrieveData = true;
			repaint();
		} catch (Exception e) {
			handleException(e, "Error while adding a damper");
			history.setLastAsError();
		}		
	}


	/**
	 * Generates a Wiggle file.
	 * @param filePath Path of the output file.
	 * @param trackName Name of the track.
	 * @see BinList#printWiggleFile(String, String)
	 */
	public void printWiggle(String filePath, String trackName) {
		try {
			binList.printWiggleFile(filePath, trackName);
		} catch (Exception e) {
			handleException(e, "Error while generating the wiggle file");
		}
	}


	/**
	 * Generates a CSV file.
	 * @param filePath Path of the output file.
	 * @see BinList#printCSVFile(String)
	 */
	public void printCSV(String filePath) {
		try {
			binList.printCSVFile(filePath);
		} catch (Exception e) {
			handleException(e, "Error while generating the CSV file");
		}
	}


	/**
	 * Generate a file containing the repartition of the bins.
	 * @param intensityBin Bins of intensity. 
	 * @see BinList#repartition(double, String)
	 */
	public void repartition(double intensityBin, String filePath) {
		try {
			binList.repartition(intensityBin, filePath);
		} catch (Exception e) {
			handleException(e, "Error while generating the intensity file");
		}
	}


	/**
	 * Subtracts the current list by the list in parameter.
	 * @param aBinList BinList we are subtracting. 
	 * @return New BinList resulting from the subtraction.
	 * @see BinList#minus(BinList)
	 */
	public BinList minus(BinList aBinList) {
		try {
			return binList.minus(aBinList);
		} catch (BinListDifferentWindowSizeException e) {
			handleException(e, "Subtracting tracks with different window sizes is not allowed");
			return null;
		} catch (Exception e) {
			handleException(e, "Error while substracting the tracks");
			return null;		
		}		
	}


	/**
	 * Divides the current list by the list in parameter.
	 * @param aBinList A BinList. 
	 * @param filter Filter for the division.
	 * @param normalized True if the division need to be normalized.
	 * @return New BinList resulting from the division.
	 * @see BinList#divideBy(BinList, int, boolean)
	 */
	public BinList divideBy(BinList aBinList, int filter, boolean normalized) {
		try {
			return binList.divideBy(aBinList, filter, normalized);
		} catch (BinListDifferentWindowSizeException e) {
			handleException(e, "Dividing tracks with different window sizes is not allowed");
			return null;
		} catch (Exception e) {
			handleException(e, "Error while substracting the tracks");
			return null;		
		}	
	}


	/**
	 * Searches the peaks of a BinList. We consider a point as a peak when the 
	 * moving standard deviation = <i>nbSDAccepted</i> * global standard deviation.
	 * @param sizeMovingSD Width (in bp) of the moving standard deviation.
	 * @param nbSDAccepted  
	 * @return A BinList with only peaks.
	 */
	public BinList searchPeaks(BinList aBinList, int sizeMovingSD, double nbSDAccepted) {
		try {
			return binList.searchPeaks(sizeMovingSD, nbSDAccepted);
		} catch (Exception e) {
			handleException(e, "Error while searching the peaks");
			return null;		
		}	
	}


	/**
	 * Computes the coefficient of correlation between 
	 * the current BinList and another BinList. 
	 * Only the chromosomes set to true in chromoList will 
	 * be used in the calculation. 
	 * @param aBinList A BinList. 
	 * @param chromoList Set to true each chromosome of this list that you want to use in the calculation.
	 * @see BinList#correlation(BinList, boolean[])
	 */
	public void correlation(BinList aBinList, boolean[] chromoList) {
		try {
			double resCorrelation = binList.correlation(aBinList, chromoList);
			JOptionPane.showMessageDialog(getRootPane(), "Correlation coefficient: \n" + new DecimalFormat("0.000").format(resCorrelation), "Correlation", JOptionPane.INFORMATION_MESSAGE);
		} catch (BinListDifferentWindowSizeException e) {
			handleException(e, "Calculating the correlation between tracks with different window sizes is not allowed");
		} catch (Exception e) {
			handleException(e, "Error while substracting the tracks");
		}	
	}


	/**
	 * @return The minimum value of the BinList.
	 * @see BinList#min()
	 */
	public Double minBinList() {
		try {
			return binList.min();
		} catch (Exception e) {
			handleException(e, "Error while searching the minimum");
			return null;
		}
	}


	/**
	 * @return The maximum value of the BinList.
	 * @see BinList#max()
	 */
	public Double maxBinList() {
		try {
			return binList.max();
		} catch (Exception e) {
			handleException(e, "Error while searching the maximum");
			return null;
		}
	}


	/**
	 * Undoes last action. 
	 */
	public void undo() {
		if (undoBinList != null) {
			redoBinList = binList;
			binList = undoBinList;
			undoBinList = null;
			retrieveData = true;
			repaint();
			history.undo();
		}		
	}


	/**
	 * Re does last action.
	 */
	public void redo() {
		if (redoBinList != null) {
			undoBinList = binList;
			binList = redoBinList;
			redoBinList = null;
			retrieveData = true;
			repaint();
			history.redo();
		}		
	}


	/**
	 * @return True if the action undo is possible.
	 */
	public boolean isUndoable() {
		return (undoBinList != null);
	}


	/**
	 * @return True if the action redo is possible.
	 */
	public boolean isRedoable() {
		return (redoBinList != null);
	}


	/**
	 * Save the selected files as JPG images.
	 * @param addressFile Address of the ouput file.
	 */
	public void saveAsImage(String addressFile) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {         
			ImageIO.write(image, "JPEG", new File(addressFile));
		}catch(Exception e) {
			handleException(e, "Error while saving the tracks as an image");
		}

	}


	/**
	 * Changes the type of graphics displayed.
	 * @param gdpCurveGraphicGraphicsType Type of graphics.
	 */
	public void setGraphicsType(GraphicsType gdpCurveGraphicGraphicsType) {
		typeOfGraph = gdpCurveGraphicGraphicsType;
		repaint();
	}


	/**
	 * @return the history of the current track.
	 */
	public History getHistory() {
		return history;
	}


	/**
	 * @param trackColor the trackColor to set
	 */
	public void setTrackColor(Color trackColor) {
		this.trackColor = trackColor;
		repaint();
	}


	/**
	 * @return the trackColor
	 */
	public Color getTrackColor() {
		return trackColor;
	}


	/**
	 * @return the type of graphics
	 */
	public GraphicsType getGraphicsType() {
		return typeOfGraph;
	}

}
