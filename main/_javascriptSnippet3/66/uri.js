MimicMe.queryVars = function(){
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        var name = pair[0];
        var value = pair[1];
        if(name != 'read'){
            MimicMe.queryVars[name] = value;
        };
    };
};  
    
MimicMe.queryVars.read = function(name){
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if (pair[0] == name) {
            return pair[1];
        }
    }
    return null;
}
MimicMe.queryVars();
