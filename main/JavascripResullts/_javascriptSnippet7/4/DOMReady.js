/**
 * @fileoverview starts the script execution after Document Object Model (DOM)
 * is loaded rather than waiting to all images to complete load
 * @method jCube.Window.DOMReady.add( fn)
 * @alias jCube( fn)
 * @param {Function} fx The funciton to be executed when the DOM is ready
 * @remarks to use jCube.Window.DOMReady.add(fn) method the library must already
 * be loaded, otherwise an issue will be throwed
 * @credit jQuery copyright (c) 2007 John Resig (jquery.com)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 * 
 * jQuery uses an hack by Matthias Miller:
 * http://www.outofhanwell.com/blog/index.php?title=the_window_onload_problem_revisited
*/
jCube.Window.DOMReady	= (function(jCube) {
	jCube.Include('Array.each');
	var DOMReady	= {
		isDOMReady:		false,
		add:	function( fn) {
			jCube.Window.DOMEvents.push(fn);
			return this;
		},
		onDOMReady:		function() {
			//if files is loaded async, ie may fires event onLoad before the scripts has really loaded
			if ( jCube.Include.asyncFiles.length > 0) {
				DOMReady.awaitingFiles	= true;
				return null;
			}
			DOMReady.awaitingFiles	= false;
			
			// Remember that the DOM is ready
			DOMReady.isDOMReady = true;
			
			if ( jCube.Window.DOMEvents ) {
				jCube.Array.each.call( jCube.Window.DOMEvents, function() {
					this.apply(document, [jCube]);
				});
				jCube.Window.DOMEvents = [];
			}
			return null;
		}
	}
	//The world would be better if only...
	if ( document.addEventListener ) {
		document.addEventListener( "DOMContentLoaded", function() {
			document.removeEventListener( "DOMContentLoaded", arguments.callee, false);
			DOMReady.onDOMReady();
		}, false );
	} else if ( document.attachEvent ) {
		// ensure firing before onload, maybe late but safe also for iframes
		document.attachEvent( "onreadystatechange", function() {
			if ( document.readyState === "complete" ) {
				document.detachEvent( "onreadystatechange", arguments.callee);
				DOMReady.onDOMReady();
			}
		});
		
		// If IE and not an iframe continually check to see if the document is ready
		if ( document.documentElement.doScroll && window == window.top ) (function(){
			if ( DOMReady.isDOMReady ) {
				return;
			}
			
			try {
				// If IE is used, use the trick by Diego Perini
				//http://javascript.nwbox.com/IEContentLoaded/
				document.documentElement.doScroll("left");
			} catch( error ) {
				setTimeout( arguments.callee, 0 );
				return;
			}
			// and execute any waiting functions
			DOMReady.onDOMReady();
		})();
	}
	
	// A fallback to window.onload, that will always work
	if ( window.addEventListener ) {
		window.addEventListener( "load", DOMReady.onDOMReady, true);
	} else {
		window.attachEvent( "onload", DOMReady.onDOMReady);
	}
	
	return DOMReady;
})(jCube);