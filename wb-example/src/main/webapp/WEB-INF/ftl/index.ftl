<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>OA系统</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            border: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
        }

        .header {
        }
        .button {
            width: 150px;
            height: 60px;
            background-color: #74b2e2;
            font-size: 25px;
            font-family: Helvetica, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', Arial, sans-serif;
            text-align: center;
        }

        .logo {
            height: 30px;
        }
    </style>
<#include "globalScript.ftl" />
</head>
<body>

<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
    <div class="header" region="north" height="100" showSplit="false" showHeader="false">
        <div class="logo"></div>
    </div>
    <div title="south" region="south" showSplit="false" showHeader="false" height="30">
        <div style="line-height:28px;text-align:center;cursor:default">Copyright © 重庆泰虹医药网络发展有限公司版权所有</div>
    </div>
    <div showHeader="false" region="west" width="180" maxWidth="250" minWidth="100">
        <div id="leftTree" class="mini-outlookmenu" onitemselect="onItemSelect"
             idField="u_id" parentField="p_id" textField="name" borderStyle="border:0;"></div>
    </div>
    <div title="center" region="center" bodyStyle="overflow:hidden;">
        <iframe id="mainframe" frameborder="0" name="main" style="width:100%;height:100%;" border="0"></iframe>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var tree = mini.get("leftTree");
    tree.setUrl(getServicePath("/me/module"));
    var iframe = document.getElementById("mainframe");

    function onItemSelect(e) {
        var item = e.item;
        iframe.src = item.uri;
    }

    //        //自动选中id
    //        var tree = mini.get("leftTree");
    //
    //        tree.selectNode("addRight");
</script>

</body>
</html>