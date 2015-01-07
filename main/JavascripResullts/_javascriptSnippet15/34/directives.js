angular.module('skills.directives', [])

    .directive('skillcategory', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'skills/views/skillcategory.html'
        };
    }])

    .directive('skillcategorysubset', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'skills/views/skillcategorysubset.html'
        };
    }])

    .directive('skills', ['$rootScope', function($rootScope) {
        return {
            restrict: 'E',
            templateUrl: $rootScope.urlRepAngular + 'skills/views/skills.html'
        };
    }]) ;