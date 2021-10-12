import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.template.utils.words.DividedWord;
import com.panda.template.utils.words.DividedWordsUtil;
import com.panda.template.utils.words.Executor;
import javafx.util.Pair;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TestCompleteLPTree {

    private static JSONArray bossJson;
    private static JSONArray zlJson;
    private static JSONArray lpJson;

    private static Map<String, List<DividedWord>> lpKwMap = new HashMap<>();
    private static Map<String, List<DividedWord>> bossKwMap = new HashMap<>();
    private static Map<String, List<DividedWord>> zlKwMap = new HashMap<>();

    private static Map<String, Integer> keyStatistic = new HashMap<>();

    private static void initBossJson() throws Exception {
        File file = new File("./format/boss_tree.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        bossJson = JSON.parseArray(origin);
    }

    private static void initLPJson() throws Exception {
        File file = new File("./format/lp_tree.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        lpJson = JSON.parseArray(origin);
    }

    private static void initZLJson() throws Exception {
        File file = new File("./format/zl_tree.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        zlJson = JSON.parseArray(origin);
    }

    private static void init() throws Exception {
        initBossJson();
        initLPJson();
        initZLJson();
    }

    private static void deepAndGenerateKw(Map<String, List<DividedWord>> map, JSONObject obj) throws IOException {
        if (obj == null || obj.getInteger("level") > 3) {
            return;
        }
        String name = obj.getString("name");
        List<DividedWord> dw = DividedWordsUtil.dividedWords(name);
        map.put(name, dw);
        for (DividedWord temp : dw) {
            Integer cnt = keyStatistic.get(temp.getWord());
            if (cnt == null) {
                keyStatistic.put(temp.getWord(), 1);
            } else {
                keyStatistic.put(temp.getWord(), cnt + 1);
            }
        }
        JSONArray children = obj.getJSONArray("children");
        for (int i = 0 ; i < children.size() ; i ++) {
            deepAndGenerateKw(map, children.getJSONObject(i));
        }
    }

    private static void generateKw() throws IOException {
        for (int i = 0 ; i < lpJson.size() ; i ++) {
            deepAndGenerateKw(lpKwMap, lpJson.getJSONObject(i));
        }

        for (int i = 0 ; i < bossJson.size() ; i ++) {
            deepAndGenerateKw(bossKwMap, bossJson.getJSONObject(i));
        }

        for (int i = 0 ; i < zlJson.size() ; i ++) {
            deepAndGenerateKw(zlKwMap, zlJson.getJSONObject(i));
        }
    }

    private static void initWordDiction() throws Exception {
        File file = new File("./jobwords.dic");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Set<String> wordsSet = new HashSet<>();
        while (true) {
            String temp = br.readLine();
            if (temp == null || "".equals(temp)) {
                break;
            }
            wordsSet.add(temp);
        }
        Dictionary.initial(DefaultConfig.getInstance());
        Dictionary.getSingleton().addWords(wordsSet);
        Set<String> disWords = new HashSet<>();
        disWords.add("金工");
        disWords.add("装工");
        disWords.add("织工");
        disWords.add("染工");
        disWords.add("医科");
        disWords.add("画师");
        disWords.add("售与");
        disWords.add("车主");
        disWords.add("电信网");
        disWords.add("光网");
        disWords.add("定员");
        disWords.add("输电线");
        disWords.add("美容师");
        disWords.add("美容美发");
        disWords.add("绩效考核");
        Dictionary.getSingleton().disableWords(disWords);
    }

    private static void deepJobTree(JSONObject obj, Executor<JSONObject> executor) throws Exception {
        if (obj.getInteger("level") >= 3) {
            executor.exec(obj);
        } else {
            deepJobTree(obj.getJSONArray("children"), executor);
        }
    }

    private static void deepJobTree(JSONArray arr, Executor<JSONObject> executor) throws Exception {
        for (int i = 0 ; i < arr.size() ; i ++) {
            deepJobTree(arr.getJSONObject(i), executor);
        }
    }

    private static String deepFindInTree(JSONObject obj, List<DividedWord> words) throws IOException {
        JSONArray children = obj.getJSONArray("children");
        int level = obj.getInteger("level");
        String name = obj.getString("name");
        if (children.isEmpty()) {
            List<DividedWord> dw = DividedWordsUtil.dividedWords(name);
            if(fit(dw, words) > 0) {
                return name;
            }
        } else {
            String res = deepFindInTree(children, words);
            if (res != null && res.length() > 0 && level >= 3) {
                return name;
            }
        }
        return null;
    }

    private static String deepFindInTree(JSONArray arr, List<DividedWord> words) throws IOException {
        for (int i = 0 ; i < arr.size() ; i ++) {
            String res = deepFindInTree(arr.getJSONObject(i), words);
            if (res != null && res.length() > 0) {
                return res;
            }
        }
        return null;
    }

    private static boolean contains(List<DividedWord> words, DividedWord word) {
        for (int i = 0 ; i < words.size() ; i ++) {
            if (words.get(i).getWord().equals(word.getWord())) {
                return true;
            }
        }
        return false;
    }

    private static boolean allOfWords(List<DividedWord> aList, List<DividedWord> bList) {
        if (aList.size() != bList.size()) {
            return false;
        }
        for (int i = 0 ; i < aList.size() ; i ++) {
            if (!contains(aList, bList.get(i)) || !contains(bList, aList.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean multiWords(List<DividedWord> aList, List<DividedWord> bList, int filter) {
        int cnt = 0;
        for (int i = 0 ; i < aList.size() ; i ++) {
            if (contains(bList, aList.get(i))) {
                cnt ++;
            }
        }
        if (cnt >= filter) {
            return true;
        }
        cnt = 0;
        for (int i = 0 ; i < bList.size() ; i ++) {
            if (contains(aList, bList.get(i))) {
                cnt ++;
            }
        }
        if (cnt >= filter) {
            return true;
        }
        return false;
    }

    private static boolean subSetWords(List<DividedWord> aList, List<DividedWord> bList) {
        int i = 0;
        for (i = 0 ; i < aList.size() ; i ++) {
            if (!contains(bList, aList.get(i))) {
                break;
            }
        }
        if (i >= aList.size()) {
            return true;
        }
        for (i = 0 ; i < bList.size() ; i ++) {
            if (!contains(aList, bList.get(i))) {
                break;
            }
        }
        if (i >= bList.size()) {
            return true;
        }
        return false;
    }

    private static int fit(List<DividedWord> aList, List<DividedWord> bList) {
        if (aList.size() <= 0 || bList.size() <= 0) {
            return 0;
        }
        //全词匹配
        if (allOfWords(aList, bList)) {
            return 1;
        }
        //三词匹配
        if (multiWords(aList, bList, 2)) {
            return 2;
        }
        //词汇子集（双向）
        if (subSetWords(aList, bList)) {
            return 3;
        }
        return 0;
    }

    private static String deepFitBoss(List<DividedWord> words) throws IOException {
        return deepFindInTree(bossJson, words);
    }

    private static String deepFitZL(List<DividedWord> words) throws IOException {
        return deepFindInTree(zlJson, words);
    }

    private static Pair<String, Integer> searchInBoss(List<DividedWord> words) throws IOException {
        Set<Map.Entry<String, List<DividedWord>>> entrySet = bossKwMap.entrySet();
        for (Map.Entry<String, List<DividedWord>> temp : entrySet) {
            int res = fit(temp.getValue(), words);
            if (res > 0) {
                return new Pair<>(temp.getKey(), res);
            }
//            else if (multiWords(temp.getValue(), words, 1)){
//                return new Pair<>(temp.getKey(), 4);
//            }
        }
//        String deepRes = deepFitBoss(words);
//        if (deepRes != null && deepRes.length() > 0) {
//            return deepRes;
//        }
        return null;
    }

    private static Pair<String, Integer> searchInZL(List<DividedWord> words) throws IOException {
        Set<Map.Entry<String, List<DividedWord>>> entrySet = zlKwMap.entrySet();
        for (Map.Entry<String, List<DividedWord>> temp : entrySet) {
            int res = fit(temp.getValue(), words);
            if (res > 0) {
                return new Pair<>(temp.getKey(), res);
            }
//            else if (multiWords(temp.getValue(), words, 1)) {
//                return new Pair<>(temp.getKey(), res);
//            }
        }
//        String deepRes = deepFitZL(words);
//        if (deepRes != null && deepRes.length() > 0) {
//            return deepRes;
//        }
        return null;
    }

    private static int total = 0;
    private static int zero = 0;
    private static int one = 0;
    private static int two = 0;
    private static int boss = 0;
    private static int zl = 0;

    private static void deepLPJobTree() throws Exception {

        deepJobTree(lpJson, new Executor<JSONObject>() {
            @Override
            public void exec(JSONObject jsonObject) throws Exception {
                List<DividedWord> words = lpKwMap.get(jsonObject.getString("name"));
                Pair<String, Integer> bossRes = searchInBoss(words);
                Pair<String, Integer> zlRes = searchInZL(words);
//                System.out.println(jsonObject.getString("name") + " - " + bossRes + " - " + zlRes);
                total ++;
                if (bossRes != null) boss ++;
                if (zlRes != null) zl ++;
                if (bossRes == null && zlRes != null) one ++;
                if (bossRes != null && zlRes == null) one ++;
                if (bossRes != null && zlRes != null) two ++;
                if (bossRes == null && zlRes == null) {
                    zero ++;
//                    System.out.println(jsonObject.getString("name") + " - " + bossRes + " - " + zlRes);
                }
            }
        });
        System.out.println("total: " + total);
        System.out.println("zero: " + zero);
        System.out.println("one: " + one);
        System.out.println("two: " + two);
        System.out.println("boss: " + boss);
        System.out.println("zl: " + zl);
    }

    public static void main(String[] args) throws Exception {
        init();
        initWordDiction();//词库初始化
        generateKw();//对所有三级职类进行分词并生成关键词表
        deepLPJobTree();//遍历LPTree并且对每个三级职类进行判断

        List<DividedWord> words = lpKwMap.get("绩效经理/主管");
        System.out.println(words.size());
        for (int i = 0 ; i < words.size() ; i ++) {
            System.out.println(words.get(i).getWord());
        }

        List<DividedWord> words2 = bossKwMap.get("绩效考核");
        System.out.println(words2.size());
        for (int i = 0 ; i < words2.size() ; i ++) {
            System.out.println(words2.get(i).getWord());
        }
    }

}
