"use strict";

app.directive('galleryThumb', function($timeout) {
    return {
        templateUrl: "/template/directives/galleryThumb.html",
        replace: true,
        scope: {
            post: "="
        },
        link: function(scope, element, attrs, controller){
        }
    }
});