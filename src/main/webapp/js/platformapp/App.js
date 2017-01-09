var wahwahApp = angular.module("wahwahApp", ['ngRoute', 'ngCookies', 'AngularStomp', 'typeahead.directives', 'wahwah.bootstrap', 'wahwah.amcharts', 'rzModule', 'angularjs-dropdown-multiselect', 'wahwah.components', 'wahwah.services']);

wahwahApp.config(['$routeProvider',
	function ($routeProvider)
	{
		$routeProvider
			.when('/dashboard', {
				templateUrl: getUrl('/views/overview.html'),
				controller: 'OverviewController',
				reloadOnSearch: false
			}).
            when('/overview', {
                redirectTo: "/dashboard"
            }).
			when('/products', {
				templateUrl: getUrl('/views/tag-list.html'),
				controller: 'ProductListController'
			}).
			when('/products/bulkupdate', {
				templateUrl: getUrl('/views/products-bulkupdate.html'),
				controller: 'ProductBulkUpdateController'
			}).
			when('/toolbar-publisher', {
				templateUrl: getUrl('/views/toolbar-publisher.html'),
				controller: 'ToolbarUploadController'
			}).
			when('/api-keys', {
				templateUrl: getUrl('/views/api-keys-list.html'),
				controller: 'APIKeysListController'
			}).
			when('/api-keys/add', {
				templateUrl: getUrl('/views/api-keys-add.html'),
				controller: 'APIKeysCreateController'
			}).
			when('/api-keys/widget-:widgetId', {
				templateUrl: getUrl('/views/api-keys-edit.html'),
				controller: 'APIKeysEditController'
			}).
			when('/profile', {
				templateUrl: getUrl('/views/profile.html'),
				controller: 'ProfileController'
			}).
			when('/users', {
				templateUrl: getUrl('/views/user-list.html'),
				controller: 'UserListController'
			}).
			when('/users/create', {
				templateUrl: getUrl('/views/user-create.html'),
				controller: 'UserCreateController'
			}).
			when('/users/:userId', {
				templateUrl: getUrl('/views/user-edit.html'),
				controller: 'UserEditController'
			}).
			when('/publishers', {
				templateUrl: getUrl('/views/publisher-list.html'),
				controller: 'PublisherListController'
			}).
			when('/publishers/create', {
				templateUrl: getUrl('/views/publisher-settings.html'),
				controller: 'PublisherController'
			}).
			when('/network-:networkId/publishers/create', {
				templateUrl: getUrl('/views/publisher-settings.html'),
				controller: 'PublisherController'
			}).
			when('/publishers/:publisherId', {
				templateUrl: getUrl('/views/publisher-settings.html'),
				controller: 'PublisherController',
				reloadOnSearch: false
			}).
			when('/networks', {
				templateUrl: getUrl('/views/network-list.html'),
				controller: 'NetworkListController'
			}).
			when('/networks/create', {
				templateUrl: getUrl('/views/network-settings.html'),
				controller: 'NetworkController'
			}).
			when('/networks/:networkId', {
				templateUrl: getUrl('/views/network-settings.html'),
				controller: 'NetworkController'
			}).
			when('/network-:networkId/publishers/', {
				templateUrl: getUrl('/views/publisher-list.html'),
				controller: 'PublisherListController'
			}).
			when('/audit-log', {
				templateUrl: getUrl('/views/audit-log.html'),
				controller: 'AuditLogController'
			}).
			when('/product-versions', {
				templateUrl: getUrl('/views/version-list.html'),
				controller: 'ProductVersionListController'
			}).
			when('/product-versions/create', {
				templateUrl: getUrl('/views/version-create.html'),
				controller: 'ProductVersionCreateController'
			}).
			when('/sites', {
				templateUrl: getUrl('/views/site-list.html'),
				controller: 'SiteListController'
			}).
			when('/publisher-:publisherId/sites/', {
				templateUrl: getUrl('/views/site-list.html'),
				controller: 'SiteListController'
			}).
			when('/sites/create/publisher-:publisherId/', {
				templateUrl: getUrl('/views/site-settings.html'),
				controller: 'SiteController'
			}).
			when('/sites/create', {
				templateUrl: getUrl('/views/site-settings.html'),
				controller: 'SiteController'
			}).
			when('/sites/:siteId', {
				redirectTo: function(routeParams){
					return "/tagsettings/" + routeParams.siteId;
				}
			}).
			when('/custom-reports', {
				templateUrl: getUrl('/views/custom-reports.html'),
				controller: 'CustomReportsController',
			}).
            when('/tagsettings/:siteId/by-tag/:productId', {
                redirectTo: function(routeParams){
					return "/tagsettings/" + routeParams.siteId + "?tag_id=" + routeParams.productId;
				}
            }).
			when('/tagsettings/:siteId', {
				templateUrl: getUrl('/views/tag-settings.html'),
				controller: 'TagSettingsController',
				reloadOnSearch: false
			}).
			when('/admin/client-features', {
				templateUrl: getUrl('/views/admin-settings-client-features.html'),
				controller: 'AdminOptionsClientFeaturesController'
			}).
			when('/adops/unmapped-creatives', {
				templateUrl: getUrl('/views/adops-settings-unmapped-line-items.html'),
				controller: 'AdOpsOptionsUnmappedCreativeController'
			}).
			when('/adops/domain-management', {
				templateUrl: getUrl('/views/adops-settings-domain-management.html'),
				controller: 'AdOpsOptionsDomainManagementController'
			}).
			when('/adops/unknown-ad-units', {
				templateUrl: getUrl('/views/adops-settings-unknown-adunits.html'),
				controller: 'AdOpsOptionsUnknownAdUnitController'
			}).
			when('/adops/line-item-mapping', {
				templateUrl: getUrl('/views/adops-settings-line-item-mappings.html'),
				controller: 'AdOpsOptionsLineItemMappingController'
			}).
			when('/adops/monthly-reconciliation', {
				templateUrl: getUrl('/views/adops-settings-monthly-reconciliation.html'),
				controller: 'AdOpsOptionsMonthlyReconciliationController'
			}).
			when('/demand-sources', {
				templateUrl: getUrl('/views/demand-source-list.html'),
				controller: 'DemandSourceListController',
				reloadOnSearch: false
			}).
			when('/demand-sources/create', {
				templateUrl: getUrl('/views/demand-source-create.html'),
				controller: 'DemandSourceCreateController as $ctrl'
			}).
			when('/demand-sources/google-callback/:googleService', {
				templateUrl: getUrl('/views/demand-source-create.html'),
				controller: 'DemandSourceCreateController as $ctrl'
			}).
            when('/demand-sources/:demandSourceId', {
                templateUrl: getUrl('/views/demand-source-edit.html'),
                controller: 'DemandSourceEditController as $ctrl',
				reloadOnSearch: false
            }).
			when('/demand-source-:demandSourceId/connections/', {
				templateUrl: getUrl('/views/demand-source-connections-list.html'),
				controller: 'DemandSourceConnectionListController'
			}).
			when('/demand-source-:demandSourceId/connections/create', {
				templateUrl: getUrl('/views/demand-source-connections-edit.html'),
				controller: 'DemandSourceConnectionEditController'
			}).
            when('/demand-source-:demandSourceId/connections/:demandSourceConnectionId', {
                templateUrl: getUrl('/views/demand-source-connections-edit.html'),
                controller: 'DemandSourceConnectionEditController'
            }).
            when('/upgrade/from-free', {
                templateUrl: getUrl('/views/upsell.html'),
                controller: 'UpsellFromFreeController as $ctrl'
            }).
			when('/admin/upgrade/list-accounts', {
			    templateUrl: getUrl('/views/upgrade-account-list.html'),
                controller: 'AdminUpgradeAccountListController as $ctrl'
			}).
            when('/admin/upgrade/account-:accountId', {
                templateUrl: getUrl('/views/upgrade-account.html'),
                controller: 'AdminUpgradeAccountController as $ctrl'
            }).
			otherwise({
				redirectTo: function(){
					var defaultPath = $("body").data("defaultpath");
					return defaultPath;
				}
			});
	}
]);

