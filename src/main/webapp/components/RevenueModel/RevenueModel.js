function RevenueModelController($http,moment,bootbox){
    var ctrl = this;

    ctrl.id = -1;

    ctrl.revenueModelTypes = {
        REVENUE_SHARE: "Rev Split",
        GUARANTEED_CPM: "Guaranteed CPM",
        GUARANTEED_CPM_WITH_FILL: "Guaranteed CPM w/ Fill"
    };

    ctrl.formats = {
        ALL_DEFAULT: "All Formats (Default)",
        OUTSTREAM: "AnyAd - Outstream",
        FLOATER: "AnyAd - Floater",
        BANNER: "AnyAd - Banner",
        CUSTOM: "Non-AnyAd: Custom Format",
        ADSERVERNATIVE: "Non-AnyAd: Ad Server Ad Unit"
    };
    
    ctrl.revenueModelToEdit = null;

    ctrl.editorSettings = {
        showCPMEditor: false,
        showMinimumPayout: false
    };

    ctrl.$onInit = function(){
        if(ctrl.entityId){
            loadRevenueModels();
        }
    };

    ctrl.$onChanges = function(changesObj){
        if(changesObj.entityId.currentValue){
            loadRevenueModels();
        }
    };

    function loadRevenueModels(){
        $http.get(getUrl("/api/1.0/revenue-model/" + ctrl.entityType + "/" + ctrl.entityId)).success(function(data){
            ctrl.revenueModels = data.revenue_models;

            angular.forEach(ctrl.revenueModels,function(value){

                if(value.format == null){
                    value.format = "ALL_DEFAULT";
                }

            });
        });
    }

    ctrl.addNew = function(){

        var currentRevenueModel = null;

        for(var i = 0; i < ctrl.revenueModels.length; i++){
            var revModel = ctrl.revenueModels[i];

            if(revModel.format == "ALL_DEFAULT" && revModel.revenue_model_end_date == null){
                currentRevenueModel = revModel;
            }
        }

        ctrl.revenueModelToEdit = {
            format: "ALL_DEFAULT",
        };

        if(currentRevenueModel != null){
            ctrl.revenueModelToEdit = angular.copy(currentRevenueModel);
            delete ctrl.revenueModelToEdit.id;
        }
    };

    ctrl.editRevenueModel = function(revenueModel){
        ctrl.revenueModelToEdit = angular.copy(revenueModel);
    };

    ctrl.saveRevenueModel = function(){
        // Validate Revenue Model Dates
        var errorMessages = [];

        var format = ctrl.revenueModelToEdit.format;
        var startDate = ctrl.revenueModelToEdit.revenue_model_start_date;
        var endDate = ctrl.revenueModelToEdit.revenue_model_end_date;

        for(var i = 0; i < ctrl.revenueModels.length; i++){
            var revModel = ctrl.revenueModels[i];

            if(ctrl.revenueModelToEdit.id == revModel.id){
                continue;
            }

            var revModelStartDate = revModel.revenue_model_start_date;
            var revModelEndDate = revModel.revenue_model_end_date;

            if(revModel.format == format){

                if(revModelStartDate == null && startDate == null){
                    errorMessages.push("This financial model cannot be saved since you already have a start date of 'Initial'");
                    break;
                }

                if(revModelEndDate == null && endDate == null){
                    errorMessages.push("This financial model cannot be saved since you already have a start date of 'Current'");
                    break;
                }

                // Verify time ranges do not overlap
                if((startDate != null || endDate != null) && (revModelStartDate != null || revModelEndDate != null)){

                    var startDateForRange = moment(startDate || "2010-01-01","YYYY-MM-DD");
                    var endDateForRange = moment(endDate || "2100-12-31","YYYY-MM-DD");

                    var range = moment.range(startDateForRange,endDateForRange);

                    var refStartDateForRange = moment(revModelStartDate || "2010-01-01","YYYY-MM-DD");
                    var refEndDateForRange = moment(revModelEndDate || "2100-12-31","YYYY-MM-DD");

                    var refRange = moment.range(refStartDateForRange,refEndDateForRange);

                    if(range.overlaps(refRange)){
                        var intersect = range.intersect(refRange);

                        var startDateForIntersect = intersect.start.format("LL");
                        var endDateForIntersect = intersect.end.format("LL");

                        if(intersect.start.format("YYYY-MM-DD") == "2010-01-01"){
                            continue;
                        }

                        if(intersect.end.format("YYYY-MM-DD") == "2100-12-31"){
                            continue;
                        }

                        errorMessages.push("This financial model cannot be saved since it overlaps another model between " + startDateForIntersect + " and " + endDateForIntersect);
                    }
                }

            }
        }

        if(errorMessages.length > 0){
            console.log(errorMessages);

            var errorMessageHTML = '<p class="text-danger"><strong>You have the following errors that must be fixed before this financial model can be saved:</strong></p><ul>';

            for(var i = 0; i < errorMessages.length; i++){
                errorMessageHTML += ("<li>" + errorMessages[i] + "</li>");
            }

            errorMessageHTML += "</ul>";

            bootbox.alert(errorMessageHTML);
        } else {
            if(!ctrl.revenueModelToEdit.id){
                ctrl.revenueModelToEdit.id = ctrl.id--;
                ctrl.revenueModels.push(ctrl.revenueModelToEdit);
            } else {
                for(var i = 0; i < ctrl.revenueModels.length; i++){
                    if(ctrl.revenueModels[i].id == ctrl.revenueModelToEdit.id){
                        ctrl.revenueModels[i] = ctrl.revenueModelToEdit;
                    }
                }
            }

            ctrl.revenueModelToEdit = null;
        }

        angular.forEach(ctrl.revenueModels,function(value){

            if(value.format == "ALL_DEFAULT"){
                value.format = null;
            }

            if(value.id < 0){
                delete value.id;
            }

        });

        $http.put(getUrl("/api/1.0/revenue-model/" + ctrl.entityType + "/" + ctrl.entityId),ctrl.revenueModels).success(function(data){
            ctrl.revenueModels = data.revenue_models;

            angular.forEach(ctrl.revenueModels,function(value){

                if(value.format == null){
                    value.format = "ALL_DEFAULT";
                }

            });
        });

    };
}

angular.module('wahwah.components').component('revenueModelPanel', {
    templateUrl: getUrl('/components/RevenueModel/revenue-model.html'),
    controller: RevenueModelController,
    bindings: {
        entityType: "@",
        entityId: "<"
    }
});