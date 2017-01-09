wahwahApp.controller('DemandSourceListController', ['$scope', '$http', '$user', '$location', '$filter', function ($scope, $http, $user, $location, $filter){

    $scope.hasRole = function(roleName){
        return $user.hasRole(roleName);
    };

    $user.getUserPromise().then(function(user){
        $scope.user = user;

        if(user.accountType == "FREE" || user.accountType == "REPORTING_PRO"){
            $scope.filters.show_inactive = true;
        }

        reloadDataFromSearch();
    });

    $scope.currentPageSize = 25;

    $scope.dates = {};
    $scope.dates.endDate = moment().format('LL');
    $scope.dates.startDate = moment().subtract(7, 'days').format('LL');
    $scope.dates.todayDate = moment().format('LL');

    $scope.availablePublishers = [];
    $scope.availableSites = [];

    $scope.selectedPublisher = null;
    $scope.selectedSite = null;

    $scope.currentPublisherName = "All Publishers";
    $scope.currentSiteName = "All Sites";

    $scope.filters = {
      show_inactive: false  
    };

    var previousStartDate = $scope.dates.startDate;
    var previousEndDate = $scope.dates.endDate;

    $scope.$watch('dates.startDate', function (newValue)
    {
        if (previousStartDate != newValue)
        {

            var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
            $location.search("start_date", startDate);

            refreshDemandSources();
            previousStartDate = newValue;
        }
    });

    $scope.$watch('dates.endDate', function (newValue)
    {
        if (previousEndDate != newValue)
        {

            var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");
            $location.search("end_date", endDate);

            refreshDemandSources();
            previousEndDate = newValue;
        }

    });

    var onInitialLoad = true;

    function reloadDataFromSearch() {
        var searchParams = $location.search();

        if(searchParams.start_date){
            $scope.dates.startDate = moment(searchParams.start_date).format('LL');
        }

        if (searchParams.end_date)
        {
            $scope.dates.endDate = moment(searchParams.end_date).format('LL');
        }

        if(searchParams.publisher){
            $scope.selectedPublisher = {name: searchParams.publisher};
            $scope.currentPublisherName = searchParams.publisher;
        }

        if(searchParams.site){
            $scope.selectedSite = {name: searchParams.site};
            $scope.currentSiteName = searchParams.site;
        }

        if(onInitialLoad){
            onInitialLoad = false;
            refreshDemandSources();
        }
    }

    $scope.$on('$locationChangeSuccess', function ()
    {
        reloadDataFromSearch();
    });

    $scope.reloadData = function(){
        refreshDemandSources();
    };

    function refreshDemandSources(){

        if($scope.user.accountType == 'FREE' || $scope.user.accountType == "REPORTING_PRO"){
            loadDemandPartners(0,$scope.currentPageSize);
        } else {
            loadPublishers();
        }

    }

    function loadPublishers(){

        var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

        var params = {
            start_date: startDate,
            end_date: endDate
        };

        $http.get('/analytics/api/1.0/custom-reports/dimensions/Publisher',{params:params}).success(function (data) {
            $scope.availablePublishers = [];

            for(var i = 0; i < data.length; i++){
                $scope.availablePublishers.push({name: data[i]});
            }

            loadSites();
        });
    }

    $scope.selectPublisher = function(publisher){
        $scope.selectedPublisher = publisher;

        if(publisher) {
            $scope.currentPublisherName = publisher.name;
            $location.search("publisher",publisher.name);
        } else {
            $scope.curentPublisherName = "All Publishers";
            $location.search("publisher",null);
        }

        refreshDemandSources();
    };

    function loadSites(){

        var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

        var params = {
            start_date: startDate,
            end_date: endDate
        };

        $http.get('/analytics/api/1.0/custom-reports/dimensions/Site',{params:params}).success(function (data) {
            $scope.availableSites = [];

            for(var i = 0; i < data.length; i++){
                $scope.availableSites.push({name: data[i]});
            }

            loadDemandPartners(0,$scope.currentPageSize);
        });
    }

    $scope.selectSite = function(site){
        $scope.selectedSite = site;

        if(site) {
            $scope.currentSiteName = site.name;
            $location.search("site",site.name);
        } else {
            $scope.currentSiteName = "All Sites";
            $location.search("site",null);
        }

        refreshDemandSources();
    };

    function loadDemandPartners(pageNumber, pageSize){

        var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

        var filter = {
            show_inactive: $scope.filters.show_inactive,
            start_date: startDate,
            end_date: endDate,
            dimension_filters: []
        };

        if($scope.selectedPublisher){
            filter.dimension_filters.push({dimension: 'Publisher', value: $scope.selectedPublisher.name});
        }

        if($scope.selectedSite){
            filter.dimension_filters.push({dimension: 'Site', value: $scope.selectedSite.name});
        }

        $http.post(getUrl("/api/2.0/demand-sources/?page=" + pageNumber + "&size=" + pageSize),filter).success(function(data){
            $scope.demandSources = data.demand_sources;

            for(var i = 0; i < data.demand_sources.length; i++){
                data.demand_sources[i].sort_order = i;
            }

            $scope.page = {};
            $scope.page.current = data.page_current;
            $scope.page.size = data.page_size;
            $scope.page.total = data.page_totalcount;

            $scope.pages = [];

            for (var i = 0; i < $scope.page.total; i++)
            {
                var page = {};
                page.index = i;
                page.number = i + 1;
                $scope.pages.push(page);
            }

            $scope.orderChanged = false;
        });
    }

    $scope.setSortOrder = function(demandSource,oldSortOrder){

        if(oldSortOrder.length == 0){
            oldSortOrder = demandSource.old_sort_order;
        } else {
            demandSource.old_sort_order = oldSortOrder;
        }

        if(!demandSource.sort_order || demandSource.sort_order.length == 0 || isNaN(demandSource.sort_order)){
            return;
        }

        demandSource.sort_order = parseInt(demandSource.sort_order);

        var sourceItem = null;
        var targetItem = demandSource;
        
        for(var i = 0; i < $scope.demandSources.length; i++){

            if($scope.demandSources[i] != demandSource && $scope.demandSources[i].sort_order == demandSource.sort_order){
                sourceItem = $scope.demandSources[i];
            }
        }


        if(oldSortOrder < demandSource.sort_order){
            var tempItem = sourceItem;
            sourceItem = targetItem;
            targetItem = tempItem;
        }

        $scope.onItemDrop(sourceItem,targetItem);
    };

    $scope.onItemDrop = function(sourceItem,targetItem){

        var itemsAfterTarget = [];

        sourceItem.sort_order = targetItem.sort_order + 1;
        var currentSortOrder = targetItem.sort_order + 2;

        for(var i = 0; i < $scope.demandSources.length; i++){
            var item = $scope.demandSources[i];

            if(item != sourceItem && item.sort_order > targetItem.sort_order){
                itemsAfterTarget.push(item);
            }
        }

        itemsAfterTarget.sort(function(a,b){
            if(a.sort_order < b.sort_order){
                return -1;
            }

            if(a.sort_order > b.sort_order){
                return 1;
            }

            return 0;
        });

        for(var i = 0; i < itemsAfterTarget.length; i++){
            itemsAfterTarget[i].sort_order = currentSortOrder;
            currentSortOrder++;
        }

        currentSortOrder = 0;

        $scope.demandSources.sort(function(a,b){
            if(a.sort_order < b.sort_order){
                return -1;
            }

            if(a.sort_order > b.sort_order){
                return 1;
            }

            return 0;
        });

        for(var i = 0; i < $scope.demandSources.length; i++){
            $scope.demandSources[i].sort_order = currentSortOrder;
            currentSortOrder++;
        }

        $scope.orderChanged = true;

    };

    $scope.savePriorities = function(){

        var priorities = [];

        var demandSources = $filter('orderBy')($scope.demandSources, 'sort_order', false);

        for(var i = 0; i < demandSources.length; i++){
            var demandSource = demandSources[i];

            priorities.push({
                demand_source_id: demandSource.demand_source_id,
                priority: demandSource.sort_order
            });
        }

        if($scope.selectedPublisher == null && $scope.selectedSite == null){
            $http.put(getUrl("/api/2.0/demand-sources/priorities/network"),priorities).success(function(){
                refreshDemandSources();
            });
        }

        if($scope.selectedPublisher != null && $scope.selectedSite == null){
            $http.put(getUrl("/api/2.0/demand-sources/priorities/publisher/" + $scope.selectedPublisher.name),priorities).success(function(){
                refreshDemandSources();
            });
        }
    };

    $scope.resetPriorities = function(){

        if($scope.selectedPublisher == null && $scope.selectedSite == null){
            $http.post(getUrl("/api/2.0/demand-sources/priorities/network/reset"),{}).success(function(){
                refreshDemandSources();
            });
        }

        if($scope.selectedPublisher != null && $scope.selectedSite == null){
            $http.post(getUrl("/api/2.0/demand-sources/priorities/publisher/" + $scope.selectedPublisher.name + "/reset",{}),priorities).success(function(){
                refreshDemandSources();
            });
        }
    };

    $scope.openPage = function (pageIndex)
    {

        if ($scope.page.current == 0 && pageIndex == "previous")
        {
            return;
        }

        if ($scope.page.current == $scope.page.total - 1 && pageIndex == "next")
        {
            return;
        }

        if (pageIndex == "previous")
        {
            pageIndex = $scope.page.current - 1;
        }

        if (pageIndex == "next")
        {
            pageIndex = $scope.page.current + 1;
        }

        loadDemandPartners(pageIndex, $scope.currentPageSize);
    };

    $scope.selectDemandSource = function (demandSource)
    {
        $location.path("/demand-sources/" + demandSource.demand_source_id);
    };

    $scope.deleteDemandSource = function(demandSource)
    {
        $scope.demandSourceToDelete = demandSource;
        $scope.showConfirmDeleteAlert = true;
        $scope.showDeleteSuccess = false;
    };

    $scope.hideDeleteWarning = function(){
        $scope.showConfirmDeleteAlert = false;
        $scope.demandSourceToDelete = null;
    };

    $scope.performDelete = function(){
        $scope.showConfirmDeleteAlert = false;

        $http.delete(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceToDelete.demand_source_id)).success(function(){
            refreshDemandSources();

            $scope.showDeleteSuccess = true;

            $timeout(function(){
                $scope.showDeleteSuccess = false;
            },5000);
        });
    };
}]);

