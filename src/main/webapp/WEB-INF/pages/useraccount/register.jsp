<%--
  Created by IntelliJ IDEA.
  User: Justin
  Date: 3/17/14
  Time: 10:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, minimum-scale=1.0">
    <script type="text/javascript" src="//use.typekit.net/zhw6pvj.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <title>Red Panda Platform</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/content/static/css/static.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/content/static/css/bootstrap-dropdown.css">

    <!-- TypeKit Fonts for Proxima Nova -->
    <script src="//use.typekit.net/ukx4mpx.js"></script>
    <script>try{Typekit.load();}catch(e){}</script>

    <!-- Google Fonts for Roboto -->
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700' rel='stylesheet' type='text/css'>

    <link rel="shortcut icon" href="${pageContext.request.contextPath}/content/static/img/favicon.png">

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
</head>
</head>
<body class="authentication">
<div class="container auth">
    <header class="home-header">
        <div class="inner-wrapper">
            <div class="branding">
                <h1 class="logo"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/content/static/img/redPanda_Copy.png" alt="Red Panda Platform"/></a></h1>
            </div>
        </div>
    </header>



    <section class="auth-form">

        <div class="inner-wrapper">

            <h2>Register</h2>


            <form action="" method="post" id="_form" class="form-horizontal">

                <fieldset>

                    <c:if test="${hasErrorMessage}">
                        <p class="error_message">${errorMessage}</p>
                    </c:if>

                    <p>Thanks for your interest in <b>Red Panda Platform</b>!  To start today with <b>free aggregated</b> reporting, please register below with information on your site or network of sites.</p>

                    <!-- <div class="instructions">
                        <p>All fields are required.</p>
                    </div> -->

                    <div class="control-group required">

                        <div class="controls">
                            <input type="text" placeholder="First Name" name="firstName" id="id_first_name" />
                        </div>
                    </div>

                    <div class="control-group required">

                        <div class="controls">
                            <input type="text" placeholder="Last Name" name="lastName" id="id_last_name" />

                        </div>
                    </div>

                    <div class="control-group required">

                        <div class="controls">
                            <input type="email" placeholder="Email Address" name="emailAddress" id="id_email" />
                        </div>
                    </div>

                    <div class="control-group required">

                        <div class="controls">
                            <input type="text" placeholder="Site or Network Name" name="siteName" id="id_sitename" />
                        </div>
                    </div>

                    <div class="control-group required">
                        <div class="controls">
                            <input type="text" placeholder="Site URL" name="siteURL" id="id_siteurl" />
                        </div>
                    </div>

                    <div class="control-group required">

                        <div class="controls">
                            <select class="form-control" placeholder="Traffic Estimate" name="trafficEstimate" id="id_traffic">
                                <option value="" disabled selected>Traffic Estimate (US Desktop Monthly Page Views)</option>
                                <option value="0" label="0 - 1MM">0-1MM</option>
                                <option value="1" label="1MM - 5MM">1MM-5MM</option>
                                <option value="2" label="5MM - 10MM">5MM-10MM</option>
                                <option value="3" label="10MM - 20MM">10MM - 20MM</option>
                                <option value="4" label="20MM +">20 Million +</option>
                            </select>
                        </div>
                    </div>

                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="acceptTerms"/> I agree with the <a href="http://www.redpandaplatform.com/tos/" target="_blank">Terms of Service</a>
                        </label>
                    </div>

                    <div class="form-actions">
                        <div id="center-submit"><input class=" btn btn-primary" type="submit" value="Register" /></div>
                    </div>
                </fieldset>
            </form>


        </div>

        <footer class="footer">
            <div id="footer-logo"><img src="${pageContext.request.contextPath}/content/static/img/redPanda.png" width="90px" height="90px"/></div>
            <ul class="nav">
                <li><a href="http://www.redpandaplatform.com" target="_blank">About Red Panda Platform</a></li>
                <li><a href="mailto:support@redpandaplatform.com" target="_blank">Support</a></li>
                <li><a href="http://www.redpandaplatform.com/tos" target="_blank">Terms of Service</a></li>
                <li><a href="http://www.redpandaplatform.com/privacy-policy/" target="_blank">Privacy Policy</a></li>
            </ul>
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

