/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The Chromosome class represents a chromosome with a name and a length.
 */
public final class Chromosome {
	private String name;	// Name of the chromosome
	private int length;		// Length of the chromosome
	
	
	/**
	 * Constructor. Creates an instance of a Chromosome.
	 * @param name Name of the chromosome.
	 * @param length Length of the chromosome.
	 */
	public Chromosome(String name, int length) {
		this.setName(name);
		this.length = length;
	}
	

	/**
	 * @param length the length of a chromosome to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	
	/**
	 * @return the length of a chromosome
	 */
	public int getLength() {
		return length;
	}


	/**
	 * @param name the name of a chromosome to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the name of a chromosome
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return name;
	}
}
