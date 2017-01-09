<%--
  Created by IntelliJ  Justin
  Date: 6/7/2014
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://platform.wahwahnetworks.com/jsp/tags/" prefix = "wahwah"%>
<!DOCTYPE html>
<html lang="en" ng-app="wahwahApp">

<head>

    <title>Red Panda Platform</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="author" href="Red Panda Platform"/>

    <script type="text/javascript">
        window.NREUM||(NREUM={}),__nr_require=function(t,e,n){function r(n){if(!e[n]){var o=e[n]={exports:{}};t[n][0].call(o.exports,function(e){var o=t[n][1][e];return r(o?o:e)},o,o.exports)}return e[n].exports}if("function"==typeof __nr_require)return __nr_require;for(var o=0;o<n.length;o++)r(n[o]);return r}({QJf3ax:[function(t,e){function n(t){function e(e,n,a){t&&t(e,n,a),a||(a={});for(var c=s(e),f=c.length,u=i(a,o,r),d=0;f>d;d++)c[d].apply(u,n);return u}function a(t,e){f[t]=s(t).concat(e)}function s(t){return f[t]||[]}function c(){return n(e)}var f={};return{on:a,emit:e,create:c,listeners:s,_events:f}}function r(){return{}}var o="nr@context",i=t("gos");e.exports=n()},{gos:"7eSDFh"}],ee:[function(t,e){e.exports=t("QJf3ax")},{}],3:[function(t){function e(t){try{i.console&&console.log(t)}catch(e){}}var n,r=t("ee"),o=t(1),i={};try{n=localStorage.getItem("__nr_flags").split(","),console&&"function"==typeof console.log&&(i.console=!0,-1!==n.indexOf("dev")&&(i.dev=!0),-1!==n.indexOf("nr_dev")&&(i.nrDev=!0))}catch(a){}i.nrDev&&r.on("internal-error",function(t){e(t.stack)}),i.dev&&r.on("fn-err",function(t,n,r){e(r.stack)}),i.dev&&(e("NR AGENT IN DEVELOPMENT MODE"),e("flags: "+o(i,function(t){return t}).join(", ")))},{1:22,ee:"QJf3ax"}],4:[function(t){function e(t,e,n,i,s){try{c?c-=1:r("err",[s||new UncaughtException(t,e,n)])}catch(f){try{r("ierr",[f,(new Date).getTime(),!0])}catch(u){}}return"function"==typeof a?a.apply(this,o(arguments)):!1}function UncaughtException(t,e,n){this.message=t||"Uncaught error with no additional information",this.sourceURL=e,this.line=n}function n(t){r("err",[t,(new Date).getTime()])}var r=t("handle"),o=t(6),i=t("ee"),a=window.onerror,s=!1,c=0;t("loader").features.err=!0,t(5),window.onerror=e;try{throw new Error}catch(f){"stack"in f&&(t(1),t(2),"addEventListener"in window&&t(3),window.XMLHttpRequest&&XMLHttpRequest.prototype&&XMLHttpRequest.prototype.addEventListener&&window.XMLHttpRequest&&XMLHttpRequest.prototype&&XMLHttpRequest.prototype.addEventListener&&!/CriOS/.test(navigator.userAgent)&&t(4),s=!0)}i.on("fn-start",function(){s&&(c+=1)}),i.on("fn-err",function(t,e,r){s&&(this.thrown=!0,n(r))}),i.on("fn-end",function(){s&&!this.thrown&&c>0&&(c-=1)}),i.on("internal-error",function(t){r("ierr",[t,(new Date).getTime(),!0])})},{1:9,2:8,3:6,4:10,5:3,6:23,ee:"QJf3ax",handle:"D5DuLP",loader:"G9z0Bl"}],5:[function(t){function e(){}if(window.performance&&window.performance.timing&&window.performance.getEntriesByType){var n=t("ee"),r=t("handle"),o=t(1),i=t(2);t("loader").features.stn=!0,t(3),n.on("fn-start",function(t){var e=t[0];e instanceof Event&&(this.bstStart=Date.now())}),n.on("fn-end",function(t,e){var n=t[0];n instanceof Event&&r("bst",[n,e,this.bstStart,Date.now()])}),o.on("fn-start",function(t,e,n){this.bstStart=Date.now(),this.bstType=n}),o.on("fn-end",function(t,e){r("bstTimer",[e,this.bstStart,Date.now(),this.bstType])}),i.on("fn-start",function(){this.bstStart=Date.now()}),i.on("fn-end",function(t,e){r("bstTimer",[e,this.bstStart,Date.now(),"requestAnimationFrame"])}),n.on("pushState-start",function(){this.time=Date.now(),this.startPath=location.pathname+location.hash}),n.on("pushState-end",function(){r("bstHist",[location.pathname+location.hash,this.startPath,this.time])}),"addEventListener"in window.performance&&(window.performance.addEventListener("webkitresourcetimingbufferfull",function(){r("bstResource",[window.performance.getEntriesByType("resource")]),window.performance.webkitClearResourceTimings()},!1),window.performance.addEventListener("resourcetimingbufferfull",function(){r("bstResource",[window.performance.getEntriesByType("resource")]),window.performance.clearResourceTimings()},!1)),document.addEventListener("scroll",e,!1),document.addEventListener("keypress",e,!1),document.addEventListener("click",e,!1)}},{1:9,2:8,3:7,ee:"QJf3ax",handle:"D5DuLP",loader:"G9z0Bl"}],6:[function(t,e){function n(t){i.inPlace(t,["addEventListener","removeEventListener"],"-",r)}function r(t){return t[1]}var o=t("ee").create(),i=t(1)(o),a=t("gos");if(e.exports=o,n(window),"getPrototypeOf"in Object){for(var s=document;s&&!s.hasOwnProperty("addEventListener");)s=Object.getPrototypeOf(s);s&&n(s);for(var c=XMLHttpRequest.prototype;c&&!c.hasOwnProperty("addEventListener");)c=Object.getPrototypeOf(c);c&&n(c)}else XMLHttpRequest.prototype.hasOwnProperty("addEventListener")&&n(XMLHttpRequest.prototype);o.on("addEventListener-start",function(t){if(t[1]){var e=t[1];"function"==typeof e?this.wrapped=t[1]=a(e,"nr@wrapped",function(){return i(e,"fn-",null,e.name||"anonymous")}):"function"==typeof e.handleEvent&&i.inPlace(e,["handleEvent"],"fn-")}}),o.on("removeEventListener-start",function(t){var e=this.wrapped;e&&(t[1]=e)})},{1:24,ee:"QJf3ax",gos:"7eSDFh"}],7:[function(t,e){var n=t("ee").create(),r=t(1)(n);e.exports=n,r.inPlace(window.history,["pushState"],"-")},{1:24,ee:"QJf3ax"}],8:[function(t,e){var n=t("ee").create(),r=t(1)(n);e.exports=n,r.inPlace(window,["requestAnimationFrame","mozRequestAnimationFrame","webkitRequestAnimationFrame","msRequestAnimationFrame"],"raf-"),n.on("raf-start",function(t){t[0]=r(t[0],"fn-")})},{1:24,ee:"QJf3ax"}],9:[function(t,e){function n(t,e,n){t[0]=o(t[0],"fn-",null,n)}var r=t("ee").create(),o=t(1)(r);e.exports=r,o.inPlace(window,["setTimeout","setInterval","setImmediate"],"setTimer-"),r.on("setTimer-start",n)},{1:24,ee:"QJf3ax"}],10:[function(t,e){function n(){f.inPlace(this,p,"fn-")}function r(t,e){f.inPlace(e,["onreadystatechange"],"fn-")}function o(t,e){return e}function i(t,e){for(var n in t)e[n]=t[n];return e}var a=t("ee").create(),s=t(1),c=t(2),f=c(a),u=c(s),d=window.XMLHttpRequest,p=["onload","onerror","onabort","onloadstart","onloadend","onprogress","ontimeout"];e.exports=a,window.XMLHttpRequest=function(t){var e=new d(t);try{a.emit("new-xhr",[],e),u.inPlace(e,["addEventListener","removeEventListener"],"-",o),e.addEventListener("readystatechange",n,!1)}catch(r){try{a.emit("internal-error",[r])}catch(i){}}return e},i(d,XMLHttpRequest),XMLHttpRequest.prototype=d.prototype,f.inPlace(XMLHttpRequest.prototype,["open","send"],"-xhr-",o),a.on("send-xhr-start",r),a.on("open-xhr-start",r)},{1:6,2:24,ee:"QJf3ax"}],11:[function(t){function e(t){var e=this.params,r=this.metrics;if(!this.ended){this.ended=!0;for(var i=0;c>i;i++)t.removeEventListener(s[i],this.listener,!1);if(!e.aborted){if(r.duration=(new Date).getTime()-this.startTime,4===t.readyState){e.status=t.status;var a=t.responseType,f="arraybuffer"===a||"blob"===a||"json"===a?t.response:t.responseText,u=n(f);if(u&&(r.rxSize=u),this.sameOrigin){var d=t.getResponseHeader("X-NewRelic-App-Data");d&&(e.cat=d.split(", ").pop())}}else e.status=0;r.cbTime=this.cbTime,o("xhr",[e,r,this.startTime])}}}function n(t){if("string"==typeof t&&t.length)return t.length;if("object"!=typeof t)return void 0;if("undefined"!=typeof ArrayBuffer&&t instanceof ArrayBuffer&&t.byteLength)return t.byteLength;if("undefined"!=typeof Blob&&t instanceof Blob&&t.size)return t.size;if("undefined"!=typeof FormData&&t instanceof FormData)return void 0;try{return JSON.stringify(t).length}catch(e){return void 0}}function r(t,e){var n=i(e),r=t.params;r.host=n.hostname+":"+n.port,r.pathname=n.pathname,t.sameOrigin=n.sameOrigin}if(window.XMLHttpRequest&&XMLHttpRequest.prototype&&XMLHttpRequest.prototype.addEventListener&&!/CriOS/.test(navigator.userAgent)){t("loader").features.xhr=!0;var o=t("handle"),i=t(2),a=t("ee"),s=["load","error","abort","timeout"],c=s.length,f=t(1);t(4),t(3),a.on("new-xhr",function(){this.totalCbs=0,this.called=0,this.cbTime=0,this.end=e,this.ended=!1,this.xhrGuids={}}),a.on("open-xhr-start",function(t){this.params={method:t[0]},r(this,t[1]),this.metrics={}}),a.on("open-xhr-end",function(t,e){"loader_config"in NREUM&&"xpid"in NREUM.loader_config&&this.sameOrigin&&e.setRequestHeader("X-NewRelic-ID",NREUM.loader_config.xpid)}),a.on("send-xhr-start",function(t,e){var r=this.metrics,o=t[0],i=this;if(r&&o){var f=n(o);f&&(r.txSize=f)}this.startTime=(new Date).getTime(),this.listener=function(t){try{"abort"===t.type&&(i.params.aborted=!0),("load"!==t.type||i.called===i.totalCbs&&(i.onloadCalled||"function"!=typeof e.onload))&&i.end(e)}catch(n){try{a.emit("internal-error",[n])}catch(r){}}};for(var u=0;c>u;u++)e.addEventListener(s[u],this.listener,!1)}),a.on("xhr-cb-time",function(t,e,n){this.cbTime+=t,e?this.onloadCalled=!0:this.called+=1,this.called!==this.totalCbs||!this.onloadCalled&&"function"==typeof n.onload||this.end(n)}),a.on("xhr-load-added",function(t,e){var n=""+f(t)+!!e;this.xhrGuids&&!this.xhrGuids[n]&&(this.xhrGuids[n]=!0,this.totalCbs+=1)}),a.on("xhr-load-removed",function(t,e){var n=""+f(t)+!!e;this.xhrGuids&&this.xhrGuids[n]&&(delete this.xhrGuids[n],this.totalCbs-=1)}),a.on("addEventListener-end",function(t,e){e instanceof XMLHttpRequest&&"load"===t[0]&&a.emit("xhr-load-added",[t[1],t[2]],e)}),a.on("removeEventListener-end",function(t,e){e instanceof XMLHttpRequest&&"load"===t[0]&&a.emit("xhr-load-removed",[t[1],t[2]],e)}),a.on("fn-start",function(t,e,n){e instanceof XMLHttpRequest&&("onload"===n&&(this.onload=!0),("load"===(t[0]&&t[0].type)||this.onload)&&(this.xhrCbStart=(new Date).getTime()))}),a.on("fn-end",function(t,e){this.xhrCbStart&&a.emit("xhr-cb-time",[(new Date).getTime()-this.xhrCbStart,this.onload,e],e)})}},{1:"XL7HBI",2:12,3:10,4:6,ee:"QJf3ax",handle:"D5DuLP",loader:"G9z0Bl"}],12:[function(t,e){e.exports=function(t){var e=document.createElement("a"),n=window.location,r={};e.href=t,r.port=e.port;var o=e.href.split("://");return!r.port&&o[1]&&(r.port=o[1].split("/")[0].split("@").pop().split(":")[1]),r.port&&"0"!==r.port||(r.port="https"===o[0]?"443":"80"),r.hostname=e.hostname||n.hostname,r.pathname=e.pathname,r.protocol=o[0],"/"!==r.pathname.charAt(0)&&(r.pathname="/"+r.pathname),r.sameOrigin=!e.hostname||e.hostname===document.domain&&e.port===n.port&&e.protocol===n.protocol,r}},{}],13:[function(t,e){function n(t){return function(){r(t,[(new Date).getTime()].concat(i(arguments)))}}var r=t("handle"),o=t(1),i=t(2);"undefined"==typeof window.newrelic&&(newrelic=window.NREUM);var a=["setPageViewName","addPageAction","setCustomAttribute","finished","addToTrace","inlineHit","noticeError"];o(a,function(t,e){window.NREUM[e]=n("api-"+e)}),e.exports=window.NREUM},{1:22,2:23,handle:"D5DuLP"}],gos:[function(t,e){e.exports=t("7eSDFh")},{}],"7eSDFh":[function(t,e){function n(t,e,n){if(r.call(t,e))return t[e];var o=n();if(Object.defineProperty&&Object.keys)try{return Object.defineProperty(t,e,{value:o,writable:!0,enumerable:!1}),o}catch(i){}return t[e]=o,o}var r=Object.prototype.hasOwnProperty;e.exports=n},{}],D5DuLP:[function(t,e){function n(t,e,n){return r.listeners(t).length?r.emit(t,e,n):void(r.q&&(r.q[t]||(r.q[t]=[]),r.q[t].push(e)))}var r=t("ee").create();e.exports=n,n.ee=r,r.q={}},{ee:"QJf3ax"}],handle:[function(t,e){e.exports=t("D5DuLP")},{}],XL7HBI:[function(t,e){function n(t){var e=typeof t;return!t||"object"!==e&&"function"!==e?-1:t===window?0:i(t,o,function(){return r++})}var r=1,o="nr@id",i=t("gos");e.exports=n},{gos:"7eSDFh"}],id:[function(t,e){e.exports=t("XL7HBI")},{}],G9z0Bl:[function(t,e){function n(){var t=p.info=NREUM.info,e=f.getElementsByTagName("script")[0];if(t&&t.licenseKey&&t.applicationID&&e){s(d,function(e,n){e in t||(t[e]=n)});var n="https"===u.split(":")[0]||t.sslForHttp;p.proto=n?"https://":"http://",a("mark",["onload",i()]);var r=f.createElement("script");r.src=p.proto+t.agent,e.parentNode.insertBefore(r,e)}}function r(){"complete"===f.readyState&&o()}function o(){a("mark",["domContent",i()])}function i(){return(new Date).getTime()}var a=t("handle"),s=t(1),c=window,f=c.document;t(2);var u=(""+location).split("?")[0],d={beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",agent:"js-agent.newrelic.com/nr-686.min.js"},p=e.exports={offset:i(),origin:u,features:{}};f.addEventListener?(f.addEventListener("DOMContentLoaded",o,!1),c.addEventListener("load",n,!1)):(f.attachEvent("onreadystatechange",r),c.attachEvent("onload",n)),a("mark",["firstbyte",i()])},{1:22,2:13,handle:"D5DuLP"}],loader:[function(t,e){e.exports=t("G9z0Bl")},{}],22:[function(t,e){function n(t,e){var n=[],o="",i=0;for(o in t)r.call(t,o)&&(n[i]=e(o,t[o]),i+=1);return n}var r=Object.prototype.hasOwnProperty;e.exports=n},{}],23:[function(t,e){function n(t,e,n){e||(e=0),"undefined"==typeof n&&(n=t?t.length:0);for(var r=-1,o=n-e||0,i=Array(0>o?0:o);++r<o;)i[r]=t[e+r];return i}e.exports=n},{}],24:[function(t,e){function n(t){return!(t&&"function"==typeof t&&t.apply&&!t[i])}var r=t("ee"),o=t(1),i="nr@wrapper",a=Object.prototype.hasOwnProperty;e.exports=function(t){function e(t,e,r,a){function nrWrapper(){var n,i,s,f;try{i=this,n=o(arguments),s=r&&r(n,i)||{}}catch(d){u([d,"",[n,i,a],s])}c(e+"start",[n,i,a],s);try{return f=t.apply(i,n)}catch(p){throw c(e+"err",[n,i,p],s),p}finally{c(e+"end",[n,i,f],s)}}return n(t)?t:(e||(e=""),nrWrapper[i]=!0,f(t,nrWrapper),nrWrapper)}function s(t,r,o,i){o||(o="");var a,s,c,f="-"===o.charAt(0);for(c=0;c<r.length;c++)s=r[c],a=t[s],n(a)||(t[s]=e(a,f?s+o:o,i,s))}function c(e,n,r){try{t.emit(e,n,r)}catch(o){u([o,e,n,r])}}function f(t,e){if(Object.defineProperty&&Object.keys)try{var n=Object.keys(t);return n.forEach(function(n){Object.defineProperty(e,n,{get:function(){return t[n]},set:function(e){return t[n]=e,e}})}),e}catch(r){u([r])}for(var o in t)a.call(t,o)&&(e[o]=t[o]);return e}function u(e){try{t.emit("internal-error",e)}catch(n){}}return t||(t=r),e.inPlace=s,e.flag=i,e}},{1:23,ee:"QJf3ax"}]},{},["G9z0Bl",4,11,5]);
        ;NREUM.info={beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"f095ad3ea5",applicationID:"9777748",sa:1,agent:"js-agent.newrelic.com/nr-686.min.js"}
    </script>

    <!-- Core CSS - Include with every page -->
    <!-- Bootstrap core CSS -->
    <wahwah:link href="content/libs/mint-theme/css/bootstrap.css" rel="stylesheet"></wahwah:link>
    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">

    <!-- Page-Level Plugin CSS - Platform -->
    <wahwah:link href="content/libs/mint-theme/css/plugins/timeline/timeline.css" rel="stylesheet"></wahwah:link>
    <wahwah:link href="content/libs/typeahead.js-bootstrap/typeahead.css" rel="stylesheet"></wahwah:link>
    <wahwah:link href="content/libs/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet" ></wahwah:link>
    <wahwah:link href="content/libs/bootstrap-tagsinput/dist/bootstrap-tagsinput.css" rel="stylesheet"></wahwah:link>

    <!-- Mint Admin CSS - Include with every page -->
    <wahwah:link href="content/libs/mint-theme/css/mint-admin.css" rel="stylesheet"></wahwah:link>
    <wahwah:link href="content/platform/platform.css" rel="stylesheet"></wahwah:link>

    <!-- TypeKit Fonts for Proxima Nova -->
    <script src="//use.typekit.net/ukx4mpx.js"></script>
    <script>try{Typekit.load();}catch(e){}</script>

    <!-- Google Fonts for Roboto -->
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700' rel='stylesheet' type='text/css'>

    <!-- rzslider -->
    <wahwah:link href="content/libs/rzslider/rzslider.min.css" rel="stylesheet"></wahwah:link>

    <link rel="shortcut icon" href="${pageContext.request.contextPath}/content/static/img/favicon.png">

    <script type="text/javascript">
        (function() {
            var path = '//easy.myfonts.net/v1/js?sid=148510(font-family=Proxima+Nova+Regular)&sid=148514(font-family=Proxima+Nova+Bold)&sid=148547(font-family=Proxima+Nova+Semibold)&sid=148550(font-family=Proxima+Nova+Light)&key=QIb90d7g4l',
                    protocol = ('https:' == document.location.protocol ? 'https:' : 'http:'),
                    trial = document.createElement('script');
            trial.type = 'text/javascript';
            trial.async = true;
            trial.src = protocol + path;
            var head = document.getElementsByTagName("head")[0];
            head.appendChild(trial);
        })();
    </script>

    <script type="text/javascript" src="https://wahwah.atlassian.net/s/14e25f8377395f6514a06a8eb804558e-T/en_US4iorpx/65001/9/1.4.25/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=en-US&collectorId=c2b69de0"></script>
</head>

<body data-contextpath="${pageContext.request.contextPath}" data-commitid="${commitId}" data-servername="${serverName}" data-defaultpath="${defaultPath}" data-defaultdashboard="${defaultDashboard}" data-version="${version}">

<div id="wrapper">

<nav class="navbar navbar-default navbar-static-top" role="navigation" ng-controller="NavbarController">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <!-- <a class="navbar-brand" href="#">wah<strong>wah</strong> networks</a> -->
        <div class="branding">
            <h1 class="logo"><a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/content/static/img/RP_logo.png" alt="Red Panda Platform"/></a></h1>
        </div>
    <!-- /.navbar-header -->
    </div>
    <div class="collapse navbar-collapse">
        <ul class="nav navbar-top-links navbar-right">
                <!-- search form -->
            <li class="navbar-left search-bar" ng-show="!showSearchResults" ng-if="hasRole('SUPER_USER')">

                <span style="font-weight: bold; font-size: large; color: rgb(225, 231, 238);" ng-if="environment!= null" ng-cloak><span ng-if="environment.environmentAlias=='dev'">Development</span><span ng-if="environment.environmentAlias=='qa'">Staging</span>&nbsp;&nbsp;&nbsp;</span>
                <input type="text" ng-model="$parent.searchData" class="form-control" placeholder="Search..." style="display: inline-block; width: 20em;" ng-keypress="searchKeypress($event)">
                <button class="btn btn-default search" type="button" ng-click="search()">
                    <i class="fa fa-search"></i>
                </button>
            </li>

            <!-- /.search form -->

            <!-- user menu -->


                <user-menu></user-menu>
            </li>
        </ul>

</div>
    <div ng-if="hasRole('SUPER_USER')" style="position: relative; left: 0px; top: 0px; height: 1px; max-height: 1px;">
        <div ng-cloak ng-show="showSearchResults" style="border: 1px solid darkgray; max-width: 80em; overflow-x: hidden; overflow-y: hidden; width: 20em; height: 20em; position: absolute; top: 0px; right: 1em; background-color: white;">
            <div>

                <button class="btn btn-close" type="button" ng-click="searchResultsClose()">
                    <i class="fa fa-close"></i>
                </button>

                Search Results

            </div>
            <div class="divider"></div>
                <div style="max-height: 40em; overflow-y: scroll; max-width: 80em; overflow-x: hidden; height: 20em; width: 20em;">

                    <table class="table table-striped">
                        <tbody>
                        <tr ng-if="searchResults.total_count == 0"><td>No results!</td></tr>
                        <tr ng-if="searchResults.networks.length > 0"><td>Networks</td></tr>
                        <tr ng-repeat="network in searchResults.networks"><td>
                            <a href="#/network-{{network.id}}/publishers/">{{network.name}}</a>
                        </td></tr>
                        <tr ng-if="searchResults.publishers.length > 0"><td>Publishers</td></tr>
                        <tr ng-repeat="publisher in searchResults.publishers"><td>
                            <a href="#/publisher-{{publisher.id}}/sites/">{{publisher.name}}</a>
                        </td></tr>
                        <tr ng-if="searchResults.sites.length > 0"><td>Sites</td></tr>
                        <tr ng-repeat="site in searchResults.sites"><td>
                            <a href="#/sites/{{site.id}}">{{site.site_name}}</a>
                        </td></tr>
                        <tr ng-if="searchResults.products.length > 0"><td>Tags</td></tr>
                        <tr ng-repeat="product in searchResults.products"><td>
                            <a href="#/tagsettings/{{product.site_id}}/by-tag/{{product.id}}">{{product.product_name}}</a>
                        </td></tr>
                        <tr><td style="background-color: darkgray">&nbsp<!-- spacer for easier scrolling--><br></td></tr>
                        </tbody>
                    </table>
                </div>
        </div>
    </div>

<!-- /.navbar-top-links -->

</nav>
<!-- /.navbar-static-top -->

<nav class="navbar-default navbar-static-side" role="navigation" ng-controller="SidebarController">
    <div class="sidebar-collapse">
        <ul class="nav" id="side-menu" ng-cloak>
            <li>
                <div class="user-info-wrapper">
                    <div class="user-info">
                        <div class="user-welcome">Welcome</div>
                        <div class="username">${user.firstName} ${user.lastName}</div>
                    </div>
                </div>
            </li>

            <li ng-class="{active: (page == 'Overview')}">
                <a href="#/overview"><i class="fa fa-dashboard fa-fw fa-3x"></i> Dashboard</a>
            </li>

            <li ng-if="hasRole('INTERNAL_USER')" ng-class="{active: (page == 'Networks')}">
                <a href="#/networks"><i class="fa fa-globe fa-fw fa-3x"></i> Networks</a>
            </li>

            <li ng-if="(hasRole('INTERNAL_USER') || (hasRole('NETWORK_ADMIN') && !user.singlePublisherNetworkUser)) && user.accountType != 'FREE'" ng-class="{active: (page == 'Publishers')}">
                <a href="#/publishers"><i class="fa fa-columns fa-fw fa-3x"></i> Publishers</a>
            </li>

            <li ng-class="{active: (page == 'Sites')}" ng-if="hasRole('INTERNAL_USER')">
                <a href="#/sites"><i class="fa fa-sitemap fa-fw fa-3x"></i> Sites</a>
            </li>


            <li ng-class="{active: (page == 'Sites')}" ng-if="hasRole('NETWORK_ADMIN') && user.singlePublisherNetworkUser">
                <a ng-href="#/network-{{user.accountId}}/publishers"><i class="fa fa-sitemap fa-fw fa-3x"></i> Sites</a>
            </li>

            <li ng-class="{active: (page == 'Custom-Reports')}" ng-if="hasRole('ANALYTICS')">
                <a href="#/custom-reports"><i class="fa fa-files-o fa-fw fa-3x"></i>Custom Reports</a>
            </li>

            <li ng-if="hasRole('PUBLISHER_ADMIN')" ng-class="{active: (page == 'Users')}">
                <a href="#/users"><i class="fa fa-users fa-fw fa-3x"></i> Users</a>
            </li>

            <li ng-class="{active: (page == 'Products')}" ng-if="hasRole('DEVELOPER')">
                <a href="#/products"><i class="fa fa-puzzle-piece fa-fw fa-3x"></i> Tags</a>
            </li>

            <li ng-class="{active: (page == 'Demand Sources')}" ng-if="hasRole('DEMAND_SOURCE_ADMIN')">
                <a href="#/demand-sources"><i class="fa fa-gavel fa-fw fa-3x"></i> Demand Sources</a>
            </li>

            <li ng-class="{active: markAdOpsMenu}" ng-if="hasRole('AD_OPS_ADMIN')">
                <a ng-click="toggleAdOpsMenu();" class="nav-toggle" ng-class="{active: showAdOpsMenu}">
                    <i class="fa fa-pencil fa-fw fa-3x"></i> Ad Ops
                    <span class="fa arrow"></span>
                </a>
                <ul class="nav nav-second-level" ng-class="{collapse: !showAdOpsMenu, in: showAdOpsMenu}">
                    <li ng-if="hasRole('INTERNAL_USER')"><a href="http://ox-ui.wahwahnetworks.com" target="_blank">Ad Server - OpenX</a></li>
                    <li ng-if="hasRole('INTERNAL_USER')"><a href="http://platform4.liverail.com" target="_blank">Ad Server - LiveRail</a></li>
                    <li ng-class="{active: (page == 'Unmapped Creatives')}"><a href="#/adops/unmapped-creatives">Unmapped Creatives</a></li>
                    <li ng-class="{active: (page == 'Unknown Ad Units')}"><a href="#/adops/unknown-ad-units">Unknown Ad Units</a></li>
                    <li ng-class="{active: (page == 'Line Item Mapping')}"><a href="#/adops/line-item-mapping">Line Item Mapping</a></li>
                    <li ng-class="{active: (page == 'Monthly Reconciliation')}"><a href="#/adops/monthly-reconciliation">Monthly Reconciliation</a></li>
                    <li ng-class="{active: (page == 'Domain Management')}"><a href="#/adops/domain-management">Domain Administration</a></li>
                </ul>
            </li>

            <li ng-class="{active: markAdminMenu}" ng-if="hasRole('SUPER_USER') || hasRole('TOOLBAR_PUBLISHER') || hasRole('INTERNAL_USER')">
                <a ng-click="toggleAdminMenu();" class="nav-toggle" ng-class="{active: showAdminMenu}">
                    <i class="fa fa-cogs fa-fw fa-3x"></i> Admin Options
                    <span class="fa arrow"></span>
                </a>
                <ul class="nav nav-second-level" ng-class="{collapse: !showAdminMenu, in: showAdminMenu}">
                    <li ng-class="{active: (page == 'API Keys')}"><a href="#/api-keys">API Keys</a></li>
                    <li ng-class="{active: (page == 'Audit Log')}"><a href="#/audit-log">Audit Log</a></li>
                    <li ng-class="{active: (page == 'Bulk Update')}"><a href="#/products/bulkupdate/">Bulk Update</a></li>
                    <li ng-class="{active: (page == 'Toolbar Publisher')}" ng-if="hasRole('TOOLBAR_PUBLISHER')">
                        <a href="#/toolbar-publisher">Toolbar Publisher</a>
                    </li>
                    <li ng-class="{active: (page == 'Product Versions')}" ng-if="hasRole('DEVELOPER')">
                        <a href="#/product-versions">Product Versions</a>
                    </li>
                    <li ng-class="{active: (page == 'Client Features')}" ng-if="hasRole('DEVELOPER')">
                        <a href="#/admin/client-features">Client Features</a>
                    </li>
                    <li ng-class="{active: (page == 'Upgrade Accounts')}" ng-if="hasRole('SUPER_USER')">
                        <a href="#/admin/upgrade/list-accounts">Upgrade Accounts</a>
                    </li>
                </ul>
            </li>
        </ul>
        <!-- /#side-menu -->
    </div>
    <!-- /.sidebar-collapse -->
</nav>
<!-- /.navbar-static-side -->

<div id="page-wrapper" ng-view autoscroll>
</div>
<!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->

<!-- Modal -->
<div class="modal fade" id="supportModal" tabindex="-1" role="dialog" aria-labelledby="supportModalLabel" aria-hidden="true" ng-controller="SupportPopupController">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="supportModalLabel">Contact Red Panda Platform Support</h4>
            </div>
            <div class="modal-body">

                <form ng-if="!hasSentMail">
                    <div class="form-group">
                        <label for="support-modal-subject">Subject</label>
                        <input type="text" class="form-control" id="support-modal-subject" placeholder="Enter message subject" ng-model="$parent.subject">
                    </div>
                    <div class="form-group">
                        <label for="support-modal-body">Message</label>
                        <textarea class="form-control" id="support-modal-body" placeholder="Enter message body" ng-model="$parent.message_body"></textarea>
                    </div>
                </form>

                <p ng-if="hasSentMail">Your support message has been sent. Someone will get back to you shortly!</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" ng-click="sendSupportMail()" ng-if="!hasSentMail">Ask For Help</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal -->
<div style="position:fixed; width: 100%; height: 3em; bottom: 0px; z-index: 1000000" ng-if="productsToPublish.length > 0">
    <div class="translucent" style="position:absolute; left: 0px; top: 0px; width: 100%; height: 100%; z-index: 1000001; background-color: #ffffe0; border: 1px solid black; "></div>
    <div  id="publish-wrapper" class="opaque" style="text-align: center; position:absolute; left: 0px; top: 0px;  width: 100%; height: 100%;  z-index: 1000002; background-color: transparent; color: black;">
       <!--<div ng-include="publishTagsUrl" ></div>-->
    </div>
</div>
<!--
<a ng-href="/wahwahplatform/api/1.0/sites/tags/{{ site.id }}/zip" ng-click="startPublish(site)"><i class="fa fa-2x fa-download"></i></a>
-->

    <!-- Wahwah Platform Theme Libraries -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.js"></script>
    <wahwah:script src="content/libs/bootstrap/js/bootstrap.js"></wahwah:script>
    <wahwah:script src="content/libs/mint-theme/js/plugins/metisMenu/jquery.metisMenu.js"></wahwah:script>
    <wahwah:script src="content/libs/mint-theme/js/mint-admin.js"></wahwah:script>

    <!-- Wahwah Platform Libraries -->
    <wahwah:script src="content/libs/stomp-websocket/stomp.js"></wahwah:script>
    <wahwah:script src="content/libs/typeahead.js/typeahead.bundle.js"></wahwah:script>
    <wahwah:script src="content/libs/moment.js/moment.min.js"></wahwah:script>
    <wahwah:script src="content/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></wahwah:script>
    <wahwah:script src="content/libs/amcharts/amcharts/amcharts.js"></wahwah:script>
    <wahwah:script src="content/libs/amcharts/amcharts/serial.js"></wahwah:script>
    <wahwah:script src="content/libs/bootstrap-tagsinput/dist/bootstrap-tagsinput.js"></wahwah:script>
    <wahwah:script src="content/libs/file-saver/FileSaver.min.js"></wahwah:script>
    <wahwah:script src="content/libs/bootbox/bootbox.min.js"></wahwah:script>
    <wahwah:script src="content/libs/modernizr/modernizr.js"></wahwah:script>
    <wahwah:script src="content/libs/moment-range/moment-range.min.js"></wahwah:script>

    <!-- Wahwah Platform Angular Libraries -->
    <wahwah:script src="content/libs/angularjs/angular.js"></wahwah:script>
    <wahwah:script src="content/libs/angular-multiselect/lodash.js"></wahwah:script>
    <wahwah:script src="content/libs/angular-multiselect/angularjs-dropdown-multiselect.js"></wahwah:script>
    <wahwah:script src="content/libs/angularjs/angular-route.min.js"></wahwah:script>
    <wahwah:script src="content/libs/angularjs/angular-cookies.min.js"></wahwah:script>
    <wahwah:script src="content/libs/angular-stomp/angular-stomp.js"></wahwah:script>
    <wahwah:script src="content/libs/angular-typeahead/angular-typeahead.js"></wahwah:script>
    <wahwah:script src="content/libs/wahwah-angular-bootstrap/WahwahAngularBootstrap.js"></wahwah:script>
    <wahwah:script src="content/libs/wahwah-amcharts/WahwahAmcharts.js"></wahwah:script>


    <!-- rzslider directive -->
    <wahwah:script src="content/libs/rzslider/rzslider.min.js"></wahwah:script>

    <!-- Wahwah Platform Non-Angular Scripts -->
    <wahwah:script src="js/platformapp/RedPandaPlatform.js"></wahwah:script>

    <!-- Wahwah Platform Components and Services -->
    <wahwah:script src="components/ComponentsLoader.js"></wahwah:script>
    <wahwah:script src="js/platformapp/services/ServiceLoader.js"></wahwah:script>

    <!-- Wahwah Platform Application Scripts -->
    <wahwah:script src="js/platformapp/App.js"></wahwah:script>
    <wahwah:script src="js/platformapp/NavbarController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/SidebarController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/ProfileController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/ToolbarUploadController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/OverviewController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/CustomReportsController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/TagListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/UserListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/UserEditController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/UserCreateController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/PublisherListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/PublisherController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/NetworkListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/NetworkController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/TagSettingsController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/APIKeysControllers.js"></wahwah:script>
    <wahwah:script src="js/platformapp/AuditLogController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/ProductVersionListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/ProductVersionCreateController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/SiteController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/SiteListController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/ProductBulkUpdateController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/SupportPopupController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/AdminOptionsController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/AdOpsOptionsControllers.js"></wahwah:script>
    <wahwah:script src="js/platformapp/DemandSourceControllers.js"></wahwah:script>
    <wahwah:script src="js/platformapp/DemandSourceEditController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/DemandSourceCreateController.js"></wahwah:script>
    <wahwah:script src="js/platformapp/UpsellControllers.js"></wahwah:script>

</body>

</html>
