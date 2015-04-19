package swing.collectionTreeModel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class ImmutableTreeNode implements TreeNode {

    private final Object userObject;
    private final boolean childrenAllowed;

    private TreeNode parent;
    private Optional<Vector<ImmutableTreeNode>> children;

    public ImmutableTreeNode(Object userObject) {
        this(userObject, true);
    }

    public ImmutableTreeNode(Object userObject, boolean childrenAllowed) {
        this.userObject = checkNotNull(userObject);
        this.childrenAllowed = childrenAllowed;
        children = Optional.empty();
    }

    protected void addChild(ImmutableTreeNode child) {
        checkState(childrenAllowed, "No children are allowed for this node.");

        // Lazy creation of child list
        if (!children.isPresent()) {
            children = Optional.of(new Vector<>());
        }
        children.get().add(child);
        child.setParent(this);
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public Object getUserObject() {
        return userObject;
    }

    @Override
    public String toString() {
        return userObject.toString();
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get().elementAt(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.get().size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        checkNotNull(node);
        return children.get().indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return childrenAllowed;
    }

    @Override
    public boolean isLeaf() {
        return !children.isPresent() || children.get().isEmpty();
    }

    @Override
    public Enumeration<ImmutableTreeNode> children() {
        return children.isPresent() ? Collections.enumeration(children.get())
                : Collections.emptyEnumeration();
    }
}
