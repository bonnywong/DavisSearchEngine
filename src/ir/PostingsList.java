/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */

package ir;

import java.util.LinkedList;
import java.io.Serializable;

/**
 * A list of postings for a given word.
 */
public class PostingsList implements Serializable {

    /**
     * The postings list as a linked list.
     */
    private LinkedList<PostingsEntry> list = new LinkedList<PostingsEntry>();

    /**
     * Returns the number of postings in the list
     * @return Number of postings
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns the ith posting
     */
    public PostingsEntry get(int i) {
        return list.get(i);
    }

    /**
     * Returns a list of PostingsEntries.
     * @return
     */
    public LinkedList<PostingsEntry> getEntires() {
        return list;
    }

    public void printEntries() {
        for (PostingsEntry p : list) {
            p.printOffsets();
        }
    }

    /**
     * Inserts a PostingsEntry p into the PostingsList
     * with offset of token.
     *
     * @param p A PostingsEntry object
     * @param offset An offset
     */
    public void insert(PostingsEntry p, int offset) {
        if (size() == 0) {
            list.add(p);
        }
        // If the last docID is the same as the provided docID then do not add the new one. Only add the provided
        // offset instead. This assumes that the docs are provided in an already sorted order.
        if (list.peekLast().getDocID() != p.getDocID()) {
            list.add(p);
        } else {
            list.peekLast().insertOffset(offset);
        }
    }

    /**
     * Add a single PostingsEntry into the list
     * @param p A postings entry
     */
    public void insert(PostingsEntry p) {
        if (size() == 0) {
            list.add(p);
        }
        else if (list.peekLast().getDocID() != p.getDocID()) {
            list.add(p);
        }
    }
}


			   
