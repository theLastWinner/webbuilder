// 所有模块都通过 define 来定义
define(function (require, exports, module) {
    // 通过 require 引入依赖
    // var $ = require('jquery');
    // var Spinning = require('./spinning');
    var $ = require('jquery');
    var template = require("template");
    var MD5 = require("MD5");
    var selected=null;
    var selectedData = null;
    var editor = null;
    $(document.body).append("<div style='display: none' id='formEdiorTemp'></div>");
    UE.plugins["editCom"] = function () {
        var me = this;
        var lastClickTime = 0;
        //注册一个触发命令的事件，同学们可以在任意地放绑定触发此命令的事件
        me.addListener('click', function () {
            var nowTime = new Date().getTime();
            var focusNode = editor.selection.getStart();
            $('#console').html("当前未选中控件！");
            if (focusNode.tagName.toLowerCase() == 'input') {
                selected=focusNode;
                $('#console').html("双击加载属性("+selected.id+")");
                editSelectField((nowTime-lastClickTime<200));
                exports.onSelected(selectedData);
            }else{
                selected=null;
                selectedData=null;
                exports.clearSelectData();
            }
              lastClickTime = new Date().getTime();
        });
    };

    exports.onSelected=function(e){

    }
    exports.getSelected=function(){
        return selected;
    }

    var attrs_base =
        [//{field:"id",name:"id"},
        {field:"c_type",name:"控件类型",required:false
            ,template:"<input type='text' value='text' id='__c_type' textField='text' valueField='id'  class='mini-combobox' data=\"contrl_list\" name='c_type'   />"
        },
        {field:"name",name:"name"},
        {field:"style",name:"style",required:false},
        {field:"remark",name:"备注"},
        {field:"alias",name:"别名"},
        {field:"vtype",name:"验证规则",required:false},
        {field:"defaultValue",name:"默认值",required:false},
        {field:"url",name:"数据来源",required:false,emptyText:"有效的url路径"},
        {field:"customfield",name:"自定属性",required:false},
        {field:"javatype",name:"java类型"
            ,template:"<input type='text' value='string' id='__javaType' textField='id' valueField='id' onvaluechanged=\"\" class='mini-combobox' data=\"javaType\" name='javatype'   />"},
        {field:"datatype",name:"数据类型"
            ,template:"<input type='text' value='varchar2'  id='__dataType'  textField='dataType' valueField='dataType' onvaluechanged=\"\"  class='mini-combobox' data=\"javaType\" name='datatype'   />"},
         {field:"length",name:"数据长度"},
         {field:"primarykey",name:"是否主键"
            ,template:"<input type='text' class='mini-checkboxlist' data=\"[{'id':'true',text:'是'}]\" name='primarykey'   />"},
        {field:"notnull",name:"不能为空"
            ,template:"<input type='text' class='mini-checkboxlist' data=\"[{'id':'true',text:'是'}]\" name='notnull'/>"}
    ];

    function editSelectField(justInit) {
        var data = {};
        var el = $(selected);
        attrs_base.forEach(function (e) {
            var val =el.attr(e.field);
            if (typeof (val) != "undefined" &&val!="")
                data[e.field] = val;
        });
        data._uid=el.attr("_uid");
        selectedData = data;
        if(justInit==true){
            $('#console').html("加载完成!");
            exports.selectData(data);
        }

    }
    exports.clearSelectData=function(){}
    exports.selectData=function (data){
       for(var e in data){
           $("#form [name='"+e+"']").val(data[e]);
       }
    };

    var attrs = " _uid='{{_uid}}' id='{{name}}' ";
    attrs_base.forEach(function (e) {
        attrs += e.field + "='{{" + e.field + "}}'"
    });


    var template__ = {
        text: {
            name: "单行文本",
            template: "<input type='text' class='mini-textbox' " + attrs + " />",
        },
        textarea: {
            name: "多行文本",
            template: "<input type='text' class='mini-textarea' " + attrs + " />"
        },
        select: {
            name: "下拉列表",
            template: "<input type='text' class='mini-combobox' " + attrs + " />"
        },
        tree: {
            name: "树菜单",
            template: "<input type='text' class='mini-treeselect' " + attrs + " />"
        },
        date: {
            name: "时间控件",
            template: "<input type='text' class='mini-datepicker' " + attrs + " />"
        },
        hide: {
            name: "隐藏域",
            template: "<input type='text' class='mini-hidden' " + attrs + " />"
        },
        file: {
            name: "文件上传",
            template: "<span id='_file_uploader{{id}}'><input type='input' class='mini-hidden' " + attrs + " /><div class=\"file_list\" id=\"file_list_{{id}}\"></div><a href=\"javascript:uploadFile('{{id}}')\">选择文件</a></span>"
        },
        img: {
            name: "图片上传",
            template: "<span id='_img_uploader{{id}}'><img src='' id='img_uploader{{id}}' onclick=\"uploadImg('{{id}}')\" title='上传图片' width='128' height='128' class='img_uploader' />" +
            "<br/> <input  type='input' class='mini-hidden' " + attrs + " /></span>"
        }
    };

    var contrl_list = [];
    for(var e in template__){
        contrl_list.push({id: e,text: template__[e].name});
    }
    exports.contrl_list=contrl_list;
    // 通过 exports 对外提供接口
    exports.initEditor = function (elId) {
        editor = UE.getEditor(elId);
        editor.ready(function () {
            editor.focus();
        });
        return editor;
    };
    exports.initForm = function (id) {
        var html="<table><tr><td colspan='2' align='center'><h2>控件属性</h2><span id='console'></span>" +
            "<input type='hidden' class='mini-hidden' id='_uid' name='_uid' /></td></tr>";
        attrs_base.forEach(function(e){
            html+=(" <tr>"+
            "<td align=\"right\">"+ e.name+":</td>"+
            "<td>" +
            ((!e.template)?"<input name=\""+ e.field+"\" "+(e.emptyText?"emptyText='"+ e.emptyText+"'":"")+" class=\"mini-textbox\" required=\""+ (e.required==false?false:true)+"\"/>":e.template )+
            "</td>"+
            "</tr>");
        });
        html+="<tr align='center'>" +
            "<td>" +
            "<a class='mini-button' onclick='save' iconCls='icon-save'>保存控件</a>"+
            "</td>" +
            "<td >" +
                "<a class='mini-button' onclick='saveForm()' iconCls='icon-save'>保存表单</a>"+
            "</td>" +
            "</tr>";
         html+="</table>";
        $("#"+id).html(html);
    };
    exports.saveField = function (data) {
        data.id=data.name;
        if(selected!=null&&data._uid==selectedData._uid){
            //修改控件
            var el = $(selected);
            var old_type =selectedData.c_type;
            var new_type =data.c_type;
            //验证类型是否发生更改
            for(var e in data){
                el.attr(e,data[e]);
            }
            $('#console').html("保存成功！");
            return;
        }
        var content = editor.getContent();
        if(content!=""){
            //验证是否已存在相同name的控件
            $("#formEdiorTemp").html(content);
            var old=  $("#formEdiorTemp [id="+data.id+"]");
            logger.info(data);
            var oid=old.attr("id");
            if((typeof (oid)!="undefined")){
                $('#console').html("已存在相同的name！");
                return false;
            }
        }
        //新增的控件
        data._uid=MD5.encode(Math.random()+"");
        var render = template.compile(template__[data.c_type].template);
        var html = render(data);
        editor.focus();
        editor.execCommand('insertHtml', html);
        return html;
    }
    exports.setTemplate = function (key, val) {
        template__[key] = val;
    }

});