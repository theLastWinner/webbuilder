package ${config.packageName}.po.${config.module};
import org.webbuilder.web.core.bean.GenericPo;

/**
* ${config.remark!''}
* Created by generator ${create_time!''}
*/
public class ${config.className} extends GenericPo<String> {
    <#list fields as field>

    //${field.remark!field.name}
    private ${field.javaTypeName} ${field.name};
    </#list>
    <#list fields as field>

    /**
    * 获取 ${field.remark}
    * @return ${field.javaTypeName} ${field.remark}
    */
    public ${field.javaTypeName} ${field.getMethodName}(){
        <#if field.javaTypeName=='java.lang.String'||field.javaTypeName=='String'>
           if(this.${field.name}==null)
              return "${field.defaultValue!''}";
        </#if>
        return this.${field.name};
    }

    /**
    * 设置 ${field.remark}
    */
    public void ${field.setMethodName}(${field.javaTypeName} ${field.name}){
        this.${field.name}=${field.name};
    }
    </#list>
}
