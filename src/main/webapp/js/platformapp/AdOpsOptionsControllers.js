/**
 * Created by justin on 9/22/15.
 */

wahwahApp.controller('AdOpsOptionsUnmappedCreativeController', ['$scope', '$http', function ($scope, $http)
{

    $http.get("/analytics/api/1.0/admin/unmapped-creatives").success(function (data)
    {
        $scope.lineItems = data;

        var requestedDemandPartners = [];

        $scope.options = {};

        for (var i = 0; i < data.length; i++)
        {

            var demandPartnerId = data[i].demand_partner_id;

            var linked_ad_names = [];

            for (var j = 0; j < data[i].linked_ads.length; j++)
            {
                linked_ad_names.push(data[i].linked_ads[j].name);
            }

            data[i].linked_ad_names = linked_ad_names.join(", ");

            if (requestedDemandPartners.indexOf(demandPartnerId) == -1)
            {
                requestedDemandPartners.push(demandPartnerId);
                loadOptionsForDemandPartner(demandPartnerId);
            }


        }
    });

    function loadOptionsForDemandPartner(demandPartnerId)
    {

        $scope.$watch("options['demand-partner-" + demandPartnerId + "']", function (newValue)
        {
            for (var i = 0; i < $scope.lineItems.length; i++)
            {
                if ($scope.lineItems[i].demand_partner_id == demandPartnerId)
                {
                    $scope.lineItems[i].availableOptions = newValue;
                }
            }
        }, true);

        $http.get("/analytics/api/1.0/admin/demand-partner-line-items/demand-partner-" + demandPartnerId).success(function (optionData)
        {
            $scope.options["demand-partner-" + demandPartnerId] = optionData;
        });
    }

    $scope.save = function ()
    {
        $http.put("/analytics/api/1.0/admin/unmapped-creatives", $scope.lineItems).success(function (data)
        {
            $scope.lineItems = data;
        });
    }

}]);

wahwahApp.controller('AdOpsOptionsUnknownAdUnitController', ['$scope', '$http', function ($scope, $http)
{
    $http.get("/analytics/api/1.0/admin/unknown-adunits").success(function (data)
    {

        $scope.unknownAdUnits = data;

        for (var i = 0; i < data.length; i++)
        {
            data[i].revenue = data[i].revenue_cents / 100;
        }
    });
}]);

wahwahApp.controller('AdOpsOptionsLineItemMappingController',['$scope','$http','$timeout','$window', function($scope, $http, $timeout, $window){

    $scope.adServers = {};
    $scope.adServers['LIVE_RAIL'] = "LiveRail";
    $scope.isSaving = false;

    $scope.demandPartnerMap = {};

    $http.get("/analytics/api/1.0/admin/demand-partners").success(function(data){
        $scope.demandPartners = data;

        for(var i = 0; i < $scope.demandPartners.length; i++){
            $scope.demandPartnerMap[$scope.demandPartners[i].id] = $scope.demandPartners[i];
        }

        reloadLineItems();
    });

    function reloadLineItems(){
        $http.get("/analytics/api/1.0/admin/line-items").success(function(data){
            $scope.lineItems = data;
        });
    }

    $scope.save = function ()
    {
        $scope.isSaving = true;

        $window.scrollTo(0,0);

        $http.put("/analytics/api/1.0/admin/line-items", $scope.lineItems).success(function (data)
        {
            $scope.isSaving = false;
            $scope.lineItems = data;

            $scope.showLineItemsUpdatedAlert = true;

            $timeout(function ()
            {
                $scope.showLineItemsUpdatedAlert = false;
            }, 10000);
        });
    }


}]);

