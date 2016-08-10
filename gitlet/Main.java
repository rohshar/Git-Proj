package gitlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/** Controller class for Git.
 *  @author Rohan Sharan, Toni Lee
 */
public class Main {

    /** The GitTree we're working with. */
    private static GitTree _tree;

    /** Gets the Return GitTree. */
    static GitTree tree() {
        return _tree;
    }

    /** Sets the GitTree to NEWTREE. */
    static void setTree(GitTree newTree) {
        _tree = newTree;
    }

    /** Sets up the main function. */
    static void setup() {
        File inFile = new File("file.ser");
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            setTree((GitTree) inp.readObject());
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            System.out.println();
        }
    }

    /** Checks that Gitlet has been initialized. Return TRUE if not. */
    static boolean check() {
        File ff = new File(".gitlet");
        if (!ff.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return true;
        } else {
            return false;
        }
    }

    /** Writes over the ser file. */
    static void rewrite() {
        setFile(new File("file.ser"));
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(getSer()));
            out.writeObject(tree());
            out.close();
        } catch (IOException excp) {
            System.out.println("Couldn't save GitTree.");
        }
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String command = args[0];
        setup();
        switch (command) {
        case "init":
            if (!(args.length == 1)) {
                System.out.println("Incorrect operands.");
                break;
            }
            init();
            setFile(new File("file.ser"));
            rewrite();
            break;
        case "add":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            add(args[1]);
            rewrite();
            break;
        case "commit":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            commit(args);
            rewrite();
            break;
        case "merge":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                return;
            }
            if (check()) {
                break;
            }
            merge(args[1]);
            rewrite();
            break;
        default:
            commands2(args);
            break;
        }
    }

    /** Second set of commands. Takes in input String ARGS. */
    static void commands2(String... args) throws IOException {
        String command = args[0];
        switch (command) {
        case "rm":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            remove(args[1]);
            rewrite();
            break;
        case "log":
            if (!(args.length == 1)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            log(tree());
            break;
        case "global-log":
            if (!(args.length == 1)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            gLog(tree());
            break;
        case "find":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            find(args[1]);
            break;
        default:
            commands3(args);
            break;
        }
    }

    /** Third set of commands. Takes in input ARGS. */
    static void commands3(String...args) throws IOException {
        String command = args[0];
        switch (command) {
        case "status":
            if (!(args.length == 1)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            status();
            break;
        case "checkout":
            if (check()) {
                break;
            }
            checkout(args);
            rewrite();
            break;
        case "branch":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                break;
            }
            if (check()) {
                break;
            }
            branch(args[1]);
            rewrite();
            break;
        case "rm-branch":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                return;
            }
            if (check()) {
                break;
            }
            rmBranch(args[1]);
            rewrite();
            break;
        case "reset":
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                return;
            }
            if (check()) {
                break;
            }
            reset(args[1]);
            rewrite();
            break;
        default:
            System.out.println("No command with that name exists.");
            break;
        }
    }

    /** The checkout case #3. Takes in File FILE to check. */
    static void checkout3(String file) throws UnsupportedEncodingException {
        List<String> files = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        List<String> c1 = Utils.plainFilenamesIn(".gitlet/stage");
        List<String> c2 = Utils.plainFilenamesIn(".gitlet/"
                + tree().getBranches().get(file).getSHA());
        List<String> c3 = Utils.plainFilenamesIn(".gitlet/"
                + tree().current().getSHA());
        for (String zs : files) {
            char ch = '.';
            if (c2 != null) {
                if (!c1.contains(zs) && !c2.contains(zs) && !zs.contains(".ser")
                        && !zs.contains("Make") && !(zs.charAt(0) == ch)
                        && !(c3.contains(zs))) {
                    System.out.println("There is an untracked file "
                            + "in the way; delete it or add it first.");
                    return;
                }
                if (c2.contains(zs)) {
                    File given = new File(".gitlet/" + tree().getBranches()
                            .get(file).getSHA() + "/" + zs);
                    byte[] given1 = Utils.readContents(given);
                    String given2 = new String(given1, "UTF-8");
                    File compare = new File(zs);
                    byte[] compare1 = Utils.readContents(compare);
                    String compare2 = new String(compare1, "UTF-8");
                    String parent2;
                    try {
                        File parent = new File(".gitlet/"
                                + tree().current().getSHA() + "/" + zs);
                        byte[] parent1 = Utils.readContents(parent);
                        parent2 = new String(parent1, "UTF-8");
                    } catch (IllegalArgumentException e) {
                        parent2 = new String("");
                    }
                    if (!given2.equals(compare2)
                            && !parent2.equals(compare2)) {
                        System.out.println("There is an untracked file in the "
                                + "way; delete it or add it first.");
                        return;
                    }
                }
            }
        }
    }

    /** The checkout function taking in ARGS. */
    static void checkout(String... args) throws IOException {
        String f = args[1];
        if (tree().getBranches().containsKey(args[1])) {
            if (!(args.length == 2)) {
                System.out.println("Incorrect operands.");
                return;
            }
            if (f.equals(tree().currentBranch())) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
            List<String> files = Utils.plainFilenamesIn
                    (System.getProperty("user.dir"));
            checkout3(f);
            for (String fs : files) {
                File ffs = new File(fs);
                char ch = '.';
                if (!(fs.contains(".ser")) && !(fs.contains("Make"))
                        && !(fs.charAt(0) == ch)) {
                    ffs.delete();
                }
            }
            if (tree().getBranches().get(f).getFiles() != null) {
                for (String s : tree().getBranches().get(f).getFiles()) {
                    File read = new File(".gitlet/"
                            + tree().getBranches().get(f).getSHA() + "/" + s);
                    byte[] contents = Utils.readContents(read);
                    File toAdd = new File(s);
                    toAdd.createNewFile();
                    Utils.writeContents(toAdd, contents);
                }
            }
            tree().getBranches().get(f).clearToRemove();
            tree().setCurrentCommit(tree().getBranches().get(f));
            tree().updateBranch(f);
        } else if (args.length == 2) {
            System.out.println("No such branch exists.");
            return;
        } else if (tree().current().getFiles().contains(args[2])) {
            if (!(args.length == 3) || !(args[1].equals("--"))) {
                System.out.println("Incorrect operands.");
                return;
            }
            f = args[2];
            checkout4(f);
            File readFrom = new File(".gitlet/"
                    + tree().current().getSHA() + "/" + f);
            if (!readFrom.exists()) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            byte[] fileByte = Utils.readContents(readFrom);
            File add = new File(f);
            add.createNewFile();
            Utils.writeContents(add, fileByte);
        } else if (args.length > 3) {
            checkout2(args);
        }
    }

    /** Checkout case #4. Takes in String ARG. */
    static void checkout4(String arg) {
        File delete = new File(arg);
        if (delete.exists()) {
            delete.delete();
        }
    }

    /** Checkout case #2. Takes in a set of Strings ARGS. */
    static void checkout2(String... args) throws IOException {
        if (!(args.length == 4) || !(args[2].equals("--"))) {
            System.out.println("Incorrect operands.");
            return;
        }
        boolean hasCommit = false;
        String file = args[3];
        String commit = args[1];
        for (Commit coms : tree().getCommits()) {
            if (coms.getSHA().contains(commit)) {
                hasCommit = true;
                File readFrom = new File(".gitlet/"
                        + coms.getSHA() + "/" + file);
                if (!readFrom.exists()) {
                    System.out.println("File does not "
                            + "exist in that commit.");
                    return;
                }
                byte[] fileByte = Utils.readContents(readFrom);
                File working = new File(file);
                if (working.exists()) {
                    working.delete();
                }
                File add = new File(file);
                add.createNewFile();
                Utils.writeContents(add, fileByte);
            }
        }
        if (!hasCommit) {
            System.out.println("No commit with that id exists");
        }
    }

    /** Returns 1 if Modified in only current branch, 2 if modified in
     *  only Given, 3 if Both, 4 if Neither Takes in String FILE, String BRANCH
     *  and Commit SPLIT.
     */
    static int modifiedIn(String file, String branch, Commit split)
            throws UnsupportedEncodingException {
        if (!split.getFiles().contains(file)) {
            return 3;
        }
        File current = new File(".gitlet/"
                + tree().current().getSHA() + "/" + file);
        File given = new File(".gitlet/"
                + tree().getBranches().get(branch).getSHA()
                + "/" + file);
        File original = new File(".gitlet/" + split.getSHA() + "/" + file);
        byte[] contents = Utils.readContents(original);
        byte[] cc = Utils.readContents(current);
        byte[] gc = Utils.readContents(given);
        String contents1 = new String(contents, "UTF-8");
        String cc1 = new String(cc, "UTF-8");
        String gc1 = new String(gc, "UTF-8");
        if (!cc1.equals(gc1)) {
            return 5;
        } else if (!contents1.equals(cc1) && contents1.equals(gc1)) {
            return 1;
        } else if (contents1.equals(cc1) && !contents1.equals(gc1)) {
            return 2;
        } else if (cc1.equals(gc1)) {
            return 3;
        } else if (contents1.equals(cc1) && contents1.equals(gc1)) {
            return 4;
        } else {
            return 3;
        }
    }

    /** Returns BRANCH, the split branch. */
    static Commit split(String branch) {
        Commit a = tree().current();
        Commit c = null;
        while (a != null && c == null) {
            a = a.getParent();
            Commit b = tree().getBranches().get(branch).getParent();
            if (a.getTD() == b.getTD()) {
                c = a;
                a = null;
            } else {
                b = b.getParent();
            }
        }
        return c;
    }

    /** Check for untracked files. */
    static void untracked() throws UnsupportedEncodingException {
        List<String> currentFi = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        for (String cu : currentFi) {
            char ch = '.';
            if (!cu.contains(".ser") && !cu.contains("Make")
                    && cu.charAt(0) != ch) {
                File changed = new File(cu);
                File committed = new File(".gitlet/" + tree().current().getSHA()
                        + "/" + cu);
                byte[] cont1 = Utils.readContents(changed);
                byte[] cont2 = Utils.readContents(committed);
                String comp1 = new String(cont1, "UTF-8");
                String comp2 = new String(cont2, "UTF-8");
                if (!comp1.equals(comp2)) {
                    System.out.println("There is an untracked file in the "
                            + "way; delete it or add it first.");
                    System.exit(0);
                }
            }
        }
    }

    /** A second merge case. Takes in Lists BRANF and CURRF and
     *  Commit SPLIT. Returns TRUE if conflicted. */
    static boolean merge2(List<String> branf, List<String> currf, Commit split)
            throws UnsupportedEncodingException {
        boolean conflicted = false;
        for (String currF : currf) {
            char ch = '.';
            if (!branf.contains(currF) && split.getFiles()
                    .contains(currF) && !currF.contains(".ser")
                    && !currF.contains("Make") && currF.charAt(0) != ch) {
                File cur = new File(currF);
                File or = new File(".gitlet/" + split.getSHA()
                        + "/" + currF);
                byte[] con1 = Utils.readContents(cur);
                byte[] con2 = Utils.readContents(or);
                String origCon = new String(con2, "UTF-8");
                String newCon = new String(con1, "UTF-8");
                if (origCon.equals(newCon)) {
                    File toDel = new File(currF);
                    toDel.delete();
                    File delStage = new File(".gitlet/stage/" + currF);
                    delStage.delete();
                } else {
                    conflicted = true;
                    StringBuilder sb = new StringBuilder();
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(cur)));
                        String line;
                        sb.append("<<<<<<< HEAD");
                        sb.append("\n");
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }
                        sb.append("=======");
                        sb.append("\n");
                        sb.append(">>>>>>>");
                        sb.append("\n");
                    } catch (IOException e) {
                        System.out.println("File does not exist.");
                    }
                    File newFile = new File(currF);
                    byte [] bytes = sb.toString().getBytes();
                    try {
                        newFile.createNewFile();
                        Utils.writeContents(newFile, bytes);
                    } catch (IOException e) {
                        System.out.println("File does not exist.");
                    }

                }
            }
        }
        return conflicted;
    }

    /** The Merge pre-case. Returns True if we should exit.
     *  Takes in String BRANCH. */
    static boolean preMerge(String branch) {
        if (!tree().getBranches().containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (Utils.plainFilenamesIn(".gitlet/stage").size() > 0
                || tree().current().excludeFromParent().size() > 0) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        Commit cc = tree().current();
        while (cc != null) {
            if (cc == tree().getBranches().get(branch)) {
                System.out.println("Given branch is an"
                        + " ancestor of the current branch.");
                return true;
            }
            cc = cc.getParent();
        }
        Commit bc = tree().getBranches().get(branch);
        while (bc != null) {
            if (bc == tree().current()) {
                System.out.println("Current branch fast-forwarded.");
                return true;
            }
            bc = bc.getParent();
        }
        Commit split = split(branch);
        if (branch == tree().currentBranch()) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    /** The Merge function taking in BRANCH, the branch to merge to. */
    static void merge(String branch) throws UnsupportedEncodingException {
        boolean conflicted = false;
        untracked();
        if (preMerge(branch)) {
            return;
        }
        Commit split = split(branch);
        List<String> branchedF = tree().getBranches()
                .get(branch).getFiles();
        List<String> currentF = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        if (merge2(branchedF, currentF, split)) {
            conflicted = true;
        }
        for (String givenF : branchedF) {
            if (currentF.contains(givenF)) {
                int index = modifiedIn(givenF, branch, split);
                if (index == 2) {
                    merge4(givenF, branch);
                } else if (index == 3 || index == 5) {
                    if (index == 3) {
                        conflictCase(givenF, branch);
                    } else if (index == 5) {
                        conflicted = true;
                        conflictCase(givenF, branch);
                    }
                }
            } else if (split.getFiles().contains(givenF)) {
                File og = new File(".gitlet/" + split.getSHA() + "/" + givenF);
                File newF = new File(".gitlet/" + tree().getBranches()
                        .get(branch).getSHA() + "/" + givenF);
                byte[] ori = Utils.readContents(og);
                byte[] neww = Utils.readContents(newF);
                String orig1 = new String(ori, "UTF-8");
                String new1 = new String(neww, "UTF-8");
                if (!orig1.equals(new1)) {
                    conflicted = true;
                    merge3(givenF, branch, newF);
                }
            } else {
                merge5(givenF, branch);
            }
        }
        if (!conflicted) {
            try {
                String[] args = new String[]{"commit", "Merged " + tree()
                        .currentBranch() + " with " + branch + "."};
                commit(args);
                return;
            } catch (IOException e) {
                System.out.println();
            }
        } else {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merge case #5. Takes in String GIVENF, and String BRANCH. */
    static void merge5(String givenF, String branch) {
        File toAdd = new File(givenF);
        File toStage = new File(".gitlet/stage/" + givenF);
        try {
            toAdd.createNewFile();
            File f = new File(".gitlet/" + tree().getBranches()
                    .get(branch).getSHA() + "/" + givenF);
            byte[] fileByte = Utils.readContents(f);
            Utils.writeContents(toAdd, fileByte);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
        try {
            toStage.createNewFile();
            File f = new File(".gitlet/" + tree().getBranches()
                    .get(branch).getSHA() + "/" + givenF);
            byte[] fileByte = Utils.readContents(f);
            Utils.writeContents(toStage, fileByte);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
    }
    /** Merge case #4. Takes in String GIVENF, and String BRANCH. */
    static void merge4(String givenF, String branch) {
        File del = new File(givenF);
        del.delete();
        File del2 = new File(".gitlet/stage/" + givenF);
        del2.delete();
        File toAdd = new File(givenF);
        File toStage = new File(".gitlet/stage/" + givenF);
        try {
            toAdd.createNewFile();
            File f = new File(".gitlet/" + tree().getBranches()
                    .get(branch).getSHA() + "/" + givenF);
            byte[] fileByte = Utils.readContents(f);
            Utils.writeContents(toAdd, fileByte);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
        try {
            toStage.createNewFile();
            File f = new File(".gitlet/" + tree().getBranches()
                    .get(branch).getSHA() + "/" + givenF);
            byte[] fileByte = Utils.readContents(f);
            Utils.writeContents(toStage, fileByte);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
    }

    /** Merge case #3. Takes in String filename GIVENF, branch
     *  BRANCH, and File NEWF. */
    static void merge3(String givenF, String branch, File newF) {
        File toAdd = new File(givenF);
        System.out.println("Encountered a merge conflict.");
        StringBuilder sb = new StringBuilder();
        sb.append("<<<<<<< HEAD");
        sb.append("\n");
        sb.append("=======");
        sb.append("\n");
        String line;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(newF)));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            sb.append(">>>>>>>");
            sb.append("\n");
        } catch (IOException e) {
            System.out.println();
        }
        File newFile = new File(givenF);
        byte [] bytes = sb.toString().getBytes();
        try {
            newFile.createNewFile();
            Utils.writeContents(newFile, bytes);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
        try {
            toAdd.createNewFile();
            File f = new File(".gitlet/" + tree().getBranches()
                    .get(branch).getSHA() + "/" + givenF);
            byte[] fileByte = Utils.readContents(f);
            Utils.writeContents(toAdd, fileByte);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
    }

    /** Handles the Conflict case of merge, taking in the conflict
     *  file FILE and the branch BRANCH.
     * @param file
     * @param branch
     * @throws UnsupportedEncodingException
     */
    static void conflictCase(String file, String branch)
            throws UnsupportedEncodingException {
        String sha = tree().getBranches().get(branch).getSHA();
        File branchF = new File(".gitlet/" + sha + "/" + file);
        File currentF = new File(file);
        byte[] c1 = Utils.readContents(branchF);
        byte[] c2 = Utils.readContents(currentF);
        String c3 = new String(c1, "UTF-8");
        String c4 = new String(c2, "UTF-8");
        if (c3.equals(c4)) {
            File toStage = new File(".gitlet/stage/" + file);
            if (!toStage.exists()) {
                try {
                    toStage.createNewFile();
                    Utils.writeContents(toStage, c1);
                } catch (IOException e) {
                    System.out.println("File does not exist.");
                }
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader r;
            FileInputStream re = new FileInputStream(currentF);
            FileInputStream re2 = new FileInputStream(branchF);
            r = new BufferedReader(new InputStreamReader(re));
            BufferedReader r2;
            r2 = new BufferedReader(new InputStreamReader(re2));
            String line;
            String line2;
            sb.append("<<<<<<< HEAD");
            sb.append("\n");
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            sb.append("=======");
            sb.append("\n");
            while ((line2 = r2.readLine()) != null) {
                sb.append(line2);
                sb.append("\n");
            }
            sb.append(">>>>>>>");
            sb.append("\n");
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
        File newFile = new File(file);
        byte [] bytes = sb.toString().getBytes();
        try {
            newFile.createNewFile();
            Utils.writeContents(newFile, bytes);
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }
    }

    /** The Branch function taking in branch name S. */
    static void branch(String s) {
        if (tree().getBranches().keySet().contains(s)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        tree().addBranch(s);
    }

    /** Shows LOG of gittree G. */
    static void log(GitTree G) {
        String b = G.currentBranch();
        Commit c = G.getBranches().get(b);
        while (c != null) {
            System.out.println("===");
            System.out.print("Commit ");
            System.out.println(c.getSHA());
            System.out.println(c.getTD());
            System.out.println(c.getMessage());
            System.out.println();
            c = c.getParent();
        }
    }

    /** The GLOG function taking in GitTree G. Shows a global log. */
    static void gLog(GitTree G) {
        ArrayList<Commit> commits = G.getCommits();
        for (Commit c : commits) {
            System.out.println("===");
            System.out.print("Commit ");
            System.out.println(c.getSHA());
            System.out.println(c.getTD());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    /** The rmBranch function taking in String BRANCH, removes a branch. */
    static void rmBranch(String branch) {
        if (tree().currentBranch().equalsIgnoreCase(branch)) {
            System.out.println("Cannot remove the current branch.");

        } else {
            HashMap<String, Commit> branches = tree().getBranches();
            if (branches.containsKey(branch)) {
                branches.remove(branch);
                tree().setNew(branches);
            } else {
                System.out.println("A branch with that name does not exist.");
            }
        }
    }

    /** Resets the version control system to commit C. */
    static void reset(String c) throws IOException {
        for (Commit com : tree().getCommits()) {
            if (com.getSHA().contains(c)) {
                Commit commit = com;
                File dir = new File(".gitlet/stage");
                for (String s : Utils.plainFilenamesIn
                        (System.getProperty("user.dir"))) {
                    char ch = '.';
                    if (!(s.contains(".ser")) && !(s.contains("Make"))
                            && !(s.charAt(0) == ch)) {
                        if (!(Utils.plainFilenamesIn(dir).contains(s))
                                && !(Utils.plainFilenamesIn(".gitlet/" + tree().
                                        current().getSHA()).contains(s))) {
                            System.out.println("There is an untracked "
                                    + "file in the way;"
                                    + " delete it or add it first.");
                            return;
                        } else {
                            File f = new File(s);
                            f.delete();
                        }
                    }
                }
                File stage = new File(".gitlet/stage");
                List<String> staged = Utils.plainFilenamesIn(stage);
                for (String s : staged) {
                    File f = new File(".gitlet/stage/" + s);
                    f.delete();
                }
                for (String file : commit.getFiles()) {
                    String sh = commit.getSHA();
                    File readFrom = new File(".gitlet/" + sh + "/" + file);
                    byte[] fileByte = Utils.readContents(readFrom);
                    File add = new File(file);
                    add.createNewFile();
                    Utils.writeContents(add, fileByte);
                }
                commit.clearToRemove();
                tree().setCurrentCommit(commit);
                tree().updateBranch();
                return;
            }
        }
        System.out.println("No commit with that id exists.");
    }

    /** Displays the current status. */
    static void status() {
        System.out.println("===" + " " + "Branches" + " " + "===");
        System.out.println("*" + tree().currentBranch());
        for (String s : tree().getBranches().keySet()) {
            if (s != tree().currentBranch()) {
                System.out.println(s);
            }
        }
        System.out.println();
        System.out.println("===" + " " + "Staged Files" + " " + "===");
        for (String file : Utils.plainFilenamesIn(".gitlet/stage")) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("===" + " " + "Removed Files" + " " + "===");
        for (String s : tree().current().excludeFromParent()) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Finds the commit with message MSG. */
    static void find(String msg) {
        boolean i = false;
        for (Commit c : tree().getCommits()) {
            if (c.getMessage().equalsIgnoreCase(msg)) {
                System.out.println(c.getSHA());
                i = true;
            }
        }
        if (!i) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Initializes the Version control system GitTree. */
    static void init() {
        File newFile = new File(".gitlet");
        if (!newFile.exists()) {
            setTree(new GitTree());
            newFile.mkdir();
            File stage = new File(".gitlet/stage");
            stage.mkdir();
            byte[] empty = new byte[0];
            Commit c = new Commit();
            tree().addCommit(c);
            String id = Utils.sha1(c.getMessage(), empty);
            File initial = new File(".gitlet/" + id);
            initial.mkdir();
        } else {
            System.out.println("A gitlet version-control system"
                    + " already exists in the current directory.");
        }
    }

    /** Tracks the file FILENAME for adding. */
    static void add(String fileName) throws IOException {
        String current2;
        String original2;
        try {
            String sha = tree().current().getSHA();
            File current = new File(".gitlet/" + sha + "/" + fileName);
            File original = new File(fileName);
            byte[] current1 = Utils.readContents(current);
            byte[] original1 = Utils.readContents(original);
            current2 = new String(current1, "UTF-8");
            original2 = new String(original1, "UTF-8");
        } catch (IOException | IllegalArgumentException e) {
            current2 = "";
            original2 = "nah";
        }

        if (tree().current().excludeFromParent().contains(fileName)) {
            tree().current().clearToRemove();
            return;
        } else if (original2.equals(current2)) {
            return;
        }
        try {
            File f = new File(fileName);
            byte[] fileByte = Utils.readContents(f);
            File toAdd = new File(".gitlet/stage/" + fileName);
            toAdd.createNewFile();
            Utils.writeContents(toAdd, fileByte);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("File does not exist.");
        }
    }

    /** Marks file FILENAME for removal. */
    static void remove(String fileName) throws IOException {
        List<String> stage = Utils.plainFilenamesIn(".gitlet/stage");
        if ((tree().current().getFiles() == null)
                && !(stage.contains(fileName))) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if ((tree().current().getFiles() != null) && (tree().current()
                .getFiles().
                contains(fileName))) {
            if ((Utils.plainFilenamesIn(System.getProperty("user.dir")).
                    contains(fileName))) {
                File f = new File(fileName);
                f.delete();
            }
            tree().current().clearToRemove();
            tree().current().toRemove(fileName);
            if (stage.contains(fileName)) {
                File toDelete = new File(".gitlet/stage/" + fileName);
                toDelete.delete();
            }
        } else if (stage.contains(fileName)) {
            File toDelete = new File(".gitlet/stage/" + fileName);
            toDelete.delete();
            if (tree().getCommits().size() > 1) {
                if (tree().current().getFiles().contains(fileName)) {
                    tree().current().clearToRemove();
                    tree().current().toRemove(fileName);
                }
            }
        } else if ((Utils.plainFilenamesIn(System.getProperty("user.dir")).
                contains(fileName))) {
            tree().current().clearToRemove();
            tree().current().toRemove(fileName);
        } else {
            System.out.println("No reason to remove file.");
        }
    }

    /** Commits with ARGS as the commit message. */
    static void commit(String... args) throws IOException {
        if ((args.length == 1) || (args[1].equals(""))) {
            System.out.println("Please enter a commit message.");
            return;
        }
        String message = args[1];
        List<String> list;
        List<String> list2 = new ArrayList<>();
        list = Utils.plainFilenamesIn(".gitlet/stage");
        int size = tree().current().excludeFromParent().size();
        if ((list.size() == 0) && (size == 0)) {
            System.out.println("No changes added to the commit.");
        }
        List<String> files = new ArrayList<>();
        for (String s : list) {
            files.add(s);
        }
        if (!tree().current().getMessage().equals("initial commit")) {
            if (tree().current().getFiles() != null) {
                String sha = tree().current().getSHA();
                list2 = Utils.plainFilenamesIn(".gitlet/" + sha);
                for (String s2 : list2) {
                    if (!list.contains(s2)) {
                        if (!tree().current().excludeFromParent()
                                .contains(s2)) {
                            files.add(s2);
                        }
                    }
                }
            }
        }
        Commit c = new Commit(message, null, files);
        tree().addCommit(c);
        tree().updateBranch();
        File commitFile = new File(".gitlet/" + c.getSHA());
        commitFile.mkdir();
        for (String f : files) {
            File toAdd = new File(".gitlet/" + c.getSHA() + "/" + f);
            toAdd.createNewFile();
            File file;
            if (list.contains(f)) {
                file = new File(".gitlet/stage/" + f);
            } else {
                file = new File(".gitlet/" + c.getParent().getSHA()
                        + "/" + f);
            }
            byte[] fileByte = Utils.readContents(file);
            Utils.writeContents(toAdd, fileByte);
        }
        File stage = new File(".gitlet/stage");
        List<String> staged = Utils.plainFilenamesIn(stage);
        for (String s : staged) {
            File f = new File(".gitlet/stage/" + s);
            f.delete();
        }
    }

    /** The SER OUTFILE. */
    private static File _outFile;

    /** Gets the OUTFILE. Returns its FILE. */
    static File getSer() {
        return _outFile;
    }

    /** Sets the Outfile to NEWFILE. */
    static void setFile(File newFile) {
        _outFile = newFile;
    }
}
