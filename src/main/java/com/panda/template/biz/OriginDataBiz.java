package com.panda.template.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.template.bean.LPTagBean;
import com.panda.template.bean.NodeBean;
import com.panda.template.bean.TagBean;
import com.panda.template.utils.words.DividedWord;
import com.panda.template.utils.words.DividedWordsUtil;
import com.panda.template.utils.words.Executor;
import javafx.util.Pair;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class OriginDataBiz {

    private JSONArray bossJson;
    private JSONArray zlJson;
    private JSONArray lpJson;

    private Map<String, List<DividedWord>> lpKwMap = new HashMap<>();
    private Map<String, List<DividedWord>> bossKwMap = new HashMap<>();
    private Map<String, List<DividedWord>> zlKwMap = new HashMap<>();

    private Map<String, List<TagBean>> bossTagMap = new HashMap<>();
    private Map<String, List<TagBean>> zlTagMap = new HashMap<>();
    private Map<String, List<TagBean>> lpTagMap = new HashMap<>();

    private Map<String, LPTagBean> lpPreTagMap = new HashMap<>();

    private Map<String, Integer> keyStatistic = new HashMap<>();

    @PostConstruct
    private void start() throws Exception {
        init();
        initWordDiction();//词库初始化
        generateKw();//对所有三级职类进行分词并生成关键词表
        deepLPJobTree();//遍历LPTree并且对每个三级职类进行判断
    }

    private JSONObject findObj(JSONObject obj, String name) {
        if (obj.getString("name").equals(name)) {
            return obj;
        } else {
            JSONArray children = obj.getJSONArray("children");
            return findObj(children, name);
        }
    }

    private JSONObject findObj(JSONArray arr, String name) {
        if (arr == null || arr.isEmpty()) {
            return null;
        }
        for (int i = 0 ; i < arr.size() ; i ++) {
            JSONObject temp = arr.getJSONObject(i);
            JSONObject res = findObj(temp, name);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public LPTagBean getTags(String name) {
        return this.lpPreTagMap.get(name);
    }

    public List<NodeBean> getNode(String name) {
        List<NodeBean> result = new ArrayList<>();
        if (name == null || "".equals(name)) {
            for (int i = 0 ; i < lpJson.size() ; i ++) {
                JSONObject temp = lpJson.getJSONObject(i);
                result.add(new NodeBean(temp.getString("name")));
            }
            return result;
        } else {
            JSONObject res = findObj(lpJson, name);
            if (res == null) {
                return new ArrayList<>();
            } else {
                JSONArray arr = res.getJSONArray("children");
                for (int i = 0 ; i < arr.size() ; i ++) {
                    JSONObject temp = arr.getJSONObject(i);
                    result.add(new NodeBean(temp.getString("name")));
                }
                return result;
            }
        }
    }

    public Map<String, LPTagBean> getLpPreTagMap() {
        return lpPreTagMap;
    }

    private void initBossJson() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("data/format/boss_tree.txt").getFile());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        this.bossJson = JSON.parseArray(origin);
    }

    private void initLPJson() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("data/format/lp_tree.txt").getFile());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        this.lpJson = JSON.parseArray(origin);
    }

    private void initZLJson() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("data/format/zl_tree.txt").getFile());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        this.zlJson = JSON.parseArray(origin);
    }

    private void init() throws Exception {
        this.initBossJson();
        this.initLPJson();
        this.initZLJson();
    }

    private void deepAndGenerateTags(Map<String, List<TagBean>> tagMap, JSONObject obj) {
        int level = obj.getInteger("level");
        if (level > 3) {
            return;
        }
        String jobKindName = obj.getString("name");
        JSONArray children = obj.getJSONArray("children");
        if (children == null || children.isEmpty()) {
            return;
        }
        List<TagBean> tagBeans = new ArrayList<>();
        for (int i = 0 ; i < children.size() ; i ++) {
            JSONObject temp = children.getJSONObject(i);
            JSONArray tempChildren = temp.getJSONArray("children");
            if (tempChildren == null || tempChildren.isEmpty()) {
                continue;
            }
            for (int j = 0 ; j < tempChildren.size() ; j ++) {
                JSONObject tagObj = tempChildren.getJSONObject(j);
                tagBeans.add(new TagBean(tagObj.getString("name"), temp.getString("name")));
            }
        }
//        System.out.println("设置标签：" + jobKindName + "-" + tagBeans.size());
        tagMap.put(jobKindName, tagBeans);
    }

    private void deepAndGenerateKw(Map<String, List<DividedWord>> map, Map<String, List<TagBean>> tagMap, JSONObject obj) throws IOException {
        if (obj == null || obj.getInteger("level") > 3) {
            return;
        }
        if (obj == null || obj.getInteger("level") == 3) {
            this.deepAndGenerateTags(tagMap, obj);
        }
        String name = obj.getString("name");
        List<DividedWord> dw = DividedWordsUtil.dividedWords(name);
        map.put(name, dw);
        for (DividedWord temp : dw) {
            Integer cnt = this.keyStatistic.get(temp.getWord());
            if (cnt == null) {
                this.keyStatistic.put(temp.getWord(), 1);
            } else {
               this. keyStatistic.put(temp.getWord(), cnt + 1);
            }
        }
        JSONArray children = obj.getJSONArray("children");
        for (int i = 0 ; i < children.size() ; i ++) {
            this.deepAndGenerateKw(map, tagMap, children.getJSONObject(i));
        }
    }

    private void generateKw() throws IOException {
        for (int i = 0 ; i < this.lpJson.size() ; i ++) {
            this.deepAndGenerateKw(this.lpKwMap, this.lpTagMap, this.lpJson.getJSONObject(i));
        }

        for (int i = 0 ; i < this.bossJson.size() ; i ++) {
            this.deepAndGenerateKw(this.bossKwMap, this.bossTagMap, this.bossJson.getJSONObject(i));
        }

        for (int i = 0 ; i < this.zlJson.size() ; i ++) {
            this.deepAndGenerateKw(this.zlKwMap, this.zlTagMap, this.zlJson.getJSONObject(i));
        }
    }

    private void initWordDiction() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("data/jobwords.dic").getFile());
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

    private void deepJobTree(JSONObject obj, Executor<JSONObject> executor) throws Exception {
        if (obj.getInteger("level") >= 3) {
            executor.exec(obj);
        } else {
            this.deepJobTree(obj.getJSONArray("children"), executor);
        }
    }

    private void deepJobTree(JSONArray arr, Executor<JSONObject> executor) throws Exception {
        for (int i = 0 ; i < arr.size() ; i ++) {
            this.deepJobTree(arr.getJSONObject(i), executor);
        }
    }

    private String deepFindInTree(JSONObject obj, List<DividedWord> words) throws IOException {
        JSONArray children = obj.getJSONArray("children");
        int level = obj.getInteger("level");
        String name = obj.getString("name");
        if (children.isEmpty()) {
            List<DividedWord> dw = DividedWordsUtil.dividedWords(name);
            if(fit(dw, words) > 0) {
                return name;
            }
        } else {
            String res = this.deepFindInTree(children, words);
            if (res != null && res.length() > 0 && level >= 3) {
                return name;
            }
        }
        return null;
    }

    private String deepFindInTree(JSONArray arr, List<DividedWord> words) throws IOException {
        for (int i = 0 ; i < arr.size() ; i ++) {
            String res = this.deepFindInTree(arr.getJSONObject(i), words);
            if (res != null && res.length() > 0) {
                return res;
            }
        }
        return null;
    }

    private boolean contains(List<DividedWord> words, DividedWord word) {
        for (int i = 0 ; i < words.size() ; i ++) {
            if (words.get(i).getWord().equals(word.getWord())) {
                return true;
            }
        }
        return false;
    }

    private boolean allOfWords(List<DividedWord> aList, List<DividedWord> bList) {
        if (aList.size() != bList.size()) {
            return false;
        }
        for (int i = 0 ; i < aList.size() ; i ++) {
            if (!this.contains(aList, bList.get(i)) || !this.contains(bList, aList.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean multiWords(List<DividedWord> aList, List<DividedWord> bList, int filter) {
        int cnt = 0;
        for (int i = 0 ; i < aList.size() ; i ++) {
            if (this.contains(bList, aList.get(i))) {
                cnt ++;
            }
        }
        if (cnt >= filter) {
            return true;
        }
        cnt = 0;
        for (int i = 0 ; i < bList.size() ; i ++) {
            if (this.contains(aList, bList.get(i))) {
                cnt ++;
            }
        }
        if (cnt >= filter) {
            return true;
        }
        return false;
    }

    private boolean subSetWords(List<DividedWord> aList, List<DividedWord> bList) {
        int i = 0;
        for (i = 0 ; i < aList.size() ; i ++) {
            if (!this.contains(bList, aList.get(i))) {
                break;
            }
        }
        if (i >= aList.size()) {
            return true;
        }
        for (i = 0 ; i < bList.size() ; i ++) {
            if (!this.contains(aList, bList.get(i))) {
                break;
            }
        }
        if (i >= bList.size()) {
            return true;
        }
        return false;
    }

    private int fit(List<DividedWord> aList, List<DividedWord> bList) {
        if (aList == null || bList == null || aList.size() <= 0 || bList.size() <= 0) {
            return 0;
        }
        //全词匹配
        if (this.allOfWords(aList, bList)) {
            return 1;
        }
        //三词匹配
        if (this.multiWords(aList, bList, 2)) {
            return 2;
        }
        //词汇子集（双向）
        if (this.subSetWords(aList, bList)) {
            return 3;
        }
        return 0;
    }

    private String deepFitBoss(List<DividedWord> words) throws IOException {
        return this.deepFindInTree(bossJson, words);
    }

    private String deepFitZL(List<DividedWord> words) throws IOException {
        return this.deepFindInTree(zlJson, words);
    }

    private Pair<String, Integer> searchInBoss(List<DividedWord> words) throws IOException {
        Set<Map.Entry<String, List<DividedWord>>> entrySet = this.bossKwMap.entrySet();
        for (Map.Entry<String, List<DividedWord>> temp : entrySet) {
            int res = this.fit(temp.getValue(), words);
            if (res > 0) {
                return new Pair<>(temp.getKey(), res);
            } else if (multiWords(temp.getValue(), words, 1)){
                return new Pair<>(temp.getKey(), 4);
            }
        }
        return null;
    }

    private Pair<String, Integer> searchInZL(List<DividedWord> words) throws IOException {
        Set<Map.Entry<String, List<DividedWord>>> entrySet = this.zlKwMap.entrySet();
        for (Map.Entry<String, List<DividedWord>> temp : entrySet) {
            int res = this.fit(temp.getValue(), words);
            if (res > 0) {
                return new Pair<>(temp.getKey(), res);
            } else if (multiWords(temp.getValue(), words, 1)) {
                return new Pair<>(temp.getKey(), 4);
            }
        }
        return null;
    }

    private int total = 0;
    private int zero = 0;
    private int one = 0;
    private int two = 0;
    private int boss = 0;
    private int zl = 0;

    private void deepLPJobTree() throws Exception {

        this.deepJobTree(this.lpJson, new Executor<JSONObject>() {
            @Override
            public void exec(JSONObject jsonObject) throws Exception {
                String name = jsonObject.getString("name");
                List<DividedWord> words = lpKwMap.get(name);
                if (words == null) {
                    System.out.println("猎聘职类分词为空: " + name);
                    return;
                }
                Pair<String, Integer> bossRes = searchInBoss(words);
                Pair<String, Integer> zlRes = searchInZL(words);
//                System.out.println(jsonObject.getString("name") + " - " + bossRes + " - " + zlRes);
                LPTagBean lpTagBean = new LPTagBean();
                if (bossRes != null) {//根据匹配到的Boss的三级职类获取标签列表
                    List<TagBean> bossTagList = bossTagMap.get(bossRes.getKey());
                    lpTagBean.setBossTagList(bossTagList);
                    lpTagBean.setBossJobKind(bossRes.getKey());
                    lpTagBean.setBossType(bossRes.getValue());
                }
                if (zlRes != null) {//根据匹配到的智联招聘的三级职类获取标签列表
                    List<TagBean> zlTagList = zlTagMap.get(zlRes.getKey());
                    lpTagBean.setZlTagList(zlTagList);
                    lpTagBean.setZlJobKind(zlRes.getKey());
                    lpTagBean.setZlType(zlRes.getValue());
                }
                lpPreTagMap.put(name, lpTagBean);
                total ++;
                if (bossRes != null) boss ++;
                if (zlRes != null) zl ++;
                if (bossRes == null && zlRes != null) one ++;
                if (bossRes != null && zlRes == null) one ++;
                if (bossRes != null && zlRes != null) two ++;
                if (bossRes == null && zlRes == null) zero ++;
            }
        });
        System.out.println("total: " + total);
        System.out.println("zero: " + zero);
        System.out.println("one: " + one);
        System.out.println("two: " + two);
        System.out.println("boss: " + boss);
        System.out.println("zl: " + zl);
    }

}