wahwahApp.controller('AdOpsOptionsMonthlyReconciliationController', ['$scope','$http',function($scope,$http){

    var previousMonth = moment().startOf('month').subtract(1,'months');
    var month = previousMonth.month() + 1;
    var year = previousMonth.year();

    $http.get("/analytics/api/1.0/advertiser-data/monthly-data/available-months").success(function(data){
        $scope.availableMonths = data;

        for(var i = 0; i < $scope.availableMonths.length; i++){
            $scope.availableMonths[i].label = $scope.availableMonths[i].month_name + " " + $scope.availableMonths[i].year;

            if($scope.availableMonths[i].year == year && $scope.availableMonths[i].month == month){
                $scope.selectedMonth = $scope.availableMonths[i];
                $scope.refreshData($scope.selectedMonth);
            }
        }
    });

    $scope.refreshData = function(selectedMonth){
        $scope.selectedMonth = selectedMonth;
        refreshData(selectedMonth);
    };

    $scope.$watch("selectedMonth",function(newValue){
        if(newValue) {
            refreshData(newValue);
        }
    });

    function refreshData(selectedMonth) {
        $http.get("/analytics/api/1.0/advertiser-data/monthly-data/" + selectedMonth.year + "/" + selectedMonth.month).success(function (data) {
            $scope.monthlyAdvertiserData = data;

            for (var i = 0; i < data.length; i++) {
                data[i].platform_revenue = data[i].platform_revenue_cents / 100;

                data[i].advertiser_revenue == null;

                if (data[i].advertiser_revenue_cents != null) {
                    data[i].advertiser_revenue = data[i].advertiser_revenue_cents / 100;
                }

            }
        });
    }

    function commitAdvertiserRevenue(){
        for(var i = 0; i < $scope.monthlyAdvertiserData.length; i++){
            $scope.monthlyAdvertiserData[i].advertiser_revenue_cents = $scope.monthlyAdvertiserData[i].advertiser_revenue * 100;
        }
    }

    $scope.preview = function(){

        commitAdvertiserRevenue();

        $scope.isPreviewing = true;

        $http.put("/analytics/api/1.0/advertiser-data/monthly-data/" + $scope.selectedMonth.year + "/" + $scope.selectedMonth.month + "/preview",$scope.monthlyAdvertiserData).success(function(data){
            $scope.previewResults = data;

            for(var i = 0; i < data.length; i++){
                data[i].platform_revenue = data[i].platform_revenue_cents/100;

                data[i].advertiser_revenue == null;

                if(data[i].advertiser_revenue_cents != null){
                    data[i].advertiser_revenue = data[i].advertiser_revenue_cents/100;
                }

            }
        });
    };

    $scope.save = function(){
        commitAdvertiserRevenue();

        $scope.isPreviewing = false;

        $http.put("/analytics/api/1.0/advertiser-data/monthly-data/" + $scope.selectedMonth.year + "/" + $scope.selectedMonth.month,$scope.monthlyAdvertiserData).success(function(data){
            $scope.monthlyAdvertiserData = data;

            for(var i = 0; i < data.length; i++){
                data[i].platform_revenue = data[i].platform_revenue_cents/100;

                data[i].advertiser_revenue == null;

                if(data[i].advertiser_revenue_cents != null){
                    data[i].advertiser_revenue = data[i].advertiser_revenue_cents/100;
                }

            }
        });
    }

    $scope.onPreviewClose = function(){
        $scope.isPreviewing = false;
    }
}]);


