/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

import java.util.ArrayList;

/**
 * The RepeatFamily class provides a representation of a family of repeats.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamily {
	
	private String 						name;			// Name of the family of repeat
	private ArrayList<ArrayList<Repeat>> repeatList;	// 1 list of repeat per chromosome
	
	
	/**
	 * Creates an instance of RepeatFamily
	 * @param name name of the family
	 * @param chromoConfig {@link ChromosomeList}
	 */
	public RepeatFamily(String name, ChromosomeList chromoConfig) {
		this.name = name; 
		repeatList = new ArrayList<ArrayList<Repeat>>();
		for (int i = 0; i < chromoConfig.size(); i++) {
			repeatList.add(new ArrayList<Repeat>());
		}
	}
	
	
	/**
	 * @param chromosome
	 * @return the list of all the repeats for a chromosome
	 */
	public ArrayList<Repeat> get(short chromosome) {
		return repeatList.get(chromosome);
	}
	
	
	/**
	 * @param chromosome
	 * @param index
	 * @return the Repeat <i>index</i> on the chromosome <i>chromosome</i>
	 */
	public Repeat get(short chromosome, int index) {
		if ((chromosome < repeatList.size()) && (repeatList.get(chromosome) != null) && (index < repeatList.get(chromosome).size())) {
			return repeatList.get(chromosome).get(index);
		} else {
			return null;
		}
	} 
	
	
	/**
	 * Adds a Repeat
	 * @param chromosome chromosome of the repeat
	 * @param start start position of the repeat
	 * @param stop stop position of the repeat
	 */
	public void add(short chromosome, int start, int stop) {
		if (chromosome < repeatList.size()) {
			repeatList.get(chromosome).add(new Repeat(start, stop));
		}
	}
	
	
	/**
	 * Add a repeat
	 * @param chromosome chromosome of the repeat
	 * @param repeat Repeat
	 */
	public void add(short chromosome, Repeat repeat) {
		if (chromosome < repeatList.size()) {
			repeatList.get(chromosome).add(repeat);
		}
	}
	
	
	/**
	 * @return the size of the list (ie: chromosome count)
	 */
	public int size() {
		return repeatList.size();
	}
	
	
	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}	
}
