package org.webbuilder.web.po.script;

import org.hibernate.validator.constraints.NotEmpty;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.bean.GenericPo;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 动态脚本
 * Created by generator
 */
public class DynamicScript extends GenericPo<String> {

    //主键
    @NotEmpty(message = "id不能为空")
    private String u_id;

    //名称
    @Pattern(regexp = "[a-zA-Z]+", message = "名称只能为大小写字母组成")
    @Size(min = 4, message = "名称长度不能少于4")
    @NotEmpty(message = "名称不能为空")
    private String name;

    //类型
    @Pattern(regexp = "(js)|(groovy)|(spel)|(ognl)", message = "类型仅支持js,groovy,spel,ognl")
    private String type;

    //内容
    private String content;

    //备注
    private String remark;

    //路径
    private String path;

    //状态
    private int status;

    /**
     * 获取 主键
     *
     * @return String 主键
     */
    public String getU_id() {
        if (this.u_id == null)
            return this.u_id = StringUtil.concat(getPath(), ".", getName(), ".", getType());
        return this.u_id;
    }

    /**
     * 设置 主键
     */
    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    /**
     * 获取 名称
     *
     * @return String 名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 类型
     *
     * @return String 类型
     */
    public String getType() {
        if (this.type == null)
            return "js";
        return this.type;
    }

    /**
     * 设置 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取 内容
     *
     * @return String 内容
     */
    public String getContent() {
        if (this.content == null)
            return "";
        return this.content;
    }

    /**
     * 设置 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取 备注
     *
     * @return String 备注
     */
    public String getRemark() {
        if (this.remark == null)
            return "";
        return this.remark;
    }

    /**
     * 设置 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取 路径
     *
     * @return String 路径
     */
    public String getPath() {
        if (this.path == null)
            return "root";
        return this.path;
    }

    /**
     * 设置 路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取 状态
     *
     * @return int 状态
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * 设置 状态
     */
    public void setStatus(int status) {
        this.status = status;
    }

}
