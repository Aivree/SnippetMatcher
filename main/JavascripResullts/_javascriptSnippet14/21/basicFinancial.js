app.directive('basicFinancial' , function(){
    return {
        templateUrl: "components/basicFinancial/dir-basic-financial.html",
        controller: 'basicFinancialCtrl',
        link: function(scope, element, attrs) {
            scope.data = attrs.data;
        }
    };
});