wahwahApp.controller('DemandSourceConnectionListController', ['$scope','$http','$user', '$routeParams', function($scope, $http, $user, $routeParams){
    $scope.hasRole = function(roleName){
        return $user.hasRole(roleName);
    };

    $user.getUserPromise().then(function(user){
        $scope.user = user;
    });

    $scope.demandSourceId = $routeParams.demandSourceId;

    $http.get(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceId)).success(function(data){
        $scope.demandSource = data;
        refreshDemandSourceConnections();
    });

    $scope.currentPageSize = 25;

    function refreshDemandSourceConnections(){
        loadDemandPartnerConnections(0,$scope.currentPageSize);
    }

    function loadDemandPartnerConnections(pageNumber, pageSize){
        $http.get(getUrl("/api/1.0/demand-sources/" + $scope.demandSource.demand_source_id + "/connections?page=" + pageNumber + "&size=" + pageSize)).success(function(data){
            $scope.demandSourceConnections = data.demand_source_connections;

            for(var i = 0; i < $scope.demandSourceConnections.length; i++){
                $scope.demandSourceConnections[i].connection_details = JSON.parse($scope.demandSourceConnections[i].connection_details_encoded);
            }

            $scope.page = {};
            $scope.page.current = data.page_current;
            $scope.page.size = data.page_size;
            $scope.page.total = data.page_totalcount;

            $scope.pages = [];

            for (var i = 0; i < $scope.page.total; i++)
            {
                var page = {};
                page.index = i;
                page.number = i + 1;
                $scope.pages.push(page);
            }
        });
    }

}]);

