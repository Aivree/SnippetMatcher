;(function() {
    angular.module('main').directive('lookup', function() {
        return {
            restrict: 'E',
            link: function(scope, element, attrs) {
                scope.attrs = attrs;
            },
            templateUrl: 'app/views/lookup.html',
            controller: 'lookupController'
        }
    });
})();
