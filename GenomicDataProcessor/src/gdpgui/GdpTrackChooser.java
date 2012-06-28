/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;


/**
 * A dialog box used to choose two tracks. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpTrackChooser extends JDialog {

	private static final long 		serialVersionUID = 2840205300507226959L;	// Generated serial number
	private static JLabel 			jl1;										// Label on top of the dialog
	private static JLabel 			jl2;										// Label in the middle of the dialog 
	private static JComboBox<String>jcbTrack1;									// ComboBox to choose the first track
	private static JComboBox<String>jcbTrack2;									// ComboBox to choose the second track
	private static JButton 			jbOk;										// OK button
	private static JButton 			jbCancel;									// Cancel button
	private static String[] 		optionStrings;								// List of available tracks 
	private static boolean 			validated;									// True if OK has been pressed
	private static String 			title;										// Title of the dialog
	private static String			label1;										// Text of the first label
	private static String			label2;										// Text of the second label
	
	
	/**
	 * Private constructor. Used internally to create a GdpTrackChooser dialog. 
	 * @param parentFrame The Frame from which the dialog is displayed.
	 * @param aTitle Title of the dialog.
	 * @param aLabel1 Text of the first label.
	 * @param aLabel2 Text of the second label.
	 * @param options List of track names.
	 */
	private GdpTrackChooser(Frame parentFrame, String aTitle, String aLabel1, String aLabel2, String[] options) {
		super(parentFrame, true);
		title = aTitle;
		label1 = aLabel1;
		label2 = aLabel2;
		optionStrings = options;
		validated = false;
		initComponent();	
		setTitle(title);
		setPreferredSize(new Dimension(300, 175));
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parentFrame);
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jl1 = new JLabel(label1);
		jcbTrack1 = new JComboBox<String>(optionStrings);
		
		jl2 = new JLabel(label2);
		jcbTrack2 = new JComboBox<String>(optionStrings);

		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER ;
		c.weightx = 0.5;
		c.weighty = 0.20;
		c.anchor = GridBagConstraints.CENTER;
		add(jl1, c);

		c.gridy = 1;
		add(jcbTrack1, c);

		c.gridy = 2;
		add(jl2, c);
		
		c.gridy = 3;
		add(jcbTrack2, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);		
	}

	
	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		this.dispose();
	}

	
	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected tracks.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		this.dispose();		
	}


	/**
	 * Only public function. Displays a GdpTrackChooser dialog, and returns an array of two strings 
	 * corresponding to the name of the two selected tracks.
	 * @param parentFrame The Frame from which the dialog is displayed.
	 * @param title Title of the dialog.
	 * @param label1 First label of the dialog.
	 * @param label2 Second label of the dialog.
	 * @param options A list of track names in which the user is going to choose two tracks. 
	 * @return An array of two strings corresponding to the name of the two selected tracks if
	 * the button OK has been pressed, else returns null.
	 */
	public static int[] getTracks(Frame parentFrame, String title, String label1, String label2, String[] options) {
		GdpTrackChooser CCOP = new GdpTrackChooser(parentFrame, title, label1, label2, options);
		CCOP.setVisible(true);	
		
		if(validated) {
			int[] returnArray = new int[2];
			returnArray[0] = jcbTrack1.getSelectedIndex();
			returnArray[1] = jcbTrack2.getSelectedIndex();
			return returnArray;
		}
		else
			return null;
	}

}
