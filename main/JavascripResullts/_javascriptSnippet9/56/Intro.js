/*
 * jslint browser: true, maxerr: 50, indent: 4, devel: true, white:true;
 *//*!
 * @dependency jquery-1.7.2.min.js 
 * @dependency jquery.waitforimages.js
 */
;(function($, model, window, document, undefined) {
	
	"use strict";
	
	var Model = model.Model = model.Model || (function() {
		
		$(document).ready(function() { $('body').waitForImages(function() { loadAllIntroImagesFirst(); });});

		var cues = [];
		var frameDelay = 0;
		
		// handles how and when the steering wheel 
		// will turn from different positions
		var wheelMoves = [ 
		                  { time : 2.2, pos : 'left' }, 
		                  { time : 2.4, pos : 'center' },
		                  { time : 2.7, pos : 'right' },
						  { time : 2.9, pos : 'center' },
						  { time : 4 , pos : 'left' },
						  { time :  4.9, pos : 'center' },
						  { time :  5.8, pos : 'left' },
						  { time : 6.1 , pos : 'center' },
						  { time :  6.6, pos : 'left' },
						  { time :  7.1, pos : 'center' },
						  { time :  8.0, pos : 'left' },
						  { time : 8.3 , pos : 'center' },
						  { time : 8.5 , pos : 'right' },
						  { time :  8.9, pos : 'center' },
						  { time :  10.1, pos : 'left' },
						  { time :  10.4, pos : 'center' },
						  { time :  12.1, pos : 'left' },
						  { time :  12.7, pos : 'center' },
						  { time :  14.3, pos : 'left' },
						  { time :  15.1, pos : 'center' },
						  { time :  15.3, pos : 'right' },
						  { time :  15.5, pos : 'center' },
						  { time :  16.1, pos : 'left' },
						  { time :  16.5, pos : 'center' },
						  { time :  16.7, pos : 'left' },
						  { time :  17.1, pos : 'center' },
						  { time :  17.2, pos : 'right' },
						  { time :  17.3, pos : 'center' }
		                 ];
		
		
		var displays = ['cylender','headlights', 'driving-modes','mini-controller','heads-up-display','rear-view-cam','park-assist','8.8Nav','connected'];
	    var playing = true;
		var currentTime = 0;
		var stageVideo = document.getElementById("video"); 
		var images = [];
		 
		var playbackListener;
		var videoEnd = false;
		
		
		// LOAD ALL INITICAL IMAGES 
		var loadAllIntroImagesFirst = function () {
			 var imgCount = -1;
			 for ( var i = 0; i < displays.length; i++ ){
				   var img = '<img src="img/enter-page/heads-up/'+displays[i]+'.png"/>';
				   $(img).load(function () {
					   imgCount++;
					   if( imgCount == displays.length -1 ){
						 init();  
					   }
				   });
			 }
			
		}
		
		
		var init = function () {
			
			
			stageVideo.pause(); 
			frameDelay = 1750; 
			
			/*window.addEventListener('load', function() {
            var video = document.querySelector('#video');
            var preloader = document.querySelector('.loader');
            var num = 0;
            function checkLoad() {
	
            if (stageVideo.readyState === 4) {
            startLoader();
            } else {
            setTimeout(checkLoad, 10);
            }
			
			
            }

            checkLoad();
            }, false);*/
			
			 startLoader();
		 	
		     $("#video").bind("ended", function() { clearTimeout(playbackListener);videoEnd = true; });
		
		///startLoader();	
			
		for(var iii = 0; iii < 100; iii++){
			var t =  0.10 * iii;
			 cues.push({t:''});
		}
			
		var screenWidth = $('body').css( 'width' );
		  $('.header-slogan').on('click',function () {window.location = 'f56.html'});
		};
		
		
		var num = 0;
		var starter;
		var startLoader = function () {
			num++;
			
		   if( num == 100 ){ go(); return; }
			
           var m = num * 2;
		   $('.loading-copy').html('chargement '+ num + '%');
          
		   if( num <= 100 ){
			   starter = setTimeout(function () { startLoader(); },30);
		   }
			
		}
		
		
		
		var go = function () {
			
		  $('.loader').hide();	
		  $('#player').fadeIn( );	
		  $("#video-loader").hide();
          $("#wheel").fadeIn();
		  $("#heads-up").fadeIn();
		  $("#dash").fadeIn();
		  $(".steering-wheel").fadeIn();
		  $('#current-time').fadeOut();
		  $('#loader-holder').fadeOut();
		  cuepoint.init(cues);
		  cuepoint.play();	
		 
		  clearTimeout(playbackListener );
		  playbackListener = setTimeout(function () { drawFrame(); }, 100);
		 
		 setHeadsUpPlay();
		 speedupMeter();
		
		}
		
		
		
		var drawFrame = function () {
			clearTimeout(playbackListener);
			stageVideo.play();
			//console.log( "RUNNGING " );
			if(videoEnd == false){
			playbackListener = setTimeout(function () { drawFrame(); }, 10);
			}else{
			clearTimeout(playbackListener);
			}
		}
		
	
		var headsUpTimer;
		var count = -1;
		var setHeadsUpPlay = function () {
			clearTimeout(headsUpTimer);
			count++;
			
			if( count == 9 ){
			$("#new-features").fadeOut();
			reply();
			}
			
			var ranInt = '?v='+ Math.random()*99+ '_'+ Math.random()*99
			var img = '<img src="img/enter-page/heads-up/'+displays[count]+'.png'+ranInt+'"/>';
			 
			$(img).load(function () {
				$("#option").animate({'top':'200px'},{duration:300, easing:'easeInOutElastic',complete : function (){
					$("#option").html(img);
					$("#option").animate({'top':'20px'},{duration:500, easing:'easeInOutElastic'});
					
					if( count <= 9 ){  
					headsUpTimer = setTimeout(function () {setHeadsUpPlay();}, frameDelay);
					}
					
				}});
			});
		}
		
		
		var reply = function () {
			
			$("#option").css({'cursor':'pointer', 'z-index':'999'});
			$("#option").fadeOut();
			$("#play-again").fadeIn();
			$("#play-again").off();
			
			var n = '<img src="img/enter-page/speed-meter/'+1+'.png"/>';
			$("#meter").html( n );
			
			$("#reply-1").off();
			$("#reply-1").on('click',function () {
				$("#bookTestDrive").modal({ show: true });
			});
			
			
			
			
			$("#reply-2").off();
			$("#reply-2").on('click',function () {
				 count = -1;
				 $("#new-features").fadeIn();
				 $("#option").fadeIn();
				 $("#play-again").hide();
				 videoEnd = false;
				 playbackListener = setTimeout(function () { drawFrame(); }, 100);
				 setHeadsUpPlay();
				 c = 0;
				 speedupMeter();
			});
			
			$("#reply-3").off();
			$("#reply-3").on('click',function () {
				 window.location = 'f56.html'
			});
			
			
			
			
		}
		
		
		
		// return an array of current points 
		var getCueTime = function (num) {
			var t = false;
			var arr = [];
			for( var i = 0; i < wheelMoves.length; i++ ){
				if( num == wheelMoves[i].time ){
					t = true; 
					arr.push( { b:t, m : wheelMoves[i].pos } );
					}
				
			   }
			return arr;
		}
		
		
		// set cue point data 
		var previous;
		var setCuePoint = function (time,method) {
			
			var q;
			var r = Math.round(10*time)/10;
			//$('#current-time').html(r);
			//$('#current-time').show();
			//console.log(  getCueTime(r) + " :: " + time + " ROUNDED TO TENTH " + r  );
			if( typeof getCueTime(r)[0] != 'undefined'  ){
				
				if(typeof getCueTime(r)[0].m != 'undefined'){
					method = getCueTime(r)[0].m;
				}
			}
			
			if(method == 'left' ){
				$("#center-wheel").rotate({
					/*duration: 1,*/
                animateTo:-20
                 });
			}
			
			if(method == 'center' ){
				$("#center-wheel").rotate({
						/*duration: 1,*/
                animateTo:0 
                });
			}
			if(method == 'right' ){
				$("#center-wheel").rotate({
						/*duration: 1,*/
                animateTo:20
                });
			}
			clearTimeout(q);
			q = setTimeout(function () { stageVideo.pause();
				                     playing = false},100);
		};
		
	
		var speed;
		var c = 0;
		var speedupMeter = function () {
			c++;
			if( c < 8 ){			
			speed = setTimeout(speedupMeter,100);
			var n = '<img src="img/enter-page/speed-meter/'+c+'.png"/>';
			$("#meter").html( n );
			}
			
		}
		
	   
		return { setCuePoint    : setCuePoint, 
		         go             : go,
				 setHeadsUpPlay : setHeadsUpPlay
		
		       }
		
		
    })();

	return { Model : Model };
	
}(jQuery, window._f56 = window._f56 || {}, window, document));