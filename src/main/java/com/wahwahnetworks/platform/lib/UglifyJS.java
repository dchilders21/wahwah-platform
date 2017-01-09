package com.wahwahnetworks.platform.lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Justin on 6/10/2014.
 */

@Component
public class UglifyJS {

    private ScriptEngine scriptEngine;


    @Autowired
    public UglifyJS(ApplicationContext applicationContext) throws IOException, ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");

        Resource resource = applicationContext.getResource("classpath:uglifyjs/uglifyjs.js");

        InputStreamReader reader = new InputStreamReader(resource.getInputStream());
        scriptEngine.eval(reader);
        reader.close();
    }

    public String compress(String code) throws ScriptException {

        scriptEngine.put("sourceCode",code);

        StringBuilder jsString = new StringBuilder();
        jsString.append("ast = UglifyJS.parse(sourceCode);");
        jsString.append("ast.figure_out_scope();");
        jsString.append("compressor = UglifyJS.Compressor();");
        jsString.append("ast.figure_out_scope();");
        jsString.append("ast.compute_char_frequency();");
        jsString.append("ast.mangle_names();");
        jsString.append("result = ast.print_to_string();");
        jsString.append("result = result.toString()");

        scriptEngine.eval(jsString.toString());

        String minifiedCode = (String)scriptEngine.get("result");

        return minifiedCode;
    }
}
