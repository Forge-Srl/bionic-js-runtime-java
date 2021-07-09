package bionic.js;

import jjbridge.api.value.JSFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeoutHandlerTest {
    private static final int startingId = 15;
    private TimeoutHandler timeoutHandler;

    @Mock private ScheduledThreadPoolExecutor scheduledExecutor;

    @BeforeEach
    public void before() {
        timeoutHandler = new TimeoutHandler(startingId, scheduledExecutor);
    }

    @Test
    public void runDelayed_singleInvocation() {
        int[] value = new int[1];

        JSFunction<?> function = mock(JSFunction.class);
        doAnswer(invocation -> {
            value[0] = 2;
            return null;
        }).when(function).invoke(null);

        Runnable[] task = mockSchedule(1000);
        int id = timeoutHandler.runDelayed(function, null, 1000);

        assertNotEquals(2, value[0]);
        assertTrue(timeoutHandler.exists(id));
        task[0].run();
        assertEquals(2, value[0]);
        assertFalse(timeoutHandler.exists(id));
        assertNull(task[0]);
    }

    @Test
    public void runDelayed_multipleInvocation() {
        int[] value = new int[] {0};
        final int increment = 5;
        final int n = 35;
        final int delay = 140;
        Runnable[][] tasks = new Runnable[n][];

        JSFunction<?> function = mock(JSFunction.class);
        doAnswer(invocation -> {
            value[0] += increment;
            return null;
        }).when(function).invoke(null);

        for (int i = 1; i <= n; i++) {
            int singleDelay = delay * i;
            tasks[i - 1] = mockSchedule(singleDelay);
            assertEquals(startingId + i, timeoutHandler.runDelayed(function, null, singleDelay));
        }

        assertEquals(0, value[0]);
        for (int i = 1; i <= n; i++) {
            assertTrue(timeoutHandler.exists(startingId + i));
            tasks[i - 1][0].run();
            assertEquals(increment * i, value[0]);
            assertFalse(timeoutHandler.exists(startingId + i));
            assertNull(tasks[i - 1][0]);
        }
    }

    @Test
    public void runDelayed_remove() {
        int[] value = new int[] {0};

        JSFunction<?> function = mock(JSFunction.class);
        lenient().doAnswer(invocation -> {
            value[0] = 999;
            return null;
        }).when(function).invoke(null);

        Runnable[] task = mockSchedule(500);
        int id = timeoutHandler.runDelayed(function, null, 500);

        assertEquals(0, value[0]);
        assertTrue(timeoutHandler.exists(id));
        timeoutHandler.remove(id);
        assertFalse(timeoutHandler.exists(id));
        assertNull(task[0]);
    }

    @Test
    public void runAtFixedRate_singleInvocation() {
        int[] value = {0};

        JSFunction<?> function = mock(JSFunction.class);
        doAnswer(invocation -> {
            value[0] += 2;
            return null;
        }).when(function).invoke(null);

        Runnable[] task = mockScheduleAtFixedRate(500);
        int id = timeoutHandler.runAtFixedRate(function, null, 500);

        assertEquals(0, value[0]);
        assertTrue(timeoutHandler.exists(id));
        task[0].run();
        assertEquals(2, value[0]);
        assertTrue(timeoutHandler.exists(id));
        task[0].run();
        assertEquals(4, value[0]);
        assertTrue(timeoutHandler.exists(id));
        task[0].run();
        assertEquals(6, value[0]);
        assertTrue(timeoutHandler.exists(id));

        timeoutHandler.remove(id);
        assertNull(task[0]);
        assertEquals(6, value[0]);
        assertFalse(timeoutHandler.exists(id));
    }

    @Test
    public void runAtFixedRate_remove() {
        int[] value = new int[] {0};

        JSFunction<?> function = mock(JSFunction.class);
        lenient().doAnswer(invocation -> {
            value[0] = 999;
            return null;
        }).when(function).invoke(null);

        Runnable[] task = mockScheduleAtFixedRate(500);
        int id = timeoutHandler.runAtFixedRate(function, null, 500);

        assertEquals(0, value[0]);
        assertTrue(timeoutHandler.exists(id));

        timeoutHandler.remove(id);
        assertFalse(timeoutHandler.exists(id));
        assertNull(task[0]);

        assertEquals(0, value[0]);
        assertFalse(timeoutHandler.exists(id));
    }

    private Runnable[] mockSchedule(long delay) {
        Runnable[] task = new Runnable[] {null};
        doAnswer(invocationOnMock -> {
            task[0] = invocationOnMock.getArgument(0);

            ScheduledFuture<?> mock = mock(ScheduledFuture.class);
            when(mock.cancel(anyBoolean())).then(invocationOnMock1 -> {
                task[0] = null;
                return true;
            });
            return mock;
        }).when(scheduledExecutor).schedule(any(Runnable.class), eq(delay), eq(TimeUnit.MILLISECONDS));

        return task;
    }

    private Runnable[] mockScheduleAtFixedRate(long delay) {
        Runnable[] task = new Runnable[] {null};
        doAnswer(invocationOnMock -> {
            task[0] = invocationOnMock.getArgument(0);

            ScheduledFuture<?> mock = mock(ScheduledFuture.class);
            when(mock.cancel(anyBoolean())).then(invocationOnMock1 -> {
                task[0] = null;
                return true;
            });
            return mock;
        }).when(scheduledExecutor).scheduleAtFixedRate(any(Runnable.class), eq(delay), eq(delay), eq(TimeUnit.MILLISECONDS));

        return task;
    }
}
