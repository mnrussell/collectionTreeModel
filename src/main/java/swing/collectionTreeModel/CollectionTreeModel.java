package swing.collectionTreeModel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * This TreeModel implementation allows a TreeModel to be built based on a given
 * Collection<T> and a Hierarchy specification for the type T.
 * </p>
 * <p>
 * Imagine we have the following:
 * </p>
 * <p>
 * <blockquote><pre>
 * class Book {
 *     String publisher;
 *     String author;
 *     String title;
 * }
 *
 * Collection{@code<Book>} books;
 * </pre></blockquote>
 * </p>
 * <p>
 * We would like the Collection of books represented in a
 * {@link CollectionTreeModel} as:
 * </p>
 * <p>
 * <blockquote><pre>
 * +publisher + author + title
 * </pre></blockquote>
 * </p>
 * <p>
 * To achieve this we can build our CollectionTreeModel as follows:
 * </p>
 * <p>
 * <blockquote><pre>
 * CollectionTreeModel{@code<Book>} treeModel =
 *     CollectionTreeModel.Builder{@code<>}(books)
 *         .addNode(b -> b.publisher)
 *         .addNode(b -> b.author)
 *         .addNode(b -> b.title)
 *         .build();
 * </pre></blockquote>
 * </p>
 * <p>
 * Nodes can be assigned an id; useful when swapping nodes:
 * </p>
 * <p>
 * <blockquote><pre>
 * CollectionTreeModel{@code<Book>} treeModel =
 *     CollectionTreeModel.Builder{@code<>}(books)
 *         .addNode(b -> b.publisher, "PublisherNode")
 *         .addNode(b -> b.author, "AuthorNode")
 *         .addNode(b -> b.title)
 *         .build();
 *
 * System.out.println(hierarchy.getNodeIds());
 *
 * treeModel.getHierarchy().swapNodes("PublisherNode", "AuthorNode");
 *
 * System.out.println(treeModel.getHierarchy().getNodeIds());
 *
 * treeModel.rebuild();
 *
 * </pre></blockquote>
 * </p>
 * <p>
 * Would output:
 * </p>
 * <p>
 * <blockquote><pre>
 * [PublisherNode, AuthorNode, Node3]
 * [AuthorNode, PublisherNode, Node3]
 * </pre></blockquote>
 * </p>
 * @param <T>
 * @see Hierarchy
 */
public class CollectionTreeModel<T> extends AbstractImmutableTreeModel {

    private final Hierarchy<T> hierarchy;
    private final Collection<T> srcData;

    private CollectionTreeModel(Builder<T> builder) {
        this(builder.srcData, builder.hierarchy);
    }

    public CollectionTreeModel(Collection<T> srcData, Hierarchy<T> hierarchy) {
        this.srcData = checkNotNull(srcData);
        this.hierarchy = checkNotNull(hierarchy);
        build();
    }

    private void build() {
        root = new ImmutableTreeNode("root");
        srcData.forEach(dataRecord -> addDataRecordToRoot(dataRecord));
    }

    private void addDataRecordToRoot(T dataRecord) {
        Iterator<Object> userObjectIterator = hierarchy
                .getNodeObjectProviders().stream()
                .map(s -> s.apply(dataRecord)).collect(Collectors.toList())
                .iterator();

        ImmutableTreeNode parent = root;

        while (userObjectIterator.hasNext()) {
            Object userObject = userObjectIterator.next();

            if (userObjectIterator.hasNext()) {
                parent = getNodeForObjectUnderParent(userObject, parent);
            } else {
                // Last node object is a leaf; duplicate user objects ARE
                // allowed, but no children.
                parent.addChild(new ImmutableTreeNode(userObject, false));
            }
        }
    }

    private ImmutableTreeNode getNodeForObjectUnderParent(Object userObject,
            ImmutableTreeNode parent) {
        Enumeration<ImmutableTreeNode> children = parent.children();

        while (children.hasMoreElements()) {
            ImmutableTreeNode child = children.nextElement();

            if (child.getUserObject().equals(userObject)) {
                return child;
            }
        }

        ImmutableTreeNode newChild = new ImmutableTreeNode(userObject);
        parent.addChild(newChild);
        return newChild;
    }

    /**
     * Rebuild the tree model, usually after hierarchy nodes have been swapped.
     * @see #getHierarchy
     * @see Hierarchy.swapNodes
     */
    public void rebuild() {
        build();
        fireRootStructureChanged();
    }

    private void fireRootStructureChanged() {
        int n = getChildCount(root);
        int[] childIdx = new int[n];
        Object[] children = new Object[n];

        for (int i = 0; i < n; i++) {
            childIdx[i] = i;
            children[i] = getChild(root, i);
        }
        fireTreeStructureChanged(this, new Object[] { root }, childIdx,
                children);
    }

    /**
     * Get the hierarchy specification for querying nodes, modification,
     * swapping nodes etc.
     * @return The Hierarchy specification.
     */
    public Hierarchy<T> getHierarchy() {
        return hierarchy;
    }

    /**
     * Convenience class to help build the model.
     * @param <T>
     */
    public static class Builder<T> {

        private final Collection<T> srcData;
        private final Hierarchy<T> hierarchy;

        public Builder(Collection<T> srcData) {
            hierarchy = new Hierarchy<>();
            this.srcData = srcData;
        }

        public Builder<T> addNode(Function<T, Object> provider) {
            hierarchy.addNode(provider);
            return this;
        }

        public Builder<T> addNode(Function<T, Object> provider, Object nodeId) {
            hierarchy.addNode(provider, nodeId);
            return this;
        }

        public CollectionTreeModel<T> build() {
            return new CollectionTreeModel<>(this);
        }
    }
}
