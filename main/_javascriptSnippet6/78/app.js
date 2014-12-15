'use strict';
// version 1.4 code 462
var _VERSION = 462;
//var _PLATFORM = "debug";
var _PLATFORM = "android";
//var _PLATFORM = "ios";
var _URL = "https://m.people-projects-it.com";
//var _URL = "https://m.ber.menuplus.de";
//var _URL = "https://m-proxy.people-projects-it.com";
//var _URL = "https://10.21.0.11";
/**
 * german localization
 */
(function ($) {
    $.mobiscroll.i18n.de = $.extend($.mobiscroll.i18n.de, {
        dateFormat: 'dd.mm.yy',
        dateOrder: 'ddmmyy',
        dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
        dayNamesShort: ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'],
        dayText: 'Tag',
        hourText: 'Stunde',
        minuteText: 'Minuten',
        monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
        monthNamesShort: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
        monthText: 'Monat',
        secText: 'Sekunden',
        timeFormat: 'HH:ii',
        timeWheels: 'HHii',
        yearText: 'Jahr',
        setText: 'OK',
        cancelText: 'Abbrechen'
    });
    $.event.special.swipe.horizontalDistanceThreshold = 50;
})(jQuery);
angular.module("ngLocale", [], [
		"$provide",
		function($provide) {
			var PLURAL_CATEGORY = {
				ZERO : "zero",
				ONE : "one",
				TWO : "two",
				FEW : "few",
				MANY : "many",
				OTHER : "other"
			};
			$provide.value("$locale", {
				"NUMBER_FORMATS" : {
					"DECIMAL_SEP" : ",",
					"GROUP_SEP" : ".",
					"PATTERNS" : [ {
						"minInt" : 1,
						"minFrac" : 0,
						"macFrac" : 0,
						"posPre" : "",
						"posSuf" : "",
						"negPre" : "-",
						"negSuf" : "",
						"gSize" : 3,
						"lgSize" : 3,
						"maxFrac" : 3
					}, {
						"minInt" : 1,
						"minFrac" : 2,
						"macFrac" : 0,
						"posPre" : "",
						"posSuf" : " \u00A4",
						"negPre" : "-",
						"negSuf" : " \u00A4",
						"gSize" : 3,
						"lgSize" : 3,
						"maxFrac" : 2
					} ],
					"CURRENCY_SYM" : "€"
				},
				"pluralCat" : function(n) {
					if (n == 1) {
						return PLURAL_CATEGORY.ONE;
					}
					return PLURAL_CATEGORY.OTHER;
				},
				"DATETIME_FORMATS" : {
					"MONTH" : [ "Januar", "Februar", "März", "April", "Mai",
							"Juni", "Juli", "August", "September", "Oktober",
							"November", "Dezember" ],
					"SHORTMONTH" : [ "Jan", "Feb", "Mär", "Apr", "Mai", "Jun",
							"Jul", "Aug", "Sep", "Okt", "Nov", "Dez" ],
					"DAY" : [ "Sonntag", "Montag", "Dienstag", "Mittwoch",
							"Donnerstag", "Freitag", "Samstag" ],
					"SHORTDAY" : [ "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.",
							"Sa." ],
					"AMPMS" : [ "vorm.", "nachm." ],
					"medium" : "dd.MM.yyyy HH:mm:ss",
					"short" : "dd.MM.yy HH:mm",
					"fullDate" : "EEEE, d. MMMM y",
					"longDate" : "d. MMMM y",
					"mediumDate" : "dd.MM.yyyy",
					"shortDate" : "dd.MM.yy",
					"mediumTime" : "HH:mm:ss",
					"shortTime" : "HH:mm"
				},
				"id" : "de-de"
			});
		} ]);
/**
 * The main ISS Mobile App app module.
 * (c) PPIT 2013-2014
 * 
 * @type {angular.Module}
 */
