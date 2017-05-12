package knowledge;

import ab.vision.ABObject;
import java.util.Comparator;

public class XComparator implements Comparator<ABObject> {

    public int compare(ABObject a, ABObject b) {
        return a.x - b.x;
    }
}
