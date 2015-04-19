package swing.demo.collectionTreeModel;

import static swing.UIConstants.GAP;
import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import swing.collectionTreeModel.CollectionTreeModel;
import swing.collectionTreeModel.Hierarchy;
import swing.collectionTreeModel.TreeUtils;
import swing.demo.DemoUtils;

import com.google.common.collect.Lists;

public class CollectionTreeModelDemo extends JPanel {

    class Book {
        final String publisher;
        final String author;
        final String title;

        Book(String publisher, String author, String title) {
            this.publisher = publisher;
            this.author = author;
            this.title = title;
        }

        String getPublisher() {
            return publisher;
        }

        String getAuthor() {
            return author;
        }

        String getTitle() {
            return title;
        }
    }

    private Collection<Book> bookCollection = Lists.newArrayList(new Book(
            "Orbit", "Iain M.Banks", "The Player Of Games"), new Book("Orbit",
            "Iain M.Banks", "Use Of Weapons"), new Book("Penguin",
            "William Gibson", "Virtual Light"), new Book("Viking Press",
            "William Gibson", "Idoru"), new Book("Putnam", "William Gibson",
            "Pattern Recognition"), new Book("Putnam", "Philip K.Dick",
            "The Man in the High Castle"), new Book("Ace", "William Gibson",
            "Neuromancer"), new Book("Doubleday", "Philip K.Dick",
            "Do Androids Dream of Electric Sheep?"), new Book("Doubleday",
            "Philip K.Dick", "Ubik"));

    private Hierarchy<Book> bookHierarchy;
    private CollectionTreeModel<Book> treeModel;
    private JTree jTree;

    public CollectionTreeModelDemo() {
        initTree();
        layoutComponents();
    }

    private void initTree() {
        bookHierarchy = new Hierarchy<>();
        bookHierarchy.addNode(b -> b.getPublisher(), "Publisher");
        bookHierarchy.addNode(b -> b.getAuthor(), "Author");
        bookHierarchy.addNode(b -> b.getTitle(), "Title");

        treeModel = new CollectionTreeModel<Book>(bookCollection, bookHierarchy);

        jTree = new JTree(treeModel);
        jTree.setShowsRootHandles(true);
        jTree.setRootVisible(false);
        TreeUtils.expandAll(jTree);
    }

    private void layoutComponents() {
        setLayout(new TableLayout(new double[][] {
                { GAP, TableLayout.FILL, GAP, 250, GAP },
                { GAP, TableLayout.FILL, GAP } }));
        add(getJTreePanel(), "1,1");
        add(getHierarchyControlPanel(), "3,1");
    }

    private JPanel getJTreePanel() {
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setPreferredSize(new Dimension(320, 480));
        treePanel.add(new JScrollPane(jTree), BorderLayout.CENTER);
        return treePanel;
    }

    private JPanel getHierarchyControlPanel() {
        DefaultListModel<Object> listModel = new DefaultListModel<>();

        bookHierarchy.getNodeIds().forEach(id -> listModel.addElement(id));

        JList<Object> jList = new JList<>(listModel);
        jList.setSelectedIndex(0);

        MoveableListPanel<Object> controlPanel = new MoveableListPanel<Object>(
                jList);

        controlPanel.setSwapListener(evt -> {
            bookHierarchy.swapNodes(evt.getSwapIndex0(), evt.getSwapIndex1());
            treeModel.rebuild();
            TreeUtils.expandAll(jTree);
        });

        return controlPanel;
    }

    public static void main(String[] args) {
        DemoUtils.createAndShowDemoFrame("CollectionTreeModel Demo",
                new CollectionTreeModelDemo());
    }
}
