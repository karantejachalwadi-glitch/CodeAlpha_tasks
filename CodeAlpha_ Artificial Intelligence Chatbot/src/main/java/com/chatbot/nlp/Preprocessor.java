package com.chatbot.nlp;

import java.util.*;

/**
 * NLP Preprocessor: handles tokenization, normalization, stop-word removal,
 * and lightweight suffix-based stemming.
 */
public class Preprocessor {

    // Common English stop words to strip before intent matching
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "it", "in", "on", "at", "to", "for",
        "of", "and", "or", "but", "not", "are", "was", "were", "be",
        "been", "being", "have", "has", "had", "do", "does", "did",
        "will", "would", "could", "should", "may", "might", "can",
        "i", "me", "my", "myself", "we", "our", "you", "your", "he",
        "she", "they", "them", "this", "that", "with", "so", "if", "by"
    ));

    /**
     * Full pipeline: lowercase → strip punctuation → tokenize → remove stop words → stem.
     */
    public static List<String> process(String input) {
        String normalized = normalize(input);
        List<String> tokens = tokenize(normalized);
        List<String> filtered = removeStopWords(tokens);
        List<String> stemmed = stem(filtered);
        return stemmed;
    }

    /** Lowercase and remove non-alphanumeric characters (keep spaces). */
    public static String normalize(String input) {
        return input.toLowerCase(Locale.ENGLISH)
                    .replaceAll("[^a-z0-9\\s]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
    }

    /** Split by whitespace into individual tokens. */
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        for (String token : text.split("\\s+")) {
            if (!token.isEmpty()) tokens.add(token);
        }
        return tokens;
    }

    /** Remove common stop words from token list. */
    public static List<String> removeStopWords(List<String> tokens) {
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (!STOP_WORDS.contains(token)) result.add(token);
        }
        return result;
    }

    /**
     * Simple suffix-stripping stemmer (Porter-lite).
     * Handles common English suffixes: -ing, -ed, -er, -es, -ly, -tion, -ness, -ment.
     */
    public static List<String> stem(List<String> tokens) {
        List<String> stemmed = new ArrayList<>();
        for (String token : tokens) {
            stemmed.add(stemWord(token));
        }
        return stemmed;
    }

    public static String stemWord(String word) {
        if (word.length() <= 3) return word;

        // Order matters: check longest suffixes first
        String[][] rules = {
            {"ational", "ate"}, {"tional",  "tion"}, {"ization", "ize"},
            {"fulness", "ful"}, {"ousness", "ous"},  {"iveness", "ive"},
            {"ingness", "ing"}, {"ness",    ""},     {"ment",    ""},
            {"tion",    "te"},  {"ling",    "l"},    {"able",    ""},
            {"ible",    ""},    {"less",    ""},     {"ness",    ""},
            {"ful",     ""},    {"ous",     ""},     {"ing",     ""},
            {"ed",      ""},    {"er",      ""},     {"es",      ""},
            {"ly",      ""},    {"s",       ""}
        };

        for (String[] rule : rules) {
            String suffix = rule[0];
            String replacement = rule[1];
            if (word.endsWith(suffix) && word.length() - suffix.length() > 2) {
                return word.substring(0, word.length() - suffix.length()) + replacement;
            }
        }
        return word;
    }

    /** Compute bigrams from a token list for richer matching. */
    public static List<String> bigrams(List<String> tokens) {
        List<String> bigrams = new ArrayList<>();
        for (int i = 0; i < tokens.size() - 1; i++) {
            bigrams.add(tokens.get(i) + "_" + tokens.get(i + 1));
        }
        return bigrams;
    }
}
