/**
 * Created by 浩 on 2015-09-09 0009.
 */
define(function (require, exports, module) {
    var socketUrl = getServicePath("/socket");
    if (socketUrl.indexOf("https") != -1)
        socketUrl = socketUrl.replace("https", "wss");
    else
        socketUrl = socketUrl.replace("http", "ws");

    exports.getInstance = function () {
        if (!window.top.__WS) {
            return new WS();
        } else {
            return window.top.__WS;
        }
    }

    var WS = function () {
        var isOpen = false;
        var __proto = this;
        window.top.__WS = __proto;
        __proto._ws;
        __proto.events={};
        __proto.on = function (e, func) {
            __proto['on' + e] = func;
        };
        __proto.send = function (cmd, params,callBack) {
            if(typeof (callBack)=='string'){
                params.callBack=callBack;
                if(!__proto["on"+callBack]){
                    throw new Error("event :"+"on"+callBack+" not found!");
                    return;
                }
            }
            var data = {cmd: cmd, params: params};
            __proto._ws.send(JSON.stringify(data));
        }
        __proto.open = function () {
            if(isOpen){
                if (__proto.onopen)
                    __proto.onopen();
                return;
            }
            if (WebSocket) {
                __proto._ws = new WebSocket(socketUrl)
            } else if (SockJS) {
                __proto._ws = new SockJS(url, undefined, {protocols_whitelist: []});
            }
            if (!__proto._ws)
                __proto.onerror("浏览器不支持WebSocket！");
            else {
                __proto._ws.onopen = function () {
                    if (__proto.onopen) {
                        __proto.onopen();
                    }
                };
                __proto._ws.onmessage = function (event) {
                    var res = event.data;
                    if (res) {
                        try {
                            var jsonData = JSON.parse(res);
                            if (jsonData.cmd && jsonData.success) {
                                __proto['on' + jsonData.cmd](true, jsonData.data);
                            } else {
                                __proto['on' + jsonData.cmd](false, jsonData.data);
                            }
                        } catch (e) {
                            __proto.onerror(e);
                        }
                    }
                };
                __proto._ws.onclose = function (event) {
                    isOpen =false;
                    if (__proto.onclose) {
                        __proto.onclose(event);
                    }
                };
                isOpen = true;
            }
        };
        __proto.on('error', function (msg) {
            logger.error(msg);
        });
        return __proto;
    };
});