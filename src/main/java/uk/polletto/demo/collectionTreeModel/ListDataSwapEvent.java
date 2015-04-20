package uk.polletto.demo.collectionTreeModel;

import java.util.EventObject;

public class ListDataSwapEvent extends EventObject {

    private final int swapIndex0;
    private final int swapIndex1;

    public ListDataSwapEvent(Object src, int i, int j) {
        super(src);
        swapIndex0 = i;
        swapIndex1 = j;
    }

    public int getSwapIndex0() {
        return swapIndex0;
    }

    public int getSwapIndex1() {
        return swapIndex1;
    }
}
