(function() {
    'use strict';

    angular
        .module('core.equipo')
        .controller('ModalCrearEquipoInstanceCtrl', function ($scope, $modalInstance, $http ) {


 		 $scope.ok = function (equipo) {

 		 	console.log(equipo)

 		 	    $http({
                  url: 'http://localhost:8090/UDEM/equipo',
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { nombre: equipo.Nombre },

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

