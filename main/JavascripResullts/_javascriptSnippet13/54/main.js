'use strict';

angular.module('myAppApp')
    .controller('MainCtrl', ['$scope', 'myFactory', function ($scope, myFactory) {


        $scope.awesomeThings = [];
        $scope.test = [];

        init();

        function init() {

            $scope.awesomeThings = [
                'HTML5 Boilerplate',
                'AngularJS',
                'Karma'
            ]

            $scope.datas = myFactory.someMethod().query();

        }
    }]);
