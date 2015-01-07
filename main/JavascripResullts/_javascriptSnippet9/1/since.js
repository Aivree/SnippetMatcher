var eventDateString = 'Sat Nov 08 2014 21:00:00 GMT-0500 (Eastern Standard Time)',
    eventDate = new Date(eventDateString);

var getMilliSecondsSinceEvent = function(){
	return new Date() - eventDate;
}

var getSecondsSinceEvent = function(){
	return getMilliSecondsSinceEvent()/1000;
}

var getMinutesSinceEvent = function(){
	return getSecondsSinceEvent() / 60;
}

function getHoursSinceEvent(){
	return getMinutesSinceEvent() / 60;
}

function getDaysSinceEvent(){
	return getHoursSinceEvent() / 24;
}

var getUpdate = function(){
	document.getElementById('ms').innerHTML = getMilliSecondsSinceEvent().toFixed(0);
	document.getElementById('sec').innerHTML = getSecondsSinceEvent().toFixed(0);
	document.getElementById('min').innerHTML = getMinutesSinceEvent().toFixed(0);
	document.getElementById('hrs').innerHTML = getHoursSinceEvent().toFixed(2);
	document.getElementById('days').innerHTML = getDaysSinceEvent().toFixed(3);
}

var checkLoad = function() {   
	document.readyState !== "complete" ? setTimeout(checkLoad, 11) : pageLoaded();   
};

var pageLoaded = function()
{
	getUpdate();
	setInterval(getUpdate,1000);
}

checkLoad();  

