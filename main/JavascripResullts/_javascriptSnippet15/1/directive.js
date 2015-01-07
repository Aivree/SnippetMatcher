'use strict'

angular.module('<%= moduleName %>', [])
  .directive('<%= directive %>', [function() {
    return {
      restrict: 'E',
      scope: {},
      templateUrl: '<%= path %>.html'
    }
  }])
