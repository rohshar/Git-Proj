package gitlet;

import org.junit.Before;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Rohan Sharan, Toni Lee.
 */
public class UnitTest {

    private static final String GITLET_DIR = ".gitlet/";
    private static final String TESTING_DIR = "testing/";
    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    @Before
    public void setUp() {
        File f = new File(GITLET_DIR);
        if (f.exists()) {
            Utils.restrictedDelete(f);
        }
        f = new File(TESTING_DIR);
        if (f.exists()) {
            Utils.restrictedDelete(f);
        }
        f.mkdirs();
    }

    /** The test for init. */
    @Test
    public void initTest() {
        try {
            gitlet.Main.main("init");
            File newFile = new File(".gitlet/stage");
            assertEquals(newFile.exists(), true);
        } catch (IOException e) {
            System.out.println();
        }
    }



    @Test
    public void testRM() {
        try {
            gitlet.Main.main("init");
        } catch (IOException e) {
            System.out.println();
        }
        try {
            gitlet.Main.main("add wug.txt");
        } catch (IOException e) {
            System.out.println();
        }
        boolean a = false;
        File file = new File(".gitlet/stage/wug.txt");
        if (file.exists()) {
            a = true;
        }
        try {
            gitlet.Main.main("rm wug.txt");
        } catch (IOException e) {
            System.out.println();
        }
        boolean b = false;
        File file2 = new File(".gitlet/stage/wug.txt");
        if (file2.exists()) {
            b = true;
        }
        assertEquals(a, true);
        assertEquals(b, true);
    }

    @Test
    public void testBranch() {
        try {
            gitlet.Main.main("init");
        } catch (IOException e) {
            System.out.println();
        }
        boolean a = false;
        try {
            gitlet.Main.main("branch apple");
            a = true;
        } catch (IOException e) {
            a = false;
        }
        assertEquals(a, true);
    }

    @Test
    public void testRMBranch() {
        try {
            gitlet.Main.main("init");
        } catch (IOException e) {
            System.out.println();
        }
        boolean a = false;
        try {
            gitlet.Main.main("branch apple");
        } catch (IOException e) {
            System.out.println();
        }
        try {
            gitlet.Main.main("rm-branch apple");
            a = true;
        } catch (IOException e) {
            a = false;
        }
        assertEquals(a, true);
    }

    @Test
    public void testCheckout() throws UnsupportedEncodingException {
        try {
            gitlet.Main.main("init");
        } catch (IOException e) {
            System.out.println();
        }
        try {
            gitlet.Main.main("add wug.txt");
        } catch (IOException e) {
            System.out.println();
        }
        File file = new File(".gitlet/stage/wug.txt");
        try {
            gitlet.Main.main("commit wug");
        } catch (IOException e) {
            System.out.println();
        }
        String commit = "f";
        for (String s : Utils.plainFilenamesIn(".gitlet/")) {
            if (Utils.plainFilenamesIn(".gitlet/" + s + "/").size() > 0) {
                commit = s;
            }
        }
        try {
            gitlet.Main.main("checkout" + commit);
        } catch (IOException e) {
            System.out.println();
        }
        File newF = new File("wug.txt");
        int a = newF.compareTo(file);
        assertEquals(73, a);
    }

    /**A test for find. */
    @Test
    public void findTest() {
        try {
            gitlet.Main.main("init");
            gitlet.Main.main("add wug.txt");
            gitlet.Main.main("commit apple");
        } catch (IOException e) {
            System.out.println();
        }
    }

    /** A test for the commit tree. */
    @Test
    public void commitTest() throws IOException {
        try {
            gitlet.Main.main("init");
        } catch (IOException e) {
            System.out.println();
        }
        gitlet.Main.main("commit the message");
    }
}
