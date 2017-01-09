function PopoverListController($scope, $element, $document, $timeout){
    var ctrl = this;

    ctrl.isPopoverVisible = false;

    ctrl.$onInit = function(){
        if(ctrl.source && ctrl.source.length > 0){
            $element.css("cursor","pointer");
        } else {
            $element.css("cursor","auto");
        }
    };

    $scope.$on('$locationChangeStart',function(){
        ctrl.hidePopover();
    });

    ctrl.selectItem = function(item){
        ctrl.listItemSelect({item: item});
        ctrl.hidePopover();
    };

    ctrl.hidePopover = function(){
        if(ctrl.popoverElement) {
            ctrl.isPopoverVisible = false;
            ctrl.popoverElement.css("display", "none");
        }

        $element.find("a.popover-link").removeClass("popover-is-open");
    };

    $document.click(function(e){

        var doCancel = true;

        var target = $(e.target);

        if(target.parents("popover-list-link").length > 0 && target.parents("popover-list-link")[0] == $element[0]){
            doCancel = false;
        }

        if(target.parents("before-item").length > 0 || target.parents("after-item").length > 0){
            doCancel = true;
        }

        if(doCancel) {
            $scope.$apply(function () {
                if (ctrl.isPopoverVisible) {
                    ctrl.hidePopover();
                }
            })
        }
    });

    ctrl.showPopover = function(){

        if(!ctrl.disabled) {
            ctrl.isPopoverVisible = true;

            $timeout(function () {
                var popoverElem = $element.find(".popover");

                var height = $element.find("a.popover-link").height();
                var position = $element.find("a.popover-link").position();

                var width = $element.find("a.popover-link").width();

                var top = position.top + height;
                var left = position.left + (width / 2) - (popoverElem.width() / 2);

                ctrl.popoverElement = popoverElem;

                popoverElem.css("display", "block");
                popoverElem.css("position", "absolute");
                popoverElem.css("top", top + "px");
                popoverElem.css("left", left + "px");

                $element.find("a.popover-link").addClass("popover-is-open");
            });
        }
    };
}

function PopoverDatePickerController($scope, $element, $document, $timeout){
    var ctrl = this;

    ctrl.isPopoverVisible = false;

    ctrl.$onInit = function(){
        $element.css("cursor","pointer");
        $element.find("div.popover-date-picker").datepicker({});
    };

    $scope.$on('$locationChangeStart',function(){
        ctrl.hidePopover();
    });

    ctrl.hidePopover = function(){
        if(ctrl.popoverElement) {
            ctrl.isPopoverVisible = false;
            ctrl.popoverElement.css("display", "none");
        }

        $element.find("a.popover-link").removeClass("popover-is-open");
        $element.find(".popover-date-picker").datepicker("destroy");
    };

    $document.click(function(e){

        var doCancel = true;

        var target = $(e.target);

        if(target.parents("popover-date-picker-link").length > 0 && target.parents("popover-date-picker-link")[0] == $element[0]){
            doCancel = false;
        }

        if(doCancel) {
            $scope.$apply(function () {
                if (ctrl.isPopoverVisible) {
                    ctrl.hidePopover();
                }
            })
        }
    });

    ctrl.showPopover = function(){
        if(!ctrl.disabled) {
            ctrl.isPopoverVisible = true;

            $timeout(function () {

                ctrl.datePicker = $element.find(".popover-date-picker");

                var date = moment(new Date(ctrl.date)).toDate();

                var startDate = null;
                var endDate = null;

                if (ctrl.startDate) {
                    startDate = moment(new Date(ctrl.startDate)).toDate();
                }

                if (ctrl.endDate) {
                    endDate = moment(new Date(ctrl.endDate)).toDate();
                }

                var datePickerOptions = {
                    startDate: startDate,
                    endDate: endDate
                };

                ctrl.datePicker.data('date', date);
                ctrl.datePicker.datepicker(datePickerOptions);
                ctrl.datePicker.datepicker('update', date);

                var popoverElem = $element.find(".popover");

                var height = $element.find("a.popover-link").height();
                var position = $element.find("a.popover-link").position();

                var width = $element.find("a.popover-link").width();

                var top = position.top + height;
                var left = position.left + (width / 2) - (popoverElem.width() / 2);

                ctrl.popoverElement = popoverElem;

                popoverElem.css("display", "block");
                popoverElem.css("position", "absolute");
                popoverElem.css("top", top + "px");
                popoverElem.css("left", left + "px");

                $element.find("a.popover-link").addClass("popover-is-open");

                ctrl.datePicker.datepicker().on('changeDate', function (e) {
                    $scope.$apply(function () {
                        ctrl.date = moment(e.date).format('LL');
                        ctrl.hidePopover();
                    });
                });
            });
        }
    };
}

