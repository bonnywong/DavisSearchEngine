/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;
import java.util.LinkedList;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {

    public LinkedList<Integer> offsets = new LinkedList<Integer>();
    public int docID;
    public double score;

    public PostingsEntry(int docID) {
        this.docID = docID;
        this.score = 0.0;
    }

    public PostingsEntry(int docID, int offset) {
        this.docID = docID;
        offsets.add(offset);
        this.score = 0.0;
    }

    public PostingsEntry(int docID, double score) {
        this.docID = docID;
        this.score = score;
    }

    /**
     *  PostingsEntries are compared by their score (only relevant 
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    public int compareTo( PostingsEntry other ) {
	    return Double.compare( other.score, score );
    }

    public boolean equals(PostingsEntry other) {
        return (this.docID == other.docID);
    }

    /**
     * Returns a LinkedList with the offets of this token.
     * @return LinkedList<Integer> offsets
     */
    public LinkedList<Integer> getOffsets() {
        return offsets;
    }

    /**
     * Inserts an offset of the token.
     * @param offset The offset of the token inside the document
     */
    public void insertOffset(int offset) {
        offsets.add(offset);
    }

    /**
     * Get the document ID
     * @return The document ID
     */
    public int getDocID() {
        return docID;
    }

    /**
     * Get the score used for ranked retrieval
     * @return The score
     */
    public double getScore() {
        return score;
    }

    /**
     * Prints all offsets in this entry
     */
    public void printOffsets() {
        System.out.println("Document ID: " + docID);
        for (int i : offsets) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
    //
    //  YOUR CODE HERE
    //

}

    
