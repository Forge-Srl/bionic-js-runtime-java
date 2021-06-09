package bionic.js.testutils;

import bionic.js.Bjs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
public abstract class TestWithBjsMock {
    @Mock protected Bjs bjs;
    private NavigableMap<String[], Bjs> projects;

    public static final String BJS_TEST_PACKAGE = "bionic.js.testutils";
    public static final String BJS_TEST_PROJECT = "MockedTestProject";
    private static final Comparator<String[]> comparator = Comparator.comparing((String[] o) -> o[0])
            .thenComparing(o -> o[1]);

    @BeforeEach
    public void beforeEach() {
        projects = new TreeMap<>(comparator);
        exposeMockedBjsTo(BJS_TEST_PACKAGE);
        // Inject mocked Bjs instance to be returned by Bjs.get(projectName)
        mockPrivateStaticField(Bjs.class, "projects", projects);
    }

    @AfterEach
    public void afterEach() {
        // Remove injected mock
        mockPrivateStaticField(Bjs.class, "projects", new TreeMap<>(comparator));
        projects.clear();
    }

    public void exposeMockedBjsTo(String packageName) {
        projects.put(new String[] {packageName, BJS_TEST_PROJECT}, bjs);
    }

    private static void mockPrivateStaticField(Class<?> clazz, String name, Object value)
    {
        try
        {
            Field getField = clazz.getDeclaredField(name);
            getField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(getField, getField.getModifiers() & ~Modifier.FINAL);
            getField.set(null, value);
            modifiersField.setInt(getField, getField.getModifiers() & Modifier.FINAL);
            modifiersField.setAccessible(false);
            getField.setAccessible(false);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            fail(e.toString());
        }
    }
}
