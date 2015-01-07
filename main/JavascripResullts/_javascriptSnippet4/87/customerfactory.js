myApp.factory('CustomerFactory', function($http){
	var factory = {};
	factory.getCustomers = function(callback) {
		$http({method:'POST',url:'/getCustomers'}).success(function(data,status,headers,config){
			var customers = data.cust;
			callback(customers);
		}).error(function(data,status,headers,config){
			console.log(status);
		});
	}
	factory.addNewCustomer = function(info,callback) {
		$http({method:'POST',url:'/addCustomer',data:info}).success(function(data,status,headers,config){
			console.log(data);
			callback();
		}).error(function(data,status,headers,config){
			console.log(status);
		});
	}
	factory.deleteOldCustomer = function(info,callback) {
		$http({method:'POST',url:'/deleteCustomer',data:info}).success(function(data,status,headers,config){
			console.log(data);
			callback();
		}).error(function(data,status,headers,config){
			console.log(status);
		});
	}
	return factory;
});