/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    public int docID;
    public double score;

    public PostingsEntry(int docID) {
        this.docID = docID;
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
        if(this.docID == other.docID) {
            return true;
        } else {
            return false;
        }
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

    //
    //  YOUR CODE HERE
    //

}

    
