/**
 * An Angular module that gives you access to the browsers local storage
 * @version v0.1.5 - 2014-11-04
 * @link https://github.com/grevory/angular-local-storage
 * @author grevory <greg@gregpike.ca>
 * @license MIT License, http://www.opensource.org/licenses/MIT
 */
(function ( window, angular, undefined ) {
/*jshint globalstrict:true*/
'use strict';

var isDefined = angular.isDefined,
  isUndefined = angular.isUndefined,
  isNumber = angular.isNumber,
  isObject = angular.isObject,
  isArray = angular.isArray,
  extend = angular.extend,
  toJson = angular.toJson,
  fromJson = angular.fromJson;


// Test if string is only contains numbers
// e.g '1' => true, "'1'" => true
function isStringNumber(num) {
  return  /^-?\d+\.?\d*$/.test(num.replace(/["']/g, ''));
}

var angularLocalStorage = angular.module('LocalStorageModule', []);

angularLocalStorage.provider('localStorageService', function() {

  // You should set a prefix to avoid overwriting any local storage variables from the rest of your app
  // e.g. localStorageServiceProvider.setPrefix('youAppName');
  // With provider you can use config as this:
  // myApp.config(function (localStorageServiceProvider) {
  //    localStorageServiceProvider.prefix = 'yourAppName';
  // });
  this.prefix = 'ls';

  // You could change web storage type localstorage or sessionStorage
  this.storageType = 'localStorage';

  // Cookie options (usually in case of fallback)
  // expiry = Number of days before cookies expire // 0 = Does not expire
  // path = The web path the cookie represents
  this.cookie = {
    expiry: 30,
    path: '/'
  };

  // Send signals for each of the following actions?
  this.notify = {
    setItem: true,
    removeItem: false
  };

  // Setter for the prefix
  this.setPrefix = function(prefix) {
    this.prefix = prefix;
    return this;
  };

   // Setter for the storageType
   this.setStorageType = function(storageType) {
     this.storageType = storageType;
     return this;
   };

  // Setter for cookie config
  this.setStorageCookie = function(exp, path) {
    this.cookie = {
      expiry: exp,
      path: path
    };
    return this;
  };

  // Setter for cookie domain
  this.setStorageCookieDomain = function(domain) {
    this.cookie.domain = domain;
    return this;
  };

  // Setter for notification config
  // itemSet & itemRemove should be booleans
  this.setNotify = function(itemSet, itemRemove) {
    this.notify = {
      setItem: itemSet,
      removeItem: itemRemove
    };
    return this;
  };

  this.$get = ['$rootScope', '$window', '$document', '$parse', function($rootScope, $window, $document, $parse) {
    var self = this;
    var prefix = self.prefix;
    var cookie = self.cookie;
    var notify = self.notify;
    var storageType = self.storageType;
    var webStorage;

    // When Angular's $document is not available
    if (!$document) {
      $document = document;
    } else if ($document[0]) {
      $document = $document[0];
    }

    // If there is a prefix set in the config lets use that with an appended period for readability
    if (prefix.substr(-1) !== '.') {
      prefix = !!prefix ? prefix + '.' : '';
    }
    var deriveQualifiedKey = function(key) {
      return prefix + key;
    };
    // Checks the browser to see if local storage is supported
    var browserSupportsLocalStorage = (function () {
      try {
        var supported = (storageType in $window && $window[storageType] !== null);

        // When Safari (OS X or iOS) is in private browsing mode, it appears as though localStorage
        // is available, but trying to call .setItem throws an exception.
        //
        // "QUOTA_EXCEEDED_ERR: DOM Exception 22: An attempt was made to add something to storage
        // that exceeded the quota."
        var key = deriveQualifiedKey('__' + Math.round(Math.random() * 1e7));
        if (supported) {
          webStorage = $window[storageType];
          webStorage.setItem(key, '');
          webStorage.removeItem(key);
        }

        return supported;
      } catch (e) {
        storageType = 'cookie';
        $rootScope.$broadcast('LocalStorageModule.notification.error', e.message);
        return false;
      }
    }());



    // Directly adds a value to local storage
    // If local storage is not available in the browser use cookies
    // Example use: localStorageService.add('library','angular');
    var addToLocalStorage = function (key, value) {
      // Let's convert undefined values to null to get the value consistent
      if (isUndefined(value)) {
        value = null;
      } else if (isObject(value) || isArray(value) || isNumber(+value || value)) {
        value = toJson(value);
      }

      // If this browser does not support local storage use cookies
      if (!browserSupportsLocalStorage || self.storageType === 'cookie') {
        if (!browserSupportsLocalStorage) {
            $rootScope.$broadcast('LocalStorageModule.notification.warning', 'LOCAL_STORAGE_NOT_SUPPORTED');
        }

        if (notify.setItem) {
          $rootScope.$broadcast('LocalStorageModule.notification.setitem', {key: key, newvalue: value, storageType: 'cookie'});
        }
        return addToCookies(key, value);
      }

      try {
        if (isObject(value) || isArray(value)) {
          value = toJson(value);
        }
        if (webStorage) {webStorage.setItem(deriveQualifiedKey(key), value)};
        if (notify.setItem) {
          $rootScope.$broadcast('LocalStorageModule.notification.setitem', {key: key, newvalue: value, storageType: self.storageType});
        }
      } catch (e) {
        $rootScope.$broadcast('LocalStorageModule.notification.error', e.message);
        return addToCookies(key, value);
      }
      return true;
    };

    // Directly get a value from local storage
    // Example use: localStorageService.get('library'); // returns 'angular'
    var getFromLocalStorage = function (key) {

      if (!browserSupportsLocalStorage || self.storageType === 'cookie') {
        if (!browserSupportsLocalStorage) {
          $rootScope.$broadcast('LocalStorageModule.notification.warning','LOCAL_STORAGE_NOT_SUPPORTED');
        }

        return getFromCookies(key);
      }

      var item = webStorage ? webStorage.getItem(deriveQualifiedKey(key)) : null;
      // angular.toJson will convert null to 'null', so a proper conversion is needed
      // FIXME not a perfect solution, since a valid 'null' string can't be stored
      if (!item || item === 'null') {
        return null;
      }

      if (item.charAt(0) === "{" || item.charAt(0) === "[" || isStringNumber(item)) {
        return fromJson(item);
      }

      return item;
    };

    // Remove an item from local storage
    // Example use: localStorageService.remove('library'); // removes the key/value pair of library='angular'
    var removeFromLocalStorage = function (key) {
      if (!browserSupportsLocalStorage || self.storageType === 'cookie') {
        if (!browserSupportsLocalStorage) {
          $rootScope.$broadcast('LocalStorageModule.notification.warning', 'LOCAL_STORAGE_NOT_SUPPORTED');
        }

        if (notify.removeItem) {
          $rootScope.$broadcast('LocalStorageModule.notification.removeitem', {key: key, storageType: 'cookie'});
        }
        return removeFromCookies(key);
      }

      try {
        webStorage.removeItem(deriveQualifiedKey(key));
        if (notify.removeItem) {
          $rootScope.$broadcast('LocalStorageModule.notification.removeitem', {key: key, storageType: self.storageType});
        }
      } catch (e) {
        $rootScope.$broadcast('LocalStorageModule.notification.error', e.message);
        return removeFromCookies(key);
      }
      return true;
    };

    // Return array of keys for local storage
    // Example use: var keys = localStorageService.keys()
    var getKeysForLocalStorage = function () {

      if (!browserSupportsLocalStorage) {
        $rootScope.$broadcast('LocalStorageModule.notification.warning', 'LOCAL_STORAGE_NOT_SUPPORTED');
        return false;
      }

      var prefixLength = prefix.length;
      var keys = [];
      for (var key in webStorage) {
        // Only return keys that are for this app
        if (key.substr(0,prefixLength) === prefix) {
          try {
            keys.push(key.substr(prefixLength));
          } catch (e) {
            $rootScope.$broadcast('LocalStorageModule.notification.error', e.Description);
            return [];
          }
        }
      }
      return keys;
    };

    // Remove all data for this app from local storage
    // Also optionally takes a regular expression string and removes the matching key-value pairs
    // Example use: localStorageService.clearAll();
    // Should be used mostly for development purposes
    var clearAllFromLocalStorage = function (regularExpression) {

      regularExpression = regularExpression || "";
      //accounting for the '.' in the prefix when creating a regex
      var tempPrefix = prefix.slice(0, -1);
      var testRegex = new RegExp(tempPrefix + '.' + regularExpression);

      if (!browserSupportsLocalStorage || self.storageType === 'cookie') {
        if (!browserSupportsLocalStorage) {
          $rootScope.$broadcast('LocalStorageModule.notification.warning', 'LOCAL_STORAGE_NOT_SUPPORTED');
        }

        return clearAllFromCookies();
      }

      var prefixLength = prefix.length;

      for (var key in webStorage) {
        // Only remove items that are for this app and match the regular expression
        if (testRegex.test(key)) {
          try {
            removeFromLocalStorage(key.substr(prefixLength));
          } catch (e) {
            $rootScope.$broadcast('LocalStorageModule.notification.error',e.message);
            return clearAllFromCookies();
          }
        }
      }
      return true;
    };

    // Checks the browser to see if cookies are supported
    var browserSupportsCookies = (function() {
      try {
        return $window.navigator.cookieEnabled ||
          ("cookie" in $document && ($document.cookie.length > 0 ||
          ($document.cookie = "test").indexOf.call($document.cookie, "test") > -1));
      } catch (e) {
          $rootScope.$broadcast('LocalStorageModule.notification.error', e.message);
          return false;
      }
    }());

    // Directly adds a value to cookies
    // Typically used as a fallback is local storage is not available in the browser
    // Example use: localStorageService.cookie.add('library','angular');
    var addToCookies = function (key, value) {

      if (isUndefined(value)) {
        return false;
      } else if(isArray(value) || isObject(value)) {
        value = toJson(value);
      }

      if (!browserSupportsCookies) {
        $rootScope.$broadcast('LocalStorageModule.notification.error', 'COOKIES_NOT_SUPPORTED');
        return false;
      }

      try {
        var expiry = '',
            expiryDate = new Date(),
            cookieDomain = '';

        if (value === null) {
          // Mark that the cookie has expired one day ago
          expiryDate.setTime(expiryDate.getTime() + (-1 * 24 * 60 * 60 * 1000));
          expiry = "; expires=" + expiryDate.toGMTString();
          value = '';
        } else if (cookie.expiry !== 0) {
          expiryDate.setTime(expiryDate.getTime() + (cookie.expiry * 24 * 60 * 60 * 1000));
          expiry = "; expires=" + expiryDate.toGMTString();
        }
        if (!!key) {
          var cookiePath = "; path=" + cookie.path;
          if(cookie.domain){
            cookieDomain = "; domain=" + cookie.domain;
          }
          $document.cookie = deriveQualifiedKey(key) + "=" + encodeURIComponent(value) + expiry + cookiePath + cookieDomain;
        }
      } catch (e) {
        $rootScope.$broadcast('LocalStorageModule.notification.error',e.message);
        return false;
      }
      return true;
    };

    // Directly get a value from a cookie
    // Example use: localStorageService.cookie.get('library'); // returns 'angular'
    var getFromCookies = function (key) {
      if (!browserSupportsCookies) {
        $rootScope.$broadcast('LocalStorageModule.notification.error', 'COOKIES_NOT_SUPPORTED');
        return false;
      }

      var cookies = $document.cookie && $document.cookie.split(';') || [];
      for(var i=0; i < cookies.length; i++) {
        var thisCookie = cookies[i];
        while (thisCookie.charAt(0) === ' ') {
          thisCookie = thisCookie.substring(1,thisCookie.length);
        }
        if (thisCookie.indexOf(deriveQualifiedKey(key) + '=') === 0) {
          var storedValues = decodeURIComponent(thisCookie.substring(prefix.length + key.length + 1, thisCookie.length))
          try{
            var obj = JSON.parse(storedValues);
            return fromJson(obj)
          }catch(e){
            return storedValues
          }
        }
      }
      return null;
    };

    var removeFromCookies = function (key) {
      addToCookies(key,null);
    };

    var clearAllFromCookies = function () {
      var thisCookie = null, thisKey = null;
      var prefixLength = prefix.length;
      var cookies = $document.cookie.split(';');
      for(var i = 0; i < cookies.length; i++) {
        thisCookie = cookies[i];

        while (thisCookie.charAt(0) === ' ') {
          thisCookie = thisCookie.substring(1, thisCookie.length);
        }

        var key = thisCookie.substring(prefixLength, thisCookie.indexOf('='));
        removeFromCookies(key);
      }
    };

    var getStorageType = function() {
      return storageType;
    };

    // Add a listener on scope variable to save its changes to local storage
    // Return a function which when called cancels binding
    var bindToScope = function(scope, key, def, lsKey) {
      lsKey = lsKey || key;
      var value = getFromLocalStorage(lsKey);

      if (value === null && isDefined(def)) {
        value = def;
      } else if (isObject(value) && isObject(def)) {
        value = extend(def, value);
      }

      $parse(key).assign(scope, value);

      return scope.$watch(key, function(newVal) {
        addToLocalStorage(lsKey, newVal);
      }, isObject(scope[key]));
    };

    // Return localStorageService.length
    // ignore keys that not owned
    var lengthOfLocalStorage = function() {
      var count = 0;
      var storage = $window[storageType];
      for(var i = 0; i < storage.length; i++) {
        if(storage.key(i).indexOf(prefix) === 0 ) {
          count++;
        }
      }
      return count;
    };

    return {
      isSupported: browserSupportsLocalStorage,
      getStorageType: getStorageType,
      set: addToLocalStorage,
      add: addToLocalStorage, //DEPRECATED
      get: getFromLocalStorage,
      keys: getKeysForLocalStorage,
      remove: removeFromLocalStorage,
      clearAll: clearAllFromLocalStorage,
      bind: bindToScope,
      deriveKey: deriveQualifiedKey,
      length: lengthOfLocalStorage,
      cookie: {
        isSupported: browserSupportsCookies,
        set: addToCookies,
        add: addToCookies, //DEPRECATED
        get: getFromCookies,
        remove: removeFromCookies,
        clearAll: clearAllFromCookies
      }
    };
  }];
});
})( window, window.angular );
(function () {
	'use strict';
	var sdk = angular.module('GoldarkSDK', ['LocalStorageModule']);
	var services = angular.module('GoldarkSDK.services', ['GoldarkSDK']);
	var directives = angular.module('GoldarkSDK.directives', ['GoldarkSDK']);
	//provider
	sdk.provider('goldarkConfig', function GoldarkConfigProvider () {
		var config = {
			apiToken: null,
			appName: null,
			accessToken: null,
			user: null,
			schema: null,
			ssl: true
		};
		this.setAPIToken = function (apiToken) {
			config.apiToken = apiToken;
		};
		this.accessToken = function (accessToken) {
			config.accessToken = accessToken;
		};
		this.setAppName = function (appName) {
			config.appName = appName;
		};
		this.setSSL = function (ssl) {
			config.ssl = ssl;
		};
		this.$get = function (localStorageService) {
			config.accessToken = localStorageService.get('Goldark.accessToken');
			config.user = {
				id: localStorageService.get('Goldark.userid'),
				username: localStorageService.get('Goldark.username'),
				sessionId: localStorageService.get('Goldark.sessionId')
			}
			return config;
		};
	});
	//query builder service
	services.factory('GDQuery', function() {
		function Query(config) {
			this.perpage = 20;
			this.page = 0;
			this.total = null;
			if (config) {
				if (config.perpage) {
					this.perpage = config.perpage;
				}
				if (config.page) {
					this.page = config.page;
				}
			}
			this.query = {};
		}
		Query.prototype.next = function () {
			if (this.total != null) {
				if ((this.page + 1) < this.total) {
					this.page += 1;
				}
			}
		};
		Query.prototype.next = function () {
			if (this.page > 0) {
				this.page -= 1;
			}
		};
		Query.prototype.equals = function (name, val) {
			this.query[name] = val;
			return this;
		};
		Query.prototype.contains = function (name, val) {
			this.query[name] = '$regex:' + val;
			return this;
		};
		Query.prototype.notEquals = function (name, val) {
			this.query[name] = '$ne:' + val;
			return this;
		};
		Query.prototype.greater = function (name, val) {
			this.query[name] = '$gte:' + val;
			return this;
		};
		Query.prototype.greaterOrEqual = function (name, val) {
			this.query[name] = '$gte:' + val;
			return this;
		};
		Query.prototype.lessThan = function (name, val) {
			this.query[name] = '$lt:' + val;
			return this;
		};
		Query.prototype.lessThanOrEqual = function (name, val) {
			this.query[name] = '$lte:' + val;
			return this;
		};
		Query.prototype.containsItems = function (name, val) {
			this.query[name] = '$in:' + val;
			return this;
		};
		Query.prototype.doesNotContainItems = function (name, val) {
			this.query[name] = '$nin:' + val;
			return this;
		};
		Query.prototype.hasSizeOf = function (name, val) {
			this.query[name] = '$size:' + val;
			return this;
		};
		Query.prototype.near = function (name, val) {
			this.query[name] = '$size:' + val;
			return this;
		};
		Query.prototype.clear = function () {
			this.query = {};
			return this;
		};
		Query.prototype.toQueryString = function () {
			var qs = '';
			for (var item in this.query) {
				if (item[0] != '_') {
					qs += (qs.length == 0 ? '' : '&') + item + '=' + this.query[item];
				}
			}
			qs = qs.replace(/&$/, '').replace(/\s+/gim, '+');
			qs += '&page=' + this.page;
			qs += '&per_page' + this.perPage;
			return qs;
		};
		return Query;
	});
	//model object service
	services.factory('GDObject', function ($http, $q, goldarkConfig) {
		function Model() {
			this.name = null;
			this.id = null;
			this.field = null;
		}
		Model.withName = function (name) {
			var obj = new Model();
			obj.name = name;
			return obj;
		};
		Model.prototype.withId = function (id) {
			this.id = id;
			return this;
		};
		Model.prototype.withField = function (field) {
			this.field = field;
			return this;
		};
		Model.prototype.withIdAndField = function (id, field) {
			this.id = id;
			this.field = field;
			return this;
		};
		Model.prototype.create = function (data) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name;
			var headers = {
				'X-Api-Token': config.apiToken
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken
			}
			$http
			.post(url, JSON.stringify(data), {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.search = function (query) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '?' + (query ? query.toQueryString() : '');
			console.log(url);
			var headers = {
				'X-Api-Token': config.apiToken
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken
			}
			$http
			.get(url, {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				if (query) {
					query._total = data.total;
				}
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.update = function (data) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '/' + this.id + (this.field ? '/' + this.field : '');
			var headers = {
				'X-Api-Token': config.apiToken
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken;
			}
			$http
			.put(url, JSON.stringify(this.field ? {'value': data} : data), {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.get = function (id) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '/' + this.id + (this.field ? '/' + this.field : '');
			var headers = {
				'X-Api-Token': config.apiToken
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken
			}
			$http
			.get(url, {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.delete = function (id) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '/' + this.id;
			var headers = {
				'X-Api-Token': config.apiToken
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken
			}
			$http
			.delete(url, {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.upload = function (data, mimetype) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '/' + this.id + '/' + this.field;
			var headers = {
				'X-Api-Token': config.apiToken,
				'X-File-Encoding': 'base64',
				'Content-Type': (mimetype ? mimetype : 'text/plain')
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken;
			}
			$http
			.put(url, data, {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		Model.prototype.download = function () {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/' + this.name + '/' + this.id + '/' + this.field;
			var headers = {
				'X-Api-Token': config.apiToken,
				'X-File-Encoding': 'base64',
				'Content-Type': (mimetype ? mimetype : 'text/plain')
			};
			if (goldarkConfig.accessToken) {
				headers['X-Access-Token'] = config.accessToken;
			}
			$http
			.get(url, {headers: headers})
			.success(function (data, status, headers) {
				var response = {
					code: status,
					response: {
						contents: data,
						mimetype: headers('Content-Type'),
						encoding: 'base64',
						url: 'data:' + headers('Content-Type') + ';base64,' + data
					}
				};
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		return Model;
	});
	services.factory('GDUser', function ($http, $q, $rootScope, goldarkConfig, GDObject, localStorageService) {
		function User() {
			this.name = null;
			this.id = null;
			this.field = null;
			this.username = null;
		}
		User.withUsername = function (username) {
			var obj = new User();
			obj.username = username;
			return obj;
		};
		User.withId = function (id) {
			var obj = new User();
			obj.id = id;
			return obj;
		};
		User.withSelf = function () {
			var obj = new User();
			obj.id = localStorageService.get('Goldark.userId');
			return obj;
		};
		User.prototype.withField = function (field) {
			this.field = field;
			return this;
		};
		User.prototype.withIdAndField = function (id, field) {
			this.id = id;
			this.field = field;
			return this;
		};
		User.prototype.signup = function (data) {
			return GDObject.withName('users').create(data);
		};
		User.prototype.search = function (query) {
			if (query) {
				return GDObject.withName('users').search(query);
			}
			return GDObject.withName('users').search();
		};
		User.prototype.update = function (data) {
			return GDObject.withName('users').withId(this.id ? this.id : this.username).update(data);
		};
		User.prototype.get = function () {
			return GDObject.withName('users').withId(this.id ? this.id : this.username).get();
		};
		User.prototype.delete = function () {
			return GDObject.withName('users').withId(this.id ? this.id : this.username).delete();
		};
		User.prototype.login = function(password) {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			GDModel
			.withName('sessions')
			.create({username: this.username, password: password})
			.success(function (data) {
				localStorageService.set('Goldark.userId', data.user_id);
				localStorageService.set('Goldark.username', data.username);
				localStorageService.set('Goldark.accessToken', data.token);
				localStorageService.set('Goldark.sessionId', data.id);
				$rootScope.$broadcast('Goldark.userLoggedIn', data.response);
				deferred.resolve(data);

			})
			.error(function (data) {
				deferred.reject(data);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		User.prototype.logout = function() {
			var deferred = $q.defer();
	    	var promise = deferred.promise;
			var config = goldarkConfig;
			var protocol = config.ssl ? 'https' : 'http';
			var url = protocol + '://' + config.appName + '.goldarkapi.com/sessions';
			var headers = {
				'X-Api-Token': config.apiToken,
				'X-Access-Token': config.accessToken
			};
			$http
			.delete(url, {headers: headers})
			.success(function (data, status) {
				var response = {
					code: status,
					response: data
				};
				return localStorageService.clearAll(/^Goldark\..*/);
				$rootScope.$broadcast('Goldark.userLoggedOut', {});
				deferred.resolve(response);
			})
			.error(function (data, status) {
				var response = {
					code: status,
					error: data.responseJSON
				};				
				deferred.reject(response);
			});
			promise.success = function(fn) {
				promise.then(fn);
				return promise;
			}
			promise.error = function(fn) {
				promise.then(null, fn);
				return promise;
			}
			return promise;
		};
		return User;
	});
	services.factory('GDPush', function ($http, $q, $rootScope, goldarkConfig, GDObject) {
		//devices
		function Device() {
			this.id = null;
			this.userId = null;
		}
		Device.all = function () {
			var device = new Device();
			return device;
		};
		Device.withId = function (id) {
			var device = new Device();
			device.id = id;
			return device;
		};
		Device.withUserId = function (userId) {
			var device = new Device();
			device.userId = userId;
			return device;
		};
		Device.create = function(data) {
			return GDModel.withName('push/devices').create(data);
		};
		Device.prototype.update = function (data) {
			return GDModel.withName('push/devices').withId(this.id).update(data);
		};
		Device.prototype.search = function (query) {
			if (query) {
				return GDModel.withName('push/devices').search(query);
			}
			return GDModel.withName('push/devices').search();
		};
		Device.prototype.get = function () {
			return GDModel.withName('push/devices').withId(this.id).get();
		};
		Device.prototype.sendMessage = function (message) {
			if (this.id) {
				return GDModel.withName('push/devices/' + this.id + '/messages').create({message: message});
			}
			if (this.userId) {
				return GDModel.withName('push/users/' + this.id + '/messages').create({message: message});
			}
			return GDModel.withName('push/devices/all/messages').create({message: message});
		};
		//messages
		function Message() {
			this.id = null;
		}
		Message.withId = function (id) {
			var message = new Message();
			message.id = id;
			return message;
		}
		Message.search = function (query) {
			if (query) {
				return GDModel.withName('push/messages').search(query);
			}
			return GDModel.withName('push/messages').search();
		};
		Message.prototype.get = function () {
			return GDModel.withName('push/messages').withId(this.id).get();
		};
		var push = {
			Device: Device,
			Message: Message
		};
		return push;
	});
	//image directive
	directives.directive('gdImage', ['$compile', '$http', 'goldarkConfig', function ($compile, $http, goldarkConfig) {
		return {
			scope: {
				url: '@',
				placeholder: '@',
				error: '@',
				size: '@'
			},
			restrict: 'E',
			replace: true,
			link: function(scope, elem, attrs) {
				var config = goldarkConfig;
				var img = document.createElement('img');
				if (attrs.placeholder) {
					img.src = attrs.placeholder;
				}
				if (attrs.size) {
					var splitSize = attrs.size.split('x');
					img.width = splitSize[0];
					img.height = splitSize[1];
				}
				elem.replaceWith(img);
				var headers = {
					'X-Api-Token': config.apiToken,
					'X-File-Encoding': 'base64'
				};
				if (goldarkConfig.accessToken) {
					headers['X-Access-Token'] = goldarkConfig.accessToken;
				}
				if (attrs.url) {
					$http.get(attrs.url, {'headers': headers})
					.success(function (data, status, headers, config) {
						img.src = 'data:' + headers('Content-Type') + ';base64,' + data;
					})
					.error(function (data, status, headers, config) {
						if (attrs.error) {
							img.src = attrs.error;
						}
					});
				}
			}
		};
	}]);
}).call(this);