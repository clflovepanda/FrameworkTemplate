package com.panda.template.utils.words;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DividedWordsUtil {

    private static List<String> unUsefulWords = new ArrayList<>();

    static {
        unUsefulWords.add("员");
        unUsefulWords.add("工程师");
        unUsefulWords.add("师");
        unUsefulWords.add("工");
        unUsefulWords.add("副");
        unUsefulWords.add("科");
        unUsefulWords.add("水");
        unUsefulWords.add("空");
        unUsefulWords.add("配");
        unUsefulWords.add("拣");
        unUsefulWords.add("理");
        unUsefulWords.add("度");
        unUsefulWords.add("官");
        unUsefulWords.add("岗");
        unUsefulWords.add("类");
        unUsefulWords.add("人");
        unUsefulWords.add("端");
        unUsefulWords.add("店");
        unUsefulWords.add("医");
        unUsefulWords.add("部");
        unUsefulWords.add("品");
        unUsefulWords.add("和");
        unUsefulWords.add("级");
        unUsefulWords.add("非");
        unUsefulWords.add("辖");
        unUsefulWords.add("经理");
        unUsefulWords.add("主管");
        unUsefulWords.add("总监");
        unUsefulWords.add("专员");
        unUsefulWords.add("顾问");
        unUsefulWords.add("其他");
        unUsefulWords.add("助理");
        unUsefulWords.add("类型");
        unUsefulWords.add("教练");
        unUsefulWords.add("翻译");
    }


    public static List<DividedWord> dividedWords(String text) throws IOException {
        StringReader sr = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        List<DividedWord> result = new ArrayList<>();
        while((lex = ik.next())!=null){
            if (isUsefulWord(lex.getLexemeText())) {
                result.add(new DividedWord(lex.getLexemeText(), lex.getLexemeType()));
            }

        }
        return result;
    }

    public static boolean isUsefulWord(String word) {
        return !unUsefulWords.contains(word);
    }

}
