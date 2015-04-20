package uk.polletto.swing.collectionTreeModel;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;

public class TreeUtils {

    public static String toString(TreeNode treeNode) {
        StringBuilder sb = new StringBuilder();

        Enumeration<TreeNode> en = new PreOrderEnumeration<>(treeNode);

        while (en.hasMoreElements()) {
            TreeNode node = en.nextElement();

            for (int indents = 0; indents < getLevel(node); indents++) {
                sb.append("  ");
            }
            sb.append((node.isLeaf() ? "- " : "+ "));
            sb.append(node.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public static final int getLevel(TreeNode treeNode) {
        int count = -1;
        TreeNode current = treeNode;
        do {
            current = current.getParent();
            count++;
        } while (current != null);

        return count;
    }

    public static Enumeration<TreeNode> getPreOrderEnumeration(TreeNode treeNode) {
        return new PreOrderEnumeration<>(treeNode);
    }

    public static void expandAll(JTree jTree) {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    /**
     * Lifted and generalised from
     * {@link java.swing.tree.DefaultMutableTreeNode}
     */
    public static class PreOrderEnumeration<T extends TreeNode> implements
            Enumeration<T> {

        private T next;
        private Stack<Enumeration<T>> childrenEnums = new Stack<>();

        @SuppressWarnings("unchecked")
        PreOrderEnumeration(T node) {
            next = node;
            childrenEnums.push(node.children());
        }

        @Override
        public boolean hasMoreElements() {
            return next != null;
        }

        @Override
        public T nextElement() {
            if (next == null) {
                throw new NoSuchElementException("No more elements left.");
            }
            T current = next;
            Enumeration<T> children = childrenEnums.peek();
            next = traverse(children);

            return current;
        }

        @SuppressWarnings("unchecked")
        private T traverse(Enumeration<T> children) {
            // If more children are available step down.
            if (children.hasMoreElements()) {
                T child = children.nextElement();
                childrenEnums.push(child.children());
                return child;
            }

            // If no children are left, we return to a higher level.
            childrenEnums.pop();

            // If there are no more levels left, there is no next
            // element to return.
            if (childrenEnums.isEmpty()) {
                return null;
            } else {
                return traverse(childrenEnums.peek());
            }
        }
    }
}
