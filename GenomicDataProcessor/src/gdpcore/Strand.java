/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The Strand class represents a genomic strand.
 */
public enum Strand {
	five,
	three;

	
	/**
	 * @return "+" for strand 5', "-" for strand 3'.
	 */
	@Override
	public String toString(){
		switch (this) {
			case five:
				return "+";
			case three:
				return "-";
			default:
				return null;				
		}
	}
}
