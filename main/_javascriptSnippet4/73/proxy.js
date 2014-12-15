var http = require('http');

http.createServer(function (req, res) {
  req.headers['cookie'] && console.log(req.headers['cookie']);

  var host = req.headers['host'];
  req.headers['accept-encoding'] = 'identity';
  var r = http.request({
    host: host,
    method: req.method,
    headers: req.headers,
    path: req.url
  }, function (p_res) {
    p_res.headers['set-cookie'] && console.log(p_res.headers['set-cookie']);

    res.writeHeader(
      p_res.statusCode,
      p_res.headers
    );
       
    p_res.on('data', function (data) {
      res.write(data);
    });
    p_res.on('end', function () {
      res.end();
    });
    
    
  });

  if (req.method === 'POST') {
    req.setEncoding('utf8');
    var d = '';
    req.on('data', function (data) {
      d += data;
    });
    req.on('end', function () {
      r.end(d);
    });
  } else {
    r.end();
  }



}).listen(80);