(function() {
    
    var FirstController = function ($scope, $log, $window, exampleFactory, appSettings) {

        $scope.appSettings = appSettings;
        
        function init() {
            
        }
        
        init();
    };
    
    FirstController.$inject = ['$scope', '$log', '$window', 'exampleFactory', 
                                   'appSettings'];

    angular.module('seedApp')
      .controller('FirstController', FirstController);
    
}());