'use strict';

angular.module('typeahead.directives', []);

angular.module('typeahead.directives').directive('ngTypeahead', ['$cookies', function ($cookies) {
    return {
        restrict: 'A',
        scope: {
            filter: '&',
            ngModel: '=',
            selectedItem: '=',
            limit: '=',
            rateLimitWait: '='
        },
        link: function (scope, element, attrs) {

            var bloodhoundOptions = {
                name: 'publishers',
                local: [],
                datumTokenizer: function (d) {

                    if(typeof d == "string"){
                        return d;
                    }

                    if(typeof d == "object" && typeof d.value != "undefined"){
                        return d.value;
                    }
                },
                queryTokenizer: Bloodhound.tokenizers.whitespace
            };

            if(attrs.options){
                bloodhoundOptions.local = attrs.options;
            }

            if(attrs.url){
                bloodhoundOptions.remote = {
                    url: attrs.url,
                    prepare: function(query, settings){
                        var xsrfToken = $cookies.get('XSRF-TOKEN');

                        var encodedQuery = encodeURIComponent(encodeURIComponent(query));
                        settings.url = settings.url.replace("%QUERY",encodedQuery);
                        settings.headers = {"X-XSRF-TOKEN": xsrfToken};

                        return settings;
                    },
                    transform: function(parsedResponse){
                        if (attrs.filter) {
                            parsedResponse = scope.filter({ parsedResponse: parsedResponse });
                        }

                        var response = angular.copy(parsedResponse);

                        if(!response){
                            response = [];
                        }

                        for(var i = 0; i < response.length; i++){

                            if(typeof response[i] == "object" && typeof response[i].value != "undefined"){
                                response[i] = response[i].value;
                            }
                        }

                        return response;
                    }
                };
            }

            if (attrs.rateLimitWait) {
                bloodhoundOptions.remote.rateLimitWait = attrs.rateLimitWait;
            }

            var bloodhound = new Bloodhound(bloodhoundOptions);
            bloodhound.initialize();

            element.typeahead(null, {source: bloodhound.ttAdapter() });

            if (attrs.ngModel) {

                scope.$watch('ngModel', function (newValue) {
                    element.typeahead('val', newValue);
                }, true);
            }

            element.on('change', function (event) {
                if (attrs.ngModel) {
                    scope.ngModel = element.typeahead('val');
                }

                scope.$apply();
            });

            element.on('typeahead:selected', function () {
                if (attrs.ngModel) {
                    scope.ngModel = element.typeahead('val');
                }

                scope.$apply();
            });

            element.on('typeahead:autocompleted', function () {
                if (attrs.ngModel) {
                    scope.ngModel = element.typeahead('val');
                }

                scope.$apply();
            });

            scope.$on('$destroy', function () {
                element.typeahead('destroy');
            });
        }
    };
}]);