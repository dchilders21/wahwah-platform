function createUUID()
{
    // http://www.ietf.org/rfc/rfc4122.txt
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++)
    {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
}

function getUrl(url)
{
    var contextPath = $("body").data("contextpath");
    var commitId = $("body").data("commitid");
    var serverName = $("body").data("servername");

    var isApiMethod = url.indexOf("/api/") == 0 || url.indexOf("/useraccount/") == 0 || url.indexOf("/stomp/") == 0;

    if (!isApiMethod)
    {
        var host = serverName;

        if(window.location.port != 80){
            host = serverName + ":" + window.location.port;
        }

        var resolvedUrl = window.location.protocol + "//" + host + contextPath + url + "?rev=" + commitId;

        if(serverName == "staging.dev.wahwahnetworks.com"){
            resolvedUrl = "http://platform-assets-staging.wwnstatic.com/" + commitId + url;
        }

        if(serverName == "app.redpandaplatform.com"){
            resolvedUrl = "http://platform-assets.wwnstatic.com/" + commitId + url;
        }

        return resolvedUrl;
    }
    else
    {
        var host = serverName;

        if(window.location.port != 80){
            host = serverName + ":" + window.location.port;
        }

        var resolvedUrl = window.location.protocol + "//" + host + contextPath + url;

        return resolvedUrl;
    }
}

function hasRole(roleArray, role)
{

    if (typeof roleArray == "string")
    {
        // If you are getting this, you probably meant to use $scope.hasRole!
        return false;
    }

    if (roleArray.indexOf("DEVELOPER") >= 0)
    {
        return true;
    }

    return roleArray.indexOf(role) >= 0;
}

var wahwahToken = createUUID();