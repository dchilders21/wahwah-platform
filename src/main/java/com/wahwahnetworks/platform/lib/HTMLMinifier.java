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
 * Created by Justin on 6/11/2014.
 */

@Component
public class HTMLMinifier {

    private ScriptEngine scriptEngine;

    @Autowired
    public HTMLMinifier(ApplicationContext applicationContext) throws IOException, ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");

        Resource uglifyJSResource = applicationContext.getResource("classpath:uglifyjs/uglifyjs.js");
        InputStreamReader uglifyStreamReader = new InputStreamReader(uglifyJSResource.getInputStream());
        scriptEngine.eval(uglifyStreamReader);
        uglifyStreamReader.close();

        Resource htmlMinifierResource = applicationContext.getResource("classpath:htmlminifier/htmlminifier.min.js");
        InputStreamReader reader = new InputStreamReader(htmlMinifierResource.getInputStream());
        scriptEngine.eval(reader);
        reader.close();
    }

    public String minify(String code) throws ScriptException {
        scriptEngine.put("sourceCode",code);

        HTMLMinificationOptions minificationOptions = new HTMLMinificationOptions();
        scriptEngine.put("options",minificationOptions);

        StringBuilder jsString = new StringBuilder();
        jsString.append("result = minify(sourceCode,options)");

        scriptEngine.eval(jsString.toString());

        String minifiedCode = (String)scriptEngine.get("result");

        return minifiedCode;
    }

    public class HTMLMinificationOptions {
        public boolean removeIgnores = false;
        public boolean removeComments = true;
        public boolean removeCommentsFromCDATA = true;
        public boolean removeCDATASectionsFromCDATA= true;
        public boolean collapseWhitespace = true;
        public boolean conservativeCollapse = false;
        public boolean collapseBooleanAttributes = true;
        public boolean removeAttributeQuotes = false;
        public boolean removeRedundantAttributes = true;
        public boolean useShortDoctype = true;
        public boolean removeEmptyAttributes = true;
        public boolean removeEmptyElements = false;
        public boolean removeOptionalTags = false;
        public boolean removeScriptTypeAttributes = false;
        public boolean removeStyleLinkTypeAttributes = false;
        public boolean caseSensitive = false;
        public boolean minifyJS = true;
        public boolean minifyCSS = false;
        public boolean lint = false;

        public boolean canCollapseWhitespace(String string, Object object){
            return true;
        }

        public boolean canTrimWhitespace(){
            return true;
        }

    }
}
