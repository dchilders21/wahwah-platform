/**
 * Created by jhaygood on 2/6/16.
 */

var components = ['FormControls', 'User','PopoverMenu','TagSettings','TagInput','RevenueModel','DemandSource'];

angular.module('wahwah.components',[]);

for(var i = 0; i < components.length; i++){
    var component = components[i];
    var componentUrl = getUrl("/components/" + component + "/" + component + ".js");

    var html = '<scri' + 'pt src="' + componentUrl + '" type="text/javascript"></sc' + 'ript>';
    document.writeln(html);
}