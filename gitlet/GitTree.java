package gitlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;


/**  The GitTree Class, implementing Serializable, handles
 *   HashMaps of Branches and keeps a history of Commits.
 *   @author Rohan Sharan, Toni Lee
 *   */
public class GitTree implements Serializable {

    /** The current commit. */
    private Commit _current;
    /** The previous commit. */
    private Commit _previous;

    /** The empty GitTree Constructor. */
    GitTree() {
        this(null);
    }

    /** The GitTree Constructor. take is a commit C. */
    GitTree(Commit c) {
        _commits = new ArrayList<>();
        branches = new HashMap<>();
        _currentBranch = "master";
        this.addBranch(_currentBranch);
        if (c == null) {
            _previous = null;
            _current = null;
        } else {
            _previous = _current;
            _current = c;
        }
    }

    /** Add the commit C to this ArrayList of commits. */
    void addCommit(Commit c) {
        if (c == null) {
            _previous = _current;
            _current = null;
        } else {
            _previous = _current;
            _current = c;
            _current.setParent(_previous);
            _commits.add(c);
        }
    }

    /** Return the current branch's String. */
    String currentBranch() {
        return _currentBranch;
    }

    /** Add branch name S to HashMap of branches. */
    void addBranch(String s) {
        branches.put(s, _current);
    }

    /** Get this GitTree's and Return HashMap<STRING, COMMIT> of Branches. */
    HashMap<String, Commit> getBranches() {
        return branches;
    }

    /** Update this GitTree's BRANCHES. */
    void updateBranch() {
        if (branches.containsKey(_currentBranch)) {
            branches.remove(_currentBranch);
            branches.put(_currentBranch, _current);
        }
    }

    /** Update this GitTree's branches to BRANCH. */
    void updateBranch(String branch) {
        if (branches.containsKey(branch)) {
            branches.remove(branch);
        }
        branches.put(branch, _current);
        _currentBranch = branch;
    }

    /** Returns the current commit. */
    Commit current() {
        return _current;
    }

    /** Returns the previous commit. */
    Commit previous() {
        return _previous;
    }

    /** Sets the branches of the GitTree to B. */
    void setNew(HashMap<String, Commit> b) {
        this.branches = b;
    }

    /** Set the current commit to commit C. */
    void setCurrentCommit(Commit c) {
        _current = c;
        _previous = c.getParent();
    }

    /** Set this GitTree's ArrayList of commits to C. */
    void setCommits(ArrayList<Commit> c) {
        _commits.addAll(c);
    }

    /** Get this GitTree's Return ArrayList of Commits. */
    ArrayList<Commit> getCommits() {
        return _commits;
    }

    /** This GitTree's ArrayList of Commits. */
    private ArrayList<Commit> _commits;

    /** This GitTree's HashMap of branches. */
    private HashMap<String, Commit> branches;

    /** This GitTree's current Branch. */
    private String _currentBranch;
}
