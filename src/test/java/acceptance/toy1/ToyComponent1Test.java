package acceptance.toy1;

import acceptance.toy1.bjs.UserOfToyComponent1;
import acceptance.toy1.components.ToyComponent1;
import acceptance.toy1.components.ToyComponent2;
import bionic.js.BjsProject;
import jjbridge.engine.v8.V8Engine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToyComponent1Test
{
    @BeforeAll
    public static void beforeClass()
    {
        BjsProject.setJsEngine(new V8Engine());
    }

    @Test
    public void testWrapped_getSum() {
        ToyComponent1 wrapped = new ToyComponent1("1", "2");
        assertEquals(6, wrapped.getSum(3L));
    }

    @Test
    public void testWrapped_getToySum() {
        ToyComponent1 wrapped1 = new ToyComponent1("1", "2");
        ToyComponent1 wrapped2 = new ToyComponent1("3", "4");
        assertEquals(10, wrapped2.getToySum(wrapped1));
    }

    @Test
    public void testFromJs_sameInstance() {
        ToyComponent1 toy = new ToyComponent1("1", "2");
        UserOfToyComponent1.lastToy(toy);
        assertEquals(toy, UserOfToyComponent1.lastToy());
    }

    @Test
    public void interactingWithSubclass() {
        assertEquals(ToyComponent1.PI() + ToyComponent1.PI(), UserOfToyComponent1.piSum());
    }

    @Test
    public void interactingWithSubclass2() {
        assertEquals(69420, UserOfToyComponent1.additionalValue(new ToyComponent2("0", "0")));
    }

    @Test
    public void interactingWithSubclass3() {
        ToyComponent1 toy1 = new ToyComponent1("0", "0");
        ToyComponent2 toy2 = new ToyComponent2("0", "0");

        assertEquals(0, UserOfToyComponent1.getSum(toy1, toy1));
        assertEquals(3, UserOfToyComponent1.getSum(toy2, toy1));
        assertEquals(3, UserOfToyComponent1.getSum2(toy2, toy1));
        assertEquals(0, UserOfToyComponent1.getSum(toy1, toy2));
        assertEquals(4, UserOfToyComponent1.getSum(toy2, toy2));
        assertEquals(4, UserOfToyComponent1.getSum2(toy2, toy2));
    }
}
