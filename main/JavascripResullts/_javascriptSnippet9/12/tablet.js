/*! Copyright 2014 Infocinc (www.infocinc.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/

///////////////////////////////////////////////////////////////////////
// Globals
///////////////////////////////////////////////////////////////////////
var tablet = namespace('ifc.tablet');
var notouch = $('html').hasClass('no-touch');

//////////////////////////////////////////////////////////////////////////////////
// Enquire registering
//////////////////////////////////////////////////////////////////////////////////

function configure_enquire() {
    _switch = false;
	_slideshow = false;

    enquire.register("screen and (max-width:991px)", {
    	match: function() {
            if (section === 'home') {
            	_slideshow = true;
            	$.vegas('destroy','background');
		        $.vegas('slideshow', {
		        	loading: false,
		        	backgrounds: [
						{  src:'../../images/home/bg-4.jpg', fade:1000, valign:'top' },
						{  src:'../../images/home/bg-2.jpg', fade:1000, valign:'top' },
						{  src:'../../images/home/bg-1.jpg', fade:1000, valign:'top' },
						{  src:'../../images/home/bg-3.jpg', fade:1000, valign:'top' }
					]
				})('overlay', {
					src:'../../images/overlays/02.png'
				});
				$('body').bind('vegasstart', function() {
					$('#bg-wrapper').removeClass('invisible');
				});
			}
    	}
    });

    enquire.register("screen and (min-width:768px)", {

        unmatch: function() {
            _switch = true;
            if (app_config['side-menu']) {
                init_sidemenu();
            }
        },
        match: function() {
            if (_switch && app_config['side-menu']) {
                init_sidemenu();
            }
            if (notouch) {
                add_interaction('#footer-contact a, #footer-community a,'+
                    '#footer-nav a', 'hover-underline');
                add_interaction('.center-navigation a','navbox-hover');
            }
        }
    });
    enquire.register("screen and (min-width:992px)", {

        unmatch: function() {
            _switch = true;
            if (app_config['side-menu']) {
                init_sidemenu();
            }
        },

        match: function() {
/*            if (_switch && app_config['side-menu']) {
                init_sidemenu();
            }
*/            if (notouch) {
                add_interaction('#footer-contact a, #footer-community a,'+
                    '#footer-nav a', 'hover-underline');
                add_interaction('.center-navigation a','navbox-hover');
            }
            if (section === 'home') {
            	if (_slideshow) {
            		$.vegas('stop');
            	}
		        $.vegas({
		        		src: '../../images/home/bg-desktop.jpg',
		        		fade: 2000,
		        		load: function() {
		        			$("#bg-wrapper").removeClass('invisible');
		        		}
		        });
		        $.vegas('overlay', {
		        	src: '../../images/overlays/02.png'
		        });

            }
            if ($('#team').length !== 0) {
				window.addEventListener('load', function() {
					var video = document.querySelector('#team');
					function checkLoad() {
						if (video.readyState === 4) {
							$('.preloader').fadeOut(1000);
							video.play();
						} else {
						setTimeout(checkLoad, 100);
						}
					}
					checkLoad();
				}, false);
            }
        }
    });

}


/////////////////////////////////////////////////////////////////
// Resize handler
/////////////////////////////////////////////////////////////////
tablet.resize = function() {
    mediaState = query_screenwidth(app_config['screenwidth-tag']);
}

tablet.bind_resizehandler = function() {
    window.addEventListener('resize', tablet.resize, false);
}


tablet.detect_features = function(complete) {
    var load = [{
        test: window.matchMedia,
        nope: "/js/vendor/media.match.min.js"
    }, {
        both: ['/js/vendor/enquire.min.js'],
        complete: function() {
            complete();
        }
    }];
    Modernizr.load(load);
}


tablet.init = function() {
//    tablet.bind_resizehandler();
    tablet.detect_features(configure_enquire);
}
