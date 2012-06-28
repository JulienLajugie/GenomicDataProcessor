/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;

import javax.swing.JPanel;


/**
 * Use to define the common attributes of the different panels of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GdpConfigPanel extends JPanel {

	private static final long 		serialVersionUID = 4821469631755757767L;	// Generated serial number
	private final String 			configName;		// Type of configuration
	protected ConfigurationManager 	cm;				// A ConfigurationManager
	
	
	/**
	 * Constructor. Creates a panel GdpConfigPanel.
	 * @param aName Name of the category of configuration
	 * @param aCM A ConfigurationManager
	 */
	protected GdpConfigPanel(String aName, ConfigurationManager aCM) {
		configName = aName;
		cm = aCM;
	}
	
	
	/**
	 * Override of toString use for the JTree in order to set the name of a category. 
	 */
	@Override
	public String toString() {
		return configName;
	}
}
