/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


/**
 * Main menu bar of the GDP.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpMenuBar extends JMenuBar {
	
	private static final long 	serialVersionUID = -7070520414167566879L; 			// Generated serial number
	private GdpMainFrame 		parentFrame;										// MainFrame
	private JMenu 				jmFile, jmFileImport, jmOperation, jmEdit,			// Menus
								jmFileExport, jmOptions;
	private JMenuItem 			jmiOpen, jmLoadSolexa, jmiLoadSolid, jmiLoadNimble, 	// Menu items
								jmiLoadBedGraph, jmiLoadGenes, jmiQuit, jmiSearchPeak, 
								jmiGauss, jmiDivideBy, jmiSubtract, jmiIndex, 
								jmiIndexByChromo, jmiLog, jmiLogDamper, jmiPreferences, 
								jmiExportBedGraph, jmiExportCSV, jmiRemove,	
								jmiReset, jmiDuplicate, jmiMax, jmiMin, jmiNormalize, 
								jmiRepartition, jmiCorrelation, jmiTrackConfiguration,
								jmiUndo, jmiRedo, jmiSaveAsImage, jmiAddDumper, 
								jmiRenameCurves, jmiShowHistory, jmiSelectAll,
								jmiLoadRepeats;
	private JCheckBoxMenuItem 	jcbmiShowStripes;					// CheckBoxes
	
	
	/**
	 * Constructor. Creates the menu bar.
	 * @param aParentFrame GdpMainFrame.
	 */
	public GdpMenuBar(GdpMainFrame aParentFrame) {
		parentFrame = aParentFrame;
		buildFileMenu();
		buildEditMenu();
		buildOperationMenu();
		buildOptionMenu();
	}


	/**
	 * Creates File menu.  
	 */
	private void buildFileMenu() {
		// Build the menu File.
		jmFile = new JMenu("File");
		jmFile.setMnemonic(KeyEvent.VK_F);
		add(jmFile);
		
		// Build the open menu
		jmiOpen = new JMenuItem("Open");
		jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		jmiOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadGdp();			
			}
		});
		jmFile.add(jmiOpen);
		
		// Build sub-menu Load.
		jmFileImport = new JMenu("Import data");
		jmFile.add(jmFileImport);
		
		// Build jmi Load BedGraph.
		jmiLoadBedGraph = new JMenuItem("from a BedGraph File");
		jmiLoadBedGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadBedGraph();			
			}
		});
		jmFileImport.add(jmiLoadBedGraph);

		// Build menu Load Solexa.
		jmLoadSolexa = new JMenuItem("from a Solexa file");
		jmLoadSolexa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadSolexa();			
			}
		});
		jmFileImport.add(jmLoadSolexa);	
		
		// Build jmi Load Solid.
		jmiLoadSolid = new JMenuItem("from a Solid file");
		jmiLoadSolid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadSolid();				
			}
		});
		jmFileImport.add(jmiLoadSolid);
		
		// Build sub-menu load Nimble.
		jmiLoadNimble = new JMenuItem("from a NimbleGene file");
		jmFileImport.add(jmiLoadNimble);
		// Build Jjmi load Nimble strand 5		
		jmiLoadNimble.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadNimble();				
			}
		});
		
		// Create menu load gene file
		jmiLoadGenes = new JMenuItem("Load gene file");
		jmiLoadGenes.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadGeneFile();
			}
		});
		jmFile.add(jmiLoadGenes);
		
		// Create menu load repeat file
		jmiLoadRepeats = new JMenuItem("Load repeat file");
		jmiLoadRepeats.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.loadRepeatFile();
			}
		});
		jmFile.add(jmiLoadRepeats);
		
		jmFile.addSeparator();
		
		// Build sub-menu Export
		jmFileExport = new JMenu("Export");
		jmFileExport.setEnabled(false);
		jmFile.add(jmFileExport);
		// Build jmi export BedGraph
		jmiExportBedGraph = new JMenuItem("BedGraph");
		jmiExportBedGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.exportBedGraphSelectedCurves();				
			}
		});
		jmiExportBedGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		jmFileExport.add(jmiExportBedGraph);
		// Build jmi export CSV
		jmiExportCSV = new JMenuItem("CSV");
		jmiExportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.exportCSVSelectedCurves();					
			}
		});	
		jmFileExport.add(jmiExportCSV);
		
		// Build Save as image
		jmiSaveAsImage = new JMenuItem("Save as image");
		jmiSaveAsImage.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.saveSelectedCurvesAsImages();				
			}
		});
		jmiSaveAsImage.setEnabled(false);
		jmFile.add(jmiSaveAsImage);
		
		jmFile.addSeparator();
		
		// Build jmi Quit
		jmiQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		jmiQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		jmiQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.dispose();				
			}
		});
		jmFile.add(jmiQuit);
	}
	
	
	/**
	 * Creates Edit menu.  
	 */
	private void buildEditMenu() {
		// Build the menu File.
		jmEdit = new JMenu("Edit");
		jmEdit.setMnemonic(KeyEvent.VK_E);
		add(jmEdit);
		
		// Build sub-menu Undo.
		jmiUndo = new JMenuItem("Undo");
		jmiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		jmiUndo.setEnabled(false);
		jmiUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.undoSelectedCuvesLastAction();
			}
		});
		jmEdit.add(jmiUndo);
		
		// Build sub-menu Redo.
		jmiRedo = new JMenuItem("Redo");
		jmiRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		jmiRedo.setEnabled(false);
		jmiRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.redoSelectedCuvesLastAction();	
			}
		});
		jmEdit.add(jmiRedo);
		
		jmEdit.addSeparator();
		
		// Build select all
		jmiSelectAll = new JMenuItem("Select All");
		jmiSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		jmiSelectAll.setEnabled(false);
		jmiSelectAll.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentFrame.selectAllCurves();				
			}
		});
		jmEdit.add(jmiSelectAll);
		
		// Build sub-menu jmiRenameCurve
		jmiRenameCurves = new JMenuItem("Rename Curves");
		jmiRenameCurves.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		jmiRenameCurves.setEnabled(false);
		jmiRenameCurves.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.renameSelectedCuves();	
			}
		});
		jmEdit.add(jmiRenameCurves);
		
		// Build jmi Duplicate
		jmiDuplicate = new JMenuItem("Duplicate");
		jmiDuplicate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		jmiDuplicate.setEnabled(false);
		jmiDuplicate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentFrame.duplicateSelectedCurves();
			}
		});
		jmEdit.add(jmiDuplicate);
		
		// Build jmi Reset		
		jmiReset = new JMenuItem("Reset");
		jmiReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		jmiReset.setEnabled(false);
		jmiReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentFrame.resetBinListSelectedCurves();
			}
		});
		jmEdit.add(jmiReset);
		
		// Build jmi Remove
		jmiRemove = new JMenuItem("Remove");
		jmiRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		jmiRemove.setEnabled(false);
		jmiRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentFrame.removeSelectedCurves();
			}
		});
		jmEdit.add(jmiRemove);
	}
	
	/**
	 * Creates Option menu.
	 */
	private void buildOptionMenu() {
		// Build the menu File.
		jmOptions = new JMenu("Options");
		jmOptions.setMnemonic(KeyEvent.VK_O);
		add(jmOptions);
		
		// Build jmi Option
		jmiPreferences = new JMenuItem("Preferences");
		jmiPreferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.showConfiguration();			
			}
		});
		jmOptions.add(jmiPreferences);
		
		// Build jmi track configuration
		jmiTrackConfiguration = new JMenuItem("Track configuration");
		jmiTrackConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.trackConfiguration();				
			}
		});
		jmiTrackConfiguration.setEnabled(false);
		jmOptions.add(jmiTrackConfiguration);		
			
		// Build JCBMI ShowGenes 
		jcbmiShowStripes = new JCheckBoxMenuItem("Show Stripes", false);
		jcbmiShowStripes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.showStripes(jcbmiShowStripes.getState());
				
			}
		});
		jcbmiShowStripes.setEnabled(false);
		jmOptions.add(jcbmiShowStripes);	
		
		jmOptions.addSeparator();
		
		// Build Show history
	 	jmiShowHistory = new JMenuItem("Show History");
	 	jmiShowHistory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
	 	jmiShowHistory.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.showHistorySelectedCurves();
			}
		});	 	
	 	jmiShowHistory.setEnabled(false);
	 	jmOptions.add(jmiShowHistory);
	}


	/**
	 * Creates Operation menu. 
	 */
	private void buildOperationMenu() {
		// Build the menu Operation.
		jmOperation = new JMenu("Operation");
		jmOperation.setMnemonic(KeyEvent.VK_P);
		jmOperation.setEnabled(false);
		add(jmOperation);
		
		// Build sub-menu Gauss.
		jmiGauss = new JMenuItem("Gauss");
		jmiGauss.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.gaussSelectedCurves();			
			}
		});
		jmOperation.add(jmiGauss);

		// Build sub-menu Index.
		jmiIndex = new JMenuItem("Index");
		jmiIndex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.indexSelectedCurves();			
			}
		});
		jmOperation.add(jmiIndex);
		
		// Build sub-menu Index by chromosome.
		jmiIndexByChromo = new JMenuItem("Index by chromosome");
		jmiIndexByChromo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.indexByChromoSelectedCurves();			
			}
		});
		jmOperation.add(jmiIndexByChromo);
		
		// Build sub-menu Log.
		jmiLog = new JMenuItem("Log2");
		jmiLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.log2SelectedCurves();			
			}
		});
		jmOperation.add(jmiLog);
		
		// Build sub-menu Log with damper.
		jmiLogDamper = new JMenuItem("Log2 with damper");
		jmiLogDamper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.log2DamperSelectedCurves();			
			}
		});
		jmOperation.add(jmiLogDamper);

		// Build sub-menu add damper.
		jmiAddDumper = new JMenuItem("Add damper");
		jmiAddDumper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.addDamperSelectedCurves();			
			}
		});
		jmOperation.add(jmiAddDumper);
		
		
		// Build sub-menu Normalize.
		jmiNormalize = new JMenuItem("Normalize");
		jmiNormalize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.normalizeSelectedCurves();			
			}
		});
		jmOperation.add(jmiNormalize);
		
		// Build sub-menu Repartition.
		jmiRepartition = new JMenuItem("Repartition");
		jmiRepartition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.repartitionSelectedCurves();			
			}
		});
		jmOperation.add(jmiRepartition);
		
		jmOperation.addSeparator();
		
		// Build sub-menu Min.
		jmiMin = new JMenuItem("Minimum");
		jmiMin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.binListMinSelectedCurves();			
			}
		});
		jmOperation.add(jmiMin);
		
		// Build sub-menu Maximum.
		jmiMax = new JMenuItem("Maximum");
		jmiMax.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.binListMaxSelectedCurves();			
			}
		});
		jmOperation.add(jmiMax);
		
		jmOperation.addSeparator();

		// Build sub-menu Divide.
		jmiDivideBy = new JMenuItem("Divide");
		jmiDivideBy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.divideBySelectedCurves();			
			}
		});
		jmOperation.add(jmiDivideBy);

		// Build sub-menu Subtract.
		jmiSubtract = new JMenuItem("Subtract");
		jmiSubtract.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.subtractSelectedCurves();			
			}
		});
		jmOperation.add(jmiSubtract);

		// Build sub-menu Subtract.
		jmiSearchPeak = new JMenuItem("Search Peak");
		jmiSearchPeak.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.searchPeakSelectedCurves();			
			}
		});
		jmOperation.add(jmiSearchPeak);
		
		// Build sub-menu Subtract.
		jmiCorrelation = new JMenuItem("Correlation");
		jmiCorrelation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.correlationSelectedCurves();			
			}
		});
		jmOperation.add(jmiCorrelation);		
	}
	
	
	/**
	 * Enables / disables the menus associated to curves.
	 * @param b True to enable / False to disable.
	 */
	public void setCurvesMenusEnable(boolean b) {
		jmOperation.setEnabled(b);
		jmiTrackConfiguration.setEnabled(b);
		jmFileExport.setEnabled(b);
		jmiSaveAsImage.setEnabled(b);
		jmiRenameCurves.setEnabled(b);
		jmiDuplicate.setEnabled(b);
		jmiReset.setEnabled(b);
		jmiRemove.setEnabled(b);
		jmiShowHistory.setEnabled(b);
	}


	/**
	 * Enables / Disables the menu select all 
	 * @param b True to enable / False to disable.
	 */
	public void set1BinListAtLeastEnable(boolean b) {
		jmiSelectAll.setEnabled(b);
	}

	
	/**
	 * Enables / Disables the menu show stripes 
	 * @param b True to enable / False to disable.
	 */
	public void set1TrackAtLeastEnable(boolean b) {
		jcbmiShowStripes.setEnabled(b);	
	}
	
	
	/**
	 * Checks / Unchecks the menu "Show Stripes". 
	 * @param b True to check / False to unckeck.
	 */
	public void setShowStripesChecked(boolean b) {
		jcbmiShowStripes.setState(b);		
	}
	
	
	/**
	 * Enables / Disables the menu "Undo". 
	 * @param b True to enable / False to disable.
	 */
	public void setUndoEnable(boolean b) {
		jmiUndo.setEnabled(b);		
	}
	
	
	/**
	 * Enables / Disables the menu "Redo". 
	 * @param b True to enable / False to disable.
	 */
	public void setRedoEnable(boolean b) {
		jmiRedo.setEnabled(b);		
	}
}
