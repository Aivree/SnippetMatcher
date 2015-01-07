angular.module('angularTestApp').directive('btmodal', function(){
    return {
    	restrict: 'A',
        templateUrl: './views/directives/modal.html',
        // scope: {
            
        // },
        link: function(scope, element, attrs){
            // add new function to the original scope(controller scope)
        	scope.showBModal = function(){
                attrs.visible = !attrs.visible
                if (attrs.visible){
                    $(element).modal();
                }else{
                    $(element).modal({
                        show: false
                    });
                }
        	}
            scope.$watch(attrs.visible, function(){
                scope.showBModal();
            })

            $(element).bind("hide.bs.modal", function () {
                // use the same scope as the outter value
                scope.modalVisible = false;
                if (!scope.$$phase && !scope.$root.$$phase)
                    scope.$apply();
            });
        }
    }
})