function PopoverDateRangePickerController($timeout, $element, $document, $scope){
    var ctrl = this;

    ctrl.isPopoverVisible = false;

    ctrl.selectedDateRange = null;

    ctrl.dateRanges = [];

    ctrl.$onInit = function(){

        ctrl.todayDate = moment().format('LL');

        var refDate = moment();
        var endDate = moment();// TODO: Use current date for internal users

        // Current Time Ranges
        var thisWeek = {name: 'This Week', startDate: refDate.clone().startOf('week').format('LL'), endDate: endDate.clone().format('LL') };
        var thisMonth = {name: 'This Month', startDate: refDate.clone().startOf('month').format('LL'), endDate: endDate.clone().format('LL') };
        var thisQuarter = {name: 'This Quarter', startDate: refDate.clone().startOf('quarter').format('LL'), endDate: endDate.clone().format('LL') };

        ctrl.dateRanges.push(thisWeek);
        ctrl.dateRanges.push(thisMonth);
        ctrl.dateRanges.push(thisQuarter);

        // Previous Time Ranges
        var lastWeek = {name: 'Last Week'};
        lastWeek.startDate = moment(new Date(thisWeek.startDate)).subtract(1,'days').startOf('week').format('LL');
        lastWeek.endDate = moment(new Date(lastWeek.startDate)).endOf('week').format('LL');

        var lastMonth = {name: 'Last Month'};
        lastMonth.startDate = moment(new Date(thisMonth.startDate)).subtract(1,'days').startOf('month').format('LL');
        lastMonth.endDate = moment(new Date(lastMonth.startDate)).endOf('month').format('LL');

        var lastQuarter = {name: 'Last Quarter'};
        lastQuarter.startDate = moment(new Date(thisQuarter.startDate)).subtract(1,'days').startOf('quarter').format('LL');
        lastQuarter.endDate = moment(new Date(lastQuarter.startDate)).endOf('quarter').format('LL');

        ctrl.dateRanges.push(lastWeek);
        ctrl.dateRanges.push(lastMonth);
        ctrl.dateRanges.push(lastQuarter);

        var last7Days = {name: 'Last 7 Days'};
        last7Days.startDate = refDate.clone().subtract(7,'days').format('LL');
        last7Days.endDate = refDate.clone().format('LL');

        var last30Days = {name: 'Last 30 Days'};
        last30Days.startDate = refDate.clone().subtract(30,'days').format('LL');
        last30Days.endDate = refDate.clone().format('LL');

        var last90Days = {name: 'Last 90 Days'};
        last90Days.startDate = refDate.clone().subtract(90,'days').format('LL');
        last90Days.endDate = refDate.clone().format('LL');

        ctrl.dateRanges.push(last7Days);
        ctrl.dateRanges.push(last30Days);
        ctrl.dateRanges.push(last90Days);

        var customRange = {name: 'Custom Date Range', isCustom: true};
        ctrl.dateRanges.push(customRange);

        var hasMatch = false;

        for(var i = 0; i < ctrl.dateRanges.length; i++){
            if(ctrl.dateRanges[i].startDate == ctrl.startDate && ctrl.dateRanges[i].endDate == ctrl.endDate){
                ctrl.selectedDateRange = ctrl.dateRanges[i];
                hasMatch = true;
            }
        }

        if(!hasMatch){
            ctrl.selectedDateRange = customRange;
        }
    };
    
    ctrl.selectDateRange = function(dateRange){

        ctrl.selectedDateRange = dateRange;

        if(!dateRange.isCustom) {
            ctrl.startDate = dateRange.startDate;
            ctrl.endDate = dateRange.endDate;
        }
    };

}

angular.module('wahwah.components').component('popoverListLink', {
    templateUrl: getUrl('/components/PopoverMenu/popover-list.html'),
    controller: PopoverListController,
    transclude: {
        beforeItem: '?beforeItem',
        afterItem: '?afterItem'
    },
    bindings: {
        source: '<',
        title: '@',
        listItemName: '@',
        listItemSelect: '&',
        disabled: '<'
    }
});

angular.module('wahwah.components').component('popoverDatePickerLink', {
    templateUrl: getUrl('/components/PopoverMenu/popover-date-picker.html'),
    controller: PopoverDatePickerController,
    transclude: true,
    bindings: {
        title: '@',
        date: '=',
        startDate: '<',
        endDate: '<',
        disabled: '<'
    }
});

angular.module('wahwah.components').component('popoverDateRangePickerLink', {
    templateUrl: getUrl('/components/PopoverMenu/popover-date-range-picker.html'),
    controller: PopoverDateRangePickerController,
    bindings: {
        title: '@',
        startDate: '=',
        endDate: '=',
    }
});