var ppitapp = angular.module('ppitapp', ['ngResource', 'ngSanitize']).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/login', {
				templateUrl : 'login.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/kalenda/:Type/:Shift', {
				templateUrl : 'kalenda.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/kalendb/:Type/:Shift', {
				templateUrl : 'kalendb.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/profile', {
				templateUrl : 'profile.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/konto', {
				templateUrl : 'konto.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/kurse', {
				templateUrl : 'kurse.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/kursedetail/:kursId', {
				templateUrl : 'kursedetail.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/start', {
				templateUrl : 'start.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/error', {
				templateUrl : 'error.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/ppitkontakt', {
				templateUrl : 'ppitkontakt.html',
				jqmOptions : {
					transition : 'none'
				}
			}).when('/vertretungsplan', {
				templateUrl : 'vertretungsplan.html',
				jqmOptions : {
					transition : 'none'
				}
			}).otherwise({
				redirectTo : '/login'
			});
		} ]);

/* SERVICES */

/*
 * Application settings storage
 * and start page helper
 */
var SettingsSvc = ppitapp.factory('Settings', ['Navigation', 'Auth', '$rootScope', '$window',
                             function(Navigation, Auth, $rootScope, $window) {
	//console.log("Settings service start");
	var Settings = {};
	// customer id is hardcoded now - will be upgraded to real values
	// in later versions
	Settings.customerID = 1;
	Settings.getCustomerID = function() {
		return Settings.customerID;
	};
	// sleep duration in milliseconds
	Settings.sleepDuration = 60000;
	// current sleep timestamp
	Settings.sleepTimestamp = 0;
	Settings.getSleepTime = function() {
		var sleep = Settings.load("sleep");
		var d = new Date();
		if(sleep === null) sleep = d.getTime();
		return sleep;
	};
	Settings.setSleepTime = function() {
		var d = new Date();
		Settings.save("sleep", d.getTime());
	};
	// sleep handler
	Settings.sleepHandler = function() {
		if(_PLATFORM == "ios") {
			$window.setTimeout(function() { // iOS wrapper
				Settings.setSleepTime();
			}, 0);
		} else {
			Settings.setSleepTime();
		}
	};
	
	Settings.realResumeHandler = function() {
		//console.log("Settings.resumeHandler");
		var d = new Date();
		if(d.getTime() - Settings.getSleepTime() > Settings.sleepDuration) {
			//console.log("timeout!");
			//alert("Settings.resumeHandler: timeout expired");
			Auth.relogin(function() {
				/*
				var p = Navigation.current.page;
				var currentPage = angular.isDefined(p)? p : "";
				var startPage = Settings.getStart();
				if(currentPage != startPage) {
					Navigation.go(startPage);
					$rootScope.$apply();
				}
				*/
				//alert("relogin success");
				var startPage = Settings.getStart();
				Navigation.go(startPage);
				$rootScope.$apply();
			});
			return true;
		} else {
			//alert("no timeout");
			return false;
		}
	};
	// resume handler
	Settings.resumeHandler = function() {
		//alert("Settings.resumeHandler!");
		if(_PLATFORM == "ios") {
			$window.setTimeout(function() { // iOS wrapper
				Settings.realResumeHandler();
			}, 0);
		} else {
			Settings.realResumeHandler();
		}
	};

	/*
	 * Current date saving/loading functions
	 */
	Settings.setDate = function(d) {
		//console.log("Settings.setDate: ",d);
		Settings.save("datum", d);
	};
	
	Settings.getDate = function() {
		var d = Settings.load("datum");
		if(d === null) {
			//console.log("date is empty");
			d = Settings.getToday();
		}
		return d;
	};
	
	Settings.getToday = function() {
		var dt = new Date();
		var d = dt.toISOString();
		d = d.slice(0,10);
		return d;
	};
	
	/*
	 * Start page user settings saving/loading
	 */
	Settings.setStart = function(p) {
		Settings.save("start", p);		
	};
	
	Settings.getStart = function() {
		var st = Settings.load("start");
		// if the value is not saved
		if(st === null) {
			// default start page - "start"
			st = "start";
		}
		return st;
	};
	
	// initialization of local storage
	Settings.ls = window.localStorage;
	if (!Settings.ls) {
		//console.log('Settings service: local storage not available.');
		//showError("Achtung: lokale Speicherung ist nicht verfügbar!");
	}
	
	// internal saving/loading functions
	Settings.save = function(k,v) {
		Settings.ls.setItem(k, v);
	};
	
	Settings.load = function(k) {
		return Settings.ls.getItem(k);
	};
	$window.document.addEventListener("deviceready", function() {
		$window.document.addEventListener("pause", Settings.sleepHandler, false);
		$window.document.addEventListener("resume", Settings.resumeHandler, false);
	}, false);
	return Settings;
}]);

/* db/server requests handling service */
var DatasourceSvc = ppitapp.factory('Datasource', ['$http', 'Messages', 'Navigation', 'Auth', function($http, Messages, Navigation, Auth) {
	var DS = {};
	DS.serverURL = _URL + '/index.php';
	DS.resource = {
			'kalend': {
				method	: 'GET',
				params	: {
					'act'		: 'kalend',
					'sk'		: '@sk',
					'sd'		: '@sd',
					'ed'		: '@ed'
				}
			},
			'kalend-details': {
				method	: 'GET',
				params	: {
					'act'		: 'kalend',
					'info'		: 'details',
					'sk'		: '@sk',
					'sd'		: '@sd',
					'ed'		: '@ed'
				}
			},
			'kalend-abotag': {
				method	: 'POST',
				params	: {
					'act'		: 'kalend',
					'save'		: 'abotage',
					'sk'		: '@sk'
				}
			},
			'kalend-menue': {
				method	: 'POST',
				params	: {
					'act'		: 'kalend',
					'save'		: 'menue',
					'sk'		: '@sk'
				}
			},
			'kurse-index': {
				method	: 'GET',
				params	: {
					'act'		: 'kurse',
					'get'		: 'index',
					'sk'		: '@sk'
				}
			},
			'kurse-meine': {
				method	: 'GET',
				params	: {
					'act'		: 'kurse',
					'get'		: 'meine',
					'sk'		: '@sk'
				}
			},
			'kurse-save': {
				method	: 'POST',
				params	: {
					'act'		: 'kurse',
					'get'		: 'none',
					'sk'		: '@sk'
				}
			},
			'profile-index': {
				method	: 'GET',
				params	: {
					'act'		: 'profile',
					'sk'		: '@sk'
				}
			},
			'profile-delete': {
				method	: 'GET',
				params	: {
					'act'		: 'profile',
					'do'		: 'delete',
					'sk'		: '@sk'
				}
			},
			'profile-pwd': {
				method	: 'POST',
				params	: {
					'act'		: 'profile',
					'do'		: 'pwd',
					'sk'		: '@sk'
				}
			},
			'konto': {
				method	: 'GET',
				params	: {
					'act'		: 'konto',
					'sk'		: '@sk',
					'sd'		: '@sd',
					'ed'		: '@ed'
				}
			},
			'vertretungsplan': {
				method	: 'GET',
				params	: {
					'act'		: 'vertretungsplan',
					'sk'		: '@sk'
				}
			}
	};
	DS.requestStatus = undefined;
	DS.request = function(method, params, successHandler, failureHandler) {
		$.mobile.loading('show');
		//console.log("DS.request");
		var config = {
				method	: DS.resource[method].method
		};
		// overwrite sessionKey
		if(angular.isDefined(DS.resource[method].params.sk)) {
			DS.resource[method].params.sk = Auth.sessionKey;
			//console.log("session Key overwrite: ", Auth.sessionKey);
		}
		// compile url
		var url = DS.serverURL;
		var getParams = [];
		angular.forEach(DS.resource[method].params, function(value, key) {
			//console.log(key + ': ' + value);
			var urlPart = key + "=";
			var valueStr = value + '';
			if(valueStr.charAt(0) == '@') urlPart += params[valueStr.slice(1)];
			else urlPart += valueStr;
			//console.log(urlPart);
			getParams.push(urlPart);
		});
		var getParamsStr = getParams.join('&');
		if(getParams.length > 0) url += '?' + getParamsStr;
		//console.log("url:", url);
		config.url = url;
		// prepare POST params
		if(DS.resource[method].method == 'POST') {
			config.data = $.param(params);
			config.headers = {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'};
		}
		$http(config).success(function(data, status, headers, config) {
			//console.log("DS.request success", data, status, headers, config);
			$.mobile.loading('hide');
			if(angular.isDefined(data)) {
				if(angular.isDefined(data.fehler) && data.fehler != 0) {
					//if(method == 'kalend-menue') alert("DS.success Fehler:" + data.fehler);
					if(data.fehler == -2) {
						// we should try to make another login if credentials are saved or redirect to lgin page if they are not
						var reloginParams = [method, params, successHandler, failureHandler];
						Auth.relogin(DS.request, reloginParams);
					} else {
						//if(data.fehler )
						if(angular.isDefined(data.fehlermessage)) {
							Messages.addMessage("err", "Fehler", data.fehlermessage);
						} else {
							Messages.addMessage("err", "Fehler", "Unbekannte Fehler");
						}
					}
				} else {
					//if(method == 'kalend-menue') alert("DS.success -> successHandler");
					if(successHandler) successHandler(data);
				}
			} else {
				Messages.addMessage("err", "Fehler", "Empty server response");
			}
		}).error(function(data, status, headers, config) {
			$.mobile.loading('hide');
			//console.log("DS.request failure", data, status, headers, config);
			DS.handleError(status, data);
			if(failureHandler) failureHandler(data);
		});
	};
	DS.handleError = function(status, data) {
		DS.requestStatus = status;
		//$("#wartungPopup").popup();
		if(status == 503) {
			// WARTUNG MODUS
			//console.log("WARTUNG MODUS detected!");
			Messages.addMessage("wait");
		} else {
			Messages.addMessage("wait", "Verbindungsfehler", "App kann nicht mit " + _URL + " verbunden werden. Bitte überprüfen Sie Ihre Internetverbindung.");
		}
		Navigation.go("error");
	};
	return DS;
}]);

/* Messages service - storage for error messages and news from server */
var MessagesSvc = ppitapp.factory('Messages', [function() {
	//console.log("Messages service");
	var M = {};
	M.messages = [];
	M.messageTypes = ["err", "wait", "info", "warnung"];
	M.baseMessages = {
			"err"	: {
				"action": "refresh",
				"title"	: "Fehler",
				"text"	: "Fehler",
				"image"	: "css/images/warn.png"
			},
			"wait"	: {
				"action": "refresh",
				"title"	: "Wartung",
				"text"	: "Derzeit aktualisieren wir für Sie unser System. Deshalb steht dieser Dienst vorübergehend nicht zur Verfügung. Wir danken für Ihr Verständnis und bitten Sie es später noch einmal zu versuchen.",
				"image"	: "css/images/wartung.png"
			},
			"info"	: {
				"action": "ok",
				"title"	: "Info",
				"text"	: "",
				"image"	: "css/images/info_big.png"
			},
			"warnung"	: {
				"action": "ok",
				"title"	: "Warnung",
				"text"	: "",
				"image"	: "css/images/warn.png"
			}
	};
	M.actionType = "ok"; // "ok"/"refresh"
	M.actionHandler = undefined;
	M.setAction = function(handler) {
		M.actionHandler = handler;
	};
	M.addMessage = function(type, title, text) {
		var alreadyErr = false;
		angular.forEach(M.messages, function(mItem) {
			if(mItem.type == type) alreadyErr = true;
		});
		if(!(type == 'wait' && alreadyErr)) {
			var msg = angular.copy(M.baseMessages[type]);
			msg.id = M.messages.length;
			msg.type = type;
			if(angular.isDefined(title)) msg.title = title;
			if(angular.isDefined(text)) msg.text = text;
			M.messages.push(msg);
			M.actionType = msg.action;
		}
	};
	M.clear = function() {
		M.messages = [];
		M.actionType = "ok"; // "ok"/"refresh"
	}
	return M;
}]);

/* Auth service */
var AuthSvc = ppitapp.factory('Auth', ['$http', 'Messages', 'Navigation', function($http, Messages, Navigation) {
	//console.log('Auth service start');
	var AuthService = {};
	// user credentials
	AuthService.cred = {
		username : "",
		password : ""
	};
	AuthService.serverURL = _URL + '/index.php';
	AuthService.resource = {
			'login': {
				method	: 'POST',// hardcoded in login()
				params	: {
					'act'		: 'auth',
					'platform'	: _PLATFORM,
					'version'	: _VERSION
				}
			},
			'logout': {
				method	: 'POST',// hardcoded in logout()
				params	: {
					'act'		: 'auth',
					'logout'	: 'yes',
					'sk'		: '@sk'
				}
			}
	};
	AuthService.request = function(config, successHandler, failHandler) {
		$.mobile.loading('show');
		$http(config).success(function(data, status, headers, config) {
			$.mobile.loading('hide');
			if(angular.isDefined(data)) {
				if(angular.isDefined(data.fehler) && data.fehler != 0) {
					//console.log("Fehler: ", data);
					if(angular.isDefined(data.fehlermessage)) {
						Messages.addMessage("err", "Fehler", data.fehlermessage);
					} else {
						Messages.addMessage("err", "Fehler", "Unbekannte Fehler: " + data.fehler);
					}
					if(failHandler) failHandler(data);
				} else {
					if(successHandler) successHandler(data);
				}
			} else {
				Messages.addMessage("err", "Fehler", "Empty server response");
			}
		}).error(function(data, status, headers, config) {
			$.mobile.loading('hide');
			if(status == 503) {
				// WARTUNG MODUS
				//console.log("WARTUNG MODUS detected!");
				Messages.addMessage("wait");
			} else {
				Messages.addMessage("wait", "Verbindungsfehler", "App kann nicht mit " + _URL + " verbunden werden. Bitte überprüfen Sie Ihre Internetverbindung.");
			}
			Navigation.go("error");
			if(failHandler) failHandler(data);
		});
	};
	// user session key
	AuthService.sessionKey = "";
	// login status of current user
	AuthService.loggedIn = function() {
		return (AuthService.sessionKey != "");
	};
	// should we remember credentials or not?
	AuthService.remember = false;
	// credentials saved?
	AuthService.saved = false;
	// buttons available on start page
	// if AuthService.pages.konto == true - then konto page is available
	AuthService.pages = {};
	// additional rights
	AuthService.rights = {};
	// news loaded from server
	AuthService.news = undefined;
	// site url
	// main authorization function
	AuthService.login = function(cred, doneHandler, failHandler) {
		var url = AuthService.serverURL;
		var getParams = [];
		angular.forEach(AuthService.resource.login.params, function(value, key) {
			//console.log(key + ': ' + value);
			var urlPart = key + "=" + value;
			//console.log(urlPart);
			getParams.push(urlPart);
		});
		var getParamsStr = getParams.join('&');
		if(getParams.length > 0) url += '?' + getParamsStr;
		//console.log("url:", url);
		//console.log('login-cred:',cred);
		var config = {
				method	: 'POST',
				url		: url,
				data	: $.param(cred),
				headers	: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
		};
		AuthService.request(config, function(data) {
			if (data != "") {
				if (data.result.status == "ok") {
					// login request successfull
					//console.log('hiding page');
					AuthService.sessionKey = data.result.key;
					AuthService.pages = data.pages;
					AuthService.rights = data.rechte;
					AuthService.kontakt = data.kontakt;
					AuthService.anzahl_vertretungen = data.anzahl_vertretungen;
					setCookie('sk',AuthService.sessionKey,null,'/');
					AuthService.save(cred);
					//document.cookie = "sk=" + AuthService.sessionKey;
					//console.log( 'Auth service ok ', doneHandler);
					//$cookies.sk = AuthService.sessionKey;
					AuthService.news = undefined;
					Messages.clear();
					var news = data.nachrichten;
					if(angular.isDefined(news)) {
						AuthService.news = news;
						angular.forEach(news, function(meldung, type) {
							Messages.addMessage(type, undefined, meldung);
						});
					}
					if (doneHandler) doneHandler(data);
				} else {
					//console.log( "false login: ", data );
					if(data.result.status == 0) {
						Messages.addMessage("err", undefined, "Benutzername oder Passwort falsch!");
					} else if(data.result.status < 0){
						Messages.addMessage("err", undefined, data.fehlermessage);
					} else if(data.result.status > 0) {
						Messages.addMessage("err", undefined, "Benutzername oder Passwort falsch! Sie müssen " + data.result.status + " Sekunden warten");
					}
					if (failHandler)
						failHandler(data);
				}
			} else {
				//console.log("login empty response",	data);
				Messages.addMessage("err", undefined, "Login empty answer");
				if (failHandler)
					failHandler(data);
			}
		}, failHandler);
	};
	
	// saving credentials for further usage
	AuthService.save = function(cred) {
		if(AuthService.remember) AuthService.cred = cred;
		//console.log('AuthService.save: start.');
		var ls = window.localStorage;
		if (ls) {
			if(AuthService.remember)
				ls.setItem("cred", angular.toJson(cred));
			ls.setItem("pages", angular.toJson(AuthService.pages));
			ls.setItem("rights", angular.toJson(AuthService.rights));
			ls.setItem("kontakt", angular.toJson(AuthService.kontakt));
			ls.setItem("anzahl_vertretungen", angular.toJson(AuthService.anzahl_vertretungen));
			ls.setItem("sk", AuthService.sessionKey);
			AuthService.saved = true;
		} else {
			//console.log('AuthService.save: local storage not available.');
			Messages.addMessage("err", undefined, "Warning: local storage not available!");
			//showError("Warning: local storage not available!");
		}
	};
	
	// try to load previously saved credentials
	AuthService.load = function() {
		//console.log('AuthService.load: start.');
		var ls = window.localStorage;
		if (ls) {
			AuthService.cred = angular.fromJson(ls.getItem("cred"));
			AuthService.pages = angular.fromJson(ls.getItem("pages"));
			AuthService.rights = angular.fromJson(ls.getItem("rights"));
			AuthService.kontakt = angular.fromJson(ls.getItem("kontakt"));
			AuthService.anzahl_vertretungen = angular.fromJson(ls.getItem("anzahl_vertretungen"));
			//console.log('cred=', AuthService.cred);
			AuthService.sessionKey = ls.getItem("sk");
			//console.log('sk=', AuthService.sessionKey);
			//console.log("cookies: ",document.cookie);
			//document.cookie = "sk=" + AuthService.sessionKey;
			if (AuthService.sessionKey) {
				setCookie('sk',AuthService.sessionKey,null,'/');
				AuthService.saved = true;
			} else {
				AuthService.saved = false;
				//console.log('AuthService.load: session key empty.');
			}
		} else {
			//console.log('AuthService.load: local storage not available.');
			Messages.addMessage("err", undefined, "Warning: local storage not available!");
			//showError("Warning: local storage not available!");
		}
	};
	
	// logout function
	AuthService.logout = function(redirectFunc) {
		//console.log("AuthService.logout");
		var params = {"sk":AuthService.sessionKey};
		// compile url
		var url = AuthService.serverURL;
		var getParams = [];
		angular.forEach(AuthService.resource.logout.params, function(value, key) {
			//console.log(key + ': ' + value);
			var urlPart = key + "=";
			var valueStr = value + '';
			if(valueStr.charAt(0) == '@') urlPart += params[valueStr.slice(1)];
			else urlPart += valueStr;
			getParams.push(urlPart);
		});
		var getParamsStr = getParams.join('&');
		if(getParams.length > 0) url += '?' + getParamsStr;
		//console.log("url:", url);
		var config = {
			method	: 'POST',
			url		: url	
		};
		AuthService.request(config, function(data) {
			AuthService.clear();
			if(redirectFunc) redirectFunc();
		}, function(data) {
			AuthService.clear();
			if(redirectFunc) redirectFunc();
		});
	};
	
	// clear all data from Auth and clear local storage if available
	AuthService.clear = function() {
		//console.log('AuthService.clear.');
		//document.cookie = "";
		AuthService.cred = {
			username : "",
			password : ""
		};
		setCookie('sk',"",null,'/');
		AuthService.sessionKey = "";
		AuthService.saved = false;
		var ls = window.localStorage;
		if(ls) {
			//ls.removeItem("cred");
			ls.removeItem("sk");
		}
	};
	
	AuthService.reloginCallers = [];
	AuthService.reloginInPorgress = false;
	
	AuthService.relogin = function(callerFunc, callerParams) {
		AuthService.reloginCallers.push({
			"f" : callerFunc,
			"p"	: callerParams
		});
		if(!AuthService.reloginInPorgress) {
			var cred = undefined;
			if(AuthService.remember) {
				// if credentials is remembered
				cred = AuthService.cred;
			} else {
				// try to load them from local storage
				AuthService.load();
				if(angular.isDefined(AuthService.cred) && AuthService.cred !== null) cred = AuthService.cred;
			}
			if(angular.isDefined(cred)) {
				AuthService.reloginInPorgress = true;
				// try to login
				AuthService.login(cred, function(data) {
					AuthService.save(cred);
					if(angular.isDefined(AuthService.reloginCallers)) {
						while (AuthService.reloginCallers.length) {
							// get handler
							var o = AuthService.reloginCallers.shift();
							// run it with set of parameters as array
							o.f.apply(this, o.p);
						}
					}
					//if(angular.isDefined(callerFunc)) callerFunc.apply(this, callerParams);
					/*
					angular.forEach(AuthService.reloginCallers, function(callerObject) {
						callerObject.f.apply(this, callerObject.p);
					});
					AuthService.reloginCallers = [];
					*/
					AuthService.reloginInPorgress = false;
				}, function(data) {
					AuthService.reloginInPorgress = false;
					AuthService.reloginCallers = [];
					AuthService.clear();
					Navigation.go("error");
				});
			} else {
				AuthService.reloginCallers = [];
				Messages.addMessage("err", undefined, "Session expired.");
				AuthService.clear();
				Navigation.go("login");
			}
		}
	};
	return AuthService;
}]);

/* Kalender service v.2 */
var KalendSvc = ppitapp.factory('Kalend2', ['Auth', 'Datasource', '$window', function(Auth, Datasource, $window) {
	//console.log("Kalender service v.2 loading.");
	// define the local namespace
	var Kalender = {};
	
	/*
	 * general properties definition
	 */
	// init flag
	Kalender.started = false;
	// currently selected week
	Kalender.currentWoche = 0;
	// today date
	Kalender.today = new Date();
	// monday and sunday dates for current week
	// they should be calculated on every refresh (!!!)
	// they are used to calculate dateranges for requests
	// starting date of the current week
	Kalender.monday = new Date();
	// ending date of the current week
	Kalender.sunday = new Date();
	
	/*
	 * cache related properties
	 */
	// refresh flag
	Kalender.needRefresh = true;
	// number of weeks in both sides we need to cache
	Kalender.cacheSize = 1;
	// left border for cached data always < 0
	Kalender.leftCacheBorder = 0;
	// right border for cached data always > 0
	Kalender.rightCacheBorder = 0;
	// array of weeks
	Kalender.wochen = [];
	// left and right borders of request
	Kalender.requestStart = 0;
	Kalender.requestEnd = 0;
	// cache duration in milliseconds
	Kalender.cacheDuration = 60000;
	// current cache timestamp
	Kalender.cacheTimestamp = Kalender.today.getTime();
	//
	Kalender.cacheRefreshHandler = undefined;
	// precached array of received data
	Kalender.tageRequestResult = [];
	Kalender.detailRequestResult = [];
	
	/*
	 * event handling properties
	 */
	// error code to return to listener
	Kalender.errorCode = 0;
	// registered error handlers
	Kalender.tagErrorHandlers = [];
	Kalender.detailErrorHandlers = [];
	// success handling functions
	Kalender.tagSuccessHandlers = [];
	Kalender.detailSuccessHandlers = [];

	/*
	 * refreshing of current data depending on cacheDuration
	 */
	Kalender.onResume = function() {
		//console.log("Kalender.onResume");
		var currentDate = new Date();
		var currentTime = currentDate.getTime();
		if(currentTime - Kalender.cacheTimestamp > Kalender.cacheDuration) {
			//console.log("Kalender cache expired");
			//alert("Cache expired. Requesting new data.");
			Kalender.needRefresh = true;
			if(Kalender.cacheRefreshHandler != undefined) {
				Kalender.cacheRefreshHandler();
			} else {
				Messages.addMessage("err", undefined, "redirect error!");
			}
		}
		//alert("resume!");
	};
	
	/*
	 * initialization function - redefines all core properties
	 * in case of first run or refresh
	 */
	Kalender.init = function() {
		//console.log("Kalender2.init");
		Kalender.today = new Date();
		// starting date of the week
		Kalender.monday = new Date();
		// ending date of the week
		Kalender.sunday = new Date();
		// calculate shift in days for monday and sunday of current week
		// or next week if today is weekend
		var weekendShift = 0;
		if (Kalender.today.getDay() == 6 || Kalender.today.getDay() == 0) {
			// sunday or saturday
			weekendShift = (Kalender.today.getDay() == 0) ? 1 : 2;
		} else {
			// all other days
			weekendShift = 0 - Kalender.today.getDay() + 1;
		}
		// calculating the starting and finishing dates
		Kalender.monday.setDate(Kalender.monday.getDate() + weekendShift);
		Kalender.sunday.setDate(Kalender.sunday.getDate() + weekendShift + 6);
		// reset properties in the case of refreshing
		Kalender.tagErrorHandlers = [];
		Kalender.detailErrorHandlers = [];
		Kalender.tagSuccessHandlers = [];
		Kalender.detailSuccessHandlers = [];
		Kalender.tageRequestResult = [];
		Kalender.detailRequestResult = [];
		Kalender.wochen = [];
		Kalender.leftCacheBorder = 0;
		Kalender.rightCacheBorder = 0;
		Kalender.requestStart = 0;
		Kalender.requestEnd = 0;
		Kalender.currentWoche = 0;
		Kalender.nachricht_sekretariat = 0;
		// set flags
		Kalender.needRefresh = true;
		// add event listener for wake up of application
		// on mobile devices only once
		if(!Kalender.started)
			$window.document.addEventListener("resume", Kalender.onResume, false);
		Kalender.started = true;
	};
	
	
	/*
	 * main interface function
	 * returns requested week from cache and starts
	 * precaching of next/previous weeks
	 */
	Kalender.getWoche = function(shift) {
		//console.log('Kalender2.getWoche:', shift);
		// if we are trying to get new week - change current index
		// and start caching process, otherwise - return data from cache
		var rShift = shift;
		if(!angular.isNumber(shift)) rShift = 0; 
		if(rShift != Kalender.currentWoche || Kalender.needRefresh) {
			//console.log('Kalender2.getWoche - neue Woche!');
			// set current week index
			Kalender.currentWoche = rShift;
			// request caching
			Kalender.loadCache(rShift);
		}
		if(Kalender.wochen.length > 0) {
			// prepare resulting array
			var res = {};
			// calculate index of the reuested week in cache array
			var wIndex = rShift - Kalender.leftCacheBorder;
			//console.log('Kalender2.getWoche - wIndex:', wIndex);
			//console.log('Kalender2.getWoche - wochen[wIndex]:', Kalender.wochen[wIndex]);
			// copy requested data into resulting array
			res = angular.copy(Kalender.wochen[wIndex]);
			//console.log('Kalender2.getWoche - res:', res);
			// return resulting object
			return res;			
		} else {
			return false;
		}
	};
	
	/*
	 * main caching function
	 * here we calculating the date range to request from server
	 * and desiding if we need requesting or not
	 */
	Kalender.loadCache = function(shift) {
		//console.log('Kalender.loadCache: ', shift);
		// calculate indexes of weeks that are requested
		var leftBorder = shift - Kalender.cacheSize;
		var rightBorder = shift + Kalender.cacheSize;
		//console.log('Kalender.loadCache leftBorder:', leftBorder, ', rightBorder:', rightBorder);
		// check what are already cached
		//console.log('Kalender.loadCache preCached left border: ', Kalender.leftCacheBorder, ', right border: ', Kalender.rightCacheBorder);
		if(Kalender.needRefresh) {
			// refresh of the cache is requested
			Kalender.requestStart = Math.min(leftBorder, 0 - Kalender.cacheSize);
			Kalender.requestEnd = Math.max(rightBorder, 0 + Kalender.cacheSize);
		} else {
			// calculate the borders
			// TODO: this is very simple and ineffective way of calculating borders!
			// we should improve it later! 
			Kalender.requestStart = Math.min(leftBorder, Kalender.leftCacheBorder);
			Kalender.requestEnd = Math.max(rightBorder, Kalender.rightCacheBorder);
		}
		if((Kalender.requestStart != Kalender.leftCacheBorder) || (Kalender.requestEnd != Kalender.rightCacheBorder)) {
			// new request required!
			// calculate dates that should be loaded
			var sDate = new Date();
			var eDate = new Date();
			// calculate real dates for requested week indexes
			var sDateShift = Kalender.monday.getDate() + Kalender.requestStart * 7;
			var eDateShift = Kalender.sunday.getDate() + Kalender.requestEnd * 7;
			//sDate.setMonth(Kalender.monday.getMonth(), sDateShift);
			//eDate.setMonth(Kalender.sunday.getMonth(), eDateShift);
			sDate.setFullYear(Kalender.monday.getFullYear(), Kalender.monday.getMonth(), sDateShift);
			eDate.setFullYear(Kalender.sunday.getFullYear(), Kalender.sunday.getMonth(), eDateShift);
			//console.log('Kalender.loadCache monday:', Kalender.monday, ', sunday:', Kalender.sunday );
			//console.log('Kalender.loadCache sDateShift:', sDateShift, ', eDateShift:', eDateShift );
			//console.log('requested borders: [', Kalender.requestStart, ',', Kalender.requestEnd, ']' );
			//console.log('data requested: ', sDate, eDate );
			// request data from server!
			Kalender.request(sDate, eDate);
		} else {
			//console.log('Kalender.loadCache: requested data already cached!');
		}
	};
	
	/*
	 * function to request data from server and call proper handlers
	 * to copy received data to cache or handle errors
	 */
	Kalender.request = function(startDay, endDay) {
		//console.log('Kalender2.request');
		// calculate request url
		/*
		var url = Auth.appUrl + '/index.php?act=kalend&sk=' + Auth.sessionKey + '&sd='
				+ formatDate(startDay) + '&ed=' + formatDate(endDay);
		*/
		//console.log(url);
		var params = {
			'sk'		: Auth.sessionKey,
			'sd'		: formatDate(startDay),
			'ed'		: formatDate(endDay)
		};
		// make a request for kalender data
		Datasource.request("kalend", params, function(data) {
			// connection is ok try to parse server response
			if (data.fehler) { // server returned an error code
				Kalender.tagErrorHandler(data);
			} else if (data) {
				Kalender.needRefresh = false;
				Kalender.cacheTimestamp = Kalender.today.getTime();
				// parse and prepare for caching data
				var tageArray = new Array();
				//console.log("received: ", data);
				angular.forEach(data.kalender, function(value, index) {
					var newValue = angular.copy(value);
					// format the date
					newValue.datum = parseDate(value.datum);
					// format dates in event array
					var events = [];
					if(angular.isDefined(value.events) && value.events.length > 0) {
						angular.forEach(value.events, function(event) {
							var pEvent = angular.copy(event);
							pEvent.datum_von = parseFullDate(event.datum_von);
							pEvent.datum_bis = parseFullDate(event.datum_bis);
							var today = new Date(newValue.datum);
							if(pEvent.datum_von.toDateString() == today.toDateString()) pEvent.showTime = true;
							else pEvent.showTime = false;
							events.push(pEvent);
						});
						newValue.events = events;
					}
					// format dates in vertretungen array
					var v = [];
					if(angular.isDefined(value.vertretungsplan) && value.vertretungsplan.length > 0) {
						angular.forEach(value.vertretungsplan, function(item) {
							var newV = angular.copy(item);
							newV.datum = parseFullDate(item.datum_uhrzeit);
							v.push(newV);
						});
						newValue.vertretungsplan = v;
					}
					this.push(newValue);
				}, tageArray);
				Kalender.tageRequestResult = tageArray;
				Kalender.tagSuccessHandler();
				/*
				url = Auth.appUrl + '/index.php?act=kalend&info=details&sk='
					+ Auth.sessionKey + '&sd=' + formatDate(startDay)
					+ '&ed=' + formatDate(endDay);
				*/
				//console.log(url);
				Datasource.request('kalend-details', params, function(data) {
					//console.log("details received");
					// connection is ok try to parse server response
					if (data.fehler) { // server returned an error code
						Kalender.errorCode = data.fehler;
						Kalender.detailErrorHandler(data);
					} else if (data) {
						// parse and prepare for caching data for
						//console.log("received: ", data);
						var detailsArray = new Array();
						angular.forEach(data.kalender_details, function(value, index) {
							//console.log("parsing date: ", value);
							var newValue = angular.copy(value);
							// format the date
							newValue.datum = parseDate(value.datum);
							this.push(newValue);
						}, detailsArray);
						Kalender.detailRequestResult = detailsArray;
						Kalender.nachricht_sekretariat = data.nachricht_sekretariat;
						Kalender.detailSuccessHandler();
					} else {
						// empty response - something wrong with database
						//console.log("ups... empty data received!");
						var stubData = {
							"fehler" : 0,
							"fehlermessage" : "empty response"
						};
						Kalender.errorCode = stubData.fehler;
						Kalender.detailErrorHandler(stubData);
					}
				}, function(data) {
					// network error
					Kalender.errorCode = data.responseText;
					Kalender.detailErrorHandler(data);
				});
			} else {
				// empty response - something wrong with database
				//console.log("ups... empty data received!");
				var stubData = {
					"fehler" : 0,
					"fehlermessage" : "empty response"
				};
				Kalender.tagErrorHandler(stubData);
			}
		}, function(data) {
			// network error
			Kalender.tagErrorHandler(data);
		});
	};
	
	/*
	 * adding of error and success handlers
	 */
	Kalender.addTagErrorHandler = function(newHandler) {
		Kalender.tagErrorHandlers.push(newHandler);
	};
	
	Kalender.addTagSuccessHandler = function(newHandler) {
		Kalender.tagSuccessHandlers.push(newHandler);
	};
	
	Kalender.addDetailErrorHandler = function(newHandler) {
		Kalender.detailErrorHandlers.push(newHandler);
	};
	
	Kalender.addDetailSuccessHandler = function(newHandler) {
		Kalender.detailSuccessHandlers.push(newHandler);
	};

	/*
	 * internal error handlers
	 * it starts controllers handlers if they are defined
	 * in reverse order (older first)
	 */
	Kalender.tagErrorHandler = function(data) {
		//console.log('Kalender2.tagErrorHandler');
		var errCode = data.fehler;
		//console.log("data: ", data);
		//console.log("errCode: ", errCode);
		for(var i = Kalender.tagErrorHandlers.length; i > 0 ; i--) {
			var result = Kalender.tagErrorHandlers[i-1](errCode);
			if(result === false) break;
		}
	};
	Kalender.detailErrorHandler = function(data) {
		//console.log('Kalender2.detailErrorHandler');
		var errCode = data.fehler;
		for(var i = Kalender.detailErrorHandlers.length; i > 0 ; i--) {
			var result = Kalender.detailErrorHandlers[i-1](errCode);
			if(result === false) break;
		}
	};
	
	/*
	 * internal handlers for successfull requests
	 * caching of received data is here
	 */
	Kalender.tagSuccessHandler = function() {
		//console.log('Kalender2.tagSuccessHandler');
		// cache results first
		Kalender.cacheTagResult();
		// run registered success handlers while they return true
		// only once and in reverse order (older first)
		var l = Kalender.tagSuccessHandlers.length;
		for(var i = 0; i < l ; i++) {
			var hndlr = Kalender.tagSuccessHandlers.pop();
			var result = hndlr();
			if(result === false) break;
		}
	};
	Kalender.detailSuccessHandler = function() {
		//console.log('Kalender2.detailSuccessHandler');
		// cache results first
		Kalender.cacheDetailResult();
		// run registered success handlers while they return true
		// only once and in reverse order (older first)
		var l = Kalender.detailSuccessHandlers.length;
		for(var i = 0; i < l ; i++) {
			var hndlr = Kalender.detailSuccessHandlers.pop();
			var result = hndlr();
			if(result === false) break;
		}
		//hideError();
	};
	
	/*
	 * caching functions that copy received data to proper place in week array
	 */
	Kalender.cacheTagResult = function() {
		//console.log('Kalender2.cacheTagResult');
		//console.log('Kalender2.cacheTagResult borders: [',Kalender.leftCacheBorder,',',Kalender.rightCacheBorder,']');
		//console.log('requested borders: [', Kalender.requestStart, ',', Kalender.requestEnd, ']' );
		var wNum = Kalender.requestEnd - Kalender.requestStart + 1;
		// iterate through weeks
		for(var wIndex = 0; wIndex < wNum; wIndex++) {
			if(!Kalender.wochen[wIndex]) {
				Kalender.wochen[wIndex] = {
						'tage' : [],
						'details' : []
					};
			} else {
				Kalender.wochen[wIndex].tage = [];
			}
			// iterate through days
			for(var dIndex = 0; dIndex < 7; dIndex++) {
				Kalender.wochen[wIndex].tage.push(Kalender.tageRequestResult[wIndex*7 + dIndex]); 
			}
		}
		Kalender.leftCacheBorder = Kalender.requestStart;
		Kalender.rightCacheBorder = Kalender.requestEnd;
		//console.log('length: ',Kalender.wochen.length);
		//console.log('result: ',Kalender.wochen);
	};
	Kalender.cacheDetailResult = function() {
		//console.log('Kalender2.cacheDetailResult');
		//console.log('Kalender2.cacheDetailResult borders: [',Kalender.leftCacheBorder,',',Kalender.rightCacheBorder,']');
		//console.log('requested borders: [', Kalender.requestStart, ',', Kalender.requestEnd, ']' );
		var wNum = Kalender.requestEnd - Kalender.requestStart + 1;
		// iterate through weeks
		for(var wIndex = 0; wIndex < wNum; wIndex++) {
			if(!Kalender.wochen[wIndex]) {
				Kalender.wochen[wIndex] = {
						'tage' : [],
						'details' : []
					};
			} else {
				Kalender.wochen[wIndex].details = [];
			}
			// iterate through days
			for(var dIndex = 0; dIndex < 7; dIndex++) {
				Kalender.wochen[wIndex].details.push(Kalender.detailRequestResult[wIndex*7 + dIndex]); 
			}
		}
		Kalender.leftCacheBorder = Kalender.requestStart;
		Kalender.rightCacheBorder = Kalender.requestEnd;
		//console.log('length: ',Kalender.wochen.length);
		//console.log('result: ', Kalender.wochen);
	};
	Kalender.saveAbo = function(aboTage, success) {
		Datasource.request('kalend-abotag', {'sk' : Auth.sessionKey, 'abotage' : aboTage}, success);
	};
	Kalender.saveMenue = function(auswahl, success) {
		//alert("Kalender.saveMenue");
		Datasource.request('kalend-menue', {'sk' : Auth.sessionKey, 'auswahl' : auswahl}, success);
	};
	return Kalender;
}]);

/* Kurse service v.1 */
var KurseSvc = ppitapp.factory('Kurse', ['Auth', 'Datasource', function(Auth, Datasource) {
	//console.log("Kurse service v.1 loading...");
	// define the local namespace
	var Kurse = {};
	Auth.load();
	// timestamp of last database load
	Kurse.lastRefresh = 0;
	// last type of page data loaded
	Kurse.lastPage = 'index';
	// timelimit of cache in milliseconds
	Kurse.cacheLimit = 60000;
	// kurse resource
	/*
	Kurse.url = Auth.appUrl + '/index.php?act=kurse&get=:get&sk=:sk';
	Kurse.kurseList = $resource(Kurse.url, { get: 'index' },
			{ 'meine' : {method: 'GET', params: {'get' : 'meine'}},
				'save': {method: 'POST', params: {'get' : 'none'}}});
	*/
	// currently selected kurs
	Kurse.currentKurs = {};
	// cached Kurse resource
	Kurse.kurse = [];
	// cached MeineKurse resource
	Kurse.meineKurse = [];
	// Zahlungs Periode
	Kurse.perioden = ['N/A', 'Monatlich', '14-tägig', 'Jährlich', 'Wöchentlich', 'Pro Quartal', 'Einmalig', 'Täglich'];
	// error handling
	// error popup flag
	Kurse.errorFlag = false;
	// error message and popup text
	Kurse.fehlerMessage = "";
	// error code
	Kurse.errorCode = 0;
	
	Kurse.load = function(timestamp, handler) {
		//console.log(Auth.sessionKey);
		Auth.load();
		// check if cache timelimit has passed
		if(Kurse.kurse.length == 0 || timestamp.valueOf() - Kurse.lastRefresh > Kurse.cacheLimit) {
			//console.log("loading kurse: ", Auth.sessionKey);
			Datasource.request('kurse-index', {'sk' : Auth.sessionKey}, function(data) {
				if(angular.isDefined(data) && data.fehler == 0) {
					if(Kurse.errorFlag) Kurse.clearError();
					// prepare data for view (some parsing done in parseKurs() function)
					angular.forEach(data.kursliste, Kurse.parseKurs, data.kursliste);
					var cd = new Date();
					Kurse.lastRefresh = cd.valueOf();
					Kurse.kurse = data;
					if(handler) handler(data);
				} else {
					// error in request data
					if(angular.isDefined(data) && angular.isDefined(data.fehlermessage)) {
						Kurse.raiseError(data.fehler, data.fehlermessage);
					} else Kurse.raiseError(-4, "Incorrect data received!");
				}
			}, function(data) {
				// error connecting to server
				Kurse.raiseError(-1, "Error connecting to the server!");
				//console.log(data);
			});
			
			/*
			Kurse.kurse = Kurse.kurseList.get({'sk' : Auth.sessionKey}, function(data) {
				if(angular.isDefined(data) && data.fehler == 0) {
					if(Kurse.errorFlag) Kurse.clearError();
					// prepare data for view (some parsing done in parseKurs() function)
					angular.forEach(data.kursliste, Kurse.parseKurs, data.kursliste);
					var cd = new Date();
					Kurse.lastRefresh = cd.valueOf();
				} else {
					// error in request data
					if(angular.isDefined(data) && angular.isDefined(data.fehlermessage)) {
						Kurse.raiseError(data.fehler, data.fehlermessage);
					} else Kurse.raiseError(-4, "Incorrect data received!");
				}
			}, function(data) {
				// error connecting to server
				Kurse.raiseError(-1, "Error connecting to the server!");
				console.log(data);
			});
			*/
		} else {
			if(handler) handler(Kurse.kurse);
		}
		Kurse.lastPage = 'index';
		return Kurse.kurse;
	}

	Kurse.loadMeine = function(timestamp, handler) {
		Auth.load();
		// check if cache timelimit has passed
		if(Kurse.meineKurse.length == 0 || timestamp.valueOf() - Kurse.lastRefresh > Kurse.cacheLimit) {
			//console.log("loading-meine...");
			//console.log("loading meine kurse: ", Auth.sessionKey);
			Datasource.request('kurse-meine', {'sk' : Auth.sessionKey}, function(data) {
				if(angular.isDefined(data) && data.fehler == 0) {
					if(Kurse.errorFlag) Kurse.clearError();
					// prepare data for view (some parsing done in parseKurs() function)
					angular.forEach(data.kursliste, Kurse.parseKurs, data.kursliste);
					var cd = new Date();
					Kurse.lastRefresh = cd.valueOf();
					Kurse.meineKurse = data;
					if(handler) handler(data);
				} else {
					// error in request data
					if(angular.isDefined(data) && angular.isDefined(data.fehlermessage)) {
						Kurse.raiseError(data.fehler, data.fehlermessage);
					} else Kurse.raiseError(-4, "Incorrect data received!");
				}
			}, function(data) {
				// error connecting to server
				Kurse.raiseError(-1, "Error connecting to the server!");
				//console.log(data);
			});
		} else {
			if(handler) handler(Kurse.meineKurse);
		}
		Kurse.lastPage = 'meine';
		//return Kurse.meineKurse;
	}
	
	Kurse.changeStatus = function(statusData) {
		//console.log("Kurse.changeStatus");
		Auth.load();
		var params = {
				"sk"		: Auth.sessionKey,
				"set"		: "kurse",
				"anmeldung"	: statusData
		};
		Datasource.request('kurse-save', params, function(data) {
			//console.log("success");
			//console.log(data.kurs);
			if(angular.isDefined(data) && data.fehler == 0) {
				if(Kurse.errorFlag) Kurse.clearError();
				var editedKursList = [];
				editedKursList.push(data.kurs);
				//console.log("preparsed kurs list:", editedKursList);
				angular.forEach(editedKursList, Kurse.parseKurs, editedKursList);
				//console.log("postparsed kurs list:", editedKursList);
				var editedKurs = editedKursList[0];
				//console.log("editedKurs ", editedKurs);
				var found = false;
				// search for kurs in cached arrays
				//console.log("search for kurs in cached arrays:");
				//console.log("edited kurs id ", editedKurs.kursstammdaten.kurs_id);
				angular.forEach(Kurse.kurse.kursliste, function(value, key) {
					//console.log("selected kurs id ", value.kursstammdaten.kurs_id);
					if(!found && value.kursstammdaten.kurs_id == editedKurs.kursstammdaten.kurs_id) {
						found = true;
						//console.log(found);
						this[key] = editedKurs;
					}
				}, Kurse.kurse.kursliste);
				// if not found try to look in meinekurse array
				if(!found && Kurse.meineKurse.kursliste) angular.forEach(Kurse.meineKurse.kursliste, function(value, key) {
					if(!found && value.kursstammdaten.kurs_id == kurslist[0].kursstammdaten.kurs_id) {
						found = true;
						this[key] = editedKurs;
					}
				}, Kurse.meineKurse.kursliste);
				angular.copy(editedKurs, Kurse.currentKurs);
				// we need to refresh data as soon as possible
				Kurse.meineKurse = [];
				//console.log("end of saving");
				//console.log(Kurse.kurse.kursliste);
				//Kurse.getKurs(kurslist[0].kursstammdaten.kurse_id);
			} else {
				// error in request data
				if(angular.isDefined(data) && angular.isDefined(data.fehlermessage)) {
					Kurse.raiseError(data.fehler, data.fehlermessage);
				} else Kurse.raiseError(-4, "Incorrect data received!");
			}
		}, function(data) {
			// error connecting to server
			Kurse.raiseError(-1, "Error connecting to the server!");
			//console.log(data);
		});
	};
	
	Kurse.errorHandler = undefined;
	Kurse.registerHandler = function(handler) {
		Kurse.errorHandler = handler;
	};
	
	Kurse.raiseError = function(code, text) {
		Kurse.errorCode = code;
		Kurse.fehlerMessage = text;
		Kurse.errorFlag = true;
		//console.log("Fehler code: ", code);
		//console.log("Fehler: ", text);
		if(Kurse.errorHandler) Kurse.errorHandler();
	};
	
	Kurse.clearError = function() {
		Kurse.errorFlag = false;
		Kurse.errorCode = 0;
		Kurse.fehlerMessage = "";
		//console.log("Errors flags cleared!");		
	};

	Kurse.parseKurs = function(value,key) {
		// make a short beschreibung
		if(value.kursstammdaten.beschreibung.length > 100) this[key].kursstammdaten.beschreibung_short = value.kursstammdaten.beschreibung.slice(0,99);
		else this[key].kursstammdaten.beschreibung_short = value.kursstammdaten.beschreibung;
		// create periode label
		this[key].kursstammdaten.periode = Kurse.perioden[value.kursstammdaten.zahlungsperiode_id];
		// parse termine
		if(angular.isDefined(value.termine) && value.termine.length > 0) {
			var tempTermine =  new Array();
			angular.forEach(value.termine, function(value, index) {
				// recreate proper format of date/time object
				var newTermin = {"start_zeit" : new Date(value.start_zeit),"ende_zeit" : new Date(value.ende_zeit)};
				this.push(newTermin);
			}, tempTermine);
			this[key].termine = tempTermine;
		}
		// check anmeldedaten
		if(angular.isDefined(value.anmeldedaten)) {
			// parse status
			this[key].anmeldedaten.anmeldestatus = parseInt(value.anmeldedaten.anmeldestatus);
			// parse bemerkung
			this[key].anmeldedaten.bemerkung = (angular.isDefined(value.anmeldedaten.bemerkung))? value.anmeldedaten.bemerkung : '';
			// special price
			if(angular.isDefined(value.anmeldedaten.preis_pro_periode) && value.anmeldedaten.preis_pro_periode > 0) {
				this[key].kursstammdaten.preis_pro_periode = value.anmeldedaten.preis_pro_periode;
			}
		} else {
			this[key].anmeldedaten = {};
			this[key].anmeldedaten.anmeldestatus = 0;
			this[key].anmeldedaten.bemerkung = '';
		}
		// parse status
		switch(this[key].anmeldedaten.anmeldestatus) {
		case 10: // Angemeldet
			this[key].statusImg = 'css/images/ribbon3-o.png';
			this[key].statusClass = 'orange';
			this[key].statusTitle = 'Angemeld.';
			this[key].buttonTitle = 'Abmelden';
			this[key].buttonAction = 'deselect'; // same as class
			break;
		case 30: // Bestätigt
			this[key].statusImg = 'css/images/ribbon3-g.png';
			this[key].statusClass = 'green';
			this[key].statusTitle = 'Bestätigt';
			this[key].buttonTitle = 'Abmelden';
			this[key].buttonAction = 'deselect'; // same as class
			break;
		case 80: // Abgelehnt
			this[key].statusImg = 'css/images/ribbon3-r.png';
			this[key].statusClass = 'red';
			this[key].statusTitle = 'Abgelehnt';
			break;
		case 90: // Abgebrochen
			this[key].statusImg = 'css/images/ribbon3-r.png';
			this[key].statusClass = 'red';
			this[key].statusTitle = 'Abgebr.';
			break;
		case 0: // Undefined/initial status
			this[key].statusImg = '';
			this[key].statusClass = '';
			this[key].statusTitle = '';
			this[key].statusClass = 'status_empty';
			this[key].buttonTitle = 'Anmelden';
			this[key].buttonAction = 'select'; // same as class
			break;
		}
	};
	Kurse.getKurs = function(kursId) {
		//console.log("getKurs: ", kursId);
		var found = false;
		// try to find requested kurs in kurs list
		angular.forEach(Kurse.kurse.kursliste, function(value, key) {
			//console.log(value);
			if(kursId == value.kursstammdaten.kurs_id) {
				angular.copy(value, this);
				found = true;
			}
		}, Kurse.currentKurs);
		// if not found in kurs list
		if(!found) {
			// try to found in meine kurs list
			if(angular.isDefined(Kurse.meineKurse.kursliste)) {
				angular.forEach(Kurse.meineKurse.kursliste, function(value, key) {
					//console.log(value);
					if(kursId == value.kursstammdaten.kurs_id) {
						angular.copy(value, this);
						found = true;
					}
				}, Kurse.currentKurs);
			}
		}
		if(found) return Kurse.currentKurs;
		else return {};
	};
	Kurse.getLastPage = function() {
		return Kurse.lastPage;
	};
	Kurse.clear = function() {
		Kurse.currentKurs = {};
		Kurse.kurse = [];
		Kurse.meineKurse = [];
		Kurse.lastRefresh = 0;
		Kurse.errorFlag = false;
	};
	return Kurse;
}]);
/* Teilnehmer(Profile) service v.1 */
var ProfileSvc = ppitapp.factory('Teilnehmer', ['Auth', 'Datasource', function(Auth, Datasource) {
	//console.log("Teilnehmer service");
	var Tn = {};
	// profile data container
	Tn.profile = undefined;
	// loading flag
	Tn.loading = false;
	// cached data timeout in milliseconds
	Tn.timeout = 60000;
	// timestamp of last load
	Tn.lastLoad = undefined;
	// force refresh flag and setter
	Tn.expired = false;
	Tn.refresh = function() {
		//console.log("Teilnehmer refresh!");
		Tn.expired = true;
	};
	// returns true if cache is expired and needs refresh
	Tn.cacheExpired = function() {
		var d = new Date();
		// Tn.lastLoad === undefined means that there were no loading of profile data yet
		// d.getTime() - Tn.lastLoad > Tn.timeout - means that timeout for profile data storage is expired
		var f1 = angular.isUndefined(Tn.lastLoad);
		//console.log("f1:", f1);
		var f2 = Tn.expired;
		//console.log("f2:", f2);
		var f3 = (d.getTime() - Tn.lastLoad > Tn.timeout);
		//console.log("f3:", f3);
		return (f1 || f2 || f3);
	};
	// success & error handlers
	Tn.sHandlers = [];
	Tn.eHandlers = [];
	Tn.clearHandlers = function() {
		Tn.sHandlers = [];
		Tn.eHandlers = [];
	};
	Tn.runHandlers = function(handlers, data) {
		angular.forEach(handlers, function(handler) {
			handler(data);
		});
	};
	Tn.parseTeilnehmerData = function(data) {
		// some parsing can be done here
		return data;
	};
	// clear cached data
	Tn.clearProfileData = function() {
		Tn.profile = undefined;
		Tn.loading = false;
	};
	Tn.load = function() {
		Tn.loading = true;
		Datasource.request('profile-index', {'sk' : Auth.sessionKey}, function(data) {
			// success handler
			Tn.loading = false;
			if(angular.isDefined(data)) {
				if(data.fehler == 0) {
					// prepare data for view
					Tn.profile = Tn.parseTeilnehmerData(data);
					// set timestamp of last load
					var d = new Date();
					Tn.lastLoad = d.getTime();
					Tn.expired = false;
					// run registered success handlers
					Tn.runHandlers(Tn.sHandlers, data);
				} else {
					// run registered error handlers
					Tn.runHandlers(Tn.eHandlers, data);
				}
			} else {
				// run registered error handlers
				Tn.runHandlers(Tn.eHandlers, data);
			}
			// clear all handlers after successfull request
			Tn.clearHandlers();
		}, function(data) {
			Tn.loading = false;
			// error - network/server problems
			// run registered error handlers
			Tn.runHandlers(Tn.eHandlers, data);
			// clear all handlers after request processed
			Tn.clearHandlers();
		});
	};
	Tn.getProfile = function(sHandler, eHandler) {
		//console.log("Teilnehmer.getProfile");
		// register success handler
		if(angular.isDefined(sHandler)) {
			Tn.sHandlers.push(sHandler);
		}
		// register error handler
		if(angular.isDefined(eHandler)) {
			Tn.eHandlers.push(eHandler);
		}
		// check if data is already loaded
		// and not expired
		if(angular.isDefined(Tn.profile) && Tn.profile.fehler == 0 && !Tn.cacheExpired()) {
			Tn.runHandlers(Tn.sHandlers, Tn.profile);
			Tn.clearHandlers();
			return Tn.profile;
		} else {
			// prevent loading if process is already started
			if(!Tn.loading) {
				// clear cached data
				Tn.clearProfileData();
				// start loading procedure
				Tn.load();
			}
			return Tn.profile;
		}
	};
	Tn.savePassword = function(pwd, sHandler, eHandler) {
		Datasource.request('profile-pwd', {'sk' : Auth.sessionKey, 'pwd' : pwd}, sHandler, eHandler);
	};
	Tn.deleteFoto = function(sHandler, eHandler) {
		Datasource.request('profile-delete', {'sk' : Auth.sessionKey}, sHandler, eHandler);
	};
	//Tn.getProfile();
	return Tn;
}]);

/* Navigation service v.1.1 */
var NavigationSvc = ppitapp.factory('Navigation', ['$location', '$window', '$rootScope', 'Messages', function($location, $window, $rootScope, Messages) {
	//console.log("Navigation service");
	var Nav = {};
	// current page
	Nav.current = undefined;
	// history storage
	Nav.history = [];
	// calculation of next calendar page
	Nav.kalenderParse = function(params) {
		var newType = "";
		if(angular.isDefined(params) && params.type == 'a') {
			newType = 'b';
		} else {
			newType = 'a';
		}
		var newShift = (angular.isDefined(params) && angular.isDefined(params.shift)) ? params.shift : 0;
		var newPath = newType + "/" + newType + "/" + newShift;
		//console.log("kalenderParse result: ", newPath);
		return newPath; 
	};
	//
	Nav.kursedetailParse = function(params) {
		var newPath = "/" + params.kursId;
		//console.log("kursedetailParse result: ", newPath);
		return newPath;
	};
	// calculate main part of url
	Nav.urlParse = function(page) {
		var url = "";
		switch(page) {
		case "kalender": // legacy support
			//paramTeil = this.kalenderParse(params);
			url = '/kalend';
			break;
		case "kalend":
			//paramTeil = this.kalenderParse(params);
			url = '/kalend';
			break;
		case "profile":
			url = '/profile';
			break;
		case "kursedetail":
			//paramTeil = this.kursedetailParse(params);
			url = '/kursedetail';
			break;
		case "kurse":
			url = '/kurse';
			break;
		case "konto":
			url = '/konto';
			break;
		case "start":
			url = '/start';
			break;
		case "error":
			url = '/error';
			break;
		case "ppitkontakt":
			url = '/ppitkontakt';
			break;
		case "vertretungsplan":
			url = '/vertretungsplan';
			break;
		default:
			url = '/login';
		}
		//console.log("Nav.urlParse result: ", url);
		return url;
	};
	Nav.setCurrent = function(newPage) {
		if(!angular.isDefined(this.current)) {
			this.current = newPage;
		} else {
			if(angular.isDefined(newPage.page) && this.current.page != newPage.page) {
				this.current = newPage;
			}
		}
		$window.localStorage.setItem("nav.current", angular.toJson(this.current));
	};
	Nav.prepareUrl = function(page, params) {
		// core page url selection
		var url = this.urlParse(page);
		var paramTeil = "";
		switch(page) {
		case "kalender": // legacy support
			paramTeil = this.kalenderParse(params);
			break;
		case "kalend":
			paramTeil = this.kalenderParse(params);
			break;
		case "kursedetail":
			paramTeil = this.kursedetailParse(params);
			break;
		default:
			paramTeil = "";
		}
		return url + paramTeil;
	};
	// main navigation function
	Nav.go = function(page, params, isBack) {
		var isMsg = Messages.messages.length > 0;
		var newUrl = Nav.prepareUrl(page, params);
		if(!isBack && angular.isDefined(this.current)) this.history.push(angular.copy(this.current));
		//alert("Nav.go: " + isMsg);
		if(!isBack && isMsg) {
			var skippedPage = {"page": page, "params": params};
			this.history.push(skippedPage);
			$location.path("error");
		} else {
			this.current = {"page": page, "params": params};
			//console.log("Nav.go result: ", newUrl);
			$location.path(newUrl);
		}
	};
	Nav.goCurrent = function() {
		var p;
		if(angular.isDefined(this.current)) {
			p = this.current;
		} else {
			var c = $window.localStorage.getItem("nav.current");
			if(c === null) {
				p = {"page":"start"};
			} else {
				p = angular.fromJson(c);
			}
		}
		var newUrl = Nav.prepareUrl(p.page, p.params);
		//console.log("goCurrent: ", newUrl);
		$location.path(newUrl);
	};
	// "go back" function
	Nav.goBack = function(hard) {
		// software "back" functionality
		//console.log("Nav.goBack history: ", this.history);
		var newUrl = {page: 'start'};
		if(this.history.length > 0) {
			newUrl = this.history.pop();
		}
		//console.log("Nav.goBack go: ", newUrl);
		//alert("Nav.goBack "+newUrl.page);
		this.go(newUrl.page, newUrl.params, true);
	};
	// hardware back button functionality
	Nav.backHard = function(e) {
		e.preventDefault();
		if(angular.isDefined(Nav.current) && (Nav.current.page == 'start' || Nav.current.page == 'login')) {
	        navigator.app.exitApp();
		} else {
			Nav.go('start');
			$rootScope.$apply();
		}
	};
	$window.document.addEventListener("deviceready", function() {
		$window.document.addEventListener("backbutton", Nav.backHard, false);
	}, false);
	return Nav;
}]);

/* Controllers */

/* Kalendar kontroller multipage version (v.3) */
function KalenderCtrl3(Navigation, Teilnehmer, $scope, Kalend2, Auth, $routeParams, Settings, $filter) {
	$scope.ctrlName = "KalenderCtrl3";
	//console.log('KalenderCtrl3');
	// current week index
	$scope.shift = parseInt($routeParams.Shift);
	// template page
	$scope.type = $routeParams.Type;
	// structure for current week with two fields:
	// tage - days-info for current week
	// details - angebot-info for current week
	$scope.kalend = { 'tage' : [], 'details' : [] };
	// flag shows that all data for current week is loaded
	$scope.dataReady = false;
	// menue element for currently selected menue
	$scope.selectedMenue = {};
	$scope.selectedMenue.menue = {};
	// image src for currently selected menue
	$scope.selectedMenue.selectedMenueImage = "css/images/essen.png";
	$scope.selectedMenue.selectedMenueClass = "menue-default";
	// parameters of selected menue to send to DB
	$scope.selectedMenue.selectedMenueId = 0;
	$scope.selectedMenue.selectedMenueDate = "";
	// nachricht für menü
	$scope.selectedMenue.menueNachricht = "";
	// empty element for unselecting menu item
	$scope.emptyMenue = {
			"menue_nr" : -1,
			"bild_id" : "",
			"menue_text": "vom Essen abmelden",
			"preis" : 0,
			"ausgewaehlt" : true,
			"allergie_konflikte" : [],
			"ersatzkomponenten": [],
			"zusatzstoffe" : []
	};
	// base url for external images
	//$scope.appUrl = Auth.appUrl;
	$scope.appUrl = _URL;
	// urls for navigation buttons - deprecated
	//$scope.prevUrl = $scope.nextUrl = $scope.aktUrl = "/kalend";
	// selected date wich should be saved for further use
	$scope.selectedDate = Settings.getDate();
	// timeout id
	$scope.timeoutId = undefined;
	$scope.anmeldungText = "Hiermit melde ich mich jeden %tags%, bis auf Widerruf, dauerhaft verbindlich und kostenpflichtig („Abo“) zum Essen an.";
	$scope.abmeldungText = "Hiermit widerrufe ich meine dauerhafte Anmeldung („Abo“) zum Essen am %tags%.";
	$scope.aboText = "";
	$scope.aboTag = 0;
	$scope.aboChange = function(tagIdx) {
		//console.log("aboChange: ", tagIdx);
		$scope.aboTag = tagIdx;
		//console.log("current: ", $scope.abotage);
		//console.log("old: ", $scope.abotageOld);
		Settings.setDate($scope.selectedDate);
		var datum = $scope.kalend.tage[tagIdx].datum;
		var tagName = $filter('date')(datum, 'EEEE');
		var isAnmeldung = $scope.abotage[tagIdx];
		$scope.aboText = (isAnmeldung) ? $scope.anmeldungText : $scope.abmeldungText;
		$scope.aboText = $scope.aboText.replace('%tags%', tagName);
		$("#abotagPopup").popup("open",{'positionTo':'window'});
	};
	$scope.aboCancel = function() {
		//console.log("cancel abotag: ", $scope.aboTag);
		//console.log("current: ", $scope.abotage[$scope.aboTag]);
		//console.log("old: ", $scope.abotageOld[$scope.aboTag]);
		//$scope.abotage[$scope.aboTag] = $scope.abotageOld[$scope.aboTag];
		// previous line does not work at least in some Android versions :(
		// forced refresh of profile data
		$("#abotagPopup").popup("close");
		Teilnehmer.refresh();
		Kalend2.needRefresh = true;
		$scope.dataReady = false;
		$scope.init();
	};
	$scope.aboOk = function() {
		var abotageVal = 0;
		angular.forEach($scope.abotage, function(val, key) {
			abotageVal += (val) ? Math.pow(2, key) : 0;
		});
		Kalend2.saveAbo(abotageVal, function(data) {
			// success handling
			//console.log('success: ',data);
			if(data.fehler) {
				$scope.abotage[$scope.aboTag] = $scope.abotageOld[$scope.aboTag];
				if(data.fehler == -2) {
					$scope.aboText = data.fehlermessage;
					Teilnehmer.clearProfileData();
					Kalend2.needRefresh = true;
					$scope.dataReady = false;
					$("#abotagPopup").popup("close");
					$scope.init();
				} else {
					$scope.aboText = data.fehlermessage;
					//console.log('error: ', data.fehlermessage);
				}
			} else {
				// success
				$scope.abotageOld[$scope.aboTag] = $scope.abotage[$scope.aboTag];
				$("#abotagPopup").popup("close");
				Teilnehmer.clearProfileData();
				Kalend2.needRefresh = true;
				$scope.dataReady = false;
				$scope.init();
			}
		});
		//$scope.abotage[$scope.aboTag] = !$scope.abotage[$scope.aboTag];
	};
	/*
	 * initialization function
	 */
	$scope.init = function() {
		//console.log("KalenderCtrl3.init");
		//console.log("Date selected:", $scope.selectedDate);
		$scope.abotage = [false,false,false,false,false,false,false];
		$scope.abotageOld = [false,false,false,false,false,false,false];
		$scope.aboTyp = 0;
		var profile = Teilnehmer.getProfile(function(data) {
			// success
			console.log("getProfile success: ", data.teilnehmer);
			var abotageValue = data.teilnehmer.abotage;
			var newAbotage = [false,false,false,false,false,false,false];
			angular.forEach(newAbotage, function(value, key) {
				var isAbotag = abotageValue & Math.pow(2, key);
				if(isAbotag) {
					this[key] = true;
				}
			}, newAbotage);
			$scope.abotage = angular.copy(newAbotage);
			$scope.abotageOld = angular.copy(newAbotage);
			$scope.aboTyp = data.teilnehmer.essenabotyp;
			//console.log("Abotag init: ", newAbotage);
			//if(Kalend2.started && !Kalend2.needRefresh) $.mobile.loading('hide'); 
			$scope.$apply();
		}, function(data) {
			// error
			//console.log("error: ", data);
			Navigation.go("error");
		});
		// properties init
		var wShift = parseInt($routeParams.Shift);
		$scope.shift = wShift;
		$scope.type = $routeParams.Type;
		Navigation.setCurrent({"page" : "kalend", "params" : { "type" : $scope.type, "shift" : wShift}});
		//$scope.kalend = { 'tage' : [], 'details' : [] };
		//$scope.dataReady = false;
		$scope.selectedMenue = {};
		$scope.selectedMenue.menue = {};
		$scope.selectedMenue.selectedMenueImage = "css/images/essen.png";
		$scope.selectedMenue.selectedMenueId = 0;
		$scope.selectedMenue.selectedMenueDate = "";
		$scope.selectedMenue.menueNachricht = "";
		$scope.selectedMenue.selectedMenueClass = "menue-default";
		//$scope.appUrl = Auth.serverURL;
		// authorization check
		Auth.load();
		if (Auth.sessionKey) {
			$scope.sessionKey = Auth.sessionKey; 
			if(!Kalend2.started || Kalend2.needRefresh) {
				// set up redirect handler if it is not set yet
				if(Kalend2.cacheRefreshHandler == undefined)
					Kalend2.cacheRefreshHandler = function() {
					//alert("previous path: " + $location.path());
					//console.log("Kalend2: ",Kalend2);
					//$location.path("/login");
					//console.log("shift: ",wShift);
					Kalend2.init();
					//$scope.$apply();
				};
				Kalend2.init();
				//if(!Kalend2.started) {
					Kalend2.addTagSuccessHandler($scope.successTagHandler);
					Kalend2.addDetailSuccessHandler($scope.successDetailHandler);
					Kalend2.addTagErrorHandler($scope.errorHandler);
					Kalend2.addDetailErrorHandler($scope.errorHandler);					
				//};
				Kalend2.getWoche(wShift);
			} else {
				//console.log('KalenderCtrl3.init second run. shift: ',wShift);
				var wResult = Kalend2.getWoche(wShift);
				//var dResult = Kalender.getDetailedWoche($scope.shift);
				//console.log('KalenderCtrl3.init second run. kalend: ', wResult);
				$scope.kalend = wResult;
				/*
				if(Kalender.dataIsEmpty()) {
					//console.log('KalenderCtrl2.init empty data found. Reinitialization!');
					Kalender.clearCache();
					Kalender.init();
					Kalender.addErrorHandler($scope.errorHandler);
					Kalender.addSuccessHandler($scope.successHandler);
				} else {
					$scope.tage = wResult;
					$scope.angebote = dResult;					
				}*/
			}
		} else {
			//console.log('KalenderCtrl2.init auth error!');
			Navigation.go('login');
			//$location.path("/login");
		}
		$scope.$apply();
	};
	
	/*
	 * functions for kalender data handling
	 */
	$scope.successTagHandler = function() {
		//console.log("successTagHandler");
		var res = Kalend2.getWoche($scope.shift);
		if(res) {
			$scope.kalend = res;
			$scope.dataReady = true;
			$scope.$apply();
			//console.log("data received: ", $scope.kalend);			
		}
	};
	$scope.successDetailHandler = function() {
		//console.log("successDetailHandler");
		var res = Kalend2.getWoche($scope.shift);
		if(res) {
			$scope.kalend = res;
			//console.log("data received: ", res);
			$scope.dataReady = true;
			if($scope.kalend.details && $scope.kalend.details.length > 0 && $scope.kalend.details.detail_kostenarten)
				$scope.selectedMenue.menue = angular.copy($scope.kalend.details[0].detail_kostenarten[0].kostenarten[0].menues[0]);
			$scope.$apply();
		}
	};
	// error handler for Kalender
	$scope.errorHandler = function(errCode) {
		Settings.setDate($scope.selectedDate);
		//console.log("$scope.errorHandler: ", errCode);
		if(errCode == -2) { // authorization error
			if(Auth.saved) {
				Auth.login(Auth.cred, function() {
					// success handler
					Auth.save();
					//console.log('Auth success. reinitializing');
					Kalend2.needRefresh = true;
					$scope.dataReady = false;
					$scope.timeoutId = window.setTimeout($scope.init, 100);
					//$scope.init();
				}, function() {
					// error handler
					Auth.clear();
					Kalend2.needRefresh = true;
					//console.log('Auth fail. redirecting to login page...');
					Navigation.go('login');
					//$location.path("/login");
					$scope.$apply();
				});
			} else {
				//console.log('Session expired. Credentials not saved. Redirecting to login page...');
				Auth.clear();
				Kalend2.needRefresh = true;
				Navigation.go('login');
				//$location.path('/login');
				$scope.$apply();
			}
		} else if(errCode == 2) { // database server error
			//console.log("KalenderCtrl2.errorHandler server error redirect");
			//showError("Authorization expired. Reconnecting...");
			//Navigation.go("error");
			Navigation.go('kalend',{shift: $scope.shift});
			//var reloadUrl = $scope.aktUrl.slice(0,-1) + $scope.shift;
			//$location.path(reloadUrl);
			$scope.$apply();
			//$scope.timeoutId = setTimeout($scope.redirectUrl(reloadUrl), 1000);
		} else {
			//console.log("KalenderCtrl2.errorHandler unknown error.");
			//showError("Database error. Call People & Projects IT for answers.");
			Navigation.go("error");
			Kalend2.needRefresh = true;
			//$scope.dataReady = false;
			//$scope.timeoutId = window.setTimeout($scope.init, 10000);
		}
		//$.mobile.loading('hide');
		//$scope.$apply();
		//return false;
	};
	
	/*
	 * functions for navigation buttons
	 */
	// go to previous week
	$scope.prevWoche = function() {
		Settings.setDate($scope.selectedDate);
		//var url = $scope.prevUrl;
		//console.log('redirecting to: ', url);
		/*
		$location.routeOverride({
			jqmOptions : {
				reverse : true
			}
		});*/
		window.clearTimeout($scope.timeoutId);
		Navigation.go('kalend',{shift: $scope.shift - 1, type: $scope.type});
		//$location.path(url);
	};
	
	// go to next week
	$scope.nextWoche = function() {
		Settings.setDate($scope.selectedDate);
		//var url = $scope.nextUrl;
		//console.log('redirecting to: ', url);
		window.clearTimeout($scope.timeoutId);
		Navigation.go('kalend',{shift: $scope.shift + 1, type: $scope.type});
		//$location.path(url);
	};
	
	// go to actual week
	$scope.aktWoche = function() {
		Settings.setDate(Settings.getToday());
		//var url = $scope.aktUrl;
		//console.log('redirecting to: ', url);
		window.clearTimeout($scope.timeoutId);
		Navigation.go('kalend',{shift: 0, type: $scope.type});
		//$location.path(url);
	};
	
	// go to start page
	$scope.start = function() {
		Settings.setDate($scope.selectedDate);
		window.clearTimeout($scope.timeoutId);
		Navigation.go('start');
		//$location.path("/start");
	};
	
	// go to vertretungsplan page
	$scope.vertretungsplan = function() {
		Settings.setDate($scope.selectedDate);
		window.clearTimeout($scope.timeoutId);
		Navigation.go('vertretungsplan');
	};
	
	// go to kurs details page
	$scope.kurseDetails = function(id) {
		Settings.setDate($scope.selectedDate);
		window.clearTimeout($scope.timeoutId);
		Navigation.go('kursedetail', {kursId : id});
	};

	/*
	 * functions for template generation
	 */
	// check if the given date is today date
	// or if the date was previously selected by user
	$scope.isToday = function(d) {
		//console.log("isToday? ", d);
		//var td = new Date($scope.selectedDate);
		var savedDate = Settings.getDate();
		var td = new Date();
		if(savedDate) td = createDate(savedDate);
		var rd = createDate(d);
		//console.log(td.toDateString() == rd.toDateString());
		return td.toDateString() == rd.toDateString();
	};
	// check if in the day structure "angebote" and "events" arrays are empty
	$scope.isEmptyDay = function(t) {
		var al = t.angebote.length;
		var el = t.events.length;
		return al+el == 0;
	};
	// this function should return "true" if user has selected menu for this day
	$scope.isAngemeldet = function(tag) {
		var res = false;
		for(var i = 0; i < tag.angebote.length; i++) {
			var angebot = tag.angebote[i];
			// select only "Essensgeld" detail_kostenart ids
			if(angebot.detail_kostenart_id == "5946B518504DCEF79B6D74589C54D4D3") {
				if(angebot.angemeldet == 1) res = true;
			}
		}
		return res;
	};
	// this function should return "true" if Abotag is selected for this day
	$scope.isAbotag = function(tagIdx) {
		var d = createDate($scope.kalend.tage[tagIdx].datum);
		var td = new Date(); // today
		//console.log("datum: ", d, " today:", td);
		return ($scope.abotageOld[tagIdx]) && (d >= td);
	};
	// this function should return "true" if user has courses for this day
	$scope.hasCourses = function(tag) {
		var res = false;
		for(var i = 0; i < tag.angebote.length; i++) {
			var angebot = tag.angebote[i];
			// select all except "Essensgeld" detail_kostenart ids
			if(angebot.detail_kostenart_id != "5946B518504DCEF79B6D74589C54D4D3") res = true;
		}
		return res;
	};
	// returns "true" if there are any menues selectable for this day
	$scope.hasMenues = function(tag) {
		var res = false;
		for(var i = 0; i < tag.angebote.length; i++) {
			var angebot = tag.angebote[i];
			// select all "Essensgeld" detail_kostenart ids
			if(angebot.detail_kostenart_id == "5946B518504DCEF79B6D74589C54D4D3") res = true;
		}
		return res;
	}
	// returns "true" if there are any events for this day
	$scope.hasEvents = function(t) {
		var al = t.events.length;
		return al > 0;
	};
	// returns image src for selected menue
	$scope.getImageSrc = function(menue) {
		var iSrc = (menue.bild_id == '' || menue.bild_id == undefined)? 'css/images/essen.png' : _URL + '/img/cache/customer' + Settings.getCustomerID() + '/' + menue.bild_id + '.jpg?sk=' + Auth.sessionKey;
		//console.log('image src:',iSrc);
		return iSrc;
	};
	// returns human friendly name of "detail kostenart"
	$scope.getDetailTitle = function(tag, detail) {
		//console.log(tag);
		//console.log(detail);
		switch(detail) {
		case "5946B518504DCEF79B6D74589C54D4D3":
			return "Essen";
			break;
		case "1F40F57378133B73054B09A391A4FF7E":
			return "Kurse";
			break;
		}
		return detail + " - Unknown";
	};
	$scope.isAbotageEnabled = function(tag, detail) {
		//console.log("isAbotageEnabled tag:", tag);
		return (detail.detail_kostenartart_id == "5946B518504DCEF79B6D74589C54D4D3") && (detail.kostenarten[0].aenderbar == "1") && ($scope.aboTyp == 0);
	};

	/*
	 * functions for menues selection and saving it to DB
	 */
	$scope.selectMenue = function(tagIdx,detailIdx,angebotIdx,index) {
		//console.log("selectMenue");
		//alert("$scope.selectedDate: " + $scope.selectedDate);
		Settings.setDate($scope.selectedDate);
		var l = $scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues.length;
		for(var i = 0; i<l; i++) {
			if(i != index) $scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues[i].ausgewaehlt = "0";
		}
		if(index < l) {
			$scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues[index].ausgewaehlt = "1";
			$scope.selectedMenue.menue = angular.copy($scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues[index]);
			$scope.selectedMenue.selectedMenueImage = $scope.getImageSrc($scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues[index]);
			if($scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].menues[index].bild_id != "") {
				$scope.selectedMenue.selectedMenueClass = "menue-pic";
			} else {
				$scope.selectedMenue.selectedMenueClass = "menue-default";
			}
		} else {
			$scope.selectedMenue.menue = angular.copy($scope.emptyMenue);
			$scope.selectedMenue.selectedMenueImage = "css/images/essen.png";
			$scope.selectedMenue.selectedMenueClass = "menue-default";
		}
		$scope.selectedMenue.selectedMenueId = $scope.kalend.details[tagIdx].detail_kostenarten[detailIdx].kostenarten[angebotIdx].kostenart_id;
		//$scope.selectedMenue.selectedMenueDate = formatDate(new Date($scope.kalend.details[tagIdx].datum));
		//console.log($scope.selectedDate);
		var d = createDate($scope.selectedDate);
		//alert("createDate($scope.selectedDate): " + d);
		$scope.selectedMenue.selectedMenueDate = formatDate(d);
		//alert("$scope.selectedMenue.selectedMenueDate: " + $scope.selectedMenue.selectedMenueDate);
		$scope.menueNachricht = "";
		$scope.nachricht_sekretariat = Kalend2.nachricht_sekretariat;
		$("#postResult").html("");
		$("#menuePopup").popup("open",{'positionTo':'window'});
	};
	$scope.menueCancel = function() {
		//console.log("new cancel");
		$("#menuePopup").popup("close");
		$scope.cleanMenueDialog();
		Kalend2.needRefresh = true;
		$scope.dataReady = false;
		$scope.init();
	};
	$scope.menueOk = function() {
		//console.log('menue selected!');
		$("#postResult").html("");
		// prepare auswahl structure
		var auswahl = {'kostenart_id':'','nachricht':'','essen':{}};
		auswahl.kostenart_id = angular.copy($scope.selectedMenue.selectedMenueId);
		auswahl.nachricht = angular.copy($scope.selectedMenue.menueNachricht);
		auswahl.essen = {'datum' : '','menue_nr':0,'komponenten':[]};
		auswahl.essen.menue_nr = 1 * $scope.selectedMenue.menue.menue_nr;
		auswahl.essen.datum = $scope.selectedMenue.selectedMenueDate;
		auswahl.essen.komponenten = [];
		var l = $scope.selectedMenue.menue.ersatzkomponenten.length;
		if(l > 0) {
			for(var i = 0; i < l; i++) {
				if($scope.selectedMenue.menue.ersatzkomponenten[i].ausgewaehlt == true || $scope.selectedMenue.menue.ersatzkomponenten[i].ausgewaehlt == 1) {
					auswahl.essen.komponenten.push($scope.selectedMenue.menue.ersatzkomponenten[i].speise_id);
				}
			}
		}
		//alert(angular.toJson(auswahl));
		//console.log("menue selected: ", auswahl);
		Kalend2.saveMenue(auswahl, function(data) {
			// success handling
			//console.log('success: ',data);
			if(angular.isDefined(data.fehler) && data.fehler != 0) {
				//Kalender.clearCache();
				$("#postResult").html(data.fehlermessage);
				//console.log('error: ', data.fehlermessage);
			} else {
				// success
				$("#menuePopup").popup("close");
				$scope.cleanMenueDialog();
				Kalend2.needRefresh = true;
				$scope.dataReady = false;
				$scope.init();
			}
		});
		//$scope.$apply();
	};
	
	$scope.cleanMenueDialog = function() {
		$scope.selectedMenue = {};
		$scope.selectedMenue.menue = {};
		$scope.selectedMenue.selectedMenueImage = "css/images/essen.png";
		$scope.selectedMenue.selectedMenueId = 0;
		$scope.selectedMenue.selectedMenueDate = "";
		$scope.selectedMenue.menueNachricht = "";
		$scope.selectedMenue.selectedMenueClass = "menue-pic";
	};
	
	/*$scope.redirectPage = function(url) {
		console.log("redirectPage: ", url);
		$location.path(url);
		$scope.$apply();
	};*/
	
	/*
	 * Date selection handler
	 * function must save last selected date to settings storage
	 */
	$scope.datumSelect = function(e, d) {
		//console.log("datumSelect 4: ", d);
		$scope.selectedDate = d;
		//console.log("datumSelect saved: ", $scope.selectedDate);
		// here should be this call:
		//Settings.setDate(d);
		// but it do not work properly
		// so this way it works:
		//window.localStorage.setItem("datum", d);
		//console.log("Date in Settings: ", Settings.getDate());
	};

	// run init functions once per page loading
	$scope.init();
}
KalenderCtrl3.$inject = ['Navigation', 'Teilnehmer', '$scope', 'Kalend2', 'Auth', '$routeParams', 'Settings', '$filter'];

/* Start page controller */
function StartCtrl($scope, Navigation, Auth, Kalend2, Kurse) {
	//console.log("StartCtrl");
	$scope.goKonto = function() {
		Navigation.go("konto");
	};

	$scope.goVertretungsplan = function() {
		Navigation.go("vertretungsplan");
	};

	$scope.goPpitKontakt = function() {
		Navigation.go("ppitkontakt");
	};

	$scope.goProfile = function() {
		Navigation.go("profile");
	};

	$scope.goKurse = function() {
		Navigation.go("kurse");
	};

	$scope.goKalender = function() {
		Navigation.go("kalend");
	};

	$scope.logout = function() {
		//console.log('StartCtrl: logout');
		Auth.logout(function() {
			Kalend2.needRefresh = true;
			Kurse.clear();
			//console.log('StartCtrl: redirect');
			Navigation.go("login");
			//$location.path("/login");
			$scope.$apply();
		});
		//Kalender.clearCache();
		//$scope.$apply();
	};
	
	$scope.buttonClass = function(name) {
		if($scope.pages.indexOf(name) == -1) {
			return "ui-disabled";
		} else {
			return "";
		}
	};
	
	Auth.load();
	if (Auth.loggedIn()) {
		$scope.pages = Auth.pages;
		$scope.anzahl_vertretungen = Auth.anzahl_vertretungen;
		Navigation.setCurrent({page: 'start'});
	} else {
		Navigation.go("login");
		//$location.url("/login");
	}
}
StartCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Kalend2', 'Kurse' ];

/* Authorization controller */
function AuthCtrl($scope, Navigation, Auth, Settings) {
	//console.log('AuthCtrl');
	//console.log('version: ', Auth.version);
	$scope.ctrlName = "AuthCtrl";
	
	$scope.debug = false;
	$scope.cred = {
		username : "",
		password : ""
	};
	$scope.user = {
		username : "",
		password : ""
	};
	$scope.remember = false;
	$scope.yesno = [ {
		'label' : 'no',
		'value' : false
	}, {
		'label' : 'yes',
		'value' : true
	} ];

	$scope.reset = function() {
		$scope.user = angular.copy($scope.cred);
	};

	$scope.login = function() {
		$scope.cred = angular.copy($scope.user);
		Auth.remember = $scope.remember;
		Auth.login($scope.cred, function(data) {
			//console.log('AuthCtrl success login');
			Navigation.go($scope.userStart);
			/*
			if(angular.isDefined(data.nachrichten)) {
				Messages.setAction(function() {
					Navigation.go(Settings.getStart());
				});
				Navigation.go("error");
			} else {
				Navigation.go($scope.userStart);
			}
			*/
			$scope.$apply();
		}, function() {
			Navigation.go("error");
		});
	};
	
	Auth.load();
	$scope.userStart = Settings.getStart();
	if (Auth.sessionKey) {
		//console.log("AuthCtrl.userStart: ", $scope.userStart);
		//console.log("AuthCtrl try to resume");
		if(!Settings.realResumeHandler()) Navigation.goCurrent();
	} else {
		$scope.reset();
		Navigation.setCurrent({"page" : "login"});
	}
}
AuthCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Settings' ];

/* Profile controller */
function ProfileCtrl(Teilnehmer, $scope, Navigation, Auth, Settings, Messages) {
	//console.log('ProfileCtrl');
	$scope.ctrlName = "ProfileCtrl";
	$scope.photoEditable = false;
	$scope.pages = [];
	
	$scope.user = {};
	$scope.user.userFullName = "Bitte warten...";
	$scope.user.userPic = "css/images/default.png";
	$scope.user.gender = 0;
	
	$scope.pwd = {};
	$scope.pwd.pwd1 = "";
	$scope.pwd.pwd2 = "";
	$scope.pwd.error = "";

	// go to start page
	$scope.start = function() {
		Navigation.go("start");
		//$location.path("/start");
	};
	
	$scope.closePopup = function() {
		$("a#options").removeClass("ui-btn-active");
		$("a#password").removeClass("ui-btn-active");
		$("#passwordEdit").popup("close");
		$("#optionsEdit").popup("close");
	};
	
	// opens "Options" dialog
	$scope.editOptions = function() {
		$("#optionsEdit").popup("open");
		//console.log("popup");
	};

	// opens "Password" dialog
	$scope.editPassword = function() {
		$scope.pwd.pwd1 = "";
		$scope.pwd.pwd2 = "";
		$scope.pwd.error = "";
		$("#passwordEdit").popup("open");
		//console.log("popup");
	};
	
	// saves new Password
	$scope.savePassword = function() {
		if($scope.pwd.pwd1 != "" && $scope.pwd.pwd1 != $scope.pwd.pwd2) {
			$scope.pwd.error = "Bitte denselben Wert wiederholen.";
			$scope.$apply();
		}
		if($scope.pwd.pwd1 != "" && $scope.pwd.pwd1 == $scope.pwd.pwd2) {
			//console.log(Auth.authUrl + "&act=profile&sk=" + Auth.sessionKey);
			Teilnehmer.savePassword($scope.pwd.pwd1, function(data) {
				//console.log("savePassword: ", data);
				// check if there is error code
				if(data.fehler != 0) {
					//console.log("Setting errortext: ", data.fehlermessage);
					$scope.pwd.error = data.fehlermessage;
					$scope.$apply();
				} else {
					// success
					$scope.pwd.error = "";
					if(Auth.remember) Auth.cred.password = $scope.pwd.pwd1;
					Auth.save();
					$("#passwordEdit").popup("close");
				}
			});
		}
		$("a#password").removeClass("ui-btn-active");
	};
	
	// check if option is selected and saves it
	$scope.saveOptions = function() {
		Settings.setStart($scope.startSeite);
		//console.log("$scope.saveOptions" , Settings.getStart());
		$("#optionsEdit").popup("close");
		$("a#options").removeClass("ui-btn-active");
	};
	
	$scope.loadProfile = function() {
		//console.log('loadProfile()');
		Teilnehmer.getProfile(function(data) {
			//console.log("loadProfile: ", data);
			if(data.fehler == 0) {
				// successfully loaded data
				$scope.user.userFullName = data.teilnehmer.vorname + " " + data.teilnehmer.name;
				$scope.user.gender = data.teilnehmer.geschlecht_id;
				if(data.teilnehmer.bild_id == "") {
					if(data.teilnehmer.geschlecht_id == 1) {
						$scope.user.userPic = "css/images/schoolgirl.png";
					} else {
						$scope.user.userPic = "css/images/schoolboy.png";
					}
				} else {
					$scope.user.userPic = _URL + '/img/cache/customer' + Settings.getCustomerID() + '/' + data.teilnehmer.bild_id + '.jpg?v=' + Math.floor((Math.random()*10)+1) + '&sk=' + Auth.sessionKey;
					/*
					$.get($scope.user.userPic, function(data) {
						alert('image request result: ' + data);
					});*/
				}
				$scope.$apply();
			}
		});
	};
	
	$scope.fotoMenu = function() {
		//console.log("fotoMenu click: isEditable: ", $scope.photoEditable);
		if($scope.photoEditable && _PLATFORM !== "android") $("#fotoSelect").popup("open");
	};
	
	$scope.removeFoto = function() {
		var btn = $("#fotoSelect a#fotodelete").get(0);
		$(btn).removeClass("ui-btn-active");
		//console.log(btn);
		$("#fotoSelect").popup("close");
		Teilnehmer.deleteFoto(function(data) {
			// success handler
			//console.log(data);
			// success in removing image
			//var img = document.getElementById('profile-pic');
			if($scope.user.gender == 1) {
				$scope.user.userPic = "css/images/schoolgirl.png";
			} else {
				$scope.user.userPic = "css/images/schoolboy.png";
			}
			$scope.$apply();
			//alert(data.message);
		});
	};
	
	$scope.uploadFoto = function(type) {
		var popover = {};
		try {
			popover = new navigator.camera.CameraPopoverOptions(300,300,100,100,Camera.PopoverArrowDirection.ARROW_ANY);
		} catch(e) {}
		try {
			var options = {
					// options
					quality : 80,
					destinationType : navigator.camera.DestinationType.FILE_URI,
					allowEdit : true,
					encodingType: navigator.camera.EncodingType.JPEG,
					targetWidth: 400,
					targetHeight: 400,
					mediaType: navigator.camera.MediaType.PICTURE,
					correctOrientation: true,
					popoverOptions: popover,
					saveToPhotoAlbum: false,
				}; 
			switch(type) {
			case 1: // back camera
				options.sourceType = navigator.camera.PictureSourceType.CAMERA;
				$("a#camera").removeClass("ui-btn-active");
				break;
			case 2: // foto album
				options.sourceType = navigator.camera.PictureSourceType.PHOTOLIBRARY;
				$("a#fotolib").removeClass("ui-btn-active");
				break;
			}
			var dialog = navigator.camera.getPicture(function(imageURI) {
				// success handler
				var options = new FileUploadOptions();
				options.params = {};
				options.params.sk = Auth.sessionKey;
				//options.params.upload = "bild";
				options.fileKey = "bild";
				options.fileName = imageURI.substr(imageURI.lastIndexOf('/')+1);
				options.mimeType = "image/jpeg";
				options.chunkedMode = false; 
				var ft = new FileTransfer();
				$.mobile.loading('show');
				ft.upload(imageURI, encodeURI(_URL + "/index.php?act=profile&do=upload"), function(response) {
					var result = angular.fromJson(response.response);
					if(result.fehler != 0) {
						//alert(result.fehlermessage);
						Message.addMessage("err","Stammdaten Fehler", result.fehlermessage);
					} else {
						var img = document.getElementById('profile-pic');
						var isDefault = img.src.lastIndexOf('school');
						if(isDefault > 0) {
							// we need to make a full refresh to get bild_id
							$scope.loadProfile();
						} else {
							// we have an id and it is possible to refresh only image src
							var hasV = img.src.lastIndexOf('?');
							var oldSrc = img.src;
							if(hasV > 0) oldSrc = img.src.substr(0,img.src.lastIndexOf('?'));
							//alert(oldSrc);
							img.src = imageURI;
							var newImgSrc = oldSrc + "?v=" + Math.floor((Math.random()*10)+1) + '&sk=' + Auth.sessionKey;
							//alert(newImgSrc);
							img.src = newImgSrc;
							$scope.user.userPic = newImgSrc;
							$scope.$apply();
						}
					}
					$.mobile.loading('hide');
				}, function(error) {
					var message = "";
					switch(error.code) {
					case FileTransferError.FILE_NOT_FOUND_ERR:
						message = "File " + error.source + " not found";
						break;
					case FileTransferError.INVALID_URL_ERR:
						message = "Invalid URL: " + error.target;
						break;
					case FileTransferError.CONNECTION_ERR:
						message = "Connection failure";
						break;
					case FileTransferError.ABORT_ERR:
						message = "Connection aborted";
						break;
					}
					$.mobile.loading('hide');
					Message.addMessage("err","Stammdaten Fehler", "Fehler: " + message + ".");
				}, options, true);
				// iOS: alert should be wrapped to avoid bug
				/*
				setTimeout(function() { 
					alert('Success: '+imageURI);
				}, 0);
				*/			
			}, function(message) {
				// error handler
				// iOS: alert should be wrapped to avoid bug
				$scope.timeoutId = setTimeout(function() {
					Message.addMessage("err","Stammdaten Fehler", "Fehler: " + message + ".");
					//alert('Fehler: ' + message);
				}, 0);
				$.mobile.loading('hide');
			}, options);
		} catch(e) {
			$scope.timeoutId = setTimeout(function() { 
				//alert('Fehler: ' + e.message);
				Message.addMessage("err","Stammdaten Fehler", "Fehler: " + e.message + ".");
			}, 0);			
		} finally {
			$("#fotoSelect").popup("close");
		}
	};
	
	Auth.load();
	if (Auth.loggedIn()) {
		//$scope.pages = Auth.pages;
		$scope.pages = [];
		for(var page in Auth.pages) {
			var title = "";
			var name = "null";
			switch(Auth.pages[page]) {
			case "willkommen":
				name = "start";
				title = "Start";
				break;
			case "calendar":
				name = "kalend";
				title = "Kalender";
				break;
			case "konto":
				name = "konto";
				title = "Kontoauszug";
				break;
			case "stammdaten":
				name = "profile";
				title = "Stammdaten";
				break;
			case "kurse":
				name = "kurse";
				title = "Kurse";
				break;
			default:
				name = "null";
				break;
			}
			if(name != "null") $scope.pages.push({ "name" : name, "title" : title});
		};
		// start page to go after login screen
		$scope.startSeite = Settings.getStart();
		$scope.photoEditable = (angular.isDefined(Auth.rights) && angular.isDefined(Auth.rights.profilfoto_aendern))? Auth.rights.profilfoto_aendern == 1 : false;
		//console.log("ProfileCtrl: ", $scope.startSeite);
		$scope.loadProfile();
		Navigation.setCurrent({"page" : "profile"});
	} else {
		Navigation.go("login");
		//$location.url("/login");
	}
}
ProfileCtrl.$inject = [ 'Teilnehmer', '$scope', 'Navigation', 'Auth', 'Settings', 'Messages' ];

/* Konto controller */
function KontoCtrl($scope, Navigation, Auth, Settings, Datasource) {
	//console.log('KontoCtrl');
	$scope.ctrlName = "KontoCtrl";
	$scope.buchungen = new Array();
	// go to start page
	$scope.start = function() {
		Navigation.go("start");
		//$location.path("/start");
	};
	$scope.startDate = "";
	$scope.endDate = "";
	$scope.selectedStartDate = "";
	$scope.selectedEndDate = "";
	$scope.startGuthaben = 0;
	$scope.endGuthaben = 0;
	$scope.aktuellSaldo = 0;
	$scope.debug = "init";
	$scope.isLoaded = false;
	
	$scope.init = function() {
		//console.log('KontoCtrl.init');
		var sDate = new Date();
		sDate.setDate(1);
		$scope.startDate = formatDate(sDate);
		var eDate = new Date();
		//eDate.setMonth(eDate.getMonth()+1,0);
		$scope.endDate = formatDate(eDate);
		$scope.selectedStartDate = $scope.startDate;
		$scope.selectedEndDate = $scope.endDate;
		$("input#startDate").mobiscroll().date({
	        theme: 'jqm',
	        lang: 'de',
	        mode: 'scroller',
	        animate: 'none',
	        startYear: sDate.getFullYear() - 1,
	        endYear: sDate.getFullYear(),
	        maxDate: eDate,
	        onSelect: function(valueText, inst) {
	        	var sDate = createDate(parseDate(valueText));
	        	var eDate = createDate(parseDate($("input#endDate").val()));
	        	if(sDate > eDate) {
	        		$("input#endDate").mobiscroll('setDate', sDate, true);
	        	}
	        	$scope.load();
	        }
	    });
		$("input#startDate").mobiscroll('setDate',sDate);
		$("input#endDate").mobiscroll().date({
	        theme: 'jqm',
	        lang: 'de',
	        mode: 'scroller',
	        animate: 'none',
	        startYear: sDate.getFullYear() - 1,
	        endYear: sDate.getFullYear(),
	        maxDate: eDate,
	        onSelect: function(valueText, inst) {
	        	var eDate = createDate(parseDate(valueText));
	        	var sDate = createDate(parseDate($("input#startDate").val()));
	        	if(sDate > eDate) {
	        		$("input#startDate").mobiscroll('setDate', eDate, true);
	        	}
	        	$scope.load();
	        }
	    });
		$("input#endDate").mobiscroll('setDate',eDate);
		$scope.load();
	};
	
	$scope.load = function() {
		//console.log('KontoCtrl.load');
		$scope.isLoaded = false;
		var params = {
				'sk'	: Auth.sessionKey,
				'sd'	: $scope.selectedStartDate,
				'ed'	: $scope.selectedEndDate
		};
		//console.log(params);
		Datasource.request('konto', params, function(data) {
			//console.log("details received");
			$scope.debug = "details received";
			if (data) {
				// data handling
				//console.log("new konto data received.");
				//console.log(data);
				$scope.buchungen = angular.copy(data.buchungen);
				if(data.buchungen.length > 0) {
					var last = 0;
					for(var obj in data.buchungen) {
						last++;
					}
					//console.log("length=" + last);
					var startBuchung = data.buchungen[last-1];
					var endBuchung = data.buchungen[0];
					//console.log('buchungen:');
					//console.log(startBuchung);
					//console.log(endBuchung);
					$scope.startGuthaben = startBuchung.saldo - startBuchung.betrag; 
					$scope.endGuthaben =  endBuchung.saldo;
				}
				$scope.aktuellSaldo = data.saldoAktuell;
				$scope.startDate = $scope.selectedStartDate;
				$scope.endDate = $scope.selectedEndDate;
				$scope.debug = "success: " + angular.toJson(data);
				$scope.isLoaded = true;
				$scope.$apply();
				//console.log($scope.buchungen);
				$("a#load").removeClass("ui-btn-active");
			}
		});
	};
	
	Auth.load();
	if (Auth.loggedIn()) {
		// main code here
		$scope.init();
		Navigation.setCurrent({"page" : "konto"});
	} else {
		Navigation.go("login");
		//$location.url("/login");
	}
}
KontoCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Settings', 'Datasource' ];

/* Kurse controller */
function KurseCtrl($scope, Navigation, Auth, Kurse) {
	//console.log("KurseCtrl");
	
	$scope.mode = "index"; // index/meine
	$scope.modeTitle = "Kursanmeldung";
	// panel open flag
	$scope.sidePanelOpen = false;
	// show panel
	$scope.showPanel = function() {
		$scope.sidePanelOpen = true;
	};
	
	// toogles current mode
	$scope.toggle = function(btn) {
		if(btn != $scope.mode) {
			if($scope.mode == "meine") {
				$scope.mode = "index";
				$scope.modeTitle = "Kursanmeldung";
			} else {
				$scope.mode = "meine";
				$scope.modeTitle = "Meine Kurse";
			}
			$scope.init();
		}
	};
	
	// courses list
	$scope.kurse = [];
	
	// return to start page
	$scope.start = function() {
		Navigation.go("start");
	};
	
	// go to Kurse detail page
	$scope.kurseDetail = function(id) {
		Navigation.go("kursedetail", {kursId: id});
	};
	
	$scope.errorHandler = function() {
		if(Kurse.errorFlag) {
			//console.log("error handler ",Kurse.errorCode);
			$scope.errorMsg = Kurse.fehlerMessage;
		}
	};
	
	$scope.init = function() {
		//console.log("init");
		$scope.errorMsg = "";
		Kurse.registerHandler($scope.errorHandler);
		//console.log("init");
		var timestamp = new Date();
		if($scope.mode == "meine") {
			Kurse.loadMeine(timestamp, function(data) {
				$scope.kurse = data;
			});
			 
		} else {
			Kurse.load(timestamp, function(data) {
				$scope.kurse = data;
			});
		}
		//console.log($scope.kurse);
	};
	
	Auth.load();
	if (Auth.loggedIn()) {
		if(Kurse.getLastPage() == 'meine') {
			$scope.mode = "meine";
			$scope.modeTitle = "Meine Kurse";
			$("a#kurse_meine").addClass("ui-btn-active");
		} else {
			$("a#kurse_index").addClass("ui-btn-active");
		}
		$scope.init();
		Navigation.setCurrent({"page" : "kurse"});
	} else {
		Navigation.go("login");
		//$location.url("/login");
	}
}
KurseCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Kurse' ];

/* Kurse detail page controller */
function KurseDetailCtrl($scope, Navigation, Auth, Kurse, $routeParams) {
	// refresh timeout id
	$scope.timeoutId = 0;
	// waiting for data interval id
	$scope.intervalId = 0;
	// id of kurse
	$scope.kursId = 0;
	// kurse object
	$scope.kurs = {};
	// side panel flag
	$scope.sidePanelOpen = false;
	// submit button flag
	$scope.submitShow = function() {
		var check = angular.isDefined($scope.kurs.buttonAction) && $scope.kurs.buttonAction.length > 0 && Kurse.getLastPage() != 'meine';
		//console.log("check = ", check);
		return check;
	};
	// submit dialog show flag
	$scope.kurseSubmitDialog = false;
	// bemerkung model
	$scope.inputBemerkung = "";

	// back button action
	$scope.back = function() {
		Navigation.goBack();
		//$history.goBack();
	};
	
	$scope.start = function() {
		Navigation.go("start");
		//$location.path("/start");
	};
	
	$scope.showPanel = function() {
		$scope.sidePanelOpen = true;
	};
	
	// submit button handler
	$scope.submit = function(action) {
		//console.log('submit ',action);
		if(action == "select") {
			$scope.kurseSubmitDialog = true;
		} else {
			$scope.statusChange('00');
			$("a#kurse_submit").removeClass("ui-btn-active");
		}
	};
	// close dialog button handler
	$scope.closeDialog = function() {
		$("a#kurse_submit").removeClass("ui-btn-active");
		$scope.kurseSubmitDialog = false;
		$scope.inputBemerkung = "";
	};
	
	// anmelden/abmelden status change handler
	$scope.statusChange = function(status) {
		//console.log('change status ',status);
		var bemerkung = (status == "10") ? $scope.inputBemerkung : "";
		//console.log("bemerkung = ", bemerkung);
		var kursInfo = { "kurs_id" : $scope.kurs.kursstammdaten.kurs_id, "anmeldestatus" : status, "bemerkung" : bemerkung };
		//console.log("trying to save...");
		//console.log(kursInfo);
		Kurse.changeStatus(kursInfo);
		// close dialog after submitting if it's opened
		if($scope.kurseSubmitDialog) $scope.kurseSubmitDialog = false;
		$scope.$apply();
		$scope.timeoutId = window.setTimeout(function() {
			window.clearTimeout($scope.timeoutId);
			$("a#kurse_submit").removeClass("ui-btn-active");
			$scope.kurs = Kurse.getKurs($scope.kursId);
			//console.log($scope.kurs);
		}, 10);
	};
	
	$scope.errorHandler = function() {
		if(Kurse.errorFlag) {
			$scope.errorMsg = Kurse.fehlerMessage;
			Kurse.clear();
		}
	};
	
	// init function
	$scope.init = function() {
		$scope.errorMsg = "";
		Kurse.registerHandler($scope.errorHandler);
		var kursId = $routeParams.kursId;
		Navigation.setCurrent({"page" : "kursedetail", "params" : { "kursId" : kursId}});
		$scope.kursId = kursId;
		//console.log(kursId);
		var kursData = Kurse.getKurs(kursId);
		//console.log("kursData: ", kursData);
		if(!angular.equals(kursData,{})) {
			//console.log("NON empty kurs");
			$scope.kurs = kursData;
		} else {
			//console.log("empty kurs");
			var timestamp = new Date();
			Kurse.loadMeine(timestamp);
			$scope.intervalId = window.setInterval(function() {
				//console.log("timeout getKurs");
				var kursDataNew = Kurse.getKurs($routeParams.kursId);
				if(!angular.equals(kursDataNew,{})) {
					$scope.intervalId = window.clearInterval($scope.intervalId);
					$scope.kurs = kursDataNew;
					$scope.$apply();
					//console.log($scope.kurs);
				}
			}, 100);
		}
	};
	
	Auth.load();
	if (Auth.loggedIn()) {
		$scope.init();
	} else {
		Navigation.go("login");
		//$location.url("/login");
	}
}
KurseDetailCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Kurse', '$routeParams'];

/* Messages page controller */
function MessageCtrl($scope, Navigation, Messages, Auth) {
	$scope.action = function() {
		//console.log("$scope.action()");
		Messages.messages = [];
		Navigation.goBack();
	}
	$scope.restart = function() {
		Messages.messages = [];
		Auth.clear();
		Navigation.go("login");
	};
	$scope.init = function() {
		$scope.messages = {
				messages	: Messages.messages,
				actions		: undefined
		};
		$scope.title = (angular.isDefined(Messages.messages) && Messages.messages.length > 0)? Messages.messages[0].title : "Nachrichten";
		$scope.actionType = Messages.actionType;
	};
	$scope.init();
}
MessageCtrl.$inject = [ '$scope', 'Navigation', 'Messages', 'Auth'];

/* PPIT Kontakt controller */
function PpitKontaktCtrl($scope, Navigation, Auth, Settings, Datasource) {
	//console.log('PpitKontaktCtrl');
	$scope.ctrlName = "PpitKontaktCtrl";
	$scope.info = undefined;
	$scope.timeoutId = undefined;
	$scope.loaded = false;
	// go to start page
	$scope.start = function() {
		Navigation.go("start");
	};
	$scope.click = function(btn) {
		//console.log('PpitKontaktCtrl.click', buttons);
		if(angular.isDefined(btn)) {
			$scope.timeoutId = window.setTimeout(function() {
				window.clearTimeout($scope.timeoutId);
				var buttons = $('a.ui-btn');
				$(buttons).removeClass("ui-btn-active");
				//console.log("href:", btn);
				//window.location.href = btn;
				window.open(btn, '_system');
			}, 10);
		}
	};
	$scope.init = function() {
		//console.log('PpitKontaktCtrl.init');
		var kontaktInfo = {};
		if(angular.isDefined(Auth.kontakt.ppit_telefon)) {
			angular.forEach(Auth.kontakt, function(value, key) {
				if(angular.isDefined(value) && value.length > 0) {
					if(key.indexOf('mail') > -1) {
						kontaktInfo[key] = 'mailto:' + value.trim();
					}
					if(key.indexOf('telefon') > -1) {
						kontaktInfo[key] = 'tel:' + value.trim();
					}
				}
			});
			console.log(kontaktInfo);
			$scope.info = kontaktInfo;
			$scope.loaded = true;
		}
	};
	Auth.load();
	if (Auth.loggedIn()) {
		// main code here
		$scope.init();
		Navigation.setCurrent({"page" : "ppitkontakt"});
	} else {
		Navigation.go("login");
	}
}
PpitKontaktCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Settings', 'Datasource' ];

/* PPIT Kontakt controller */
function VertretungsplanCtrl($scope, Navigation, Auth, Settings, Datasource) {
	//console.log('VertretungsplanCtrl');
	$scope.ctrlName = "VertretungsplanCtrl";
	$scope.vertretungen = new Array();
	$scope.isLoaded = false;
	// go to start page
	$scope.start = function() {
		Navigation.go("start");
	};
	// back button action
	$scope.back = function() {
		Navigation.goBack();
	};
	// parse Vertretung info
	$scope.parse = function(item) {
		var vertretungObject = {
			"statusClass"	: "",
			"statusImg"		: "",
			"statusTitle"	: (item.vertretungsart.length > 10) ? item.vertretungsart.slice(0,6) + "..." : item.vertretungsart
		};
		switch(item.vertretungsart_id) {
		case 2:
			vertretungObject.statusImg = 'css/images/ribbon3-o.png';
			vertretungObject.statusClass = 'orange';
			break;
		case 3:
			vertretungObject.statusImg = 'css/images/ribbon3-g.png';
			vertretungObject.statusClass = 'green';
			break;
		case 1:
			vertretungObject.statusImg = 'css/images/ribbon3-r.png';
			vertretungObject.statusClass = 'red';
			break;
		default:
			vertretungObject.statusImg = 'css/images/ribbon3-r.png';
			vertretungObject.statusClass = 'red';
			break;
		}
		angular.extend(vertretungObject, item);
		vertretungObject.datum = parseFullDate(item.datum_uhrzeit);
		//console.log("datum:", d);
		return vertretungObject;
	};
	$scope.init = function() {
		//console.log('PpitKontaktCtrl.init');
		$scope.isLoaded = false;
		var params = {
			'sk'	: Auth.sessionKey
		};
		Datasource.request('vertretungsplan', params, function(data) {
			var parsedData = [];
			angular.forEach(data.vertretungen, function(item) {
				parsedData.push($scope.parse(item));
			});
			$scope.vertretungen = parsedData;
			$scope.isLoaded = true;
			//console.log("vertretungsplan:", parsedData);
			$scope.$apply();
		});
	};
	Auth.load();
	if (Auth.loggedIn()) {
		// main code here
		$scope.init();
		Navigation.setCurrent({"page" : "vertretungsplan"});
	} else {
		Navigation.go("login");
	}
}
VertretungsplanCtrl.$inject = [ '$scope', 'Navigation', 'Auth', 'Settings', 'Datasource' ];

/*
 * custom function
 */
function formatDate(d) {
	// format the date (Date() object) in dd.mm.yyyy format
	//alert("input: " + d);
	var curr_date = d.getDate();
	var curr_month = d.getMonth() + 1; // Months are zero based
	var curr_year = d.getFullYear();
	var dr = '' + (curr_date <= 9 ? '0' + curr_date : curr_date) + '.'
		+ (curr_month <= 9 ? '0' + curr_month : curr_month) + '.'
		+ curr_year; 
	//alert("output: " + dr);
	return dr;
}

function createDate(d) {
	//creates proper Date object from 2013-04-26 format (JS bug in Android 2.2)
	var da = d.split("-");
	// in Android parseInt counts all numbers with leading 0 as octal %)
	var utcDate = Date.UTC(da[0],parseInt(da[1],10) - 1,da[2]);
	var newD = new Date(utcDate);
	//alert("createDate: " + newD.toString());
	return newD;
}

function parseDate(ds) { // input - datum field from DB (string)
	var da = ds.split(".");
	// var cdate = new Date(da[2],da[1],da[0]);
	var res = '' + da[2] + '-' + da[1] + '-' + da[0];
	//console.log("parsed date: ", res);
	return res;
}

function parseFullDate(dateString) {// input - datum field from DB (string) dd.mm.yyyy hh:mm
	var dParsing = dateString.split(" ");
	var datum = dParsing[0].split(".");
	var uhrzeit = dParsing[1].split(":");
	//console.log("date parse:", datum, uhrzeit);
	var d = new Date();
	d.setFullYear(datum[2], parseInt(datum[1],10) - 1, datum[0]);
	d.setHours(uhrzeit[0], uhrzeit[1], 0, 0);
	return d;
}
function setCookie(name, value, expires, path, domain, secure) {
	//console.log('setCookie. path=' + path);
    if (!name || !value) return false;
    var str = name + '=' + encodeURIComponent(value);
    if (expires) str += '; expires=' + expires.toGMTString();
    if (path)    str += '; path=' + path;
    if (domain)  str += '; domain=' + domain;
    if (secure)  str += '; secure';
    document.cookie = str;
    //return true;
}