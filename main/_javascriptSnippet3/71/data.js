function (e, params) {
  var viewdata = {querystring: ''}
    , params = params || {}
    , qs, qo, split

  if (params.q) {
    viewdata.querystring = params.q
  } else {
    qs = window.location.search

    if (qs.length > 0) {
      qs =  qs.substring(1),
      split = qs.split('&')

      qo = {}
      for(var i = 0; i < split.length; i++) {
          var kv = split[i].split('=');
          if (kv[0] !== 'q') continue 
          qo[kv[0]] = decodeURIComponent(kv[1] ? kv[1].replace(/\+/g, ' ') : kv[1]);
      }
      viewdata.querystring = qo.q
    }
  }
    
  viewdata.result = params.result

  if (!params.result && viewdata.querystring.length > 0)
    $(this).trigger('doSearch', [{'q': viewdata.querystring}])

  $('#navbarcontent').trigger('renderNavbar', [{'title': 'search'}])
  $('#menuleftcontent').trigger('_init', ['/search'])

  return viewdata
}
