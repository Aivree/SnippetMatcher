// no-js.js
// Elliot Harris
// Public Domain

var nojs = (function() {
  var t = {}
	t.go = function() {
		var elements = document.getElementsByClassName('no-js');
		for (var i = elements.length - 1; i >= 0; i--) {
			t.removeJS(elements[i]);
		};
	}

	t.removeJS = function(e) {
		if(typeof e.classList != 'undefined') {
			e.classList.remove('no-js');
		} else {
			e.className = e.className.replace(/\bno-js\b/,'');
		}
	}

	var readyBound = false;

	// from JQuery
	t.bindReady = function (){
		if ( readyBound ) return;
		readyBound = true;
		if ( document.addEventListener ) {
			document.addEventListener( "DOMContentLoaded", function(){
				document.removeEventListener( "DOMContentLoaded", arguments.callee, false );
				t.go();
			}, false );
		} else if ( document.attachEvent ) {
			document.attachEvent("onreadystatechange", function(){
				if ( document.readyState === "complete" ) {
					document.detachEvent( "onreadystatechange", arguments.callee );
					t.go();
				}
			});
			if ( document.documentElement.doScroll && window == window.top ) (function(){
				if ( t.isReady ) return;
				try {
					document.documentElement.doScroll("left");
				} catch( error ) {
					setTimeout( arguments.callee, 0 );
					return;
				}
				t.go();
			})();
		} else {
			window.onload(function(){t.go();});
		}
	}
	return t;
}());

// Make sure it goes when ready.
nojs.bindReady();
