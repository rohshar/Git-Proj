package gitlet;

import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.io.Serializable;




/**  @author Rohan Sharan, Toni Lee */
public class Commit implements Serializable {

    /** The Commit Constructor. Takes in string MESSAGE,
     *  the commit PARENT, and list of files FILES.
     *  */
    Commit(String message, Commit parent, List<String> files) {
        _message = message;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        _datetime = df.format(calobj.getTime());
        _parent = parent;
        _files = files;
    }

    /** The empty Commit Constructor. */
    Commit() {
        this("initial commit", null, null);
    }

    /** Set this commits parent to commit P. */
    void setParent(Commit p) {
        _parent = p;
    }

    /** Get the current time and date. Return STRING */
    String getTD() {
        return _datetime;
    }

    /** Get the current message. Return STRING*/
    String getMessage() {
        return _message;
    }

    /** Get this commit's parent Return COMMIT. */
    Commit getParent() {
        return _parent;

    }

    /** Get this Commit's SHA. Return the STRING. */
    String getSHA() {
        if (getMessage().equals("initial commit")) {
            _sha = Utils.sha1(getMessage(), getTD());
        } else {
            String sha = getParent().getSHA();
            int code = getFiles().hashCode();
            _sha = Utils.sha1(sha + getMessage() + code + getTD());
        }
        return _sha;
    }

    /** Get this commit's list of files. Return List<String>. */
    List<String> getFiles() {
        return _files;
    }

    /** Set this commit's list of files to List FILES. */
    void setFiles(List<String> files) {
        _files = files;
    }

    /** Remove FILE from files. */
    void removeFile(String file) {
        _files.remove(file);
    }

    /** Mark FILE for removal from this commit's child. */
    void toRemove(String file) {
        toRemoveFromChild.add(file);
    }

    /** Return the list of files to exclude from this commit's child. */
    List<String> excludeFromParent() {
        return toRemoveFromChild;
    }

    /** Clear this commit's toRemovefromchild Arraylist. */
    void clearToRemove() {
        toRemoveFromChild = new ArrayList<>();
    }

    /** The List of files to remove from child. */
    private List<String> toRemoveFromChild = new ArrayList<>();
    /** The tree that I belong to. */
    private GitTree _tree;
    /** My SHA ID. */
    private String _sha;
    /** My message. */
    private String _message;
    /** My date and time. */
    private String _datetime;
    /** My parent. */
    private Commit _parent;
    /** My files. */
    private List<String> _files;
}
