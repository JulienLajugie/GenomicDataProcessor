/**
 * @author Julien Lajugie
 * @version 0.1
 */
package gdpcore;

/**
 * The Gene class provides a representation of a gene.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Gene {
	private String 		name; 			// Name of gene
	private short		chromo;			// Chromosome
	private Strand		strand;			// Strand of the gene
	private int 		txStart; 		// Transcription start position
	private int 		txStop; 		// Transcription end position
	private int[] 		exonStarts; 	// Exon start positions
	private int[] 		exonStops; 		// Exon end positions
	private double[]	exonScores;		// Exon score

	
	/**
	 * Public constructor.
	 * @param aName Name of gene.
	 * @param aChromo Reference sequence chromosome or scaffold.
	 * @param aStrand String representing the strand of a gene. (ie "+" or "-") 
	 * @param aTxStart Transcription start position.
	 * @param aTxStop Transcription end position.
	 * @param arrayExonStarts Exon start positions.
	 * @param arrayExonStops Exon end positions.
	 * @param arrayExonScores Exon scores
	 */
	public Gene(String aName, short aChromo, String aStrand, int aTxStart, int aTxStop, int[] arrayExonStarts, int[] arrayExonStops, double[] arrayExonScores) {
		name = aName;
		chromo = aChromo;
		if (aStrand.equals("+")) {
			strand = Strand.five;
		} else if (aStrand.equals("-")) {
			strand = Strand.three;
		}
		txStart = aTxStart;
		txStop = aTxStop;
		exonStarts = arrayExonStarts;
		exonStops = arrayExonStops;
		exonScores = arrayExonScores;
	}
	
	/**
	 * Public constructor.
	 * @param aName Name of gene.
	 * @param aChromo Reference sequence chromosome or scaffold.
	 * @param aStrand Strand of the gene.
	 * @param aTxStart Transcription start position.
	 * @param aTxStop Transcription end position.
	 * @param arrayExonStarts Exon start positions.
	 * @param arrayExonStops Exon end positions.
	 * @param arrayExonScores Exon scores
	 */
	public Gene(String aName, short aChromo, Strand aStrand, int aTxStart, int aTxStop, int[] arrayExonStarts, int[] arrayExonStops, double[] arrayExonScores) {
		name = aName;
		chromo = aChromo;
		strand = aStrand;
		txStart = aTxStart;
		txStop = aTxStop;
		exonStarts = arrayExonStarts;
		exonStops = arrayExonStops;
		exonScores = arrayExonScores;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the txStart
	 */
	public int getTxStart() {
		return txStart;
	}
	
	/**
	 * @return the txStop
	 */
	public int getTxStop() {
		return txStop;
	}
	
	/**
	 * @return the exonStarts
	 */
	public int[] getExonStarts() {
		return exonStarts;
	}
	
	/**
	 * @return the exonStops
	 */
	public int[] getExonStops() {
		return exonStops;
	}
	
	/**
	 * @param aName Name of a chromosome
	 * @return True if <i>aName</i> equals the name of the chromosome. False otherwise.
	 */
	public boolean equals(String aName) {
		return name.equalsIgnoreCase(aName);
	}


	/**
	 * @return The chromosome of the gene.
	 */
	public short getChromo() {
		return chromo;
	}


	/**
	 * @return the strand
	 */
	public Strand getStrand() {
		return strand;
	}

	/**
	 * @param exonScores the exonScores to set
	 */
	public void setExonScores(double[] exonScores) {
		this.exonScores = exonScores;
	}

	/**
	 * @return the exonScores
	 */
	public double[] getExonScores() {
		return exonScores;
	}
}
