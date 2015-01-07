    /*
 #############################

 DYO

 - http://stackoverflow.com/questions/18291838/integrating-fabricjs-and-angularjs
 - http://stackoverflow.com/questions/18685453/angular-js-and-fabric-js-fabric-canvas-changes-behavior-once-code-is-moved-to-a
 - http://codereview.stackexchange.com/questions/31444/first-angularjs-directive
 - https://github.com/kangax/fabric.js/issues/553

 - https://github.com/kangax/fabric.js/wiki/FAQ

 #############################
 */

function Dyo($scope, $rootScope, $timeout, Cache, $location, $upload, $window, $http, Files, $routeParams) {
  var cachedClipart = Cache.get('clipart'), cachedColors = Cache.get('colors'), cachedFonts = Cache.get('fonts');
  $scope.clipart = cachedClipart ? cachedClipart: [];
  $scope.colors = cachedColors ? cachedColors : [];
  $scope.fonts = cachedFonts ? cachedFonts: [];
            // Design Enhancement Phase 2: prompt user to sign in
            var apiDomain = "http://tassel-topper-api.herokuapp.com";
            var user = Cache.get("user");
         /*   if(!user) {
                var msg = 'Do you want to login to save your design to the cloud?';
                msg += '<nav class="alertify-buttons">' +
                        '<button class="alertify-button alertify-button-primary" id="alertify-primary" onclick="window.location.href = \'/#!/signup\';window.location.reload();">Sign up</button>' +
                        '</nav>';
                if (typeof device !== 'undefined') {
                    navigator.notification.confirm(
                        msg,
                        function(btn) {
                            if (btn===2) {
                                $rootScope.returnUrl='design-your-own';
                                $location.path('/login');
                                if(!$scope.$$phase) $scope.$apply();
                            }

                            if (btn ===1){
                                $rootScope.returnUrl='design-your-own';
                                $location.path('/home');
                                if(!$scope.$$phase) $scope.$apply();
                            }
                        },
                        'Confirm', // CONTACTTITLE
                        ['Not right now','Login'] // buttonLabels => 1, 2
                    );
                } else {
                    alertify.set({ labels: {
                        ok     : "Login",
                        cancel : "Not right now"
                    } });
                    alertify.confirm(msg, function (e) {
                        if (e) {
                            $rootScope.returnUrl='design-your-own';
                            $location.path('/login');
                            if(!$scope.$$phase) $scope.$apply();
                        }
                        else{
                            $rootScope.returnUrl='design-your-own';
                            $location.path('/home');
                            if(!$scope.$$phase) $scope.$apply();
                        }
                    });
                }
            }*/

    $scope.dim = false;

    var designId = $routeParams.id;
    // Method load current design
    if(designId){
        $rootScope.miniAlerts({msg: 'Loading...'});
        $scope.getDesignById = function (id){
            api.designs.get({id: id}, function (res, err){
                $scope.$apply(function(){

                    Cache.set('history', []);
                    Cache.set('design', JSON.stringify(res));
                    Cache.set('first-item-saved', true);
                    
                    // Reset back ground for previous design
                    if(res.jsondata.background == "")
                        res.jsondata.background = "#FFFFFF";

                    $scope.canvas.clear();

                    $scope.progressShow=true;
                    $scope.canvas.loadFromJSON(res.jsondata,function(){
                        $scope.loadCanvas();
                        $scope.progressShow=false;
                        var historyArray = [];
                        historyArray.push(JSON.stringify(res.jsondata));
                        Cache.set('history', historyArray);
                    });
                });
            });
        }
        $scope.getDesignById(designId);
    }else{
        var keepHistory = Cache.get('keep-history');
        if(!keepHistory){
            Cache.set('history', []);
            Cache.set('design', {});
        }
        Cache.set('first-item-saved', false);
    }

  $rootScope.viewport = ', user-scalable=no';
  
    // Task for save last changes
    $scope.checkChanges = setInterval(function () {
        
        // Just verify if an object it is selected
        if ($scope.canvas._activeObject != null) {

            var tmp = JSON.stringify($scope.canvas);

            // And the object has diferent data and is not moving
            if ($scope.canvas._activeObject.isMoving == false && $scope.history[$scope.step] != tmp) {

                $scope.saveChanges();
            }
        }
    }, 1000);

    $scope.selectedObject = null;
    $scope.filterFont = '';
    $scope.filterColor = '';
    $scope.filterClipart = '';
    $scope.color = Cache.get('color') ? Cache.get('color') : '000000';
    $scope.font = Cache.get('font') ? Cache.get('font') : 'Arial';

    // Steps and History defaults
    $scope.history_limit = 5;
    $scope.history = Cache.get('history') ? Cache.get('history') : [];
    $scope.history = ($scope.history.length > 5) ? [] : $scope.history;
    $scope.step = ($scope.history.length-1 > 0) ? $scope.history.length-1 : 0;
    console.log('History: '+$scope.history.length+', Step: '+$scope.step);
    
    $scope.tempSize = 0;
    $scope.canvasx=0;
    $scope.design = Cache.get('design') ? Cache.get('design') : null;
    $scope.obj = {
        scale:0,
        angle:0,
        progress:0
    }
  
    /*

     getColors
     - Get all colors from the remote API

     */
    $scope.getColors = function() {
        if (typeof device === 'undefined') {
            api.colors.get({active: true, $limit: 200, $sort: {group: 1}}, function (colors, err) {
                if (err) DEBUG && console.log(err);
                else {
                    $scope.colors = colors;
                    Cache.set('colors', colors);
                    if (!$scope.$$phase) $scope.$apply();
                }
            })
        } else {
            console.log('$http get Colors');
            $http.get('data/colors.json')
                .success(function(data, status, headers, config) {
                    $scope.colors = data;
                    if(!$scope.$$phase) $scope.$apply();
                })
                .error(function(data, status, headers, config) {
                    DEBUG && console.log(data);
                });
        }
    };
    $scope.getColors();

    /*

     getFonts
     - Get all fonts from the remote API

     */
    $scope.getFonts = function() {
        if (typeof device === 'undefined') {
            api.fonts.get({active: true, $limit: 200, $sort: {name: 1}}, function(fonts, err){
                if(err) DEBUG && console.log(err);
                else {
                    $scope.fonts = fonts;
                    Cache.set('fonts', fonts);
                    if(!$scope.$$phase) $scope.$apply();
                }
            })
        } else {
            console.log('$http get Fonts');
            $http.get('data/fonts.json')
                .success(function(data, status, headers, config) {
                    $scope.fonts = data;
                    if(!$scope.$$phase) $scope.$apply();
                })
                .error(function(data, status, headers, config) {
                    DEBUG && console.log(data);
                });
        }
    };
    $scope.getFonts();


    /*

     getClipart
     - Get all clipart from the remote API

     */
    $scope.getClipart = function() {
        if (typeof device === 'undefined') {
            api.clipart.get({active: true, $limit: 1000, $sort: {group: 1}}, function(clipart, err){
                if(err) DEBUG && console.log(err);
                else {
                    //DEBUG && console.log(clipart);
                    $scope.clipart = clipart;
                    Cache.set('clipart', clipart);
                    if(!$scope.$$phase) $scope.$apply();
                }
            })
        } else {
            console.log('$http get Clipart');
            $http.get('data/clipart.json')
                .success(function(data, status, headers, config) {
                    $scope.clipart = data;
                    if(!$scope.$$phase) $scope.$apply();
                })
                .error(function(data, status, headers, config) {
                    DEBUG && console.log(data);
                });
        }

    };
    $scope.getClipart();

    /*
     share
     - http://stackoverflow.com/questions/11206955/saving-canvas-as-a-png-or-jpg

     */
    $scope.share=function(){
        $scope.progressShow=true;
        $scope.save(function(design){
            $scope.progressShow=false;
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
            if(!design)return;
            var link=$scope.design.preview;
            if(typeof device !== 'undefined') {

                window.plugins.socialsharing.share(
                    'Tassel Toppers - The Professional Way to Decorate Your Grad Cap', // Message
                    'Tassel Toppers - The Professional Way to Decorate Your Grad Cap', // Subject
                    $scope.design.preview, // Image
                    'http://tasseltoppers.com' // Link
                );

            } else {
                /*
                 var params = {
                 method: 'feed',
                 name: 'Tassel Toppers',
                 link: 'http://tasseltoppers.com',
                 picture: link.file,
                 caption: 'Check out Tassel Toppers!',
                 description: 'Tassel Toppers - The Professional Way to Decorate Your Grad Cap'
                 };

                 try {
                 FB.ui(params, function(fb) {
                 DEBUG && console.log(fb);
                 });
                 } catch (e) {
                 DEBUG && console.log(e);
                 }https://www.facebook.com/dialog/feed?app_id=108361862612466&display=popup&caption=An%20example%20caption&picture=http://dyo.tasseltoppers.com/1e75e3a1-3f4e-406c-a839-af142ad34bb9.png&link=http%3A%2F%2Fwww.tasseltoppers.com%2F%23!%2Fdesign-your-own&redirect_uri=https://www.tasseltoppers.com/#!/design-your-own
                 */
                var caption=encodeURIComponent("Check out this Grad Cap I just created at www.tasseltoppers.com. Now I'm ready to Graduate! #tasseltopper #decoratedgradcap ");
                var urllink=encodeURIComponent("http://beta.tasseltoppers.com/#!/design-your-own?id="+$scope.design.id);
                var baseurl=encodeURIComponent("http://beta.tasseltoppers.com");
                var msg='<h3 class="turq">How do you want to share your design?</h3> ' +
                    '<img src="'+link+'" width="50%"/><br/>' +
                    '<a href="https://www.facebook.com/dialog/feed?app_id=108361862612466&display=popup&' +
                    'caption=' + caption+
                    '&picture=' + link +
                    '&link=' +urllink+
                    '&redirect_uri=' +baseurl+
                    '" target="_blank"><img src="images/icon-facebook_200x200.png" class="sharesocial"/></a>' +
                    '<a href="https://twitter.com/home?status='+caption+urllink+'" target="_blank"><img src="images/icon-twitter_200x200.png" class="sharesocial"/></a>'+
                    '<a href="https://pinterest.com/pin/create/button/?url='+urllink+'&media='+link+'&description='+encodeURIComponent("Decorated Grad Caps by Tassel Toppers")+'" target="_blank"><img src="images/icon-pinterest_200x200.png" class="sharesocial"/></a>';
                //if(isMobile.any())msg+='<a href="instagram://user?username=tasseltoppers" target="_blank"><img src="images/icon-instagram_200x200.png" class="sharesocial"/></a>';
                    msg+='<a href="mailto:?subject=Decorated Grad Caps by Tassel Toppers&body='+caption+urllink+'"><img src="images/icon-email_200x200.png" class="sharesocial"/></a>';
                if(isMobile.any()){
                    var phonelink="sms:?body="+caption+urllink;
                    if(isMobile.iOS())phonelink="sms:;body="+caption+urllink;
                    msg+='<a href="'+phonelink+'" target="_blank"><img src="images/icon-sms_200x200.png" class="sharesocial"/></a>';
                }

                alertify.set({ labels: {
                    ok     : "OK"
                } });
                alertify.alert(msg, function (e) {

                });
            }
        });
    };

    $scope.editText = function(){
        if (!$scope.selectedObject) $scope.addText();
        else {
            if($scope.selectedObject.get('type')=="text" || $scope.selectedObject.get('type')=="CurvedText"){
                $scope.textEdit();
            }
            else{
                $scope.addText();
            }
        }
    };
    $scope.editFont = function(){
        if ($scope.selectedObject){
            $scope.fontsShow = true;
            if(!$scope.$$phase) $scope.$apply();
        }
    };

    $scope.hideModal = function(){
        $scope.colorsShow = false;
        $scope.clipartShow = false;
        $scope.fontsShow = false;
        $scope.photosShow = false;
        $scope.optionsShow = false;
        $scope.textOptionsShow = false;
        if(!$scope.$$phase) $scope.$apply();

    };


    /*
     addText
     - prompts for text

     */
    $scope.addText = function() {

        var msg = 'Please enter your text. <p class="small"><em class="red">Note: Black/Blue text on a Black/Blue background does not print well. We suggest using White text on a dark background to STAND OUT IN A CROWD!</em></p>';
        if (typeof device !== 'undefined') {
            navigator.notification.prompt(
                msg,
                function(res) {
                    if (res.buttonIndex===2) $scope.text(res.input1);
                },
                'Enter Text', // title
                ['Cancel','Add'], // buttonLabels => 1, 2
                '' // default text
            );
        } else {
            alertify.set({ labels: {
                ok     : "Add",
                cancel : "Cancel"
            } });
            alertify.prompt(msg, function (e, str) {
                if (e) $scope.text(str);
            });
        }

    };

    $scope.textEdit = function() {
        var text=$scope.selectedObject.getText();
        var msg = 'Please edit your text.';
        if (typeof device !== 'undefined') {
            navigator.notification.prompt(
                msg,
                function(res) {
                    if (res.buttonIndex===2) $scope.modifyText(res.input1);
                },
                'Edit Text', // title
                ['Cancel','Change'], // buttonLabels => 1, 2
                text // default text
            );
        } else {
            alertify.set({ labels: {
                ok     : "Change",
                cancel : "Cancel"
            } });
            alertify.prompt(msg, function (e, str) {
                if (e) $scope.modifyText(str);
            },text);
        }

    };
    
    /*
     text
     -
     */
    $scope.text = function(type) {
        var text = new fabric.Text(type, {
            left: 100,
            top: 100,
            fontFamily: $scope.font
            //stroke: '#c3bfbf'
        });
        $scope.canvas.add(text);
        text.set({left:$scope.tempSize/2-text.width/2, top:$scope.tempSize/2-text.height/2});
        text.setCoords();
        $scope.canvas.renderTop().renderAll().calcOffset();
        $scope.selectedObject=null;
        $scope.canvas.setActiveObject($scope.canvas.item($scope.canvas.getObjects().length-1));
        if(!$scope.$$phase) $scope.$apply();
    };
    $scope.modifyText = function(type) {
        props = $scope.selectedObject.toObject();
        delete props['text'];
        var textSample = new fabric.Text(type, props);
        $scope.canvas.remove($scope.selectedObject);
        $scope.canvas.add(textSample).renderAll();
        $scope.selectedObject=null;
        $scope.canvas.setActiveObject($scope.canvas.item($scope.canvas.getObjects().length-1));
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };

    /**
     * Change Text align
     */
    $scope.alignText = function(side) {

        var props = $scope.selectedObject.toObject();
        console.log(props);
        // $scope.selectedObject;
        // $scope.canvas.renderAll();
        // {
        // }
        // alert(side);

        props = $scope.selectedObject.toObject();
        var text = props['text'];
        delete props['text'];
        props.textAlign = side;
        var textSample = new fabric.Text(text, props);
        $scope.canvas.remove($scope.selectedObject);
        $scope.canvas.add(textSample).renderAll();
        $scope.selectedObject=null;
        $scope.canvas.setActiveObject($scope.canvas.item($scope.canvas.getObjects().length-1));
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };

    /*
     changeFont
     - Changes the font of selected item and future selections

     */
    $scope.changeFont = function(font){
        $scope.fontsShow = false;
        if(font){
            $scope.selectedObject.set("fontFamily", font);
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }
    };

    /*

     changeColor
     re: http://stackoverflow.com/questions/15675856/fabric-js-change-color-fill-stroke-of-imported-svg

     */

    $scope.changeColor = function(color) {
        if (color && !$scope.selectedObject){

            $scope.setBackgroundColor(color);
            $scope.accept();
        } 
        else if (color && $scope.selectedObject) {
            $scope.selectedObject.setFill(color);
            $scope.canvas.renderAll();
        }
    };

    /*

     setBackground Color
     - Will set the background color if no object is selected
     re: http://stackoverflow.com/questions/13173381/need-to-change-the-canvas-background-color-while-using-fabric-js

     */
    $scope.setBackgroundColor = function(color) {
        $scope.canvas.backgroundColor = color;
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };

    /*

     sendBack
     doc: http://fabricjs.com/docs/fabric.StaticCanvas.html
     re: http://stackoverflow.com/questions/6939782/fabric-js-problem-with-drawing-multiple-images-zindex
     - sendToBack()

     */
    $scope.sendBack = function() {
        $scope.selectedObject.sendBackwards();
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };

    $scope.sendForward = function() {
        $scope.selectedObject.bringForward();
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };



    /*

     remove
     - removes an item from the canvas
     re: http://stackoverflow.com/questions/11829786/delete-multiple-objects-at-once-on-a-fabric-js-canvas-in-html5

     */
    $scope.remove = function() {
        $scope.canvas.remove($scope.selectedObject);
        $scope.deselect();
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };


    /*

     undo
     - goes back in history one step

     */
    $scope.undo = function(){

        if ($scope.step <= 0 && !$rootScope.minialert) {

            $rootScope.miniAlerts({msg: 'No more history...', time: 1000});
            $scope.step = 0;
        }
        else if ($scope.step !== 0) {

            $scope.canvas.clear();
            $scope.step = $scope.step - 1;
            $scope.canvas.loadFromJSON($scope.history[$scope.step],$scope.canvas.renderAll.bind($scope.canvas));
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }
        console.log('Amount: '+$scope.history.length+', Step: '+$scope.step);
    };


    /*

     redo
     - goes forward in history one step

     */
    $scope.redo = function(){

        if ($scope.step >= $scope.history_limit) {

            $scope.step = $scope.history.length - 1;
            $rootScope.miniAlerts({msg: 'Most recent change...', time: 1000})
        }
        else if ($scope.step === $scope.history.length-1 && !$rootScope.minialert) {

            $rootScope.miniAlerts({msg: 'Most recent change...', time: 1000})
        }
        else if ($scope.step !== $scope.history.length-1) {
            $scope.canvas.clear();
            $scope.step = $scope.step + 1;
            $scope.canvas.loadFromJSON($scope.history[$scope.step],$scope.canvas.renderAll.bind($scope.canvas));
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }
        console.log('History: '+$scope.history.length+', Step: '+$scope.step);
    };

    /**
     * Save last change to canvas
     */
    $scope.saveChanges = function () {

        if (JSON.stringify($scope.canvas) == $scope.history[$scope.step]) return false;

        if ($scope.step < ($scope.history_limit - 1) && $scope.history.length > 0) {

            while ($scope.step != ($scope.history.length - 1)) {

                $scope.history.pop();
            }
        }

        if ($scope.history.length >= $scope.history_limit) $scope.history.remove(0);
        $scope.history.push(JSON.stringify($scope.canvas));
        $scope.step = $scope.history.length-1;
        
        var json=JSON.stringify($scope.history);
        Cache.set('history', json);
    }

    /*

     accept
     - saves changes and pushed to the history.  This will wipe any history if between steps

     */
    $scope.accept = function() {

        try {

            $scope.saveChanges();

            // Remove all next changes from history
            // var step = $scope.step;
            // var curElement = document.activeElement;

            // if ($scope.step < 4 && $scope.history.length > 0) {

            //     while ($scope.step != ($scope.history.length - 1)) {

            //         $scope.history.pop();
            //     }
            // }

            // if ($scope.history.length >= 5) $scope.history.remove(0);
            // $scope.history.push(JSON.stringify($scope.canvas));
            // $scope.step = $scope.history.length-1;
            
            // var json=JSON.stringify($scope.history);
            // Cache.set('history', json);
            $scope.deselect();
            // curElement.focus();

            $scope.resetControls();
            if(!$scope.$$phase) $scope.$apply();

        } catch(e) {
            $rootScope.miniAlerts({msg: 'Exceeded Local Storage space', time: 1000});
        }
    };

    /*

     cancel
     - deselects the current layer and reloads the current position in the history

     */
    $scope.cancel = function() {
        $scope.resetControls();
        $scope.canvas.loadFromJSON($scope.history[$scope.step],$scope.canvas.renderAll.bind($scope.canvas));
        $scope.canvas.renderTop().renderAll().calcOffset();
        $scope.deselect();
        if(!$scope.$$phase) $scope.$apply();
    };

    /**
     * Event for click object on canvas
     * 
     */
    $scope.select = function(){

        // if selected it is text 
        var text = document.getElementById('prontText');

        // If the object it is curved text load text controls
        if ($scope.canvas._activeObject.originalObject != undefined) {

            var obj = $scope.canvas._activeObject.originalObject;
        }

        console.log($scope.canvas._activeObject);
        if ($scope.canvas._activeObject && $scope.canvas._activeObject.type == 'text') {

            text.innerHTML = 'Edit Text';
        }
        else {
            
            text.innerHTML = 'Add Text';
        }

        $scope.resetControls();
        $scope.canvas._objects.forEach(function(obj){
            //if ($scope.selectedObject !== obj) obj.selectable = false;
        });
    };

    $scope.deselect = function(){

        // Change Add Text button
        document.getElementById('prontText').innerHTML = 'Add Text';

        $scope.selectedObject = null;
        if(!$scope.$$phase) $scope.$apply();
        $scope.canvas._objects.forEach(function(obj){
            obj.selectable = true;
        });
        $scope.canvas.deactivateAll().renderAll();
        // $scope.canvas.renderAll();
    };

    $scope.save = function(ctx) {
        $scope.accept();
        if(!$rootScope.user) {
            var msg = 'Do you want to login to save your design to the cloud?';
            msg += '<nav class="alertify-buttons">' +
                    '<button class="alertify-button alertify-button-primary" id="alertify-primary" onclick="window.location.href = \'/#!/signup\';window.location.reload();">Sign up</button>' +
                    '</nav>';
            if (typeof device !== 'undefined') {
                navigator.notification.confirm(
                    msg,
                    function(btn) {
                        if (btn===2) {
                            // Bug#15: keep history when relogin.
                            Cache.set('keep-history', true);
                            $rootScope.returnUrl='design-your-own';
                            $location.path('/login');
                            if(!$scope.$$phase) $scope.$apply();
                        }
                    },
                    'Confirm', // CONTACTTITLE
                    ['Not right now','Login'] // buttonLabels => 1, 2
            );
            } else {
                alertify.set({ labels: {
                    ok     : "Login",
                    cancel : "Not right now"
                } });
                alertify.confirm(msg, function (e) {
                    if (e) {
                        // Bug#15: keep history when relogin.
                        Cache.set('keep-history', true);
                        $rootScope.returnUrl='design-your-own';
                        $location.path('/login');
                        if(!$scope.$$phase) $scope.$apply();
                    }
                    else{
                        if(ctx)ctx(null);
                    }
                });
            }
        }else{
            var firstSave = Cache.get("first-item-saved");
            if(firstSave && ctx == null){
                $scope.alertifyNewSave();
            }else{
                $scope.saveDesign(ctx);
            }
        }
    };
    
    // Save method
    // ** This method will excute to api
    $scope.saveDesign = function (ctx) {
        $scope.dim = true;
        $scope.accept();
        $rootScope.miniAlerts({msg: 'Saving...'});
                var l=$scope.tempSize;
                var h=Math.sqrt(l*l/2);
                var n=840;
                var delta=-n/2+l/2;
                var scale=n/h;

                var rect = new fabric.Rect({
                    left: -2000,
                    top: -2000,
                    fill:'red',
                    width: l+4000,
                    height: l+4000
                });

                $scope.saving=true;
                $scope.canvas.add(rect);

                var objs = $scope.canvas.getObjects().map(function(o) {
                    return o.set('active', true);
                });
                var group = new fabric.Group(objs, {
                    originX: 'center',
                    originY: 'center'
                });

                $scope.canvas._activeObject = null;

                group.setAngle(-45);
                group.setScaleX(scale);
                group.setScaleY(scale);
                group.left-=delta;
                group.top-=delta;

                $scope.canvas.setActiveGroup(group.setCoords()).renderAll();
                $scope.deselect();
                $scope.canvas.remove(rect);
                var pw=$scope.canvas.width;
                var ph=$scope.canvas.height;
                $scope.canvas.setWidth(n);
                $scope.canvas.setHeight(n);
                $scope.canvas.calcOffset();

                $scope.canvas.clipTo = function (ctx) {
                    var clipTo = new fabric.Rect({ left: 0, top:0, width: n, height: n, rx: 0, ry: 0, angle: 0, fill: '#FFFFFF', strokeWidth: 1, stroke: 'rgba(0,0,0,0.2)'});
                    clipTo.render(ctx);
                }

                $scope.canvas.renderTop().renderAll().calcOffset();
                if(!$scope.$$phase) $scope.$apply();


                var data=$scope.canvas.toSVG();
                var jsondata=$scope.canvas.toJSON();
                var datauri = $scope.canvas.toDataURL('png');


                $scope.canvas.renderTop().renderAll().calcOffset();
                $scope.canvas.setWidth(pw);
                $scope.canvas.setHeight(ph);
                $scope.canvas.renderTop().renderAll().calcOffset();
                $scope.loadCanvas();
                $scope.saving=false;

                api.design.post({data: data, jsondata: JSON.stringify(jsondata), png: datauri, user: {id:user.id, username: user.username}}, function(res, error) {
                  if (error) DEBUG && console.log("error");
                  else {
                    // Handle preview url
                    var datauri = $scope.canvas.toDataURL("png");
                    api.design.post({id: res.id, preview: datauri, emailSend: true}, function (res, err) {
                        $scope.$apply(function(){
                            $scope.dim = false;
                        });
                        $scope.design = res;
                        Cache.set('design', $scope.design);
                             // Process cart
                        // $scope.processCart4FirstSave($scope.design);

                        if(ctx)ctx(res);
                    });

                    Cache.set("first-item-saved", true);    

                  }
                });

                $scope.canvas.clipTo = function (ctx) {
                    $scope.clipp(ctx);
                };
    }

    // Update method
    // ** This method will excute to api
    $scope.updateDesign = function (id) {
        $scope.dim = true;
        $scope.accept();
        $rootScope.miniAlerts({msg: 'Modifying...'});

        var l=$scope.tempSize;
        var h=Math.sqrt(l*l/2);
        var n=840;
        var delta=-n/2+l/2;
        var scale=n/h;

        var rect = new fabric.Rect({
                    left: -2000,
                    top: -2000,
                    fill:'red',
                    width: l+4000,
                    height: l+4000
                });

        $scope.saving=true;
        $scope.canvas.add(rect);

        var objs = $scope.canvas.getObjects().map(function(o) {
            return o.set('active', true);
        });
        var group = new fabric.Group(objs, {
                    originX: 'center',
                    originY: 'center'
                });

        $scope.canvas._activeObject = null;

        group.setAngle(-45);
        group.setScaleX(scale);
        group.setScaleY(scale);
        group.left-=delta;
        group.top-=delta;

        $scope.canvas.setActiveGroup(group.setCoords()).renderAll();
        $scope.deselect();
        $scope.canvas.remove(rect);
        var pw=$scope.canvas.width;
        var ph=$scope.canvas.height;
        $scope.canvas.setWidth(n);
        $scope.canvas.setHeight(n);
        $scope.canvas.calcOffset();

        $scope.canvas.clipTo = function (ctx) {
            var clipTo = new fabric.Rect({ left: 0, top:0, width: n, height: n, rx: 0, ry: 0, angle: 0, fill: '#FFFFFF', strokeWidth: 1, stroke: 'rgba(0,0,0,0.2)'});
            clipTo.render(ctx);
        }

        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();

        var data=$scope.canvas.toSVG();
        var jsondata=$scope.canvas.toJSON();
        var datauri = $scope.canvas.toDataURL('png');

        $scope.canvas.renderTop().renderAll().calcOffset();
        $scope.canvas.setWidth(pw);
        $scope.canvas.setHeight(ph);
        $scope.canvas.renderTop().renderAll().calcOffset();
        $scope.loadCanvas();
        $scope.saving=false;

        api.design.post({id:id, data: data, jsondata: jsondata, png: datauri}, function (res, err) {
            if (err) console.log(err);
            else {
                DEBUG && console.log(res);
                $scope.design=res;
                Cache.set('design', $scope.design);

                // Handle preview url
                var datauri = $scope.canvas.toDataURL("png");
                api.design.post({id: id, preview: datauri, emailSend: false}, function (res, err) {
                    $scope.$apply(function(){
                        $scope.dim = false;
                    });
                    $scope.design=res;
                    Cache.set('design', $scope.design);
                });

            }
        });

        $scope.canvas.clipTo = function (ctx) {
            $scope.clipp(ctx);
        };
    }

    // Alertify 
    // ** This method handle Save/New file after save an design.
    $scope.alertifyNewSave = function () {
        var dId = designId === undefined ? $scope.design['id'] : designId;
        var msg = 'Save Options <br/> <p class="small"><em class="red">Note:  All saved designs are located under View Account: Designs</em></p>';
            if (typeof device !== 'undefined') {
                navigator.notification.confirm(
                    msg,
                    function(btn) {
                        if (btn===2) {
                            Cache.set("first-item-saved", null);
                            $scope.saveDesign();
                        }

                        if (btn===1) {
                            $scope.updateDesign(dId);
                        }
                    },
                    'Confirm', // CONTACTTITLE
                    ['Overwrite Existing','Save as New'] // buttonLabels => 1, 2
                );
            } else {
                alertify.set({ labels: {
                    ok     : "Save as New",
                    cancel : "Overwrite Existing"
                } });
                alertify.confirm(msg, function (e) {
                    if (e) {
                        Cache.set("first-item-saved", null);
                        $scope.saveDesign();
                    }else{
                        // Update design file
                        $scope.updateDesign(dId);
                    }
                });
            }
    }

    $scope.addToCart = function() {
        $rootScope.wasInCart=true;
        $scope.save(function(design){
            $scope.processCart($scope.design);
        });

    };


    // Process cart for first save
    $scope.processCart4FirstSave=function(design){
        $rootScope.cart = Cache.get('cart') ? Cache.get('cart') : [];
        var length = $rootScope.cart.length;
        var product={};
        product.active= true;
        product.description="Design your Own";
        product.group="D";
        product.images=[design.preview];
        product.name="Design Your Own";
        product.price=15;
        product.tags= [
            "DYO"
        ];
        product.url="design-your-own";
        product.id=design.id;
        product.qty = 1;
        var found = false;
        for (var i = 0; i < length; i++) {
            if ($rootScope.cart[i].id === design.id) {
                console.log('FOUND');
                $rootScope.cart[i]=product;
                found = true;
            }
        }
        if (!found) {
            $rootScope.cart.push(product);
        }
        // Save local and to API
        Cache.set('cart', $rootScope.cart);
        api.cart.post({cart: $rootScope.cart}, function(res, err) {
            if(err) console.log(err);
            else console.log(res);
        });
    };

    // Process cart 
    $scope.processCart=function(design){
        $rootScope.cart = Cache.get('cart') ? Cache.get('cart') : [];
        var length = $rootScope.cart.length;
        var product={};
        product.active= true;
        product.description="Design your Own";
        product.group="D";
        product.images=[design.preview];
        product.name="Design Your Own";
        product.price=15;
        product.tags= [
            "DYO"
        ];
        product.url="design-your-own";
        product.id=design.id;
        product.qty = 1;
        var found = false;
        for (var i = 0; i < length; i++) {
            if ($rootScope.cart[i].id === design.id) {
                console.log('FOUND');
                $rootScope.cart[i]=product;
                found = true;
            }
        }
        if (!found) {
            $rootScope.cart.push(product);
        }


        // Save local and to API
        Cache.set('cart', $rootScope.cart);
        api.cart.post({cart: $rootScope.cart}, function(res, err) {
            if(err) console.log(err);
            else console.log(res);
        });

        $rootScope.changeView('/cart', 'slideLeft');
        if(!$scope.$$phase) $scope.$apply();
    };


    $scope.clear = function() {
        var msg = 'You sure you want to clear the canvas and history?';
        if (typeof device !== 'undefined') {
            navigator.notification.confirm(
                msg,
                function(btn) {
                    if (btn===2) clear();
                },
                'Confirm', // CONTACTTITLE
                ['Cancel','Complete'] // buttonLabels => 1, 2
            );
        } else {
            alertify.set({ labels: {
                ok     : "Complete",
                cancel : "Cancel"
            } });
            alertify.confirm(msg, function (e) {
                if (e) clear();
            });
        }

        function clear(){
            $scope.history = [];
            $scope.step = 0;
            $scope.canvas.clear();
            $scope.setBackgroundColor('white');
            $scope.design=null;
            Cache.set('design', $scope.design);
            Cache.set('history', $scope.history);
            if(!$scope.$$phase) $scope.$apply();
        }
    };

    $scope.resetControls = function() {
        $scope.galleryOn = false;
        $scope.optionsShow = false;
        $scope.textOptionsShow = false;
        $scope.fontsShow = false;
        $scope.colorsShow = false;
        $scope.filterFont = '';
        $scope.filterColor = '';
        $scope.filterClipart = '';
        if(!$scope.$$phase) $scope.$apply();
    };

    $scope.resetClipArtScroll = function() {
      $timeout(function() {
        $scope.$parent.myScroll['clipart-iscroll'].refresh();    
      }, 2000);
    };

    $scope.photoLibrary = function() {

        if (typeof device !== 'undefined') {
            navigator.camera.getPicture(
                function(mediaFile) {
                    DEBUG && console.log(JSON.stringify(mediaFile));
                    Files.put(mediaFile, 'files', function(file){
                        DEBUG && console.log(JSON.stringify(file));
                        $scope.addImage(file);
                        if(!$scope.$$phase) $scope.$apply();
                    });

                }, function(err) {
                    DEBUG && console.log(JSON.stringify(err));
                },
                {destinationType: Camera.DestinationType.FILE_URI, sourceType: Camera.PictureSourceType.PHOTOLIBRARY, mediaType: Camera.MediaType.PICTURE, encodingType: Camera.EncodingType.JPEG, quality: 50, targetWidth: 1024, targetHeight: 1024}
            );
        } else {
            alertify.set({ labels: {
               ok     : "Accept",
               cancel : "Cancel"
           } });
            //http://stackoverflow.com/questions/17922557/angularjs-how-to-check-for-changes-in-file-input-fields
            alertify.confirm('<p class="center"><input class="uploadImage" type="file" id="uploadImage" /></p><p class="small">NOTE: We only accept JPG, GIF and PNG files smaller than 2MB</p>', function (e) {
                if (e) {
                  // user clicked "ok"
                  $scope.onFileSelect(document.getElementById('uploadImage').files);
                }
            });
            if(!$scope.$$phase) $scope.$apply();
        }
    };

    /*

     IMAGE UPLOAD

     */
    $scope.onFileSelect = function($files) {

        $scope.photosShow = false;
        // $files: an array of files selected, each file has name, size, and type.
        for (var i = 0; i < $files.length; i++) {

            var file = $files[i];

            // If the file weighs over 2 meg
            if (file.size > 2097152)

              // Show error
              $scope.showUploadError();
                
            // If the file weighs less than 2 megs
            else{
                $scope.progressShow=true;
                $scope.upload = $upload.upload({
                    url: window.apiroot+'/files',
                    withCredentials: true,
                    file: file
                }).progress(function(evt) {
                        var p=parseInt(100.0 * evt.loaded / evt.total);
                        $scope.obj.progress=p;
                    console.log('percent: ' + p);
                }).error(function(err) {
                    console.log(err);
                }).success(function(data, status, headers, config) {
                    console.log(data);
                        $scope.addImage(data.name);
                        $scope.progressShow=false;
                        $scope.obj.progress=0;
                });
            }

        }
    };

    /*
      Mike Yañez

     Show error message for upload image
     - prompts for text

     */
    $scope.showUploadError = function() {

        var msg = 'The file you are trying to upload is too large. The max. file size to upload is 2 MB. Please try again';
        if (typeof device !== 'undefined') {
            navigator.notification.prompt(
                msg,
                function(res) {
                    var ocho = res;
                },
                'Enter Text', // title
                ['Cancel'], // buttonLabels => 1, 2
                '' // default text
            );
        } else {
            alertify.set({ labels: {
                ok     : "Try Again",
            } });
            alertify.alert(msg, $scope.photoLibrary);
        }

    };

    fabric.Image.fromURL=function(d,f,e){
        var c=fabric.document.createElement("img");
        c.onload=function(){
            if(f){f(new fabric.Image(c,e))}
            c=c.onload=null
        };
        c.setAttribute('crossOrigin','anonymous');
        c.src=d;
    };
    $scope.addImage = function(image) {
        if (image) {
            console.log('Adding image: '+image);
            fabric.Image.fromURL(image, function(img) {
                img.set({
                    left: 150,
                    top: 150
                });
                $scope.canvas.add(img);
                var h=img.width;
                if(img.height>h)h=img.height;
                var scale=$scope.tempSize/h - 0.2;
                var div=2;
                if(scale<1){
                    img.scale(scale);
                    div=div/scale;
                }

                img.set({left:$scope.tempSize/2-img.width/div, top:$scope.tempSize/2-img.height/div});
                img.setCoords();
                $scope.canvas.renderTop().renderAll().calcOffset();
                if(!$scope.$$phase) $scope.$apply();
            });
        }
    };

    /*

     SVG
     - re: http://stackoverflow.com/questions/15303989/fabricjs-loading-displaying-svg

     */
    $scope.loadSVG = function(svg) {

        $scope.clipartShow = false;
        if(!$scope.$$phase) $scope.$apply();

        var group = [];
        var url = '//s3.amazonaws.com/dyo.tasseltoppers.com/cliparts/';
        var ext = '.svg';

        // Google Chrome hack, adding get parameter to force reading headers from
        // remote server. Needed to load cliparts from a consistent URL, and a
        // URL that supports both HTTP and HTTPS protocol.
        fabric.loadSVGFromURL(url+svg+ext+'?r='+Math.random(), function (objects, options) {
            var obj = fabric.util.groupSVGElements(objects, options);
            $scope.canvas.add(obj).centerObject(obj).renderAll();

            obj.set({
                left: 100,
                top: 100
            });
            obj.scale(0.5);
            obj.set({left:$scope.tempSize/2-obj.width/4, top:$scope.tempSize/2-obj.height/4});
            obj.setCoords();
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        },function(item, object) {
            object.set('id',item.getAttribute('id'));
            if(!$scope.$$phase) $scope.$apply();
        });

    };

    /*

     load
     - Getthe design from the remote API

     */
    $scope.getDesign = function() {
        api.designs.get({active: true, $sort: {created: -1}}, function(designs, err){
            if(err) DEBUG && console.log(err);
            else if(designs.length>0){
                var design=designs[0];
                if(!$scope.design){
                    $scope.design=design;
                    Cache.set('design', $scope.design);
                }
                $scope.canvas.clear();
                $scope.progressShow=true;
                $scope.canvas.loadFromJSON(design.jsondata,function(){
                    $scope.loadCanvas();
                    $scope.progressShow=false;
                });

            }
        });
    };
    $scope.getDesignId = function(designid) {
        $scope.progressShow=true;
        api.designs.get(designid, function(design, err){
            $scope.progressShow=false;
            if(err) DEBUG && console.log(err);
            else if(design){
                $scope.canvas.clear();
                $scope.progressShow=true;
                $scope.canvas.loadFromJSON(design.jsondata,function(){
                    $scope.loadCanvas();
                    $scope.progressShow=false;
                });

            }
        });
    };
    $scope.loadCanvas=function(){
        var l2=$scope.tempSize;
        var h2=Math.sqrt(l2*l2/2);
        var n2=840;
        var delta2=0;//Math.sqrt(h2*h2-$scope.canvasx*$scope.canvasx);//-n2/2+l2/2;
        var scale2=h2/n2;
        var rect2 = new fabric.Rect({
            left: -2000,
            top: -2000,
            fill:'red',
            width: 4000,
            height: 4000
        });
        $scope.saving=true;
        $scope.canvas.add(rect2);

        var objs2 = $scope.canvas.getObjects().map(function(o) {
            return o.set('active', true);
        });
        var group2 = new fabric.Group(objs2, {
            originX: 'center',
            originY: 'center'
        });

        $scope.canvas._activeObject = null;

        group2.setAngle(45);
        group2.setScaleX(scale2);
        group2.setScaleY(scale2);
        group2.left+=$scope.canvasx;
        group2.top+=delta2;

        $scope.canvas.setActiveGroup(group2.setCoords()).renderAll();
        $scope.deselect();
        $scope.canvas.remove(rect2);

        $scope.canvas.clipTo = function (ctx) {
            var clipTo = new fabric.Rect({ left: $scope.canvasx, top:0, width: h2, height: h2, rx: h2/20, ry: h2/20, angle: 45, fill: '#FFFFFF', strokeWidth: 1, stroke: 'rgba(0,0,0,0.2)'});
            clipTo.render(ctx);

        }
        $scope.saving=false;
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
        $scope.refreshCanvas();
        $scope.canvas.clipTo = function (ctx) {
            $scope.clipp(ctx);
        };
    }
    $scope.resizeObjects=function(scale){
        $scope.canvas.forEachObject(function(obj){
            obj.left=obj.left*scale;
            obj.scaleX=obj.scaleX*scale;
            obj.top=obj.top*scale;
            obj.scaleY=obj.scaleY*scale;
            obj.setCoords();
            if(!$scope.$$phase) $scope.$apply();
        });
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();
    };

    $scope.refreshCanvas=function(){
        // Make sure objects are selectable on reload
        $timeout(function(){
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }, 400);
        $timeout(function(){
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }, 500);
        $timeout(function(){
            $scope.canvas.renderTop().renderAll().calcOffset();
            if(!$scope.$$phase) $scope.$apply();
        }, 700);
    };
    $scope.alignCenter=function(){
        if($scope.selectedObject){
            $scope.selectedObject.centerH();

        }
        $scope.canvas.renderAll();
    };
    $scope.alignMiddle=function(){
        if($scope.selectedObject){
            $scope.selectedObject.centerV();
        }
        $scope.canvas.renderAll();
    };
    $scope.$watch('obj.scale', function (newValue, oldValue) {
        console.log('oldValue=' + oldValue);
        console.log('newValue=' + newValue);
        if (newValue == null) return false;
        if($scope.selectedObject && !isNaN(newValue)&& newValue!=""){
            $scope.selectedObject.setOriginToCenter();
            $scope.selectedObject.scale(parseFloat(newValue)).setCoords();
            $scope.selectedObject.setCenterToOrigin();

        }
        $scope.canvas.renderTop().renderAll().calcOffset();
    });
    $scope.$watch('obj.angle', function (newValue, oldValue) {

        console.log('oldValue=' + oldValue);
        console.log('newValue=' + newValue);
        if (newValue == null) return false;
        if($scope.selectedObject && !isNaN(newValue) && newValue!=""){
            $scope.selectedObject.setAngle(parseFloat(newValue)).setCoords();
        }
        $scope.canvas.renderTop().renderAll().calcOffset();
    });
    $scope.$watch('obj.curve', function (newValue, oldValue) {

        if($scope.selectedObject && !isNaN(newValue)){

            var angle = parseFloat(newValue);
            var obj = $scope.canvas.getActiveObject();

            if(angle==0){
                var props = {};
                if(obj){
                    if(/CurvedText/.test(obj.type)) {
                        var default_text = obj.getText();
                        props = obj.toObject();
                        delete props['type'];
                        var textSample = new fabric.Text(default_text, props);
                        $scope.canvas.remove(obj);
                        $scope.canvas.add(textSample);
                        $scope.canvas.setActiveObject($scope.canvas.item($scope.canvas.getObjects().length-1));
                        $scope.selectedObject=textSample;
                    }
                }
            }
            else{

                var reverse = (angle < 0) ? 1: 0;

                // if(reverse) {

                //     angle *= 8;
                // }
                // else {

                    angle *= 8;
                // }

                // var radius = Math.abs(angle);
                // var radius=obj.width/2;
                // var spacing = obj.width / obj.text.length;
        
                // var perimeter = 360 * ((obj.text.length * obj.text.length) / Math.abs(angle));
                // var perimeter = ((360 + Math.abs(angle)) / obj.text.length) * Math.abs(angle);
                var perimeter = (360 / Math.abs(angle)) * obj.text.length * obj.text.length;

                var radius = (perimeter / (2 * 3.1416)) * obj.text.length;
                // var spacing = (obj.text.length / Math.abs(angle));
                var spacing = (Math.abs(angle) / obj.text.length) * 2;
                // var spacing = (obj.width / obj.text.length);

                // radius = (360 * 3.1416 * 2 * radius)/ obj.text.length;
                // var spacing = radius;
// return true;
                // if(/text/.test(obj.type)) {
                //     var default_text = obj.getText();
                    // props = obj.toObject();
                    // delete props['type'];
                //     props['textAlign'] = 'center';
                //     props['radius'] = radius;
                //     props['spacing'] = spacing;
                //     props['reverse'] = reverse;
                //     props['align'] = "left";

                //     var textSample = new fabric.CurvedText(default_text, props);
                    // $scope.canvas.remove(obj);
                    // var test = fabric.Text.fromElement(svg, props);
                    // $scope.canvas.add(test1).renderAll();

                    // $scope.canvas.add(test).renderAll();
                    // $scope.canvas.setActiveObject($scope.canvas.item($scope.canvas.getObjects().length-1));
                    // $scope.selectedObject = test1;
                // }
                // else{
                    // obj.set("radius", radius);
                    // obj.set("spacing", spacing);
                    // obj.set("reverse", reverse);
                    // obj.set("align", "left");

                // }
            }
        }
        $scope.canvas.renderTop().renderAll().calcOffset();
    });
    $scope.isMobile=function(){
        return isMobile.any();
    }
    var isMobile = {
        Android: function() {
            return navigator.userAgent.match(/Android/i);
        },
        BlackBerry: function() {
            return navigator.userAgent.match(/BlackBerry/i);
        },
        iOS: function() {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i);
        },
        Opera: function() {
            return navigator.userAgent.match(/Opera Mini/i);
        },
        Windows: function() {
            return navigator.userAgent.match(/IEMobile/i);
        },
        any: function() {
            return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
        }
    };
    $scope.$watch('canvas', function() {
        console.log('CANVAS LOADED');
        // Load from History
        $scope.canvas.clear();
        var designid=$location.search().id;
        if(designid){
           if(!$scope.design || $scope.design && $scope.design.id!=designid){
               alertify.set({ labels: {
                   ok     : "Yes",
                   cancel : "No"
               } });
               alertify.confirm("Do you want to load the design?", function (e) {
                   if (e) {
                       $scope.getDesignId(designid);
                       return;
                   }
               });
           }
        }
        if($scope.history.length > 0) {
            $scope.progressShow=true;
            $scope.canvas.loadFromJSON($scope.history[$scope.step],function(){
                $scope.canvas.renderAll.bind($scope.canvas);
                $scope.refreshCanvas();
                $scope.progressShow=false;
            });
            var h=$window.innerHeight-92-105-20;
            if($window.innerWidth<h)h=$window.innerWidth;
            var scale=h/$scope.tempSize;
            $scope.resizeObjects(scale);
            $scope.refreshCanvas();
            // if($rootScope.wasInCart){
            //     $scope.addToCart();
            //     $rootScope.wasInCart=false;
            // }
        }
        else if($scope.design){
            $scope.canvas.clear();
            $scope.progressShow=true;
            $scope.canvas.loadFromJSON($scope.design.jsondata,function(){
                $scope.loadCanvas();
                $scope.progressShow=false;
            });
        }
        else if(!$scope.design){
            //$scope.getDesign();
        }
            
        $scope.canvas.renderTop().renderAll().calcOffset();
        if(!$scope.$$phase) $scope.$apply();

    });
    $scope.keyPress = function(keyCode){
        console.log(keyCode);
        if($scope.selectedObject){
            if(keyCode==46){
                $scope.remove();
            }
            if(keyCode==13){
                $scope.accept();
            }
            if(keyCode==27){
                $scope.cancel();
            }
        }
    }
    $scope.is_keyboard=false;
    $scope.focused = function(){
        $scope.is_keyboard=true;
    };
    $scope.blurred = function(){
        $scope.is_keyboard=false;
    };
    $scope.clipp = function(ctx){
        if($scope.is_keyboard){

        }
        var h=$window.innerHeight-92-105-20;
        if($window.innerWidth<h)h=$window.innerWidth;
        if($scope.is_keyboard && isMobile.any()){
            h=$scope.tempSize;
        }
        $scope.tempSize=h;
        h=Math.sqrt(h*h/2);
        var x=Math.sqrt(h*h/2);
        $scope.canvasx=x;

        var clipTo = new fabric.Rect({ left: x, top:0, width: h, height: h, rx: h/20, ry: h/20, angle: 45, fill: '#FFFFFF', strokeWidth: 1, stroke: 'rgba(0,0,0,0.2)'});
        clipTo.render(ctx);
    };

}
Dyo.$inject = ['$scope', '$rootScope', '$timeout', 'Cache', '$location', '$upload','$window', '$http', 'Files', '$routeParams'];

