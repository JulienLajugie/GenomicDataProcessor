/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;
import gdpgui.GdpBinListTrack.GraphicsType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;


/**
 * A frame allowing to configure the properties of a curve.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpTrackConfiguration extends JDialog {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;
	
	private static final long 	serialVersionUID = -479634976273674864L;	// Generated serial number
	private int 				maxPreferredHeight;				// Maximum value for preferredHeight
	private JLabel 				jlPreferredHeight;				// Label preferredHeight
	private JFormattedTextField jftfPreferredHeight;			// TextField preferredHeight
	private Integer 			preferredHeight = null;			// Value of preferredHeight
	private JLabel 				jlXLine;						// Label XLine
	private JFormattedTextField jftfXLine;						// TextField XLine
	private Integer				xLine = null;					// Value of XLine
	private JLabel 				jlYLine;						// Label YLine
	private JFormattedTextField jftfYLine;						// TextField YLine
	private JLabel 				jlHorizontalGrid;				// Label horizontal grid
	private JCheckBox			jcbHorizontalGrid;				// Check box horizontal grid
	private Boolean				showHorizontalGrid = null;		// Horizontal grid showed or hid
	private Double				yLine = null;					// Value of YLine
	private JLabel				jlGraphicsType;					// Label type of graphics
	private JComboBox<GraphicsType>	jcbGraphicsType;				// Combo Box type of graphics
	private GraphicsType		graphicsType = null;			// Type of graphics
	private JLabel				jlCurvesColor;					// Label curve color
	private JButton				jbCurvesColor;					// Button to choose the color
	private Color				curvesColor = null;				// Color of the curves	
	private JButton 			jbOk;							// Button OK
	private JButton 			jbCancel;						// Button cancel
	private int					approved = CANCEL_OPTION;		// Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	/**
	 * Custom NumberFormatter accepting null value.
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private static final class CustomFormatter extends NumberFormatter  {

		private static final long serialVersionUID = 4767288688793575709L;	// Generated serial number

		public CustomFormatter(NumberFormat f) {
			super(f);
		}
		
		@Override
		public Object stringToValue(String text) throws ParseException {
            if (text == null || text.length() == 0) {
                return null;
            }
           return super.stringToValue(text);
        }
} 
	
	/**
	 * Constructor, create an instance of GdpTrackConfiguration.
	 * @param owner Parent frame.
	 */
	public GdpTrackConfiguration(Frame owner, ConfigurationManager cm) {
		super(owner, "Track configuration", true);
		maxPreferredHeight = cm.getGdpTrackConfigMaxPreferredHeight();
		setSize(new Dimension(cm.getGdpTrackConfigDefaultWidth(), cm.getGdpTrackConfigDefaultHeight()));
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setLocationRelativeTo(owner);
		setVisible(false);
	}

	
	/**
	 * Initializes the component and all the subcomponents.
	 */
	private void initComponent() {
		jlYLine = new JLabel("Show Y axis at y=");
		jlXLine = new JLabel("Show vertical lines every (in bp):");
		jlPreferredHeight = new JLabel("Preferred height:");

		CustomFormatter yLineFormatter = new CustomFormatter(new DecimalFormat("0.00"));
		jftfYLine = new JFormattedTextField(yLineFormatter);
		jftfYLine.setColumns(6);

		CustomFormatter xLineFormatter = new CustomFormatter(new DecimalFormat("###,###,###"));
		xLineFormatter.setMinimum(1);
		jftfXLine = new JFormattedTextField(xLineFormatter);
		jftfXLine.setColumns(6);

		CustomFormatter preferredHeightFormatter = new CustomFormatter(new DecimalFormat("0"));
		preferredHeightFormatter.setMaximum(maxPreferredHeight);
		preferredHeightFormatter.setMinimum(1);
		jftfPreferredHeight = new JFormattedTextField(preferredHeightFormatter);
		jftfPreferredHeight.setColumns(6);

		jlHorizontalGrid = new JLabel("Show horizontal grid:");
		jcbHorizontalGrid = new JCheckBox();
		jcbHorizontalGrid.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				showHorizontalGrid = jcbHorizontalGrid.isSelected();				
			}
		});
		if (showHorizontalGrid != null) {
			jcbHorizontalGrid.setSelected(showHorizontalGrid);
		}
		
		
		jlGraphicsType = new JLabel("Type of the graphics:");
		GraphicsType[] typesOfGraph = GraphicsType.values();
		jcbGraphicsType = new JComboBox<GraphicsType>(typesOfGraph);
		jcbGraphicsType.addItem(null);
		jcbGraphicsType.setSelectedItem(null);
		
		jlCurvesColor = new JLabel("Color of the curves:");
		jbCurvesColor = new JButton("Color");
		jbCurvesColor.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseCurveColor();				
			}
		});
				
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});
		
		if (preferredHeight != null) {
			jftfPreferredHeight.setValue(preferredHeight);
		}
		if (xLine != null) {
			jftfXLine.setValue(xLine);
		}
		if (yLine != null) {
			jftfYLine.setValue(yLine);
		}
		if (curvesColor != null) {
			jbCurvesColor.setBackground(curvesColor);
			jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		}
		if (curvesColor != null) {
			jbCurvesColor.setBackground(curvesColor);
			jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		}
		if (graphicsType != null) {
			jcbGraphicsType.setSelectedItem(graphicsType);
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlHorizontalGrid, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jcbHorizontalGrid, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlYLine, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jftfYLine, c);
	
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlXLine, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jftfXLine, c);

		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlPreferredHeight, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jftfPreferredHeight, c);

		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlGraphicsType, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbGraphicsType, c);	
				
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlCurvesColor, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbCurvesColor, c);		
		

		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);
	}
	

	/**
	 * @return The if the show horizontal grid option has been checked.
	 */
	public final Boolean getShowHorizontalGrid() {
		return showHorizontalGrid;
	}
	
	
	/**
	 * @return The preferred height for a curve.
	 */
	public final Integer getPreferredHeight() {
		return preferredHeight;
	}

	/**
	 * @return The value of xLine.
	 */
	public final Integer getXLine() {
		return xLine;
	}

	/**
	 * @return The value of yLine.
	 */
	public final Double getYLine() {
		return yLine;
	}

	/**
	 * @return The value of curvesColor
	 */
	public final Color getCurvesColor() {
		return curvesColor;
	}
	
	/**
	 * @return The value graphicsType
	 */
	public final GraphicsType getGraphicsType() {
		return (GraphicsType)jcbGraphicsType.getSelectedItem();
	}
	
	/**
	 * Hides this frame when Cancel is pressed. 
	 */
	private void jbCancelActionPerformed() {
		this.setVisible(false);
	}
	
	
	/**
	 * Asks the user to choose a color.
	 */
	private void chooseCurveColor() {		
		Color newCurvewColor = JColorChooser.showDialog(getRootPane(), "Choose a color for the selected curves", curvesColor);
		if (newCurvewColor != null) {
			curvesColor = newCurvewColor;
			jbCurvesColor.setBackground(curvesColor);
			jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		}		
	}

	
	/**
	 * Called when OK is pressed.
	 */
	private void jbOkActionPerformed() {
		approved = APPROVE_OPTION;
		if (jftfPreferredHeight.getValue() == null) {
			preferredHeight = null;
		} else {
			preferredHeight = ((Number)jftfPreferredHeight.getValue()).intValue();
		}
		if (jftfXLine.getValue() == null) {
			xLine = null;
		} else {
			xLine = ((Number)jftfXLine.getValue()).intValue();
		}
		if (jftfYLine.getValue() == null) {
			yLine = null;
		} else {
			yLine = ((Number)jftfYLine.getValue()).doubleValue();
		}
		this.setVisible(false);
	}
	
	
	/**
	 * Shows the component.
	 * @param commonShowHGrid Check or uncheck the show grid box.
	 * @param commonHeight A preferred Height to show in the corresponding text field.
	 * @param commonXline A xLine value to show in the text corresponding field.
	 * @param commonYline A yLine value to show in the corresponding text field.
	 * @param commonColor A common curve color.
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showTrackConfiguration(Boolean commonShowHGrid, Integer commonHeight, Integer commonXline, Double commonYline, Color commonColor, GraphicsType commonGraphicsType) {
		showHorizontalGrid = commonShowHGrid;
		preferredHeight = commonHeight;
		xLine = commonXline;
		yLine = commonYline;
		curvesColor = commonColor;
		graphicsType = commonGraphicsType;
		initComponent();
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
		setVisible(true);
		return approved;
	}

}
