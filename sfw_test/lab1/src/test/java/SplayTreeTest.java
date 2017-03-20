import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ivan on 11.03.17.
 */
public class SplayTreeTest {
    SplayTree tree;
    @Before
    public void setUp() throws Exception {
        tree = new SplayTree();

    }

    @After
    public void tearDown() throws Exception {
        tree.clear();
    }

    @Test
    public void clear() throws Exception {
        tree.insert(10);
        assertTrue(tree.count() > 0);
        tree.clear();
        assertEquals(tree.count(), 0);
        tree.insert(10);
        assertEquals(tree.count(), 1);

    }

    @Test
    public void insertRoot() throws Exception{
        assertEquals(tree.root, null);
        tree.insert(5);
        assertNotEquals(tree.root, null);
        assertEquals(tree.root.value, 5);
        tree.insert(10);
        assertEquals(tree.root.value, 10);
        assertEquals(tree.root.left.value, 5);
        tree.clear();
        assertEquals(tree.root, null);
        tree.insert(5);
        assertNotEquals(tree.root, null);
        assertEquals(tree.root.value, 5);
        tree.insert(1);
        assertEquals(tree.root.value, 1);
        assertEquals(tree.root.right.value, 5);

    }

    @Test
    public void insert() throws Exception {
        tree.insert(128);
        assertEquals(tree.root.value, 128);
        tree.insert(256);
        assertEquals(tree.root.value, 256);
        assertEquals(tree.root.left.value, 128);
        tree.insert(64);
        assertEquals(tree.root.value, 64);
        assertEquals(tree.root.right.value, 128);
        assertEquals(tree.root.right.right.value, 256);
        tree.insert(32);
        assertEquals(tree.root.value, 32);
        assertEquals(tree.root.right.value, 64);
        assertEquals(tree.root.right.right.value, 128);
        assertEquals(tree.root.right.right.right.value, 256);

        tree.insert(16);
        assertEquals(tree.root.value, 16);
        assertEquals(tree.root.right.value, 32);
        assertEquals(tree.root.right.right.value, 64);
        assertEquals(tree.root.right.right.right.value, 128);
        assertEquals(tree.root.right.right.right.right.value, 256);

        tree.insert(65);
        assertEquals(tree.root.value, 65);
        assertEquals(tree.root.left.value, 32);
        assertEquals(tree.root.left.left.value, 16);
        assertEquals(tree.root.left.right.value, 64);
        assertEquals(tree.root.right.value, 128);
        assertEquals(tree.root.right.right.value, 256);

    }

    @Test
    public void remove() throws Exception {
        tree.insert(128);
        assertEquals(tree.count(), 1);
        tree.remove(128);
        assertEquals(tree.count(), 0);

        tree.insert(128);
        tree.insert(256);
        tree.insert(64);
        tree.insert(32);
        assertEquals(tree.root.value, 32);
        assertEquals(tree.root.right.value, 64);
        assertEquals(tree.root.right.right.value, 128);
        assertEquals(tree.root.right.right.right.value, 256);
        tree.remove(256);
        assertEquals(tree.root.value, 32);
        assertEquals(tree.root.right.value, 128);
        assertEquals(tree.root.right.left.value, 64);
        tree.remove(64);
        assertEquals(tree.root.value, 32);
        assertEquals(tree.root.right.value, 128);

    }

    @Test
    public void dfs() throws Exception {
        tree.insert(128);
        tree.insert(1);
        tree.insert(256);
        tree.insert(64);
        tree.insert(32);
        int prev = 0;
        for (SplayTreeNode node : tree) {
            assertTrue(node.value > prev);
            prev = node.value;
        }
    }

    @Test
    public void contains() throws Exception {
        tree.insert(64);
        tree.insert(128);
        assertTrue(tree.contains(64));
        assertTrue(tree.contains(128));
        assertFalse(tree.contains(32));
    }

}