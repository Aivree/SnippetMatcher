function doTheMagic(counter) {
  alert("It worked on " + counter);
}

// wait for document ready then call handler function
var checkLoad = function(counter) {
  counter++;
  if (document.readyState != "complete" && counter<1000) {
    var fn = function() { checkLoad(counter); };
    setTimeout(fn,10);
  } else doTheMagic(counter);
};
checkLoad(0);