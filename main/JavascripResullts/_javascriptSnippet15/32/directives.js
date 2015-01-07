angular.module('experiences.directives', [])

    .directive('experience', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'experiences/views/experience.html'
        };
    }])

    .directive('expgroup', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'experiences/views/expgroup.html'
        };
    }])

    .directive('experiences', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'experiences/views/experiences.html'
        };
    }]) ;