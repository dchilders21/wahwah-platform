package com.wahwahnetworks.platform.lib.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Created by jhaygood on 2/24/15.
 */
public class PlatformScriptTag extends SimpleTagSupport
{
    private String src;

    public void setSrc(String jsSource){
        this.src = jsSource;
    }

    public String getSrc(){
        return src;
    }

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();

        HttpServletRequest httpServletRequest = (HttpServletRequest)pageContext.getRequest();

        String baseUrl = httpServletRequest.getContextPath();
        String commitId = (String)httpServletRequest.getAttribute("commitId");

        String scriptUrl = baseUrl + "/" + src + "?rev=" + commitId;

        String serverName = httpServletRequest.getServerName();

        if(serverName.equals("staging.dev.wahwahnetworks.com")){
            scriptUrl = "http://platform-assets-staging.wwnstatic.com/" + commitId + "/" + src;
        }

        if(serverName.equals("app.redpandaplatform.com")){
            scriptUrl = "http://platform-assets.wwnstatic.com/" + commitId + "/" + src;
        }

        JspWriter out = pageContext.getOut();
        out.print("<script type=\"text/javascript\" src=\"" + scriptUrl + "\"></script>");
    }
}
