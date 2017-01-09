wahwahApp.controller('CustomReportsController', ['$scope', '$http', '$user', '$location', function ($scope, $http, $user, $location) {

    var ctrl = $scope.$ctrl = {};

    $user.getUserPromise().then(function(user){
        if(user.accountType == "FREE"){
            $location.path("/upgrade/from-free");
        }
    });

    ctrl.isLoading = false;

    ctrl.dates = {};
    ctrl.dates.endDate = moment().subtract(1, 'days').format('LL');
    ctrl.dates.startDate = moment().startOf('month').format('LL');
    ctrl.dates.todayDate = moment().format('LL');

    if (moment().date() == 1)
    {
        ctrl.dates.startDate = moment().subtract(1, 'months').startOf('month').format('LL');
    }

    ctrl.availableDimensions = [];
    ctrl.availableMetrics = [];
    ctrl.availableMembers = {};

    ctrl.selectedDimensions = [];
    ctrl.selectedMetrics = [];
    ctrl.selectedFilters = {};

    ctrl.$onInit = function(){
        $http.get('/analytics/api/1.0/custom-reports/metrics').success(function(data){
            ctrl.availableMetrics = data;

            $http.get('/analytics/api/1.0/custom-reports/dimensions').success(function(data){
                ctrl.availableDimensions = data;
            });
        });
    };

    ctrl.addDimension = function(){
        ctrl.selectedDimensions.push(ctrl.dimensionToAdd);
        ctrl.dimensionToAdd = null;
    };

    ctrl.addMetric = function(){
        ctrl.selectedMetrics.push(ctrl.metricToAdd);
        ctrl.metricToAdd = null;
    };

    ctrl.addFilter = function(){
        if(!ctrl.selectedFilters[ctrl.dimensionToFilterOn]){
            ctrl.selectedFilters[ctrl.dimensionToFilterOn] = [];
        }

        ctrl.selectedFilters[ctrl.dimensionToFilterOn].push(ctrl.dimensionMemberToFilterOn);
    };

    ctrl.setExport = function() {

        ctrl.isLoading = true;
        var dimension_filters = [];

        var startDate = moment(new Date(ctrl.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date(ctrl.dates.endDate)).format("YYYY-MM-DD");

        for (var key in ctrl.selectedFilters) {
            dimension_filters.push({
                "dimension": key,
                "value": ctrl.selectedFilters[key][0]
            })

        }

        var a = {
            "metrics": ctrl.selectedMetrics,
            "dimension_groups": ctrl.selectedDimensions,
            "dimension_filters": dimension_filters,
            "start_date": startDate,
            "end_date": endDate

        };

        $http.post('/analytics/api/1.0/custom-reports/run/', a).success(function (data)
        {
            if(data.is_successful){
                var csv = data.csv_result;
                var blob = new Blob([csv], {type: 'text/csv'});
                window.saveAs(blob, "custom-report-" + moment(new Date(ctrl.dates.todayDate)).format("YYYY-MM-DD") + ".csv");
            }

            ctrl.isLoading = false;
        }).error(function (data)
        {
            if(data.response_type){

                var errorHandled = false;

                if(data.response_type == "CUBE_ERROR"){
                    var alertMessage = '<strong class="modal-error">' + data.error_message + "</strong><br><br>";
                    alertMessage += "Of the measures selected, the following sets are compatible:<br><br>";

                    for(var i = 0; i < data.measure_groups.length; i++){
                        var group = data.measure_groups[i];
                        alertMessage += "<ul>";
                        alertMessage += ("<li>Group " + (i + 1) + "</li><ul>");

                        for(var j = 0; j < group.length; j++){
                            var value = group[j];
                            alertMessage += ("<li>" + value + "</li>");
                        }

                        alertMessage += "</ul></ul>";
                    }

                    errorHandled = true;

                    bootbox.alert(alertMessage);
                }

                if(!errorHandled){
                    if(data.error_message){
                        bootbox.alert(data.error_message);
                    } else {
                        bootbox.alert("There was an unknown error when generating your report");
                    }

                }

            } else {
                console.error("Error in Custom Reporting Service");
            }

            ctrl.isLoading = false;
        });

    }

    ctrl.checkDimensionEligibility = function(item) {

        var bannedDimensions = ['Ad Unit Type'];

        if(bannedDimensions.indexOf(item) > -1){
            return false;
        }

        return true;
    };

    ctrl.checkDimensionFilterEligibility = function(item) {

        var bannedDimensions = ['Date','Account', 'Ad Unit Type'];

        if(bannedDimensions.indexOf(item) > -1){
            return false;
        }

        return true;
    };

    $scope.$watch('$ctrl.dimensionToFilterOn',function(dimension){

        if(dimension) {
            $http.get('/analytics/api/1.0/custom-reports/dimensions/' + encodeURI(dimension)).success(function (data) {
                ctrl.availableMembers[dimension] = data;
            });
        }

    });

    ctrl.$onInit();

}]);