wahwahApp.controller('DemandSourceConnectionEditController', ['$scope', '$http', '$location', '$routeParams', '$timeout', '$cookies', function($scope,$http,$location,$routeParams,$timeout,$cookies){

    $scope.isCreating = true;
    $scope.demandSourceId = $routeParams.demandSourceId;

    $scope.connectionTypeFields = {};

    $scope.needsAuthorization = ['GOOGLE_ADSENSE','GOOGLE_ADX'];
    $scope.hasAuthorization = [];

    $cookies.remove("GoogleAuthoriztion");


    var isAdSenseCallback = false;
    var isGoogleAdXCallack = false;

    if($routeParams.google_service == "adsense"){
        isAdSenseCallback = true;
    }

    if($routeParams.google_service == "adx"){
        isGoogleAdXCallack = true;
    }

    if(isAdSenseCallback){
        $scope.hasAuthorization.push('GOOGLE_ADSENSE');
        $scope.connectionTypeFields["adsense_refresh_token"] = $routeParams.token;
    }

    if(isGoogleAdXCallack){
        $scope.hasAuthorization.push('GOOGLE_ADX');
        $scope.connectionTypeFields["adx_refresh_token"] = $routeParams.token;
    }

    $http.get(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceId)).success(function(data){
        $scope.demandSource = data;

        if($routeParams.demandSourceConnectionId){
            $http.get(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceId + "/connections/" + $routeParams.demandSourceConnectionId)).success(function(data){
                $scope.demandSourceConnection = data;
                $scope.connectionTypeFields = data.connection_details;

                loadConnectionTypes();
            });
        } else {
            loadConnectionTypes();
        }
    });

    function loadConnectionTypes(){
        $http.get(getUrl("/api/1.0/demand-sources/connection-types")).success(function(data){
            $scope.connectionTypes = data.connection_types;

            for(var i = 0; i < $scope.connectionTypes.length; i++){
                $scope.connectionTypes[i].connection_metadata = JSON.parse($scope.connectionTypes[i].connection_metadata_encoded);

                var typeKey = $scope.connectionTypes[i].type_key;

                $scope.connectionTypes[i].needs_auth = false;

                if($scope.needsAuthorization.indexOf(typeKey) >= 0){
                    $scope.connectionTypes[i].needs_auth = true;
                }

                if($scope.hasAuthorization.indexOf(typeKey) >= 0){
                    $scope.connectionTypes[i].needs_auth = false;
                }

                if(typeKey == $routeParams.type){
                    $scope.selectedConnectionType = $scope.connectionTypes[i];
                }

                if($scope.demandSourceConnection && typeKey == $scope.demandSourceConnection.type_key){
                    $scope.selectedConnectionType = $scope.connectionTypes[i];
                }

                if($routeParams.google_service == "adx" && typeKey == "GOOGLE_ADX"){
                    $scope.selectedConnectionType = $scope.connectionTypes[i];
                }

                if($routeParams.google_service == "adsense" && typeKey == "GOOGLE_ADSENSE"){
                    $scope.selectedConnectionType = $scope.connectionTypes[i];
                }
            }
        });
    }

    $scope.resetConnectionTypeFields = function(){
        $scope.connectionTypeFields = {};
    };

    $scope.authorize = function(){

        var googleAuthorizationObject = {
            demand_source_id: $scope.demandSourceId,
        };

        if($routeParams.demandSourceConnectionId){
            googleAuthorizationObject.connection_id = $routeParams.demandSourceConnectionId;
        }

        $cookies.putObject("GoogleAuthorization",googleAuthorizationObject);

        if($scope.selectedConnectionType.type_key == "GOOGLE_ADSENSE"){
            window.location = "/google/adsense/login";
        }

        if($scope.selectedConnectionType.type_key == "GOOGLE_ADX"){
            window.location = "/google/adx/login";
        }
    };

    $scope.save = function(){

        if($scope.demandSourceConnection){

            $scope.demandSourceConnection.type_key = $scope.selectedConnectionType.type_key;
            $scope.demandSourceConnection.type_name = $scope.selectedConnectionType.name;
            $scope.demandSourceConnection.connection_details_encoded = JSON.stringify($scope.connectionTypeFields);

            $http.put(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceId + "/connections/" + $scope.demandSourceConnection.id),$scope.demandSourceConnection).success(function(data){
                $scope.demandSourceConnection = data;

                $scope.showSaveSuccess = true;

                $timeout(function(){
                    $scope.showSaveSuccess = false;
                },5000);
            });

        } else {
            var demandSourceConnection = {};
            demandSourceConnection.demand_source_id = $scope.demandSourceId;
            demandSourceConnection.type_key = $scope.selectedConnectionType.type_key;
            demandSourceConnection.type_name = $scope.selectedConnectionType.name;
            demandSourceConnection.connection_details = $scope.connectionTypeFields;

            $http.post(getUrl("/api/1.0/demand-sources/" + $scope.demandSourceId + "/connections"),demandSourceConnection).success(function(){
                $location.path("/demand-source-" + $scope.demandSourceId + "/connections");
            });
        }
    };

}]);
