<!DOCTYPE html>
<html>
<head lang="cn">
    <meta charset="UTF-8">
    <title></title>
    <style type="text/css">
        html,body
        {
            width:100%;
            height:100%;
            border:0;
            margin:0;
            padding:0;
            overflow:visible;
        }
        .red{
            color: red;
        }
    </style>

</head>
<body>
<div class="mini-toolbar" style="padding:2px;border:0;">
    <table style="width:100%;">
        <tr>
            <td style="width:100%;">
                <a class="mini-button" iconCls="icon-add" plain="true" onclick="mini.get('win').show()">新建模型</a>
                <span class="separator"></span>
                <a class="mini-button" iconCls="icon-reload" onclick="grid.reload()" plain="true">刷新</a>
            </td>
        </tr>
    </table>
</div>
<!--撑满页面-->
<div class="mini-fit" style="height:100px;">
    <div id="grid" class="mini-datagrid" ajaxOptions="{type : 'get'}"  pageSize="20" style="width:100%;height:100%;" idField="id" sizeList="[5,10,20,50]" >
        <div property="columns">
            <div type="indexcolumn"></div>
            <div field="id" width="60" align="center" headerAlign="center" allowSort="false">id</div>
            <div field="name" width="120" align="center" headerAlign="center" allowSort="false">名称</div>
            <div field="key" width="120" headerAlign="center" allowSort="false">key</div>
            <div field="revision" width="100"  align="center" headerAlign="center">修订版</div>
            <div field="createTime" width="100" align="center"  headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss" allowSort="false">创建日期</div>
            <div field="lastUpdateTime" width="100"  align="center" headerAlign="center"  dateFormat="yyyy-MM-dd HH:mm:ss" allowSort="false">最后一次修改日期</div>
            <div field="action" width="100" align="center" headerAlign="center"  allowSort="false">操作</div>
        </div>
    </div>
</div>
    <div id="win" title="新建模型" class="mini-window"  showFooter="true" style="width: 300px;height: 200px">
        <table style="margin: auto;">
            <tbody>
            <tr>
                <td>key</td>
                <td><input name="key" class="mini-textbox" required="true" /></td>
            </tr>
            <tr>
                <td>名称</td>
                <td><input name="name" class="mini-textbox" required="true" /></td>
            </tr>
            <tr>
                <td>摘要</td>
                <td><input name="description" class="mini-textarea"  /></td>
            </tr>
            </tbody>
            <div property="footer" style="text-align:right;padding:5px;padding-right:15px;">
                <a class="mini-button" iconCls="icon-ok"  onclick="createModel()" style='vertical-align:middle;' >
                    创建模型
                </a>&nbsp;
                <a class="mini-button" iconCls="icon-no" onclick="mini.get('win').hide()" style='vertical-align:middle;' >
                    取消
                </a>
            </div>
        </table>
    </div>
</body>
</html>
<script type="text/javascript" charset="utf-8" src="${basePath}resources/boot.js"></script>
<script type="text/javascript" charset="utf-8" src="${basePath}plugins/miniui/root.js"></script>
<script type="text/javascript" charset="utf-8" src="${basePath}plugins/ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${basePath}plugins/ueditor/ueditor.all.min.js"></script>
<!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
<script type="text/javascript" charset="utf-8" src="${basePath}plugins/ueditor/lang/zh-cn/zh-cn.js"></script>
<script>
    mini.parse();
    var grid = mini.get("grid");
    init();
    function init() {
        grid.setUrl(getServicePath("/bpm/model/"));
        grid.load();
    }

    grid.on("drawcell",function(e){
        var row = e.record,
             column = e.column,
                field = e.field,
                value = e.value;
        if(field=='action'){
            e.cellHtml="<a href='javascript:editModel()'>编辑</a>&nbsp;";
            e.cellHtml+="<a href='javascript:deploy()' class='red'>发布</a>";
        }
    });

    function editModel(){
        var row = grid.getSelected();
        if(!row)return;
        openWindow("${basePath}bpm/model/editor.ftl?id="+row.id,"工作流设计器","80%","80%",function(e){
            grid.reload();
        });
    }

    function createModel(){
        var form = new mini.Form("#win");
        form.validate();
        if (form.isValid() == false) return;
        var data = form.getData();
        seajs.use("request",function(request){
            request.add("/bpm/model/",data,function(r){
                if(r.success){
                    openWindow("${basePath}bpm/model/editor.ftl?id="+ r.data.id,"工作流设计器","80%","80%",function(e){
                        grid.reload();
                    });
                }else{
                    mini.alert(r.data);
                }
            });
        });
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
</script>