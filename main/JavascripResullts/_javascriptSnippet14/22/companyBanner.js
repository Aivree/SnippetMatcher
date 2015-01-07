app.directive('companyBanner' , function(){
    return {
        templateUrl: "components/companyBanner/dir-company-banner.html",
        controller: 'companyBannerCtrl',
        link: function(scope, element, attrs) {
            scope.data = attrs.data;
        }
    };
});
