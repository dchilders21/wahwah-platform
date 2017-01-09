wahwahApp.controller('PublisherController', ['$scope', '$http', '$routeParams', '$location', '$log','$user',
	function ($scope, $http, $routeParams, $location, $log, $user)
	{
		$scope.$ctrl = {};
		var ctrl = $scope.$ctrl;

        ctrl.tabs = [
            {name: 'Publisher Info', id: 'publisher-info'},
            {name: 'Financial Model', id: 'financial-info'}
        ];

		$scope.publisherQuery = getUrl("/api/1.0/publishers/query/%QUERY");
		$scope.user = {roles: []};
		$scope.currentPublisher = {};
		$scope.currentPublisher.autoCreateSiteAndToolbar = true;
		$scope.currentPublisher.account_type = 'PUBLISHER';
		$scope.currentPublisher.language = "en";
		$scope.currentPublisher.country = "UNITED_STATES";

		$scope.network = {};
		$scope.network.id = null;

		$scope.allowedFormats = [ {type: "OUTSTREAM", description: "Outstream"},{type: "FLOATER", description: "Floater"},{type: "BANNER", description: "Banner"}];

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

        $scope.getParentFormatDescription = function()
		{
			var type = $scope.currentPublisher.parent_default_product_format;
			for (var i = 0; i < $scope.allowedFormats.length; i++)
			{
				if ($scope.allowedFormats[i].type == type)
					return $scope.allowedFormats[i].description;
			}

			return "";
		};

		$scope.disableSubmit = false;

		$scope.errorMsg = "";

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

		$scope.currentPublisherSiteSettings = {
			site_language: $scope.currentPublisher.language,
			site_country: $scope.currentPublisher.country
		};

		$scope.$watch('currentPublisher.language', function (newValue, oldValue)
		{
			 $scope.currentPublisherSiteSettings.site_language = $scope.currentPublisher.language;

		});

		$scope.$watch('currentPublisher.country', function (newValue, oldValue)
		{
			$scope.currentPublisherSiteSettings.site_country = $scope.currentPublisher.country;

		});

		$scope.showRevenueShareField = false;
		$scope.showGuaranteedCPMField = false;
		$scope.showGuaranteedMinimumField = false;

		$user.getUserPromise().then(function(data){
			$scope.user = data;
		});

		$scope.getNetworkData = function()
		{
			if ($scope.network.id == null) // Don't call too soon
				return;
			$http.get(getUrl("/api/1.0/networks/" + $scope.network.id + "/")).success(function (data)
			{
				$scope.network.currentNetwork = data;
			});
		};

		if ($routeParams.publisherId)
		{
			$http.get(getUrl("/api/1.0/publishers/" + $routeParams.publisherId + "/")).success(function (data)
			{
				$scope.currentPublisher = data;
				if ($scope.currentPublisher.passback_display_tag_html != null)
				{
					ctrl.isDisplayPassback = true;
				}
				if ($scope.currentPublisher.parent_account_id != null)
				{
					$scope.network.id = $scope.currentPublisher.parent_account_id;
					$scope.allowedFormats.push({type: null, description: "Inherit ("+$scope.getParentFormatDescription()+")"});
					$scope.getNetworkData();
					ctrl.isDisplayPassback = false;
				}
			});
		}  else if ($routeParams.networkId != null)
		{
			$scope.network.id = $routeParams.networkId;
			$scope.getNetworkData();
		}


		$scope.hasRole = function (roleName)
		{
			return $user.hasRole(roleName);
		};

		$scope.createPublisher = function ()
		{
			$scope.disableSubmit = true;

			$scope.currentPublisher.site = $scope.currentPublisherSiteSettings;

			if ($scope.network.id != null)
			{
				$scope.currentPublisher.parent_account_id = $scope.network.id;
			}

			if (ctrl.isDisplayPassback == false)
				$scope.currentPublisher.passback_display_tag_html = null;

			$scope.errorMsg = "";

			$http.post(getUrl("/api/1.0/publishers/"), $scope.currentPublisher).success(function (data)
			{

				ctrl.publisherForm.$setSubmitted();
				$scope.disableSubmit = false;

				$log.log(data);

				if ($scope.network.id == null)
				{
					$location.path("/publishers/"+data.id);
				}
				else
				{
					$location.path("/network-"+$scope.network.id+"/publishers/");
				}
			}).error(function (data)
			{
				$scope.disableSubmit = false;

				var msgLower = "";
				if (typeof(data.message)!= "undefined"  && data.message != null)
					msgLower = data.message.toString().toLowerCase();

				if (msgLower.indexOf("duplicate name error") >= 0)
				{
					if (msgLower.indexOf("openx") >= 0)
					{
						$scope.errorMsg = "Duplicate publisher name in OpenX. Please choose a different name.";
					}
					else
					{
						$scope.errorMsg = "Duplicate publisher name. Please choose a different name.";
					}
				}

				else if (msgLower.indexOf("invalid url") >= 0)
				{
					$scope.errorMsg = "Invalid URL for site. URLs should start with http:// or https:// followed by domain and path";
				}
				else if (msgLower.indexOf("name length") >= 0)
					$scope.errorMsg = "Please limit publisher name to 255 characters.";
				else
				if (msgLower.indexOf("openx") >= 0)
				{
					if (msgLower.indexOf("restclientexception") >= 0)
					{
						if (msgLower.indexOf("could not extract response") >= 0)
						{
							$scope.errorMsg = "OpenX returned unexpected response. Check if OpenX is under temporary maintenance."
						}
						else
							$scope.errorMsg = "Unknown REST error communicating with OpenX";
					}
					else
						$scope.errorMsg = "Unknown OpenX communication error"
				}
				else
					$scope.errorMsg = "Bad publisher values. Please check";
			});
		}

		$scope.updatePublisher = function ()
		{
			$scope.disableSubmit = true;
			var publisherId = $scope.currentPublisher.id;
			$log.log('Update publisher pressed: ' + publisherId);

			$scope.currentPublisher.site = $scope.currentPublisherSiteSettings;
			if (ctrl.isDisplayPassback == false)
				$scope.currentPublisher.passback_display_tag_html = null;

			$scope.errorMsg = '';

			$http.put(getUrl("/api/1.0/publishers/" + publisherId), $scope.currentPublisher).success(function (data)
			{

				ctrl.publisherForm.$setSubmitted();
				$scope.disableSubmit = false;

				$log.log(data);
				if ($scope.network.id == null)
				{
					$location.path("/publishers/");
				}
				else
				{
					$location.path("/network-"+$scope.network.id+"/publishers/");
				}
			}).error(function (data, status, headers, config)
			{
				$scope.disableSubmit = false;
				var msgLower = "";
				if (typeof(data.message) != "undefined"  && data.message != null)
					msgLower = data.message.toString().toLowerCase();
				if (msgLower.indexOf("duplicate name error") >= 0)
				{
					if (msgLower.indexOf("openx") >= 0)
					{
						$scope.errorMsg = "Duplicate publisher name in Openx. Please choose a different name.";
					}
					else
					{
						$scope.errorMsg = "Duplicate publisher name. Please choose a different name.";
					}
				}
				else if (msgLower.indexOf("invalid url") >= 0)
				{
					$scope.errorMsg = "Invalid URL for site. URLs should start with http:// or https:// followed by domain and path";
				}
				else if (msgLower.indexOf("name length") >= 0)
					$scope.errorMsg = "Please limit publisher name to 255 characters.";
				else
				if (msgLower.indexOf("openx") >= 0)
				{
					if (msgLower.indexOf("restclientexception") >= 0)
					{
						if (msgLower.indexOf("could not extract response") >= 0)
						{
							$scope.errorMsg = "Object creation prevented by OpenX returning unexpected response. Check if OpenX is under temporary maintenance."
						}
						else
							$scope.errorMsg = "Unknown REST error communicating with OpenX";
					}
					else
						$scope.errorMsg = "Unknown OpenX communication error"
				}
				else
					$scope.errorMsg = "Bad publisher values. Please check";
			});
		}

		$scope.onPublisherFilter = function (data)
		{
			var publishers = [];

			for (var i = 0; i < data.publishers.length; i++)
			{
				publishers.push({value: data.publishers[i].name});
			}

			return publishers;
		};

		$scope.isNewPublisher = function ()
		{
			return typeof($scope.currentPublisher.id) == "undefined";
		}

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

			if (ctrl.publisherForm.$dirty && !ctrl.publisherForm.$submitted)
			{
				var confirmLocationChange = confirm("You have unsaved information. Click 'OK' to leave the current page or click 'Cancel' to stay on this page.");
				if (!confirmLocationChange)
				{
					event.preventDefault();
				}
			}
		});


	}]);
