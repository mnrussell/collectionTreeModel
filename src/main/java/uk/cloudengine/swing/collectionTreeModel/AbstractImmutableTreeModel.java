package uk.cloudengine.swing.collectionTreeModel;

import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * <p>
 * A bare bones immutable implementation of {@link TreeModel}. Immutable in the
 * sense that it assumes there will be no adding, removing, modifying individual
 * nodes; hence the use of {@link ImmutableTreeNode} and
 * {@link valueForPathChanged} has an empty implementation.
 * </p>
 * <p>
 * Structural change listeners are supported. At the structural level the whole
 * tree may be rebuilt.
 * </p>
 * @see ImmutableTreeNode
 * @see CollectionTreeModel
 */
abstract class AbstractImmutableTreeModel implements TreeModel {

    protected EventListenerList listenerList = new EventListenerList();
    protected ImmutableTreeNode root;

    // In the absence of a generic TreeModel interface this may be handy when we
    // can guarantee the argument object is of type TreeNode.
    private TreeNode castToTreeNode(Object obj) {
        return (TreeNode) obj;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return castToTreeNode(parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return castToTreeNode(parent).getChildCount();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return castToTreeNode(parent).getIndex(castToTreeNode(child));
    }

    @Override
    public boolean isLeaf(Object node) {
        return castToTreeNode(node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Registers a listener to the model.
     * @param listener the listener to add
     */
    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        listenerList.add(TreeModelListener.class, listener);
    }

    /**
     * Removes a listener from the model.
     * @param listener the listener to remove
     */
    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listenerList.remove(TreeModelListener.class, listener);
    }

    /**
     * Returns all registered <code>TreeModelListener</code> listeners.
     * @return an array of listeners.
     */
    public TreeModelListener[] getTreeModelListeners() {
        return listenerList.getListeners(TreeModelListener.class);
    }

    /**
     * Returns the registered listeners of a given type.
     * @param listenerType The listener type to return
     * @return an array of listeners
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    /**
     * fireTreeStructureChanged.
     * @param source The node where the model has changed
     * @param path The path to the root node
     * @param childIndices The indices of the affected elements
     * @param children The affected elements
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        TreeModelListener[] listeners = getTreeModelListeners();
        if (listeners.length == 0) {
            return;
        }
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
                children);
        for (int i = listeners.length - 1; i >= 0; --i)
            listeners[i].treeStructureChanged(event);
    }
}
