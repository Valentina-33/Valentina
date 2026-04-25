package org.junit.jupiter.api;
public class Assertions {
    public interface Executable { void execute() throws Throwable; }

    public static void assertTrue(boolean c) { if(!c) throw new AssertionError(); }
    public static void assertTrue(boolean c, String m) { if(!c) throw new AssertionError(m); }
    public static void assertFalse(boolean c) { if(c) throw new AssertionError(); }
    public static void assertFalse(boolean c, String m) { if(c) throw new AssertionError(m); }

    public static void assertEquals(Object a, Object b) {
        if(a==null?b!=null:!a.equals(b)) throw new AssertionError("expected="+a+", actual="+b);
    }
    public static void assertEquals(Object a, Object b, String m) {
        if(a==null?b!=null:!a.equals(b)) throw new AssertionError(m+" (expected="+a+", actual="+b+")");
    }
    public static void assertEquals(int a, int b) { if(a!=b) throw new AssertionError("expected="+a+", actual="+b); }
    public static void assertEquals(int a, int b, String m) { if(a!=b) throw new AssertionError(m+" (expected="+a+", actual="+b+")"); }
    public static void assertEquals(long a, long b) { if(a!=b) throw new AssertionError(); }
    public static void assertEquals(long a, long b, String m) { if(a!=b) throw new AssertionError(m); }

    public static void assertAll(Executable... es) {
        AssertionError first = null;
        for(Executable e:es) {
            try { e.execute(); }
            catch(AssertionError ae) { if(first==null) first=ae; }
            catch(Throwable t) { if(first==null) first=new AssertionError(t); }
        }
        if(first!=null) throw first;
    }
    public static <T extends Throwable> T assertThrows(Class<T> c, Executable e) {
        try { e.execute(); }
        catch(Throwable t) {
            if(c.isInstance(t)) return c.cast(t);
            throw new AssertionError("wrong type: "+t.getClass()+" - "+t.getMessage());
        }
        throw new AssertionError("did not throw "+c.getSimpleName());
    }
    public static <T extends Throwable> T assertThrows(Class<T> c, Executable e, String m) {
        return assertThrows(c, e);
    }
}
