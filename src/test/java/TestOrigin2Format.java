import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TestOrigin2Format {

    private static JSONArray formatBossArray = new JSONArray();
    private static JSONArray formatZhiLianArray = new JSONArray();
    private static JSONArray formatLiePinArray = new JSONArray();

    private static void deepFormatBoss(JSONArray childrenArr, JSONObject origin, int level) {
        JSONObject formatObj = new JSONObject();
        formatObj.put("name", origin.getString("name"));
        JSONArray children = new JSONArray();
        formatObj.put("children", children);
        formatObj.put("level", level);
        childrenArr.add(formatObj);

        JSONArray originChildren = origin.getJSONArray("subLevelModelList");
        if (originChildren != null && origin.size() > 0) {
            for (int i = 0 ; i < originChildren.size() ; i ++) {
                deepFormatBoss(children, originChildren.getJSONObject(i), level + 1);
            }
        }
    }

    private static void formatBoss() throws Exception {
        File file = new File("boss_job_kind.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        JSONArray originJson = JSON.parseArray(origin);
        for (int i = 0 ; i < originJson.size() ; i ++) {
            JSONObject temp = originJson.getJSONObject(i);
            deepFormatBoss(formatBossArray, temp, 1);
        }
    }

    private static void deepFormatZL(JSONArray childrenArr, JSONObject origin, int level) {
        JSONObject formatObj = new JSONObject();
        formatObj.put("name", origin.getString("name"));
        JSONArray children = new JSONArray();
        formatObj.put("children", children);
        formatObj.put("level", level);
        childrenArr.add(formatObj);

        JSONArray originChildren = origin.getJSONArray("children");
        if (originChildren != null && origin.size() > 0) {
            for (int i = 0 ; i < originChildren.size() ; i ++) {
                deepFormatZL(children, originChildren.getJSONObject(i), level + 1);
            }
        }
    }

    private static void formatZhiLian() throws Exception {
        File file = new File("zl_job_kind.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String origin = br.readLine();
        JSONArray originJson = JSON.parseArray(origin);
        for (int i = 0 ; i < originJson.size() ; i ++) {
            JSONObject temp = originJson.getJSONObject(i);
            deepFormatZL(formatZhiLianArray, temp, 1);
        }
    }

    private static JSONObject findNode(JSONArray arr, String name) {
        for (int i = 0 ; i < arr.size() ; i ++) {
            JSONObject temp = arr.getJSONObject(i);
            if (name.equals(temp.getString("name"))) {
                return temp;
            }
        }
        return null;
    }

    private static void addLiePinNode(String one, String two, String three) {
        JSONObject oneObj = findNode(formatLiePinArray, one);
        if (oneObj == null) {
            oneObj = new JSONObject();
            oneObj.put("name", one);
            oneObj.put("children", new JSONArray());
            oneObj.put("level", 1);
            formatLiePinArray.add(oneObj);
        }
        JSONArray oneChildren = oneObj.getJSONArray("children");
        JSONObject twoObj = findNode(oneChildren, two);
        if (twoObj == null) {
            twoObj = new JSONObject();
            twoObj.put("name", two);
            twoObj.put("children", new JSONArray());
            twoObj.put("level", 2);
            oneChildren.add(twoObj);
        }
        JSONArray twoChildren = twoObj.getJSONArray("children");
        JSONObject threeObj = findNode(twoChildren, three);
        if (threeObj == null) {
            threeObj = new JSONObject();
            threeObj.put("name", three);
            threeObj.put("children", new JSONArray());
            threeObj.put("level", 3);
            twoChildren.add(threeObj);
        }
    }

    private static void formatLiePin() throws Exception {
        File file = new File("lp_job_kind.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        while(true) {
            String temp = br.readLine();
            if (temp == null || "".equals(temp)) {
                break;
            }
            String[] ss = temp.split("\t");
            addLiePinNode(ss[0], ss[1], ss[2]);
        }
    }

    public static void main(String[] args) throws Exception {
        formatLiePin();
        System.out.println(formatLiePinArray.toJSONString());
        System.out.println(formatLiePinArray.size());
    }
}
