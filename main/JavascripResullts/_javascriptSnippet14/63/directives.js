'use strict';

/* Directives */


//angular.module('myApp.directives', []).
myApp.directive('appVersion', ['version', function(version) {
    return function(scope, elm, attrs) {
      elm.text(version);
    };
  }]);
  
myApp.directive('reveal', function(){
   return {
        restrict: "EA",
        templateUrl: "partials/foundation/reveal.html",
        controller: 'RevealController'
   }
});  

myApp.directive("enter", function(){
   return function(scope, element, attrs){
       element.bind("mouseenter", function(){
           scope.$apply(attrs.enter)
       })
   } 
});