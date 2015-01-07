'use strict';

angular.module('myApp', []);


angular.module('myApp').controller('MyController', function($scope) {

    $scope.init = function(){
        console.log('hihi');

        $scope.greeting = 'Hello';
        $scope.person = 'World';
    };


});
