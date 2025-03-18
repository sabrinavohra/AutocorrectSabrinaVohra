import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Sabrina Vohra
 */
public class Autocorrect {
    String[] dictionary;
    int threshold;
    ArrayList<String> matches;
    ArrayList[] matchesSplit;

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        dictionary = words;
        this.threshold = threshold;
        matches = new ArrayList<>();
        matchesSplit = new ArrayList[threshold + 1];
        for(int i = 0; i < matchesSplit.length; i++) {
            matchesSplit[i] = new ArrayList<>();
        }
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        for (String s : dictionary) {
            int current = lev(typed, s);
            if ((current <= threshold) && ((s.length() - threshold) < typed.length())) {
                matchesSplit[current].add(s);
                for (ArrayList editList : matchesSplit) {
                    Collections.sort(editList);
                }
            }
        }
        for(int i = 0; i < matchesSplit.length; i++) {
            for(int j = 0; j < matchesSplit[i].size(); j++) {
                matches.add((String) matchesSplit[i].get(j));
            }
        }
        String[] finalList = new String[matches.size()];
        for(int i = 0; i < matches.size(); i++) {
            finalList[i] = matches.get(i);
        }

        return finalList;
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int lev (String typed, String dict) {
        int[][] ed = new int[typed.length() + 1][dict.length() + 1];
        for(int i = 0; i < typed.length(); i++) {
            ed[i][0] = i;
        }
        for(int j = 0; j < dict.length(); j++) {
            ed[0][j] = j;
        }
        for(int i = 1; i < typed.length() + 1; i++) {
            for(int j = 1; j < dict.length() + 1; j++) {
                if(typed.substring(i - 1, i).equals(dict.substring(j - 1, j))) {
                    ed[i][j] = ed[i - 1][j - 1];
                }
                else {
                    int small = Math.min(ed[i-1][j], ed[i][j-1]);
                    ed[i][j] = Math.min(ed[i-1][j-1], small) + 1;
                }
            }
        }
        return ed[typed.length()][dict.length()];
    }


    public ArrayList<String> edsort (ArrayList<String> current, int toAddEd, String toAdd) {
        if(toAddEd == 1) {
            current.addFirst(toAdd);
        }
        if(current.isEmpty()) {
            current.addFirst(toAdd);
            return current;
        }
        else {
            return edsort(current, toAddEd, toAdd);
        }
    }


    public ArrayList<String> sort (ArrayList<String> current, String toAdd, int index) {
        if(current.isEmpty()) {
            current.add(toAdd);
            return current;
        }
        if(current.get(index).compareTo(toAdd) > 0) {
            current.add(index + 1, toAdd);
            return  current;
        }
        else {
            return sort(current, toAdd, index - 1);
        }
    }
}