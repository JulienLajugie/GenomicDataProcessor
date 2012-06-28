/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

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

import gdpcore.ConfigurationManager;
import gdpcore.Repeat;
import gdpcore.RepeatFamily;
import gdpcore.RepeatFileExtractor;

/**
 * Track showing the repeats loaded from a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpRepeatTrack extends GdpTrack {

	private static final long 	serialVersionUID = -7690975134349820158L; // Generated serial number
	private static final short 	REPEAT_HEIGHT = 6;				// Height of a repeat in pixel
	private static final short 	SPACE_HEIGHT = 3;				// Height of the space between two families of repeats
	private static final String FONT_NAME = "ARIAL";			// Font used for the name of the repeat
	private static final int	FONT_SIZE = 9;					// Size of the font
	private static final int	VERTICAL_LINES_COUNT = 10;		// Number of vertical lines
	private static final int 	STROKE_WIDTH = 1;				// Width of the stroke for the drawing
	private RepeatFileExtractor repeatList;						// List of repeats
	private int 				firstLineToDisplay = 0;			// Number of the first line to be displayed
	private int 				repeatLinesCount = 0;			// Number of lines of repeats
	private int 				mouseStartDragY = -1;			// Position of the mouse when start dragging
	
	/**
	 * Constructor, creates a GdpRepeatTrack.
	 * @param rfe {@link RepeatFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param chromo currently displayed chromosome
	 * @param minX currently displayed start position
	 * @param maxX currently displayed stop position
	 */
	public GdpRepeatTrack(RepeatFileExtractor rfe, ConfigurationManager cm, short chromo, int minX, int maxX) {
		super(chromo, minX, maxX, VERTICAL_LINES_COUNT);
		repeatList = rfe;
	}


	/**
	 * Draws the repeats and the vertical lines
	 * @param g Graphics
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (repeatList != null) {
			setBackground(Color.WHITE);
			Graphics2D g2D = (Graphics2D)g;
			g2D.setStroke(new BasicStroke(STROKE_WIDTH,	BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawVerticalLines(g2D, null);
			drawRepeat(g2D);
			drawVerticalLineCentered(g2D);
			drawBorder(g2D);
		}
	}


	/**
	 * Draws the repeats.
	 * @param g2D
	 */
	private void drawRepeat(Graphics2D g2D) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();
		short currentChromosome = displayedXWindow.getChromosome();
		int currentHeight = SPACE_HEIGHT * 2;
		int width = getWidth();
		ArrayList<RepeatFamily> repeatFamilyList = repeatList.get(currentChromosome, currentMinX, currentMaxX, xFactor);
		// Set the font		
		g2D.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
		FontMetrics fm = g2D.getFontMetrics();
		// Calculate how many lines are displayable
		int displayedLineCount = (getHeight() - SPACE_HEIGHT) / (REPEAT_HEIGHT + 2 * SPACE_HEIGHT) + 1;
		// Calculate how many scroll on the Y axis are necessary to show all the genes
		int yScrollCount = repeatFamilyList.size() - displayedLineCount + 1;
		if (yScrollCount < 0) {
			yScrollCount = 0;
		}
		if (yScrollCount != repeatLinesCount) {
			firePropertyChange("Y scroll count changed", repeatLinesCount, yScrollCount);
			repeatLinesCount = yScrollCount;
		}
		int currentColor = firstLineToDisplay;		
		// Loop for each line of the track
		for (int i = 0; i < displayedLineCount; i++) { //(RepeatFamily currentFamily : repeatFamilyList) {
			if (i + firstLineToDisplay < repeatFamilyList.size()) {
				// Retrieve the repeat associated to the current line to draw
				RepeatFamily currentFamily = repeatFamilyList.get(i + firstLineToDisplay);
				if ((currentChromosome < currentFamily.size() && currentFamily.get(currentChromosome).size() > 0)) {
					// Calculate if the background is white or gray
					if (currentColor % 2 == 1) {
						g2D.setColor(Color.LIGHT_GRAY);
						g2D.fillRect(0, currentHeight, width, REPEAT_HEIGHT + 2 * SPACE_HEIGHT);
					}
					// Calculate the color of the line
					g2D.setColor(intToColor(currentColor));
					currentHeight += SPACE_HEIGHT;
					// Loop for each repeat of the current family
					for(Repeat currentRepeat : currentFamily.get(currentChromosome)) {
						if (currentRepeat != null) {
							int x = genomePosToScreenPos(currentRepeat.getStart());
							int repeatWidth = genomePosToScreenPos(currentRepeat.getStop()) - x;
							if (repeatWidth < 1) {
								repeatWidth = 1;
							}
							g2D.fillRect(x, currentHeight, repeatWidth, REPEAT_HEIGHT);
						}
					}
					// Calculate the witdh of the text of the repeat name
					int textWidth = fm.stringWidth(currentFamily.getName());
					// draw a rectangle under the text with the color of the background
					if (currentColor % 2 == 1) {
						g2D.setColor(Color.LIGHT_GRAY);
					} else {
						g2D.setColor(Color.WHITE);
					}
					g2D.fillRect(0, currentHeight, textWidth + 2, REPEAT_HEIGHT);
					currentHeight += REPEAT_HEIGHT;
					// Write the repeat name
					g2D.setColor(intToColor(currentColor));
					g2D.drawString(currentFamily.getName(), 2, currentHeight);
					currentHeight += SPACE_HEIGHT;				
					currentColor++;
				}
			}
		}		
		g2D.setFont(null);		
	}	


	/**
	 * Associates a Color to an int. 
	 * @param i int
	 * @return a Color
	 */
	private Color intToColor(int i) {
		Color[] colorArray = {Color.BLACK, Color.GREEN, Color.BLUE, Color.PINK, Color.RED, Color.CYAN, Color.MAGENTA, Color.ORANGE};
		i = i % colorArray.length;
		return colorArray[i];
	}


	/**
	 * @return The number of lines of Repeats
	 */
	public int getRepeatLinesCount() {
		return repeatLinesCount;
	}


	/**
	 * The number of the first line to display
	 * @param newValue Number of the first line
	 */
	public void setFirstLineToDisplay(int newValue) {
		firstLineToDisplay = newValue;
		repaint();
	}
	

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if ((e.getWheelRotation() + firstLineToDisplay >= 0) || (e.getWheelRotation() + firstLineToDisplay <= repeatLinesCount)) {
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
	 * Changes the scroll bar position of the panel when mouse dragged with the right button.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {

			int distance = (mouseStartDragY - e.getY()) / (REPEAT_HEIGHT + 2 * SPACE_HEIGHT);
			if (Math.abs(distance) > 0) {
				firePropertyChange("first line to display changed", firstLineToDisplay, firstLineToDisplay + distance);
				mouseStartDragY = e.getY();
			}
		}		
	}
}
