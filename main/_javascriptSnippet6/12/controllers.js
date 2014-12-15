'use strict';

/* Controllers */
var database;

var module = angular.module('myApp.controllers', []).controller(
	'TwitterOauthCallbackController', TwitterOauthCallbackController)
	.controller('TwitterAccountController', TwitterAccountController)
	.controller('ETradeAccountController', ETradeAccountController).controller(
		'NotesController', NotesController).controller('YouCanTweetThis',
		TwitterAccountController).controller('PaperTradeController',
		PaperTradeController).controller('GoogleController', GoogleController)
	.controller('LinkedInController', LinkedInController).controller(
		'MongoController', MongoController);

configuration.pages.forEach(function (page) {
	var controller = page + 'Controller';
	module.controller(controller, window[controller]);
});

function NotesController($scope, $http, user, $routeParams, notesService) {
	user.getCurrent().then(
		function(currentUser) {
			var url;
			database = currentUser.email;
			database = 'note';
			if ($routeParams.id) {
				url = '/npserver/db/{0}/{1}'.format(database, $routeParams.id);
			} else {
				url = '/npserver/db/{0}?oid=false&orderBy={updateTime:-1}'
					.format(database);
			}
			$http({
				method : 'GET',
				url : url
			}).success(function(data, status, headers, config) {
				if ($routeParams.id) {
					$scope.article = data;
				} else {
					$scope.articles = data;
				}
			}).error(function(data, status, headers, config) {
				alert('{0} {1} {2}'.format(url, data, status));
			});
		});
	$scope.save = function() {
		var oid = $scope.article._id.$oid;
		$http.put('/npserver/db/{0}/{1}'.format(database, oid), $scope.article);
	};
	$scope.about = function() {
	};
	//
}

