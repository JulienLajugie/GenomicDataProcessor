/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;


import java.awt.*;

import javax.swing.*; 
import javax.swing.border.EtchedBorder;


/**
 * Progress bar displayed during the loading of a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpProgressBar extends JDialog {

	private static final long 	serialVersionUID = 8940806856659914685L;	// Generated serial number
	private final JLabel		jlWait;										// Label wait
	private final JProgressBar 	jpbWait;									// Progress bar
	
	/**
	 * Shows a progress bar displayed during the loading of a file.
	 * @param parentFrame Frame displaying the progressbar popup
	 */
	public GdpProgressBar(Frame parentFrame) {
		super(parentFrame);
		setResizable(false);
		setUndecorated(true);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		// Create the label
		jlWait = new JLabel("Loading in progress...");
		
		// Create the progress bar
		jpbWait = new JProgressBar();
		jpbWait.setIndeterminate(true);
		
		JPanel jp = new JPanel(new GridBagLayout());
		jp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
		// Add the components
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		jp.add(jlWait, c);
		
		c.gridx = 0;
		c.gridy = 1;
		jp.add(jpbWait, c);		
		
		add(jp);
		pack();
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}	
}

