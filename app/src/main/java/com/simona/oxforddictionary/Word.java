package com.simona.oxforddictionary;

import java.util.ArrayList;

public class Word {
    private String word;
    private ArrayList<String> definition;
    private ArrayList<String> synonyms;
    private ArrayList<String> examples;

    public Word(String word, ArrayList<String> definition, ArrayList<String> synonims, ArrayList<String> examples) {
        this.word = word;
        this.definition = definition;
        this.synonyms = synonims;
        this.examples = examples;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<String> getDefinition() {
        return definition;
    }

    public ArrayList<String> getSynonims() {
        return synonyms;
    }

    public ArrayList<String> getExamples() {
        return examples;
    }

}
