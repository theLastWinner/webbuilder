/**
 * 用于请求controller通用资源，请求的资源必须提供标准接口
 * Created by 浩 on 2015-07-28 0028.
 */
define(function (require, exports, module) {
    var request = require("request");
    exports.get = function (name, callback) {
        var uri = getServicePath("/config/info");
        if(name.indexOf(".")!=-1){
            var id = name.substring(0,name.indexOf("."));
            var key = name.substring(name.indexOf(".")+1,name.length);
            uri+=name+"/"+key;
        }else{
            uri+=name+"/";
        }
        request.ajax(uri, {}, "GET", function (e, s) {
            callback(e);
        });
    }

});