wahwahApp.config(['$sceDelegateProvider',function($sceDelegateProvider){

    var commitId = $("body").data("commitid");
    var serverName = $("body").data("servername");

    var whitelist = ["self"]; // Only same-origin

    // For Staging and Production, we only allow urls for the appropriate CDN domain and commit id

    if(serverName == "staging.dev.wahwahnetworks.com"){
        var url = "http://platform-assets-staging.wwnstatic.com/" + commitId + "/**";
        whitelist.push(url);
    }

    if(serverName == "app.redpandaplatform.com"){
        var url = "http://platform-assets.wwnstatic.com/" + commitId + "/**";
        whitelist.push(url);
    }

    $sceDelegateProvider.resourceUrlWhitelist(whitelist);

}]);

wahwahApp.config(['$httpProvider',
		function ($httpProvider)
		{
			$httpProvider.interceptors.push(function ($q)
			{
				return {
					'request': function (config)
					{
						if (config.url.indexOf("/analytics/") == 0)
						{
							config.headers["X-Wahwah-Token"] = wahwahToken;
						}

						return config;
					},
					'response': function (response)
					{
						// If response was successful, return response
						return response;
					},
					'responseError': function (rejection)
					{
						if (rejection.status === 401)
						{
							var loginPage = getUrl("/useraccount/login");
							window.location = loginPage;
						}

						return $q.reject(rejection);
					}
				}
			})

		}]
);

wahwahApp.factory("$flashMessage", function ()
{
	var currentMessage = "";

	return {
		setMessage: function (message)
		{
			currentMessage = message;
		},
		getMessage: function ()
		{
			var message = currentMessage;
			currentMessage = "";
			return message;
		}
	};
});

wahwahApp.filter('capitalize', function() {
    return function(input) {
        if (input != null){
            input = input.toLowerCase();
            return input.substring(0,1).toUpperCase() + input.substring(1);
        }
    }
});

wahwahApp.constant('Modernizr',Modernizr);
wahwahApp.constant('moment',moment);
wahwahApp.constant('bootbox',bootbox);

if (!String.prototype.endsWith) {
    String.prototype.endsWith = function(searchString, position) {
        var subjectString = this.toString();
        if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}