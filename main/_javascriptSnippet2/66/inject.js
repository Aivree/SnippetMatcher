function getURLParameter(name) {
  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
}

function getHashValue(key) {
  var val = location.hash.match(new RegExp(key+'=([^&]*)'));
  return val ? val[1] : val;
}

function redir() {
  var query = getHashValue('q') ? getHashValue('q') : getURLParameter('q');

  if (query.match(/![A-Za-z0-9]+/) || query.substring(0, 2) === "! " || query.substring(0, 1) === "\\") {
    console.log("BANG");
    window.location.replace("https://duckduckgo.com/?q=" + query);
  }

}

redir();

window.onpopstate = function() {
  redir();
};
