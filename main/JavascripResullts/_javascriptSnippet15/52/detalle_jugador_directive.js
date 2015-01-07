angular.module('app')
	.directive('detalleJugador', function() {
		return {
			restrict : 'E',
			scope:{
				jugador: '='
			},
			templateUrl : '/detalle-jugador',
		};
	});
	