/*

 DYO DIRECTIVE

 */
app.directive('dyo', function factory($window, $timeout) {
    return {
        restrict: 'A',
        compile: function compile(elm, attrs) {

            // http://www.w3schools.com/tags/canvas_rotate.asp
            var canvasSize=($window.innerHeight-92-105-20);
            if($window.innerWidth<canvasSize)canvasSize=$window.innerWidth;

            var domElt = document.getElementById('dyo-canvas');
            dscope = angular.element(domElt).scope();
            dscope.canvaswidth = canvasSize+'px';
            dscope.canvasheight = canvasSize+'px';


            var canvasTemplate = '<canvas id="myCanvas" height="'+canvasSize+'" width="'+canvasSize+'"></canvas>';
            var canvasElement = angular.element(canvasTemplate);



            elm.append(canvasElement);

            return function link($scope, element, attrs) {
                $scope.winheight=$window.innerHeight;
                $scope.is_landscape = ($window.innerHeight < $window.innerWidth);
                /*
                 Canvas
                 */
                $scope.canvas = new fabric.Canvas(canvasElement[0]); //, {isDrawingMode: true});

                fabric.Object.prototype.setOriginToCenter = function() {
                    this._originalOriginX = this.originX;
                    this._originalOriginY = this.originY;

                    var center = this.getCenterPoint();

                    this.set({
                        originX: 'center',
                        originY: 'center',
                        left: center.x,
                        top: center.y
                    });
                };

                fabric.Object.prototype.setCenterToOrigin = function() {
                    var originPoint = this.translateToOriginPoint(
                        this.getCenterPoint(),
                        this._originalOriginX,
                        this._originalOriginY);

                    this.set({
                        originX: this._originalOriginX,
                        originY: this._originalOriginY,
                        left: originPoint.x,
                        top: originPoint.y
                    });
                };
                $scope.canvas.clipTo = function (ctx) {
                    $scope.clipp(ctx);

                };

                /*

                 Selected
                 - Selected and saves the object to a variable until we release it.

                 */
                $scope.canvas.on('object:selected', function(options) {


                        //console.log(options.target)
                    if($scope.saving)return;
                    $scope.selectedObject = options.target;

                    $scope.obj.scale=Math.round($scope.selectedObject.scaleX*100)/100;
                    $scope.obj.angle=Math.round($scope.selectedObject.angle*100)/100;
                    $scope.selectedObject.set({cornerSize: 20, borderColor: 'red', cornerColor: 'red'});




                    $scope.select();
                    if($scope.selectedObject.get('type')=="text" || $scope.selectedObject.get('type')=="CurvedText" || $scope.selectedObject.originalObject != undefined){
                        $scope.textOptionsShow=true;
                    }
                    else{
                        $scope.optionsShow=true;
                    }
                    if(!$scope.$$phase) $scope.$apply();
                    $scope.canvas.renderAll();




                });
                /*

                 Set Active
                 - When clicking around the canvas, this will keep it set on the object
                 we have selected until we release it with cancel or accept buttons.

                 */
                $scope.canvas.on('selection:cleared', function() {
                    //console.log('cleared..');
                    if($scope.selectedObject) $scope.accept();//$scope.canvas.setActiveObject($scope.selectedObject);
                });


                /*

                 Out of Bounds
                 t-shirt / tshirt
                 - http://stackoverflow.com/questions/19979644/set-object-drag-limit-in-fabric-js

                 */
                $scope.canvas.on("object:moving", function(){
                    //console.log('moving...');
                });
                $scope.canvas.on("object:rotating", function(obj){
                    if($scope.selectedObject.angle>360)$scope.selectedObject.angle=$scope.selectedObject.angle-360;
                    $scope.obj.angle=Math.round($scope.selectedObject.angle*100)/100;
                    if(!$scope.$$phase) $scope.$apply();

                });
                $scope.canvas.on("object:scaling", function(obj){
                    $scope.obj.scale=Math.round($scope.selectedObject.scaleX*100)/100;
                    if(!$scope.$$phase) $scope.$apply();

                });
                /*
                 On Window Resize / Adjust canvas
                 */

                angular.element($window).bind('resize', function () {
                    var h=$window.innerHeight-92-105-20;
                    if($window.innerWidth<h)h=$window.innerWidth;

                    var scale=h/$scope.tempSize;

                    $scope.canvas.setWidth(h);
                    $scope.canvas.setHeight(h);
                    $scope.canvas.calcOffset();


                    $scope.canvas.clipTo = function (ctx) {
                        $scope.clipp(ctx);
                    };

                    $scope.canvas.forEachObject(function(obj){
                        obj.left=obj.left*scale;
                        obj.scaleX=obj.scaleX*scale;
                        obj.top=obj.top*scale;
                        obj.scaleY=obj.scaleY*scale;
                        obj.setCoords();
                        if(!$scope.$$phase) $scope.$apply();
                    });

                    $scope.canvaswidth = h+'px';
                    $scope.canvasheight = h+'px';





                    $scope.canvas.renderTop().renderAll().calcOffset();
                    if(!$scope.$$phase) $scope.$apply();
                    $scope.resizing=false;
                });

            }
        }
    };
});
