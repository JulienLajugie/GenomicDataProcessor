/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * The Repeat class provides a representation of a repeat.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Repeat {
	
	private long 	start;		// position start
	private long 	stop;		// position stop
	
	/**
	 * Creates an instance of Repeat
	 * @param start start position of the repeat
	 * @param stop stop position of the repeat
	 */
	public Repeat (int start, int stop) {
		this.start = start;
		this.stop = stop;
	}
	
	
	/**
	 * Creates and returns a copy of this repeat
	 */
	public Repeat clone() {
		return new Repeat((int)start, (int)stop);
	}
	
	
	/**
	 * @return the start
	 */
	public final int getStart() {
		return (int)start;
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
		return (int)stop;
	}
	
	
	/**
	 * @param stop the stop to set
	 */
	public final void setStop(int stop) {
		this.stop = stop;
	}
}
