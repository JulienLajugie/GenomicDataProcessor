/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * The GenomicWindow class represents a window on the genome. 
 * @author Julien Lajugie
 * @version 0.1
 */
public class GenomicWindow {

		private short 	chromosome;	// Chromosome of the window
		private int  	start;		// Position start of the window
		private int 	stop;		// Position stop of the window

		
		/**
		 * Default constructor. 
		 */
		public GenomicWindow() {
			super();
		}
		
		/**
		 * Creates an instance of GenomicWindow.
		 * @param chromosome a chromosome
		 * @param start a window start
		 * @param stop a window stop
		 */
		public GenomicWindow(short chromosome, int start, int stop) {
			super();
			this.chromosome = chromosome;
			this.start = start;
			this.stop = stop;
		}
		
		
		/**
		 * @param chromosome the chromosome to set
		 */
		public void setChromosome(short chromosome) {
			this.chromosome = chromosome;
		}
		
		
		/**
		 * @return the chromosome
		 */
		public short getChromosome() {
			return chromosome;
		}
		
		
		/**
		 * @param start the start to set
		 */
		public void setStart(int start) {
			this.start = start;
		}
		
		
		/**
		 * @return the start
		 */
		public int getStart() {
			return start;
		}
		
		
		/**
		 * @param stop the stop to set
		 */
		public void setStop(int stop) {
			this.stop = stop;
		}
		
		
		/**
		 * @return the stop
		 */
		public int getStop() {
			return stop;
		}
		
}
