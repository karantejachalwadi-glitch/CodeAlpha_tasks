package com.chatbot.nlp;

import java.util.*;
import java.util.regex.*;

/**
 * Intent Classifier – scores each registered intent against the user input
 * using a weighted combination of:
 *   1. Regex pattern hits (high weight)
 *   2. Keyword overlap on stemmed tokens (medium weight)
 *   3. Bigram overlap on stemmed tokens (bonus weight)
 *
 * Returns the best-scoring intent (or null if nothing passes the threshold).
 */
public class IntentClassifier {

    private static final double REGEX_SCORE      = 3.0;
    private static final double KEYWORD_SCORE    = 1.0;
    private static final double BIGRAM_SCORE     = 0.5;
    private static final double THRESHOLD        = 1.0;   // minimum score to accept an intent

    private final List<Intent> intents = new ArrayList<>();

    /** Register an intent with the classifier. */
    public void addIntent(Intent intent) {
        intents.add(intent);
    }

    /**
     * Classify the raw user input and return the best matching intent,
     * or null if no intent passes the confidence threshold.
     */
    public Intent classify(String rawInput) {
        String normalized = Preprocessor.normalize(rawInput);
        List<String> tokens = Preprocessor.process(rawInput);
        List<String> bigrams = Preprocessor.bigrams(tokens);

        Intent bestIntent = null;
        double bestScore  = 0.0;

        for (Intent intent : intents) {
            double score = scoreIntent(intent, normalized, tokens, bigrams);
            score *= intent.getWeight();   // apply per-intent weight multiplier

            if (score > bestScore) {
                bestScore  = score;
                bestIntent = intent;
            }
        }

        return (bestScore >= THRESHOLD) ? bestIntent : null;
    }

    /** Compute raw (pre-weight) score for a single intent. */
    private double scoreIntent(Intent intent, String normalized,
                               List<String> tokens, List<String> bigrams) {
        double score = 0.0;

        // 1. Regex pattern matching (on normalized input)
        for (String pattern : intent.getRegexPatterns()) {
            try {
                if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                           .matcher(normalized).find()) {
                    score += REGEX_SCORE;
                }
            } catch (PatternSyntaxException ignored) { /* skip bad patterns */ }
        }

        // 2. Keyword overlap (stemmed keywords vs stemmed tokens)
        Set<String> tokenSet = new HashSet<>(tokens);
        for (String kw : intent.getKeywords()) {
            String stemmedKw = Preprocessor.stemWord(kw.toLowerCase(Locale.ENGLISH));
            if (tokenSet.contains(stemmedKw)) {
                score += KEYWORD_SCORE;
            }
        }

        // 3. Bigram bonus
        Set<String> bigramSet = new HashSet<>(bigrams);
        for (String kw : intent.getKeywords()) {
            // Check if any bigram starts with this keyword
            String stemmedKw = Preprocessor.stemWord(kw.toLowerCase(Locale.ENGLISH));
            for (String bg : bigramSet) {
                if (bg.startsWith(stemmedKw + "_") || bg.endsWith("_" + stemmedKw)) {
                    score += BIGRAM_SCORE;
                    break;
                }
            }
        }

        return score;
    }

    public List<Intent> getIntents() {
        return Collections.unmodifiableList(intents);
    }
}
