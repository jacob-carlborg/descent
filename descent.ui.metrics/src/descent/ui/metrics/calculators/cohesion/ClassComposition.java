package descent.ui.metrics.calculators.cohesion;

import java.util.ArrayList;
import java.util.List;

import descent.core.JavaModelException;

public class ClassComposition {
    private List methods = new ArrayList();
    private FieldSet allFields = new FieldSet();
    
    public void addMethod(FieldSet fields) {
        if (fields.size() > 0) {
            methods.add(fields);
            allFields.addAll(fields);
        }
    }

    int getNumberOfMethods() {
        return methods.size();
    }
    
    private FieldSet getMethod(int index) {
        return (FieldSet) methods.get(index);
    }

    public int getChidamberKemererLackOfCohesion() {
        int noCommon = 0;
        int common = 0;
        for (int i = 1; i < methods.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (getMethod(i).intersects(getMethod(j))) {
                    common++;
                } else {
                    noCommon++;
                }
            }
        }
        
        return Math.max(0, noCommon - common);
    }
    
    public double getHendersonSellersLackOfCohesion() throws JavaModelException {
        int sumOfFieldCounts = getSumOfFieldsUsedPerMethod();
        
        double averageFieldCount = ((double) sumOfFieldCounts) / allFields.size();
        return (averageFieldCount - methods.size()) / (1 - methods.size());
    }

    private int getSumOfFieldsUsedPerMethod() {
        int sum = 0;

        for (int i = 0; i < getNumberOfMethods(); i++) {
            sum += getMethod(i).size();
        }
        return sum;
    }
}
