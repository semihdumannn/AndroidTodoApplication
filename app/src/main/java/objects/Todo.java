package objects;

import java.util.HashMap;
import java.util.Map;

public class Todo {
    String title,desc,date,key,userId;
    Boolean finished;

    public Todo() {
    }

    public Todo(String title, String desc, String date, String key, Boolean finished,String userId) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.key = key;
        this.finished = finished != null ? finished : false;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("title", title);
        result.put("desc", desc);
        result.put("date", date);
        result.put("finished", finished);
        result.put("userId",userId);
        return result;
    }


}
