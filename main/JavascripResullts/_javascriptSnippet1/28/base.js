window.dirg = {
  assert: function(x, msg) { if (!x) { throw msg; } },

  getUrlParameter: function(name) {
      return decodeURIComponent(
          (new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)')
           .exec(location.search)||[,""])[1].replace(/\+/g, '%20')
          )||null;
  }

};