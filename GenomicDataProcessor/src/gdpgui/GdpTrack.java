/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.GenomicWindow;
import gdpcore.StripeFileExtractor;
import gdpcore.Stripe;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;



/**
 * The GdpTrack class contains the properties and method common to the different kind
 * of tracks used by Gdp.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GdpTrack extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long 	serialVersionUID = -2682583492009542729L;
	private int 				mouseStartDragX = -1;		// Position of the mouse when start dragging
	private boolean				isScrollMode = false;		// true when the middle button is in lock mode
	private int 				scrollModeIntensity = 0;	// Intensity of the scroll.
	private ScrollModeThread 	scrollModeThread; 			// Thread executed when the scroll mode is on
	private StripeFileExtractor sfe = null;					// Stripe file to display
	protected double			xFactor;					// Factor between the genomic width and the screen width
	protected int 				verticalLinesCount;			// Number of vertical lines
	protected GenomicWindow		displayedXWindow;			// The genomic window displayed by the track


	/**
	 * The ScrollModeThread class is used to scroll the track horizontally 
	 * when the scroll mode is on (ie when the middle button of the mouse is clicked)  
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class ScrollModeThread extends Thread {
		@Override
		public void run() {
			synchronized (this) {
				while (isScrollMode) {
					GenomicWindow newWindow = new GenomicWindow();
					newWindow.setChromosome(displayedXWindow.getChromosome());
					newWindow.setStart(displayedXWindow.getStart()- scrollModeIntensity);
					newWindow.setStop(displayedXWindow.getStop() - scrollModeIntensity);
					firePropertyChange("Position changed", displayedXWindow, newWindow);
					yield();
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	/**
	 * Constructor.
	 * @param chromo a chromosome
	 * @param minX a window start
	 * @param maxX a window stop
	 */
	protected GdpTrack(short chromo, int minX, int maxX, int verticalLinesCount) {
		super(true);
		displayedXWindow = new GenomicWindow(chromo, minX, maxX);
		this.verticalLinesCount = verticalLinesCount;
		scrollModeThread = new ScrollModeThread();
		addMouseListener(this);		
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}


	/**
	 * Sets the variable xFactor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		xFactor = (double)getWidth() / (double)(displayedXWindow.getStop() - displayedXWindow.getStart());
		drawStripes(g);
	}


	/**
	 * Changes the chromosome, the minimum and the maximum positions of the track.
	 * @param chromosome New chromosome.
	 * @param min	New minimum position.
	 * @param max New maximum position.
	 */
	public void setChromosome(short chromosome, int min, int max) {
		displayedXWindow.setChromosome(chromosome);
		displayedXWindow.setStart(min);
		displayedXWindow.setStop(max);
		repaint();
	}


	/**
	 * Changes the minimum position of the track.
	 * @param min New minimum position to display.
	 */
	public void setMinX(int min) {
		displayedXWindow.setStart(min);
		repaint();
	}


	/**
	 * Changes the maximum position of the track.
	 * @param max New maximum position to display.
	 */
	public void setMaxX(int max) {
		displayedXWindow.setStop(max);
		repaint();
	}


	/**
	 * Changes the current minimum and maximum position of the track.
	 * @param min New minimum position.
	 * @param max New maximum position.
	 */
	public void setMinMaxX(int min, int max) {
		displayedXWindow.setStart(min);
		displayedXWindow.setStop(max);
		repaint();
	}


	/**
	 * @param genomicPosition A position on the genome
	 * @return The absolute position on the screen. 0 screen = 0 genomic
	 */
	protected int genomePosToAbsoluteScreenPos(int genomicPosition) {
		int res = (int)((double)genomicPosition * xFactor);
		if (xFactor > 10)
		System.out.println("factor:" + xFactor + " gen start:" + genomicPosition + "screen start:" + res);
		return res;
	}


	/**
	 * @param genomicPosition A position on the genome
	 * @return A X position on the screen.
	 */
	protected int genomePosToScreenPos(int genomicPosition) {
		if (genomicPosition < displayedXWindow.getStart()) {
			return 0;
		} else if (genomicPosition > displayedXWindow.getStop()) {
			return getWidth();
		} else {
			return (int)(((double)(genomicPosition - displayedXWindow.getStart())) * xFactor);
		}
	}


	/**
	 * @param genomicPositionStart A start position on the genome
	 * @param genomicPositionStop A stop position on the genome
	 * @return The width on the screen between this to position
	 */
	protected int twoGenomePosToScreenWidth(int genomicPositionStart, int genomicPositionStop) {
		int x1 = 0;
		int x2 = getWidth();
		if (genomicPositionStart > displayedXWindow.getStart()) {
			x1 = (int)(((double)(genomicPositionStart - displayedXWindow.getStart())) * xFactor);
		}
		if (x2 < displayedXWindow.getStop()) {
			x2 = (int)(((double)(genomicPositionStop - displayedXWindow.getStart())) * xFactor);
		}
		return (int)(x2 - x1);
	}


	/**
	 * @param x0 Position 1 on the screen
	 * @param x1 Position 2 on the screen
	 * @return The distance in base pair between the positions displayed on the screen at x0 and x1 
	 */
	private int twoScreenPosToGenomeWidth(int x0, int x1) {
		double distance = ((double)(x1 - x0) / (double)getWidth() * (double)(displayedXWindow.getStop() - displayedXWindow.getStart()));
		if ((distance >= 0) && (distance < 1)) {
			distance = 1;
		} else if ((distance < 0) && (distance > -1)) {
			distance = -1;
		}	
		return (int)distance;
	}


	/**
	 * Draws vertical lines with a gap of <i>genGapBetweenLineX</i> if <i>genGapBetweenLineX</i> is not null.
	 * Otherwise draws <i>verticalLinesCount</i> vertical lines.
	 * @param g2D Graphics2D
	 * @param genGapBetweenLineX Gap between the vertical lines.
	 */
	protected void drawVerticalLines(Graphics2D g2D, Integer genGapBetweenLineX) {
		int currentMinX = displayedXWindow.getStart();
		int currentMaxX = displayedXWindow.getStop();

		if (genGapBetweenLineX == null) {
			if (verticalLinesCount == 0) {
				return;
			} else {
				genGapBetweenLineX = (currentMaxX - currentMinX) / verticalLinesCount;
			}
		}
		int nbLineX = (int)((currentMaxX - currentMinX) / genGapBetweenLineX);
		// We draw line only if there is a gap of 4 pixels between two lines
		if (nbLineX < (getWidth() / 4)) {
			g2D.setColor(Color.LIGHT_GRAY);
			// position of the first xline
			int genFirstLineX = currentMinX - (currentMinX % genGapBetweenLineX);
			for(int i = 0; i <= nbLineX; i++) {
				int genLineX = (int)(i * genGapBetweenLineX + genFirstLineX);
				int screenLineX = genomePosToScreenPos(genLineX);
				if (screenLineX > 0) {
					DecimalFormat formatter = new DecimalFormat("###E0");
					formatter.setRoundingMode(RoundingMode.DOWN);
					String positionStr = formatter.format(genLineX);
					g2D.drawLine(screenLineX, 0, screenLineX, getHeight());
					g2D.drawString(positionStr, (int)screenLineX, 10);
				}
			}
		}
	}


	/**
	 * Draws a vertical line in the middle of the screen.
	 * @param g2D Graphics2D
	 */
	protected void drawVerticalLineCentered(Graphics2D g2D) {
		// Draw a blue line in the middle of the curve
		g2D.setColor(Color.BLUE);
		g2D.drawLine((int)(getWidth() / 2), 0, (int)(getWidth() / 2), getHeight());
	}


	/**
	 * Draws the border of the graph. 
	 * @param g2D
	 */
	protected void drawBorder(Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		// We draw a border
		g2D.drawLine(0, 0, getWidth(), 0);
		g2D.drawLine(0, 0, 0, getHeight());
	}


	/**
	 * Returns a color associated to an intensity. 
	 * High intensities are red. Medium are green. Low are blue.
	 * @param intensity An intensity indexed between min and max.
	 * @param min minimum intensity value
	 * @param max maximum intensity value
	 * @return A color
	 */
	protected Color intensityToColor(double intensity, double min, double max) {
		double distance = max - min;
		double newScore = intensity - min;
		double distanceQuarter = distance / 4;
		int r = 0;
		int v = 0;
		int b = 0;

		if ((newScore >= 0) && (newScore <= distanceQuarter)) {
			r = 0;
			v = (int)(newScore * 255 / distanceQuarter);
			b = 255;			
		} else if ((newScore > distanceQuarter) && (newScore <= 2 * distanceQuarter)) {
			r = 0;
			v = 255;
			b = (int)(255 - (newScore - distanceQuarter) * 255 / distanceQuarter);			
		} else if ((newScore > 2 * distanceQuarter) && (newScore <= 3 * distanceQuarter)) {
			r = (int)((newScore - 2 * distanceQuarter) * 255 / distanceQuarter);
			v = 255;
			b = 0;			
		} else if ((newScore > 3 * distanceQuarter) && (newScore <= distance)) {
			r = 255;
			v = (int)(255 - (newScore - 3 * distanceQuarter) * 255 / distanceQuarter);
			b = 0;			
		}		
		return new Color(r, v, b);
	}


	/**
	 * Handles the exception thrown while working with a bin list.
	 * @param e An exception.
	 * @param message Error message to display.
	 */
	protected void handleException(Exception e, String message) {
		JOptionPane.showMessageDialog(getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}


	/**
	 * Load a stripe file.
	 * @param sfe a stripe file
	 */
	public void setStripes(StripeFileExtractor sfe) {
		this.sfe = sfe;
		repaint();
	}


	/**
	 * Remove a strip file
	 */
	public void removeStripes() {
		sfe = null;
		repaint();
	}


	/**
	 * @return true if a stripe file is loaded. False otherwise.
	 */
	public boolean isStripeLoaded() {
		return (sfe != null);
	}


	/**
	 * Draws the strip of sfe if this variable is set.
	 * @param g Graphics
	 */
	private void drawStripes(Graphics g) {
		if (sfe != null) {
			int height = getHeight();
			int currentMinX = displayedXWindow.getStart();
			int currentMaxX = displayedXWindow.getStop();

			g.setColor(Color.YELLOW);
			ArrayList<Stripe> stripeList = sfe.get(displayedXWindow.getChromosome(), currentMinX, currentMaxX, xFactor);
			if (stripeList != null) {
				for (int i = 0; i < stripeList.size(); i++) {
					int x1 = genomePosToScreenPos(stripeList.get(i).getStart());
					int widthRepeat = twoGenomePosToScreenWidth(stripeList.get(i).getStart(), stripeList.get(i).getStop());
					if (widthRepeat < 1) {
						widthRepeat = 1;
					}
					g.setColor(stripeList.get(i).getColor());
					g.fillRect(x1, 0, widthRepeat, height);
				}
			}
		}
	}


	/**
	 * Calls firePropertyChange with a new X window center where the mouse double clicked.
	 * Sets the scroll mode on when the middle button is clicked
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
			// Compute the distance from the cursor to the center of the screen
			int distance = twoScreenPosToGenomeWidth(getWidth() / 2, e.getX());
			GenomicWindow newWindow = new GenomicWindow();
			newWindow.setChromosome(displayedXWindow.getChromosome());
			newWindow.setStart(displayedXWindow.getStart()+ distance);
			newWindow.setStop(displayedXWindow.getStop() + distance);
			firePropertyChange("Position changed", displayedXWindow, newWindow);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			isScrollMode = !isScrollMode;
			if (isScrollMode) {
				setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				scrollModeIntensity = 0;
				Thread scrollThread = new Thread(scrollModeThread);
				scrollThread.start();
			} else {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		}
	}


	/**
	 * Changes the cursor when the mouse is on a track.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		if (isScrollMode) {
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));	
		} else {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}	
	}

	@Override
	public void mouseExited(MouseEvent e) {}


	/**
	 * Sets the variable mouseStartDragX when the user press the button 1 of the mouse.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			mouseStartDragX = e.getX();
		}		
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Changes the window to display when the user drag the track.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			int distance = twoScreenPosToGenomeWidth(e.getX(), mouseStartDragX);
			GenomicWindow newWindow = new GenomicWindow();
			newWindow.setChromosome(displayedXWindow.getChromosome());
			newWindow.setStart(displayedXWindow.getStart()+ distance);
			newWindow.setStop(displayedXWindow.getStop() + distance);
			firePropertyChange("Position changed", displayedXWindow, newWindow);
			mouseStartDragX = e.getX();
		}		
	}


	/**
	 * Calculate the intensity of the scroll when the scroll mode is on
	 */
	@Override
	public void mouseMoved(final MouseEvent e) {
		if (isScrollMode) {
			scrollModeIntensity = twoScreenPosToGenomeWidth(e.getX(), getWidth() / 2) / 10;
		}
	}

	/**
	 * Launches a propertyChange Event when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		firePropertyChange("Zoom changed", 0, -e.getWheelRotation());
	}
}
