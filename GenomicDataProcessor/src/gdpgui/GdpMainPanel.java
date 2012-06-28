/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BinList;
import gdpcore.Chromosome;
import gdpcore.ChromosomeList;
import gdpcore.ConfigurationManager;
import gdpcore.GeneFileExtractor;
import gdpcore.GenomicWindow;
import gdpcore.RepeatFileExtractor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.text.NumberFormatter;


/**
 * Main panel of the GDP. Contains  a  list of curve container, a gene container 
 * and some components allowing to zoom, to change the displayed positions and chromosome.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpMainPanel extends JPanel {

	private static final long 		serialVersionUID = 3291999275477538823L;	// Generated serial number
	private int				 		defaultMin;						// Default minimum position
	private int 					defaultMax; 					// Default maximum position
	private short 					defaultIndexChromo;				// Default chromosome index
	private int 					nbXScrollIincrement; 			// Number of increment with jsbXAxis
	private int 					minZoom; 						// Minimum zoom = 2^MIN_ZOOM bases 
	private JFormattedTextField  	jftfMax;						// TextField for the max position
	private JFormattedTextField  	jftfMin;						// TextField for the min position
	private JComboBox<Chromosome>	jcbChromosome;					// ComboBox for the chromosome
	private GdpListOfTrackContainers listOfTracksPanel;				// Panel with the list of curve container
	private JScrollBar 				jsbXAxis;						// ScrollBar used to move the position
	private JScrollBar 				jsbZoom;						// ScrollBar used to zoom +/-
	private JLabel					jlZoom;							// Label displaying the current zoom
	private JLabel 					jlChromo;						// Label "Chromosome"
	private int 					validMin;						// A valid min position
	private int 					validMax;						// A valid max position
	private short					validIndexChromo;				// A valid chromosome index
	private ChromosomeList 			chromoConfig;					// A chromosome configuration
	private boolean					jsbZoomPerformAction = true;
	private boolean					jsbXAxisPerformAction = true;
	private boolean					jftfMaxPerformAction = true;
	private boolean					jftfMinPerformAction = true;


	/**
	 * Creates a GdpMainPanel.
	 */
	public GdpMainPanel(ConfigurationManager cm, ChromosomeList aChromoConfig) {
		super();
		defaultMin = cm.getGdpMainPanelDefaultMin();
		defaultMax = cm.getGdpMainPanelDefaultMax();
		defaultIndexChromo = cm.getGdpMainPanelDefaultChromo();
		nbXScrollIincrement = cm.getGdpMainPanelNbXScrollIncrement();
		minZoom = cm.getGdpMainPanelMinZoom();
		validMin = defaultMin;
		validMax = defaultMax;
		validIndexChromo = defaultIndexChromo;
		chromoConfig = aChromoConfig;
		setVisible(false);
		initComponent();
	}


	/**
	 * Initializes the component and subcomponents.
	 */
	private void initComponent() {
		// Create TextField for the min position
		jftfMin = new JFormattedTextField(new DecimalFormat("###,###,###"));
		jftfMin.setValue(validMin);
		jftfMin.setColumns(8);
		jftfMin.setMinimumSize(jftfMin.getPreferredSize());
		jftfMin.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfMinPropertyChange();					
			}
		});
		// Create TextField for the max position
		jftfMax = new JFormattedTextField(new DecimalFormat("###,###,###"));
		jftfMax.setValue(validMax);
		jftfMax.setColumns(8);
		jftfMax.setMinimumSize(jftfMax.getPreferredSize());
		jftfMax.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfMaxPropertyChange();					
			}
		});
		// Set the maximum and minimum of the formatters
		setFormatters();
		// Create ComboBox for the chromosome selection
		jcbChromosome = new JComboBox<Chromosome>(chromoConfig.getAllChromosomes());
		jcbChromosome.setSelectedIndex(validIndexChromo);
		jcbChromosome.setPreferredSize(new Dimension(jcbChromosome.getPreferredSize().width + 20, jcbChromosome.getPreferredSize().height));
		jcbChromosome.setMinimumSize(jcbChromosome.getPreferredSize());
		jcbChromosome.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				jcbChromosomeItemStateChanged();				
			}
		});
		// Create listOfTracksPanel
		listOfTracksPanel = new GdpListOfTrackContainers();
		listOfTracksPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				trackPropertyChange(evt);				
			}
		});

		// Create the X axis JScrollBar
		jsbXAxis = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, minPosition(), maxPosition() + (validMin - validMax));
		jsbXAxis.setBlockIncrement((int)((validMax - validMin) / nbXScrollIincrement));
		jsbXAxis.setUnitIncrement((int)((validMax - validMin) / nbXScrollIincrement));
		jsbXAxis.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jsbXAxisAdjustmentValueChanged();
			}
		});
		// Create the zoom JScrollBar
		int zoomBarPosition = (int)(Math.log10(defaultMax - defaultMin) / Math.log10(2));
		int zoomBarMaxPosition = (int)(Math.log10(chromoConfig.get(defaultIndexChromo).getLength()) / Math.log10(2)) + 1;
		zoomBarPosition = zoomBarMaxPosition + minZoom - zoomBarPosition;
		jsbZoom = new JScrollBar(JScrollBar.HORIZONTAL, zoomBarPosition, 0, minZoom, zoomBarMaxPosition);
		jsbZoom.setBlockIncrement(1);
		jsbZoom.setUnitIncrement(1);
		jsbZoom.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jsbZoomAdjustmentValueChanged();
			}
		});

		// Create zoom label
		jlZoom = new JLabel();		
		jlZoom.setText("Zoom: " + new DecimalFormat("###,###,###").format(defaultMax - defaultMin));
		// Create the GdpGeneContainer panel

		// Create the label chromosome
		jlChromo = new JLabel("Chromosome:");

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 0.5;
		c.weighty = 0.96;
		add(listOfTracksPanel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 0.5;
		c.weighty = 0.01;
		c.anchor = GridBagConstraints.PAGE_START;
		add(jsbXAxis, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.01;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(jftfMin, c);

		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jftfMax, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_START;
		add(jsbZoom, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.PAGE_END;
		add(jlChromo, c);

		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		add(jlZoom, c);	

		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.PAGE_START;
		add(jcbChromosome, c);
	}


	/**
	 * @return the greatest position to display
	 */
	private int maxPosition() {
		return chromoConfig.get(validIndexChromo).getLength() + (validMax - validMin) / 2;
	}


	/**
	 * @return the smallest position to display
	 */
	private int minPosition() {
		return 0 - (validMax - validMin) / 2;
	}


	/**
	 * Resets the min and the max of the formatter of the two JFormattedTextFields
	 */
	private void resetFormatters() {
		((NumberFormatter)jftfMin.getFormatter()).setMinimum(null);
		((NumberFormatter)jftfMin.getFormatter()).setMaximum(null);
		((NumberFormatter)jftfMax.getFormatter()).setMinimum(null);
		((NumberFormatter)jftfMax.getFormatter()).setMaximum(null);
	}

	/**
	 * Sets the min and the max of the formatter of the two JFormattedTextFields
	 */
	private void setFormatters() {
		((NumberFormatter)jftfMin.getFormatter()).setMinimum(minPosition());
		((NumberFormatter)jftfMin.getFormatter()).setMaximum(validMax - 1);
		((NumberFormatter)jftfMax.getFormatter()).setMinimum(validMin + 1);
		((NumberFormatter)jftfMax.getFormatter()).setMaximum(maxPosition());
	}


	/**
	 * Called when the Zoom JScrollBar is used.
	 */
	private void jsbZoomAdjustmentValueChanged() {
		if (jsbZoomPerformAction) {
			int zoomValue = jsbZoom.getMaximum() + jsbZoom.getMinimum() - jsbZoom.getValue();
			int halfDistanceNewInterval = (int)(Math.pow(2, zoomValue) / 2) + 1;
			if ((2 * halfDistanceNewInterval) > chromoConfig.get(validIndexChromo).getLength()) {
				halfDistanceNewInterval = chromoConfig.get(validIndexChromo).getLength();
			}
			int midPositionOldInterval = validMin + (validMax - validMin) / 2;
			validMin = midPositionOldInterval - halfDistanceNewInterval;
			validMax = midPositionOldInterval + halfDistanceNewInterval;
			resetFormatters();
			jftfMaxPerformAction = false;
			jftfMax.setValue(validMax);
			jftfMinPerformAction = false;
			jftfMin.setValue(validMin);
			setFormatters();
			setJsbXAxis();
			jlZoom.setText("Zoom: " + new DecimalFormat("###,###,###").format(validMax - validMin));
			listOfTracksPanel.setMinMaxX(validMin, validMax);
		} else {
			jsbZoomPerformAction = true;
		}		
	}


	/**
	 * Called when the max TextField changes. 
	 */
	private void jftfMaxPropertyChange() {
		if (jftfMaxPerformAction) {
			int currentMaxValue = ((Number)(jftfMax.getValue())).intValue();
			if (currentMaxValue != validMax) {
				resetFormatters();
				validMax = currentMaxValue;
				setFormatters();
				setJsbXAxis();
				setJsbZoom();
				listOfTracksPanel.setMaxX(validMax);	
			}
		}
		else {
			jftfMaxPerformAction = true;
		}
	}


	/**
	 * Called when the min TextField changes.
	 */
	private void jftfMinPropertyChange() {
		if (jftfMinPerformAction) {
			int currentMinValue = ((Number)(jftfMin.getValue())).intValue();
			if (currentMinValue != validMin) {
				resetFormatters();
				validMin = currentMinValue;
				setFormatters();
				setJsbXAxis();
				setJsbZoom();
				listOfTracksPanel.setMinX(validMin);	
			}
		} else {
			jftfMinPerformAction = true;
		}
	}


	/**
	 * Called when the chromosome CheckBox changes.
	 */
	private void jcbChromosomeItemStateChanged() {
		short currentChromosome = (short)jcbChromosome.getSelectedIndex();
		validIndexChromo = currentChromosome;
		validMin = defaultMin;
		int chromoMaxLength = chromoConfig.get(validIndexChromo).getLength();
		validMax = (defaultMax >= chromoMaxLength) ? chromoMaxLength : defaultMax;		
		resetFormatters();
		jftfMinPerformAction = false;
		jftfMin.setValue(validMin);
		jftfMaxPerformAction = false;
		jftfMax.setValue(validMax);
		setFormatters();
		setJsbXAxis();
		setJsbZoom();		
		listOfTracksPanel.setChromosome(validIndexChromo, validMin, validMax);
	}


	/**
	 * Called when the X axis JScrollBar changes.
	 */
	private void jsbXAxisAdjustmentValueChanged() {
		if (jsbXAxisPerformAction) {
			int gap = validMax - validMin;
			validMin = jsbXAxis.getValue();
			validMax = validMin + gap;
			resetFormatters();
			jftfMinPerformAction = false;
			jftfMin.setValue(validMin);
			jftfMaxPerformAction = false;
			jftfMax.setValue(validMax);
			setFormatters();
			listOfTracksPanel.setMinMaxX(validMin, validMax);	
		} else {
			jsbXAxisPerformAction = true;
		}
	}


	/**
	 * Adds a BinList track
	 * @param cm {@link ConfigurationManager}
	 * @param aBinList {@link BinList}
	 * @param trackName Name of the track
	 */
	public void addBinListTrack(ConfigurationManager cm, BinList aBinList, String trackName) {
		try {
			listOfTracksPanel.addBinListTC(cm, aBinList, trackName, validIndexChromo, validMin, validMax);
			setVisible(true);
			revalidate();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getRootPane(), "Error while adding the binList track", "Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}


	/**
	 * Adds a gene track
	 * @param gfe {@link GeneFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param filePath address of the gene file
	 */
	public void addGeneTrack(GeneFileExtractor gfe, ConfigurationManager cm, String filePath) {
		listOfTracksPanel.addGeneTC(gfe, cm, filePath, validIndexChromo, validMin, validMax);
		setVisible(true);
		revalidate();
	}


	/**
	 * Adds a repeat track 
	 * @param rfe {@link RepeatFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param filePath address of the repeat file
	 */
	public void addRepeatTrack(RepeatFileExtractor rfe, ConfigurationManager cm, String filePath) {
		listOfTracksPanel.addRepeatTC(rfe, cm, filePath, validIndexChromo, validMin, validMax);
		setVisible(true);
		revalidate();	
	}

	/**
	 * Changes the properties of the X axis JScrollBar. 
	 */
	private void setJsbXAxis() {
		jsbXAxisPerformAction = false;
		jsbXAxis.setMinimum(validMin);
		jsbXAxis.setMaximum(validMin);		
		jsbXAxis.setValue(validMin);
		jsbXAxis.setBlockIncrement((int)((validMax - validMin) / nbXScrollIincrement));
		jsbXAxis.setUnitIncrement((int)((validMax - validMin) / nbXScrollIincrement));
		jsbXAxis.setMinimum(minPosition());
		jsbXAxis.setMaximum(maxPosition() + (validMin - validMax));		
	}


	/**
	 * Changes the properties of the zoom JScrollBar.
	 */
	private void setJsbZoom() {
		int distance = validMax - validMin;
		int zoomBarPosition = (int)(Math.log10(distance) / Math.log10(2)) + 1;
		int zoomBarMaxPosition = (int)(Math.log10(chromoConfig.get(defaultIndexChromo).getLength()) / Math.log10(2)) + 1;
		jsbZoom.setMaximum(zoomBarMaxPosition);
		zoomBarPosition = jsbZoom.getMaximum() + minZoom - zoomBarPosition;
		jsbZoomPerformAction = false;
		jsbZoom.setValue(zoomBarPosition);
		jlZoom.setText("Zoom: " + new DecimalFormat("###,###,###").format(validMax - validMin));
	}


	/**
	 * @return The getGdpListOfTrackContainers of this panel.
	 */
	public GdpListOfTrackContainers getGdpListOfTrackContainers() {
		return listOfTracksPanel;
	}


	/**
	 * Called when we remove the last CurvePanel.
	 * Hide this panel and restore the default position.
	 */
	public void reset() {
		setVisible(false);
		resetFormatters();
		validMin = defaultMin;
		validMax = defaultMax;
		validIndexChromo = defaultIndexChromo;
		setFormatters();
		jftfMin.setValue(validMin);
		jftfMax.setValue(validMax);
		jcbChromosome.setSelectedIndex(defaultIndexChromo);
		setJsbXAxis();
		setJsbZoom();
	}


	/**
	 * Called when the properties of graphics change.
	 * @param evt
	 */
	public void trackPropertyChange(PropertyChangeEvent evt) {
		// Fire a property change event if the property of the list of curves changes
		if (evt.getPropertyName().equals("Curve state changes")) {
			firePropertyChange("Curve state changes", evt.getOldValue(), evt.getNewValue());
			// change the position
		} else if (evt.getPropertyName().equals("Position changed")) {
			int newValidMin = ((GenomicWindow)evt.getNewValue()).getStart();
			int newValidMax = ((GenomicWindow)evt.getNewValue()).getStop();
			if (newValidMin < minPosition()) {
				newValidMin = minPosition();
				newValidMax = newValidMin + validMax - validMin;
			} else if (newValidMax > maxPosition()) {
				newValidMax = maxPosition();
				newValidMin = newValidMax - (validMax - validMin);
			}
			validMin = newValidMin;
			validMax = newValidMax;
			resetFormatters();
			jftfMinPerformAction = false;
			jftfMin.setValue(validMin);
			jftfMaxPerformAction = false;
			jftfMax.setValue(validMax);
			setFormatters();
			setJsbXAxis();
			listOfTracksPanel.setMinMaxX(validMin, validMax);	
		} else if (evt.getPropertyName().equals("Zoom changed")) {
			jsbZoom.setValue(jsbZoom.getValue() + (Integer)evt.getNewValue());
		} else if (evt.getPropertyName().equals("Close track")) {
			if (listOfTracksPanel.isEmpty()) {
				reset();
				repaint();
				revalidate();
			}
		}
	}
}