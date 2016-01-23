/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    /**
     *  Inserts this token in the index.
     */
    public void insert( String token, int docID, int offset ) {

        PostingsEntry entry = new PostingsEntry(docID);

        // If not indexed earlier then create new PostingsList and entry
        if( index.get(token) == null) {
            PostingsList list = new PostingsList();
            list.insert(entry);
            index.put(token, list);
        } else {
            index.get(token).insert(entry);
        }
    }


    /**
     *  Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
        return index.keySet().iterator();
    }


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        return index.get(token);
    }


    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
        if (query.size() == 1) {
            return index.get(query.terms.get(0)); // Just one term
        }
        if (query.size() > 1) {
            PostingsList answer = new PostingsList();
            PostingsList p1, p2;
            p1 = index.get(query.terms.get(0));
            p2 = index.get(query.terms.get(1));

        }
    }

    //intersect (terms, word) eg (calpurnia and brutus) AND Caesar
    public PostingsList intersect() {

    }

    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
