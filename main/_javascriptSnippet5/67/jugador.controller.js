(function() {
    'use strict';

    angular
        .module('core.jugador')
        .controller('JugadorCtrl', function($scope, $http, $location, $log, $modal, jugadoresCache) {

           
          // Funcion para obtener todos los jugadores con los datos en cahe
          $http.get("http://localhost:8090/UDEM/jugador", 
            {cache: jugadoresCache }
            )
            .success(function (data) {
              console.log(data)
                $scope.jugadores = data;
            });



    // Funcion para agregar a un jugador     
    $scope.addJugador = function(jugador) {
    console.log("Add Jugador")

    $http({
                  url: 'http://localhost:8090/UDEM/jugador',
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: jugador.Nombre,
                          Apellidopaterno : jugador.Apellidopaterno,
                          Apellidomaterno : jugador.Apellidomaterno,
                          Posicion : jugador.Posicion
                           },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                    console.log(data);
                })


    };

   // Funcion para agregar a un jugador     
    $scope.borrarJugador = function(jugador) {
    console.log("Borrar")


    $http({
                  url: 'http://localhost:8090/UDEM/DELETE/jugador/' + jugador.Id,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: jugador.id,
                           },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                  console.log(data)
                    $scope.jugadores.splice( $scope.jugadores.indexOf(jugador), 1 );
                    console.log("Se borro jugador");
                })


    };

    // Funcion para abrir el modal de Crear jugador
    $scope.openModalCrear = function (size) {

    var modalInstance = $modal.open({
      templateUrl: 'ModalCrearJugador.html',
      controller: 'ModalCrearJugadorInstanceCtrl',
      size: size
    });

    modalInstance.result.then(function (jugador) {
      $scope.jugadores.push(jugador)
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };
      // Funcion para abrir el modal de Crear jugador
    $scope.openModalEditar = function (size,jugador) {

    var modalInstance = $modal.open({
      templateUrl: 'ModalEditarJugador.html',
      controller: 'ModalEditarJugadorInstanceCtrl',
      size: size,
            resolve: {
        jugador: function () {
          return jugador;
        }}
    });

    modalInstance.result.then(function (jugador) {

    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };


});


})();