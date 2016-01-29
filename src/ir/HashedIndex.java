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
import java.util.LinkedList;


/**
 * Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    public static final int debug = 0;
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
            //index.get(word1).printEntries();
            return index.get(word1); // Just one term
        }
        // Instersection for queries larger than 1 word.
        if (query.size() > 1) {
            if (queryType == INTERSECTION_QUERY) {
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

            if (queryType == PHRASE_QUERY) {
                System.out.println("Phrase search!");
                PostingsList answer = new PostingsList();
                int k = 1;
                while (query.terms.size() != 0) {
                    if (answer.size() == 0) {
                        PostingsList w1postings = index.get(query.terms.pop());
                        PostingsList w2postings = index.get(query.terms.pop());

                        answer = positionalIntersect(w1postings, w2postings, k);
                        k++;
                    } else {
                        PostingsList wPostings = index.get(query.terms.pop());
                        answer = positionalIntersect(answer, wPostings, k); //INCREMENT K!!!!
                        k++;
                    }
                }
                return answer;
            }
        }
        return null;
    }

    //intersect (terms, word) eg (calpurnia and brutus) AND Caesar

    /**
     * Finds the intersection between two postingslists, stores the answers and
     * returns it.
     *
     * @param p1 PostingsList object to intersect
     * @param p2 PostingsList object to intersect
     * @return A PostingsList containing the intersections
     */
    public PostingsList intersect(PostingsList p1, PostingsList p2) {
        PostingsList intersections = new PostingsList();
        int i = 0, j = 0;
        while (i != p1.size() && j != p2.size()) {
            if (p1.get(i).equals(p2.get(j))) {
                intersections.insert(p1.get(i));
                i++; j++;
            }
            else if (p1.get(i).getDocID() < p2.get(j).getDocID()) {
                i++;
            }
            else {
                j++;
            }
        }
        return intersections;
    }

    /**
     * Performs a phrase intersection by utilizing the token offsets.
     * @param p1 PostingsList to intersect with
     * @param p2 PostingsList to intersect with
     * @param k
     * @return A PostingsList containing the intersections
     */
    public PostingsList positionalIntersect(PostingsList p1, PostingsList p2, int k) {
        PostingsList intersections = new PostingsList();
        int i = 0, j = 0;
        while (i != p1.size() && j != p2.size()) {
            if (p1.get(i).equals(p2.get(j))) {
                LinkedList<Integer> offsetsA = p1.get(i).getOffsets();
                LinkedList<Integer> offsetsB = p2.get(j).getOffsets();
                int m = 0, n = 0;
                //Begin the offset checks
                while (m != offsetsA.size() && n != offsetsB.size()) {
                    if ((offsetsB.get(n) - offsetsA.get(m)) == k) { //
                        //System.out.println("Found phrase intersection!");
                        intersections.insert(p1.get(i));
                        m++; n++;
                    }
                    else if ((offsetsB.get(n) - offsetsA.get(m)) > k) {
                        m++;
                    }
                    else {
                        n++;
                    }
                }
                i++; j++;
            }
            else if (p1.get(i).getDocID() < p2.get(j).getDocID()) {
                i++;
            }
            else {
                j++;
            }
        }
        return intersections;
    }

    /**
     * No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
