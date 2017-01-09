wahwahApp.controller('SiteController', ['$scope', '$http', '$routeParams', '$location', '$timeout', '$flashMessage', '$route',
	function ($scope, $http, $routeParams, $location, $timeout, $flashMessage, $route)
	{

		$scope.publisherQuery = getUrl("/api/1.0/publishers/query/%QUERY");

		$scope.disableSubmit = false;

		$scope.parentNetwork = null;

		$scope.user = {roles: []};


		$scope.showSaveSuccess = false;
		$scope.showSaveError = false;

		$scope.errorMsg = "";

		$scope.currentSite = {
			site_language: "en",
			site_country: "UNITED_STATES",
			traffic_estimate: "_0_TO_1MM",
			site_country: "UNITED_STATES",
			site_type: "ENTERTAINMENT",
			custom_site: false
		};

		$scope.initialPublisherId = null;

		if ($routeParams.publisherId)
		{
			$scope.currentSite.publisher_id = $routeParams.publisherId;
			$scope.initialPublisherId = $scope.currentSite.publisher_id;

			$http.get(getUrl("/api/1.0/publishers/" + $routeParams.publisherId + "/")).success(function (data)
			{
				$scope.onPublisherFilter( {publishers: [ data ] } );
				$scope.currentSite.account_name = data.name;

				if (data.parent_account_id != null)
				{
				    if(data.parent_account_type == "NETWORK") {
                        $http.get(getUrl("/api/1.0/networks/" + data.parent_account_id)).success(function (data) {
                            $scope.parentNetwork = data;
                        });
                    }

                    if(data.parent_account_type == "REPORTING_PRO") {
                        $http.get(getUrl("/api/1.0/reporting-pro-accounts/" + data.parent_account_id)).success(function (data) {
                            $scope.parentNetwork = data;
                        });
                    }
				}
			});
		}

		var message = $flashMessage.getMessage();

		if (message == "SITE_CREATED")
		{
			$scope.showSaveSuccess = true;

			$timeout(function ()
			{
				$scope.showSaveSuccess = false;
			}, 10000);
		}

		$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
		{
			$scope.user = data;

			if ($scope.hasRole('SUPER_USER'))
			{
				$scope.currentSite['@type'] = 'InternalSite';
			}
			else
			{
				$scope.currentSite['@type'] = 'SiteModel';
			}
		});

		if ($routeParams.siteId)
		{
			$http.get(getUrl("/api/1.0/sites/" + $routeParams.siteId + "/")).success(function (data)
			{
				$scope.currentSite = data;
			});
		}

		$scope.hasRole = function (roleName)
		{
			return window.hasRole($scope.user.roles, roleName);
		};

		$scope.siteLanguages = [{
			id: 'en',
			name: 'English'
		}, {
			id: 'es',
			name: 'Spanish'
		}, {
			id: 'pt',
			name: 'Portugese'
		}];

		$scope.siteCountries = [{
			id: 'UNITED_STATES',
			name: 'United States'
		}, {
			id: 'SPAIN',
			name: 'Spain'
		}, {
			id: 'BRAZIL',
			name: 'Brazil'
		}];

		$scope.trafficEstimates = [{

			id: '_0_TO_1MM',
			name: 'Up to 1M'
		}, {
			id: '_1MM_TO_5MM',
			name: '1MM - 5MM'
		}, {
			id: '_5MM_TO_10MM',
			name: '5MM - 10MM'
		}, {
			id: '_10MM_TO_20MM',
			name: '10MM - 20MM'
		}, {
			id: '_20MM_PLUS',
			name: '20 Million+'
		}/*, {
		 id: '_1M_PLUS',
		 name: '1 Million+'
		 } */];

		$scope.siteTypes = [{
			id: 'ENTERTAINMENT',
			name: 'Entertainment'
		}, {
			id: 'WOMENS_LIFESTYLE',
			name: 'Womens Lifestyle'
		}, {
			id: 'MENS_LIFESTYLE',
			name: 'Mens Lifestyle'
		}, {
			id: 'NEWS_AND_BUSINESS',
			name: 'News & Business'
		}, {
			id: 'SPORTS',
			name: 'Sports'
		}];

		$scope.createSite = function ()
		{
			$scope.errorMsg = "";

			$scope.disableSubmit = true;

			$scope.showSaveError = false;

			if (typeof($scope.currentSite.publisher_id ) == "undefined" || $scope.currentSite.publisher_id == null)
				$scope.currentSite.publisher_id = publisherNameIdMap[$scope.currentSite.account_name];

			$http.post(getUrl("/api/1.0/sites"), $scope.currentSite).success(function (data)
			{
				$scope.disableSubmit = false;

				$scope.siteForm.$setSubmitted();
				// Don't flash message for SITE_CREATED anymore
				//$flashMessage.setMessage("SITE_CREATED");

				//$scope.returnToSiteList();
				$location.path("/sites/"+data.id);
			})
				.error(function (data, status, headers, config)
				{
					$scope.disableSubmit = false;
					$scope.showSaveError = true;

					$scope.currentSite.publisher_id = $scope.initialPublisherId;

					var msgLower = "";
					if (typeof(data.message)!= "undefined"  && data.message != null)
						msgLower = data.message.toString().toLowerCase();

					if (msgLower.indexOf("duplicate name error") >= 0)
					{
						if (msgLower.indexOf("openx") >= 0)
						{
							$scope.errorMsg = "Duplicate site name in Openx. Please choose a different name.";
						}
						else
						{
							$scope.errorMsg = "Duplicate site name. Please choose a different name.";
						}
					}
					else if (msgLower.indexOf("invalid url") >= 0)
						$scope.errorMsg = "Invalid URL. URLs should start with http:// or https:// followed by domain and path";
					else if (msgLower.indexOf("name length") >= 0)
						$scope.errorMsg = "Please limit site name to 255 characters.";
					else
						$scope.errorMsg = "Unknown error. Please check site values";

					$timeout(function ()
					{
						$scope.showSaveError = false;
					}, 15000);
				});
		};

		$scope.createSiteAndCreateNew = function ()
		{
			$scope.errorMsg = "";

			$scope.disableSubmit = true;

			$scope.showSaveError = false;

			$http.post(getUrl("/api/1.0/sites"), $scope.currentSite).success(function (data)
			{
				$scope.disableSubmit = false;

				$scope.siteForm.$setSubmitted();
				$flashMessage.setMessage("SITE_CREATED");
				$route.reload();
			})
				.error(function (data, status, headers, config)
				{
					$scope.disableSubmit = false;
					$scope.showSaveError = true;

					$scope.currentSite.publisher_id = $scope.initialPublisherId;

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
					else if (msgLower.indexOf("invalid url") >= 0)
						$scope.errorMsg = "Invalid URL. URLs should start with http:// or https:// followed by domain and path";
					else if (msgLower.indexOf("name length") >= 0)
						$scope.errorMsg = "Please limit site name to 255 characters.";
					else
						$scope.errorMsg = "Unknown error: Please check site values";

					$timeout(function ()
					{
						$scope.showSaveError = false;
					}, 15000);
				});
		}

		$scope.updateSite = function ()
		{
			$scope.errorMsg = "";

			$scope.disableSubmit = true;

			$scope.showSaveError = false;

			var siteId = $scope.currentSite.id;
			console.log('Update site pressed: ' + siteId);

			if (typeof($scope.currentSite["internal_notes"]) == undefined)
			{
				$scope.currentSite["internal_notes"] = null;
			}

			if (typeof($scope.currentSite.publisher_id ) == "undefined" || $scope.currentSite.publisher_id == null)
				$scope.currentSite.publisher_id = publisherNameIdMap[$scope.currentSite.account_name];

			$http.put(getUrl("/api/1.0/sites/" + siteId), $scope.currentSite).success(function ()
			{
				$scope.disableSubmit = false;

				$scope.siteForm.$setSubmitted();
				$scope.showSaveSuccess = true;

				$timeout(function ()
				{
					$scope.showSaveSuccess = false;
				}, 10000);


			}).error(function (data, status, headers, config)
			{
				$scope.disableSubmit = false;
				$scope.showSaveError = true;

				$scope.currentSite.publisher_id = $scope.initialPublisherId;

				var msgLower = "";
				if (typeof(data.message)!= "undefined"  && data.message != null)
					msgLower = data.message.toString().toLowerCase();

				if (msgLower.indexOf("duplicate name error") >= 0)
				{
					if (msgLower.indexOf("openx") >= 0)
					{
						$scope.errorMsg = "Duplicate site name in Openx. Please choose a different name.";
					}
					else
					{
						$scope.errorMsg = "Duplicate site name. Please choose a different name.";
					}
				}
				else if (msgLower.indexOf("invalid url") >= 0)
					$scope.errorMsg = "Invalid URL. URLs should start with http:// or https:// followed by domain and path";
				else if (msgLower.indexOf("name length") >= 0)
					$scope.errorMsg = "Please limit site name to 255 characters.";
				else
					$scope.errorMsg = "Unknown error. Please check site values";

				$timeout(function ()
				{
					$scope.showSaveError = false;
				}, 15000);
			});
		}

		var publisherNameIdMap = [];
		$scope.onPublisherFilter = function (data)
		{
			var publishers = [];
			publisherNameIdMap = [];

			for (var i = 0; i < data.publishers.length; i++)
			{
				var name = data.publishers[i].name;
				publishers.push({value: name});
				publisherNameIdMap[name] = data.publishers[i].id;
			}

			return publishers;
		};

		$scope.cancel = function ()
		{
			$scope.returnToSiteList();
		};

		$scope.returnToSiteList = function()
		{
			if ($scope.currentSite.publisher_id == null)
			{
				$location.path("/sites/");
			}
			else
			{
				$location.path("/publisher-" + $scope.currentSite.publisher_id + "/sites/");
			}

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

			if ($scope.siteForm.$dirty && !$scope.siteForm.$submitted)
			{
				var confirmLocationChange = confirm("You have unsaved information. Click 'OK' to leave the current page or click 'Cancel' to stay on this page.");
				if (!confirmLocationChange)
				{
					event.preventDefault();
				}
			}
		});

	}]);
