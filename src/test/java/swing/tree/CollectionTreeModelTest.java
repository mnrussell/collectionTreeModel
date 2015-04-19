package swing.tree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.swing.tree.TreeNode;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import swing.collectionTreeModel.CollectionTreeModel;
import swing.collectionTreeModel.TreeUtils;

import com.google.common.collect.Lists;

public class CollectionTreeModelTest {

    private final List<Book> testBookList = Lists.newArrayList(
            new Book("Orbit", "Iain M.Banks", "The Player Of Games"),
            new Book("Orbit", "Iain M.Banks", "Use Of Weapons"),
            new Book("Penguin", "William Gibson", "Virtual Light"),
            new Book("Viking Press", "William Gibson", "Idoru"),
            new Book("Putnam", "William Gibson", "Pattern Recognition"),
            new Book("Putnam", "Philip K.Dick", "The Man in the High Castle"),
            new Book("Ace", "William Gibson", "Neuromancer"),
            new Book("Doubleday", "Philip K.Dick", "Do Androids Dream of Electric Sheep?"),
            new Book("Doubleday", "Philip K.Dick", "Ubik"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CollectionTreeModel<Book> testModel;

    @Test
    public void rebuild_GivenAuthorNodeHasBeenSwappedWithPublisherNode() {
        givenTestModelWithNamedNodesOrderedBy_PublisherAuthorTitle();
        testModel.getHierarchy().swapNodes("Publisher", "Author");
        testModel.rebuild();

        String expectedString = "+ root\n" +
                "  + Iain M.Banks\n" +
                "    + Orbit\n" +
                "      - The Player Of Games\n" +
                "      - Use Of Weapons\n" +
                "  + William Gibson\n" +
                "    + Penguin\n" +
                "      - Virtual Light\n" +
                "    + Viking Press\n" +
                "      - Idoru\n" +
                "    + Putnam\n" +
                "      - Pattern Recognition\n" +
                "    + Ace\n" +
                "      - Neuromancer\n" +
                "  + Philip K.Dick\n" +
                "    + Putnam\n" +
                "      - The Man in the High Castle\n" +
                "    + Doubleday\n" +
                "      - Do Androids Dream of Electric Sheep?\n" +
                "      - Ubik\n";

        assertEquals(expectedString, TreeUtils.toString((TreeNode) testModel.getRoot()));
    }

    private void givenTestModelWithNamedNodesOrderedBy_PublisherAuthorTitle() {
        testModel = new CollectionTreeModel.Builder<>(testBookList)
                .addNode(b -> b.publisher, "Publisher")
                .addNode(b -> b.author, "Author")
                .addNode(b -> b.title, "Title").build();
    }
}
