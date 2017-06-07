import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
public class Commit implements java.io.Serializable {
    private final int YEAR = 1900;
    private int id;
    private String name;
    private String time;
    private Commit parent;
    private HashMap<String, Commit> fileInheritMap;
    private HashSet<String> files;
    public Commit(Commit c) {
        Date d = new Date();
        String curTime = String.format("%02d:%02d:%02d", d.getHours(), 
            d.getMinutes(), d.getSeconds());
        String curDate = String.format("%d-%02d-%02d", (d.getYear() + YEAR), 
            d.getMonth(), d.getDate());
        String timeStamp = (curDate + " " + curTime);
        this.id = c.id;
        this.name = c.name;
        this.time = timeStamp;
        this.fileInheritMap = c.fileInheritMap;
        this.files = c.files;
    }
    public Commit(int id, String name, String time) {
        this.id = id;
        this.name = name;
        this.time = time;
        fileInheritMap = new HashMap<String, Commit>();
        files = new HashSet<String>();
    }
    public HashSet<String> getTrackedFiles() {
        return files;
    }
    public HashMap<String, Commit> getInheritMap() {
        return fileInheritMap;
    }
    public void changeId(int idNum) {
        this.id = idNum;
    }
    public void changeMap(HashMap<String, Commit> hM) {
        fileInheritMap = hM;
    }
    public void inheritFrom(String s, Commit c) {
        fileInheritMap.put(s, c);
    }
    public void removeInherit(String s) {
        fileInheritMap.remove(s);
    }
    public void setParent(Commit commit) {
        parent = commit;
    }
    public Commit parent() {
        return parent;
    }
    public int id() {
        return id;
    }
    public void changeMsg(String msg) {
        name = msg;
    }
    public String name() {
        return name;
    }
    public String time() {
        return time;
    }
}
