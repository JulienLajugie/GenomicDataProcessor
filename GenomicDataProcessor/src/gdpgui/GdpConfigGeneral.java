/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

/**
 * Panel 'General' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigGeneral extends GdpConfigPanel {

	private static final long 	serialVersionUID = -8430423146954660442L;	// Generated serial number	
	private JLabel 				jlChromoConfig;		// Label chromosome config file
	private JTextField 			jtfChromoConfig;	// TextField chromosome config file 
	private JButton 			jbBrowse;			// Button browse
	private JLabel 				jlWindowSize;		// Label window size 
	private JFormattedTextField jftfWindowSize;		// TextField window size

	
	/**
	 * Constructor. Creates a panel GdpConfigGeneral.
	 * @param aCm A ConfigurationManager
	 */
	public GdpConfigGeneral(ConfigurationManager aCm) {
		super("General", aCm);
		
		jlChromoConfig = new JLabel("Chromosome configuration file: ");
		String currentChromoConfigFile = (new File(aCm.getChromoConfigFile()).getAbsolutePath());
		jtfChromoConfig = new JTextField(currentChromoConfigFile);
		jtfChromoConfig.setColumns(30);
		jtfChromoConfig.setEditable(false);
		jtfChromoConfig.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				jtfChromoConfigPropertyChange();
			}
		});
		
		jbBrowse = new JButton("Browse");
		jbBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbBrowseActionPerformed();				
			}
		});
		

		// Create jftfWindowSize
		NumberFormatter formatter = new NumberFormatter(new DecimalFormat("###,###,###"));
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		jftfWindowSize = new JFormattedTextField(formatter);
		jftfWindowSize.setValue(cm.getWindowSize());
		jftfWindowSize.setColumns(6);
		jftfWindowSize.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				cm.setWindowSize(((Number)jftfWindowSize.getValue()).intValue());
			}
		});
		// Create jlWindowSize
		jlWindowSize = new JLabel("Default size of the windows: ");	
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlChromoConfig, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfChromoConfig, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;		
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbBrowse, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlWindowSize, c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 20, 0, 0);
		add(jftfWindowSize, c);		
		
		setVisible(true);
	}
	
	/**
	 * Displays a file chooser when the button browse is clicked.
	 * Sets the value of the textField with the value returned by the file chooser.
	 */
	private void jbBrowseActionPerformed() {
		JFileChooser jfc = new JFileChooser();		
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File currentChromoConfigFile = new File(jtfChromoConfig.getText());
		jfc.setSelectedFile(currentChromoConfigFile.getAbsoluteFile());
		jfc.setDialogTitle("Chromosome Configuration file");
		int returnVal =jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			jtfChromoConfig.setText(jfc.getSelectedFile().toString());
			if ((jtfChromoConfig.getText().length() > 0) && !(new File(jtfChromoConfig.getText())).equals(new File(cm.getChromoConfigFile()))) {
				JOptionPane.showMessageDialog(getRootPane(), "Save present configuration and restart GDP to apply changes.", "Chromosome configuration changed", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * Sets the configuration manager if the value of
	 * jtfChromoConfigPropertyChange changes.
	 */
	private void jtfChromoConfigPropertyChange() {
		cm.setChromoConfigFile(jtfChromoConfig.getText());
	}
}
