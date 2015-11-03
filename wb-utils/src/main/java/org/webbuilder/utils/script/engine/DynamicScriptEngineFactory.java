package org.webbuilder.utils.script.engine;

import org.webbuilder.utils.script.engine.SpEL.SpElEngine;
import org.webbuilder.utils.script.engine.groovy.GroovyEngine;
import org.webbuilder.utils.script.engine.js.JavaScriptEngine;
import org.webbuilder.utils.script.engine.ognl.OgnlEngine;
import org.webbuilder.utils.script.engine.python.PythonScriptEngine;
import org.webbuilder.utils.script.engine.ruby.RubyScriptEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-27 0027.
 */
public final class DynamicScriptEngineFactory {
    private static final Map<String, DynamicScriptEngine> map = new HashMap<>();

    static {
        map.put("js", new JavaScriptEngine());
        map.put("groovy", new GroovyEngine());
        map.put("ruby", new RubyScriptEngine());
        map.put("python", new PythonScriptEngine());
        map.put("spel", new SpElEngine());
        map.put("ognl", new OgnlEngine());
    }

    public static final DynamicScriptEngine getEngine(String type) {
        return map.get(type);
    }

}
