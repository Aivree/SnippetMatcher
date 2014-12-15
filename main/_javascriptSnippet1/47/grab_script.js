// function getURLParameter(name) {
//     return decodeURIComponent(
//         (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
//     );
// }


// if ( getURLParameter( 'fbcollab_scrape' ) != "null") {
//   chrome.runtime.sendMessage({ event: 'Scrape', data: {script: document.getElementsByTagName("pre")[0].textContent } })
//   window.close();
// }