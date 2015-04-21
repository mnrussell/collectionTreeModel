package uk.polletto.swing.collectionTreeModel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * This class is primarily for use by {@link CollectionTreeModel} to specify the
 * hierarchical structure for a given Object type. Methods are provided to
 * modify and query the hierarchical structure.
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
 * +publisher
 *   + author
 *     + title
 * </pre></blockquote>
 * </p>
 * <p>
 * To achieve this our Hierarchy class would be constructed as follows:
 * </p>
 * <p>
 * <blockquote><pre>
 * Hierarchy{@code<Book>} hierarchy = new Hierarchy{@code<>}();
 * hierarchy.addNode(b -> b.publisher);
 * hierarchy.addNode(b -> b.author);
 * hierarchy.addNode(b -> b.title);
 * </pre></blockquote>
 * </p>
 * <p>
 * Nodes can be assigned an id; useful when swapping nodes:
 * </p>
 * <p>
 * <blockquote><pre>
 * Hierarchy{@code<Book>} hierarchy = new Hierarchy{@code<>}();
 * hierarchy.addNode(b -> b.publisher, "PublisherNode");
 * hierarchy.addNode(b -> b.author, "AuthorNode");
 * hierarchy.addNode(b -> b.title);
 *
 * System.out.println(hierarchy.getNodeIds());
 *
 * hierarchy.swapNodes("PublisherNode", "AuthorNode");
 *
 * System.out.println(hierarchy.getNodeIds());
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
 * <p>
 * As seen above when no id is provided then a default id of the form Node[i] is
 * automatically allocated; where i is the index+1 of the node at the time it
 * was added.
 * </p>
 * @param <T> The type of data for which we want to specify a Hierarchy.
 * @see CollectionTreeModel
 */
public class Hierarchy<T> {

    private final List<Function<T, Object>> nodeObjectProviders = Collections
            .synchronizedList(new ArrayList<>());

    private final BiMap<Object, Function<T, Object>> nodeIdToProviderMap = HashBiMap
            .create();

    private static final String defaultNodeIdFormat = "Node%d";

    /**
     * <p>
     * Adds the given node object provider forming a new node in the hierarchy.
     * The node will be allocated a generated id of the form
     * <code>Id<i>n</i></code> where <code><i>n</i></code> is a number
     * representing the index of the added provider.
     * </p>
     * @param nodeObjectProvider Functional interface providing the data object
     *        for the node.
     */
    public void addNode(Function<T, Object> nodeObjectProvider) {
        addNode(nodeObjectProvider, createDefaultProviderId());
    }

    private String createDefaultProviderId() {
        return String.format(defaultNodeIdFormat,
                nodeObjectProviders.size() + 1);
    }

    /**
     * <p>
     * Adds the given data provider forming a new node in the hierarchy. The
     * node is allocated the specified id. Care should be taken by the user to
     * ensure this id is both non-null and unique; duplicate id's will result in
     * an IllegalArgumentException being thrown.
     * </p>
     * @param nodeObjectProvider Functional interface providing the data object
     *        from T.
     * @param nodeId User specified unique, non-null identifier for this node.
     * @exception IllegalArgumentException if the nodeId is equivalent to one
     *            previously added or generated.
     */
    public void addNode(Function<T, Object> nodeObjectProvider, Object nodeId) {
        addNodeProviderAndIdMapping(nodeObjectProvider, nodeId);
    }

    private void addNodeProviderAndIdMapping(
            Function<T, Object> nodeObjectProvider, Object nodeId) {
        checkNotNull(nodeObjectProvider);
        checkNotNull(nodeId);
        checkArgument(!nodeIdToProviderMap.containsKey(nodeId),
                "Duplicate node id ('%s').", nodeId);

        nodeObjectProviders.add(nodeObjectProvider);
        nodeIdToProviderMap.forcePut(nodeId, nodeObjectProvider);
    }

    /**
     * <p>
     * Returns an ImmutableList of the Functional interfaces forming the node
     * object providers.
     * </p>
     * @return ImmutableList of Functional node object providers.
     */
    ImmutableList<Function<T, Object>> getNodeObjectProviders() {
        return ImmutableList.copyOf(nodeObjectProviders);
    }

    /**
     * <p>
     * Returns an ImmutableList of the node id's in the same order as the
     * corresponding nodes.
     * </p>
     * @return ImmutableList of provider id's.
     */
    public ImmutableList<Object> getNodeIds() {
        synchronized (nodeObjectProviders) {
            return ImmutableList.copyOf(nodeObjectProviders.stream()
                    .map(s -> nodeIdToProviderMap.inverse().get(s))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * <p>
     * Swap nodes corresponding to the given id's. If
     * <code>nodeId1.equals(nodeId1)</code> then no change occurs.
     * </p>
     * @param nodeId1
     * @param nodeId2
     * @throws IndexOutOfBoundsException if either of the specified provider
     *         id's do not correspond to any of the current providers.
     * @see #indexOf(Object)
     * @see #swapNodes(int, int)
     */
    public void swapNodes(Object nodeId1, Object nodeId2) {
        swapNodes(indexOf(nodeId1), indexOf(nodeId2));
    }

    /**
     * <p>
     * Swap nodes corresponding to the given zero based indices. If
     * <code>(i == j)</code> then no change occurs. If either of the specified
     * indices are not in the range <code>0 <= index < nodes.size()</code> then
     * an IndexOutOfBoundsException is thrown.
     * </p>
     * @param i index of first node to swap.
     * @param j index of second node to swap.
     * @throws IndexOutOfBoundsException if either of the specified indices are
     *         not in range.
     * @see #indexOf(Object)
     * @see #swapNodes(Object, Object)
     */
    public void swapNodes(int i, int j) {
        Collections.swap(nodeObjectProviders, i, j);
    }

    /**
     * <p>
     * Return the index of the node associated with the given id.
     * </p>
     * @param nodeId The unique provider identifier.
     * @return The index of the provider with the specified id where
     *         <code>0 <= index < node.size()</code>
     */
    public final int indexOf(Object nodeId) {
        synchronized (nodeObjectProviders) {
            Function<T, Object> providerMatch = nodeIdToProviderMap.get(nodeId);
            return (providerMatch == null) ? -1 : nodeObjectProviders
                    .indexOf(providerMatch);
        }
    }
}
