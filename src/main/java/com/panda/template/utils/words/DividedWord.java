package com.panda.template.utils.words;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DividedWord {

    private String word;
    private int type;

    public DividedWord() {}

    public DividedWord(String word, int type) {
        this.word = word;
        this.type = type;
    }

}
