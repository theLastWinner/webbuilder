package org.webbuilder.sql.trigger;

import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public class ScriptTriggerSupport implements Trigger, Serializable {

    private String name;

    private String id;

    private String language = "js";

    private String content;

    private DynamicScriptEngine engine;

    public ScriptTriggerSupport() {
    }

    public ScriptTriggerSupport(String id, String name, String language, String content) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.content = content;
    }

    public ScriptTriggerSupport(String name, String language, String content) {
        this.name = name;
        this.language = language;
        this.content = content;
    }

    @Override
    public TriggerResult execute(Map<String, Object> root) throws TriggerException {
        ExecuteResult result = engine.execute(getId(), root);
        TriggerResult triggerResult = new TriggerResult();
        if (result.isSuccess()) {
            Object res = result.getResult();
            if (res != null) {
                if (res instanceof Boolean) {
                    triggerResult.setSuccess(((Boolean) res));
                } else if (res instanceof String) {
                    triggerResult.setSuccess(false);
                    triggerResult.setMessage(res.toString());
                } else if (res instanceof Map) {
                    Map<String, Object> res_map = ((Map) res);
                    triggerResult.setSuccess("true".equals(String.valueOf(res_map.get("success"))));
                    triggerResult.setMessage(String.valueOf(res_map.get("message")));
                    triggerResult.setData(res_map.get("data"));
                } else if (res instanceof TriggerResult) {
                    triggerResult = ((TriggerResult) res);
                } else {
                    triggerResult.setSuccess(false);
                }
            }
        } else {
            triggerResult.setSuccess(false);
            triggerResult.setData(result.getResult());
            triggerResult.setMessage(result.getMessage());
        }
        return triggerResult;
    }

    @Override
    public void init() throws TriggerException {
        engine = DynamicScriptEngineFactory.getEngine(getLanguage());
        if (engine == null) {
            throw new TriggerException(String.format("init trigger error ,cause by language %s not support", getLanguage()));
        }
        try {
            engine.compile(getId(), content);
        } catch (Exception e) {
            throw new TriggerException(String.format("init trigger error ,cause by %s", e.getMessage()), e);
        }
    }

    public String getId() {
        if (id == null)
            id = MD5.encode(String.valueOf(System.nanoTime()));
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }

}
