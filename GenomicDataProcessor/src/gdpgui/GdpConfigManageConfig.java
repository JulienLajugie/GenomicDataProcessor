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
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import gdpcore.ConfigurationManager;


/**
 * Panel 'Manage configuration' of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigManageConfig extends GdpConfigPanel {

	private static final long	serialVersionUID = -4500715706703181969L; // Generated serial number
	private JLabel 				jlSave;		// Label save
	private JButton 			jbSave;		// Button save
	private JLabel 				jlRestore;	// Label restore
	private JButton 			jbRestore;	// Button restore
	
	
	/**
	 * Constructor. Creates a panel GdpConfigManageConfig.
	 * @param aCM A ConfigurationManager
	 */
	public GdpConfigManageConfig(ConfigurationManager aCM) {
		super("Manage configuration", aCM);
		
		jlSave = new JLabel("Save configuration:");
		jbSave = new JButton("Save");
		jbSave.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					cm.writeConfig();
					JOptionPane.showMessageDialog(getRootPane(), "The configuration has been saved", "Configuration saved", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRootPane(), "Error while saving the configuration", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}				
			}
		});
		
		jlRestore = new JLabel("Restore default configuration:");
		jbRestore = new JButton("Restore");
		jbRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange("Restore", cm, new ConfigurationManager());
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlSave, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbSave, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlRestore, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbRestore, c);		
	}
}
