(function(){
    var app = angular.module("places", []);

    app.controller('placesController', function(){

    })

    app.directive('backgroundImage', function(){
        return function(scope, element, attrs){
            var url = attrs.backImg;
            element.css({
                'background-image': 'url(' + url +')',
                'background-size' : 'cover'
            });
        };
    })
})