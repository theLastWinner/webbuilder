<!DOCTYPE html>
<html>
<head lang="cn">
    <meta charset="UTF-8">
    <title></title>
    <style type="text/css">
        html, body {
            width: 100%;
            height: 100%;
            border: 0;
            margin: 0;
            padding: 0;
            overflow: visible;
        }
    </style>
<#include "/WEB-INF/ftl/globalScript.ftl"/>
</head>
<body>
<div class="mini-toolbar" style="padding:2px;border:0;">
    <table style="width:100%;">
        <tr>
            <td style="width:100%;">
                <a class="mini-button" iconCls="icon-reload" onclick="grid.reload()" plain="true">刷新</a>
            </td>
        </tr>
    </table>
</div>
<!--撑满页面-->
<div class="mini-fit" style="height:100px;">
    <div id="grid" class="mini-datagrid" ajaxOptions="{type : 'get'}" pageSize="20" style="width:100%;height:100%;"
         idField="id" sizeList="[5,10,20,50]">
        <div property="columns">
            <div type="indexcolumn">序号</div>
            <div field="id" width="60" align="center" headerAlign="center" allowSort="false">编号</div>
            <div field="name" width="120" align="center" headerAlign="center" allowSort="false">任务名称</div>
            <div field="createDate" width="100" align="center" headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss"
                 allowSort="false">创建日期
            </div>
            <div field="action" width="100" align="center" headerAlign="center" allowSort="false">操作</div>
        </div>
    </div>
</div>

</body>
</html>
<script type="text/javascript" charset="utf-8" src="../../plugins/miniui/root.js"></script>
<script>
    mini.parse();
    var grid = mini.get("grid");
    init();
    function init() {
        grid.setUrl(getServicePath("/bpm/process/todo/"));
        grid.load({includes:mini.encode(["reson","request_name"])});
    }

    grid.on("drawcell", function (e) {
        var row = e.record,
                column = e.column,
                field = e.field,
                value = e.value;
        if (field == 'action') {
            var html ="<a href='javascript:doTask()'>办理</a>";
            html+= "&nbsp;&nbsp;<a href='javascript:diagram()'>查看流程图</a>";
            e.cellHtml =html;
        }
    });

    function diagram() {
        var row = grid.getSelected();
        if (!row) {
            return;
        }
        openWindow(getRootPath() + "/diagram-viewer/index.html?processDefinitionId=" + row.definition.id + "&processInstanceId=" + row.processInstanceId, "查看流程图", "80%", "80%", function (e) {
        });
    }

    function doTask() {
        var row = grid.getSelected();
        if (!row)return;
        openWindow("do.html?id=" + row.id, "办理", "80%", "80%", function (e) {
            grid.reload();
        });
    }

    function createModel() {

    }

    // 开启iframeWindow
    function openWindow(url, title, width, height, ondestroy) {
        mini.open({
            url: url,
            showMaxButton: true,
            title: title,
            width: width,
            height: height,
            ondestroy: ondestroy
        });
    }
</script>