(function() {

    angular.module('wahwah.components').component('datePicker', {
        templateUrl: getUrl('/components/FormControls/date-picker.html'),
        controller: DatePickerController,
        bindings: {
            id: '=',
            selectedDate: '='
        }
    });

    angular.module('wahwah.components').component('accordionList', {
        templateUrl: getUrl('/components/FormControls/accordion-list.html'),
        controller: AccordionListController,
        bindings: {
            listItems: '=',
            listItemName: '@',
            subListItems: '@',
            subListItemName: '@',
            onSubListItemSelect: '&',
            itemTemplateUrl: '@'
        }
    });

    angular.module('wahwah.components').component('accordion', {
        templateUrl: getUrl('/components/FormControls/accordion.html'),
        controller: AccordionController,
        bindings: {
            listItems: '=',
            listItemName: '@',
            itemTemplateUrl: '@',
            onListItemSelect: '&',
            onListItemDelete: '&',
            headerTemplateUrl: '@',
        }
    });

    angular.module('wahwah.components').component('modalDialog', {
        templateUrl: getUrl('/components/FormControls/modal-dialog.html'),
        controller: ModalDialogController,
        transclude: {
            modalTitle: 'modalTitle',
            modalBody: 'modalBody',
            modalFooter: '?modalFooter'
        },
        bindings: {
            modalId: '@',
            onClose: '&',
            visible: '<'
        }
    });

    function DatePickerController($scope,$element,Modernizr){
        var ctrl = this;

        var supportsDateField = Modernizr.inputtypes.date;

        ctrl.$postLink = function(){
            ctrl.datePicker = $element.find(".input-group");

            var datePickerOptions = {
                format: 'yyyy-mm-dd',
                startDate: "2015-04-01",
                endDate: null,
                showOnFocus: false,
                forceParse: false
            };

            ctrl.datePicker.datepicker(datePickerOptions);

            setParsedDate();

            ctrl.datePicker.datepicker().on('changeDate',function(e){

                if(ctrl.isChangingDate){
                    ctrl.isChangingDate = false;
                    return;
                }

                $scope.$apply(function () {
                    ctrl.selectedDateParsed = e.date;
                    ctrl.onSelectedDateChange();
                });
            });

            if(!supportsDateField){
                $element.find("input[type=date]").on('click',function(){
                   $scope.$apply(function(){
                      ctrl.datePicker.datepicker('show');
                   });
                });

                $element.find("input[type=date]").on('keypress',function(){
                   $scope.$apply(function(){
                      ctrl.datePicker.datepicker('hide');
                   });
                });
            }
        };

        ctrl.selectedDateParsed = null;
        ctrl.isChangingDate = false;

        ctrl.$onInit = function(){
            setParsedDate();
        };

        ctrl.onSelectedDateChange = function(){
            ctrl.isChangingDate = true;

            if(ctrl.selectedDateParsed) {
                ctrl.selectedDate = moment(ctrl.selectedDateParsed).format("YYYY-MM-DD");
            }
        };

        $scope.$watch('$ctrl.selectedDate',function(){
           setParsedDate();
        });

        function setParsedDate(){
            ctrl.selectedDateParsed = moment(ctrl.selectedDate,"YYYY-MM-DD").toDate();

            if(ctrl.datePicker) {
                ctrl.datePicker.data('date', ctrl.selectedDateParsed);
                ctrl.datePicker.datepicker('update', ctrl.selectedDateParsed);
            }
        }
    }

    function AccordionListController(){
        var ctrl = this;

        ctrl.selectedListItem = null;

        ctrl.$onInit = function(){
            if(ctrl.listItems.length > 0){
                ctrl.selectedListItem = ctrl.listItems[0];
            }

            ctrl._itemTemplateUrl = getUrl(ctrl.itemTemplateUrl);
        };
        
        ctrl.selectListItem = function(listItem){
            ctrl.selectedListItem = listItem;
        };

        ctrl.selectSubListItem = function(subListItem){
            ctrl.onSubListItemSelect({item:subListItem});
        };
    }

    function AccordionController(){
        var ctrl = this;

        ctrl.selectedListItem = null;

        ctrl.$onInit = function(){
            if(ctrl.listItems.length > 0){
                ctrl.selectedListItem = ctrl.listItems[0];
            }

            ctrl._itemTemplateUrl = getUrl(ctrl.itemTemplateUrl);
            if (ctrl.headerTemplateUrl)
                ctrl._headerTemplateUrl = getUrl(ctrl.headerTemplateUrl);
        };

        ctrl.openListItem = function(listItem){

            if (listItem == ctrl.selectedListItem)
                ctrl.selectedListItem = null;
            else
                ctrl.selectedListItem = listItem;
        };

        ctrl.selectListItem = function(listItem){
            ctrl.onListItemSelect({item:listItem});
        };

        ctrl.deleteListItem = function(listItem){
            ctrl.onListItemDelete({item:listItem});
        };

    }

    function ModalDialogController($element,$interval){
        var ctrl = this;

        ctrl.$onInit = function(){
            ctrl.isWide = false;
            ctrl.isNested = false;
        };

        ctrl.close = function(){
          ctrl.onClose();
        };

        ctrl.$postLink = function(){
            if(typeof $element.attr("wide") != "undefined"){
                ctrl.isWide = true;
            }

            if($element.parents("modal-dialog").length > 0){
                ctrl.isNested = true;
                ctrl.parentElement = $element.parents("modal-dialog .modal");
                ctrl.parentScope = ctrl.parentElement.scope();
                ctrl.parentCtrl = ctrl.parentScope.$ctrl;

                $interval(function(){

                    if(ctrl.visible){
                        resizeModal();
                    }

                },250);
            }
        };

        ctrl.$onChanges = function(changesObj){
            if(changesObj.visible.currentValue){
                $element.find(".modal").css("display","block");

                if(ctrl.isNested){
                    ctrl.parentCtrl.isChildDialogVisible = true;
                }

                resizeModal();

            } else {
                $element.find(".modal").css("display","none");
                $element.find(".modal-backdrop").css("height", "0px");

                if(ctrl.isNested) {
                    ctrl.parentCtrl.isChildDialogVisible = false;
                }
            }
        };

        function resizeModal(){
            var backdropHeight = $element.find(".modal")[0].scrollHeight;


            if(ctrl.isNested){

                var modalHeight = $element.find(".modal .modal-dialog").height();
                var parentHeight = modalHeight + 10;

                // Resize Parent Modal
                $element.parents(".modal-body").css("height",parentHeight + "px");

                // Position Our Modal
                $element.find(".modal").css("top","66px");

                // Adjust Backdrop
                backdropHeight = $element.parents(".modal-dialog").height();
                $element.find(".modal-backdrop").css("top","80px").css("left","10px").css("right","10px");
                $element.find(".modal").css("overflow-y","auto");
            }

            // Set background height
            $element.find(".modal-backdrop").css("height", backdropHeight + "px");
        }
    }
})();



