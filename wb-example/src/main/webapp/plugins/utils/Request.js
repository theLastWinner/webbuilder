/**
 * 用于请求controller通用资源，请求的资源必须提供标准接口
 * Created by 浩 on 2015-07-28 0028.
 */
define(function (require, exports, module) {
    var $ = require("jquery");
    var request = this;

    function Batch() {
        var me = this;
        this.resources = [];
        this.async = false;
        this.add = function (service, param, resultName) {
            if (typeof (param) == 'undefined')
                param = {};
            if (typeof (resultName) == 'undefined')
                resultName = service;
            me.resources.push({service: service, param: param, resultName: resultName});
            return me;
        };
        this.do = function (callback) {
            var api = getServicePath("services");
            var param = {};
            param["resources"] = JSON.stringify(me.resources);
            ajax(api, param, "GET", callback, me.async);
        }
    }

    exports.Batch = Batch;

    //自定义表单访问
    var form = function () {
        var api = "cf/";
        this.list = function (form_id, data, callback) {
            ajax(api + form_id + "/list", data, "GET", callback, false);
        }

        this.total = function (form_id, data, callback) {
            ajax(api + form_id + "/total", data, "GET", callback, false);
        }

        this.info = function (form_id, id, callback) {
            ajax(api + form_id + "/" + id, {id: id}, "GET", callback, false);
        }

        this.add = function (form_id, data, callback) {
            ajax(api + form_id, data, "POST", callback, false);
        }

        this.update = function (form_id, id, data, callback) {
            ajax(api + form_id + "/" + id, data, "PUT", callback, false);
        }

        this.del = function (form_id, id, callback) {
            ajax(api + form_id + "/" + id, {id: id}, "DELETE", callback, false);
        }
        return this;
    }

    exports.form = new form();
    function post(uri,data,callback){
         service(uri,data,"POST",callback);
    }
    function get(uri,data,callback){
        service(uri,data,"GET",callback);
    }
    function service(uri, data, method, callback) {
        ajax(getServicePath(uri), data, method, callback, false);
    }

    function list(module, data, callback) {
        ajax(getServicePath(module), data, "GET", callback, false);
    }

    function total(module, data, callback) {
        ajax(getServicePath(module) + "/total", data, "GET", callback, false);
    }

    function info(module, id, callback) {
        ajax(getServicePath(module) + "/" + id, {id: id}, "GET", callback, false);
    }

    function add(module, data, callback) {
        ajax(getServicePath(module), data, "POST", callback, false,true);
    }

    function update(module, data, callback) {
        ajax(getServicePath(module), data, "PUT", callback, false,true);
    }

    function del(module, id, callback) {
        ajax(getServicePath(module) + "/" + id, {id: id}, "DELETE", callback, false,true);
    }

    function ajax(uri, data, method, callback, syc,requestBody) {
        if (requestBody==true) {
            data = JSON.stringify(data);
        }
        var param = {
            type: method,
            url: uri,
            data: data,
            cache: false,
            async: syc == true,
            success: callback,
            dataType: 'json'
        };
        if (requestBody==true) {
            param.contentType = "application/json";
        }
        $.ajax(param);
    }

    function jsoup(uri, data, method, callback, syc) {
        var callback_ = "callback" + (Math.ceil(Math.random() * 1000000));
        var param = {
            type: method,
            url: uri,
            data: data,
            cache: false,
            async: syc == true,
            success: callback,
            callback: callback_,
            dataType: 'json'
        };
        $.ajax(param);
    }

    exports.jsoup = jsoup;
    exports.ajax = ajax;
    exports.list = list;
    exports.add = add;
    exports.update = update;
    exports.del = del;
    exports.total = total;
    exports.info = info;
    exports.service = service;
    exports.get=get;
    exports.post=post;

    var params = function () {
        var urlParams = {};
        var match,
            pl = /\+/g,  // Regex for replacing addition symbol with a space
            search = /([^&=]+)=?([^&]*)/g,
            decode = function (s) {
                return decodeURIComponent(s.replace(pl, " "));
            },
            query = window.location.search.substring(1);
        while (match = search.exec(query))
            urlParams[decode(match[1])] = decode(match[2]);
        return urlParams;
    }
    var hash = function () {
        var hash = window.location.hash;
        if (hash.length > 0) {
            hash = hash.substring(1, hash.length);
        }
        var hashs = hash.split('&');
        var data = {};
        try {
            hashs.forEach(function (e) {
                var d = e.split('=');
                if (d[0] && d[1])
                    data[d[0]] = d[1];
            });
        } catch (e) {
        }
        return data;
    }
    exports.params = params();
    exports.hash = hash();

})