angular.module('wahwah.components').component('tagInput', {
    templateUrl: getUrl('/components/TagInput/tag-input.html'),
    controller: TagInputController,
    bindings: {
        selectedTags: '=',
        options: '<',
        optionLabel: '@'
    }
});

function TagInputController($scope,$element){
    var ctrl = this;
    ctrl.initialOptions = [];

    ctrl.$onInit = function(){
        var bloodhoundOptions = {
            matchAnyQueryToken: true,
            local: [],
            datumTokenizer: function(d){
                return Bloodhound.tokenizers.whitespace(d);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace
        };

        ctrl.bloodhoundEngine = new Bloodhound(bloodhoundOptions);
        ctrl.bloodhoundEngine.add(ctrl.initialOptions);

        var selectElement = $element.find("select");

        selectElement.tagsinput({
            typeaheadjs: {
                source: ctrl.bloodhoundEngine.ttAdapter()
            },
            freeInput: false
        });

        $element.find(".twitter-typeahead").css("display","inline");

        ctrl.inSelectedTagsChange = true;

        for(var i = 0; i < ctrl.selectedTags.length; i++){
            var tag = ctrl.selectedTags[i];
            selectElement.tagsinput("add",tag);
        }

        ctrl.inSelectedTagsChange = false;
    };

    $scope.$watchCollection('$ctrl.options',function(newOptions){

        ctrl.initialOptions = [];

        for(var i = 0; i < newOptions.length; i++){
            var val = newOptions[i];

            if(ctrl.optionLabel){
                val = $scope.$eval(ctrl.optionLabel,val);
            }

            ctrl.initialOptions.push(val);
        }

        if(ctrl.bloodhoundEngine) {
            ctrl.bloodhoundEngine.clear();
            ctrl.bloodhoundEngine.add(ctrl.initialOptions);
        }
    });

    $scope.$watchCollection('$ctrl.selectedTags',function(newSelectedTags, oldSelectedTags){
        if(angular.equals(newSelectedTags,oldSelectedTags)){
            return;
        }

        ctrl.inSelectedTagsChange = true;

        var selectElement = $element.find("select");
        selectElement.tagsinput("removeAll");

        for(var i = 0; i < newSelectedTags.length; i++){
            var tag = newSelectedTags[i];
            selectElement.tagsinput("add",tag);
        }

        ctrl.inSelectedTagsChange = false;
    });

    $element.find("select").on('itemAdded', function(event) {

        var handler = function(){
            if(ctrl.selectedTags.indexOf(event.item) == -1){
                ctrl.selectedTags.push(event.item);
            }
        };

        if(ctrl.inSelectedTagsChange){
            handler();
        } else {
            $scope.$apply(handler);
        }
    });

    $element.find("select").on('itemRemoved', function(event){
        var handler = function(){
            var i = ctrl.selectedTags.indexOf(event.item);
            ctrl.selectedTags.splice(i,1);
        };

        if(ctrl.inSelectedTagsChange){
            handler();
        } else {
            $scope.$apply(handler);
        }
    });
}