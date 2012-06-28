/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The BinListNullException is thrown when a BinList is null.
 */
public class BinListNullException extends BinListException {
	/**
	 * Generated ID 
	 */
	private static final long serialVersionUID = -2973141697166450934L;
	/**
	 * Constructor.
	 */
	public BinListNullException() {
		super(new String("The BinList is null."), new NullPointerException());
	}
}