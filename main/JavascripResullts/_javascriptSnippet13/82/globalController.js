'use strict';
(function(angular, $) {

    var gema = {
        name: 'Dodecahedro',
        price: '2.96',
        description: 'Esta es una gema'
    }

    angular.module('MyApp.GlobalController', [])
        .controller('GlobalController', [
            '$scope',
            function($scope) {
                $scope.getPrueba = function() {
                    $scope.prueba = 'Prueba de Init';
                    return $scope.prueba;
                };
            }
        ])
        .controller('HomeController', [
            '$scope',
            'maxGemas',
            function($scope, maxGemas) {
                console.log("HomeController");
                console.log(maxGemas);
                $scope.producs = gema;
            }
        ]);

}(angular, $))