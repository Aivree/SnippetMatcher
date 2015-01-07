// var tools = {
//    sendData:function (data, path) {
//        var url = tools.addQueryUrlParam(data, path);
//        window.log["pv"] = new Image();
//        window.log["pv"].src = url;
//    },
//    addQueryUrlParam:function (url, data) {
//        for (key in data) {
//            if (url.indexOf("?") == -1) {
//                url += "?";
//            } else {
//                url += "&";
//            }
//            url += encodeURIComponent(key) + "=" + encodeURIComponent(data[key]);
//        }
//        return url
//    }
//}

(function () {

    var DomReady = window.DomReady = {};

    // Everything that has to do with properly supporting our document ready event. Brought over from the most awesome jQuery. 

    var userAgent = navigator.userAgent.toLowerCase();

    // Figure out what browser is being used
    var browser = {
        version: (userAgent.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1],
        safari: /webkit/.test(userAgent),
        opera: /opera/.test(userAgent),
        msie: (/msie/.test(userAgent)) && (!/opera/.test(userAgent)),
        mozilla: (/mozilla/.test(userAgent)) && (!/(compatible|webkit)/.test(userAgent))
    };

    var readyBound = false;
    var isReady = false;
    var readyList = [];

    // Handle when the DOM is ready
    function domReady() {
        // Make sure that the DOM is not already loaded
        if (!isReady) {
            // Remember that the DOM is ready
            isReady = true;

            if (readyList) {
                for (var fn = 0; fn < readyList.length; fn++) {
                    readyList[fn].call(window, []);
                }

                readyList = [];
            }
        }
    };

    // From Simon Willison. A safe way to fire onload w/o screwing up everyone else.
    function addLoadEvent(func) {
        var oldonload = window.onload;
        if (typeof window.onload != 'function') {
            window.onload = func;
        } else {
            window.onload = function () {
                if (oldonload) {
                    oldonload();
                }
                func();
            }
        }
    };

    // does the heavy work of working through the browsers idiosyncracies (let's call them that) to hook onload.
    function bindReady() {
        if (readyBound) {
            return;
        }

        readyBound = true;

        // Mozilla, Opera (see further below for it) and webkit nightlies currently support this event
        if (document.addEventListener && !browser.opera) {
            // Use the handy event callback
            document.addEventListener("DOMContentLoaded", domReady, false);
        }

        // If IE is used and is not in a frame
        // Continually check to see if the document is ready
        if (browser.msie && window == top) (function () {
            if (isReady) return;
            try {
                // If IE is used, use the trick by Diego Perini
                // http://javascript.nwbox.com/IEContentLoaded/
                document.documentElement.doScroll("left");
            } catch (error) {
                setTimeout(arguments.callee, 0);
                return;
            }
            // and execute any waiting functions
            domReady();
        })();

        if (browser.opera) {
            document.addEventListener("DOMContentLoaded", function () {
                if (isReady) return;
                for (var i = 0; i < document.styleSheets.length; i++)
                    if (document.styleSheets[i].disabled) {
                        setTimeout(arguments.callee, 0);
                        return;
                    }
                // and execute any waiting functions
                domReady();
            }, false);
        }

        if (browser.safari) {
            var numStyles;
            (function () {
                if (isReady) return;
                if (document.readyState != "loaded" && document.readyState != "complete") {
                    setTimeout(arguments.callee, 0);
                    return;
                }
                if (numStyles === undefined) {
                    var links = document.getElementsByTagName("link");
                    for (var i = 0; i < links.length; i++) {
                        if (links[i].getAttribute('rel') == 'stylesheet') {
                            numStyles++;
                        }
                    }
                    var styles = document.getElementsByTagName("style");
                    numStyles += styles.length;
                }
                if (document.styleSheets.length != numStyles) {
                    setTimeout(arguments.callee, 0);
                    return;
                }

                // and execute any waiting functions
                domReady();
            })();
        }

        // A fallback to window.onload, that will always work
        addLoadEvent(domReady);
    };

    // This is the public function that people can use to hook up ready.
    DomReady.ready = function (fn, args) {
        // Attach the listeners
        bindReady();

        // If the DOM is already ready
        if (isReady) {
            // Execute the function immediately
            fn.call(window, []);
        } else {
            // Add the function to the wait list
            readyList.push(function () { return fn.call(window, []); });
        }
    };

    bindReady();

})();
var eop = {
    on : function (oElement, sEvent, fnHandler) {
        oElement.addEventListener ? oElement.addEventListener(sEvent, fnHandler, false) : (oElement["_" + sEvent + fnHandler] = fnHandler, oElement[sEvent + fnHandler] = function () {
            oElement["_" + sEvent + fnHandler]()
        }, oElement.attachEvent("on" + sEvent, oElement[sEvent + fnHandler]))
    },
    off : function (oElement, sEvent, fnHandler) {
        oElement.removeEventListener ? oElement.removeEventListener(sEvent, fnHandler, false) : oElement.detachEvent("on" + sEvent, oElement[sEvent + fnHandler])
    },
    addLoadHandler : function (fnHandler) {
        this.addHandler(window, "load", fnHandler)
    },
    getEvent : function (event) {
        return event ? event : window.event;
    },
    getTarget : function (event) {
        return event.target || event.srcElement;
    }

}
Date.prototype.format = function (format)
{
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    }

    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
      (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
          RegExp.$1.length == 1 ? o[k] :
            ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}
if (!Array.prototype.forEach) {
    Array.prototype.forEach = function (fun /*, thisp*/) {
        var len = this.length;
        if (typeof fun != "function")
            throw new TypeError();

        var thisp = arguments[1];
        for (var i = 0; i < len; i++) {
            if (i in this)
                fun.call(thisp, this[i], i, this);
        }
    };
}
function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1);
};

function guid() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
           s4() + '-' + s4() + s4() + s4();
}

function toArray(arraylike) {
    var array = new Array(arraylike.length);
    for (var i = 0, n = arraylike.length; i < n; i++)
        array[i] = arraylike[i];
    return array;
}

function getUserVisitData()
{
    var pagesVisitData = store.get('uservisit');
    if (!pagesVisitData)
        pagesVisitData = [];
    return pagesVisitData;
}