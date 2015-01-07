var links = new Array();
var isIE=!!window.ActiveXObject;
links[1] = "http://localhost/index.php?m=poster&c=index&a=poster_click&siteid=1&id=14";
if (isIE){
	if (document.readyState=="complete"){
		statAD('1', '14', '11');
	} else {
		document.onreadystatechange=function(){
			if(document.readyState=="complete") statAD('1', '14', '11');
		}
	}
} else {
	statAD('1', '14', '11');
}

links[2] = "http://localhost/index.php?m=poster&c=index&a=poster_click&siteid=1&id=13";
if (isIE){
	if (document.readyState=="complete"){
		statAD('1', '13', '11');
	} else {
		document.onreadystatechange=function(){
			if(document.readyState=="complete") statAD('1', '13', '11');
		}
	}
} else {
	statAD('1', '13', '11');
}

links[3] = "http://localhost/index.php?m=poster&c=index&a=poster_click&siteid=1&id=12";
if (isIE){
	if (document.readyState=="complete"){
		statAD('1', '12', '11');
	} else {
		document.onreadystatechange=function(){
			if(document.readyState=="complete") statAD('1', '12', '11');
		}
	}
} else {
	statAD('1', '12', '11');
}

links[4] = "http://localhost/index.php?m=poster&c=index&a=poster_click&siteid=1&id=11";
if (isIE){
	if (document.readyState=="complete"){
		statAD('1', '11', '11');
	} else {
		document.onreadystatechange=function(){
			if(document.readyState=="complete") statAD('1', '11', '11');
		}
	}
} else {
	statAD('1', '11', '11');
}

var imgs = new Array();
for(var n = 1; n <= 5; n++) imgs[n] = new Image();
imgs[1].src = "http://localhost/uploadfile/2014/0512/20140512102123255.jpg";
imgs[2].src = "http://localhost/uploadfile/2014/0512/20140512102102527.jpg";
imgs[3].src = "http://localhost/uploadfile/2014/0512/20140512102041938.jpg";
imgs[4].src = "http://localhost/uploadfile/2014/0512/20140512102013765.jpg";
var tits = new Array();
tits[1] ="";
tits[2] ="";
tits[3] ="";
tits[4] ="";
var imgwidth = 1900;//图片宽度
var imgheight = 320;//图片宽度
var str = "";
str += "<span style='position:relative'>";
str += "<span><a id='dlink' href='" + links[1] + "' target='_blank'><img id='dimg' src='" + imgs[1].src + "' border='0' width='" + imgwidth + "' height='"+imgheight+"' style='filter:Alpha(opacity=100)' onmouseover='Pause(true)' onmouseout='Pause(false)'></a></span>";
//修改点1：循环添加内层div内容以增加个数
str += "</span>";
document.write(str);
var oi = document.getElementById("dimg");
var pause = false;
var curid = 1;
var lastid = 1;
var sw = 1;
var opacity = 100;
var speed = 15;
var delay = (document.all)? 400:700;

function SetAlpha(){
	if(document.all){
		if(oi.filters && oi.filters.Alpha) oi.filters.Alpha.opacity = opacity;
	} else {
		oi.style.MozOpacity = ((opacity >= 100)? 99:opacity) / 100;
	}
}

function statAD(siteid, id, pid) {
	var sp = document.createElement("SCRIPT");
	sp.src = "http://localhost/index.php?m=poster&c=index&a=show&siteid="+siteid+"&id="+id+"&spaceid="+pid;
	document.body.appendChild(sp);
}

function ImgSwitch(id, p){
	if(p){
		pause = true;
		opacity = 100;
		SetAlpha();
	}
	oi.src = imgs[id].src;
	document.getElementById("dlink").href = links[id];
	//document.getElementById("it" + lastid).className = "off";
	//document.getElementById("it" + id).className = "on";
	//document.getElementById("titnv").innerHTML = "<b>" + tits[id] + "</b>";
	curid = lastid = id;
}

function ScrollImg(){
	if(pause && opacity >= 100) return;
	if(sw == 0){
		opacity += 2;
		if(opacity > delay){ opacity = 100; sw = 1; }
	}
	if(sw == 1){
		opacity -= 3;
		if(opacity < 10){ opacity = 10; sw = 3; }
	}
	SetAlpha();
	if(sw != 3) return;
	sw = 0;
	curid++;

	if(curid > 4) curid = 1;
	ImgSwitch(curid, false);
}

function Pause(s){
	pause = s;
}

function StartScroll(){
	setInterval(ScrollImg, speed);
}

function CheckLoad(){
	if (imgs[1].complete == true && imgs[2].complete == true) {
		clearInterval(checkid);
		setTimeout(StartScroll, 2000);
	}
}
var checkid = setInterval(CheckLoad, 10);