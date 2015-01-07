app.directive('registerForm', function () {
  return {
    restrict: 'E',
    templateUrl: 'views/partials/_register-form.html',
    controller: function ($scope, $element, $attrs) {},
    link: function (scope, el, attrs) {
      el.bind('submit', function (e) {
        scope.signUp()(scope.user, 'up');
      });
    }
  }
});