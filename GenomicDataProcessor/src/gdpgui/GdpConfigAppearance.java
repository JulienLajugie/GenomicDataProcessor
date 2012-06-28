/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;
import gdpgui.GdpBinListTrack.GraphicsType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.NumberFormatter;


/**
 * Panel 'Appearance' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigAppearance extends GdpConfigPanel {

	private static final long 		serialVersionUID = -6533729776439754169L; // Generated serial number
	private JLabel 					jlCurveSize;		// Label curve graphics size
	private JFormattedTextField 	jftfCurveSize;		// TextField curve graphics size
	private JLabel 					jlGeneSize;			// Label gene graphics size
	private JFormattedTextField 	jftfGeneSize;		// TextField gene graphics size
	private JLabel 					jlLookAndFeel;		// Label look and feel
	private JComboBox<String>		jcbLookAndFeel;		// ComboBox look and feel
	private JLabel 					jlGraphType;		// Label graphType
	private JComboBox<GraphicsType>	jcbGraphType;		// ComboBox graphType
	

	/**
	 * Constructor. Creates a panel GdpConfigAppearance.
	 * @param aCm A ConfigurationManager.
	 */
	public GdpConfigAppearance(ConfigurationManager aCm) {
		super("Appearance", aCm);
		
		// Create formatter for the jftf
		NumberFormatter formatter = new NumberFormatter(new DecimalFormat("0"));
		formatter.setMaximum(cm.getGdpTrackConfigMaxPreferredHeight());
		formatter.setMinimum(1);
		// Create jftfCurveSize
		jftfCurveSize = new JFormattedTextField(formatter);
		jftfCurveSize.setValue(cm.getGdpCurveContainerPreferredHeight());
		jftfCurveSize.setColumns(6);
		jftfCurveSize.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				cm.setGdpCurveContainerPreferredHeight(((Number)jftfCurveSize.getValue()).intValue());
				
			}
		});
		// Create jlCurveSize
		jlCurveSize = new JLabel("Height of the curve panel: ");
		
		// Create jftfGeneSize
		jftfGeneSize = new JFormattedTextField(formatter);
		jftfGeneSize.setValue(cm.getGdpGeneContainerPreferredHeight());
		jftfGeneSize.setColumns(6);
		jftfGeneSize.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				cm.setGdpGeneContainerPreferredHeight(((Number)jftfGeneSize.getValue()).intValue());
				
			}
		});
		// Create jlCurveSize
		jlGeneSize = new JLabel("Height size of the gene panel: ");	
		
		// Retrieve the list of installed look and feel
		LookAndFeelInfo[] lafi = UIManager.getInstalledLookAndFeels();
		String[] installedAndFeelClassNames = new String[lafi.length];
		for (int i = 0; i < lafi.length; i++) {
			installedAndFeelClassNames[i] = lafi[i].getName();
		}
		
		// Create jcbLookAndFeel
		jcbLookAndFeel = new JComboBox<String>(installedAndFeelClassNames);
		// Select the look and feel of the configuration
		for (int i = 0; i < lafi.length; i++) {
			if (lafi[i].getClassName().equals(cm.getLookAndFeel())) {
				jcbLookAndFeel.setSelectedIndex(i);
			}
		}		
		jcbLookAndFeel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				cm.setLookAndFeel(UIManager.getInstalledLookAndFeels()[jcbLookAndFeel.getSelectedIndex()].getClassName());
			}
		});
		// Create jlLookAndFeel
		jlLookAndFeel = new JLabel("Look and feel:  ");
						
		// Create jcbGraphType
		GraphicsType[] typesOfGraph = GraphicsType.values();
		jcbGraphType = new JComboBox<GraphicsType>(typesOfGraph);
		jcbGraphType.setSelectedItem(cm.getGdpCurveGraphicGraphicsType());
		jcbGraphType.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				cm.setGdpCurveGraphicGraphicsType((GraphicsType)jcbGraphType.getSelectedItem());				
			}
		});
		// Create jlGraphType
		jlGraphType = new JLabel("Type of graph: ");
				
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlCurveSize, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 20, 0, 0);
		add(jftfCurveSize, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlGeneSize, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 20, 0, 0);
		add(jftfGeneSize, c);		
		
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlLookAndFeel, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(10, 20, 0, 0);
		add(jcbLookAndFeel, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlGraphType, c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(10, 20, 0, 0);
		add(jcbGraphType, c);
		
		setVisible(true);
	}	
}
