/**
 * Created by jhaygood on 2/19/16.
 */

wahwahApp.controller('TagSettingsController', ['$scope', '$http', '$routeParams', '$location', '$timeout','$user',
    function($scope, $http, $routeParams, $location, $timeout, $user){

        $scope.$ctrl = {};
        var ctrl = $scope.$ctrl;

        ctrl.tabs = [
            {name: 'Site Info', id: 'site-info'},
        ];

        ctrl.siteLanguages = [
            {id: 'en', name: 'English'},
            {id: 'es', name: 'Spanish'},
            {id: 'pt', name: 'Portuguese'}
        ];

        ctrl.siteCountries = [
            {id: 'UNITED_STATES', name: 'United States'},
            {id: 'SPAIN', name: 'Spain'},
            {id: 'BRAZIL', name: 'Brazil'}
        ];

        ctrl.trafficEstimates = [
            {id: '_0_TO_1MM', name: 'Up to 1M'},
            {id: '_1MM_TO_5MM', name: '1MM - 5MM'},
            {id: '_5MM_TO_10MM', name: '5MM - 10MM'},
            {id: '_10MM_TO_20MM', name: '10MM - 20MM'},
            {id: '_20MM_PLUS', name: '20 Million+'}
        ];

        ctrl.siteTypes = [
            {id: 'ENTERTAINMENT', name: 'Entertainment'},
            {id: 'WOMENS_LIFESTYLE', name: 'Womens Lifestyle'},
            {id: 'MENS_LIFESTYLE', name: 'Mens Lifestyle'},
            {id: 'NEWS_AND_BUSINESS', name: 'News & Business'},
            {id: 'SPORTS', name: 'Sports'}
        ];

        ctrl.adFormats = ['In-Banner', 'Floater', 'Outstream'];

        ctrl.adFormatNames = {
            'floater': "Floater",
            'banner': "In-Banner",
            'ostream': 'Outstream',
        };

        ctrl.productTypes = {
            OUTSTREAM: 'Outstream',
            FLOATER: "Floater",
            BANNER: "In-Banner",
            ADSERVERNATIVE: "Ad Server Ad Unit",
            CUSTOM: "Custom"
        };

        ctrl.adHorizontalAlignments = ['left', 'right', 'center'];
        ctrl.adVerticalAlignments = ['top', 'bottom', 'middle'];

        ctrl.getUrl = getUrl;
        ctrl.xsrfToken = $scope.$parent.xsrfToken;

        ctrl.hasLoadedTabs = false;

        ctrl.allowedFormats = [{type: "OUTSTREAM", description: "Outstream"},{type: "FLOATER", description: "Floater"},{type: "BANNER", description: "Banner"}];
        ctrl.getParentFormatDescription = function()
        {
            var type = ctrl.tagSettings.parent_default_product_format;
            for (var i = 0; i < ctrl.allowedFormats.length; i++)
            {
                if (ctrl.allowedFormats[i].type == type)
                    return ctrl.allowedFormats[i].description;
            }

            return "";
        };

        $scope.horizAlignment;
        $scope.vertAlignment;

        ctrl.hasRole = function(roleName){
            return $user.hasRole(roleName);
        };

        function reloadDataFromSearch()
        {
            ctrl.currentTab = ctrl.tabs[0];

            var searchParams = $location.search();

            ctrl.selectedTabId = searchParams.tab;
            $scope.productId = ctrl.productId = searchParams.tag_id;

            for(var i = 0; i < ctrl.tabs.length; i++){
                if(ctrl.selectedTabId == ctrl.tabs[i].id) {
                    ctrl.currentTab = ctrl.tabs[i];
                    break;
                }
            }
        }

        $scope.setHorizontalDemo = function(alignment) {
            $scope.horizAlignment = alignment;
        };

        $scope.setVerticalDemo = function(alignment) {
            $scope.vertAlignment = alignment;
        };

        $scope.$on('$locationChangeSuccess', function ()
        {
            reloadDataFromSearch();
        });

        ctrl.setCurrentTab = function(tab){
            $location.search('tab',tab.id);
        };

        ctrl.setTagFormat = function(formatName){

            for(var formatType in ctrl.adFormatNames){
                if(ctrl.adFormatNames[formatType] == formatName){
                    ctrl.currentTag.product.standalone_ad.ad_format = formatType;
                }
            }
        };

        $scope.siteId = $routeParams.siteId;
        $scope.productId = $routeParams.productId;

        $scope.universalSiteTagDownloadUrl = getUrl("/api/1.0/sites/universaltag/txt/" + $scope.siteId)  + "?xsrf_token=" + $scope.xsrfToken;

        $scope.refreshSiteData = function(){
            $http.get(getUrl("/api/1.0/tag-settings/site-" + $scope.siteId)).success(function(data){
                 ctrl.tagSettings = data;
                 ctrl.allowedFormats.push({type: null, description: "Inherit ("+ctrl.getParentFormatDescription()+")"});
                 setSiteData();
            });
        };

        function setSiteData(){
            ctrl.currentSite = ctrl.tagSettings.site;

            ctrl.currentSite.isDisplayPassback = false;
            if (ctrl.currentSite.passback_display_tag_html != null)
            {
                ctrl.currentSite.isDisplayPassback = true;
            }


            if(!ctrl.hasLoadedTabs) {
                if (ctrl.tagSettings.network == null || ctrl.tagSettings.network.account_type != "REPORTING_PRO") {
                    ctrl.tabs.push({name: 'Tag Settings', id: 'tag-settings'});
                }
            }

            $user.getUserPromise().then(function(user){
                ctrl.user = user;
            });

            $user.hasRolePromise('NETWORK_ADMIN').then(function(doesHaveRole){
                if(doesHaveRole) {

                    if(!ctrl.hasLoadedTabs) {
                        ctrl.tabs.push({name: 'Domains', id: 'domains'});
                        ctrl.tabs.push({name: 'Financial Model', id: 'financial-model'});

                        if (ctrl.tagSettings.network == null || ctrl.tagSettings.network.account_type != "REPORTING_PRO") {
                            ctrl.tabs.push({name: 'Special Features', id: 'features'});
                            ctrl.tabs.push({name: 'Advanced', id: 'advanced'});
                        }
                    }
                }

                ctrl.hasLoadedTabs = true;

                reloadDataFromSearch();
            });

            if(ctrl.tagSettings.tags.length > 0) {
                ctrl.selectTag(ctrl.tagSettings.tags[0]);
            }

            ctrl.originalSite = angular.copy(ctrl.tagSettings.site);

            if($scope.productId){
                for(var i = 0; i < ctrl.tagSettings.tags.length; i++){
                    if(ctrl.tagSettings.tags[i].product.id == $scope.productId){
                        ctrl.selectTag(ctrl.tagSettings.tags[i]);
                        break;
                    }
                }
            }

            if(ctrl.currentTag && ctrl.currentTag.product.type == "STANDALONE_AD"){
                $scope.setHorizontalDemo(ctrl.currentTag.product.standalone_ad.expansion_align_horiz);
                $scope.setVerticalDemo(ctrl.currentTag.product.standalone_ad.expansion_align_vert);
            }
        }

        if ($scope.siteId)
        {
            $scope.refreshSiteData();
        }

        ctrl.selectTag = function(tag){

            $location.search('tag_id',tag.product.id);
            ctrl.currentTag = tag;

            // Convert TOOLBAR to STANDALONE_AD

            if(ctrl.currentTag.product.type == 'TOOLBAR'){

                ctrl.currentTag.wasToolbar = true;

                ctrl.currentTag.product.standalone_ad = {
                    ad_format: 'floater',
                    align_vert: ctrl.currentTag.product.toolbar.align_vert,
                    align_horiz: ctrl.currentTag.product.toolbar.align_horiz,
                    expansion_align_vert: ctrl.currentTag.product.toolbar.align_vert,
                    expansion_align_horiz: ctrl.currentTag.product.toolbar.align_horiz,
                    inad_breakout: false,
                    outstream_autoload: false,
                    outstream_float: false,
                };

                ctrl.currentTag.product.type = 'STANDALONE_AD';
            }
        };

        ctrl.createAdditionalTag = function(){

            ctrl.newProduct = {
                name: "",
            };

            /*
            bootbox.prompt("What is the new tag name?",function(result){
                $scope.$apply(function(){
                    $http.post(getUrl("/api/1.0/tag-settings/site-" + $scope.siteId + "/tags"),result).success(function(data){
                        ctrl.tagSettings.tags.push(data);
                        ctrl.selectTag(data);
                    });
                });
            }); */
        };
        
        ctrl.saveAdditionalTag = function(){
            $http.post(getUrl("/api/1.0/tag-settings/site-" + $scope.siteId + "/tags"),ctrl.newProduct).success(function(data){
                ctrl.tagSettings.tags.push(data);
                ctrl.selectTag(data);
            });

            ctrl.newProduct = null;
        };

        ctrl.disableSubmit = false;


        ctrl.cancel = function ()
        {
            ctrl.returnToSiteList();
        };

        ctrl.returnToSiteList = function()
        {
            if (ctrl.currentSite.account_id == null)
            {
                $location.path("/sites/");
            }
            else
            {
                $location.path("/publisher-"+ctrl.currentSite.account_id+"/sites/");
            }

        };

        ctrl.save = function(){

            if(ctrl.currentTag){
                $scope.productId = ctrl.currentTag.product.id;
            }

            if (ctrl.currentSite.isDisplayPassback == false)
            {
                ctrl.currentSite.passback_display_tag_html = null
            }

            ctrl.disableSubmit = true;

            $http.put(getUrl("/api/1.0/tag-settings/site-" + $scope.siteId), ctrl.tagSettings).success(function(data){

                ctrl.tagSettings = data;
                setSiteData();

                ctrl.showSaveSuccess = true;

                ctrl.disableSubmit = false;

                $timeout(function(){
                    ctrl.showSaveSuccess = false;
                },10000);
             })
                .error(function (data)
                {

                    ctrl.disableSubmit = false;

                    ctrl.showSaveError = true;

                    var msgLower = "";
                    if (typeof(data.message)!= "undefined"  && data.message != null)
                        msgLower = data.message.toString().toLowerCase();

                    if (msgLower.indexOf("invalid url") >= 0)
                        ctrl.errorMsg = "Invalid URL. URLs should start with http:// or https:// followed by domain and path";
                    else if (msgLower.indexOf("name length") >= 0)
                        ctrl.errorMsg = "Please name to 255 characters.";
                    else
                        ctrl.errorMsg = "Unknown error: Please check site values";


                    $timeout(function ()
                    {
                        ctrl.showSaveError = false;
                    }, 15000);
                });
        };

    }
]);