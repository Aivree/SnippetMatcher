 $(document).ready(function() {
	 
	 // Read Query String Parameters
	 var urlParams = {};
	(function () {
		var match,
			pl     = /\+/g,  // Regex for replacing addition symbol with a space
			search = /([^&=]+)=?([^&]*)/g,
			decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
			query  = window.location.search.substring(1);
	
		while (match = search.exec(query))
		   urlParams[decode(match[1])] = decode(match[2]);
	})();

	 // Contact Form Auto Fill Subject Field
	 $('#si_contact_subject1').val(urlParams["subject"]);
	 
	 // Product page vertical tabs
	 $("#vtabs1").jVertTabs();
	 
 });


