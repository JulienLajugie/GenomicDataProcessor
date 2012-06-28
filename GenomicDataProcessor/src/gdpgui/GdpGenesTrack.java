/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;
import gdpcore.Gene;
import gdpcore.GeneFileExtractor;
import gdpcore.Strand;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Track showing the genes loaded from a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGenesTrack extends GdpTrack {

	private static final long 			serialVersionUID = -7921333252419394574L; 	// Generated serial number
	// TODO: add to the config manager the following static properties
	private static final int 			MIN_DISTANCE_BETWEEN_2_GENES = 5;	// Minimum distance in pixel between two genes 
	private static final int			MAX_ZOOM_PRINT_NAME = 5000000;	// We print the name of the genes if the zoom is below this value 
	private static final String 		FONT_NAME = "ARIAL";			// Font used for the name of the genes
	private static final int			FONT_SIZE = 9;					// Size of the font
	private static final short			GENE_HEIGHT = 6;				// Size of a gene in pixel

	private float 						strokeWidth;					// Stroke size of the graphics
	private GeneFileExtractor 			geneList;						// Genes extracted from the file 
	private ArrayList<ArrayList<Gene>> 	organizedGeneList;				// Genes organized to be printed
	private int 						currentZoom = -1;				// Current zoom
	private int 						currentWidth = -1;				// Current width
	private int 						firstLineToDisplay = 0;			// Number of the first line to be displayed
	private int 						geneLinesCount = 0;				// Number of lines of genes
	private int 						mouseStartDragY = -1;			// Position of the mouse when start dragging


	/**
	 * Constructor, creates a GdpGenesTrack.
	 * @param gfe {@link GeneFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param chromo Chromosome to display
	 * @param minX Minimum position to display
	 * @param maxX Maximum position to display
	 */
	public GdpGenesTrack(GeneFileExtractor gfe, ConfigurationManager cm, short chromo, int minX, int maxX) {
		super(chromo, minX, maxX, cm.getGdpGenesGraphicsVerticalLinesCount());
		strokeWidth = cm.getGdpGenesGraphicsStrokeWidth();
		geneList = gfe;
	}

	
	/**
	 * Draws the genes and the vertical lines
	 * @param g Graphics
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (geneList != null) {
			setBackground(Color.WHITE);
			Graphics2D g2D = (Graphics2D)g;
			g2D.setStroke(new BasicStroke(strokeWidth,	BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawVerticalLines(g2D, null);			
			drawGenes(g2D);
			drawVerticalLineCentered(g2D);
			drawBorder(g2D);
		}
	}


	/**
	 * Draws the genes.
	 * @param g2D Graphics2D
	 */
	private void drawGenes(Graphics2D g2D) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		// Compute the absolute start and stop position
		int startAbsolutePos = genomePosToAbsoluteScreenPos(currentMinX); 
		int stopAbsolutePos = genomePosToAbsoluteScreenPos(currentMaxX); 
		
		// Set the font for the name of the genes
		g2D.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
		
		// Check if we have to retrieve the data (ie if the zoom changed or if the window has been resized) 
		if ((currentZoom != currentMaxX - currentMinX) || (currentWidth != getWidth())) {
			createGraphicsGeneList(displayedXWindow.getChromosome());
			currentZoom = currentMaxX - currentMinX;
			currentWidth = getWidth();
		}

		// Retrieve the genes to print
		ArrayList<ArrayList<Gene>> genesToPrint = getGenesToPrint(startAbsolutePos, stopAbsolutePos);

		if (genesToPrint != null) {
			// Compute the maximum number of line displayable
			int displayedLineCount = 0;
			if (currentMaxX - currentMinX > MAX_ZOOM_PRINT_NAME) {
				displayedLineCount = (getHeight() - 3 * GENE_HEIGHT) / (GENE_HEIGHT * 2) + 1;
			} else {
				displayedLineCount = (getHeight() - 4 * GENE_HEIGHT) / (GENE_HEIGHT * 3) + 1;
			}
			// Calculate how many scroll on the Y axis are necessary to show all the genes
			int yScrollCount = genesToPrint.size() - displayedLineCount + 1;
			if (yScrollCount < 0) {
				yScrollCount = 0;
			}
			if (yScrollCount != geneLinesCount) {				
				firePropertyChange("Y scroll count changed", geneLinesCount, yScrollCount);
				geneLinesCount = yScrollCount;
			}

			// For each line of genes on the screen
			for (int i = 0; i < displayedLineCount; i++) {
				// Calculate the height of the gene
				int currentHeight;				
				if (currentMaxX - currentMinX > MAX_ZOOM_PRINT_NAME) {
					currentHeight = i * (GENE_HEIGHT * 2) + 3 * GENE_HEIGHT;
				} else {
					currentHeight = i * (GENE_HEIGHT * 3) + 4 * GENE_HEIGHT;
				}
				// Calculate which line has to be printed depending on the position of the scroll bar
				int currentLine = i + firstLineToDisplay;

				if (currentLine < genesToPrint.size()) {
					// For each gene of the current line
					for (Gene geneToPrint : genesToPrint.get(currentLine)) {
						// Choose the color depending on the strand
						if (geneToPrint.getStrand() == Strand.five) {
							g2D.setColor(Color.RED);
						} else {
							g2D.setColor(Color.BLUE);
						}
						// Draw the gene
						int x1 = geneToPrint.getTxStart() - startAbsolutePos;
						int x2 = geneToPrint.getTxStop() - startAbsolutePos;
						g2D.drawLine(x1, currentHeight, x2, currentHeight);
						// Draw the name of the gene if the zoom is small enough
						if (currentMaxX - currentMinX <= MAX_ZOOM_PRINT_NAME) {
							String geneName = geneToPrint.getName();
							g2D.drawString(geneName, x1 + 2, currentHeight - 1);
						}
						// For each exon of the current gene
						for (int j = 0; j < geneToPrint.getExonStarts().length; j++) {
							int exonX = geneToPrint.getExonStarts()[j] - startAbsolutePos;
							int exonWidth = geneToPrint.getExonStops()[j] - startAbsolutePos - exonX;
							if (exonWidth < 1){
								exonWidth = 1;
							}
							if (geneToPrint.getExonScores() != null) {
								g2D.setColor(intensityToColor(geneToPrint.getExonScores()[j], 0, 1000));
							}
							g2D.fillRect(exonX, currentHeight + 1, exonWidth, GENE_HEIGHT);
						}	
					}
				}
			}
		}
		g2D.setFont(null);
	}


	/**
	 * Creates a list of genes to print with the screen coordinates and a name with the strand in parentheses. 
	 * Calls the function that creates the gene list organized per line so two genes doesn't overlap.  
	 * @param chromosome Current chromosome.
	 */
	private void createGraphicsGeneList(short chromosome) {
		// Retrieve the raw gene list for the current chromosome
		ArrayList<Gene> graphicsGeneList = new ArrayList<Gene>();
		// For each gene of the current chromosome
		for (Gene currentGene : geneList.get(chromosome)) {
			// Name of the gene with the strand in parentheses
			String aName = currentGene.getName() + "(" + currentGene.getStrand() + ")";
			short aChromo = chromosome;
			Strand aStrand = currentGene.getStrand();
			// Genomic positions are converted in screen position 
			int aTxStart = genomePosToAbsoluteScreenPos(currentGene.getTxStart());
			int aTxStop = genomePosToAbsoluteScreenPos(currentGene.getTxStop());
			int[] arrayExonStarts = new int[currentGene.getExonStarts().length]; 
			// Genomic start positions of each exon are converted in screen position
			for (int i = 0; i < currentGene.getExonStarts().length; i++) {
				arrayExonStarts[i] = genomePosToAbsoluteScreenPos(currentGene.getExonStarts()[i]);
			}
			int[] arrayExonStops = new int[currentGene.getExonStops().length];
			// Genomic stop positions of each exon are converted in screen position
			for (int i = 0; i < currentGene.getExonStops().length; i++) {
				arrayExonStops[i] = genomePosToAbsoluteScreenPos(currentGene.getExonStops()[i]);
			}
			double[] arrayExonScores = currentGene.getExonScores();
			graphicsGeneList.add(new Gene(aName, aChromo, aStrand, aTxStart, aTxStop, arrayExonStarts, arrayExonStops, arrayExonScores));
		}
		// Calls the function that create the list organized per line.
		createOrganizedGeneList(graphicsGeneList);
	}


	/**
	 * Organizes a gene list on multiple lines so two displayed genes don't overlap.
	 * @param graphicsGeneList List to organize.
	 */
	private void createOrganizedGeneList(ArrayList<Gene> graphicsGeneList) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		// FontMetrics is used to know the width of a string
		FontMetrics fm = getGraphics().getFontMetrics();
		organizedGeneList = new ArrayList<ArrayList<Gene>>();
		// how many genes have been organized
		int organizedGeneCount = 0;
		// which genes have already been selected and organized
		boolean[] organizedGenes = new boolean[graphicsGeneList.size()];
		Arrays.fill(organizedGenes, false);
		int currentLine = 0;
		// loop until every gene has been organized
		while (organizedGeneCount < graphicsGeneList.size()) {
			organizedGeneList.add(new ArrayList<Gene>());
			// we loop on the gene list
			for (int i = 0; i < graphicsGeneList.size(); i++) {
				// if the current gene has not been organized yet
				if (!organizedGenes[i]) {
					// if the current line is empty we add the current gene
					if (organizedGeneList.get(currentLine).size() == 0) {
						organizedGeneList.get(currentLine).add(graphicsGeneList.get(i));
						organizedGenes[i] = true;
						organizedGeneCount++;
					} else {
						int currentStart = graphicsGeneList.get(i).getTxStart();
						int previousStop;
						// if we don't print the gene names the previous stop is the stop position of the gene + the minimum length between two genes 
						if (currentMaxX - currentMinX > MAX_ZOOM_PRINT_NAME) {
							previousStop = organizedGeneList.get(currentLine).get(organizedGeneList.get(currentLine).size() - 1).getTxStop() + MIN_DISTANCE_BETWEEN_2_GENES;
						} else { // if we print the name the previous stop is the max between the stop of the gene and the end position of the name of the gene (+ MIN_DISTANCE_BETWEEN_2_GENES in both case) 
							int previousNameStop = fm.stringWidth(organizedGeneList.get(currentLine).get(organizedGeneList.get(currentLine).size() - 1).getName()) + organizedGeneList.get(currentLine).get(organizedGeneList.get(currentLine).size() - 1).getTxStart();
							int previousGeneStop = organizedGeneList.get(currentLine).get(organizedGeneList.get(currentLine).size() - 1).getTxStop();
							previousStop = (previousNameStop > previousGeneStop) ? previousNameStop : previousGeneStop; 
							previousStop += MIN_DISTANCE_BETWEEN_2_GENES; 
						}
						// if the current gene won't overlap with the previous one we add it to the current line of the list of organized genes
						if (currentStart > previousStop) {
							organizedGeneList.get(currentLine).add(graphicsGeneList.get(i));
							organizedGenes[i] = true;
							organizedGeneCount++;							
						}						
					}
				}
			}			
			currentLine++;
		}
	}

	
	/**
	 * @param start Absolute start position on the screen 
	 * @param stop Absolute stop position on the screen
	 * @return Return the list of the genes to print between start and stop
	 */
	private ArrayList<ArrayList<Gene>> getGenesToPrint(int start, int stop) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		FontMetrics fm = getGraphics().getFontMetrics();
		ArrayList<ArrayList<Gene>> resultList = new ArrayList<ArrayList<Gene>>();
		// search genes for each line
		for (ArrayList<Gene> currentLine : organizedGeneList) { 
			// search the start
			int indexStart = findStart(currentLine, start, 0, currentLine.size() - 1);
			// search if the there is a previous stop (stop of the gene or stop of the name of the string)stopping after the start
			if (indexStart > 0) {
				int previousStop;
				if (currentMaxX - currentMinX > MAX_ZOOM_PRINT_NAME) {
					previousStop = currentLine.get(indexStart - 1).getTxStop();
				} else {
					int previousNameStop = fm.stringWidth(currentLine.get(indexStart - 1).getName()) + currentLine.get(indexStart - 1).getTxStart();
					int previousGeneStop = currentLine.get(indexStart - 1).getTxStop();
					previousStop = (previousNameStop > previousGeneStop) ? previousNameStop : previousGeneStop; 
				}
				if (previousStop > start) {
					indexStart = indexStart - 1;
				}
			}
			// search the stop
			int indexStop = findStop(currentLine, stop, 0, currentLine.size() - 1);
			if (currentLine.get(indexStart) != null) { 
				// add all the genes found for the current line between index start and index stop to the result list 
				resultList.add(new ArrayList<Gene>());
				for (int i = indexStart; i <= indexStop; i++) {
					resultList.get(resultList.size() - 1).add(currentLine.get(i));
				}
			}
		}
		return resultList;
	}

	
	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value. 
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int findStart(ArrayList<Gene> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getTxStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getTxStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}
	

	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position stop equals to value. 
	 * Index of the first gene with a stop position superior to value if nothing found.
	 */
	private int findStop(ArrayList<Gene> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getTxStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getTxStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}


	@Override
	public void setChromosome(short chromosome, int min, int max) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		// Call the parent setChromosome methode
		super.setChromosome(chromosome, min, max);
		// Retrieve the organized list of gene for the new chromosome
		createGraphicsGeneList(displayedXWindow.getChromosome());
		// Set the currentZoom and currentWidth properties
		currentZoom = currentMaxX - currentMinX;
		currentWidth = getWidth();
	}


	/**
	 * @return The number of lines of genes
	 */
	public int getGeneLinesCount() {
		return geneLinesCount;
	}


	/**
	 * The number of the first line to display
	 * @param newValue Number of the first line
	 */
	public void setFirstLineToDisplay(int newValue) {
		firstLineToDisplay = newValue;
		repaint();
	}


	/**
	 * Search a gene in the list of genes.
	 * @param name Name of the searched gene.
	 * @return A gene called <i>name</i>. Null if not found.
	 */
	public Gene searchGene(String name) {
		return geneList.search(name);
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if ((e.getWheelRotation() + firstLineToDisplay >= 0) || (e.getWheelRotation() + firstLineToDisplay <= geneLinesCount)) {
				firePropertyChange("first line to display changed", firstLineToDisplay, firstLineToDisplay + e.getWheelRotation());
			}		
		} else {
			super.mouseWheelMoved(e);
		}
	}
	
	/**
	 * Sets the variable mouseStartDragY when the user press the right button of the mouse.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			mouseStartDragY = e.getY();
		}		
	}
	
	/**
	 * Changes the scroll bar position of the genes when mouse dragged with the right button.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		
		super.mouseDragged(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			int distance = 0;
			if (currentMaxX - currentMinX > MAX_ZOOM_PRINT_NAME) {
				distance = (mouseStartDragY - e.getY()) / (2 * GENE_HEIGHT);
			} else {
				distance = (mouseStartDragY - e.getY()) / (3 * GENE_HEIGHT);
			}
			if (Math.abs(distance) > 0) {
				firePropertyChange("first line to display changed", firstLineToDisplay, firstLineToDisplay + distance);
				mouseStartDragY = e.getY();
			}
		}		
	}
}
