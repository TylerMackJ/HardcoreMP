package com.tylermackj.hardcoremp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordProvider {
    public class NamePair {
        String adjective;
        String noun;

        public NamePair(String adjective, String noun) {
            this.adjective = adjective;
            this.noun = noun;
        }

        public String getDisplayName() {
            String displayAdjective = this.adjective.substring(0,1).toUpperCase() + this.adjective.substring(1);
            String displayNoun = this.noun.substring(0,1).toUpperCase() + this.noun.substring(1);
            return displayAdjective + " " + displayNoun;
        }

        public String getCodeName() {
            return this.adjective + "_" + this.noun;
        }
    }

    private static WordProvider instance;

    private ArrayList<String> adjectives = new ArrayList<>();
    private ArrayList<String> nouns = new ArrayList<>();

    private WordProvider() {
        BufferedReader adjectives = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/dictionaries/Adjectives.txt"))); 
        BufferedReader nouns = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/dictionaries/Nouns.txt"))); 

        String line;
        try {
            while ((line = adjectives.readLine()) != null) {
                this.adjectives.add(line);
            }

            while ((line = nouns.readLine()) != null) {
                this.nouns.add(line);
            }
        }
        catch (IOException e) {}
    }

    public static WordProvider getInstance() {
        if (instance == null) {
            WordProvider.instance = new WordProvider();
        }

        return WordProvider.instance;
    }

    public String getAdjective() {
        return this.adjectives.get(Utils.random.nextInt(this.adjectives.size()));
    }

    public String getNoun() {
        return this.nouns.get(Utils.random.nextInt(this.nouns.size()));
    }

    public NamePair getPair() {
        return new NamePair(getAdjective(), getNoun());
    }
}
