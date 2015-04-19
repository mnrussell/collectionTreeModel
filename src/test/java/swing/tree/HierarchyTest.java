package swing.tree;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import swing.collectionTreeModel.Hierarchy;

import com.google.common.collect.ImmutableList;

public class HierarchyTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Hierarchy<Book> testHierarchy;

    @Before
    public void beforeEachTest() {
        testHierarchy = new Hierarchy<>();
    }

    @Test
    public void testDuplicateNodesDoesNotThrowException() {
        testHierarchy.addNode(b -> b.publisher, "Publisher");
        testHierarchy.addNode(b -> b.author, "Author");
        testHierarchy.addNode(b -> b.author, "AuthorAgain");
        testHierarchy.addNode(b -> b.title, "Title");

        assertEquals(
                ImmutableList.of("Publisher", "Author", "AuthorAgain", "Title"),
                testHierarchy.getNodeIds());
    }

    @Test
    public void getNodeIds_GivenIdsProvidedForAll() {
        givenHierarchyWithThreeNamedNodes();

        assertEquals(ImmutableList.of("Publisher", "Author", "Title"),
                testHierarchy.getNodeIds());
    }

    @Test
    public void getNodeIds_GivenIdsProvidedForSome() {
        testHierarchy.addNode(b -> b.publisher, "Publisher");
        testHierarchy.addNode(b -> b.author, "Author");
        testHierarchy.addNode(b -> b.title);

        assertEquals(ImmutableList.of("Publisher", "Author", "Node3"),
                testHierarchy.getNodeIds());
    }

    @Test
    public void getNodeIds_GivenNoIdsProvided() {
        givenHierarchyWithThreeNodes();

        assertEquals(ImmutableList.of("Node1", "Node2", "Node3"),
                testHierarchy.getNodeIds());
    }

    @Test
    public void indexOfNode() {
        givenHierarchyWithThreeNamedNodes();

        assertEquals(0, testHierarchy.indexOf("Publisher"));
        assertEquals(1, testHierarchy.indexOf("Author"));
        assertEquals(2, testHierarchy.indexOf("Title"));
    }

    @Test
    public void indexOfNode_GivenNodeIdDoesNotExist() {
        givenHierarchyWithThreeNamedNodes();

        assertEquals(-1, testHierarchy.indexOf("foobarId"));
    }

    @Test
    public void swapNodesUsingNodeIds() {
        givenHierarchyWithThreeNamedNodes();
        testHierarchy.swapNodes("Publisher", "Author");

        assertEquals(0, testHierarchy.indexOf("Author"));
        assertEquals(1, testHierarchy.indexOf("Publisher"));
    }

    @Test
    public void swapNodesUsingIndices() {
        givenHierarchyWithThreeNodes();
        testHierarchy.swapNodes(0, 1);

        assertEquals(0, testHierarchy.indexOf("Node2"));
        assertEquals(1, testHierarchy.indexOf("Node1"));
    }

    @Test
    public void testThrowsIllegalArgumentException_GivenDuplicateNodeId() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate node id ('Author').");

        testHierarchy.addNode(b -> b.author, "Author");
        testHierarchy.addNode(b -> b.title, "Author");
    }

    private void givenHierarchyWithThreeNodes() {
        testHierarchy.addNode(b -> b.publisher);
        testHierarchy.addNode(b -> b.author);
        testHierarchy.addNode(b -> b.title);
    }

    private void givenHierarchyWithThreeNamedNodes() {
        testHierarchy.addNode(b -> b.publisher, "Publisher");
        testHierarchy.addNode(b -> b.author, "Author");
        testHierarchy.addNode(b -> b.title, "Title");
    }
}
