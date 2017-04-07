/**
 * Created by ivan on 11.03.17.
 */
public class SplayTreeNode {
    SplayTreeNode left, right, parent;
    int value;

    public SplayTreeNode(int value, SplayTreeNode left, SplayTreeNode right, SplayTreeNode parent){
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public SplayTreeNode(int value){
        this(value, null, null, null);
    }
}
