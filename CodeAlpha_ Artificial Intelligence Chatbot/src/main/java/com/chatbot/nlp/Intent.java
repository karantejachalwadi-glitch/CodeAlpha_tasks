package com.chatbot.nlp;

import java.util.*;

/**
 * Represents a single intent with its pattern keywords, regex patterns,
 * possible responses, and a confidence weight.
 */
public class Intent {

    private final String name;
    private final List<String> keywords;          // stemmed keywords to match
    private final List<String> regexPatterns;     // raw regex patterns on original input
    private final List<String> responses;         // pool of responses (one chosen randomly)
    private final double weight;                  // base weight multiplier for this intent

    public Intent(String name, List<String> keywords,
                  List<String> regexPatterns, List<String> responses,
                  double weight) {
        this.name = name;
        this.keywords = keywords;
        this.regexPatterns = regexPatterns;
        this.responses = responses;
        this.weight = weight;
    }

    // ---------- getters ----------

    public String getName()              { return name; }
    public List<String> getKeywords()    { return keywords; }
    public List<String> getRegexPatterns() { return regexPatterns; }
    public List<String> getResponses()   { return responses; }
    public double getWeight()            { return weight; }

    /**
     * Pick a random response from the response pool.
     */
    public String getRandomResponse() {
        if (responses.isEmpty()) return "I'm not sure how to respond to that.";
        return responses.get(new Random().nextInt(responses.size()));
    }

    @Override
    public String toString() {
        return "Intent{name='" + name + "', keywords=" + keywords + "}";
    }
}
