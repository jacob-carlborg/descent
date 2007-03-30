package descent.ui.metrics.calculators.cohesion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FieldSet {
    private List fields;
    
    public FieldSet() {
        fields = new ArrayList();
    }
    
    public FieldSet(FieldSet set) {
        this();
        addAll(set);
    }
    
    public void add(String field) {
        if (!fields.contains(field)) {
            fields.add(field);
        }
    }
    
    public void addAll(FieldSet set) {
        Iterator iter = set.fields.iterator();
        while (iter.hasNext()) {
            add((String) iter.next());
        }
    }
    
    public boolean intersects(FieldSet set) {
        Iterator fieldIterator = fields.iterator();
        while (fieldIterator.hasNext()) {
            if (set.contains((String) fieldIterator.next())) {
                return true;
            }
        }

        return false;
    }
    
    public boolean contains(String field) {
        return fields.contains(field);
    }

    public int size() {
        return fields.size();
    }
}