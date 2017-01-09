wahwahApp.controller('UserEditController', ['$scope', '$http', '$routeParams', '$timeout', '$flashMessage', '$user', function ($scope, $http, $routeParams, $timeout, $flashMessage, $user)
{

	$scope.editUser = {};
	$scope.editUserId = $routeParams.userId;

	$scope.roles = ["PUBLISHER_ADMIN", "PUBLISHER_USER", "PLAYER_ADMIN", "ANALYTICS"];

	$scope.user = {roles: []};

	$scope.publisherQuery = getUrl("/api/1.0/accounts/query/%QUERY");

	$scope.hasRole = function (roleName)
	{
		return $user.hasRole(roleName);
	};

	$scope.publisherNameInvalid = false;

	$scope.showProfileUpdatedAlert = false;
	$scope.showRolesUpdatedAlert = false;
	$scope.showPublisherNameInvalidAlert = false;
	$scope.showProfileCreatedAlert = false;

	$scope.roleSelections = {
		"USER": false,
		"DEVELOPER": false,
		"SUPER_USER": false,
		"PUBLISHER_ADMIN": false,
		"PLAYER_ADMIN": false,
		"TOOLBAR_PUBLISHER": false,
		"ANALYTICS": false,
		"PUBLISHER_USER": false,
		"INTERNAL_USER": false,
        "NETWORK_USER": false,
        "NETWORK_ADMIN": false
	};

	var flashMessage = $flashMessage.getMessage();

	$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
	{

		if (flashMessage == "USER_CREATED")
		{
			$scope.showProfileCreatedAlert = true;

			$timeout(function ()
			{
				$scope.showProfileCreatedAlert = false;
			}, 10000);
		}

		$scope.user = data;

        if ($scope.hasRole('NETWORK_ADMIN') || $scope.hasRole('INTERNAL_USER')){
            $scope.roles.unshift("NETWORK_ADMIN");
        }

		if ($scope.hasRole('SUPER_USER'))
		{
			$scope.roles.unshift("SUPER_USER", "INTERNAL_USER", "ADVERTISING_ADMIN");
			$scope.roles.push("TOOLBAR_PUBLISHER");
		}

		if ($scope.hasRole('DEVELOPER'))
		{
			$scope.roles.unshift("DEVELOPER");
		}
	});

	var userId = $routeParams.userId;


	$http.get(getUrl("/api/1.0/users/" + userId)).success(function (data)
	{
		$scope.editUser = data;
		$scope.publisherName = data.accountName;

		$scope.applyRolesFromUser();
	});

	$scope.displayRole = function (roleType)
	{

		switch (roleType)
		{
			case "USER":
				return "User";
			case "SUPER_USER":
				return "Super User";
			case "PLAYER_ADMIN":
				return "Player Administrator";
			case "PUBLISHER_ADMIN":
				return "Publisher Administrator";
			case "TOOLBAR_PUBLISHER":
				return "Toolbar Publisher";
			case "ANALYTICS":
				return "Analytics Access";
			case "DEVELOPER":
				return "Software Engineer";
			case "PUBLISHER_USER":
				return "Publisher User Level Access";
			case "INTERNAL_USER":
				return "Internal User Level Access";
			case "ADVERTISING_ADMIN":
				return "Advertising Administrator";
            case "NETWORK_ADMIN":
                return "Red Panda Network Admin";
		}
	};

	$scope.applyRolesFromUser = function ()
	{
		for (var i = 0; i < $scope.roles.length; i++)
		{
			var role = $scope.roles[i];
			$scope.roleSelections[role] = false;
		}
		for (var i = 0; i < $scope.editUser.roles.length; i++)
		{
			var role = $scope.editUser.roles[i];

			$scope.roleSelections[role] = true;
		}
	};


	$scope.onPublisherFilter = function (data)
	{
		var publishers = [];

		var accounts = data.publishers;

		for (var i = 0; i < accounts.length; i++)
		{
			var account = accounts[i];
			if (account.account_type == "NETWORK" ||
				(account.account_type == "PUBLISHER" && account.redpanda_publisher_creator_id == null && account.parent_account_id == null))
			{
				publishers.push({value: account.name});
			}
		}

		return publishers;
	};

	$scope.saveRoles = function ()
	{

		$scope.editUser.roles = [];

		// Apply Selections
		for (var roleName in $scope.roleSelections)
		{
			var isSelected = $scope.roleSelections[roleName];

			if (roleName == "USER")
			{
				isSelected = true;
			}

			if ($scope.hasRole('SUPER_USER') && isSelected)
			{
				$scope.editUser.roles.push(roleName);
			}

			if ($scope.hasRole('PUBLISHER_ADMIN') && (roleName != "DEVELOPER" && roleName != "SUPER_USER" && roleName != "TOOLBAR_PUBLISHER" && roleName != "INTERNAL_USER") && isSelected)
			{
				$scope.editUser.roles.push(roleName);
			}

			if ($scope.hasRole('INTERNAL_USER') && (roleName != "DEVELOPER" && roleName != "SUPER_USER" && roleName != "TOOLBAR_PUBLISHER" ) && isSelected)
			{
				$scope.editUser.roles.push(roleName);
			}

			if ($scope.hasRole('USER') && roleName == "USER" && isSelected)
			{
				$scope.editUser.roles.push(roleName);
			}

			if (roleName == "ANALYTICS" && isSelected)
			{
				$scope.editUser.roles.push(roleName);
			}
		}

		// Correct Selections
		var isSuperUser = $scope.editUser.roles.indexOf("SUPER_USER") >= 0;
		var isPublisherAdmin = $scope.editUser.roles.indexOf("PUBLISHER_ADMIN") >= 0;
		var isPlayerAdmin = $scope.editUser.roles.indexOf("PLAYER_ADMIN") >= 0;
		var isToolbarPublisher = $scope.editUser.roles.indexOf("TOOLBAR_PUBLISHER") >= 0;
		var hasAnalyticsAccess = $scope.editUser.roles.indexOf("ANALYTICS") >= 0;
		var isPublisherUser = $scope.editUser.roles.indexOf("PUBLISHER_USER") >= 0;
		var isInternalUser = $scope.editUser.roles.indexOf("INTERNAL_USER") >= 0;
		var isDeveloperUser = $scope.editUser.roles.indexOf("DEVELOPER") >= 0;
		var isAdvertisingAdmin = $scope.editUser.roles.indexOf("ADVERTISING_ADMIN") >= 0;
		var isNetworkAdmin = $scope.editUser.roles.indexOf("NETWORK_ADMIN") >= 0;


		if (isDeveloperUser && !isSuperUser)
		{
			isSuperUser = true;
			$scope.editUser.roles.push("SUPER_USER");
		}

		if (isSuperUser && !isInternalUser)
		{
			isInternalUser = true;
			$scope.editUser.roles.push("INTERNAL_USER");
		}


		if (isInternalUser && !isPlayerAdmin)
		{
			isPlayerAdmin = true;
			$scope.editUser.roles.push("PLAYER_ADMIN");
		}

		if (isInternalUser && !isAdvertisingAdmin)
		{
			isAdvertisingAdmin = true;
			$scope.editUser.roles.push("ADVERTISING_ADMIN");
		}

		if (isInternalUser && !hasAnalyticsAccess)
		{
			hasAnalyticsAccess = true;
			$scope.editUser.roles.push("ANALYTICS");
		}

		if (isInternalUser && !isPublisherAdmin)
		{
			isPublisherAdmin = true;
			$scope.editUser.roles.push("PUBLISHER_ADMIN");
		}

		if (isPublisherAdmin && !isPublisherUser)
		{
			isPublisherUser = true;
			$scope.editUser.roles.push("PUBLISHER_USER");
		}

		if (isPlayerAdmin && isAdvertisingAdmin && isPublisherAdmin && !isInternalUser)
		{
			isInternalUser = true;
			$scope.editUser.roles.push("INTERNAL_USER");
		}

		if (isSuperUser && !isToolbarPublisher)
		{
			isToolbarPublisher = true;
			$scope.editUser.roles.push("TOOLBAR_PUBLISHER");
		}

		if (isInternalUser && !isNetworkAdmin)
		{
			isToolbarPublisher = true;
			$scope.editUser.roles.push("NETWORK_ADMIN");
		}

		$scope.applyRolesFromUser();

		$http.put(getUrl("/api/1.0/users/" + $scope.editUserId + "/roles"), $scope.editUser.roles).success(function (data)
		{

			$scope.userEditForm.roleForm.$setSubmitted();

			$scope.editUser = data;
			$scope.editUserName = data.accountName;

			$scope.applyRolesFromUser();

			$scope.showRolesUpdatedAlert = true;

			$timeout(function ()
			{
				$scope.showRolesUpdatedAlert = false;
			}, 10000);
		})

	}

	$scope.savePersonalInformation = function ()
	{

		$scope.publisherNameInvalid = false;

		var updatedProfile = {
			firstName: $scope.editUser.firstName,
			lastName: $scope.editUser.lastName,
			emailAddress: $scope.editUser.emailAddress,
			accountName: $scope.editUser.accountName,
			loginEnabled: $scope.editUser.loginEnabled
		};

		$http.put(getUrl("/api/1.0/users/" + $scope.editUserId + "/profile"), updatedProfile).success(function (data)
		{

			$scope.userEditForm.profileForm.$setSubmitted();

			$scope.showProfileUpdatedAlert = true;

			$timeout(function ()
			{
				$scope.showProfileUpdatedAlert = false;
			}, 10000);

			$scope.editUser = data;
			$scope.publisherName = data.accountName;

			$scope.applyRolesFromUser();
		}).error(function (data)
		{

			if (data.message == "Publisher with name: " + updatedProfile.publisherName + " does not exist")
			{

				$scope.showPublisherNameInvalidAlert = true;

				$timeout(function ()
				{
					$scope.showPublisherNameInvalidAlert = false;
				}, 15000);

				$scope.publisherNameInvalid = true;
			}
		});
	}


	$scope.loginSettingApproved = true;
	$scope.showLoginChangeAlert = false;
	$scope.loginEnabledChangeConfirm = function ()
	{
		if ($scope.editUser.loginEnabled == false)
		{
			$scope.loginSettingApproved = false;
			$scope.showLoginChangeAlert = true;
		}
		else
		{
			$scope.loginSettingApproved = true;
			$scope.showLoginChangeAlert = false;
		}
	}

	$scope.confirmLoginChange = function ()
	{
		$scope.loginSettingApproved = true;
		$scope.showLoginChangeAlert = false;
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
		
		var isDirty = $scope.userEditForm.$dirty;
		var isSubmitted = $scope.userEditForm.$submitted;

		if (isDirty && !isSubmitted)
		{
			var confirmLocationChange = confirm("You have unsaved information. Click 'OK' to leave the current page or click 'Cancel' to stay on this page.");
			if (!confirmLocationChange)
			{
				event.preventDefault();
			}
		}
	});


}]);