function TwitterAccountController($scope, $http, user, $routeParams,
	notesService) {
	notesService.loadAccounts($http, $scope);
	if ($routeParams.access_token) {
		$scope.account = {
			email : $scope.email,
			created : new Date().getTime(),
			type : 'etrade',
			access_token : $routeParams.access_token,
			secret : $routeParams.secret,
			name : 'New Account'
		};
	}
	$scope.newAccount = function() {
		var callback = window.location.toString().match(/(.*\/).*#/)[1]
			+ 'twitter_callback.html';
		$http.get(
			'/ape/coconut/twitter/authorize?callback={0}'.format(callback))
			.success(function(data, status, headers, config) {
				localStorage.setItem(data.token, data.secret);
				window.location = data.location;
			});
	};
	$scope.saveAccount = function() {
		$http.post('/npserver/db/account?oid=false', $scope.account).success(
			function() {
				window.location.hash = '/twitter';
			});
	};
	$scope.tweet = function(message) {
		var body = {
			access_token : $scope.account.access_token,
			secret : $scope.account.secret,
			status : htmlToPlaintext(message || $scope.status)
		};
		$http.post('/ape/coconut/twitter/status', body).success(function() {
			alert('posted');
		});
	};
	window.$http = $http;
}
function ETradeAccountController($scope, $http, user, $routeParams,
	notesService, etradeService) {
	window.$http = $http;
	var email = '';
	user.getCurrent().then(function(currentUser) {
		$scope.email = currentUser.email;
		etradeService.init($scope, $http, $scope.account);
	});
	$scope.newAccount = function() {
		$http.get('/ape/coconut/etrade/authorize').success(
			function(data, status, headers, config) {
				$scope.newEtradeAccount = {
					email : $scope.email,
					created : new Date().getTime(),
					type : 'etrade',
					request_token : data.request_token,
					request_token_secret : data.request_token_secret
				};
				window.open(data.location);
			});
	};

	$scope.exchangeCode = function(message) {
		var url = '/ape/coconut/etrade/callback?request_token={0}&oauth_verifier={1}&request_token_secret={2}'
			.format(
				encodeURIComponent($scope.newEtradeAccount.request_token),
				$scope.newEtradeAccount.code,
				encodeURIComponent($scope.newEtradeAccount.request_token_secret));
		$http
			.get(url)
			.success(
				function(data) {
					$scope.newEtradeAccount.access_token = data.access_token;
					$scope.newEtradeAccount.access_token_secret = data.access_token_secret;
					delete $scope.newEtradeAccount.code;
					delete $scope.newEtradeAccount.request_token;
					delete $scope.newEtradeAccount.request_token_secret;
					console.log(data);
					$scope.etradeAccount = $scope.newEtradeAccount;
					$scope.etradeReady = true;
					$http.post('/npserver/db/account', $scope.etradeAccount)
						.success(function(data) {
							console.log(data);
						});

				});
	};
}

function TwitterOauthCallbackController($location, $scope, $http, user,
	$routeParams) {
	var url = '/ape/coconut/twitter/callback?oauth_token={0}&oauth_verifier={1}&secret={2}'
		.format(encodeURIComponent($routeParams.oauth_token),
			encodeURIComponent($routeParams.oauth_verifier),
			encodeURIComponent(localStorage.getItem($routeParams.oauth_token)));
	$http
		.get(url)
		.success(
			function(data) {
				window.location = 'index.html#/twitter/account/new?access_token={0}&secret={1}'
					.format(data.token, data.secret);
			});
}

function PaperTradeController($scope, $http) {
	var account, paperTradeAccount = localStorage.getItem('paperTradeAccount');
	if (paperTradeAccount) {
		account = JSON.parse(paperTradeAccount);
	} else {
		account = {
			shortBalance : 0,
			longBalance : 0,
			balance : 0,
			profit : 0,
			positions : []
		};
	}
	$scope.account = account;
	$scope.quotes = {
		symbols : 'QQQ,FB',
		data : {}
	};
	$scope.order = {
		symbol : 'QQQ',
		quantity : 1000
	};
	$scope.reset = function() {
		$scope.account.balance = 0;
		$scope.account.shortBalance = 0;
		$scope.account.longBalance = 0;
	};
	function saveQuote(quote) {
		$scope.quotes.data[quote._id] = quote;
	}
	function downloadQuotes() {
		function stem(symbols) {
			return '"'
				+ symbols.replace(/ /g, '').toUpperCase().split(',')
					.join('","') + '"';
		}
		var symbols = stem($scope.quotes.symbols), url = 'http://ntrsystem.com/npserver/db/quotes?query={"_id":{"$in":[{0}]}}'
			.format(symbols);
		$http.get(url).success(function(data) {
			setTimeout(downloadQuotes, 5000);
			if (angular.isArray(data)) {
				angular.forEach(data, saveQuote);
			} else {
				saveQuote(data);
			}
		});
	}
	downloadQuotes();
	function updateProfit(position) {
		if (position.quantity > 0) {
			position.profit = (getBid(position.symbol) - position.cost)
				* position.quantity;
		} else if (position.quantity < 0) {
			position.profit = (getAsk(position.symbol) - position.cost)
				* position.quantity;
		}
	}
	function updateProfits() {
		angular.forEach($scope.account.positions, updateProfit);
	}
	setInterval(updateProfits, 1000);
	function getAsk(symbol) {
		if (!$scope.quotes.data[symbol])
			throw "Invalid symbol " + symbol;
		return $scope.quotes.data[symbol].ask;
	}
	function getBid(symbol) {
		if (!$scope.quotes.data[symbol])
			throw "Invalid symbol " + symbol;
		return $scope.quotes.data[symbol].bid;
	}
	function tradeOpen(symbol, quantity) {
		symbol = symbol.toUpperCase();
		var cost = quantity > 0 ? getAsk(symbol) : getBid(symbol);
		account.balance -= quantity * cost;
		if (quantity > 0) {
			account.longBalance -= quantity * cost;
		} else {
			account.shortBalance -= quantity * cost;
		}
		account.positions.push({
			created : new Date().getTime(),
			symbol : symbol,
			cost : cost,
			quantity : quantity,
			profit : 0
		});
		save();
	}
	function tradeClose(symbol, remaining) {
		angular
			.forEach(
				$scope.account.positions,
				function(position) {
					function sameSign(a, b) {
						return (a > 0 && b > 0) || (a < 0 && b < 0);
					}
					if (position.symbol === symbol
						&& sameSign(remaining, position.quantity)) {
						var available = (Math.abs(position.quantity) < Math
							.abs(remaining)) ? position.quantity : remaining, cost = available < 0 ? getAsk(symbol)
							: getBid(symbol), amount = available * cost;
						account.balance += amount;
						if (available > 0) {
							account.longBalance += amount;
						} else {
							account.shortBalance += amount;
						}
						position.quantity -= available;
						remaining -= available;
						if (position.quantity === 0) {
							updateProfit(position);
							account.profit += position.profit;
						}
					}
				});
		save();
	}
	function save() {
		localStorage
			.setItem('paperTradeAccount', angular.toJson(account, true));
	}
	$scope.buy = function() {
		tradeOpen($scope.order.symbol, +1 * $scope.order.quantity);
	};
	$scope.sell = function() {
		tradeClose($scope.order.symbol.toUpperCase(), +1
			* $scope.order.quantity);
	};
	$scope.short = function() {
		tradeOpen($scope.order.symbol, -1 * $scope.order.quantity);
	};
	$scope.cover = function() {
		tradeClose($scope.order.symbol.toUpperCase(), -1
			* $scope.order.quantity);
	};
	$scope.setOrderSymbol = function(symbol) {
		$scope.order.symbol = symbol;
	};
}

function GoogleController($scope, $http) {
	$http
		.get(
			'/ape/coconut/google/rest/ya29.1.AADtN_Vb9ZPLqdhgVB859L-VzIiqiaqo5EkCCazkbgTmK22GyofZ88tZcpfqr6BqQkYwkC9QyNvP842ExyjfOYFt8Ww48AVuTk_naGU69CPSDUzTx3aPNKai8iNqJrGAVw/https://www.google.com/m8/feeds/contacts/default/full')
		.success(function(data) {
			$scope.google = data;
		});
}

function LinkedInController($scope, $http) {
	user.getCurrent().then(
		function(currentUser) {
			$scope.connectLinkedIn = function() {
				window.open('/ape/coconut/oauth2/authorize/linkedin/{0}'
					.format(currentUser.email));
			};
			$http.get(
				'/ape/coconut/oauth2/account/linkedin/{0}'
					.format(currentUser.email)).success(function(data) {
				$scope.account = data;
			});
		});
}

function MongoController($scope, $http) {
	$scope.endpoint = 'http://local.origami42.com/npserver/db';
	$scope.database = 'origami';
	$scope.collection = 'metric_instances';
	$scope.query = '{}';
	$scope.sort = '{_id:1}';
	$scope.skip = '0';
	$scope.limit = 1000;

	function successRunQuery(data, status, headers, config) {
		$scope.queryResult = data.map(function(item) {
			var result = [];
			var properties = [];
			for (var key in item) {
				result.push(key + ' : ' + JSON.stringify(item[key]));
				properties.push({key : key,
					value : item[key],
					text : JSON.stringify(item[key]) });
			}
			result.sort();
			properties.sort(function (a, b) {
				return a.key > b.key;
			});
			return { text : result.join('\n'), raw : item,
				properties : properties };
		});
	}

	$scope.runQuery = function() {
		$http.get(
				'{0}/{1}?database={2}&query={3}&limit={4}&orderBy={5}&skip={6}&oid=true'
						.format($scope.endpoint, $scope.collection,
								$scope.database, $scope.query, $scope.limit,
								$scope.sort, $scope.skip)).success(
				successRunQuery);
		$('title').html($scope.collection);
	};

	$scope.insert = function(expression) {
		insertAtCursor($('textarea')[0], expression);
	};

	$scope.addFilter = function (data) {
		console.log(data);
		var json = JSON.parse($scope.query);
		json[data.key] = data.value;
		var jsonString = JSON.stringify(json, undefined, 4);
		$scope.query = '{\n' + jsonString.substring(1, jsonString.length-1)
			+ '\n}';
	};

	$http.get('{0}?database={1}'.format($scope.endpoint,$scope.database)).success(function (data) {
		$scope.collections = data;
	});
}

function MetricsController($http, $scope) {
	$scope.metrics = [];
	$http.get('/npserver/db/metrics?database=analytics').success(function (data) {
		$scope.metrics = data;
		console.log(data);
	});
	$scope.search = function() {
		
	}
}
// //////////////////////////////////////////////////////////////////////////////
