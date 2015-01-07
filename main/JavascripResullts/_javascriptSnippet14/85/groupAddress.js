"use strict";

app.directive('groupAddress', function() {
    return {
        templateUrl: "/template/directives/groupAddress.html",
        replace: true,
        scope: {
            groupAddress: '='
        },
        link: function(scope, element, attrs, controller){
        }
    }
});