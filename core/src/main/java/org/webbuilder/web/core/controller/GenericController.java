package org.webbuilder.web.core.controller;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.*;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用Controller，使用RESTful和json进行数据提交及访问。
 * 如果要进行权限控制，可以在方法上注解{@link Authorize}
 * <br/>所有Controller应继承改类，并手动注解@Controller 以及@RequestMapping
 * <br/>json解析使用fastJson
 * Created by 周浩 on 2015-07-28 0028.
 */
public abstract class GenericController<PO extends GenericPo<PK>, PK> implements Serializable {

    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取此Controller 需要的服务类
     *
     * @return
     */
    protected abstract GenericService<PO, PK> getService();

    /**
     * 获取PO的类型
     *
     * @return PO类型
     */
    protected final Class<PO> getPOType() {
        return (Class<PO>) ClassUtil.getGenericType(this.getClass(), 0);
    }

    /**
     * 获取PK(主键)的类型
     *
     * @return PK(主键)类型
     */
    protected final Class<PK> getPKType() {
        return (Class<PK>) ClassUtil.getGenericType(this.getClass(), 1);
    }

    /**
     * 查询列表,并返回查询结果
     *
     * @param pageUtil 分页工具实体，由SpringMvc自动填充其属性，具体参数见{@link PageUtil}
     * @return 直接返回查询结果实体交由fastJson进行json序列化，
     * 当发生错误时，不会抛出异常，而是返回{@link ResponseMessage}实体
     */
    @RequestMapping(method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("查询列表")
    public ResponseData list(@JsonParam PageUtil pageUtil) {
        // 获取条件查询
        try {
            Object data;
            if (!pageUtil.isPaging())//不分页
                data = getService().select(pageUtil.params());
            else
                data = getService().selectPager(pageUtil);
            return new ResponseData(data).includes(getPOType(), pageUtil.getIncludesArray())
                    .excludes(getPOType(), pageUtil.getExcludesArray());
        } catch (Exception e) {
            return new ResponseData(new ResponseMessage(false, e));
        }
    }

    /**
     * 根据id（主键）查询数据
     *
     * @param id 主键
     * @return 请求结果
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("查询明细")
    public Object info(@PathVariable("id") PK id) {
        try {
            PO po = getService().selectByPk(id);
            if (po == null)
                return new ResponseMessage(false, "data is not found!", "404");
            return new ResponseMessage(true, po);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }


    /**
     * 根据查询条件，查询数据数量
     *
     * @param pageUtil 查询条件
     * @return 请求结果
     */
    @RequestMapping(value = "/total", method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("查询总数")
    public Object total(@JsonParam PageUtil pageUtil) {
        try {
            // 获取条件查询
            Map<String, Object> map = pageUtil.getQueryMap();
            return new ResponseMessage(true, getService().total(map));
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 请求添加数据，请求必须以POST方式，必要参数为：json
     *
     * @param object 前端请求的对象
     * @return 请求结果
     */
    @RequestMapping(method = RequestMethod.POST, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("新增")
    public Object add(@RequestBody PO object) {
        try {
            object.valid();
            getService().insert(object);
            return new ResponseMessage(true, object.getU_id());
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 请求删除指定id的数据，请求方式为DELETE，使用rest风格，如请求 /delete/1 ，将删除id为1的数据
     *
     * @param id 要删除的id标识
     * @return 请求结果
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("删除")
    public Object delete(@PathVariable("id") PK id) {
        try {
            int i = getService().delete(id);
            return new ResponseMessage(i > 0, i);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 请求更新数据，请求必须以PUT方式
     *
     * @param object 前端请求的对象
     * @return 请求结果
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("修改")
    public Object update(@PathVariable("id") PK id, @RequestBody(required = true) PO object) {
        try {
            object.setU_id(id);
            getService().update(object);
            return new ResponseMessage(true, "更新成功!");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 请求更新数据，请求必须以PUT方式，必要参数为：json
     *
     * @param json 前端请求的对象
     * @return 请求结果
     */
    @RequestMapping(method = RequestMethod.PUT, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    @AccessLogger("修改")
    public Object update(@RequestBody(required = true) String json) {
        try {
            if (json.startsWith("[")) {
                List<PO> datas = JSON.parseArray(json, getPOType());
                getService().update(datas);
            } else if (json.startsWith("{")) {
                PO data = JSON.parseObject(json, getPOType());
                getService().update(data);
            }
            return new ResponseMessage(true, "更新成功!");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }
}
