app.directive('signForm', function () {
  return {
    restrict: 'E',
    scope: {
      signIn: '&in',
      signUp: '&up'
    },
    templateUrl: 'views/partials/_sign-form.html',
    controller: function ($scope, $element, $attrs) {
      $scope.returningUser = true;
    },
    link: function (scope, el, attrs) {}
  }
});