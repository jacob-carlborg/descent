package descent.ui.metrics.calculators;

public final class CalculatorUtils {
    private static final String METHOD_SUB_ID = "method";
    private static final String TYPE_SUB_ID = "type";

    private CalculatorUtils() {
    }

    public static final String createMethodMetricId(Class calculatorClass) {
        return createMetricId(calculatorClass, METHOD_SUB_ID);
    }

    public static final String createTypeMetricId(Class calculatorClass) {
        return createMetricId(calculatorClass, TYPE_SUB_ID);
    }

    public static final String createMetricId(Class calculatorClass, String subId) {
        return calculatorClass.getName() + "." + subId;
    }
}
