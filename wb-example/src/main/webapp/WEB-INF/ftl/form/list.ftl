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
                <a class="mini-button" iconCls="icon-add" onclick="newForm()" plain="true">新建表单</a>
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
            <div field="u_id" width="100" align="center" headerAlign="center" allowSort="false">标识</div>
            <div field="name" width="80" align="center" headerAlign="center" allowSort="false">名称</div>
            <div field="table_name" width="120" align="center" headerAlign="center" allowSort="false">表名</div>
            <div field="remark" width="160" align="center" headerAlign="center" allowSort="false">备注</div>
            <div field="action" width="100" align="center" headerAlign="center" allowSort="false">操作</div>
        </div>
    </div>
</div>

</body>
</html>

<script>
    mini.parse();
    var grid = mini.get("grid");
    init();
    var request;
    function init() {
        grid.setUrl(getServicePath("/form"));
        seajs.use("request", function (req) {
            request=req;
        });
        grid.load({includes:mini.encode(["u_id","name","table_name","remark"])});
    }

    grid.on("drawcell", function (e) {
        var row = e.record,
                column = e.column,
                field = e.field,
                value = e.value;
        if (field == 'action') {
            var html ="<a href='javascript:edit()'>编辑</a>";
            e.cellHtml =html;
        }
    });

    function edit(){
        var row =  grid.getSelected();
        if(!row)return;
        openWindow(ROOT_PATH+"/plugins/designer/form/FormDesigner.html?id="+row.u_id,"表单设计器","80%","80%",function(e){
            grid.reload();
        });
    }
    function newForm(){
        openWindow(ROOT_PATH+"/plugins/designer/form/FormDesigner.html","表单设计器","80%","80%",function(e){
            grid.reload();
        });
    }
    function createModel() {

    }
</script>