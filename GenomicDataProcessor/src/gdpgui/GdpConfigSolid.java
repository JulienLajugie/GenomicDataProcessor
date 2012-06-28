/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;

import java.io.File;


/**
 * Panel 'Loader/Solid' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigSolid extends GdpConfigLoader {

	private static final long serialVersionUID = 1041613127550974896L; // Generated serial number

	
	/**
	 * Constructor. Creates a panel GdpConfigSolid.
	 * @param aCM A ConfigurationManager.
	 */
	public GdpConfigSolid(ConfigurationManager aCM) {
		super("Solid", new File(aCM.getSolidLogFile()), aCM);
	}
	

	@Override
	protected void jtfLogFilePropertyChange() {
		cm.setSolidLogFile(getNewLogFile());
	}

}
