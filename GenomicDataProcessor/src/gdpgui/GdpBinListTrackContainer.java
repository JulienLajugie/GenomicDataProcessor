/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BinList;
import gdpcore.BinListNoDataException;
import gdpcore.ConfigurationManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;


/**
 * A container for a BinList track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpBinListTrackContainer extends GdpTrackContainer {

	private static final long 	serialVersionUID = -1208361367105578689L;	// Generated serial number
	private JFormattedTextField jftfIntensityMin;				// TextField for the intensity max
	private JFormattedTextField jftfIntensityMax;				// TextField for the intensity min
	private JCheckBox 			jcbSelected;					// CheckBox used to select the curve
	private TitledBorder 		borderWhenNotSelected;			// Border of the component when the curve isn't selected 
	private Border 				borderWhenSelected;				// Border of the component when the curve is selected
	private double 				validIntensityMin;				// A valid minimum intensity to display
	private double 				validIntensityMax;				// A valid maximum intensity to display

	
	/**
	 * Public constructor.
	 * @param cm Configuration manager.
	 * @param trackName Name of the track.
	 * @param aBinList BinList of the track
	 * @param chromo Displayed chromosome.
	 * @param xMin Position start.
	 * @param xMax Position stop.
	 * @throws BinListNoDataException
	 */
	GdpBinListTrackContainer(ConfigurationManager cm, String trackName, BinList aBinList, short chromo, int xMin, int xMax) throws BinListNoDataException {
		super(trackName, cm.getGdpCurveContainerPreferredHeight());
		this.validIntensityMin = aBinList.min();
		this.validIntensityMax = aBinList.max();
		this.trackPanel = new GdpBinListTrack(cm, aBinList, chromo,  xMin, xMax, validIntensityMin, validIntensityMax);
		createBorders();
		initComponent();
		setBorder(borderWhenNotSelected);
		setSize(new Dimension(cm.getGdpCurveContainerPreferredWidth(), cm.getGdpCurveContainerPreferredHeight()));
		setPreferredSize(new Dimension(cm.getGdpCurveContainerPreferredWidth(), cm.getGdpCurveContainerPreferredHeight()));
		setMinimumSize(new Dimension(cm.getGdpCurveContainerMinWidth(), cm.getGdpCurveContainerMinHeight()));
	}

	
	/**
	 * Creates the borders of the container (1 when selected, 1 when not).
	 */
	private void createBorders() {
		// Create border when not selected
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		DecimalFormat df = new DecimalFormat("0.##");
		borderWhenNotSelected = BorderFactory.createTitledBorder(loweredEtched, trackName + " [" + df.format(((GdpBinListTrack)trackPanel).getBinList().getWindowSize() / 1000.0) + "kb]");
		// Create border when selected
		Border redline = BorderFactory.createLineBorder(Color.red);
		borderWhenSelected = BorderFactory.createCompoundBorder(redline, borderWhenNotSelected);
	}

	@Override
	protected void initTrackPanel() {
		trackPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
			}
		});

	}


	@Override
	protected void initControlPanel() {
		// Initialize the checkBox
		jcbSelected = new JCheckBox("Select");
		jcbSelected.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("Curve state changes", !jcbSelected.isSelected(), jcbSelected.isSelected());
				if (jcbSelected.isSelected()) {
					setBorder(borderWhenSelected);
				} else {
					setBorder(borderWhenNotSelected);
				}
			}
		});

		// Initialize the TextField intensity max
		NumberFormatter intensityMaxFormatter = new NumberFormatter(new DecimalFormat("0.00"));
		intensityMaxFormatter.setMinimum(validIntensityMin);
		jftfIntensityMax = new JFormattedTextField(intensityMaxFormatter);
		jftfIntensityMax.setValue(validIntensityMax);
		jftfIntensityMax.setColumns(6);
		jftfIntensityMax.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfIntensityMaxPropertyChange();					
			}
		});

		// Initialize the TextField intensity min
		NumberFormatter intensityMinFormatter = new NumberFormatter(new DecimalFormat("0.00"));
		intensityMinFormatter.setMaximum(validIntensityMax);
		jftfIntensityMin = new JFormattedTextField(intensityMinFormatter);
		jftfIntensityMin.setValue(validIntensityMin);
		jftfIntensityMin.setColumns(6);
		jftfIntensityMin.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfIntensityMinPropertyChange();					
			}
		});

		// Add the subcomponents
		controlPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.33;
		c.weighty = 0.49;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		controlPanel.add(jftfIntensityMax, c);

		c.gridy = 1;
		c.weighty = 0.02;
		c.anchor = GridBagConstraints.CENTER;
		controlPanel.add(jcbSelected, c);

		c.gridy = 2;
		c.weighty = 0.49;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		controlPanel.add(jftfIntensityMin, c);
	}

	
	/**
	 * Called when the maximum intensity changes.
	 * Repaints the graphics and changes the maximum value of the textField jftfIntensityMin.
	 */
	private void jftfIntensityMaxPropertyChange() {
		double currentMaxValue = ((Number)(jftfIntensityMax.getValue())).doubleValue();
		validIntensityMax = currentMaxValue;
		((GdpBinListTrack)trackPanel).setMaxY(validIntensityMax);
		((NumberFormatter)jftfIntensityMin.getFormatter()).setMaximum(validIntensityMax);
	}


	/**
	 * Called when the minimum intensity changes.
	 * Repaints the graphics and changes the minimum value of the textField jftfIntensityMax.
	 */
	private void jftfIntensityMinPropertyChange() {
		double currentMinValue = ((Number)(jftfIntensityMin.getValue())).doubleValue();
		validIntensityMin = currentMinValue;
		((GdpBinListTrack)trackPanel).setMinY(validIntensityMin);	
		((NumberFormatter)jftfIntensityMax.getFormatter()).setMinimum(validIntensityMin);
	}


	/**
	 * Changes the current minimum and maximum intensity.
	 * @param min New minimum intensity.
	 * @param max New maximum intensity.
	 */
	public void setMinMaxY(Double min, Double max) {
		if (min > validIntensityMax) {
			jftfIntensityMax.setValue(max);
			jftfIntensityMin.setValue(min);

		} else {
			jftfIntensityMin.setValue(min);
			jftfIntensityMax.setValue(max);
		}
		validIntensityMax = max;
		validIntensityMin = min;		
	}


	/**
	 * @return A boolean indicating if the track is selected or not 
	 */
	public boolean isSelected() {
		return jcbSelected.isSelected();
	}


	/**
	 * Changes the name of the track and update the titled border.
	 * @param trackName A name for the track.
	 */
	@Override
	public void setTrackName(String trackName) {
		// Create border when not selected
		DecimalFormat df = new DecimalFormat("0.##");
		super.setTrackName(trackName + " [" + df.format(((GdpBinListTrack)trackPanel).getBinList().getWindowSize() / 1000.0) + "kb]");
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		borderWhenNotSelected = BorderFactory.createTitledBorder(loweredEtched, trackName);
		// Create border when selected
		Border redline = BorderFactory.createLineBorder(Color.red);
		borderWhenSelected = BorderFactory.createCompoundBorder(redline, borderWhenNotSelected);
		setBorder(borderWhenSelected);
	}


	/**
	 * Selects the track.
	 */
	public void select() {
		jcbSelected.setSelected(true);		
	}

	
	/**
	 * @return The {@link GdpBinListTrack} of the curve.
	 */
	@Override
	public GdpBinListTrack getTrack() {
		return (GdpBinListTrack) trackPanel;
	}
}