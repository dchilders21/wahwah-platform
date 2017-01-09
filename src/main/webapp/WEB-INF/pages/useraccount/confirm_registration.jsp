<%--
  Created by IntelliJ IDEA.
  User: Justin
  Date: 3/17/14
  Time: 10:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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

            <h2>Registration Request Confirmed</h2>
            <fieldset class="no-form">
                <div class="instructions">
                    <p>Thank you for your interest in Red Panda Platform. Please look for an email to activate your account, at which point you will set up your initial password and demand sources.</p>
                </div>
            </fieldset>


        </div>
    </section>



    <footer class="footer">
        <div id="footer-logo"><img src="${pageContext.request.contextPath}/content/static/img/redPanda.png" width="90px" height="90px"/></div>
        <ul class="nav">
            <li><a href="http://www.redpandaplatform.com" target="_blank">About Red Panda Platform</a></li>
            <li><a href="mailto:support@redpandaplatform.com" target="_blank">Support</a></li>
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

