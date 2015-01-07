agbeApp.directive('agbeObject', function () {
    return {
        templateUrl: 'template/agbe-object.html',
        scope: {

        },
        link: function ($scope, element, $attrs) {
            $scope.objectText = $attrs.objectText;
            $scope.objectId = $attrs.objectId;
            $scope.objectNb = $attrs.objectNb;
            if ($scope.objectNb == null) {
                $scope.objectNb = 1;
            }
            // object number declaration
            $scope.agbeService.declareObjectNumber($scope.objectId,$scope.objectNb);
        },
        controller: ['$scope', '$attrs', 'agbeService', function ($scope, $attrs, agbeService) {

            $scope.agbeService = agbeService;

            $scope.isObjectPresent = function () {
                return agbeService.isObjectPresent($scope.objectId);
            },

                $scope.onObjectClick = function () {
                    agbeService.take($scope.objectId);
                }
        }]
    }
});