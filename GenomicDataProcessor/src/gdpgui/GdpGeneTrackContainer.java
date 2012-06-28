/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;
import gdpcore.Gene;
import gdpcore.GeneFileExtractor;
import gdpcore.GenomicWindow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 * Panel containing a gene track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGeneTrackContainer extends GdpTrackContainer {

	private static final long 	serialVersionUID = -2889622826972844084L; 	// Generated serial number
	// TODO: static in config manager
	private static final int	MINIMUM_HEIGHT = 120;
	private JTextField 			jtfGeneName;				// TextField used to search for a gene
	private JButton				jbClose;					// Button close
	private JButton 			jbSearch;					// Button to launch the gene search
	private JScrollBar			jsbYScroll;					// Scroll bar move vertically the gene track
	

	/**
	 * Creates a GdpGeneTrackContainer.
	 * @param gfe {@link GeneFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param filePath Address of the file containing the gene data
	 * @param chromo Chromosome to display
	 * @param minX Minimum position to display
	 * @param maxX Maximum position to display
	 */
	public GdpGeneTrackContainer(GeneFileExtractor gfe, ConfigurationManager cm, String filePath, short chromo, int minX, int maxX) {
		super("Genes \"" + filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1) + "\"", cm.getGdpGeneContainerPreferredHeight());
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder border = BorderFactory.createTitledBorder(loweredEtched, trackName);
		setBorder(border);
		trackPanel = new GdpGenesTrack(gfe, cm, chromo, minX, maxX);
		initComponent();
		setMinimumSize(new Dimension(getMinimumSize().width, MINIMUM_HEIGHT));
	}
	
	
	/**
	 * @return The GdpGenesTrack of this container.
	 */
	@Override
	public GdpGenesTrack getTrack() {
		return (GdpGenesTrack) trackPanel;
	}
	

	@Override
	protected void initControlPanel() {
		// Build scroll bar
		jsbYScroll = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, ((GdpGenesTrack)trackPanel).getGeneLinesCount());
		jsbYScroll.addAdjustmentListener(new AdjustmentListener() {			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				((GdpGenesTrack)trackPanel).setFirstLineToDisplay(jsbYScroll.getValue());				
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
		
		
		// Build the TextField
		jtfGeneName = new JTextField("Gene Name");
		// This listener write / erase the text "Gene Name" in the search TextField
		jtfGeneName.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if ((jtfGeneName.getText() == null) || (jtfGeneName.getText().length() == 0)) {
					jtfGeneName.setText("Gene Name");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				if (jtfGeneName.getText().equals("Gene Name")) {
					jtfGeneName.setText("");
				} else {
					jtfGeneName.setSelectionStart(0);
					jtfGeneName.setSelectionEnd(jtfGeneName.getText().length());
				}				
			}
		});
		// This listener is used to launch the search when enter is pressed in the search TextField
		jtfGeneName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent ke) {
				if (ke.getKeyChar() == '\n') {
					searchGene();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
		// Build the button search
		jbSearch = new JButton("Search");
		jbSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchGene();
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
		c.weightx = 0;
		c.weighty = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		controlPanel.add(jbClose, c);		
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0.5;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		controlPanel.add(jtfGeneName, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_START;
		controlPanel.add(jbSearch, c);
		
	}
	
	
	/**
	 * Searches the gene whose name is entered in the text field 
	 */
	private void searchGene() {
		if ((jtfGeneName.getText() != null) && (jtfGeneName.getText().length() > 0) && (!jtfGeneName.getText().equals("Gene Name"))) {
			Gene searchedGene = ((GdpGenesTrack)trackPanel).searchGene(jtfGeneName.getText());
			if (searchedGene != null) {
				GenomicWindow newPosition = new GenomicWindow();
				newPosition.setChromosome(searchedGene.getChromo());
				newPosition.setStart(searchedGene.getTxStart());
				newPosition.setStop(searchedGene.getTxStop());
				firePropertyChange("Position changed", null, newPosition);						
			} else {
				JOptionPane.showMessageDialog(getRootPane(), "No gene found.", "Search result", JOptionPane.WARNING_MESSAGE);
			}							
		}
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