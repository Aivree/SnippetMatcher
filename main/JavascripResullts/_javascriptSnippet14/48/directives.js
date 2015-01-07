addressBookApp.directive('addContactForm', function() {
    return {
        restrict: 'E',
        scope: {            
            person: '=',
            ngSubmit: '&'
        },
        templateUrl: '/partials/contactForm2.html',
        controller: 'addController'/*,
        link: function(scope, element, attrs){
            scope.action = attrs.action;
        }*/
    }
});

addressBookApp.directive('editContactForm', function() {
    return {
        restrict: 'E',
        scope: {
            person: '=',
            ngSubmit: '&',
            ngInit: '&'
        },
        templateUrl: '/partials/contactForm2.html',
        controller: 'editController'/*,
        link: function(scope, element, attrs){
            scope.action = attrs.action;
        }*/
    }
});