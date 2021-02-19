package bionic.js;

import jjbridge.api.JSEngine;
import jjbridge.api.inspector.JSInspector;
import jjbridge.api.runtime.JSRuntime;

/**
 * This class represents a Bjs project bundle.
 * <p>A subclass of this type, must conform to the following constrains:</p>
 * <ul>
 *     <li>Its name must begin with "{@code Bjs}";</li>
 *     <li>Have a static method annotated with {@link BjsProjectTypeInfo.BjsProjectInitializer};</li>
 *     <li>That method must call {@link #initProject()} as first instruction.</li>
 * </ul>
 * */
public class BjsProject
{
    private static JSEngine jsEngine;
    private static int inspectorPort = -1;

    /**
     * Sets the JavaScript engine instance that will be used to run JavaScript code.
     *
     * @param engine The JavaScript engine to be used by Bjs
     * */
    public static void setJsEngine(JSEngine engine)
    {
        jsEngine = engine;
    }

    /**
     * Sets the IP port that will be used to connect to the JavaScript inspector.
     *
     * <p>This method must be called right after {@link BjsProject#setJsEngine(JSEngine)} to ensure inspector will be
     * initialized correctly.</p>
     * <p>If you do not want the inspector to be available (e.g. when running in production), just never call this
     * method or pass a negative (i.e. invalid) port number.</p>
     *
     * @param port The IP port used by the JavaScript inspector
     * */
    public static void enableInspector(int port)
    {
        inspectorPort = port >= 0 ? port : -1;
    }

    protected static void initProject()
    {
        if (jsEngine == null)
        {
            throw new RuntimeException("Global jsEngine for BjsProject is null.\n"
                    + "Please call BjsProject.setJsEngine() to set the engine.");
        }
        JSRuntime runtime = jsEngine.newRuntime();
        if (inspectorPort != -1)
        {
            JSInspector inspector = jsEngine.newInspector(inspectorPort);
            inspector.attach(runtime);
        }
        Bjs.setDefaultRuntime(runtime);
    }
}
