'use strict';

angular.module('myApp', []);


angular.module('myApp').controller('myController', function($scope) {

    $scope.init = function(){
        $scope.greeting = 'Hello';
        $scope.person = 'World';
    };


});
