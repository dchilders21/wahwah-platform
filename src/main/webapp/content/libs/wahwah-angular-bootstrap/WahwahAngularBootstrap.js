'use strict';

angular.module('wahwah.bootstrap', []);

angular.module('wahwah.bootstrap').directive('ngTooltip', function () {

    return {
        restrict: 'A',
        link: function(scope,element,attrs){
            element.tooltip();
        }
    }

});

angular.module('wahwah.bootstrap').directive('ngTooltipIf', function () {

    return {
        restrict: 'A',
        scope: {
            ngTooltipIf: '='
        },
        link: function(scope,element,attrs){
            element.attr("data-trigger","manual");

            var isHidden = true;

            scope.$watch('ngTooltipIf', function (newValue) {
                if(newValue){
                    if(isHidden) {
                        element.tooltip("show");
                        isHidden = true;
                    }
                } else {
                    if(!isHidden){
                        element.tooltip("hide");
                        isHidden = false;
                    }
                }
            }, true);
        }
    }

});

angular.module('wahwah.bootstrap').directive('ngPopover', function () {

    return {
        restrict: 'A',
        link: function(scope,element,attrs){
            element.popover();
        }
    }

});

angular.module('wahwah.bootstrap').directive('ngPopoverIf', [function () {

    return {
        restrict: 'A',
        scope: {
            ngPopoverIf: '='
        },
        link: function(scope,element,attrs){

            element.attr("data-trigger","manual");

            var isHidden = true;

            scope.$watch('ngPopoverIf', function (newValue) {
                if(newValue){
                    if(isHidden) {
                        element.popover("show");
                        isHidden = true;
                    }
                } else {
                    if(!isHidden){
                        element.popover("hide");
                        isHidden = false;
                    }
                }
            }, true);
        }
    }
}]);

angular.module('wahwah.bootstrap').directive('ngShowModal',function(){
    return {
        restrict: 'A',
        scope: {
            ngShowModal: '=',
            close: '&'
        },
        link: function(scope, element, attrs){

            function showHideModal(value){
                if(value){
                    element.modal('show');
                } else {
                    element.modal('hide');
                }
            };

            showHideModal(scope.ngShowModal);

            scope.$watch('ngShowModal',function(newValue){
                showHideModal(newValue);
            });

            element.on('hidden.bs.modal',function(){
              scope.$apply(function(){
                  scope.close();
              })
            });

        }
    };
});

angular.module('wahwah.bootstrap').directive('ngInlineEdit',function(){
    return {
        restrict: 'A',
        scope: {
            model: '=',
            modelShadow: '&model',
            type: '@'
        },
        templateUrl: getUrl("/content/libs/wahwah-angular-bootstrap/templates/inline-editor.html"),
        link: function(scope,element,attrs){

            scope.form = {};
            scope.data = {};

            scope.isEditable = false;

            if(!scope.type){
                scope.type = "text";
            }

            switch(scope.type){
                case "text":
                    scope.fieldType = "text";
                    break;
                case "number":
                    scope.fieldType = "number";
                    break;
                case "money":
                    scope.fieldType = "text";
                    break;
            }

            function setModelShadowValue(value){

                if(value == null){
                    scope.data.modelShadowValue = null;
                    return;
                }

                if(scope.type == "number" || scope.type == "money"){
                    scope.data.modelShadowValue = parseFloat(value.toString());
                }

                if(scope.type == "text"){
                    scope.data.modelShadowValue = value.toString();
                }
            }

            setModelShadowValue(scope.model);

            scope.saveValue = function(){

                scope.model = scope.data.modelShadowValue;

                if(scope.model != null && scope.model.toString().length == 0){
                    scope.model = null;
                }

                scope.setEditable(false);
            };

            scope.discardValue = function(){
                scope.setEditable(false);
            };

            scope.$watch('model',function(newValue){
                setModelShadowValue(newValue);
            });

            scope.setEditable = function(value){

                if(value){
                    setModelShadowValue(scope.model);
                }

                scope.isEditable = value;
            };
        }
    };
});

