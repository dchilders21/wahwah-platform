wahwahApp.controller('NetworkController', ['$scope', '$http', '$routeParams', '$location', '$log','$user', '$timeout',
	function ($scope, $http, $routeParams, $location, $log, $user, $timeout)
	{
		$scope.$ctrl = {};
		var ctrl = $scope.$ctrl;

		ctrl.tabs = [
			{name: 'Network Info', id: 'network-info'},
			{name: 'Financial Model', id: 'financial-info'},
		];

		$scope.networkQuery = getUrl("/api/1.0/networks/query/%QUERY");
		$scope.user = {roles: []};
		$scope.currentNetwork = {};
		$scope.currentNetwork.account_type = 'NETWORK';
		$scope.currentNetwork.language = "en";
		$scope.currentNetwork.country = "UNITED_STATES";
		$scope.currentNetwork.autoCreateNetworkPublisher = true;

		$scope.allowedFormats = [{type: "OUTSTREAM", description: "Outstream"},{type: "FLOATER", description: "Floater"},{type: "BANNER", description: "Banner"}];


		$scope.disableSubmit = false;
		$scope.showSaveError = false;
		$scope.showSaveSuccess = false;

		$scope.languages = [
			{id: 'en', name: 'English'},
			{id: 'es', name: 'Spanish'},
			{id: 'pt', name: 'Portuguese'}
		];

		$scope.countries = [
			{id: 'UNITED_STATES', name: 'United States'},
			{id: 'SPAIN', name: 'Spain'},
			{id: 'BRAZIL', name: 'Brazil'}
		];

		$scope.accountTypes = [
			{id: 'PUBLISHER', name: 'Marketplace Network'},
			{id: 'NETWORK', name: 'Red Panda Network'}
		];

		$scope.currentNetworkSiteSettings = {
			site_language: $scope.currentNetwork.language,
			site_country: $scope.currentNetwork.country
		};

		$scope.$watch('currentNetwork.language', function (newValue, oldValue)
		{
			 $scope.currentNetworkSiteSettings.site_language = $scope.currentNetwork.language;

		});

		$scope.$watch('currentNetwork.country', function (newValue, oldValue)
		{
			$scope.currentNetworkSiteSettings.site_country = $scope.currentNetwork.country;

		});


		ctrl.isDisplayPassback = false;
		$scope.toggleDisplayPassback = function()
		{
			if ($scope.currentPublisher.passback_display_tag_html == null)
			{
				$scope.currentPublisher.passback_display_tag_html = "";
			}
		}

		$user.getUserPromise().then(function(data){
			$scope.user = data;
		});

        ctrl.setCurrentTab = function(tab){
            $location.search('tab',tab.id);
        };

        function reloadDataFromSearch()
        {
            ctrl.currentTab = ctrl.tabs[0];

            var searchParams = $location.search();

            ctrl.selectedTabId = searchParams.tab;

            for(var i = 0; i < ctrl.tabs.length; i++){
                if(ctrl.selectedTabId == ctrl.tabs[i].id) {
                    ctrl.currentTab = ctrl.tabs[i];
                    break;
                }
            }
        }

        $scope.$on('$locationChangeSuccess', function ()
        {
            reloadDataFromSearch();
        });

        reloadDataFromSearch();

		if ($routeParams.networkId)
		{
			$http.get(getUrl("/api/1.0/networks/" + $routeParams.networkId + "/")).success(function (data)
			{
				$scope.currentNetwork = data;
				ctrl.isDisplayPassback = false;
				if ($scope.currentNetwork.passback_display_tag_html != null)
				{
					ctrl.isDisplayPassback = true;
				}
			});
		}

		$scope.hasRole = function (roleName)
		{
			return $user.hasRole(roleName);
		};

		$scope.createNetwork = function ()
		{
			$scope.disableSubmit = true;

			$scope.checkAutoCreate(); // Have to do this first

			$scope.currentNetwork.site = $scope.currentNetworkSiteSettings;

			$scope.showSaveError = false;
			$scope.showSaveSuccess = false;

			if (ctrl.isDisplayPassback == false)
				$scope.currentNetwork.passback_display_tag_html = null;

			$http.post(getUrl("/api/1.0/networks/"), $scope.currentNetwork).success(function (data)
			{

				ctrl.networkForm.$setSubmitted();

				$log.log(data);

				$scope.showSaveSuccess = true;

				$location.path("/networks/"+data.id);
			}).error(function (data)
			{
				var msgLower = "";
				if (typeof(data.message)!= "undefined"  && data.message != null)
					msgLower = data.message.toString().toLowerCase();

				if (msgLower.indexOf("duplicate name error") >= 0)
				{
					if (msgLower.indexOf("openx") >= 0)
					{
						$scope.errorMsg = "Duplicate network name in Openx. Please choose a different name.";
					}
					else
					{
						$scope.errorMsg = "Duplicate network name. Please choose a different name.";
					}
				}
				else if (msgLower.indexOf("name length") >= 0)
					$scope.errorMsg = "Please limit network name to 255 characters.";
				else
					$scope.errorMsg = "Bad network values. Please check";

				$scope.disableSubmit = false;
				$scope.showSaveError = true;
				$timeout(function ()
				{
					$scope.showSaveError = false;
				}, 15000);
			});
		};

		$scope.checkAutoCreate = function()
		{
			if ($scope.currentNetwork.account_type == 'NETWORK') // Should have already changed
			{
				$scope.currentNetwork.autoCreateSiteAndToolbar = false;
				$scope.currentNetworkSiteSettings.site_url = "";
			}
		};

		$scope.updateNetwork = function ()
		{
			$scope.disableSubmit = true;

			var networkId = $scope.currentNetwork.id;
			$log.log('Update network pressed: ' + networkId);

			$scope.currentNetwork.site = $scope.currentNetworkSiteSettings;

			$scope.showSaveError = false;
			$scope.showSaveSuccess = false;

			if (ctrl.isDisplayPassback == false)
				$scope.currentNetwork.passback_display_tag_html = null;

			$http.put(getUrl("/api/1.0/networks/" + networkId), $scope.currentNetwork).success(function (data)
			{

				ctrl.networkForm.$setSubmitted();

				$log.log(data);
				$scope.showSaveSuccess = true;
				$location.path("/networks/");
			}).error(function (data)
			{
				var msgLower = "";
				if (typeof(data.message)!= "undefined"  && data.message != null)
					msgLower = data.message.toString().toLowerCase();
				if (msgLower.indexOf("duplicate name error") >= 0)
				{
					if (msgLower.indexOf("openx") >= 0)
					{
						$scope.errorMsg = "Duplicate network name in Openx. Please choose a different name.";
					}
					else
					{
						$scope.errorMsg = "Duplicate network name. Please choose a different name.";
					}
				}
				else if (msgLower.indexOf("name length") >= 0)
					$scope.errorMsg = "Please limit network name to 255 characters.";
				else
					$scope.errorMsg = "Bad network values. Please check";

				$scope.disableSubmit = false;
				$scope.showSaveError = true;

				$timeout(function ()
				{
					$scope.showSaveError = false;
				}, 15000);
			});
		};

		$scope.onNetworkFilter = function (data)
		{
			var networks = [];

			for (var i = 0; i < data.networks.length; i++)
			{
				networks.push({value: data.networks[i].name});
			}

			return networks;
		};

		$scope.isNewNetwork = function ()
		{
			return typeof($scope.currentNetwork.id) == "undefined";
		};

		$scope.$on('$locationChangeStart', function (event, newUrl, oldUrl)
		{
			function stripQueryString(url)
			{
				if (url.indexOf("?") == -1)
					url = url + "?";
				return url.substring(0,url.indexOf("?"));;
			}
			newUrl = stripQueryString(newUrl);
			oldUrl = stripQueryString(oldUrl);

			if(newUrl == oldUrl){
				return;
			}

			if (ctrl.networkForm.$dirty && !ctrl.networkForm.$submitted)
			{
				var confirmLocationChange = confirm("You have unsaved information. Click 'OK' to leave the current page or click 'Cancel' to stay on this page.");
				if (!confirmLocationChange)
				{
					event.preventDefault();
				}
			}
		});


	}]);
