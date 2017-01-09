wahwahApp.controller('AdminOptionsClientFeaturesController', ['$scope', '$http', '$user', function ($scope, $http, $user)
{
    $scope.isAddingFeature = false;
    $scope.isEditingFeature = false;

    $scope.showDeleteClientFeatureAlert = false;

    $scope.features = [];

    $scope.hasRole = function(roleName){
        return $user.hasRole(roleName);
    };

    $scope.variableTypes = {'STRING': "String", 'BOOLEAN': "Boolean", 'NUMBER': "Number", 'NONE': "None"};

    $scope.showCreateFeaturePopup = function(){
        $scope.isEditingFeature = false;
        $scope.isAddingFeature = true;

        $scope.newFeature = {};
    };

    $scope.onAddFeatureClose = function(){
        $scope.isAddingFeature = false;
        $scope.isEditingFeature = false;
    };

    function refreshClientFeatures(){
        $http.get(getUrl("/api/1.0/client-features/")).success(function(data){
            $scope.features = data.clientFeatures;
        });
    }

    refreshClientFeatures();

    $scope.saveNewFeature = function(){

        $http.post(getUrl("/api/1.0/client-features/"),$scope.newFeature).success(function(data){
            $scope.isAddingFeature = false;
            $scope.features.push(data);
        });

    };

    $scope.updateExistingFeature = function(){

        var featureId = $scope.newFeature.id;

        $http.put(getUrl("/api/1.0/client-features/" + featureId + "/"),$scope.newFeature).success(function(data){

            for(var i = 0; i < $scope.features.length; i++){
                if($scope.features[i].id == data.id){
                    $scope.features[i] = data;
                }
            }

            $scope.isEditingFeature = false;

        });

    };

    $scope.performDelete = function(){

        var featureId = $scope.featureToDelete.id;

        $http.delete(getUrl("/api/1.0/client-features/" + featureId + "/")).success(function(){

            var indexToDelete = -1;

            for(var i = 0; i < $scope.features.length; i++){
                if($scope.features[i].id == featureId){
                    indexToDelete = i;
                    break;
                }
            }

            $scope.features.splice(indexToDelete,1);

            $scope.hideDeleteWarning();
        });
    }

    $scope.showAssociatedSites = function(feature){

    };

    $scope.editFeature = function(feature){
        $scope.isAddingFeature = false;
        $scope.isEditingFeature = true;
        $scope.newFeature = angular.copy(feature);
    };

    $scope.deleteFeature = function(feature){
        $scope.featureToDelete = feature;
        $scope.showDeleteClientFeatureAlert = true;
    };

    $scope.hideDeleteWarning = function(){
        $scope.featureToDelete = null;
        $scope.showDeleteClientFeatureAlert = false;
    }
}]);

function AdminUpgradeAccountListController($http,$location){
    var ctrl = this;

    ctrl.page = {
        current: 0,
        size: 25
    };

    fetchAccounts();

    function fetchAccounts(){

        var params = {
            page: ctrl.page.current,
            size: ctrl.page.size
        };

        $http.get(getUrl("/api/1.0/accounts/can-upgrade"), {params: params}).success(function(data){
            ctrl.accounts = data.publishers;

            ctrl.page = {};
            ctrl.page.current = data.page_current;
            ctrl.page.size = data.page_size;
            ctrl.page.total = data.page_totalcount;

            ctrl.pages = [];

            for (var i = 0; i < ctrl.page.total; i++)
            {
                var page = {};
                page.index = i;
                page.number = i + 1;
                ctrl.pages.push(page);
            }
        });
    }

    ctrl.selectAccount = function(account){
        $location.path("/admin/upgrade/account-" + account.id);
    };

    ctrl.openPage = function(page){

        if(isNaN(page)){
            if(page == "previous"){
                ctrl.page.current--;
            }

            if(page == "next"){
                ctrl.page.current++;
            }
        } else {
            ctrl.page.current = page;
        }

        fetchAccounts();
    };
}

function AdminUpgradeAccountController($http,$routeParams,$scope,$location){

    var ctrl = this;

    $http.get(getUrl("/api/1.0/accounts/" + $routeParams.accountId)).success(function(data){
        ctrl.account = data;
    });

    ctrl.doUpgrade = function(){
        bootbox.confirm({
            title: 'Confirm Upgrade Request',
            message: 'Please confirm that you wish to upgrade ' + ctrl.account.name,
            buttons: {
                'cancel': {
                    label: 'Cancel',
                    className: 'btn-default pull-left'
                },
                'confirm': {
                    label: 'Confirm Upgrade',
                    className: 'btn-danger pull-right'
                }
            },
            callback: function(result) {
                $scope.$apply(function(){

                    ctrl.isUpgrading = true;

                    if (result) {
                        $http.post(getUrl("/api/1.0/accounts/perform-upgrade"),ctrl.account.id).success(function(data){
                            ctrl.isUpgrading = false;
                            ctrl.account = data;
                            $location.path('/network-' + data.id + '/publishers/');
                        });

                    }
                });
            }
        });
    }
}

wahwahApp.controller('AdminUpgradeAccountListController',AdminUpgradeAccountListController);

wahwahApp.controller('AdminUpgradeAccountController',AdminUpgradeAccountController);