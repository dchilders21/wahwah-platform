/**
 * Created by Brian Bober on 12/17/2014.
 */

wahwahApp.controller('NavbarController', ['$scope', '$http', '$location', '$cookies', '$user', '$timeout', 'SessionService', function ($scope, $http, $location, $cookies, $user, $timeout, SessionService)
{
	$scope.$parent.xsrfToken = $cookies.get('XSRF-TOKEN'); // Store in $parent so that every controller doesn't need $cookies


	$scope.searchData = "";
	$scope.searchResults = {};
	$scope.showSearchResults = false;

	$scope.environment = null;

	$scope.showLogoutDialog = false;

	$scope.user = {roles: []};

	$user.getUserPromise().then(function (data)
	{
		$scope.user = data;
	});

	$scope.hasRole = function (roleName)
	{
		return $user.hasRole(roleName)
	};


	$http.get(getUrl("/api/1.0/system/environment/")).success(function (data)
	{
		$scope.environment = data;
	});

	function waitForSessionTimeoutCheck(){
		SessionService.getSessionTimeoutPromise().then(function(timeoutObject){

			var currentTime = moment(timeoutObject.current);
			var expiredTime = moment(timeoutObject.expiresAt);

			var difference = currentTime.diff(expiredTime,'minutes');

			$http.post(getUrl("/api/1.0/users/ping")).success(function(){
				waitForSessionTimeoutCheck();
			});

			if(difference >= 20 && !$scope.showLogoutDialog){

				$scope.showLogoutDialog = true;

				bootbox.confirm({
					title: 'Session Expired',
					message: 'Your session has expired. You can log out now or stay logged in if you wish to continue working.',
					buttons: {
						'cancel': {
							label: 'Logout',
							className: 'btn-default pull-left'
						},
						'confirm': {
							label: 'Stay Logged In',
							className: 'btn-danger pull-right'
						}
					},
					callback: function(result) {

						$scope.$apply(function(){

							$scope.showLogoutDialog = false;

							if (result) {

								$http.post(getUrl("/api/1.0/users/ping")).success(function(){
									SessionService.updateLastRequestTime();
									waitForSessionTimeoutCheck();
								});

							} else {
								window.location = getUrl("/useraccount/logout");
							}

						});
					}
				});
			}
		});
	}

	waitForSessionTimeoutCheck();

	$scope.searchKeypress = function(event)
	{
		if (event.keyCode == 13)
		{
			$scope.search();
		}
	};

	$scope.search = function ()
	{
		$http.get(getUrl("/api/1.0/search/") + $scope.searchData).success(function (data)
		{

			// do something with data
			$scope.searchResults = data;
			$scope.showSearchResults = true;
		});
	};

	$scope.searchResultsClose = function ()
	{
		$scope.showSearchResults = false;
	};

	$('.prop-open').click(function (e)
	{
		e.stopPropagation();
	});

}]);
