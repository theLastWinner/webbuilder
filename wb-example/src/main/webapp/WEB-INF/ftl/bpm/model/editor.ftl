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

</head>
<body>
<iframe id="iframe"  style="border: 0px;width: 100%;height: 100%;">

</iframe>
</body>
</html>
<script src="${basePath}resources/boot.js"></script>
<script>
    seajs.use(["jquery","request"],function($,request){
        var id = request.params.id;
        $("#iframe").attr("src",getRootPath()+"/service/editor?id="+id);
    });
</script>