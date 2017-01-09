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
public class PlatformLinkTag extends SimpleTagSupport {

    private String rel;
    private String href;

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();

        HttpServletRequest httpServletRequest = (HttpServletRequest)pageContext.getRequest();

        String baseUrl = httpServletRequest.getContextPath();
        String commitId = (String)httpServletRequest.getAttribute("commitId");

        String resolvedHref = baseUrl + "/" + href + "?rev=" + commitId;

        String serverName = httpServletRequest.getServerName();

        if(serverName.equals("staging.dev.wahwahnetworks.com")){
            resolvedHref = "http://platform-assets-staging.wwnstatic.com/" + commitId + "/" + href;
        }

        if(serverName.equals("app.redpandaplatform.com")){
            resolvedHref = "http://platform-assets.wwnstatic.com/" + commitId + "/" + href;
        }

        JspWriter out = pageContext.getOut();
        out.print("<link rel=\"" + rel + "\" href=\"" + resolvedHref + "\">");
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
