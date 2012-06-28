/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

/**
 * An abstract generic track container.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GdpTrackContainer extends JPanel {

	private static final long serialVersionUID = 7538587832797631397L;
	private static final int	CONTROL_PANEL_WIDTH = 100;
	protected Integer 			preferredHeight;			// Current preferred height
	protected String			trackName;
	protected GdpTrack			trackPanel;
	protected JPanel			controlPanel;
	
	/**
	 * Constructor
	 * @param trackName name of the track
	 * @param preferredHeight preferred height of the track
	 */
	protected GdpTrackContainer(String trackName, int preferredHeight) {
		this.trackName = trackName;
		this.preferredHeight = preferredHeight;
		// initialize the control panel
		controlPanel = new JPanel();
		controlPanel.setMinimumSize(new Dimension(CONTROL_PANEL_WIDTH, controlPanel.getMinimumSize().height));
		controlPanel.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH, controlPanel.getMaximumSize().height));
		controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, this.preferredHeight));
	}
		
	/**
	 * initializes the component and the subcomponents
	 */
	protected final void initComponent() {
		initTrackPanel();
		initControlPanel();
		// add the subcomponents
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// add track panel
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		add(trackPanel, c);
		// add control panel
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.weightx = 0;
		add(controlPanel, c);
	}
	
	/**
	 * initializes the track panel. 
	 */
	abstract protected void initTrackPanel();
	
	/**
	 * initializes the control panel
	 */
	abstract protected void initControlPanel();
	
	/**
	 * @return the track panel
	 */
	public GdpTrack getTrack() {
		return trackPanel;
	}
	
	/**
	 * @return the name of the track
	 */
	public String getTrackName() {
		return trackName;
	}	
	
	/**
	 * sets the name of the track
	 * @param trackName name of  the track
	 */
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	
	/**
	 * changes the preferred height
	 * @param value new preferred height
	 */
	public void setPreferredHeight(int value) {
		preferredHeight = value;
		setPreferredSize(new Dimension(getPreferredSize().width, preferredHeight));
		revalidate();
		repaint();
	}

}
