<!DOCTYPE html>
<html>
<head lang="cn">
    <meta charset="UTF-8">
    <title></title>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            border: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            font-size: 15px;
            font-family: 'Trebuchet MS', Arial, sans-serif;
        }
    </style>
<#include "/WEB-INF/ftl/globalScript.ftl" />
</head>
<body>
<!--工具栏-->
<div class="mini-toolbar" style="padding:2px;border:0;" id="searchForm">
    <#if user.hasAccessModuleLevel('module','C')??>
        <a class="mini-button" iconCls="icon-add" plain="true" onclick="add()">新增</a>
    </#if>
    <#if user.hasAccessModuleLevel('module','U')??>
        <a class="mini-button" iconCls="icon-save" plain="true" onclick="save()">保存</a>
    </#if>
    <#if user.hasAccessModuleLevel('module','D')??>
        <a class="mini-button" iconCls="icon-remove" plain="true" onclick="remove()">删除</a>
    </#if>
    <span class="separator"></span>
    <a class="mini-button" iconCls="icon-reload" plain="true" onclick="grid.reload();">刷新</a>
    <a class="mini-button" iconCls="icon-print" plain="true" onclick="exportExcel(grid,'导出')">导出EXCEL</a>
    <input class="mini-textbox" name="name" onenter="search()"/>
    <a class="mini-button" onclick="search()" iconCls="icon-search" plain="true" onclick="search()">查询</a>
</div>
<div class="mini-fit" style="height:100px;" align="center">
    <div id="datagrid" class="mini-datagrid" pageSize="50" ajaxOptions="{type : 'get'}"
         style="width: 100%; height: 100%" showColumnsMenu="true" allowAlternating="true" allowCellSelect="true"
         multiSelect="true" editNextOnEnterKey="true" editNextRowCell="true" allowCellEdit="true" showfooter="false"
         contextMenu="#gridMenu">
        <div property="columns">
            <div type="checkcolumn"></div>
            <div field="u_id" name="rowid"  allowSort="false" width="50" align="center" headeralign="center" allowSort="true">标识</div>
            <div field="name" align="center" headeralign="center" width="60" allowSort="true">角色名称
                <input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
            </div>
            <div field="remark" align="center" headeralign="center" width="50" allowSort="true">备注
                <input property="editor" class="mini-textarea" style="width:100%;"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    mini.parse();
    var url = getServicePath("/role");
    var grid = mini.get("datagrid");
    grid.setUrl(url);
    grid.load({includes:mini.encode(["u_id","name","remark"])});

    function add(){
        openWindow(ROOT_PATH+"/sys/role/add.ftl","添加角色","700","70%",function(e){
            grid.reload();
        });
    }

    function save(){
        var data = grid.getChanges();
        if(data&&data.length>0){
            seajs.use("request",function(req){
                req.update("/role",data,function(e){
                    if(e.success)grid.reload();
                    else if(e.code='400') alert(mini.decode(e.data)[0].message);
                    else alert(e.data)
                });
            })
        }
    }
</script>