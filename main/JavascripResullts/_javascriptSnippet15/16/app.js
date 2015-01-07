angular.module('demo', [])
    .directive('myDirective', function(){
        return {
            restrict: "E",
            scope: {},
            templateUrl: "static/js/myAngular/myPage.html",
            link: function(scope) {
                scope.name = "World";
            }
        }
    });
