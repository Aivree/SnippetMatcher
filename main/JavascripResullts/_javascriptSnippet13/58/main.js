'use strict';

angular.module('youtubemobiApp')
  .controller('MainCtrl', function ($scope, $resource, Socket, User) {

//    $scope.users = Users.query();
    Socket.init();
  });
