package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		// base case - if file cannot be found/loaded
		if (docFile == null) {
			throw new FileNotFoundException("ERROR: Could not find file! Please try entering the name of the file correctly!");
		}

		Scanner s = new Scanner(new File(docFile));

		// Hash table that contains the words and the frequency at which they appear in the file, to be returned by the method
		HashMap<String,Occurrence> toReturn = new HashMap<String, Occurrence>();
		while(s.hasNext()) {
			String word = s.next();

			if (getKeyword(word) != null) {
				// if the word isn't pre-existent in the hash table, word is inserted into the table with a default frequency of 1
				if (!toReturn.containsKey(getKeyword(word))) {
					toReturn.put(getKeyword(word), new Occurrence(docFile, 1));
				} else {
					// increases the existent word's frequency in the hash table by 1
					toReturn.get(getKeyword(word)).frequency = toReturn.get(getKeyword(word)).frequency + 1;
				}
			}
		}

		// closes the scanner as we're done using it
		s.close();

		//finished product
		return toReturn;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		// goes through the words that are in the inputted hash table
		Set<String> foo = kws.keySet();
		Iterator<String> bar = foo.iterator();

		while (bar.hasNext()) {
			String word = bar.next();

			// if the word isn't already in the master index, insert the word and where it appears in the index
			if (!keywordsIndex.containsKey(word)) {
				keywordsIndex.put(word, new ArrayList<>());
				keywordsIndex.get(word).add(kws.get(word));
			} else {
				// inserts the where the word is in the proper location of the master index w/ insertLastOccurrence
				keywordsIndex.get(word).add(kws.get(word));
				insertLastOccurrence(keywordsIndex.get(word));
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		String toReturn = "";
		String brk = null;
		ArrayList<Character> punctuations = new ArrayList<Character>(Arrays.asList('.', ',', '?', ';', ':', '!'));

		// iterates through the word's letters/characters
		for (int i = 0; i < word.length(); i++) {
			// if there isn't a letter in the String, the method breaks and simply returns nothing
			if ((i != (word.length() - 1)) && (!Character.isAlphabetic(word.charAt(i))) && (Character.isAlphabetic(word.charAt(i + 1)))) {
				return brk;
			}
			// checks if a character is potentially a punctuation character; if it isn't continue with the loop :)
			else if (!Character.isAlphabetic(word.charAt(i))) {
				if (punctuations.contains(word.charAt(i))) {
					continue;
				} else {
					return brk;
				}
			} else {
				toReturn += word.toLowerCase().charAt(i);
			}
		}

		/*checks if there's a noise word in the toReturn string, if the final string is null, or there's a noise word that results when
		concatenating the string based on spaces*/
		if (toReturn.equals("") || hm1(toReturn)) {
			return brk;
		}

		// finished product
		return toReturn;

	}

	// conditionals to check to help w/ finalized output string and compare with the NoiseWord array
	public boolean hm1(String s) {
		if (!noiseWords.contains(s) && !noiseWords.contains(s.concat(" "))) {
			return false;
		}
		return true;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process, <-- IMPORTANT!!!
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// list to be returned containing the midpoint indexes in the input list through binary search
		ArrayList<Integer> toReturn = new ArrayList<Integer>();

		// binary search time :)
		int min = 0;
		int max = occs.size() - 2;
		int mid = 0;
		int toFind = occs.get(occs.size() - 1).frequency;

		while (min <= max) {
			mid = ((min+max)/2);
			toReturn.add(mid);

			if (occs.get(mid).frequency < toFind) {
				max = mid-1;
			} else if (occs.get(mid).frequency > toFind) {
				min = mid+1;
			} else {
				break;
			}
		}

		Occurrence foo = occs.remove(occs.size() - 1);
		occs.add(min,foo);
		return toReturn;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> toReturn = new ArrayList<>();

		// bc I don't if we're allowed to alter our parameters directly
        String keyword1 = kw1.toLowerCase();
        String keyword2 = kw2.toLowerCase();

        // base case: if the index doesn't contain the inputted keywords, return nothing
        if (!keywordsIndex.containsKey(keyword1) && !keywordsIndex.containsKey(keyword2)) {
            return null;
        }

        // if kw1 is found but kw2 isn't, adds 5 search results based on kw1, if not all
        else if (keywordsIndex.containsKey(keyword1) && !keywordsIndex.containsKey(keyword2)) {
            hm2(toReturn, keyword1);
        }

        // if kw2 is found but kw1 isn't, adds 5 search results based on kw2, if not all
        else if (!keywordsIndex.containsKey(keyword1) && keywordsIndex.containsKey(keyword2)) {
            hm2(toReturn, keyword2);
        }

        // if both keywords are found,
        else if (keywordsIndex.containsKey(keyword1) && keywordsIndex.containsKey(keyword2)) {
            hm3(toReturn, keyword1, keyword2);
        }

        return toReturn;

	}

	// helper method condensing the iterations and insertions needed for when one keyword is found in the index, but the other isn't
	public void hm2(ArrayList<String> base, String foo) {
	    for (int i = 0; i < 5; i++) {
	        if (i == keywordsIndex.get(foo).size()) {
	            break;
            } else {
	            base.add(keywordsIndex.get(foo).get(i).document);
            }
        }
    }

    // helper method condensing the iterations and insertions needed for when both keywords are found in the index
    public void hm3(ArrayList<String> base, String foo, String bar) {
        int ct1 = 0;
        int ct2 = 0;

        // goes through and adds the keywords found in the index based on the conditionals
        while ((ct1 < keywordsIndex.get(foo).size() || ct2 < keywordsIndex.get(bar).size()) && base.size() < 5) {

            /* if the count for the first keyword is equivalent to the number of times it appears in the index, and the List to-return
            does not already contain the second keyword(s), it is inserted into the List to-return*/
            if(ct1 == keywordsIndex.get(foo).size()) {
                if(!base.contains(keywordsIndex.get(bar).get(ct2).document)) {
                    base.add(keywordsIndex.get(bar).get(ct2).document);
                }
                ct2++;
            }

             /* if the count for the second keyword is equivalent to the number of times it appears in the index, and the List to-return
            does not already contain the first keyword(s), it is inserted into the List to-return*/
            else if(ct2 == keywordsIndex.get(bar).size()) {
                if(!base.contains(keywordsIndex.get(foo).get(ct1).document)) {
                    base.add(keywordsIndex.get(foo).get(ct1).document);
                }
                ct1++;
            }

            /* if the number of times the first keyword is count is >= the number of times the second keyword is found, and the List to-return
            does not already contain the first keyword(s), it is inserted into the List to-return*/
            else if(keywordsIndex.get(foo).get(ct1).frequency >= keywordsIndex.get(bar).get(ct2).frequency) {
                if(!base.contains(keywordsIndex.get(foo).get(ct1).document)) {
                    base.add(keywordsIndex.get(foo).get(ct1).document);
                }
                ct1++;
            }

            /* if the number of times the second keyword is count is >= the number of times the first keyword is found, and the List to-return
            does not already contain the second keyword(s), it is inserted into the List to-return*/
            else {
                if(!base.contains(keywordsIndex.get(bar).get(ct2).document)) {
                    base.add(keywordsIndex.get(bar).get(ct2).document);
                }
                ct2++;
            }
        }
    }
}
