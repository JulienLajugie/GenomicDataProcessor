/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;

import java.io.File;

/**
 * Panel 'BedGraph' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigBedGraph extends GdpConfigLoader {

	private static final long serialVersionUID = 3749581332541763497L;	// Generated serial number

	
	/**
	 * Constructor. Creates a panel GdpConfigBedGraph.
	 * @param aCM A ConfigurationManager.
	 */
	public GdpConfigBedGraph(ConfigurationManager aCM) {
		super("BedGraph", new File(aCM.getBedGraphLogFile()), aCM);
	}

	@Override
	protected void jtfLogFilePropertyChange() {
		cm.setBedGraphLogFile(getNewLogFile());
	}

}
