jQuery(function($){


    $.fn.zxSuperSlider = function( options ){


        // 默认配置
        var settings = {
            areaId: 'superSlider',
            loadBottom: '300',
            groupBox: 'group',
            groupInnerBox: 'groupInner',
            groupPage: 'groupPage',
            boxW: 300,
            boxH: 150,
            autoSlide: true,
            vertical: false,
            speed: 800,
            interval:8000,
            defaultShowRow: 3,
            ajaxUrl: 'getResource.php',
            tplId: 'groupTmpl',
            loading: true
        };

        if( options ) $.extend( settings, options );

        var S = SuperSlider = {index: 1};
        S.el = this;
        //alert(S.el.offset().top);
        //S.el.css('display', 'none');  // 初始化时，先隐藏对象

        S = $.extend( S, {
            initialize: function() {
                this.initData();
                this.scrollEvent();
            },
            // 初始化数据
            initData: function() {

                $('#'+ settings.areaId).css({paddingBottom: settings.loadBottom+'px'})
                S.groupArr = [];
                S.groupBoxs = $('.'+ settings.groupBox, S.el);
                S.groupBoxs.each(function(){

                    /*S.groupArr.push({
                        groupBox: $(this),
                        groupInnerBox: $('.'+ settings.groupInnerBox, $(this)),
                        groupUl: $('ul', $(this)),
                        groupLis: $('li', $(this))
                    });*/
                    S.groupSingle( $(this) );
                });
                S.scrollHOld = 0;

            },
            groupSingle: function( obj ) {

                var G = Group = {};
                var boxW,boxH;

                boxW = settings.boxW;
                boxH = settings.boxH;

                G.el = obj;
                G.idex = 0; // 初始化时，默认的图片索引为0（第一张图片）

                G = $.extend( G, {
                    initialize: function(){
                        this.initData();
                        this.initBox();
                        this.renderPage();
                    },
                    initData: function() {
                        G.innerBox = $('.'+ settings.groupInnerBox, G.el);
                        G.ul = $('ul', G.el);

                        // 选择切换模式 （水平 或 垂直）
                        if( settings.vertical ) {
                            G.MT = 'margin-top';
                        } else {
                            G.MT = 'margin-left';
                        }

                        this.lisClone();
                        G.lis = $('li', G.el);


                    },
                    initBox: function() {

                        G.innerBox.width( boxW ).height( boxH ).
                            css( 'overflow', 'hidden' );

                        if( settings.vertical ) {
                            G.ul.width( boxW ).height( G.lis.length * boxH );
                        } else {
                            G.ul.width( G.lis.length * boxW ).height( boxH );
                        }
                        G.lis.width( boxW ).height( boxH ).
                            css({float: 'left', overflow: 'hidden'});
                    },
                    lisClone: function() {
                        G.ul.prepend( $('li:last-child',G.ul).clone().
                            css(G.MT,"-"+ (settings.vertical ? boxH : boxW) +"px") );

                        G.ul.append($('li:nth-child(2)',G.ul).clone());
                    },
                    // 渲染页脚
                    renderPage: function() {
                        var PHtml,PDiv,aHtml,on;

                        PHtml = "<div class='"+settings.groupPage+"'></div>";
                        PDiv = $( PHtml).width( settings.boxW );
                        aHtml = "";

                        // 这里 （-2） 是去除UL里面clone出来的两个对象
                        for( var i=0;i< G.lis.length-2;i++ ) {
                            on = !i ? 'on': '';
                            aHtml += "<a target='_blank' href='javascript:;' class='"+ on +"'>"+ ( i + 1 ) +"</a>";
                        }

                        PDiv.html( aHtml );

                        // 给滑动页脚添加事件
                        G.pageObj = PDiv; // 将page对象赋值到G对象上
                        this.addEvent( PDiv );

                        G.el.append( PDiv );


                        if( settings.autoSlide ) G.autoSlide();
                    },
                    // 自动滑动
                    autoSlide: function() {
                        G.Time = setTimeout( function(){
                            // 计时器触发后，去除 “点击事件”
                            G.Time = null;
                            G.pageObj.off('click');
                            G.run();
                        },settings.interval );
                    },
                    addEvent: function( eObj ) {
                        var tar,idex;

                        eObj.off('click').on('click', function( e ){
                            tar = e.target;
                            if( tar.tagName == "A" ) {
                                clearTimeout( G.Time ); // 成功激活滑动事件后，暂时清空“计时器”
                                eObj.off('click'); // 成功激活滑动事件后，暂时去除事件绑定
                                idex = $('a', eObj).index( $(tar) );


                                G.idex = idex; // 同步激活的图片索引
                                G.run( idex );
                            }

                        });
                    },
                    run: function( idex ) {
                        //alert(idex);

                        if( idex!=null ) {
                            G.doEffect( idex );
                        } else {

                            if(G.idex < (G.lis.length -2 ) ) {
                                G.idex++;
                            } else {
                                G.idex = 0;
                            }

                            G.doEffect( G.idex );
                        }
                    },
                    doEffect: function( idex ) {
                        var aniObj, cdex, dex;

                        // 给激活的按钮 添加 “激活标识”
                        if( idex == G.lis.length - 2) {
                            dex = 0;
                        } else {
                            dex = idex ;
                        }

                        //console.log(dex);
                        $('a', G.pageObj).removeClass('on').eq( dex ).addClass('on');
                        if ( idex!=null ) {
                            cdex = idex;
                        } else {

                        }
                        //console.log( idex );
                        if( settings.vertical ) {
                            aniObj = {
                                'margin-top': "-"+ ( cdex * boxH )
                            };
                        } else {
                            aniObj = {
                                'margin-left':  "-"+ ( cdex * boxW )
                            };
                        }

                        G.ul.animate(aniObj, settings.speed, function(){
                            G.doEffectAction();
                            G.addEvent( G.pageObj ); // 滑动完成，恢复滑动事件
                            G.autoSlide(); // 滑动完成，恢复计时器

                            if( G.idex == (G.lis.length - 2) ) {
                                G.idex = 0;
                                if( settings.vertical ) {
                                    G.ul.css( {marginTop: 0} );
                                } else {
                                    G.ul.css( {marginLeft: 0} );
                                }

                            } else {

                            }
                        });



                    },
                    doEffectAction: function() {
                        //console.log('1212');
                        console.log(G);
                    }
                });

                var IMG = {
                    loadBack: function( src, callback, error ) {
                        var img, userAgent;

                        img = new Image();

                        img.onerror = function() {
                            if( error ) error();
                            img.onload = img.onerror = null;
                            return;
                        }

                        img.src = src;
                        if( img.readyState == "complete" || img.complete == true ) {
                            if( callback ) callback();
                            img.onload = img.onerror = null;
                            return;
                        }

                        img.onload = function() {
                            if( callback ) callback();
                            img.onload = img.onerror = null;
                            return;
                        }

                    }
                };

                // 初始化数据前，判断 “图片” 是否加载完成
                $.extend(G, {
                    initializeBefore: function(){
                        G.el.css( 'float', 'left').width( boxW).height( boxH );
                        this.checkLoad();
                    },
                    checkLoad: function() {
                        var imgs;

                        if( settings.loading ) {
                            G.el.addClass('loading'); // 加载效果
                            $('.'+ settings.groupInnerBox, G.el).addClass('hidden');
                        }
                        imgs = $('.'+ settings.groupInnerBox +' img', G.el );
                        G.loadIdex = 0;
                        G.loadNum = imgs.length;

                        imgs.each( function() {
                            IMG.loadBack( $(this).attr('src'), function() {

                                G.loadIdex++;
                                G.doInitialize();
                            }, $.proxy(function() {

                                // 载入出错后，减去校验基数
                                G.loadNum--;
                                // 删除载入出错的图片
                                $(this).parent().parent().remove();
                                G.doInitialize();
                            }, this));
                        });

                    },
                    doInitialize: function() {
                        // 完成图片加载后，进行数据初始化
                        //console.log(G.loadIdex+";"+G.loadNum);

                        if( G.loadIdex >= G.loadNum ) {
                            if( settings.loading ) {
                                setTimeout(function() {

                                    G.el.removeClass('loading'); // 加载完后，去除效果
                                    $('.'+ settings.groupInnerBox, G.el).removeClass('hidden');

                                    G.initialize();
                                },1000);


                            }

                        }
                    }
                });

                G.initializeBefore();


            },
            scrollEvent: function() {
                // 监听窗口滑动
                $(window).scroll(function( e ){
                    //console.log(e);
                    clearTimeout(S.Time);  // 窗口滑动一直滑动时，则清除计时器
                    S.Time = setTimeout(function() {
                        S.scroll();
                        S.checkInner();
                    }, 300);  // 窗口停止滑动时，开始计时

                });
            },
            // 检测“可视区域内”的 “对象”，使其保持滑动效果，并清除 “可视区域外” 的滑动效果
            checkInner: function() {
                var cTop,cBottom,scrollH;

                scrollH = document.documentElement.scrollTop || document.body.scrollTop || 0;
                cTop = scrollH;
                cBottom = cTop + document.documentElement.clientHeight;
                //console.log( "cTop="+ cTop +";cBottom="+ cBottom);

            },
            scroll: function() {
                var topDis, H, bottomDis, scrollH;

                topDis = S.el.offset().top;
                H = S.el.height();
                bottomDis = topDis + H - settings.loadBottom;
                scrollH = document.documentElement.scrollTop || document.body.scrollTop || 0;

                if( ( scrollH >= bottomDis && scrollH > S.scrollHOld ) ||
                    ( $(window).height() + scrollH + 50) >= $(document).height() ) {
                    //console.log(bottomDis+";"+scrollH);
                    S.getResource();
                }
                S.scrollHOld = scrollH;

            },
            getResource: function() {
                var promise = $.ajax({
                    url: settings.ajaxUrl,
                    type: 'POST',
                    data: 'index='+ S.index,
                    dataType: 'json'
                });

                promise.always(function(){});
                promise.done(function( dataJson ){
                    if(dataJson && dataJson.length){
                        S.index++;
                        S.render(dataJson);
                    } else {
                        //if( !settings.unlimite ) Container.index--;
                    }
                });
                promise.error(function(){});
            },
            render: function( boxList ) {
                var len,len2,groupList,lis;

                len = boxList.length;
                groupList = [];

                for ( var i=0;i<len;i++ ) {
                    lis = [];
                    len2 = boxList[i].length;

                    for( var j=0;j<len2;j++ ) {
                        lis.push({
                            src: boxList[i][j]['img'],
                            url: boxList[i][j]['linkurl'],
                            width: settings.boxW,
                            height: settings.boxH
                        });
                    }

                    groupList.push({lis: lis});
                }

                S.handleTpl( $('#'+settings.tplId).tmpl(groupList) );

            },
            handleTpl: function( objs ) {
                var len = objs.length;

                for ( var i=0;i<len;i++ ) {
                    S.groupSingle( $(objs[i]) );
                }
                S.el.append( objs );
            }
        });


        S.initialize();



    };
});