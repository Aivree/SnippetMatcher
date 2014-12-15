var http = require('http');

var proto = {};

/**
 * Decorate a http server with easy to use client request methods.
 *
 * @param {http.Server} server  http.Server to decorate
 */
module.exports = function decorate(server) {
  if (!server) { throw new Error('provide server'); }
  if (!(server instanceof http.Server)) { throw new TypeError('server must be a http.Server'); }

  // setup request methods on the given server
  Object.keys(proto).forEach(function(key) {
    if (server[key]) {
      throw new Error('server already has a property ' + key);
    }

    server[key] = proto[key];
  });

  return server;
};

function _putJSON(spec) {
  spec = spec || {};
  spec.headers = spec.headers || {};
  spec.headers['Content-Type'] = spec.headers['Content-Type'] || 'application/json';
  spec.method = spec.method || 'PUT';

  if (!spec.url) { throw new Error('provide url'); }
  if (typeof spec.url !== 'string') { throw new TypeError('url must be a string'); }

  if (this.basicAuth) {
    spec.headers.Authorization = 'Basic ' + this.basicAuth;
  }

  var address = this.address();
  if (!address) {
    throw new Error('can not obtain address, maybe the server is not listening yet');
  }

  var options = {
    headers: spec.headers,
    host: address.address,
    port: address.port,
    path: spec.url,
    method: spec.method
  };

  var req = http.request(options, function (res) {
    var body = new Buffer(0);

    res.on('data', function (chunk) {
      body = Buffer.concat([body, chunk]);
    });

    res.on('end', function () {
      if (!body.length) {
        spec.callback(res);
      } else if (res.headers['content-type'] && res.headers['content-type'].match(/^text/)) {
        spec.callback(res, body.toString());
      } else if (/^application\/json/.test(res.headers['content-type'])) {
        spec.callback(res, JSON.parse(body.toString()));
      } else if (/^application\/x-json-stream/.test(res.headers['content-type'])) {
        var lines = body.toString().split('\n');
        var result = [];
        lines.forEach(function (line) {
          if (line) { result.push(JSON.parse(line)); }
        });
        spec.callback(res, result);
      } else {
        spec.callback(res, body);
      }
    });
  });

  req.on('error', function (e) {
    console.log('ERROR: ', e);
  });

  if (spec.data) {
    if (spec.headers['Content-Type'] === 'application/json') {
      req.write(JSON.stringify(spec.data));
    } else {
      req.write(spec.data);
    }
  }
  req.end();
}

/**
 * Set basic authentication for all requests.
 *
 * @param {String} username  the username to submit
 * @param {String} password  the password to submit
 */
proto.setBasicAuth = function setBasicAuth(username, password) {
  this.basicAuth = null;
  if (typeof username === 'string' || typeof password === 'string') {
    this.basicAuth = new Buffer('' + username + ':' + password).toString('base64');
  }
};

proto.get = function get(url, headers, cb) {
  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'GET', callback: cb, headers: headers });
};

proto.head = function head(url, headers, cb) {
  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'HEAD', callback: cb, headers: headers });
};

proto.post = function post(url, data, headers, cb) {
  if (typeof data === 'undefined') { throw new Error('provide data'); }
  if (typeof data === 'function') { throw new TypeError('data can not be a function'); }

  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'POST', data: data, callback: cb, headers: headers });
};

proto.put = function put(url, data, headers, cb) {
  if (typeof data === 'undefined') { throw new Error('provide data'); }
  if (typeof data === 'function') { throw new TypeError('data can not be a function'); }

  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'PUT', data: data, callback: cb, headers: headers });
};

proto.patch = function patch(url, data, headers, cb) {
  if (typeof data === 'undefined') { throw new Error('provide data'); }
  if (typeof data === 'function') { throw new TypeError('data can not be a function'); }

  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'PATCH', data: data, callback: cb, headers: headers });
};

proto.del = function del(url, headers, cb) {
  headers = headers || {};
  if (typeof headers === 'function') {
    cb = headers;
    headers = {};
  }
  cb = cb || function () {};
  _putJSON.call(this, { url: url, method: 'DELETE', callback: cb, headers: headers });
};
