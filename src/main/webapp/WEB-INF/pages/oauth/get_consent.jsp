
<%--
  Created by IntelliJ IDEA.
  User: Justin
  Date: 3/17/14
  Time: 10:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://platform.wahwahnetworks.com/jsp/tags/" prefix = "wahwah"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, minimum-scale=1.0">
    <script type="text/javascript" src="//use.typekit.net/zhw6pvj.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <title>Red Panda Platform</title>

    <wahwah:link rel="stylesheet" href="content/static/css/static.css"></wahwah:link>
    <wahwah:link rel="stylesheet" href="content/static/css/bootstrap-dropdown.css"></wahwah:link>

    <!-- TypeKit Fonts for Proxima Nova -->
    <script src="//use.typekit.net/ukx4mpx.js"></script>
    <script>try{Typekit.load();}catch(e){}</script>

    <!-- Google Fonts for Roboto -->
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700' rel='stylesheet' type='text/css'>

    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-41853403-1']);
        _gaq.push(['_trackPageview']);

        (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>

    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
    <wahwah:script src="js/static/js/lib/bootstrap.min.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/bootstrap-dropdown.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/bootstrap-tab.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/jquery.bxslider.min.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/jquery.easing.1.3.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/jquery.fitvids.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/jquery.quicksilver.js"></wahwah:script>
    <wahwah:script src="js/static/js/lib/jquery.simpleFAQ.js"></wahwah:script>
    <wahwah:script src="js/static/js/application.js"></wahwah:script>
</head>
<body class="authentication">
<div class="container auth">
    <header class="home-header">
        <div class="inner-wrapper">
            <div class="branding">
                <h1 class="logo"><a href="${pageContext.request.contextPath}/">wah<span>wah</span> platform</a></h1>
            </div>
        </div>
    </header>



    <section class="auth-form">
        <div class="inner-wrapper">

            <h2>Authorize Application</h2>

            <form action="${pageContext.request.contextPath}/oauth/grant_access" method="post" id="_form" class="form-horizontal">

                <script type="text/javascript">
                    function denyAccess(){
                        document.getElementById('do-grant').value = false;
                        document.getElementById('_form').submit();
                    }

                    function grantAccess(){
                        document.getElementById('do-grant').value = true;
                        document.getElementById('_form').submit();
                    }

                </script>

                <input type="hidden" name="xsrfToken" value="${xsrfToken}"/>
                <input type="hidden" name="clientId" value="${clientId}"/>
                <input type="hidden" name="redirectUri" value="${redirectUri}"/>
                <input type="hidden" name="responseType" value="${responseType}"/>
                <input type="hidden" name="scope" value="${scope}"/>
                <input type="hidden" name="state" value="${state}"/>
                <input type="hidden" id="do-grant" name="doGrant" value="false">

                <fieldset>

                    <p><strong>${application.applicationName}</strong> wants to request access to the following information in your Red Panda Platform account:</p>

                    <ul>
                        <c:forEach var="scopeItem" items="${scopes}">
                            <c:if test="${scopeItem.equals(\"read\")}"><li>Read Your Profile Information</li></c:if>
                            <c:if test="${ScopeItem.equals(\"offline\")}"><li>Access Your Profile Offline</li></c:if>
                        </c:forEach>
                    </ul>

                    <div class="form-actions">
                        <div class="form-action-okcancel">
                            <button type="button" class="btn btn-danger" onclick="denyAccess()">Deny</button>
                            <button type="button" class="btn btn-primary" onclick="grantAccess()">Authorize</button>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
    </section>


    <footer class="footer">
        <div id="footer-logo"><img src="${pageContext.request.contextPath}/content/static/img/Wahwah_Logo.png" width="96px" height="90px"/></div>
        <!--<div class="inner-wrapper"> -->
        <ul class="nav">
            <li><a href="http://www.wahwahnetworks.com" target="_blank">About Red Panda Platform</a></li>
            <li><a href="http://www.wahwahnetworks.com/terms-and-conditions.html" target="_blank">Terms &amp; Conditions</a></li>
            <li><a href="http://www.wahwahnetworks.com/privacy-policy.html" target="_blank">Privacy Policy</a></li>
            <li><a href="http://www.wahwahnetworks.com/faq.html" target="_blank">FAQ</a></li>
            <li><a href="http://www.wahwahnetworks.com/support.html" target="_blank">Support</a></li>


        </ul>
        <!-- </div> -->
    </footer>
</div> <!-- end .container -->





<script text="type/javascript">
    // Responsive Carousel
    $('#use-cases').bxSlider({
        slideWidth: 540,
        minSlides: 2,
        maxSlides: 2,
        slideMargin: 20
    });

    $('.faqs').simpleFAQ();
</script>

</body>
</html>