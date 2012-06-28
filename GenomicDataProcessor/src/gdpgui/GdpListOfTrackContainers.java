/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpgui;

import gdpcore.BinList;
import gdpcore.BinListNoDataException;
import gdpcore.ConfigurationManager;
import gdpcore.GeneFileExtractor;
import gdpcore.RepeatFileExtractor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


/**
 * A JScrollPane containing a list of track containers.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpListOfTrackContainers extends JScrollPane {

	private static final long serialVersionUID = -8766182651979827637L;	// Generated serial number
	private ArrayList<GdpTrackContainer> 	listOfTrackContainers;	// List of track containers
	private JSplitPane 						jspList;				// Panel containing the tracks


	/**
	 * Constructor. Creates a GdpListOfTrackContainers.
	 */
	public GdpListOfTrackContainers() {
		super();
		listOfTrackContainers = new ArrayList<GdpTrackContainer>();
		getVerticalScrollBar().setUnitIncrement(10);
	}


	/**
	 * Adds a new BinList track.
	 * @param cm {@link ConfigurationManager}
	 * @param aBinList {@link BinList}
	 * @param trackName Name of the track.
	 * @param chromo Current displayed chromosome
	 * @param minX Current displayed minimum position 
	 * @param maxX Current displayed maximum position 
	 * @throws BinListNoDataException
	 */
	public void addBinListTC(ConfigurationManager cm, BinList aBinList, String trackName, short chromo, int minX, int maxX) throws BinListNoDataException {
		GdpBinListTrackContainer trackContainer = new GdpBinListTrackContainer(cm, trackName, aBinList, chromo, minX, maxX);
		// Fire a property change event if the property of the track changes 
		trackContainer.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
			}
		});
		if (jspList == null) {
			jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JPanel(), trackContainer);
			jspList.setOneTouchExpandable(true);
		} else {
			JSplitPane jspTemp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jspList, trackContainer); 			
			jspTemp.setOneTouchExpandable(true);
			jspList = jspTemp;
		}
		listOfTrackContainers.add(trackContainer);
		setViewportView(jspList);
		repaint();
	}


	/**
	 * Adds a new gene track. 
	 * @param gfe {@link GeneFileExtractor}
	 * @param cm {@link ConfigurationManager}
	 * @param filePath path to the gene file
	 * @param chromo current displayed chromosome
	 * @param minX Current displayed minimum position 
	 * @param maxX Current displayed maximum position 
	 */
	public void addGeneTC(GeneFileExtractor gfe, ConfigurationManager cm, String filePath, short chromo, int minX, int maxX) {
		final GdpGeneTrackContainer trackContainer = new GdpGeneTrackContainer(gfe, cm, filePath, chromo, minX, maxX);
		// Fire a property change event if the property of the track changes
		trackContainer.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Close track")) {
					removeTrackContainer(trackContainer);
				}
				firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

			}
		});		
		if (jspList == null) {
			jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JPanel(), trackContainer);
			jspList.setOneTouchExpandable(true);
		} else {
			JSplitPane jspTemp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jspList, trackContainer); 			
			jspTemp.setOneTouchExpandable(true);
			jspList = jspTemp;
		}
		listOfTrackContainers.add(trackContainer);
		setViewportView(jspList);
		repaint();
	}


	/**
	 * Adds a new repeat track.
	 * @param rfe {@link RepeatFileExtractor} 
	 * @param cm {@link ConfigurationManager}
	 * @param filePath path to the gene file
	 * @param chromo current displayed chromosome
	 * @param minX Current displayed minimum position 
	 * @param maxX Current displayed maximum position 
	 */
	public void addRepeatTC(RepeatFileExtractor rfe, ConfigurationManager cm, String filePath, short chromo, int minX, int maxX) {
		final GdpRepeatTrackContainer trackContainer = new GdpRepeatTrackContainer(rfe, cm, filePath, chromo, minX, maxX);
		// Fire a property change event if the property of the track changes
		trackContainer.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Close track")) {
					removeTrackContainer(trackContainer);
				}
				firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

			}
		});		
		if (jspList == null) {
			jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JPanel(), trackContainer);
			jspList.setOneTouchExpandable(true);
		} else {
			JSplitPane jspTemp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jspList, trackContainer); 			
			jspTemp.setOneTouchExpandable(true);
			jspList = jspTemp;
		}
		listOfTrackContainers.add(trackContainer);
		setViewportView(jspList);
		repaint();		
	}


	/**
	 * Removes all the selected BinList tracks
	 */
	public void removeSelectedBinListTC() {
		int i = 0;

		while (i < listOfTrackContainers.size()) {
			if ((listOfTrackContainers.get(i) instanceof GdpBinListTrackContainer) && (((GdpBinListTrackContainer)listOfTrackContainers.get(i)).isSelected())) {
				listOfTrackContainers.remove(i);
			} else {
				i++;
			}
		}
		jspList.removeAll();
		if (listOfTrackContainers.size() > 0) {
			jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JPanel(), listOfTrackContainers.get(0));
			jspList.setOneTouchExpandable(true);
			for (int j = 1; j < listOfTrackContainers.size(); j++) {
				JSplitPane jspTemp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jspList, listOfTrackContainers.get(j)); 
				jspTemp.setOneTouchExpandable(true);
				jspList = jspTemp;

			}
		}
		setViewportView(jspList);
		jspList.revalidate();
	}


	/**
	 * Removes a track container.
	 * @param tc a track container
	 */
	public void removeTrackContainer(GdpTrackContainer tc) {
		listOfTrackContainers.remove(tc);
		jspList.removeAll();
		if (listOfTrackContainers.size() > 0) {
			jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JPanel(), listOfTrackContainers.get(0));
			jspList.setOneTouchExpandable(true);
			for (int j = 1; j < listOfTrackContainers.size(); j++) {
				JSplitPane jspTemp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jspList, listOfTrackContainers.get(j)); 
				jspTemp.setOneTouchExpandable(true);
				jspList = jspTemp;

			}
		}
		setViewportView(jspList);
		jspList.revalidate();
	}


	/**
	 * @param i Index of a BinList track container
	 * @return The BinList track container with index i
	 */
	public GdpBinListTrackContainer getBinListTC(int i) {
		if (listOfTrackContainers.get(i) instanceof GdpBinListTrackContainer) {
			return (GdpBinListTrackContainer)listOfTrackContainers.get(i);
		} else {
			return null;
		}
	}


	/**
	 * @return The list of all the BinList track container
	 */
	public ArrayList<GdpBinListTrackContainer> getAllBinListTC() {
		ArrayList<GdpBinListTrackContainer> resultList = new ArrayList<GdpBinListTrackContainer>();
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			if (trackContainer instanceof GdpBinListTrackContainer) {
				resultList.add((GdpBinListTrackContainer)trackContainer);
			}
		}		
		return resultList;
	}


	/**
	 * @return the whole list of generic track container
	 */
	public ArrayList<GdpTrackContainer> getAllGenericTC() {
		ArrayList<GdpTrackContainer> resultList = new ArrayList<GdpTrackContainer>();
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			if (!(trackContainer instanceof GdpBinListTrackContainer)) {
				resultList.add(trackContainer);
			}
		}		
		return resultList;
	}


	/**
	 * @return A list containing all the selected BinList track container
	 */
	public ArrayList<GdpBinListTrackContainer> getSelectedBinListTC() {
		ArrayList<GdpBinListTrackContainer> resultList = new ArrayList<GdpBinListTrackContainer>();
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			if ((trackContainer instanceof GdpBinListTrackContainer) && (((GdpBinListTrackContainer)trackContainer).isSelected())) {
				resultList.add((GdpBinListTrackContainer)trackContainer);
			}
		}
		return resultList;
	}	


	/**
	 * Set the minimum position to display
	 * @param minX Minimum position to display.
	 */
	public void setMinX(int minX) {
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			trackContainer.getTrack().setMinX(minX);
		}
	}


	/**
	 * Set the maximum position to display
	 * @param maxX Maximum position to display.
	 */
	public void setMaxX(int maxX) {
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			trackContainer.getTrack().setMaxX(maxX);
		}
	}


	/**
	 * Set the minimum and the maximum position to display
	 * @param minX Minimum position to display.
	 * @param maxX Maximum position to display.
	 */
	public void setMinMaxX(int minX, int maxX) {
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			trackContainer.getTrack().setMinMaxX(minX, maxX);
		}
	}


	/**
	 * Set the chromosome, the minimum and the maximum position to display 
	 * @param chromo Chromomsome to display.
	 * @param minX Minimum position to display.
	 * @param maxX Maximum position to display.
	 */
	public void setChromosome(short chromo, int minX, int maxX) {
		for (GdpTrackContainer trackContainer : listOfTrackContainers) {
			trackContainer.getTrack().setChromosome(chromo, minX, maxX);
		}
	}


	/**
	 * @return true if there is no track, false otherwise.
	 */
	public boolean isEmpty() {
		return (listOfTrackContainers.size() == 0);
	}
}