angular.module('wahwah.bootstrap').directive('ngTabs',function(){
    return {
        restrict: 'AE',
        scope: {
            tabs: '=',
            selectedTab: '=',
            tabLabel: '@',
            tabSelect: '&'
        },
        templateUrl: getUrl("/content/libs/wahwah-angular-bootstrap/templates/tabs.html"),
        link: function(scope,element,attrs){
            scope.setActiveTab = function(tab){
                scope.selectedTab = tab;
                scope.tabSelect({tab: tab});
            }
        }
    }

});

angular.module('wahwah.bootstrap').directive('autofocus',['$timeout',function($timeout){
    return {
        restrict: 'A',
        link: function ($scope, $element) {
            $timeout(function () {
                $element[0].focus();
            });
        }
    };
}]);

angular.module('wahwah.bootstrap').directive('ngEnter', function (){
    return {
        restrict: 'A',
        link: function (scope, elements, attrs) {
            elements.bind('keydown keypress', function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.ngEnter);
                    });
                    event.preventDefault();
                }
            });
        }
    };
});

angular.module('wahwah.bootstrap').directive('dndTable',function(){
   return {
       restrict: 'A',
       scope: {
           dndTableRowItemName: "@",
           dndOnDrop: "&"
       },
       link: function(scope, element, attrs) {

           element.css("position","relative");

           var $sourceRow = null;
           var $row = null;
           var $table = null;

           var currentHoverRow = null;

           element.on("mousedown",".dnd-icon",function(e){
                $sourceRow = angular.element(this).parents("tr");
                $row = $sourceRow.clone();

               $sourceRow.css("display","none");

                var tbody = $row.wrap("<tbody></tbody>").parent();
                $table = tbody.wrap("<table></table>").parent();

                if(element.hasClass("table")){
                    $table.addClass("table");
                }

                if(element.hasClass("table-striped")){
                    $table.addClass("table-striped");
                }

                if(element.hasClass("table-grid")){
                    $table.addClass("table-grid");
                }

                $table.css("zIndex",5000);

                $table.css("position","absolute");
                $table.css("top",e.pageY + "px");
                $table.css("left",element.offset().left + "px");
                $table.css("width",element.width() + "px");

                angular.element("body").append($table);
           });

           angular.element("body").on("mouseup",function(e){
               if($table){
                   $table.remove();
                   $table = null;

                   var dropElement = document.elementFromPoint(e.pageX,e.pageY);
                   var $dropRow = angular.element(dropElement).parents("tr");

                   $sourceRow.css("display","table-row");
                   var defaultHeight  = $sourceRow.find("td").css("padding-bottom").replace("px","");
                   angular.element(currentHoverRow).find("td").css("padding-bottom",defaultHeight + "px");

                   if($dropRow.parents("table")[0] == element[0]){
                       scope.$apply(function(){

                           var source = $sourceRow.scope()[scope.dndTableRowItemName];
                           var target = $dropRow.scope()[scope.dndTableRowItemName];

                           scope.dndOnDrop({source: source, target: target});
                       });
                   }
               }
           });

           angular.element("body").on("mousemove",function(e){
               if($table) {
                   $table.css("top", (e.pageY + 1) + "px");

                   var hoverElement = document.elementFromPoint(e.pageX,e.pageY);
                   var $hoverElement = angular.element(hoverElement).parents("tr");
                   var hoverRow = $hoverElement[0];

                   if(currentHoverRow != hoverRow){

                       var lineHeight = $hoverElement.find("td").css("line-height").replace("px","");
                       var defaultHeight  = $hoverElement.find("td").css("padding-bottom").replace("px","");
                       var expandedHeight = parseInt(defaultHeight) + parseInt(lineHeight);

                       angular.element(currentHoverRow).find("td").css("padding-bottom",defaultHeight + "px");
                       $hoverElement.find("td").css("padding-bottom",expandedHeight + "px");

                       currentHoverRow = hoverRow;
                   }
               }
           })
       }
   }
});