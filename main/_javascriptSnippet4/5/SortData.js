ngReviews.factory('sortData', function ($http) {
	return {
		getSort : function (successcb) {
			$http({method : 'GET', url : 'json/SortData.json' }).
				success(function (data, status, headers, config) {
					successcb(data);
				}).
				error(function (data, status, headers, config) {
					$log.warn(data, status, headers, config);
				});
		}
	};
});