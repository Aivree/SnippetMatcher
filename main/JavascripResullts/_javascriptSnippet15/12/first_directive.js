angular.module("App.directives")
    .directive("firstDirective", function () {
        return{
            restrict: 'E',
            controller: function ($scope) {
                $scope.name = "firstDirective";
            },
            replace: true,
            templateUrl: "templates/first_directive.html"
        };
    });