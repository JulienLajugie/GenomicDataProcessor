/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.io.File;

import gdpcore.ConfigurationManager;


/**
 * Panel 'Loader/Solexa' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigSolexa extends GdpConfigLoader {

	private static final long serialVersionUID = 3738260428486401934L; // Generated serial number

	/**
	 * Constructor. Creates a panel GdpConfigSolexa.
	 * @param aCM A ConfigurationManager.
	 */
	public GdpConfigSolexa(ConfigurationManager aCM) {
		super("Solexa", new File(aCM.getSolexaLogFile()), aCM);
	}
	
	/**
	 * @see gdpgui.GdpConfigLoader#jtfLogFilePropertyChange()
	 */
	@Override
	protected void jtfLogFilePropertyChange() {
		cm.setSolexaLogFile(getNewLogFile());
	}

}
