function TagSettingsDomainPanelController(){
    var ctrl = this;

    ctrl.errorMessages = [];

    var errors = {
        NO_SUGGESTED_DOMAIN: "Please highlight at least one suggested domain",
        NO_DOMAIN: "Please highlight at least one domain to remove"
    };

    ctrl.selectedSuggestedDomains = [];
    ctrl.selectedDomains = [];

    ctrl.createDomain = function(){
        var newDomain = {
            domain: ctrl.newCustomDomain,
            id: 0,
            is_mapped: false,
            mapped: false,
            platform_site_id: ctrl.site.id,
            site_name: ctrl.site.site_name
        };

        ctrl.domains.push(newDomain);
    };

    ctrl.addDomain = function(){
        if(ctrl.selectedSuggestedDomains.length == 0){
            showErrorMessage(errors.NO_SUGGESTED_DOMAIN);
        } else {
            clearErrorMessage(errors.NO_SUGGESTED_DOMAIN);
        }

        for (var i = 0; i < ctrl.selectedSuggestedDomains.length; i++)
        {
            var selectedDomain = ctrl.selectedSuggestedDomains[i];

            selectedDomain.platform_site_id = ctrl.site.id;
            selectedDomain.site_name = ctrl.site.site_name;

            var j = getIndexForDomain(ctrl.suggestedDomains,selectedDomain.domain);
            ctrl.suggestedDomains.splice(j,1);
            ctrl.domains.push(selectedDomain);
        }
    };

    ctrl.removeDomain = function(){
        if(ctrl.selectedDomains.length == 0){
            showErrorMessage(errors.NO_DOMAIN);
        } else {
            clearErrorMessage(errors.NO_DOMAIN);
        }

        for (var i = 0; i < ctrl.selectedDomains.length; i++)
        {
            var selectedDomain = ctrl.selectedDomains[i];

            var j = getIndexForDomain(ctrl.domains,selectedDomain.domain);
            ctrl.domains.splice(j,1);
            ctrl.suggestedDomains.push(selectedDomain);
        }
    };

    function getIndexForDomain(array,domain){
        for(var i = 0; i < array.length; i++){
            if(array[i].domain == domain){
                return i;
            }
        }

        return -1;
    }

    function showErrorMessage(errorMessage){
        if(ctrl.errorMessages.indexOf(errorMessage) == -1){
            ctrl.errorMessages.push(errorMessage);
        }
    }

    function clearErrorMessage(errorMessage){
        var i = ctrl.errorMessages.indexOf(errorMessage);

        if(i > -1){
            ctrl.errorMessages.splice(i,1);
        }
    }
}

function TagSettingsFeaturePanelController(){
    var ctrl = this;

    ctrl.addSelectedFeature = function(){

        var productFeature = {
            feature: ctrl.selectedFeatureToAdd,
            feature_id: ctrl.selectedFeatureToAdd.id,
            value_string: null,
            value_number: null,
            value_boolean: null,
            variable_type: ctrl.selectedFeatureToAdd.variable_type
        };

        ctrl.tag.product.features.push(productFeature);
    };

    ctrl.removeFeature = function(feature){
        var i = ctrl.tag.product.features.indexOf(feature);
        ctrl.tag.product.features.splice(i,1);
    };
}

TagSettingsAdvancedPanelController.$inject = ['$user','$http'];

function TagSettingsAdvancedPanelController($user, $http){

    var ctrl = this;

    ctrl.logLevels = ['error', 'info', 'warn', 'debug'];

    ctrl.hasRole = function (roleName)
    {
        return $user.hasRole(roleName);
    };

    $http.get(getUrl("/api/1.0/product-versions/without-checkout")).success(function (data)
    {
        ctrl.productVersions = data.productVersions;
    });

    $http.get(getUrl("/api/1.0/system/environment/")).success(function (data)
    {
        ctrl.environment = data;
    });

    ctrl.adUnitTypes = {
        "DISPLAY": "Display",
        "MOBILE": "Mobile",
        "VIDEO": "Video"
    };

    ctrl.addNewRow = function(){
        if (ctrl.tag.product.is_locked)
            return;
        var rightNow = new Date();
        var res = rightNow.toISOString().slice(0,10).replace(/-/g,"") + "." + rightNow.getUTCHours() + rightNow.getUTCMinutes() + rightNow.getUTCSeconds();

        var adUnitTitle;

        if(ctrl.tag.product.type == "AD_SERVER_NATIVE"){
            adUnitTitle = "Ad Server Ad Unit " + res;
        } else {
            adUnitTitle = "Add'l Passbacks " + res;
        }

        ctrl.tag.ad_units.push({
            title: adUnitTitle,
            platform_created: false,
            is_editing: true
        });
    };

    ctrl.removeRow = function(adUnit){
        if (ctrl.tag.product.is_locked)
            return;
        var i = ctrl.tag.ad_units.indexOf(adUnit);
        ctrl.tag.ad_units.splice(i,1);
    };

    ctrl.saveRow = function(adUnit){
        if (ctrl.tag.product.is_locked)
            return;
        adUnit.is_editing = false;
        adUnit.ad_unit_name = adUnit.title;
    };

    ctrl.editRow = function(adUnit){
        if (ctrl.tag.product.is_locked)
            return;
        adUnit.is_editing = true;
    };

}

angular.module('wahwah.components').component('tagSettingsDomainPanel', {
    templateUrl: getUrl('/components/TagSettings/tag-settings-domain.html'),
    controller: TagSettingsDomainPanelController,
    bindings: {
        site: '<',
        domains: '<',
        suggestedDomains: '<'
    }
});

angular.module('wahwah.components').component('tagSettingsFeaturePanel', {
    templateUrl: getUrl('/components/TagSettings/tag-settings-feature.html'),
    controller: TagSettingsFeaturePanelController,
    bindings: {
        tag: '<',
        availableFeatures: '<'
    }
});

angular.module('wahwah.components').component('tagSettingsAdvancedPanel', {
    templateUrl: getUrl('/components/TagSettings/tag-settings-advanced.html'),
    controller: TagSettingsAdvancedPanelController,
    bindings: {
        site: '<',
        tag: '<'
    }
});