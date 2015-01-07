
// Document Ready
// http://stackoverflow.com/questions/799981/document-ready-equivalent-without-jquery
var ready=function(){function g(){if(!a.isReady){try{document.documentElement.doScroll("left")}catch(b){setTimeout(g,1);return}a.ready()}}var e,c,m={"[object Boolean]":"boolean","[object Number]":"number","[object String]":"string","[object Function]":"function","[object Array]":"array","[object Date]":"date","[object RegExp]":"regexp","[object Object]":"object"},a={isReady:!1,readyWait:1,holdReady:function(b){b?a.readyWait++:a.ready(!0)},ready:function(b){if(!0===b&&!--a.readyWait||!0!==b&&!a.isReady){if(!document.body)return setTimeout(a.ready,
1);a.isReady=!0;!0!==b&&0<--a.readyWait||e.resolveWith(document,[a])}},bindReady:function(){if(!e){e=a._Deferred();if("complete"===document.readyState)return setTimeout(a.ready,1);if(document.addEventListener)document.addEventListener("DOMContentLoaded",c,!1),window.addEventListener("load",a.ready,!1);else if(document.attachEvent){document.attachEvent("onreadystatechange",c);window.attachEvent("onload",a.ready);var b=!1;try{b=null==window.frameElement}catch(f){}document.documentElement.doScroll&&
b&&g()}}},_Deferred:function(){var b=[],f,c,e,h={done:function(){if(!e){var c=arguments,d,g,j,l,k;f&&(k=f,f=0);d=0;for(g=c.length;d<g;d++)j=c[d],l=a.type(j),"array"===l?h.done.apply(h,j):"function"===l&&b.push(j);k&&h.resolveWith(k[0],k[1])}return this},resolveWith:function(a,d){if(!e&&!f&&!c){d=d||[];c=1;try{for(;b[0];)b.shift().apply(a,d)}finally{f=[a,d],c=0}}return this},resolve:function(){h.resolveWith(this,arguments);return this},isResolved:function(){return!(!c&&!f)},cancel:function(){e=1;b=
[];return this}};return h},type:function(a){return null==a?String(a):m[Object.prototype.toString.call(a)]||"object"}};document.addEventListener?c=function(){document.removeEventListener("DOMContentLoaded",c,!1);a.ready()}:document.attachEvent&&(c=function(){"complete"===document.readyState&&(document.detachEvent("onreadystatechange",c),a.ready())});return function(b){a.bindReady();a.type(b);e.done(b)}}();


function getAllImages() {
	var articles = document.getElementsByTagName('article');
	var imgs = new Array();

	if (articles.length > 0) {

		// iterate the articles array and get img elements of them
		for (var i = 0; i < articles.length; i++) {
			var imgsCurrent = articles[i].getElementsByTagName('img');

			// if there are some img inside the post (article), push to array of images
			if (imgsCurrent.length > 0) {
				for (var j = 0; j < imgsCurrent.length; j++) {
					imgs.push(imgsCurrent[j]);
				}
			}
		}
	}

	return imgs;
}

function setImageDescription(images) {

	for (var i = 0; i < images.length; i++) {
		newDiv = document.createElement('div');
		newDiv.className = 'image';

		parent = imgs[i].parentNode;
		parent.replaceChild(newDiv, imgs[i]);

		description = document.createElement('span');
		description.innerHTML = imgs[i].alt;

		linkZoom = document.createElement('a');
		linkZoom.setAttribute('href', imgs[i].src);
		linkZoom.setAttribute('class', 'linkZoom');
		linkZoom.innerHTML = "...";

		newDiv.appendChild(imgs[i]);
		newDiv.appendChild(description);
		newDiv.appendChild(linkZoom);
	}
}

function adjustImagePercentSize(images) {

	for (var i = 0; i < images.length; i++) {
		widthReal = images[i].naturalWidth;
		widthAdjusted = images[i].clientWidth;

		percent = widthAdjusted * 100 / widthReal;
		parent = images[i].parentNode;
		elementZoom = parent.getElementsByTagName('a');
		elementZoom[0].innerHTML = Math.round(percent) + "%";

	}

}

function adjustDivSize(images) {

	for (var i = 0; i < images.length; i++) {
		if (images[i].naturalWidth < 700) {

			divParent = images[i].parentNode;
			divParent.style.margin = '0px auto';

			pParent = divParent.parentNode;
			pParent.style.margin = '0 auto';
			pParent.style.maxWidth = (images[i].naturalWidth + 20) + 'px';
		}
	}

}

ready(function() {

	imgs = getAllImages(); // array with all images within each post
	setImageDescription(imgs); // set a <span> with de alt text of image

	// When the page is loaded
	window.onload = function() {

		// when the page is loaded set the image % size
		adjustImagePercentSize(imgs);
		
		adjustDivSize(imgs);

		// pool : http://thomasgriffinmedia.com/blog/2013/05/handy-javascript-polling-function-for-resize-and-other-events/ (on 06/01/2013)
		var poll = (function(){
		    var timer = 0;
		    return function(callback, ms){
		        clearTimeout(timer);
        		timer = setTimeout(callback, ms);
    		};
		})();
		window.onresize = function() {
	    	poll(function(){
	    		// adjust the images % size every 25 ms
	        	adjustImagePercentSize(imgs);
	    	}, 25);
		};
	
	}
});