package il.ac.tau.cs.software1.bigram;

import java.io.IOException;
import java.util.Locale;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14500;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods - already implemented for you. Use them!
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Converts a single token (a whitespace-free piece of text from the file)
     * into the form in which it is stored in the vocabulary:
     *   - a token with at least one English letter  -> the token in lowercase
     *   - a token made only of digits (an integer)  -> SOME_NUM
     *   - anything else is not a legal word         -> null
     *
     * @pre: token != null
     */
    public static String normalizeWord(String token) {
        boolean hasLetter = false;
        boolean allDigits = !token.isEmpty();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            if (c < '0' || c > '9') {
                allDigits = false;
            }
        }
        if (hasLetter) {
            return token.toLowerCase(Locale.ROOT);
        }
        if (allDigits) {
            return SOME_NUM;
        }
        return null;
    }

    /*
     * @pre: vocabulary != null
     * @post: $ret = the index of word in vocabulary, or ELEMENT_NOT_FOUND if
     * word is null or does not appear in it
     */
    public static int findIndex(String[] vocabulary, String word) {
        if (word == null) {
            return ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < vocabulary.length; i++) {
            if (word.equals(vocabulary[i])) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }
    

    ///////////////////////////////////////////////////////////////////////////
    // Questions 1 - 5: implement these
    ///////////////////////////////////////////////////////////////////////////


    private static String[] extractContent(String file_name, boolean byLines) throws IOException {
        
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file_name))){
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        if (byLines) {
            if (sb.length() == 0) {
                return new String[0];
            }
            return sb.toString().split("\n");
        }

        String content = sb.toString().trim();
        if (content.isEmpty()) {
            return new String[0];
        } 
        return content.split("\\s+");
    }

    private static void saveToFile(String fileName, String[] lines) throws IOException {
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
            for (String l: lines){
                bw.write(l);
                bw.write(System.lineSeparator());
            }
        }
    }

    private static boolean contains(String[] words, String target, int size){
        if (target == null) return true;
        for (int i = 0; i < size; i++){
            if (target.equals(words[i])) return true;
        }
        return false;
    }

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public String[] buildVocabularyIndex(String fileName) throws IOException { // Question 1
        String[] words = extractContent(fileName, false);
        String[] unique_words = new String[Math.min(words.length, MAX_VOCABULARY_SIZE)];
        int unique_count = 0;
        for (String w: words){
            String parsed = normalizeWord(w);
            if (parsed == null) continue;
            if (!contains(unique_words, parsed, unique_count)){
                unique_words[unique_count] = parsed;
                unique_count++;
            }
            if (unique_count == MAX_VOCABULARY_SIZE) break;
        }
        String[] result = new String[unique_count];
        for (int i = 0; i < unique_count; i++) result[i] = unique_words[i];
        return result;
    }

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Question 2
        String[] lines = extractContent(fileName, true);
        int[][] map = new int[vocabulary.length][vocabulary.length];
        for (String line: lines){
            String [] splitted_line = line.trim().split("\\s+");
            int prevIdx = findIndex(vocabulary, normalizeWord(splitted_line[0]));
            for (int i = 1; i < splitted_line.length; i++){
                int currIdx = findIndex(vocabulary, normalizeWord(splitted_line[i]));
                if (currIdx == ELEMENT_NOT_FOUND || prevIdx == ELEMENT_NOT_FOUND) continue;
                map[prevIdx][currIdx]++;
                prevIdx = currIdx;
            }
        }
        return map;
    }

    /*
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: fileName is a legal file path
     */
    public void saveModel(String fileName) throws IOException { // Question 3
        String[] lines_voc = new String[mVocabulary.length + 1];
        lines_voc[0] = Integer.toString(mVocabulary.length) + " words";
        for (int i = 0; i < mVocabulary.length; i++){
            lines_voc[i+1] = i + "," + mVocabulary[i];
        }
        saveToFile(fileName + VOC_FILE_SUFFIX, lines_voc);

        int count = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            for (int j = 0; j < mVocabulary.length; j++) {
                if (mBigramCounts[i][j] > 0) {
                    count++;
                }
            }
        }

        String[] lines_counts = new String[count];
        int ind = 0;
        for (int i = 0; i < mVocabulary.length; i++){
            for (int j = 0; j < mVocabulary.length; j++){
                if (mBigramCounts[i][j] == 0) continue;
                lines_counts[ind] = i + "," + j + ":" + mBigramCounts[i][j];
                ind++;
            }
        }
        saveToFile(fileName + COUNTS_FILE_SUFFIX, lines_counts);
    }

    /*
     * @pre: fileName is a legal file path
     */
    public void loadModel(String fileName) throws IOException { // Question 4
        String[] lines = extractContent(fileName + VOC_FILE_SUFFIX, true);
        String[] vocab = new String[lines.length-1];
        for (int i = 1; i < lines.length; i++){
            String[] sp = lines[i].split(",", 2);
            vocab[Integer.parseInt(sp[0])] = sp[1];
        }
        mVocabulary = vocab;

        String[] c_lines = extractContent(fileName + COUNTS_FILE_SUFFIX, true);
        int[][] counts = new int[mVocabulary.length][mVocabulary.length];
        for(String l: c_lines){
            String[] sp = l.split(":");
            String[] indices = sp[0].split(",");
            int i = Integer.parseInt(indices[0]);
            int j = Integer.parseInt(indices[1]);
            counts[i][j] = Integer.parseInt(sp[1]);
        }
        mBigramCounts = counts;
    }

    /*
     * @pre: sentence is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: each two words in the sentence are separated with a single space
     * @post: if sentence is probable, according to the model, $ret = true, else, $ret = false
     */
    public boolean isLegalSentence(String sentence) { // Question 5
        if (sentence.isEmpty()) return true;
        String[] all_words = sentence.split("\\s+");
        if (all_words.length == 1) return findIndex(mVocabulary, all_words[0])!=ELEMENT_NOT_FOUND;
        for (int k = 0; k < all_words.length - 1; k++){
            int i = findIndex(mVocabulary, all_words[k]);
            int j = findIndex(mVocabulary, all_words[k+1]);
            if (i == ELEMENT_NOT_FOUND || j == ELEMENT_NOT_FOUND || mBigramCounts[i][j] == 0) return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queries on the model - already implemented for you.
    // They start working once Questions 1 - 2 are done.
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: word is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
     */
    public int getWordIndex(String word) {
        return findIndex(mVocabulary, word);
    }

    /*
     * @pre: word1, word2 are in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
     * exist in the vocabulary, $ret = 0
     */
    public int getBigramCount(String word1, String word2) {
        int index1 = getWordIndex(word1);
        int index2 = getWordIndex(word2);
        if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return mBigramCounts[index1][index2];
    }

    /*
     * @pre: word is in lowercase, and is in mVocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the word with the lowest vocabulary index that appears most frequently after word
     * (if a bigram starting with word was never seen, $ret will be null)
     */
    public String getMostFrequentProceeding(String word) {
        int[] row = mBigramCounts[getWordIndex(word)];
        int best = ELEMENT_NOT_FOUND;
        for (int i = 0; i < row.length; i++) {
            if (row[i] > 0 && (best == ELEMENT_NOT_FOUND || row[i] > row[best])) {
                best = i;
            }
        }
        return best == ELEMENT_NOT_FOUND ? null : mVocabulary[best];
    }

    /*
     * @pre: arr1.length = arr2.length
     * @post: if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calculates CosineSim
     */
    public static double calcCosineSim(int[] arr1, int[] arr2) {
        double dot = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            dot += (double) arr1[i] * arr2[i];
            norm1 += (double) arr1[i] * arr1[i];
            norm2 += (double) arr2[i] * arr2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return -1;
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * @pre: word is in vocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w)
     * among all the other words in vocabulary (the lowest index wins a tie)
     */
    public String getClosestWord(String word) {
        int wordIndex = getWordIndex(word);
        int best = ELEMENT_NOT_FOUND;
        double bestSim = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            if (i == wordIndex) {
                continue;
            }
            double sim = calcCosineSim(mBigramCounts[wordIndex], mBigramCounts[i]);
            if (best == ELEMENT_NOT_FOUND || sim > bestSim) {
                best = i;
                bestSim = sim;
            }
        }
        // A vocabulary with a single word: the closest word is the word itself
        return best == ELEMENT_NOT_FOUND ? word : mVocabulary[best];
    }
}
