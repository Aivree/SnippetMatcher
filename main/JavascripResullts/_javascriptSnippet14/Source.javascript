app.controller('modelController', function($scope, $attrs) {
    if (!$attrs.model) throw new Error("No model for modelController");

    // Initialize $scope using the value of the model attribute, e.g.,
    $scope.url = "http://example.com/fetch?model="+$attrs.model;
})

<div ng-controller="modelController" model="foobar">
  <a href="{{url}}">Click here</a>
</div>