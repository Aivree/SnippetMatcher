var urlParams;
(window.onpopstate = function () {
  var match,
  pl     = /\+/g,  // Regex for replacing addition symbol with a space
  search = /([^&=]+)=?([^&]*)/g,
  decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
  query  = window.location.search.substring(1);

  urlParams = {};
  while (match = search.exec(query))
    urlParams[decode(match[1])] = decode(match[2]);
})();

if (urlParams["p"] == "projects" ) {
  // projects
  $(function() {
    $("#pageContent").load("projects.html");
  });
} else if (urlParams["p"] == "gpg") {
  $(function() {
    $("#pageContent").load("njt.html");
  });
} else { 
  // home
  $(function() {
    $("#pageContent").load("home.html");
  });
}
