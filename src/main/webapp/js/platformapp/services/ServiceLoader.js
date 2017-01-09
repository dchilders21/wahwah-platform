/**
 * Created by jhaygood on 2/25/16.
 */

var services = ['User','Session'];

angular.module('wahwah.services',[]);

for(var i = 0; i < services.length; i++){
    var service = services[i];
    var serviceUrl = getUrl("/js/platformapp/services/" + service + "Service.js");

    var html = '<scri' + 'pt src="' + serviceUrl + '" type="text/javascript"></sc' + 'ript>';
    document.writeln(html);
}