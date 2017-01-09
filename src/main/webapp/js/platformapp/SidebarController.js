/**
 * Created by Justin on 5/17/2014.
 */

wahwahApp.controller('SidebarController', ['$scope', '$http', '$location', '$user', function ($scope, $http, $location, $user)
{
	$scope.page = "";
	setPage();

	$scope.showAdOpsMenu = false;

	$scope.hasRole = function (roleName)
	{
		return $user.hasRole(roleName);
	};

	$scope.$on("$locationChangeSuccess", function ()
	{
		setPage();
	});

	$user.getUserPromise().then(function(user){
		$scope.user = user;
	});

	$scope.toggleAdOpsMenu = function(){

		$scope.showAdOpsMenu = !$scope.showAdOpsMenu;

		$scope.markAdminMenu = false;
		$scope.showAdminMenu = false;

        if(!$scope.showAdOpsMenu) {

            $scope.markAdOpsMenu = false;

            if ($scope.page == "Unknown Ad Units") {
                $scope.markAdOpsMenu = true;
            }

            if ($scope.page == "Unmapped Creatives") {
                $scope.markAdOpsMenu = true;
            }

			if ($scope.page == "Line Item Mapping") {
				$scope.markAdOpsMenu = true;
			}
        } else {
            $scope.markAdOpsMenu = true;
        }

	};

	$scope.toggleAdminMenu = function(){

		$scope.showAdminMenu = !$scope.showAdminMenu;

		$scope.markAdOpsMenu = false;
		$scope.showAdOpsMenu = false;

		if(!$scope.showAdminMenu) {

			$scope.markAdminMenu = false;

			if ($scope.page == "API Keys") {
				$scope.markAdminMenu = true;
			}

			if ($scope.page == "Audit Log") {
				$scope.markAdminMenu = true;
			}

			if ($scope.page == "Bulk Update") {
				$scope.markAdminMenu = true;
			}

			if ($scope.page == "Toolbar Publisher") {
				$scope.markAdminMenu = true;
			}
			if ($scope.page == "Product Versions") {
				$scope.markAdminMenu = true;
			}
		} else {
			$scope.markAdminMenu = true;
		}

	};

	function setPage()
	{

		$scope.page = "";

		$scope.markAdOpsMenu = false;
		$scope.showAdOpsMenu = false;

		$scope.markAdminMenu = false;
		$scope.showAdminMenu = false;

		if ($location.path() == "/overview" || $location.path() == "/dashboard")
		{
			$scope.page = "Overview";
		}

		if ($location.path().indexOf("/sites") == 0)
		{
			$scope.page = "Sites";
		}

		if ($location.path().indexOf("/publisher-") == 0 && $location.path().endsWith("/sites/")){
			$scope.page = "Sites";
		}

		if ($location.path().indexOf("/custom-reports") == 0)
		{
			$scope.page = "Custom-Reports";
		}

		if ($location.path().indexOf("/tagsettings") == 0){
			$scope.page = "Sites";
		}

		if ($location.path().indexOf("/toolbar-publisher") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
			$scope.page = "Toolbar Publisher";
		}

		if ($location.path().indexOf("/users") == 0)
		{
			$scope.page = "Users";
		}

		if ($location.path().indexOf("/api-keys") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
			$scope.page = "API Keys";
		}

		if ($location.path().indexOf("/audit-log") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
			$scope.page = "Audit Log";
		}

		if ($location.path().indexOf("/products") == 0)
		{
			$scope.page = "Products";
		}
		if ($location.path().indexOf("/products/bulkupdate") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
			$scope.page = "Bulk Update";
		}

		if ($location.path().indexOf("/publishers") == 0)
		{
			$scope.page = "Publishers";
		}

		if ($location.path().indexOf("/product-versions") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
			$scope.page = "Product Versions";
		}

		if ($location.path().indexOf("/admin") == 0)
		{
			$scope.markAdminMenu = true;
			$scope.showAdminMenu = true;
		}

		if ($location.path().indexOf("/admin/client-features") == 0)
		{
			$scope.page = "Client Features";
		}

		if($location.path().indexOf("/admin/upgrade") == 0)
		{
			$scope.page = "Upgrade Accounts";
		}

		if ($location.path().indexOf("/adops") == 0)
		{
			$scope.showAdOpsMenu = true;
			$scope.markAdOpsMenu = true;
		}

        if ($location.path().indexOf("/adops/unmapped-creatives") == 0)
        {
            $scope.page = "Unmapped Creatives";
        }

		if ($location.path().indexOf("/adops/unknown-ad-units") == 0)
		{
			$scope.page = "Unknown Ad Units";
		}

		if ($location.path().indexOf("/adops/line-item-mapping") == 0)
		{
			$scope.page = "Line Item Mapping";
		}

		if ($location.path().indexOf("/demand-source") == 0){
			$scope.page = "Demand Sources";
		}

		if ($location.path().indexOf("/networks") == 0){
			$scope.page = "Networks";
		}
	}

}]);
