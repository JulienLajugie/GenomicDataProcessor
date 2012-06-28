/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The NoDataException is thrown when a BinList doesn't contain data.
 */
public class BinListNoDataException extends BinListException {
	/**
	 * Generated ID 
	 */
	private static final long serialVersionUID = 5514921742063873035L;

	/**
	 * Default constructor.
	 */
	public BinListNoDataException() {
		super(new String("The BinList doesn't contan data."), new NullPointerException());
	}
}