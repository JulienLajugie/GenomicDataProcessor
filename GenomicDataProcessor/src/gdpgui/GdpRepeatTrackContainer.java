/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import gdpcore.ConfigurationManager;
import gdpcore.RepeatFileExtractor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 * Panel containing a repeat track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpRepeatTrackContainer extends GdpTrackContainer {

	private static final long 	serialVersionUID = 1254833385804675922L;
	// TODO put static variable in config class
	private static final int	MINIMUM_HEIGHT = 50;		// Minimum height of the panel		
	private static final int	PREFERED_HEIGHT = 120;		// Prefered height of the panel
	private JScrollBar			jsbYScroll;					// Scroll bar move vertically the repeat track
	private JButton				jbClose;					// Button close
	
	
	/**
	 * Creates a GdpRepeatTrackContainer.
	 * @param rfe {@link RepeatFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param filePath Address of the file containing the repeat data
	 * @param chromo Chromosome to display
	 * @param minX Minimum position to display
	 * @param maxX Maximum position to display
	 */
	public GdpRepeatTrackContainer(RepeatFileExtractor rfe, ConfigurationManager cm, String filePath, short chromo, int minX, int maxX) {
		super("Repeats \"" + filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1) + "\"", PREFERED_HEIGHT);
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder border = BorderFactory.createTitledBorder(loweredEtched, trackName);
		setBorder(border);
		trackPanel = new GdpRepeatTrack(rfe, cm, chromo, minX, maxX);
		initComponent();
		setMinimumSize(new Dimension(getMinimumSize().width, MINIMUM_HEIGHT));
		setVisible(true);		
	}


	/**
	 * @return The GdpRepeatTrack of this container.
	 */
	@Override
	public GdpRepeatTrack getTrack() {
		return (GdpRepeatTrack) trackPanel;
	}
	
	
	@Override
	protected void initControlPanel() {
		// Build scroll bar
		jsbYScroll = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
		jsbYScroll = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, ((GdpRepeatTrack)trackPanel).getRepeatLinesCount());
		jsbYScroll.addAdjustmentListener(new AdjustmentListener() {			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				((GdpRepeatTrack)trackPanel).setFirstLineToDisplay(jsbYScroll.getValue());				
			}
		});
		
		// Build the button close
		jbClose = new JButton("x");
		jbClose.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange("Close track", false, true);
			}
		});		
		
		// Add the component
		controlPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0;
		c.weighty = 1;
		c.gridheight = 3;
		c.anchor = GridBagConstraints.LINE_START;
		controlPanel.add(jsbYScroll, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		controlPanel.add(jbClose, c);		
	}
	

	@Override
	protected void initTrackPanel() {
		trackPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Y scroll count changed")) {
					jsbYScroll.setValue(0);
					jsbYScroll.setMaximum((Integer)evt.getNewValue());					
				} else if (evt.getPropertyName().equals("first line to display changed")) {
					jsbYScroll.setValue((Integer)evt.getNewValue());
				} else {
					firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
				}
			}
		});	
	}
}
