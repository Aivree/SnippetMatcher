/**
 * 类加载器
 */
(function(W,D){
  if(W.I){
    return;
  }
  W.I = {
    version:'2.0.3',
    ROOT:'auto',
    debug:true
  };
  
  var M = 'icore.js';
  var R = '';
  var L = false;
  var SYN = true;
  var Q = {};
  //加载队列
  var P = {};
  
  var _dir = function(u){
    var r = '';
    if(!L){
      var c = D.getElementsByTagName('script');
      for(var i=0;i<c.length;i++){
        var s = c[i].getAttribute('src');
        if(!s) continue;
        var j = s.indexOf(M);
        if(j>-1){
          r = s.substr(0,j);
          break;
        }
      }
      _autoRoot(r);
      R = r;
      L = true;
    }else{
      r = R;
    }
    if(u){
      r += u;
    }
    return r;
  };
  
  var _autoRoot = function(r){
    if('auto'!=I.ROOT){
      return;
    }
    var n = r.indexOf('://'); 
    if(n>0){
      r = r.substr(n+3);
    }
    n = r.indexOf('/');
    if(n<0){
      I.ROOT = '';
    }else{
      r = r.substr(n);
      n = r.indexOf('/',1);
      if(n<0){
        I.ROOT = '/';
      }else{
        r = r.substr(0,n);
        if(r.indexOf('.')<0){
          I.ROOT = r;
        }else{
          I.ROOT = '/';
        }
      }
    }
  };
  
  var initPackage = function(k){
    var p = I;
    for(var i=0;i<k.length;i++){
      if(!p[k[i]]){
        p[k[i]]={};
      }
      p = p[k[i]];
    }
    return p;
  };
  
  var localLoad = function(c,callback){
    if(I.debug){
      I.lang.Store.clear();
      throw 'I.js debug mode';
    }
    var v = I.lang.Store.get('i_js_version');
    if(!v||I.version!=v){
      I.lang.Store.remove('I.*');
      I.lang.Store.set('i_js_version',I.version);
      throw 'I.js need update';
    }
    var code = I.lang.Store.get('I.'+c);
    if(!code){
      throw c+' need update';
    }
    var o = D.createElement('script');
    o.text = code;
    D.getElementsByTagName('head')[0].appendChild(o);
    try{
      o.parentNode.removeChild(o);
    }catch(e){}
    callback.call(callback,c);
  };
  var lazyLoad = function(c,callback){
    var o = D.createElement('script');
    o.type = 'text/javascript';
    var src = R+c.replace(/\./g,'/')+'.js?t='+new Date().getTime();
    o.src = src;
    o.onload = function(){
      try{
        this.parentNode.removeChild(this);
      }catch(e){
        return;
      }
      callback.call(callback,c);
    };
    o.onreadystatechange = function(){
      if(/loaded|complete/.test(this.readyState)){
        try{
          this.parentNode.removeChild(this);
        }catch(e){
          return;
        }
        callback.call(callback,c);
      }
    };
    D.getElementsByTagName('head')[0].appendChild(o);
  };
  var initClass = function(klass,code){
    var k = klass.split('.');
    var name = k.pop();
    var p = initPackage(k);
    if(p[name]){
      return p[name];
    }else{
      var o = eval('(false||'+code+')')(W,D);
      p[name] = o;
      return o;
    }
  };
  var checkLoad = function(callback){
    for(var i in P){
      if(!P[i].loaded){
        W.setTimeout(function(){
          checkLoad(callback);
        },1);
        return;
      }
    }
    var cq = {};
    for(var i in P){
      if(!Q[i].loaded){
        var cs =_depend(Q[i].code);
        for(var j=0;j<cs.length;j++){
          cq[cs[j]] = {};
        }
      }
    }
    var over = true;
    for(var i in cq){
      if(P[i]) continue;
      P[i] = {loaded:false};
      tryLoad(i);
      over = false;
    }
    if(!over){
      W.setTimeout(function(){
        checkLoad(callback);
      },1);
      return;
    }
    for(var i in P){
      if(!Q[i].loaded){
        initClass(i,Q[i].code);
        Q[i].loaded = true;
      }
    }
    SYN = false;
    callback.call(callback);
  };
  var tryLoad = function(c){
    try{
      localLoad(c,function(){
        P[c].loaded = true;
      });
    }catch(e){
      lazyLoad(c,function(){
        P[c].loaded = true;
      });
    }
  };
  var load = function(cs,callback){
    SYN = true;
    for(var i=0;i<cs.length;i++){
      var s = cs[i];
      if(Q[s]) continue;
      if(P[s]) continue;
      P[s] = {loaded:false};
      tryLoad(s);
    }
    checkLoad(callback);
  };
  
  var preLoad = function(cs,f){
    if(SYN){
      W.setTimeout(function(){
        preLoad(cs,f);
      },1);
    }else{
      load(cs,f);
    }
  };
  
  _dir();
  
  var F = [
    function(f){W.attachEvent('onload',f);},
    function(f){W.addEventListener('DOMContentLoaded',f,false);}
  ];
  for(var i=0;i<F.length;i++){
    try{
      F[i](function(){
        SYN = false;
      });
      break;
    }catch(e){}
  }
    
  var _regist = function(c,cd){
    Q[c] = {
      loaded:false,
      code:cd
    };
    if(0==c.indexOf('lang.')){
      try{
        initClass(c,cd);
      }catch(e){
        throw e;
      }
    }else{
      try{
        I.lang.Store.set('I.'+c,'I.regist(\''+c+'\','+cd+'+\'\');');
      }catch(e){}
    }
  };
  
  var _depend = function(code){
    var c = {};
    var s = code;
    var idx = s.indexOf('I.');
    while(idx>-1){
      s = s.substr(idx+2);
      var kh = [];
      kh.push(s.indexOf('('));
      kh.push(s.indexOf('['));
      kh.push(s.indexOf(';'));
      var ix = -1;
      for(var i=0;i<kh.length;i++){
        if(kh[i]<0){
          continue;
        }
        if(ix<0){
          ix = kh[i];
        }else{
          if(kh[i]<ix){
            ix = kh[i];
          }
        }
      }
      
      var q = s.substr(0, ix);
      if(q.indexOf('.')>-1){
        var k = q.substr(0, q.lastIndexOf('.'));
        if(k.indexOf('.')>-1){
          c[k] = {};
        }
      }
      idx = s.indexOf('I.');
    }
    s = code;
    idx = 0;
    var arr = /skin\s*:\s*['|"]/g.exec(s);
    while(null!=arr){
      idx = arr.index;
      s = s.substr(idx+arr[0].length);
      var h = arr[0];
      h = h.substr(h.length-1);
      idx = s.indexOf(h);
      var skin = s.substr(0,idx);
      skin = 'skin.'+skin;
      c[skin] = {};
      arr = /skin\s*:\s*['|"]/g.exec(s);
    }
    s = code;
    arr = /util\.Skin\.init\(['|"]/g.exec(s);
    while(null!=arr){
      idx = arr.index;
      s = s.substr(idx+arr[0].length);
      var h = arr[0];
      h = h.substr(h.length-1);
      idx = s.indexOf(h);
      var skin = s.substr(0,idx);
      skin = 'skin.'+skin;
      c[skin] = {};
      arr = /util\.Skin\.init\(['|"]/g.exec(s);
    }
    var z = [];
    for(var i in c){
      if(i.indexOf('+')>-1){
        i = i.substr(0,i.indexOf('+'));
      }
      if(0==i.indexOf('lang.')||'ROOT'==i||'debug'==i||'version'==i){
        continue;
      }
      z.push(i);
    }
    return z;
  };
  
  var _want = function(callback){
    var cs = _depend(callback+'');
    if(cs.length<1){
      callback.apply(callback);
    }else{
      preLoad(cs,callback);
    }
  };
  
  var _get = function(klass,callback){
    preLoad([klass],function(){
      var k = klass.split('.');
      var name = k.pop();
      var obj = I;
      for(var i=0;i<k.length;i++){
        obj = obj[k];
      }
      callback.call(obj[name]);
    });
  };

  I.regist = function(klass,code){_regist(klass,code);return this;};
  I.dir = function(res){return _dir(res);};
  I.want = function(callback){_want(callback);};
  I.get = function(klass,callback){_get(klass,callback);};
  
  I.regist('lang.Store',function(w,d){
    var _host = location.hostname||'localhost';
    var _LS = [{
      loaded:false,
      instance:null,
      support:function(){
        if(!this.loaded){
          this.instance = w.localStorage;
          this.loaded = true;
        }
        return this.instance?true:false;
      },
      setItem:function(k,v){
        this.instance.setItem(k,v);
      },
      getItem:function(k){
        return this.instance.getItem(k);
      },
      clear:function(){
       this.instance.clear();
      },
      removeItem:function(k){
        var inst = this.instance;
        var c = [];
        var l = inst.length;
        switch(k.indexOf('*')){
          case 0:
            var suffix = k.substr(1);
            for(var i=0;i<l;i++){
              var key = inst.key(i);
              if(key.indexOf(suffix)==key.length-suffix.length){
                c.push(key);
              }
            }
          break;
          case k.length-1:
            var prefix = k.substr(0,k.length-1);
            for(var i=0;i<l;i++){
              var key = inst.key(i);
              if(0==key.indexOf(prefix)){
                c.push(key);
              }
            }
          break;
          default:c.push(k);break;
        }
        for(var i =0;i<c.length;i++){
          inst.removeItem(c[i]);
        }
      }
    },
    {
      loaded:false,
      instance:null,
      support:function(){
        if(!this.loaded){
          try{
            var box = d.body||d.getElementsByTagName('head')[0]||d.documentElement;
            var o = d.createElement('input');
            o.type = 'hidden';
            o.style.display = 'none';
            o.addBehavior ("#default#userData");
            box.appendChild(o);
            var exp = new Date();
            exp.setDate(exp.getDate()+365);
            o.expires = exp.toUTCString();
            this.instance = o;
          }catch(e){}
          this.loaded = true;
        }
        return this.instance?true:false;
      },
      setItem:function(k,v){
        var obj = this.instance;
        obj.load(_host);
        obj.setAttribute(k,v);
        obj.save(_host);
      },
      getItem:function(k){
        this.instance.load(_host);
        return this.instance.getAttribute(k);
      },
      clear:function(){
        var exp = new Date();
        exp.setDate(exp.getDate()-2);
        this.instance.expires = exp.toUTCString();
        exp = new Date();
        exp.setDate(exp.getDate()+365);
        this.instance.expires = exp.toUTCString();
      },
      removeItem:function(k){
        var index = k.indexOf('*');
        if(0!=index&&k.length-1!=index){
          this.instance.load(_host);
          this.instance.removeAttribute(k);
          this.instance.save(_host);
        }else{
          this.clear();
        }
      }
    }];
    var _set = function(k,v){
      for(var i=0;i<_LS.length;i++){
        if(_LS[i].support()){
          _LS[i].setItem(k,v);
          break;
        }
      }
    };
    var _get = function(k){
      for(var i=0;i<_LS.length;i++){
        if(_LS[i].support()){
          return _LS[i].getItem(k);
        }
      }
      return null;
    };
    var _clear = function(){
      for(var i=0;i<_LS.length;i++){
        if(_LS[i].support()){
          _LS[i].clear();
          break;
        }
      }
    };
    var _remove = function(k){
      for(var i=0;i<_LS.length;i++){
        if(_LS[i].support()){
          _LS[i].removeItem(k);
          break;
        }
      }
    };
    return {
      set:function(k,v){_set(k,v);},
      get:function(k){return _get(k);},
      remove:function(k){return _remove(k);},
      clear:function(){_clear();}
    };
  }+'');
  
  I.regist('lang.Core',function(W,D){
    var EACH = {'[object Array]':true,'[object NodeList]':true,'[object HTMLCollection]':true,'[object Arguments]':true};
    var C = [
      function(o,s){o.style.cssText = s;},
      function(o,s){o.setAttribute('style',s);}
    ];
    var S = [
      function(o,s){o.className = s;},
      function(o,s){o.setAttribute('class',s);}
    ];
    var P = [
      function(o,n){o.style.filter='alpha(opacity='+n+')';},
      function(o,n){o.style.opacity = n/100;}
    ];
    var E = [
      function(o,s,f){
        o.attachEvent('on'+s,function(e){
          f(o,W.event || e);
        });
      },
      function(o,s,f){
        o.addEventListener(s,function(e){
          f(o,W.event || e);
        },false);
      }
    ];
    var STYLE = [
      function(s){
        var o = D.createStyleSheet();
        o.cssText = s;
      },
      function(s){
        var o = D.createElement("style");
        o.type = "text/css";
        o.innerHTML = s;
        D.getElementsByTagName("head")[0].appendChild(o);
      }
    ];
    var G = {
      ID:function(o,s){return o.getElementById(s);},
      NAME:function(o,s){return o.getElementsByName(s);},
      TAG:function(o,s){return o.getElementsByTagName(s);},
      CHILD:function(o){
        var c = [];
        for(var i=0;i<o.childNodes.length;i++){
          var m = o.childNodes[i];
          if(1 == m.nodeType){
            c.push(m);
          }
        }
        return c;
      },
      NEXT:function(d,o){
        var pt = o.parentNode;
        var chd = this.CHILD(pt);
        var me = false;
        for(var i=0;i<chd.length;i++){
          if(me){
            return chd[i];
          }
          if(chd[i]==o){
            me = true;
          }
        }
        return null;
      },
      PREVIOUS:function(d,o){
        var pt = o.parentNode;
        var chd = this.CHILD(pt);
        var last = null;
        for(var i=0;i<chd.length;i++){
          if(chd[i]==o){
            return last;
          }
          last = chd[i];
        }
        return null;
      },
      CLASS:function(o,s){
        if(o.getElementsByClassName){
          return o.getElementsByClassName(s);
        }
        var c = [];
        var els = this['TAG'](o,'*');
        var i = els.length;
        var ss = s.replace(/\-/g, '\\-');
        var pattern = new RegExp('(^|\\s)'+ss+'(\\s|$)');
        while(--i>=0){
          if(pattern.test(els[i].className)||pattern.test(els[i].getAttribute('class'))){
            c.push(els[i]);
          }
        }
        return c;
      }
    };
    var A = {
      IN:function(a,b){b.appendChild(a);},
      BEFORE:function(a,b){b.parentNode.insertBefore(a,b);},
      AFTER:function(a,b){b.parentNode.lastChild == b?this['IN'](a,b.parentNode):this['BEFORE'](a,b.nextSibling);}
    };
    var $ = function(o){
      switch(o.length){
        case 1: return typeof o[0] == 'string'?('*'==o[0]?G['CHILD'](D.body):G['ID'](D,o[0])):o[0];
        case 2: return '*'==o[1]?G['CHILD']($([o[0]])):G[o[0].toUpperCase()](D,o[1]);
        case 3: return G[o[1].toUpperCase()]($([o[0]]),o[2]);
      }
      return D;
    };
    var _insert = function(o){
      if(o.length<1) return null;
      var m = typeof o[0] == 'string'?D.createElement(o[0]):o[0];
      switch(o.length){
        case 1: A['IN'](m,D.body);break;
        case 2: A['IN'](m,o[1]);break;
        case 3: A[o[1].toUpperCase()](m,o[2]);break;
      }
      return m;
    };
    var _region = function(o){
      var x = 0,y = 0,w = 0,h = 0;
      switch(o.length){
        case 1:
          var r = _region([]);
          var m = o[0];
          w = m.offsetWidth;
          h = m.offsetHeight;
          if(m.getBoundingClientRect){
            x = m.getBoundingClientRect().left + r.x - D.documentElement.clientLeft;
            y = m.getBoundingClientRect().top + r.y - D.documentElement.clientTop;
          }else for(;m;x+=m.offsetLeft,y+=m.offsetTop,m=m.offsetParent);
          break;
        default:
          x = Math.max(D.documentElement.scrollLeft,D.body.scrollLeft);
          y = Math.max(D.documentElement.scrollTop,D.body.scrollTop);
          w = D.documentElement.clientWidth;
          h = D.documentElement.clientHeight;
      }
      return {'x':x,'y':y,'width':w,'height':h};
    };
    var _listen = function(o,s,f){
      _each(o,function(m,j){
        for(var i=0;i<E.length;i++){
          try{
            E[i]($([m]),s,f);
            break;
          }catch(e){}
        }
      });
    };
    var _opacity = function(o,n){
      _each(o,function(m,j){
        for(var i=0;i<P.length;i++){
          try{
            P[i]($([m]),n);
          }catch(e){}
        }
      });
    };
    var _css = function(o,s){
      _each(o,function(m,j){
        for(var i=0;i<C.length;i++){
          try{
            C[i](m,s);
            break;
          }catch(e){}
        }
      });
    };
    var _cls = function(o,s){
      _each(o,function(m,j){
        for(var i=0;i<S.length;i++){
          try{
            S[i](m,s);
            break;
          }catch(e){}
        }
      });
    };
    var _style = function(s){
      for(var i=0;i<STYLE.length;i++){
        try{
          STYLE[i](s);
          break;
        }catch(e){}
      }
    };
    var _each = function(l,f){
      var tp = Object.prototype.toString.apply(l);
      if(EACH[tp]||(l.length&&(!l.alert)&&('[object String]'!=tp))){
        for(var i=0;i<l.length;i++){
          f(l[i],i);
        }
      }else{
        f(l,0);
      }
    };
    var _delay = function(time,callback){
      W.setTimeout(function(){
        callback.call(callback);
      },time);
    };
   
    I['$'] = function(){return $(arguments);};
    I['region'] = function(){return _region(arguments);};
    I['css'] = function(o,s){_css(o,s);return I;};
    I['cls'] = function(o,s){_cls(o,s);return I;};
    I['trim'] = function(s){return s.replace(/(^\s*)|(\s*$)/g,'');};
    I['insert'] = function(){return _insert(arguments);};
    I['listen'] = function(o,s,f){_listen(o,s,f);return f;};
    I['opacity'] = function(o,n){_opacity(o,n);return I;};
    I['style'] = function(s){_style(s);return I;};
    I['each'] = function(l,f){_each(l,f);return I;};
    I['delay'] = function(time,callback){_delay(time,callback);};
    
    return {};
  }+'');
})(window,document);