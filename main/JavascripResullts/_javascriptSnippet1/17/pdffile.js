var url = getURLParameter('file');


function getURLParameter(name) {
    var url = decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null
    return url + ".pdf";
}
