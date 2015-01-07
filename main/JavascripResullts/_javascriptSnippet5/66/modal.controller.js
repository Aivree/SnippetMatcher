(function() {
    'use strict';

    angular
        .module('shared.alert')
////////////////////////controller pais
        .controller('ModalInstanceCtrl', function ($scope, $modalInstance, items, $http) {
          console.log(items);
  $scope.items = items;
  $scope.selected = {
   // item: $scope.items[0]
  };

  $scope.ok = function (data) {
    console.log(data);
    $http({
                  url: 'http://localhost:8090/pais/update/'+data.id,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: data.nombre },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                 
                    console.log(data);
                    //$scope.data.splice(index, 1);
                })

                $scope.selected.item="chingon";
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  })
////////////////////contoroller Liga

  .controller('LigaModalInstanceCtrl', function ($scope, $modalInstance, items, $http) {
          console.log(items);
  $scope.items = items;
  $scope.selected = {
   // item: $scope.items[0]
  };

  $scope.ok = function (data) {
    console.log(data);
         var nombre = data.Nombre;
         var pais = data.Pais.id;
         

         $http({
             url: 'http://localhost:8090/liga',
             method: "POST",
             transformRequest: function (obj) {
                 var str = [];
                 for (var p in obj)
                     str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                 return str.join("&");
             },
             data: { Nombre: nombre, Idpais: pais },

             headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
         }).success(function (data) {
            $scope.selected.item=data;
            $modalInstance.close($scope.selected.item);
           })



  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  })

















/////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
      .controller('ModalCtrl', function ($scope, $modal, $log) {

  $scope.items = ['item1', 'item2', 'item3'];

  $scope.open = function (size) {

    var modalInstance = $modal.open({
      templateUrl: 'myModalContent.html',
      controller: 'ModalInstanceCtrl',
      size: size,
      resolve: {
        items: function () {
          return $scope.items;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };

});


})();