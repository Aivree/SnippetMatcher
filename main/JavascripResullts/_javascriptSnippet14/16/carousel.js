'use strict';

myApp.controller('CarouselCtrl', function ($scope, $stateParams) {
    $scope.currentPage = 0;
});

myApp.directive('carousel', function() {
    return {
        restrict: 'E',
        link: function(scope, element, attrs)
        {
            scope.pages = attrs.pages.split(",");
        },
        controller:'CarouselCtrl',
        templateUrl:'carousel/carousel.html'
    };
});



