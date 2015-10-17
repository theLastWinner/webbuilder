package org.webbuilder.web.service.resource;

import org.webbuilder.utils.base.MD5;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.core.utils.RandomUtil;
import org.webbuilder.web.dao.resource.ResourcesMapper;
import org.webbuilder.web.po.resource.Resources;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源服务类
 * Created by generator
 *
 * @Copyright 2015 www.cqtaihong.com Inc. All rights reserved.
 * 注意：本内容仅限于重庆泰虹医药网络发展有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service
public class ResourcesService extends GenericService<Resources, String> {

    //默认数据映射接口
    @Resource
    protected ResourcesMapper resourcesMapper;

    @Override
    protected ResourcesMapper getMapper() {
        return this.resourcesMapper;
    }

    /**
     * 参照{@link GenericService#selectByPk(Object)},在此基础上增加{@link Cacheable}注解以实现缓存
     */
    @Override
    @Cacheable(value = "resources", key = "'id_'+#id")
    public Resources selectByPk(String id) throws Exception {
        return super.selectByPk(id);
    }

    /**
     * 根据资源md5 查询资源信息
     *
     * @param md5 md5值
     * @return 资源对象
     * @throws Exception
     */
    @Cacheable(value = "resources", key = "'md5_'+#md5")
    public Resources selectByMd5(String md5) throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("md5", md5);
        List<Resources> resources = this.getMapper().select(map);
        if (resources != null && resources.size() > 0)
            return resources.get(0);
        return null;
    }

    @Override
    public int insert(Resources data) throws Exception {
        data.setU_id(this.newUid(6));//6位随机id
        return super.insert(data);
    }

    public String newUid(int len) throws Exception {
        String uid = RandomUtil.randomChar(len);
        for (int i = 0; i < 10; i++) {
            if (this.selectByPk(uid) == null) {
                return uid;
            }
        }  //如果10次存在重复则位数+1
        return newUid(len + 1);
    }
}
