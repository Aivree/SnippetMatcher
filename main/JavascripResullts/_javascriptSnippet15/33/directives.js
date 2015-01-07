angular.module('projects.directives', [])

    .directive('project', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'projects/views/project.html'
        };
    }])

    .directive('projectdetails', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'projects/views/projectdetails.html'
        };
    }])

    .directive('projects', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'projects/views/projects.html'
        };
    }]) ;