wahwahApp.controller('AdOpsOptionsDomainManagementController', ['$scope','$http',function($scope,$http)
{

    $scope.publisherQuery = getUrl("/api/1.0/publishers/query/%QUERY");

    // Initial list
    $scope.unmappedDomainList = [];
    $scope.unmappedDomainListFiltered = [];

    // Create
    $scope.manualCreateDomain = "";

    // Site/publisher
    $scope.publisher = "";
    $scope.publisherId = 0;
    $scope.siteList = [];
    $scope.chosenSite = '';
    $scope.chosenSiteDomainList = [];

    // Selected
    $scope.siteSelected = [];
    $scope.unmappedSelected = [];

    $scope.errorMsg = '';

    $scope.$watch("chosenSite",

        function handleSiteChange(newValue, oldValue)
        {
            $scope.updateSiteDomainList();
        }
    );


    $scope.$watch(
        "publisher",
        function handlePublisherChange(newValue, oldValue)
        {
            $http.get($scope.publisherQuery.replace("%QUERY", newValue)).success(function (data)
            {
                if (data.publishers.length == 1)
                {
                    $http.get(getUrl("/api/1.0/sites/publisher-" + data.publishers[0].id + "?page=0&size=10000")).success(function (data)
                    {
                        $scope.siteList = data.sites;
                        $scope.flash(document.getElementById('siteName'));
                    });
                }
            });


        }
    );

    $scope.flash = function (obj)
    {

        obj.style.border = "1px solid red";
        window.setTimeout(function ()
        {
            obj.style.border = "none";
        }, 1000);

    }

    $scope.alertError = function (msg)
    {
        $scope.errorMsg = msg;
        window.setTimeout(function ()
        {
            $scope.$apply(function ()
            {
                $scope.errorMsg = '';
            });
        }, 5000);
    }

    $scope.filter = '';


    $scope.$watch(
        "filter",
        function handleFilterChange(newValue, oldValue)
        {
            $scope.filterDomain();

        }
    );

    $scope.filterDomain = function()
    {
        $scope.unmappedDomainListFiltered = [];
        if ($scope.filter == '' || typeof($scope.filter) == "undefined")
        {
            $scope.unmappedDomainListFiltered = $scope.unmappedDomainList;
            return;
        }
        for (var i = 0; i < $scope.unmappedDomainList.length; i++)
        {
            var domainEntry = $scope.unmappedDomainList[i];
            if (domainEntry.domain.toLowerCase().indexOf($scope.filter.toLowerCase()) != -1)
            {
                $scope.unmappedDomainListFiltered.push(domainEntry);
            }
        }
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

    $scope.add = function()
    {
        var unmappedSelected = $scope.unmappedSelected;

        /* Shouldn't happen anymore w/ ng-disabled
        if (unmappedSelected.length == 0)
        {
            $scope.alertError("Please highlight at least one domain under 'Unmapped Domains'.");
            return;
        }*/
        if (typeof($scope.chosenSite.id) != "undefined")
        {
            for (var i = 0; i < unmappedSelected.length; i++)
            {
                unmappedSelected[i]["platform_site_id"] = $scope.chosenSite.id;
                unmappedSelected[i]["site_name"] = $scope.chosenSite.site_name;
            }
            $http.put("/analytics/api/1.0/domains/", JSON.stringify(unmappedSelected)).success(function (data)
            {
                $scope.updateSiteDomainList();
                $scope.startList();
            }).error(function()
                {
                    $scope.alertError("Unknown error adding site domain mapping.");
                    // Update anyway, just in case, since we are pushing multiple at one time
                    $scope.updateSiteDomainList();
                    $scope.startList();
                });
        }
        else
        {
            $scope.alertError("Please choose a site first.");
            $scope.flash(document.getElementById("siteName"));
            $scope.flash(document.getElementById("publisherName"));
        }

    }

    $scope.remove = function()
    {
        var siteSelected = $scope.siteSelected;
        /* Shouldn't happen anymore w/ ng-disabled
        if (siteSelected.length == 0)
        {
            $scope.alertError("Please highlight at least one domain under 'Site'.");
            return;
        }*/
        for (var i = 0; i < siteSelected.length; i++)
        {
            var domainEntry = siteSelected[i];
            $http.delete("/analytics/api/1.0/domains/" + domainEntry.id).success(function (data)
            {
                if (i >= siteSelected.length -1)
                {
                    $scope.updateSiteDomainList();
                    $scope.startList();
                }

            }).error(function()
                {
                    $scope.alertError("Unknown error removing at least one site domain mapping. ");
                    // Update here as well, since the error would only be on last item in that case
                    if (i >= siteSelected.length -1)
                    {
                        $scope.updateSiteDomainList();
                        $scope.startList();
                    }
                    // continue intentionally
                });
        }


    }

    $scope.createDomain = function()
    {
        var newDomain = [{
            domain: $scope.manualCreateDomain,
            id: 0,
            is_mapped: false,
            mapped: false,
            platform_site_id: $scope.chosenSite.id,
            site_name: $scope.chosenSite.site_name

        }];
        $http.put("/analytics/api/1.0/domains/", JSON.stringify(newDomain)).success(function (data)
        {
            $scope.updateSiteDomainList();
        }).error(function()
        {
            $scope.alertError("Invalid domain name: " + $scope.manualCreateDomain);
        });
    }



    // Begin


    $scope.startList = function()
    {
        $http.get("/analytics/api/1.0/domains/?is_mapped=false").success(function (data)
        {
            $scope.unmappedDomainList = data;
            $scope.filterDomain();
        });
    }

    $scope.updateSiteDomainList = function()
    {
        if (typeof($scope.chosenSite) == "undefined" || typeof($scope.chosenSite.id) == "undefined")
            return;
        $http.get("/analytics/api/1.0/domains/?site_id="+$scope.chosenSite.id).success(function (data)
        {
            $scope.chosenSiteDomainList = data;

            $scope.flash(document.getElementById('domain-select-mapped'));
            $scope.flash(document.getElementById('domain-select-unmapped'));
        });
    }


    $scope.startList();

}]);