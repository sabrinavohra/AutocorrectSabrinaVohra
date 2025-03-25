import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Sabrina Vohra
 */
public class Autocorrect {
    // Declares global variables
    String[] dictionary;
    int threshold;
    ArrayList<String> matches;
    // Makes ArrayList to split up words by edit distance
    ArrayList[] matchesSplit;

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        // Sets dictionary and threshold and ArrayList to hold added words
        dictionary = words;
        this.threshold = threshold;
        matches = new ArrayList<>();
        // Creates new ArrayLists for each edit distance
        matchesSplit = new ArrayList[threshold + 1];
        for(int i = 0; i < matchesSplit.length; i++) {
            matchesSplit[i] = new ArrayList<>();
        }
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distance, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        // Runs test for each String in the dictionary
        for (String s : dictionary) {
            // Runs lev edit distance calculator on each String and compares to the typed String
            int current = lev(typed, s);
            // Adds to the correct edit distance ArrayList if the edit distance is less than or equal to the threshold
            if ((current <= threshold) && ((s.length() - threshold) < typed.length())) {
                matchesSplit[current].add(s);
            }
        }
        // Sorts the ArrayLists, adds each one to one ArrayList once sorted
        for (ArrayList arrayList : matchesSplit) {
            Collections.sort(arrayList);
            for (Object o : arrayList) {
                matches.add((String) o);
            }
        }
        // Adds all the words in the ArrayList into an Array that will be returned
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

    // Finds the Levenshtein distance (edit distance) between any two words
    public int lev (String typed, String dict) {
        // Creates an Array and adds an extra to the lengths to leave a row and column blank
        int[][] ed = new int[typed.length() + 1][dict.length() + 1];
        // Adds increasing values by one to the "empty" row and column
        for(int i = 0; i <= typed.length(); i++) {
            ed[i][0] = i;
        }
        for(int j = 0; j <= dict.length(); j++) {
            ed[0][j] = j;
        }
        // Runs through the rest of the rows and columns
        for(int i = 1; i < typed.length() + 1; i++) {
            for(int j = 1; j < dict.length() + 1; j++) {
                // If the heads are the same, add the value of the tails into the box in the Array
                if(typed.substring(i - 1, i).equals(dict.substring(j - 1, j))) {
                    ed[i][j] = ed[i - 1][j - 1];
                }
                // Otherwise, add one to the minimum value of the three surrounding spaces to get the edit distance with
                // a different letter
                else {
                    int small = Math.min(ed[i-1][j], ed[i][j-1]);
                    ed[i][j] = Math.min(ed[i-1][j-1], small) + 1;
                }
            }
        }
        // Returns the last box that has the value for the full length of both words
        return ed[typed.length()][dict.length()];
    }

    // Runs the program for the terminal
    public static void main(String[] args) {
        String[] dict = loadDictionary("large");
        Autocorrect a = new Autocorrect(dict, 2);
        // Creates variable to run the program infinitely, until the user presses stop
        boolean end = false;
        while(!end) {
            // Takes in user input for the misspelled word
            System.out.println("Enter your word: ");
            boolean inDict = false;
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            // Find more efficient way to search through dictionary
            // Works through dictionary
            for (int i = 0; i < dict.length; i++) {
                // If the word is in the dictionary, return as such
                if (dict[i].equals(in)) {
                    inDict = true;
                    System.out.println("This word is correctly spelt!");
                }
            }
            // If the word is not in the dictionary, find the most similar words and print them
            if (!inDict) {
                String[] list = a.runTest(in);
                if(list.length == 0) {
                    System.out.println("There are no similar words in the dictionary.");
                }
                for (String s : list) {
                    System.out.println(s);
                }
            }
            System.out.println("---------");
        }
    }
}