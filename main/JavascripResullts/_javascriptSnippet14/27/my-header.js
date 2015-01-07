'use strict';

angular.module('blogApp')
  .controller('HeaderController', function ($scope, $location, $element) {

  })
  .directive('myHeader', function(){
    return {
      restrict: 'E',
      scope: {
        head: '=head'
      },
      templateUrl: 'components/my-header/my-header.html',
      link: function(scope, element, attrs){
        scope.head = attrs.head;
      }
    };
  });
