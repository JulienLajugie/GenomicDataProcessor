/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.ConfigurationManager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


/**
 * Configuration frame used to change the configuration of the program.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpConfigMain extends JDialog implements TreeSelectionListener, PropertyChangeListener {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final long 		serialVersionUID = 4050757943368845382L; // Generated serial number
	private ConfigurationManager 	cm;				// A ConfigurationManager
	private JTree 					jt;				// Tree
	private JScrollPane 			jspTreeView; 	// Scroll pane containing the tree
	private JPanel 					jpOption;		// Panel containing the different panel of configuration
	private JButton					jbOk;			// Button OK
	private JButton					jbCancel;		// Button cancel
	private JSplitPane 				jspDivider;		// Divider between the tree and the panel
	private int						approved = CANCEL_OPTION;	// Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	
	
	/**
	 * Constructor. Creates a GdpConfigMain.
	 * @param owner Parent frame.
	 * @param aConfigurationManager	Configuration manager representing the present configuration.
	 */
	public GdpConfigMain(JFrame owner, ConfigurationManager aConfigurationManager) {
		super(owner, "Configuration", true);

		cm = aConfigurationManager.clone();
		setSize(new Dimension(cm.getGdpConfigPreferredWidth(), cm.getGdpConfigPreferredHeight()));
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		initComponent();		
		setLocationRelativeTo(owner);
		getRootPane().setDefaultButton(jbOk);
		
	}


	/**
	 * Initializes the component and subcomponents. 
	 */
	private void initComponent() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Options");;
		createNodes(top);
		jt = new JTree(top);
		// hide the root node
		jt.setRootVisible(false);
		// hide the lines
		jt.setShowsRootHandles(true);
		// Remove the icon from the tree
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		jt.setCellRenderer(renderer);
		jt.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jt.addTreeSelectionListener(this);
		jspTreeView = new JScrollPane(jt);
		jpOption = new JPanel();
		
		//Add the scroll panes to a split pane.
		jspDivider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jspDivider.setLeftComponent(jspTreeView);
		jspDivider.setBottomComponent(jpOption);

        Dimension minimumSize = new Dimension(cm.getGdpConfigMinimumSplitWidth(), 1);
        jspTreeView.setMinimumSize(minimumSize);
        jpOption.setMinimumSize(minimumSize);
        jspDivider.setDividerLocation(cm.getGdpConfigPreferredWidth() / 3); 

        jbOk = new JButton("OK");
        jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);				
			}
		});
        jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
        
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.99;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(jspDivider, c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.99;
		c.weighty = 0.01;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);
		
		c.weightx = 0.01;
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);
        
	}


	/**
	 * Creates the data of the tree.
	 * @param top Root DefaultMutableTreeNode of the tree.
	 */
	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode option = null;

		category = new DefaultMutableTreeNode(new GdpConfigGeneral(cm));
		top.add(category);
		
		category = new DefaultMutableTreeNode(new GdpConfigAppearance(cm));
		top.add(category);

		category = new DefaultMutableTreeNode("Loaders");
		top.add(category);

		option = new DefaultMutableTreeNode(new GdpConfigBedGraph(cm));
		category.add(option);

		option = new DefaultMutableTreeNode(new GdpConfigNimble(cm));
		category.add(option);

		option = new DefaultMutableTreeNode(new GdpConfigSolid(cm));
		category.add(option);

		option = new DefaultMutableTreeNode(new GdpConfigSolexa(cm));
		category.add(option);
		
		category = new DefaultMutableTreeNode(new GdpConfigManageConfig(cm));
		top.add(category);
	}


	/**
	 * Changes the panel displayed when the node of the tree changes.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		jt.getLastSelectedPathComponent();
		jpOption.removeAll();
		if ((node != null) && (node.isLeaf())) {
			Object nodeInfo = node.getUserObject();
			if (nodeInfo != null) {				
				jpOption.add((JPanel)nodeInfo);
				((JPanel)nodeInfo).addPropertyChangeListener(this);
			}
		}
		jpOption.revalidate();
		jpOption.repaint();
	}


	/**
	 * Shows the component.
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showConfigurationDialog() {
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
		setVisible(true);
		return approved;
	}
	
	
	/**
	 * @return The ConfigurationManager
	 */
	public ConfigurationManager getConfigurationManager() {
		return cm;
	}


	/**
	 * Restores the data and regenerate the tree when the option 
	 * restore configuration is clicked.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("Restore")) {
			cm = (ConfigurationManager)evt.getNewValue();
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("Options");
			createNodes(top);
			jt.setModel(new DefaultTreeModel(top));
			jt.revalidate();
		}		
	}
}
