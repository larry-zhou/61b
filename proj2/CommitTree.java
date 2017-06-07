import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.io.Serializable;
import java.util.HashSet;

public class CommitTree implements Serializable {
    private int commitNum = 0;
    private String currBranch;
    private HashMap<String, Commit> branchHead;
    private HashSet<Commit> commits;
    private HashMap<String, ArrayList<Integer>> commitIds;
    private Commit head;
    private HashSet<String> added;
    private HashSet<String> removed;
    private HashMap<String, String> fileMap;
    public void addCommitNum() {
        commitNum += 1;
    }

    public void removeBranch(String s) {
        if (!branchHead.containsKey(s)) {
            System.out.println("A branch with that name does not exist.");
        } else if (s.equals(currBranch)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branchHead.remove(s);
        }
    }
    public HashMap<String, String> getFileMap() {
        return fileMap;
    }

    public String getCurrBranch() {
        return currBranch;
    }
    public  HashMap<String, ArrayList<Integer>> commitIdMap() {
        return commitIds;
    }
    public void createBranch(String s, Commit c) {
        branchHead.put(s, c);
    }
    public TreeSet<String> getBranches() {
        TreeSet<String> branches = new TreeSet<String>(branchHead.keySet());
        return branches;
    }
    public void changeBranch(String s) {
        currBranch = s;
    }
    public Commit getBranchHead(String s) {
        return branchHead.get(s);
    }
    public void removeStagedFile(String s) {
        added.remove(s);
    }
    public void removeFile(String s) {
        removed.add(s);
    }
    public void removeFilePath(String s) {
        fileMap.remove(s);
    }
    public HashSet<String> getRemovedFiles() {
        return removed;
    }

    public HashSet<String> getStagedFiles() {
        return added;
    }

    public int getCommitNum() {
        return commitNum;
    }

    public String getFilePath(String s) {
        return fileMap.get(s);
    }

    public CommitTree(Commit commitData) {
        commits = new HashSet<Commit>();
        commitIds = new HashMap<String, ArrayList<Integer>>();
        added = new HashSet<String>();
        removed = new HashSet<String>();
        fileMap = new HashMap<String, String>();
        branchHead = new HashMap<String, Commit>();
        head = commitData;
        currBranch = "master";
        branchHead.put(currBranch, commitData);
        commits.add(commitData);
        commitNum += 1;
    }

    public void addFile(String file, String path) {
        fileMap.put(file, path);
    }
    public void stage(String fileName) {
        added.add(fileName);
    }
    public void add(Commit commit) {
        if (!commitIds.containsKey(commit.name())) {
            ArrayList<Integer> aL = new ArrayList<Integer>();
            aL.add(commit.id());
            commitIds.put(commit.name(), aL);
        } else {
            ArrayList<Integer> aL = commitIds.get(commit.name());
            aL.add(commit.id());
            commitIds.put(commit.name(), aL);
        }
        commit.setParent(branchHead.get(currBranch));
        commits.add(commit);
        branchHead.put(currBranch, commit);
        head = commit;
        added = new HashSet<String>();
        commitNum += 1;

    }
    public Commit getCurrCommit() {
        return branchHead.get(currBranch);
    }
}
