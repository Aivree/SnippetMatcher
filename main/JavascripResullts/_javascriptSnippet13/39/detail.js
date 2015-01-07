'use strict';
var initApp = angular.module('initApp.controllers');

initApp.controller('detailController', function ($scope, points) {

    $scope.point = points.currentPhoto;
});
