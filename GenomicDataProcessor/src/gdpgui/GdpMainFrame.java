/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BedGraphFileExtractor;
import gdpcore.BinList;
import gdpcore.BinListNoDataException;
import gdpcore.ChromosomeList;
import gdpcore.ConfigurationManager;
import gdpcore.GdpFileExtractor;
import gdpcore.GeneFileExtractor;
import gdpcore.RepeatFileExtractor;
import gdpcore.StripeFileExtractor;
import gdpcore.NimbleFileExtractor;
import gdpcore.SolexaFileExtractor;
import gdpcore.SolidFileExtractor;
import gdpcore.BinList.IntensityCalculation;
import gdpgui.GdpBinListTrack.GraphicsType;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;



/**
 * Main frame of the application.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpMainFrame extends JFrame {

	/**
	 * Starts the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GdpMainFrame().setVisible(true);
			}
		});
	}

	private static final long serialVersionUID = 8413763896102640886L;	// Generated serial number
	private GdpMainPanel 					gdpMainPanel;			// Main panel of the application
	private GdpMenuBar 						gdpMenuBar;				// Menu bar of the application
	private ConfigurationManager			cm;						// Configuration manager	
	private ChromosomeList 					chromoConfig;			// Chromosome configuration
	private StripeFileExtractor				sfe = null;				// A stripe file

	/**
	 * Constructor. Initializes the component.
	 */
	public GdpMainFrame () {
		super("Genomic Data Processor");
		// Create the configuration manager
		cm = new ConfigurationManager();
		try {
			cm.readConfig();
			chromoConfig = new ChromosomeList(cm.getChromoConfigFile());
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(getContentPane(), "Error while loading the configuration file", "Incorrect file", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}

		//Load the look and feel
		changeLookAndFeel();

		// Create the menu bar
		gdpMenuBar = new GdpMenuBar(this);
		gdpMainPanel = new GdpMainPanel(cm, chromoConfig);
		gdpMainPanel.addPropertyChangeListener("Curve state changes", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				selectedCurvesChange();				
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(gdpMenuBar);
		setMinimumSize(new Dimension(600, 400));
		setPreferredSize(new Dimension(800, 600));
		add(gdpMainPanel);
		pack();
		this.setLocationByPlatform(true);
		setVisible(true);		
	}


	/**
	 * Changes the look and feel of the application
	 */
	private void changeLookAndFeel() {
		try {			
			UIManager.setLookAndFeel(cm.getLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Recursive function.
	 * If the name in of the parameter <i>name</i> is not unique 
	 * the function add (<i>number</i>) at the end of the name
	 * and check if this new name is unique. If not this function
	 * self calls with number + 1.
	 * @param name Name of a new curve. 
	 * @param number Number to add at the end of the name.
	 * @return A unique name.
	 */
	private String findUniqueName(String name, int number) {
		String currentName;
		if(number != 0) {
			currentName = new String(name + "(" + number + ")");
		} else {
			currentName = new String(name);
		}
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
		for(GdpBinListTrackContainer gdpcc : list) {
			if(gdpcc.getTrackName().equals(currentName)) {	
				return findUniqueName(name, ++number);
			}
		}
		return currentName;		
	}


	/**
	 * Asks the user to enter a name for a curve.
	 * Proposed the user aName as default name 
	 * Make this name unique by adding '(#)' when necessary. 
	 * @param aName proposed name.
	 * @return A unique name for a curve, null if canceled.
	 */
	private String getTrackName(String aName) {
		String curveName = null;
		// Ask the user to give a name to the curve.
		while ((curveName == null) || (curveName.trim().length() == 0)) {
			curveName = (String)JOptionPane.showInputDialog(getRootPane(), "Enter a name for the curve:", "Curve name", JOptionPane.QUESTION_MESSAGE, null, null, findUniqueName(aName, 0));
			// Case the user canceled				
			if(curveName == null) {
				return null;
			}
		}
		// We want the name of the curve to be unique
		curveName = curveName.trim();
		curveName = findUniqueName(curveName, 0);
		return curveName;
	}


	/**
	 * Enables / disables some menus depending if there is at least one curve selected.
	 */
	private void selectedCurvesChange() {
		if(gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC().size() == 0) {
			gdpMenuBar.setCurvesMenusEnable(false);
		} else {
			gdpMenuBar.setCurvesMenusEnable(true);
		}
		setUndoRedoEnableDisable();
	}


	/**
	 * @param type File type to open. Used for the title of the JFileChooser.
	 * @return A file to open. Null if none.
	 */
	private String chooseFileToLoad(String type) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Open " + type + " file");		
		int returnVal = jfc.showOpenDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().toString();
		} else {
			return null;
		}
	}


	/**
	 * Disables the main windows before the loading of a file.
	 */
	private void loadingFileStart() {
		setEnabled(false);
	}	


	/**
	 * Adds the curve after the loading of a file.
	 * @param worker
	 */
	private void loadingFileDone(SwingWorker<BinList, Void> worker, String addressFile) {
		setEnabled(true);
		// "new File(addressFile).getName()" extracts the file name without the path
		String curveName = getTrackName(new File(addressFile).getName());
		if (curveName != null) {
			try {	
				gdpMainPanel.addBinListTrack(cm, worker.get(), curveName);
				System.gc();
				gdpMenuBar.set1BinListAtLeastEnable(true);
				gdpMenuBar.set1TrackAtLeastEnable(true);
				refreshStripes();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(getContentPane(), "Error while loading the file", "Incorrect file", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}	
		}
	}

	
	/**
	 * Loads a Gdp file.
	 */
	public void loadGdp() {
		final String addressFile = chooseFileToLoad("Gdp");
		if (addressFile != null) {
				final GdpProgressBar progressBar = new GdpProgressBar(this);
				SwingWorker<BinList, Void> worker = new SwingWorker<BinList, Void>() {		
					@Override
					protected BinList doInBackground() throws Exception {
						loadingFileStart();
						GdpFileExtractor gfe = new GdpFileExtractor(null, chromoConfig, addressFile);
						BinList bl = gfe.getBinList();
						return bl;
					}

					@Override
					protected void done() {
						progressBar.dispose();
						loadingFileDone(this, addressFile);
					}

				};	
				worker.execute();
		}		
	}

	/**
	 * Loads a Solid file.
	 */
	public void loadSolid() {
		final String addressFile = chooseFileToLoad("Solid");
		if (addressFile != null) {
			final Integer aWindowSize = GdpNumberOptionPane.getValueWindow(this, cm.getWindowSize());
			if(aWindowSize != null) {
				final GdpProgressBar progressBar = new GdpProgressBar(this);
				SwingWorker<BinList, Void> worker = new SwingWorker<BinList, Void>() {		
					@Override
					protected BinList doInBackground() throws Exception {
						loadingFileStart();
						SolidFileExtractor sfe = new SolidFileExtractor(cm.getSolidLogFile(), chromoConfig, addressFile);
						BinList bl = sfe.getBinList(aWindowSize);
						return bl;
					}

					@Override
					protected void done() {
						progressBar.dispose();
						loadingFileDone(this, addressFile);
					}

				};	
				worker.execute();
			}
		}
	}


	/**
	 * Loads a BedGraph file.
	 */
	public void loadBedGraph() {
		final String addressFile = chooseFileToLoad("BedGraph");
		if (addressFile != null) {
			final Integer aWindowSize = GdpNumberOptionPane.getValueWindow(this, cm.getWindowSize());
			if(aWindowSize != null) {
				final GdpProgressBar progressBar = new GdpProgressBar(this);
				SwingWorker<BinList, Void> worker = new SwingWorker<BinList, Void>() {			
					@Override
					protected BinList doInBackground() throws Exception {
						loadingFileStart();
						BedGraphFileExtractor bgfe = new BedGraphFileExtractor(cm.getBedGraphLogFile(), chromoConfig, addressFile);
						// TODO: option intensity configuration
						BinList bl = bgfe.getBinList(aWindowSize, IntensityCalculation.sum);
						return bl;
					}

					@Override
					protected void done() {
						progressBar.dispose();
						loadingFileDone(this, addressFile);
					}
				};

				worker.execute();
			}
		}
	}


	/**
	 * Loads a NimbleGene file.
	 */
	public void loadNimble() {
		final String addressFile  = chooseFileToLoad("NimbleGene");
		if (addressFile != null) {
			final Integer aWindowSize = GdpNumberOptionPane.getValueWindow(this, cm.getWindowSize());
			if(aWindowSize != null) {	
				final GdpProgressBar progressBar = new GdpProgressBar(this);
				SwingWorker<BinList, Void> worker = new SwingWorker<BinList, Void>() {			
					@Override
					protected BinList doInBackground() throws Exception {
						loadingFileStart();
						NimbleFileExtractor nfe = new NimbleFileExtractor(cm.getNimbleLogFile(), chromoConfig, addressFile);
						BinList bl = nfe.getBinList(aWindowSize, BinList.IntensityCalculation.average);
						return bl;
					}

					@Override
					protected void done() {
						progressBar.dispose();
						loadingFileDone(this, addressFile);
					}
				};

				worker.execute();
			}
		}
	}


	/**
	 * Loads a Solexa file.
	 */
	public void loadSolexa() {
		final String addressFile  = chooseFileToLoad("Solexa");
		if (addressFile != null) {
			final Integer aWindowSize = GdpNumberOptionPane.getValueWindow(this, cm.getWindowSize());
			if(aWindowSize != null) {
				final GdpProgressBar progressBar = new GdpProgressBar(this);
				SwingWorker<BinList, Void> worker = new SwingWorker<BinList, Void>() {			
					@Override
					protected BinList doInBackground() throws Exception {
						loadingFileStart();
						SolexaFileExtractor sfe = new SolexaFileExtractor(cm.getSolexaLogFile(), chromoConfig, addressFile);
						BinList bl = sfe.getBinList(aWindowSize);
						return bl;
					}

					@Override
					protected void done() {
						progressBar.dispose();
						loadingFileDone(this, addressFile);
					}
				};
				worker.execute();
			}
		}
	}


	/**
	 * Applies a gaussian filter to each selected curve.
	 */
	public void gaussSelectedCurves() {

		int maxWindow = 0;
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			int currentWindow = aCCP.getTrack().getBinList().getWindowSize();
			if(currentWindow > maxWindow)
				maxWindow = currentWindow;
		}
		if(maxWindow > 0) {
			Integer sigma = GdpGenomicWidthChooser.getSigma(this, maxWindow);
			if(sigma != null) {
				for(GdpBinListTrackContainer aCCP : list) {
					this.setEnabled(false);
					aCCP.getTrack().gauss(sigma);
					this.setEnabled(true);
				}
				setUndoRedoEnableDisable();
			}
		}
	}


	/**
	 * Divides a curve by another one. Creates a new curve from the result of the division.
	 */
	public void divideBySelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			String[] options = new String[list.size()];
			for(int i = 0; i < list.size(); i++) {
				options[i] = list.get(i).getTrackName();				
			}
			int[] binListsIndex = GdpTrackChooser.getTracks(this, "Select tracks to Divide", "Divide", "by", options);
			if(binListsIndex != null) {
				BinList binList = list.get(binListsIndex[1]).getTrack().getBinList();
				Number filter = GdpNumberOptionPane.getValue(this, "Filter", "Enter a value for the filter:", new DecimalFormat("0"), 0, 1000, 10);
				if(filter != null) {
					BinList resBinList = null;
					int normalized = JOptionPane.showConfirmDialog(getRootPane(), "Do you want the division to be to normalized");
					if (normalized == JOptionPane.YES_OPTION) {
						resBinList = list.get(binListsIndex[0]).getTrack().divideBy(binList, filter.intValue(), true);
					} else {
						resBinList = list.get(binListsIndex[0]).getTrack().divideBy(binList, filter.intValue(), false);
					}
					if (resBinList != null) {
						String curveName = getTrackName(list.get(binListsIndex[0]).getTrackName() + "-divided-by-" + list.get(binListsIndex[1]).getTrackName());
						if (curveName != null) {
							gdpMainPanel.addBinListTrack(cm, resBinList, curveName);
						}
					}
				}
			}
		}
	}


	/**
	 * subtracts a curve from another one. Creates a new curve from the result of the division.
	 */
	public void subtractSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			String[] options = new String[list.size()];
			for(int i = 0; i < list.size(); i++) {
				options[i] = list.get(i).getTrackName();				
			}
			int[] binListsIndex = GdpTrackChooser.getTracks(this, "Select trakcs to subtract", "Subtract", "from", options);
			if(binListsIndex != null) {
				BinList binList = list.get(binListsIndex[0]).getTrack().getBinList();
				BinList resBinList = list.get(binListsIndex[1]).getTrack().minus(binList);
				if (resBinList != null) { 
					String curveName = getTrackName(list.get(binListsIndex[1]).getTrackName() + "-minus-" + list.get(binListsIndex[0]).getTrackName());
					if (curveName != null) {
						gdpMainPanel.addBinListTrack(cm, resBinList, curveName);
					}
				}
			}
		}
	}


	/**
	 * Indexes the intensities of the selected curves.
	 */
	public void indexSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			Number saturation = GdpNumberOptionPane.getValue(this, "Saturation:", "Enter a value for the saturation:", new DecimalFormat("0.0"), 0, 100, 1);
			if(saturation != null) {
				Number indexMin = GdpNumberOptionPane.getValue(this, "Minimum", "Enter minimum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
				if (indexMin != null) {
					Number indexMax = GdpNumberOptionPane.getValue(this, "Maximum", "Enter the maximum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
					if(indexMax != null) {
						for(GdpBinListTrackContainer aCCP : list) {
							aCCP.getTrack().index(saturation.doubleValue(), indexMin.doubleValue(), indexMax.doubleValue());
							aCCP.setMinMaxY(indexMin.doubleValue(), indexMax.doubleValue());
						}
						setUndoRedoEnableDisable();
					}
				}
			}
		}
	}


	/**
	 * Indexes the intensity values of the selected curves by chromosome.
	 */
	public void indexByChromoSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			Number saturation = GdpNumberOptionPane.getValue(this, "Saturation", "Enter a value for the saturation:", new DecimalFormat("0.0"), 0, 100, 1);
			if(saturation != null) {
				Number indexMin = GdpNumberOptionPane.getValue(this, "Minimum", "Enter minimum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
				if (indexMin != null) {
					Number indexMax = GdpNumberOptionPane.getValue(this, "Maximum", "Enter the maximum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
					if(indexMax != null) {
						for(GdpBinListTrackContainer aCCP : list) {
							aCCP.getTrack().indexByChromo(saturation.doubleValue(), indexMin.doubleValue(), indexMax.doubleValue());
							aCCP.setMinMaxY(indexMin.doubleValue(), indexMax.doubleValue());
						}
						setUndoRedoEnableDisable();
					}
				}
			}
		}
	}


	/**
	 * Logs with a damper the values of the selected curves and adds the average.
	 */
	public void log2DamperSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			Number damper = GdpNumberOptionPane.getValue(this, "Damper", "Enter a value for damper in: f(x)=log2(x + damper) - log2(avg + damper)", new DecimalFormat("0.0"), 0, 1000, 100);
			if(damper != null) {
				for(GdpBinListTrackContainer aCCP : list) {
					aCCP.getTrack().log(damper.doubleValue());
					try {
						aCCP.setMinMaxY(aCCP.getTrack().getBinList().min(), aCCP.getTrack().getBinList().max());
					} catch (BinListNoDataException e) {
						e.printStackTrace();
					}
				}
				setUndoRedoEnableDisable();
			}
		}
	}


	/**
	 * Logs the values of the selected curves.
	 */
	public void log2SelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			for(GdpBinListTrackContainer aCCP : list) {
				aCCP.getTrack().log();
				try {
					aCCP.setMinMaxY(aCCP.getTrack().getBinList().min(), aCCP.getTrack().getBinList().max());
				} catch (BinListNoDataException e) {
					e.printStackTrace();
				}
			}
			setUndoRedoEnableDisable();
		}
	}


	/**
	 * Adds a damper to the selected curves.
	 */
	public void addDamperSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			Number damper = GdpNumberOptionPane.getValue(this, "Damper", "Enter a value for damper to add: f(x)=x + damper", new DecimalFormat("0.0"), 0, 1000, 10);
			if(damper != null) {
				for(GdpBinListTrackContainer aCCP : list) {
					aCCP.getTrack().addDamper(damper.doubleValue());
					try {
						aCCP.setMinMaxY(aCCP.getTrack().getBinList().min(), aCCP.getTrack().getBinList().max());
					} catch (BinListNoDataException e) {
						e.printStackTrace();
					}
				}
				setUndoRedoEnableDisable();
			}
		}
	}


	/**
	 * Saves the selected files as CSV files.
	 */
	public void exportCSVSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			JFileChooser saveFC = new JFileChooser();
			saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			saveFC.setDialogTitle("Save " + aCCP.getTrackName());
			saveFC.setSelectedFile(new File(aCCP.getTrackName() + ".csv"));
			int returnVal = saveFC.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (!cancelBecauseFileExist(saveFC.getSelectedFile())) {
					gdpMenuBar.setEnabled(false);
					aCCP.getTrack().printCSV(saveFC.getSelectedFile().toString());
					gdpMenuBar.setEnabled(true);
				}
			}
		}
	}


	/**
	 * Saves the selected files as BedGraph files.
	 */
	public void exportBedGraphSelectedCurves() {

		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			JFileChooser saveFC = new JFileChooser();
			saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			saveFC.setDialogTitle("Save curve " + aCCP.getTrackName());
			saveFC.setSelectedFile(new File(aCCP.getTrackName() + ".txt"));
			int returnVal = saveFC.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (!cancelBecauseFileExist(saveFC.getSelectedFile())) {
					gdpMenuBar.setEnabled(false);
					aCCP.getTrack().printWiggle(saveFC.getSelectedFile().toString(), aCCP.getTrackName());
					gdpMenuBar.setEnabled(true);
				}
			}
		}
	}


	/**
	 * Removes the selected curves.
	 */
	public void removeSelectedCurves() {
		gdpMainPanel.getGdpListOfTrackContainers().removeSelectedBinListTC();
		if (gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC().size() == 0) {
			gdpMenuBar.set1BinListAtLeastEnable(false);
			selectedCurvesChange();
		}
		if (gdpMainPanel.getGdpListOfTrackContainers().isEmpty()) {
			gdpMainPanel.reset();
		}		
	}


	/**
	 * Resets the selected curves.
	 */
	public void resetBinListSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			aCCP.getTrack().resetBinList();
			try {
				aCCP.setMinMaxY(aCCP.getTrack().getBinList().min(), aCCP.getTrack().getBinList().max());
			} catch (BinListNoDataException e) {
				e.printStackTrace();
			}
		}
		setUndoRedoEnableDisable();
	}


	/**
	 * Duplicates the selected curves.
	 */
	public void duplicateSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			Number copyCountNum = GdpNumberOptionPane.getValue(this, "Copy", "Enter the number of copy for \"" + aCCP.getTrackName() + "\":", new DecimalFormat("0"), 0, 10, 1);
			if (copyCountNum != null) {
				int copyCount = copyCountNum.intValue();
				for (int i = 0; i < copyCount; i++) {
					String curveName = getTrackName(aCCP.getTrackName());
					if (curveName != null) {
						gdpMainPanel.addBinListTrack(cm, aCCP.getTrack().getBinList(), curveName);
					}
				}
			}
		}
	}


	/**
	 * Displays the smallest value of the selected curves.
	 */
	public void binListMinSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			double min = aCCP.getTrack().minBinList();
			JOptionPane.showMessageDialog(getRootPane(), min, "Minimum of \"" + aCCP.getTrackName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Displays the greatest value of the selected curves.
	 */
	public void binListMaxSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			double max = aCCP.getTrack().maxBinList();
			JOptionPane.showMessageDialog(this, max, "Maximum of \"" + aCCP.getTrackName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Normalizes the selected curves.
	 */
	public void normalizeSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if (list.size() > 0) {
			Number factor = GdpNumberOptionPane.getValue(this, "Multiplicative constant", "Enter a factor of X:", new DecimalFormat("0"), 0, 1000000000, 1000000);
			if (factor != null) {
				for (GdpBinListTrackContainer gdpcc : list) {
					gdpcc.getTrack().normalize(factor.intValue());
				}
				setUndoRedoEnableDisable();
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


	/**
	 * Generates a file showing the repartition of the intensity values of a curve.
	 */
	public void repartitionSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if (list.size() > 0 ) {
			Number intensityBin = GdpNumberOptionPane.getValue(this, "Size", "Enter the size of the bin of intensity:", new DecimalFormat("0.0"), 0, 1000, 1);
			if (intensityBin != null) {
				for (GdpBinListTrackContainer gdpcc : list) {
					JFileChooser saveFC = new JFileChooser();
					saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
					saveFC.setDialogTitle("Bin repartition " + gdpcc.getTrackName());
					saveFC.setSelectedFile(new File(".csv"));
					int returnVal = saveFC.showSaveDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						if (!cancelBecauseFileExist(saveFC.getSelectedFile())) {
							gdpMenuBar.setEnabled(false);						
							gdpcc.getTrack().repartition(intensityBin.doubleValue(), saveFC.getSelectedFile().toString());
							gdpMenuBar.setEnabled(true);
						}
					}
				}
			}
		}
	}


	/**
	 * Computes the correlation between to BinList.
	 */
	public void correlationSelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if (list.size() > 0) {
			String[] options = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				options[i] = list.get(i).getTrackName();				
			}
			int[] binListsIndex = GdpTrackChooser.getTracks(this, "Select tracks for the correlation", "Correlation between", "and", options);
			if (binListsIndex != null) {
				boolean[] selectedChromo = GdpChromosomeChooser.getSelectedChromo(this, chromoConfig);
				if (selectedChromo != null) {
					BinList binList = list.get(binListsIndex[1]).getTrack().getBinList();
					list.get(binListsIndex[0]).getTrack().correlation(binList, selectedChromo);			
				}
			}
		}
	}


	/**
	 * Shows the GdpTrackConfiguration frame and configures the selected curves.
	 */
	public void trackConfiguration() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		if(list.size() > 0) {
			Boolean showHGrid = list.get(0).getTrack().getShowHorizontalGrid();
			Integer preferredHeight = list.get(0).getPreferredSize().height;
			Integer lineX = list.get(0).getTrack().getLineX();
			Double lineY = list.get(0).getTrack().getLineY();	
			Color curvesColor = list.get(0).getTrack().getTrackColor();
			GraphicsType graphicsType = list.get(0).getTrack().getGraphicsType();
			for (GdpBinListTrackContainer gdpcc : list) {
				if (gdpcc.getPreferredSize().height != preferredHeight) {
					preferredHeight = null;
				}
				if ((lineX != null) && (!gdpcc.getTrack().getLineX().equals(lineX))) {
					lineX = null;
				}
				if (gdpcc.getTrack().getLineY() != lineY) {
					lineY = null;
				}	
				if (gdpcc.getTrack().getShowHorizontalGrid() != showHGrid) {
					showHGrid = null;
				}
				if (gdpcc.getTrack().getTrackColor() != curvesColor) {
					curvesColor = null;
				}
				if (gdpcc.getTrack().getGraphicsType() != graphicsType) {
					graphicsType = null;
				}
			}
			GdpTrackConfiguration tcf = new GdpTrackConfiguration(this, cm);
			int res = tcf.showTrackConfiguration(showHGrid, preferredHeight, lineX, lineY, curvesColor, graphicsType);
			if (res == GdpTrackConfiguration.APPROVE_OPTION) {
				for(GdpBinListTrackContainer gdpcc : list) {
					if (tcf.getPreferredHeight() != null) {
						gdpcc.setPreferredHeight(tcf.getPreferredHeight());
					}
					if (tcf.getShowHorizontalGrid() != null) {
						gdpcc.getTrack().setShowHorizontalGrid(tcf.getShowHorizontalGrid());
					}
					if (tcf.getCurvesColor() != null) {
						gdpcc.getTrack().setTrackColor(tcf.getCurvesColor());
					}
					if (tcf.getGraphicsType() != null) {
						gdpcc.getTrack().setGraphicsType(tcf.getGraphicsType());
					}
					gdpcc.getTrack().setXLine(tcf.getXLine());
					gdpcc.getTrack().setYLine(tcf.getYLine());
				}
			}
		}
	}


	/**
	 * Opens the configuration frame.
	 */
	public void showConfiguration() {
		GdpConfigMain conf = new GdpConfigMain(this, cm);
		int res = conf.showConfigurationDialog();
		if (res == GdpTrackConfiguration.APPROVE_OPTION) {
			if (!cm.equals(conf.getConfigurationManager())) {
				ConfigurationManager oldCm = cm;
				cm = conf.getConfigurationManager();
				if (! cm.getLookAndFeel().equals(oldCm.getLookAndFeel())) {
					changeLookAndFeel();
				}
				if (! cm.getGdpCurveGraphicGraphicsType().equals(oldCm.getGdpCurveGraphicGraphicsType())) {
					changeGraphicsType();
				}
				if (cm.getGdpCurveContainerPreferredHeight() != oldCm.getGdpCurveContainerPreferredHeight()) {
					for (GdpBinListTrackContainer gcc : gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC()) {
						gcc.setPreferredHeight(cm.getGdpCurveContainerPreferredHeight());
					}
				}
				if (cm.getGdpGeneContainerPreferredHeight() != oldCm.getGdpGeneContainerPreferredHeight()) {
					for (GdpTrackContainer trackContainer : gdpMainPanel.getGdpListOfTrackContainers().getAllGenericTC()) {
						trackContainer.setPreferredHeight(cm.getGdpGeneContainerPreferredHeight());
					}
				}
			}
		}

	}


	/**
	 * changes the type of graphics for each curve panel.
	 */
	private void changeGraphicsType() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
		for (GdpBinListTrackContainer aCCP : list)  {
			aCCP.getTrack().setGraphicsType(cm.getGdpCurveGraphicGraphicsType());
		}
	}


	/**
	 * Creates new curves containing only peaks.
	 */
	public void searchPeakSelectedCurves() {
		int maxWindow = 0;
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			int currentWindow = aCCP.getTrack().getBinList().getWindowSize();
			if(currentWindow > maxWindow)
				maxWindow = currentWindow;
		}
		if(maxWindow > 0) {
			Integer sizeMovingSD = GdpGenomicWidthChooser.getMovingStdDevWidth(this, maxWindow);
			if(sizeMovingSD != null) {
				Number nbSDAccepted = GdpNumberOptionPane.getValue(this, "Threshold", "Select only peak with a local SD x time higher than the global one", new DecimalFormat("0.0"), 0, 1000, 1); 
				if(nbSDAccepted != null) {
					for(GdpBinListTrackContainer aCCP : list) {
						String curveName = getTrackName("Peaks-from-" + aCCP.getTrackName());
						if (curveName != null) {
							gdpMainPanel.addBinListTrack(cm, aCCP.getTrack().searchPeaks(aCCP.getTrack().getBinList(), sizeMovingSD, nbSDAccepted.doubleValue()), curveName);
							refreshStripes();
						}
					}
				}
			}
		}
	}


	/**
	 * Undoes the last action performed on the selected curve.
	 */
	public void undoSelectedCuvesLastAction() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			aCCP.getTrack().undo();
		}	
		setUndoRedoEnableDisable();
	}


	/**
	 * Redoes the last action performed on the selected curve.
	 */
	public void redoSelectedCuvesLastAction() {
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			aCCP.getTrack().redo();
		}	
		setUndoRedoEnableDisable();
	}


	/**
	 * Enables or disables the menus Undo / Redo. 
	 */
	public void setUndoRedoEnableDisable() {
		gdpMenuBar.setUndoEnable(false);
		gdpMenuBar.setRedoEnable(false);
		ArrayList<GdpBinListTrackContainer> list = this.gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			if (aCCP.getTrack().isUndoable()) {
				gdpMenuBar.setUndoEnable(true);
			}
			if (aCCP.getTrack().isRedoable()) {
				gdpMenuBar.setRedoEnable(true);
			}
		}
	}


	/**
	 * Saves the selected curves as JPG images.
	 */
	public void saveSelectedCurvesAsImages() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			JFileChooser saveFC = new JFileChooser();
			saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			saveFC.setDialogTitle("Save " + aCCP.getTrackName() + " as a JPG image");
			saveFC.setSelectedFile(new File(".jpg"));
			int returnVal = saveFC.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (!cancelBecauseFileExist(saveFC.getSelectedFile())) {
					aCCP.getTrack().saveAsImage(saveFC.getSelectedFile().toString());
				}
			}
		}
	}


	/**
	 * Renames the selected curves.
	 */
	public void renameSelectedCuves() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for(GdpBinListTrackContainer aCCP : list) {
			if (aCCP.isSelected()) {
				String curveName = null;
				// Ask the user to give a name to the curve.
				while ((curveName == null) || (curveName.trim().length() == 0)) {
					curveName = (String)JOptionPane.showInputDialog(getRootPane(), "Enter a new name for the curve:", "Curver name", JOptionPane.QUESTION_MESSAGE, null, null, aCCP.getTrackName());
					// Case the user canceled				
					if(curveName == null) {
						return;
					}
				}
				// We want the name of the curve to be unique
				curveName = curveName.trim();
				curveName = findUniqueName(curveName, 0);
				aCCP.setTrackName(curveName);
			}
		}
	}


	/**
	 * Loads a stripe file if <i>state</i> is true or hides the stripes if <i>state</i> is false.
	 * @param state 
	 */
	public void showStripes(boolean state) {
		if (gdpMainPanel != null) {
			if (state) {
				JFileChooser jfcStripes = new JFileChooser();
				jfcStripes.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfcStripes.setDialogTitle("Stripe file");
				int returnVal = jfcStripes.showOpenDialog(getRootPane());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						sfe = new StripeFileExtractor(chromoConfig, jfcStripes.getSelectedFile().toString());
						ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
						for(GdpBinListTrackContainer aCCP : list) {
							aCCP.getTrack().setStripes(sfe);
						}
						for (GdpTrackContainer trackContainer : gdpMainPanel.getGdpListOfTrackContainers().getAllGenericTC()) {
							trackContainer.getTrack().setStripes(sfe);;
						}
					} catch (Exception e) {
						sfe = null;
						refreshStripes();
						JOptionPane.showMessageDialog(getRootPane(), "Stripe file incorrect", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						gdpMenuBar.setShowStripesChecked(false);
					} 
				} else {
					sfe = null;
					refreshStripes();
					gdpMenuBar.setShowStripesChecked(false);
				}
			} else {
				sfe = null;
				ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
				for(GdpBinListTrackContainer aCCP : list) {
					aCCP.getTrack().removeStripes();
				}
				for (GdpTrackContainer trackContainer : gdpMainPanel.getGdpListOfTrackContainers().getAllGenericTC()) {
					trackContainer.getTrack().removeStripes();;
				}
			}
		}
	}


	/**
	 * Refreshes the stripe displayed.
	 */
	private void refreshStripes() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
		if (sfe == null) {
			for(GdpBinListTrackContainer aCCP : list) {
				aCCP.getTrack().removeStripes();
			}
			for (GdpTrackContainer trackContainer : gdpMainPanel.getGdpListOfTrackContainers().getAllGenericTC()) {
				trackContainer.getTrack().removeStripes();;
			}
		} else {
			for(GdpBinListTrackContainer aCCP : list) {
				aCCP.getTrack().setStripes(sfe);
			}
			for (GdpTrackContainer trackContainer : gdpMainPanel.getGdpListOfTrackContainers().getAllGenericTC()) {
				trackContainer.getTrack().setStripes(sfe);
			}
		}

	}


	/**
	 * Shows the history of the selected curves.
	 */
	public void showHistorySelectedCurves() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getSelectedBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			new GdpHistoryFrame(this, aCCP.getTrackName(), aCCP.getTrack().getHistory());
		}

	}


	/**
	 * Selects every curve.
	 */
	public void selectAllCurves() {
		ArrayList<GdpBinListTrackContainer> list = gdpMainPanel.getGdpListOfTrackContainers().getAllBinListTC();
		for (GdpBinListTrackContainer aCCP : list) {
			aCCP.select();
		}
	}


	/**
	 * Loads a gene file.
	 */
	public void loadGeneFile() {
		final String addressFile  = chooseFileToLoad("gene");
		if (addressFile != null) {
			final GdpProgressBar progressBar = new GdpProgressBar(this);
			SwingWorker<GeneFileExtractor, Void> worker = new SwingWorker<GeneFileExtractor, Void>() {			
				@Override
				protected GeneFileExtractor doInBackground() throws Exception {
					setEnabled(false);
					GeneFileExtractor gfe = new GeneFileExtractor(chromoConfig, addressFile);
					return gfe;
				}

				@Override
				protected void done() {					
					setEnabled(true);
					progressBar.dispose();
					try {
						gdpMainPanel.addGeneTrack(this.get(), cm, addressFile);
						refreshStripes();
						gdpMenuBar.set1TrackAtLeastEnable(true);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(getRootPane(), "Error while adding the gene track", "Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			};
			worker.execute();
		}
	}


	/**
	 * Loads a repeat file
	 */
	public void loadRepeatFile() {
		final String addressFile  = chooseFileToLoad("repeat");
		if (addressFile != null) {
			final GdpProgressBar progressBar = new GdpProgressBar(this);
			SwingWorker<RepeatFileExtractor, Void> worker = new SwingWorker<RepeatFileExtractor, Void>() {			
				@Override
				protected RepeatFileExtractor doInBackground() throws Exception {
					setEnabled(false);
					RepeatFileExtractor rfe = new RepeatFileExtractor(chromoConfig, addressFile);
					return rfe;
				}

				@Override
				protected void done() {
					setEnabled(true);
					progressBar.dispose();
					try {
						gdpMainPanel.addRepeatTrack(this.get(), cm, addressFile);
						refreshStripes();
						gdpMenuBar.set1TrackAtLeastEnable(true);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(getRootPane(), "Error while adding the repeat track", "Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			};
			worker.execute();
		}
	}
}

