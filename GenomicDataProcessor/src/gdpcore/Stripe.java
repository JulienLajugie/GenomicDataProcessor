/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.awt.Color;

/**
 * The Stripe class represents an object with 
 * a start position on a chromosome
 * a stop position on a chromosome
 * a score (or intensity)
 * a color associated to this score.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Stripe {

	private int 	start;	// Start position
	private int 	stop;	// Stop position
	private double 	score;	// Score
	private Color 	color;	// Color of the stripe (the color is calculated from the value of the score) 


	/**
	 * Public constructor.
	 * @param start
	 * @param stop
	 * @param score
	 * @param color
	 */
	public Stripe(int start, int stop, Double score, Color color) {
		this.start = start;
		this.stop = stop;
		this.score = score;
		this.color = color;
	}


	/**
	 * Public constructor. The color will be null.
	 * @param start
	 * @param stop
	 * @param score
	 */
	public Stripe(int start, int stop, Double score) {
		this.start = start;
		this.stop = stop;
		this.score = score;
		this.color = null;
	}

	
	/**
	 * Creates and returns a copy of this Stripe
	 */
	public Stripe clone() {
		return new Stripe(start, stop, score, color);
	}
	

	/**
	 * Compute the color depending on the value of the score 
	 * compared to a min score and a max score.
	 * Min is blue, max is red.
	 * @param min
	 * @param max
	 */
	public void setColor(double min, double max) {
		if (min == max) {
			color = Color.yellow;
		} else {

			double distance = max - min;
			double newScore = score - min;
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
			 //System.out.println(newScore + " (" + r + ", " + v + ", " + b + ")");
			color = new Color(r, v, b);
		}
	}


	/**
	 * @return the start
	 */
	public final int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public final void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public final int getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public final void setStop(int stop) {
		this.stop = stop;
	}

	/**
	 * @return the score
	 */
	public final Double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public final void setScore(Double score) {
		this.score = score;
	}

	/**
	 * @return the color
	 */
	public final Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public final void setColor(Color color) {
		this.color = color;
	}

}
