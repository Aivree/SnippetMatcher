app.directive('loginForm', function () {
  return {
    restrict: 'E',
    templateUrl: 'views/partials/_login-form.html',
    controller: function ($scope, $element, $attrs) {},
    link: function (scope, el, attrs) {
      el.bind('submit', function (e) {
        scope.signIn()(scope.user, 'in');
      });
    }
  }
});