

angular.module('soundcloudPlayerApp')
  .directive('scArtwork', function() {
    return {
      restrict: 'E',
      scope: {
        track: '='
      },
      controller: function ($scope, $attrs) {

        $scope.size = $attrs.size;

        $scope.$watch('track', function () {
          if ($scope.track){
            $scope.artwork_url = $scope.track.artwork_url.replace('large', 't' + $attrs.size + 'x' + $attrs.size);
            console.log($scope.artwork_url);
          }
        });

      },
      template:
        '<div class="artwork" style="background-image:url({{ artwork_url }}); width: {{ size }}px; height: {{ size }}px"></div>'
    };
  });
