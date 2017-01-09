wahwahApp.controller('PublisherListController', ['$scope', '$http', '$routeParams', '$timeout', '$location', '$user', function ($scope, $http, $routeParams, $timeout, $location, $user)
{


	$scope.xsrfToken = $scope.$parent.xsrfToken;

	$scope.options = {};
	$scope.options.seeAll = false;

	$scope.network = {};
	$scope.network.id = null;

    $scope.showCog = false;

	$scope.universalNetworkTagDownloadUrl = getUrl("/api/1.0/networks/universaltag/txt/" + $scope.network.id) + "?xsrf_token=" + $scope.xsrfToken;

	$scope.getPublisherUniversalPublisherTagDownloadUrl = function(publisherId)
	{
		return getUrl("/api/1.0/publishers/universaltag/txt/" + publisherId) + "?xsrf_token=" + $scope.xsrfToken;
	};

	$scope.getPublisherAllTagDownloadUrl = function (publisherId)
	{
		// Get ALL sites, not just page 1
		return getUrl("/api/1.0/publishers/tags/txt/") + publisherId + "?xsrf_token=" + $scope.xsrfToken;
	};


	$scope.getNetworkUniversalTagDownloadUrl = function(networkId) {
        return getUrl("/api/1.0/networks/universaltag/txt/" + networkId) + "?xsrf_token=" + $scope.xsrfToken;
    };

    $scope.getNetworkAllTagDownloadUrl = function (networkId) {
        // Get ALL sites, not just page 1
        return getUrl("/api/1.0/networks/tags/txt/") + networkId + "?xsrf_token=" + $scope.xsrfToken;
    };

    if ($routeParams.networkId) {
        $scope.network.id = $routeParams.networkId;
        $http.get(getUrl("/api/1.0/networks/" + $scope.network.id + "/")).success(function (data) {
            $scope.network.currentNetwork = data;

            if (data.is_single_publisher) { // Redirect to Site List
                $location.path("publisher-" + data.single_publisher_id + "/sites/");
            }
        });
    }

    $scope.user = {roles: []};

    $scope.showConfirmDeleteAlert = false;

    $scope.errorMsg = "";
    $scope.showError = false;

    $scope.hasRole = function (roleName) {
        return $user.hasRole(roleName);
    };


    $user.getUserPromise().then(function (user) {
        $scope.user = user;
    });

    $http.get(getUrl("/api/1.0/users/current/")).success(function (data) {
        $scope.user = data;
    });

    $scope.users = [];

    $scope.currentPageSize = 25;

    $scope.refreshPublishers = function () {
        loadPublishers(0, $scope.currentPageSize);
    };

    function loadPublishers(pageNumber, pageSize, seeAll) {
        if (typeof(seeAll) == "undefined")
            seeAll = false;
        var networkIdStr = ($scope.network.id == null) ? "" : "network-" + $scope.network.id + "/";
        var seeAllStr = ($scope.network.id != null) ? "" : "&seeAll=" + seeAll;
        $http.get(getUrl("/api/1.0/publishers/" + networkIdStr + "?page=" + pageNumber + "&size=" + pageSize + seeAllStr)).success(function (data) {
            $scope.publishers = data.publishers;
            $scope.page = {};
            $scope.page.current = data.page_current;
            $scope.page.size = data.page_size;
            $scope.page.total = data.page_totalcount;

            $scope.pages = [];

            for (var i = 0; i < $scope.page.total; i++) {
                var page = {};
                page.index = i;
                page.number = i + 1;
                $scope.pages.push(page);
            }
        });
    }

    $scope.openPage = function (pageIndex) {

        if ($scope.page.current == 0 && pageIndex == "previous") {
            return;
        }

        if ($scope.page.current == $scope.page.total - 1 && pageIndex == "next") {
            return;
        }

        if (pageIndex == "previous") {
            pageIndex = $scope.page.current - 1;
        }

        if (pageIndex == "next") {
            pageIndex = $scope.page.current + 1;
        }

        loadPublishers(pageIndex, $scope.currentPageSize);
    };

    $scope.refreshPublishers();

    $scope.deletePublisher = function (pub) {
        $scope.showConfirmDeleteAlert = true;
        $scope.pubToDelete = pub;
    };

    $scope.hideDeleteWarning = function () {
        $scope.showConfirmDeleteAlert = false;
    };

    $scope.performDelete = function () {
        $scope.showCog = true;
        $http.delete(getUrl("/api/1.0/publishers/" + $scope.pubToDelete.id)).success(function () {
            $scope.showConfirmDeleteAlert = false;
            $scope.showCog = false;
            $scope.refreshPublishers();
        }).error(function () {
            $scope.showConfirmDeleteAlert = false;
            $scope.showError = true;
            $scope.errorMsg = "Unable to delete " + $scope.pubToDelete.name;
            $scope.pubToDelete = null;
            $timeout(function () {
                $scope.showError = false;
            }, 15000);
        });
    };

    $scope.selectPublisher = function (pub) {
        $location.path("/publisher-" + pub.id + "/sites/");
    };

    $scope.changeSeeAll = function () {
        if (typeof($scope.page) == "undefined")
            return;
        loadPublishers($scope.page.current, $scope.currentPageSize, $scope.options.seeAll);
    }
}]);