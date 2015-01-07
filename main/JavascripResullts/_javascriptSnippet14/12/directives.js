app.directive('view', ['Views', function(Views) {
        var link = function link(scope, element, attrs) {
            for (var a in attrs) {
                if (a.indexOf('$') !== 0) {
                    scope[a] = attrs[a];
                }
            }
            scope.raceSelect();
        };
        return {
            restrict: 'AE',
            controller: "init",            
            templateUrl: function(element, attrs) {
                return attrs.url;
            },
            compile: function(element, attrs, transclude) {
                this.controller = Views.views[attrs.url].controller;
                return link;
            }
        };
    }]);
