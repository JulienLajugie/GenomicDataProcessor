/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import gdpcore.History;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * A history frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpHistoryFrame extends JFrame {

	private static final long serialVersionUID = 9059804292908454294L;
	private final int		HISTORY_FRAME_WIDTH = 500;		// default width
	private final int		HISTORY_FRAME_HEIGHT = 300;		// default height
	private JList<String> 	jlHistory;						// list containing the history
	private JScrollPane     jspHistory;						// scroll pane containing the history list 
	private JButton 		jbSave;							// save button
	private JButton 		jbClose;						// close button
	private final String 	curveName;						// name of a curve
	private final History 	history;						// history of a curve
	
	
	/**
	 * Public constructor. 
	 * @param parentFrame Frame in which showing the history.
	 * @param curveName Name of a curve.
	 * @param history History of a curve.
	 */
	public GdpHistoryFrame(Frame parentFrame, String curveName, History history) {
		super(curveName);
		this.curveName = curveName; 
		this.history = history;
		initComponent();
		setSize(HISTORY_FRAME_WIDTH, HISTORY_FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parentFrame);
		getRootPane().setDefaultButton(jbClose);
		setVisible(true);
	}
	
	
	/**
	 * Initializes the component and sub components.
	 */
	private void initComponent() {
		jlHistory = new JList<String>(history.get());
		jlHistory.setLayoutOrientation(JList.VERTICAL);
		jlHistory.setVisibleRowCount(history.size());
		
		jspHistory = new JScrollPane(jlHistory);
		
		jbClose = new JButton("Close");
		jbClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});	
		
		jbSave = new JButton("Save");
		jbSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				saveHistory();
			}
		});
		
		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 0.99;
		add(jspHistory, c);
		
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weighty = 0.01;
		add(jbSave, c);		
		
		c.gridx = 1;
		c.gridy = 1;
		add(jbClose, c);
	}
	
	
	/**
	 * Saves the history in a file.
	 */
	public void saveHistory() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save " + curveName + " history");
		jfc.setSelectedFile(new File(".txt"));
		int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if (!cancelBecauseFileExist(jfc.getSelectedFile())) {
				try {
					history.save(jfc.getSelectedFile());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRootPane(), "Error while saving the history", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Asks if the user wants to replace a file if this file already exists.
	 * @param f A file.
	 * @return True if the user wants to cancel. False otherwise.
	 */
	private boolean cancelBecauseFileExist(File f) {
		if (f.exists()) {
			int res = JOptionPane.showInternalConfirmDialog(getContentPane(), "The file " + f.getName() + " already exists. Do you want to replace the existing file?.", "File already exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if (res == JOptionPane.NO_OPTION) {
				return true;
			}
		}
		f.delete();
		return false;
	}
}
