'use strict';

angular.module('manhattanApp')
.directive('wdHeader', function() {
return {
    restrict: 'A',
    templateUrl: '../../views/common/header.html',
    scope: true,
    replace: true,
    controller: ['$scope', 
    function($scope) {
        
    }],
    link: function($scope, $element, $attrs, $controller) {
    }
};
});
