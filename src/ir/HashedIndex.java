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
 * Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    /* Query types */
    public static final int INTERSECTION_QUERY = 0;
    public static final int PHRASE_QUERY = 1;
    public static final int RANKED_QUERY = 2;

    /**
     * The index as a hashtable.
     */
    private HashMap<String, PostingsList> index = new HashMap<String, PostingsList>();


    /**
     * Inserts this token in the index.
     * Note that offset is likely the tokens position inside the document
     */
    public void insert(String token, int docID, int offset) {

        PostingsEntry entry = new PostingsEntry(docID, offset);

        // If not indexed earlier then create new PostingsList and entry
        if (index.get(token) == null) {
            PostingsList list = new PostingsList();
            list.insert(entry);
            index.put(token, list);
        } else {
            index.get(token).insert(entry, offset);
        }
    }


    /**
     * Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
        return index.keySet().iterator();
    }


    /**
     * Returns the postings for a specific term, or null
     * if the term is not in the index.
     */
    public PostingsList getPostings(String token) {
        return index.get(token);
    }


    /**
     * Searches the index for postings matching the query.
     */
    public PostingsList search(Query query, int queryType, int rankingType, int structureType) {
        if (query.size() == 1) {
            String word1 = query.terms.get(0);
            index.get(word1).printEntries();
            return index.get(word1); // Just one term
        }
        // Instersection for queries larger than 1 word.
        if (query.size() > 1 && queryType == INTERSECTION_QUERY) {

            System.out.println("Intersection search!");
            PostingsList answer = new PostingsList();

            while (query.terms.size() != 0) {
                if (answer.size() == 0) { // Start of the intersect, pick two words
                    PostingsList w1postings = index.get(query.terms.pop());
                    PostingsList w2postings = index.get(query.terms.pop());

                    answer = intersect(w1postings, w2postings);
                } else {
                    PostingsList wPostings = index.get(query.terms.pop());
                    answer = intersect(answer, wPostings);
                }
            }
            return answer;
        }
        // Phrase query
        if (query.size() > 1 && queryType == PHRASE_QUERY) {
            System.out.println("Phrase query.");
        }

        return null;
    }

    //intersect (terms, word) eg (calpurnia and brutus) AND Caesar

    /**
     * Finds the intersection between two postingslists, stores the answers and
     * returns it.
     * @param A PostingsList object to intersect
     * @param B PostingsList object to intersect
     * @return A PostingsList object containing the intersections
     */
    public PostingsList intersect(PostingsList A, PostingsList B) {
        PostingsList answer = new PostingsList();
        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j < B.size(); j++) {
                if (A.get(i).getDocID() == B.get(j).getDocID()) {
                    System.out.print(i);
                    answer.insert(A.get(i));
                    break;
                }
            }
        }
        return answer;
    }

    /**
     * No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
