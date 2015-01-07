app.directive("textboxControl", function () {
    return {
        restrict: 'A',
        templateUrl: function(el,attrs) {
            var type = attrs.type;
            var template = '/App/Views/tpl/textbox-control.tpl.html';
            if (type=='number') {
                template = '/App/Views/tpl/textbox-number-control.tpl.html';
            }
            return template;
        },
        replace: 'true',
        ransclude: true,
        scope: {
            value: '=textboxControl',
            name: '@',
            readonly: '@',
            disabled: '@'
        },
        controller: ['$scope', '$element', '$attrs', function ($scope, $element, $attrs) {
        }],
        link: function (scope, ele, attr, ctrl) {
            
            
        }
    };
});