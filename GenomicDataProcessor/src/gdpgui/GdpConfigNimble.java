/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BinList;
import gdpcore.ConfigurationManager;
import gdpcore.BinList.IntensityCalculation;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * Panel 'Loader/NimbleGene' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigNimble extends GdpConfigLoader {

	private static final long 				serialVersionUID = 5276529108555973058L;// Generated serial number
	private JLabel 							jlCriterionOfCalculation;		// Label criterion of calculatio
	private JComboBox<IntensityCalculation>	jcCriterionOfCalculation;		// ComboBox criterion of calculation

	
	/**
	 * Constructor. Creates a panel GdpConfigNimble.
	 * @param aCM A ConfigurationManager.
	 */
	public GdpConfigNimble(ConfigurationManager aCM) {
		super("NimbleGene", new File(aCM.getNimbleLogFile()), aCM);
		jlCriterionOfCalculation = new JLabel("Method to extract the data: ");
		
		IntensityCalculation[] ic = BinList.IntensityCalculation.values();
		jcCriterionOfCalculation = new JComboBox<IntensityCalculation>(ic);
		jcCriterionOfCalculation.setSelectedIndex(cm.getCriterionOfCalculation().ordinal());
		jcCriterionOfCalculation.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				cm.setCriterionOfCalculation((BinList.IntensityCalculation)jcCriterionOfCalculation.getSelectedItem());				
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20, 0, 0, 0);
		add(jlCriterionOfCalculation, c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcCriterionOfCalculation, c);
	}

	@Override
	protected void jtfLogFilePropertyChange() {
		cm.setNimbleLogFile(getNewLogFile());

	}

}
