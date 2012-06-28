/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gdpcore.ConfigurationManager;

/**
 * Use to define the common attributes of the different panels of the menu
 * 'Loader' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GdpConfigLoader extends GdpConfigPanel {

	private static final long 	serialVersionUID = 725595139616780434L; // Generated serial number
	private JLabel 				jlLogFile;	// Label log file
	private JTextField 			jtfLogFile;	// TextField log file 
	private JButton 			jbBrowse;	// Button browse
	
	
	/**
	 * Constructor. Creates a panel GdpConfigLoader.
	 * @param aName Type of loader
	 * @param currentLogFile Value of the current log file.
	 * @param aCM A ConfigurationManager
	 */
	protected GdpConfigLoader(final String aName, final File currentLogFile, ConfigurationManager aCM) {
		super(aName, aCM);
		
		jlLogFile = new JLabel(aName + " log file: ");
		
		jtfLogFile = new JTextField(currentLogFile.getAbsolutePath());
		jtfLogFile.setColumns(30);
		jtfLogFile.setEditable(false);
		jtfLogFile.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				jtfLogFilePropertyChange();
			}
		});
		
		jbBrowse = new JButton("Browse");
		jbBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbBrowseActionPerformed(aName, currentLogFile);				
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlLogFile, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfLogFile, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;		
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbBrowse, c);
	}
	
	
	/**
	 * @return The value of the TextField log file
	 */
	protected String getNewLogFile() {
		return jtfLogFile.getText();
	}


	/**
	 * Displays a file chooser when the button browse is clicked.
	 * Sets the value of the textField with the value returned by the file chooser.
	 * @param aName	Type of loader.
	 * @param currentLogFile Name of the current log file.
	 */
	private void jbBrowseActionPerformed(String aName, File currentLogFile) {
		JFileChooser jfc = new JFileChooser();		
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setSelectedFile(currentLogFile.getAbsoluteFile());
		jfc.setDialogTitle(aName + " log file");
		int returnVal =jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			jtfLogFile.setText(jfc.getSelectedFile().toString());
		}
	}
	
	
	/**
	 * Sets the property log file of the ConfigurationManager when the value 
	 * of the TextField for the path to the log file changes. 
	 * This property is different for each kind of loader.
	 */
	abstract protected void jtfLogFilePropertyChange();
		
}
