package swing.demo.collectionTreeModel;

import static com.google.common.base.Preconditions.checkState;
import static swing.UIConstants.GAP;
import info.clearthought.layout.TableLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/**
 * <p>
 * Rudimentary Decorator which takes a JList and returns a JPanel component with
 * Move Up / Down buttons to allow re-ordering of the List elements. Allows a
 * single {@link ListDataSwapListener} to be added and fires
 * {@link ListDataSwapEvent} when elements are swapped around.
 * </p>
 * <p>
 * The {@link ListModel} returned by the {@link JList} must be an instance of
 * {@link DefaultListModel}. The reason being {@link DefaultListModel} has set
 * operations allowing reordering of the model elements.
 * </p>
 * @param <T> The object type of the list model.
 */
public class MoveableListPanel<T> extends JPanel {

    private final JList<T> jList;
    private JButton upButton;
    private JButton downButton;

    private ListDataSwapListener swapListener;

    public MoveableListPanel(JList<T> jList) {
        checkState(jList.getModel() instanceof DefaultListModel,
                "jList.getModel() should be an instance of DefaultListModel.");
        this.jList = jList;

        initComponents();
        layoutComponents();
    }

    public void setSwapListener(ListDataSwapListener l) {
        swapListener = l;
    }

    private void initComponents() {
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        upButton = new JButton("Move Up");
        downButton = new JButton("Move Down");

        upButton.addActionListener(evt -> {
            int selIndex = jList.getSelectedIndex();

            if (selIndex > 0) {
                swapListElements(selIndex, selIndex - 1);
                jList.setSelectedIndex(selIndex - 1);
                jList.ensureIndexIsVisible(selIndex - 1);
            }
        });

        downButton.addActionListener(evt -> {
            int selIndex = jList.getSelectedIndex();

            if (selIndex != -1 && selIndex < jList.getModel().getSize() - 1) {
                swapListElements(selIndex, selIndex + 1);
                jList.setSelectedIndex(selIndex + 1);
                jList.ensureIndexIsVisible(selIndex + 1);
            }
        });

        jList.addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                setButtonStatesAccordingToListSelection();
            }
        });

        setButtonStatesAccordingToListSelection();
    }

    private void swapListElements(int i, int j) {
        DefaultListModel<T> listModel = (DefaultListModel<T>) jList.getModel();
        T aObject = listModel.getElementAt(i);
        T bObject = listModel.getElementAt(j);
        listModel.set(i, bObject);
        listModel.set(j, aObject);

        if (swapListener != null) {
            swapListener
                    .listDataSwapped(new ListDataSwapEvent(listModel, i, j));
        }

    }

    private void setButtonStatesAccordingToListSelection() {
        int selIndex = jList.getSelectedIndex();
        int lastIndex = jList.getModel().getSize() - 1;

        upButton.setEnabled(selIndex > 0);
        downButton.setEnabled(selIndex != -1 && selIndex < lastIndex);
    }

    private void layoutComponents() {
        double[][] layout = new double[][] {
                { TableLayout.FILL, GAP, 100, GAP },
                { 30, GAP, 30, 100, TableLayout.FILL } };

        setLayout(new TableLayout(layout));
        add(new JScrollPane(jList), "0,0, 0,3");
        add(upButton, "2,0");
        add(downButton, "2,2");
    }
}
