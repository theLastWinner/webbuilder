var bootPATH = getRootPath()+"/plugins/miniui/";
document.write('<script src="' + bootPATH + '../jquery/jquery-1.6.2.min.js" type="text/javascript" ></sc' + 'ript>');
document.write('<script src="' + bootPATH + 'miniui.js" type="text/javascript" ></sc' + 'ript>');
document.write('<link href="' + bootPATH + 'themes/default/miniui.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/icons.css" rel="stylesheet" type="text/css" />');
//skin
var skin = getCookie("miniuiSkin");
if (skin) {
    document.write('<link href="' + bootPATH + 'miniui/themes/' + skin + '/skin.css" rel="stylesheet" type="text/css" />');
}
function getCookie(sName) {
    var aCookie = document.cookie.split("; ");
    var lastMatch = null;
    for (var i = 0; i < aCookie.length; i++) {
        var aCrumb = aCookie[i].split("=");
        if (sName == aCrumb[0]) {
            lastMatch = aCrumb;
        }
    }
    if (lastMatch) {
        var v = lastMatch[1];
        if (v === undefined) return v;
        return unescape(v);
    }
    return null;
}

// 开启iframeWindow
function openWindow(url,title,width,height,ondestroy){
    mini.open({
        url:url,
        showMaxButton: true,
        title: title,
        width: width,
        height: height,
        ondestroy: ondestroy
    });
}