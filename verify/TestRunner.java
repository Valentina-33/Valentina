import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        runClass(Class.forName("eci.edu.byteProgramming.ejercicio.paper.ApplicationTest"));
        runClass(Class.forName("eci.edu.byteProgramming.ejercicio.paper.util.auxiliaryTest"));
        System.out.println();
        System.out.println("============================================");
        System.out.println(" Tests JUnit (src/test/java): " + passed + " passed, " + failed + " failed");
        System.out.println("============================================");
        System.exit(failed == 0 ? 0 : 1);
    }

    static void runClass(Class<?> cls) throws Exception {
        System.out.println();
        System.out.println(">>> " + cls.getName());
        java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor(); ctor.setAccessible(true); Object instance = ctor.newInstance();
        for (Method m : cls.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                m.setAccessible(true);
                try {
                    m.invoke(instance);
                    System.out.println("  [PASS] " + m.getName());
                    passed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.out.println("  [FAIL] " + m.getName() + " -> " + cause);
                    failed++;
                }
            }
        }
    }
}
