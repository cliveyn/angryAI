package knowledge;

import ab.vision.ABObject;
import java.util.Comparator;

public class YComparator implements Comparator<ABObject> {

    public int compare(ABObject a, ABObject b) {
        return a.y - b.y;
    }
}
