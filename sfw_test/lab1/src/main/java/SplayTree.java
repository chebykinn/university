import java.util.Iterator;
import java.util.Stack;

public class SplayTree implements Iterable<SplayTreeNode> {
    public class SplayTreeIterator implements Iterator<SplayTreeNode>{
        private Stack<SplayTreeNode> stack = new Stack<>();
        private SplayTreeNode current;

        private SplayTreeIterator(SplayTreeNode root) {
            current = root;
        }

        public SplayTreeNode next() {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            SplayTreeNode splayTreeNode = current;
            current = current.right;

            return splayTreeNode;
        }

        public boolean hasNext() {
            return (!stack.isEmpty() || current != null);
        }
    }
    public Iterator<SplayTreeNode> iterator() {
        return new SplayTreeIterator(root);
    }

    SplayTreeNode root = null;
    int count;

    public void clear(){
        root = null;
        count = 0;
    }

    public SplayTree(){
        clear();
    }

    public SplayTree(int value){
       insert(value);
    }

    public void insert(int value) {
        SplayTreeNode n = root;
        SplayTreeNode parent = null;
        while( n != null ){
            parent = n;
            if( value < n.value ){
                n = n.left;
            }else{
                n = n.right;
            }
        }
        n = new SplayTreeNode(value);
        n.parent = parent;
        if( parent == null ){
            root = n;
        }else if( value < parent.value ){
            parent.left = n;
        }else{
            parent.right = n;
        }
        splay(n);
        count++;
    }

    private SplayTreeNode findNode(int value) {
        SplayTreeNode z = root;
        while (z != null) {
            if (value < z.value) {
                z = z.left;
            }else if (value > z.value) {
                z = z.right;
            }else {
                return z;
            }
        }
        return null;
    }

    public boolean contains(int value){
        return findNode(value) != null;
    }

    public void remove(SplayTreeNode node){
        if (node == null) return;
        splay(node);
        if( (node.left != null) && (node.right != null) ) {
            SplayTreeNode min = node.left;
            while( min.right != null )
                min = min.right;

            min.right = node.right;
            node.right.parent = min;
            node.left.parent = null;
            root = node.left;
        }
        else if ( node.right != null ) {
            node.right.parent = null;
            root = node.right;
        }
        else if( node.left != null ) {
            node.left.parent = null;
            root = node.left;
        }
        else {
            root = null;
        }
        node.parent = null;
        node.left = null;
        node.right = null;
        node = null;
        count--;
    }

    public void remove(int value){
        SplayTreeNode node = findNode(value);
        remove(node);
    }

    public void makeLeftChildParent(SplayTreeNode c, SplayTreeNode p) {
        if ((c == null) || (p == null) || (p.left != c) || (c.parent != p))
            throw new RuntimeException("Incorrect node");

        if (p.parent != null) {
            if (p == p.parent.left)
                p.parent.left = c;
            else
                p.parent.right = c;
        }
        if (c.right != null)
            c.right.parent = p;

        c.parent = p.parent;
        p.parent = c;
        p.left = c.right;
        c.right = p;
    }

    public void makeRightChildParent(SplayTreeNode c, SplayTreeNode p) {
        if ((c == null) || (p == null) || (p.right != c) || (c.parent != p))
            throw new RuntimeException("Incorrect node");
        if (p.parent != null) {
            if (p == p.parent.left)
                p.parent.left = c;
            else
                p.parent.right = c;
        }
        if (c.left != null)
            c.left.parent = p;
        c.parent = p.parent;
        p.parent = c;
        p.right = c.left;
        c.left = p;
    }

    private void splay(SplayTreeNode x) {
        while (x.parent != null) {
            SplayTreeNode parent = x.parent;
            SplayTreeNode grand = parent.parent;
            if (grand == null) {
                if (x == parent.left)
                    makeLeftChildParent(x, parent);
                else
                    makeRightChildParent(x, parent);
            }
            else {
                if (x == parent.left) {
                    if (parent == grand.left) {
                        makeLeftChildParent(parent, grand);
                        makeLeftChildParent(x, parent);
                    }
                    else {
                        makeLeftChildParent(x, x.parent);
                        makeRightChildParent(x, x.parent);
                    }
                }
                else {
                    if (parent == grand.left) {
                        makeRightChildParent(x, x.parent);
                        makeLeftChildParent(x, x.parent);
                    }
                    else {
                        makeRightChildParent(parent, grand);
                        makeRightChildParent(x, parent);
                    }
                }
            }
        }
        root = x;
    }

    int count(){
        return count;
    }

}
