import java.io.File;
import java.util.HashMap;
import java.util.Date;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
public class Gitlet {
    private static CommitTree cT;
    private static final int YEAR = 1900;

    private static Commit findSplitPoint(Commit curr, Commit giv) {
        Commit splitPoint = null;
        HashSet<Integer> currBranchIds = new HashSet<Integer>();
        while (curr.parent() != null) {
            currBranchIds.add(curr.id());
            curr = curr.parent();
        }
        currBranchIds.add(curr.id());
        while (giv.parent() != null) {
            if (currBranchIds.contains(giv.id())) {
                splitPoint = giv;
                return splitPoint;
            } else { 
                giv = giv.parent();
            }
        }
        if (splitPoint == null) {
            splitPoint = giv;
            return splitPoint;
        }
        return splitPoint;
    }
    private static boolean dangerous() {
        String one = "Warning: The command you entered may alter the files ";
        String two = "in your working directory. Uncommitted changes may be lost. ";
        String three = "Are you sure you want to continue? (yes/no)";
        System.out.println(one + two + three);
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if ("yes".equals(input)) {
                break;
            } else {
                System.out.println("You did not enter 'yes'. Not continuing dangerous method.");
                return true;
            }
        }
        return false;
    }
    private static void readOutCommit(Commit cM) {
        try {
            File f = new File(".gitlet/Commit " + Integer.toString(cM.id()));
            if (!f.exists()) {
                f.mkdir();
            }
            File commitFile = new File(".gitlet/Commit " + Integer.toString(cM.id()) 
                + "/Commit " + Integer.toString(cM.id()) + ".ser");
            FileOutputStream fileOut = new FileOutputStream(commitFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(cM);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            String msg = "IOException while saving Commit #.ser";
            System.out.println(msg);
        }
    }

    private static Commit readInCommit(int id) {
        File myCommit = new File(".gitlet/Commit " + Integer.toString(id) 
            + "/Commit " + Integer.toString(id) + ".ser");
        Object com = null;
        if (myCommit.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(myCommit);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                com = objectIn.readObject();
                fileIn.close();
                objectIn.close();
                return (Commit) com;
            } catch (IOException e) {
                e.printStackTrace();
                String msg = "IOException while loading comTree.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading comTree.";
                System.out.println(msg);
            }
        }
        return (Commit) com;
    }

    private static CommitTree readIn() {
        File myCommitTree = new File(".gitlet/" + "CommitTree.ser");
        Object comTree = null;
        if (myCommitTree.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(myCommitTree);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                comTree = objectIn.readObject();
                fileIn.close();
                objectIn.close();
                return (CommitTree) comTree;
            } catch (IOException e) {
                e.printStackTrace();
                String msg = "IOException while loading comTree.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading comTree.";
                System.out.println(msg);
            }
        }
        return (CommitTree) comTree;
    }

    private static void readOut(CommitTree commTree) {
        try {
            File initialCommitFile = new File(".gitlet/" + "CommitTree.ser");
            FileOutputStream fileOut = new FileOutputStream(initialCommitFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(commTree);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            String msg = "IOException while saving Commit 0.ser";
            System.out.println(msg);
        }
    }

    public static void initialize() {
        Date d = new Date();
        String curTime = String.format("%02d:%02d:%02d", d.getHours(), 
            d.getMinutes(), d.getSeconds());
        String curDate = String.format("%d-%02d-%02d", (d.getYear() + YEAR), 
            d.getMonth(), d.getDate());
        String timeStamp = (curDate + " " + curTime);
        Commit c = new Commit(0, "initial commit", timeStamp);
        cT = new CommitTree(c);
        File f = new File(".gitlet");
        if (f.exists()) {
            System.out.println("A gitlet version control system" 
                + "already exists in the current directory.");
            return;
        }
        if (!f.exists()) {
            f.mkdir();
        }
        File z = new File(".gitlet/Commit " + c.id());
        if (!z.exists()) {
            z.mkdir();
        }
        readOut(cT);
        readOutCommit(c);    
    }

    public static void add(String fileName) {
        cT = readIn();
        // File g = cT.getFile(fileName);
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (cT.getRemovedFiles().contains(fileName)) {
            cT.getRemovedFiles().remove(fileName);
            readOut(cT);
            return;
        }
        if (cT.getFilePath(f.getName()) == null) {
            cT.stage(fileName);
            readOut(cT);
            return;
        }
        if (f.exists() && !f.isDirectory()) {
            try {
                //stack overflow
                Path path = Paths.get(fileName);
                byte[] data = Files.readAllBytes(path);
                Path path2 = Paths.get(".gitlet/Commit " + cT.getCurrCommit().id() 
                    + "/" + fileName);
                byte[] data2 = Files.readAllBytes(path2);
                if (Arrays.equals(data, data2)) {
                    System.out.println("File has not been modified since the last commit.");
                    return;
                } else {
                    cT.stage(fileName);
                    readOut(cT);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void commit(String commitMsg) {
        //stack overflow
        Date d = new Date();
        String curTime = String.format("%02d:%02d:%02d", d.getHours(), 
            d.getMinutes(), d.getSeconds());
        String curDate = String.format("%d-%02d-%02d", (d.getYear() + YEAR), 
            d.getMonth(), d.getDate());
        String timeStamp = (curDate + " " + curTime);
        cT = readIn();
        if (cT.getStagedFiles().isEmpty() && cT.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit c = new Commit(cT.getCommitNum(), commitMsg, timeStamp);
        File f = new File(".gitlet/Commit " + c.id());
        if (!f.exists()) {
            f.mkdir();
        }
        for (String s : cT.getStagedFiles()) {
            try {
                Path source = Paths.get(s);
                File z = new File(".gitlet/Commit " + c.id() + "/" + s);
                z.getParentFile().mkdirs();
                Path dest = Paths.get(".gitlet/Commit " + c.id() + "/" + s);
                Files.copy(source, dest);

            } catch (IOException e) {
                e.printStackTrace();
                String msg = "IOException while trying to copy files1238901283";
                System.out.println(msg);
            }
            c.inheritFrom(s, c);
            cT.addFile(s, (".gitlet/Commit " + c.id() + "/" + s));
        }
        c.setParent(cT.getCurrCommit());
        for (String s : cT.getRemovedFiles()) {
            cT.removeFilePath(s);
        }
        HashMap<String, Commit> m1 = new HashMap<String, Commit>(c.getInheritMap());
        HashMap<String, Commit> m2 = new HashMap<String, Commit>(c.parent().getInheritMap());
        for (String s : m1.keySet()) {
            if (m2.containsKey(s)) {
                m2.remove(s);
            }
        }
        //stack overflow
        HashMap<String, Commit> m3 = new HashMap<String, Commit>();
        m3.putAll(m1);
        m3.putAll(m2);
        c.changeMap(m3);
        cT.add(c);
        readOut(cT);
        readOutCommit(c);  
    }

    public static void remove(String fileName) {
        cT = readIn();
        if (!cT.getStagedFiles().contains(fileName) 
            && !cT.getCurrCommit().getInheritMap().containsKey(fileName)) {
            System.out.println("No reason to remove the file");
            return;
        } else if (cT.getStagedFiles().contains(fileName)) {
            cT.removeStagedFile(fileName);
            readOut(cT);
        } else {
            cT.removeFile(fileName);
            readOut(cT);
        }

    }

    public static void log() {
        cT = readIn();
        Commit c = (Commit) (cT.getCurrCommit());
        while (c.parent() != null) {
            System.out.println("====");
            System.out.println("Commit " + c.id());
            System.out.println(c.time());
            System.out.println(c.name());
            System.out.println();
            c = c.parent();
        }
        System.out.println("====");
        System.out.println("Commit " + c.id());
        System.out.println(c.time());
        System.out.println(c.name());
    }

    public static void globalLog() {
        cT = readIn();
        System.out.println(cT.getCommitNum());
        int i = (cT.getCommitNum() - 1);
        while (i != 0) {
            Commit c = readInCommit(i);
            if (c == null) {
                i -= 1;
            } else {
                System.out.println("====");
                System.out.println("Commit " + c.id());
                System.out.println(c.time());
                System.out.println(c.name());
                System.out.println();
                i -= 1;
            }
        } 
        Commit c = readInCommit(0);
        System.out.println("====");
        System.out.println("Commit " + c.id());
        System.out.println(c.time());
        System.out.println(c.name());
    }

    public static void find(String msg) {
        cT = readIn();
        ArrayList<Integer> aL = cT.commitIdMap().get(msg);
        if (aL == null) {
            System.out.println("Found no commit with that message.");
        } else {
            for (int i : aL) {
                System.out.println(i);
            }
        }
    }

    public static void status() {
        cT = readIn();
        System.out.println("=== Branches ===");
        for (String branch : cT.getBranches()) {
            if (branch.equals(cT.getCurrBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String stagedFiles : cT.getStagedFiles()) {
            System.out.println(stagedFiles);
        }
        System.out.println();
        System.out.println("=== Files Marked For Removal ===");
        for (String removedFiles : cT.getRemovedFiles()) {
            System.out.println(removedFiles);
        }
    }

    public static void checkout(int id, String fileName) {
        if (dangerous()) {
            return;
        }
        Commit c = readInCommit(id);
        if (c == null) {
            System.out.println("No commit with that id exists.");
        }
        if (c.getInheritMap().containsKey(fileName)) {
            try {
                //stack overflow
                Commit com = c.getInheritMap().get(fileName);
                Path source = Paths.get(".gitlet/Commit " + com.id() + "/" + fileName);
                Path dest = Paths.get(fileName);
                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                String msg = "IOException while trying to copy files21";
            }
        }
    }

    public static void checkout(String name) {
        if (dangerous()) {
            return;
        }
        cT = readIn();
        if (cT.getBranches().contains(name) && !cT.getCurrBranch().equals(name)) {
            cT.changeBranch(name);
            for (String nm : cT.getCurrCommit().getInheritMap().keySet()) {
                try {
                    Commit inherited = cT.getCurrCommit().getInheritMap().get(nm);
                    // File x = new File(".gitlet/Commit " 
                    //     + Integer.toString(inherited.id()) + "/" + nm);
                    // x.getParentFile().mkdirs();
                    Path source = Paths.get(".gitlet/Commit " 
                        + Integer.toString(inherited.id()) + "/" + nm);
                    Path dest = Paths.get(nm);
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    e.printStackTrace();
                    String msg = "IOException while trying to copy file";
                    System.out.println(msg);
                }
            }
        } else if (cT.getCurrCommit().getInheritMap().containsKey(name)) {
            try {
                Commit c = cT.getCurrCommit().getInheritMap().get(name);
                Path source = Paths.get(".gitlet/Commit " + Integer.toString(c.id()) + "/" + name);
                Path dest = Paths.get(name);
                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
                String msg = "IOException while trying to copy files";
                System.out.println(msg);
            }
        } else {
            if (cT.getCurrBranch().equals(name)) {
                System.out.println("No need to checkout the current branch.");
            } else {
                String m; 
                m = ("File does not exist in the most recent commit, or no such branch exists.");
                System.out.println(m);
                return;
            }
        }
        readOut(cT);
    }

    public static void branch(String branchName) {
        cT = readIn();
        if (cT.getBranches().contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        cT.createBranch(branchName, cT.getCurrCommit());
        readOut(cT);
    }

    public static void removeBranch(String s) {
        cT = readIn();
        if (!cT.getBranches().contains(s)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }        
        cT.removeBranch(s);
        readOut(cT);
    }

    public static void reset(String id) {
        if (dangerous()) {
            return;
        }
        cT = readIn();
        Commit c = readInCommit(Integer.parseInt(id));
        if (c == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        cT.createBranch(cT.getCurrBranch(), c);
        readOut(cT);
        for (String name : c.getInheritMap().keySet()) {
            try {
                Commit inherited = cT.getCurrCommit().getInheritMap().get(name);
                Path source = Paths.get(".gitlet/Commit " 
                    + Integer.toString(inherited.id()) + "/" + name);
                Path dest = Paths.get(name);
                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                String msg = "IOException while trying to copy files1";
                System.out.println(msg);
            }
        }

    }

    // /* Merges files from the head of the given branch into the head of the current branch. */
    public static void merge(String givenBranch) {
        if (dangerous()) {
            return;
        }
        cT = readIn();
        if (givenBranch.equals(cT.getCurrBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (!cT.getBranches().contains(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Commit given = cT.getBranchHead(givenBranch);
        Commit current = cT.getBranchHead(cT.getCurrBranch());
        Commit splitPoint = findSplitPoint(current, given);
        for (String s : current.getInheritMap().keySet()) {
            Commit g = given.getInheritMap().get(s);
            Commit cur = current.getInheritMap().get(s);
            if (given.getInheritMap().containsKey(s)) {
                if ((splitPoint.id()) == (g.id())) {
                    return;
                }
            }
            if (((splitPoint.id()) == (cur.id()) 
                && !((splitPoint.id()) == (g.id())))) {
                try {
                    Commit comSource = given.getInheritMap().get(s);
                    Commit comDest = current.getInheritMap().get(s);
                    Path source = Paths.get(".gitlet/Commit " 
                        + Integer.toString(comSource.id()) + "/" + s);
                    Path dest = Paths.get(s);
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    String msg = "IOException while trying to copy files2";
                    System.out.println(msg);
                }

            }
            if (!(((splitPoint.id()) == (cur.id())) 
                && !((splitPoint.id()) == (g.id())))) {
                if (given.getInheritMap().containsKey(s)) {
                    try {
                        Commit comSource = given.getInheritMap().get(s);
                        Commit comDest = current.getInheritMap().get(s);
                        Path source = Paths.get(".gitlet/Commit " 
                            + Integer.toString(comSource.id()) + "/" + s);
                        Path dest = Paths.get(s + ".conflicted");
                        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException e) {
                        String msg = "IOException while trying to copy files3";
                        System.out.println(msg);
                    }   
                }             
            }
        }
        readOut(cT);

    }

    public static void rebase(String givenBranch) {
        if (dangerous()) {
            return;
        }
        cT = readIn();
        if (givenBranch.equals(cT.getCurrBranch())) {
            System.out.println("Cannot rebase a branch onto itself.");
            return;
        }
        if (!cT.getBranches().contains(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Commit given = cT.getBranchHead(givenBranch);
        Commit current = cT.getBranchHead(cT.getCurrBranch());
        while (current.parent() != null) {
            if (given.id() == current.id()) {
                System.out.println("Already up-to-date.");
                readOut(cT);
                return;
            }
            current = current.parent();
        }
        if (given.id() == current.id()) {
            System.out.println("Already up-to-date.");
            readOut(cT);
            return;
        }
        given = cT.getBranchHead(givenBranch);
        current = cT.getBranchHead(cT.getCurrBranch());
        rebaseHelper(current, given, givenBranch);
        Commit splitPoint = findSplitPoint(current, given);
        HashMap<String, Commit> newMap = new HashMap<String, Commit>();
        for (String s : given.getInheritMap().keySet()) {
            if (!(given.getInheritMap().get(s)).equals(splitPoint.getInheritMap().get(s)) 
                && (current.getInheritMap().get(s)).equals(splitPoint.getInheritMap().get(s))) {
                newMap.put(s, given.getInheritMap().get(s));
            }
        }
        ArrayList<Integer> reversedIdOrder = new ArrayList<Integer>();
        while (splitPoint.id() != current.id()) {
            reversedIdOrder.add(current.id());
            current = current.parent();
        }
        for (int i = reversedIdOrder.size() - 1; i >= 0; i -= 1) {
            int iD = reversedIdOrder.get(i);
            Commit c = readInCommit(iD);
            Commit copy = new Commit(c);
            copy.setParent(given);
            copy.changeId(cT.getCommitNum());
            HashMap<String, Commit> m1 = new HashMap<String, Commit>(copy.getInheritMap());
            HashMap<String, Commit> m3 = new HashMap<String, Commit>();
            m3.putAll(m1);
            m3.putAll(newMap);
            copy.changeMap(m3);
            cT.addCommitNum();
            given = copy;
            cT.createBranch(cT.getCurrBranch(), given);
            readOutCommit(copy);
        }
        readOut(cT);
    }
    public static boolean rebaseHelper(Commit current, Commit given, String givenBranch) {
        while (given.parent() != null) {
            if (given.id() == current.id()) {
                given = cT.getBranchHead(givenBranch);
                cT.createBranch(cT.getCurrBranch(), given);
                readOut(cT);
                return true;
            }
            given = given.parent();
        }
        if (given.id() == current.id()) {
            given = cT.getBranchHead(givenBranch);
            cT.createBranch(cT.getCurrBranch(), given);
            checkout(cT.getCurrBranch());
            readOut(cT);
            return true;
        }
        return false;
    }  

    private static boolean upToDateCheck(Commit current, Commit given) {
        while (current.parent() != null) {
            if (given.id() == current.id()) {
                System.out.println("Already up-to-date.");
                readOut(cT);
                return true;
            }
            current = current.parent();
        }
        if (given.id() == current.id()) {
            System.out.println("Already up-to-date.");
            readOut(cT);
            return true;
        }
        return false;
    }

    private static void replay(Commit rC) {
        System.out.println("Currently replaying:");
        System.out.println("====");
        System.out.println("Commit " + rC.id());
        System.out.println(rC.time());
        System.out.println(rC.name());
        System.out.println();
        System.out.println("Would you like to (c)ontinue, " 
            + "(s)kip this commit, or change this commit's (m)essage?");
    }
    private static boolean iRebaseFailureCheck(String givenBranch, CommitTree cmT) {
        if (givenBranch.equals(cmT.getCurrBranch())) {
            System.out.println("Cannot rebase a branch onto itself.");
            return true;
        }
        if (!cmT.getBranches().contains(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        return false;
    }
    private static ArrayList<Integer> reversedArray(Commit splitPoint, Commit current) {
        ArrayList<Integer> reversedIdOrder = new ArrayList<Integer>();
        while (splitPoint.id() != current.id()) {
            reversedIdOrder.add(current.id());
            current = current.parent();
        }
        return reversedIdOrder;
    }
    private static String iRebaseHelperOne() {
        System.out.println("Please enter either c, s, or m");
        System.out.println("Would you like to (c)ontinue, " /* Cant skip first commit */
            + "(s)kip this commit, or change this commit's (m)essage?"); 
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    private static String iRebaseMessage() {
        System.out.println("Please enter a new message for this commit.");
        Scanner messageScanner = new Scanner(System.in);
        return messageScanner.nextLine();
    }
    private static void mapRefresh(int counter, Commit given, Commit splitPoint) {
        cT = readIn();
        int holder = cT.getCommitNum();
        while (cT.getCommitNum() <= (counter + holder - 1)) {
            HashMap<String, Commit> newMap = new HashMap<String, Commit>();
            Commit c = readInCommit(cT.getCommitNum());
            for (String s : given.getInheritMap().keySet()) {
                if (!(given.getInheritMap().get(s)).equals(splitPoint.getInheritMap().get(s)) 
                    && (c.getInheritMap().get(s)).equals(splitPoint.getInheritMap().get(s))) {
                    newMap.put(s, given.getInheritMap().get(s));
                }
            }
            HashMap<String, Commit> m1 = new HashMap<String, Commit>(c.getInheritMap());
            HashMap<String, Commit> m3 = new HashMap<String, Commit>();
            m3.putAll(m1);
            m3.putAll(newMap);
            c.changeMap(m3);
            cT.addCommitNum();
            readOutCommit(c);
        }
        readOut(cT);
    }
    public static void iRebase(String givenBranch) {
        if (dangerous()) {
            return;
        }
        cT = readIn();
        if (iRebaseFailureCheck(givenBranch, cT)) {
            return;
        }
        Commit given = cT.getBranchHead(givenBranch);
        Commit current = cT.getBranchHead(cT.getCurrBranch());
        if (upToDateCheck(current, given)) {
            return;
        }
        if (rebaseHelper(current, given, givenBranch)) {
            return;
        }
        Commit splitPoint = findSplitPoint(current, given);
        ArrayList<Integer> reversedIdOrder = reversedArray(splitPoint, current);
        int i = 0;
        Commit temp = null;
        Commit head = null;
        int counter = 0;
        while (true) {
            while (i <= reversedIdOrder.size() - 1) {
                int iD = reversedIdOrder.get(i);
                Commit c = readInCommit(iD);
                replay(c);
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                while (!input.equals("c") && !input.equals("s") && !input.equals("m") 
                    || head == null && input.equals("s") 
                    || i == (reversedIdOrder.size() - 1) && input.equals("s")) {
                    input = iRebaseHelperOne();
                }
                if (input.equals("c")) {
                    Commit copy = new Commit(c);
                    if (head == null) {
                        head = copy;
                    }
                    if (temp != null) {
                        temp.setParent(copy);
                    }
                    temp = copy;
                    counter += 1;
                } /* If s dont do anything*/
                if (input.equals("m")) {
                    String message = iRebaseMessage();
                    Commit copy = new Commit(c);
                    if (head == null) {
                        head = copy;
                    }
                    if (temp != null) {
                        temp.setParent(copy);
                    }
                    copy.changeMsg(message);
                    temp = copy;
                    counter += 1;
                }
                i += 1;
            }
            temp.setParent(given);
            int ii = counter - 1;
            cT.createBranch(cT.getCurrBranch(), head);
            while (ii >= 0) {
                head.changeId(ii + cT.getCommitNum());
                readOutCommit(head);
                head = head.parent();
                ii -= 1;
            }
            readOut(cT);
            mapRefresh(counter, given, splitPoint);
            return;
        } 
    }
    public static void main(String[] args) {
        while (true) {
            String command = args[0];
            String[] tokens = new String[args.length - 1];
            System.arraycopy(args, 1, tokens, 0, args.length - 1);
            switch (command) {
                case "init":
                    initialize();
                    return;
                case "add":
                    String fileName = tokens[0];
                    add(fileName);
                    return;           
                case "commit":
                    try {
                        String msg = tokens[0]; 
                        commit(msg);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Please enter a commit message.");
                    }
                    return;    
                case "rm":
                    String fileN = tokens[0];
                    remove(fileN);
                    return; 
                case "log":
                    log();
                    return;
                case "global-log":
                    globalLog();
                    return;
                case "find":
                    find(tokens[0]);
                    return;
                case "status":
                    status();
                    return;
                case "checkout":
                    if (tokens.length == 2) {
                        int id = Integer.parseInt(tokens[0]);
                        String s = tokens[1];
                        checkout(id, s);
                        return;
                    }
                    if (tokens.length == 1) {
                        String name = tokens[0];
                        checkout(name);                        
                    }
                    return;
                case "branch":
                    String branchN = tokens[0];
                    branch(branchN);
                    return;
                case "rm-branch":
                    removeBranch(tokens[0]);
                    return;
                case "reset":
                    reset(tokens[0]);
                    return;
                case "merge":
                    merge(tokens[0]);
                    return;
                case "rebase":
                    rebase(tokens[0]);
                    return;
                case "i-rebase":
                    iRebase(tokens[0]);
                    return;
                default:
                    System.out.println("Invalid command.");  
                    return;
            }
        }
    }
}
