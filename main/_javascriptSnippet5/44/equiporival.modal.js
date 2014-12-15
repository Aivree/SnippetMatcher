(function() {
    'use strict';

    angular
        .module('core.equiporival')
        .controller('ModalCrearEquipoRivalInstanceCtrl', function ($scope, $modalInstance, $http ) {


 		 $scope.ok = function (equipo) {

 		 	console.log(equipo)

 		 	    $http({
                  url: 'http://localhost:8090/UDEM/equiporival',
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: equipo.Nombre },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                 
                    console.log(data);
                    $modalInstance.close(data);
                })

  		 	
  		 };

 		 $scope.cancel = function () {
 		 	console.log('Entro a cancel de equipo');
   		 	$modalInstance.dismiss('cancel');
 		 };
	})
        .controller('ModalEditarEquipoRivalInstanceCtrl', function ($scope, $modalInstance, $http,EquipoRival ) {
          console.log(EquipoRival);
          $scope.equiporival=EquipoRival;
     $scope.ok = function (equipo) {

      console.log(equipo)

          $http({
                  url: 'http://localhost:8090/UDEM/UPDATE/equiporival/'+equipo.Idrival,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: equipo.Nombre,Logo:equipo.Logo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                 
                    console.log(data);
                    $modalInstance.close(data);
                })

        
       };

     $scope.cancel = function () {
      console.log('Entro a cancel de equipo');
        $modalInstance.dismiss('cancel');
     };
  });


})();