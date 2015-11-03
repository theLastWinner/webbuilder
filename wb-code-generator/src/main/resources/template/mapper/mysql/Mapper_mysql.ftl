<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://www.mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${config.packageName}.dao.${config.module}.${config.className}Mapper">
    <resultMap id="${config.className}ResultMap" type="${config.className}" >
        <id property="u_id" column="u_id" javaType="string" jdbcType="VARCHAR" />
    <#list fields as field >
        <result property="${field.name}" column="${field.name}" javaType="${field.javaTypeName}" jdbcType="${field.jdbcType}" />
    </#list>
    </resultMap>

    <!--字段配置-->
    <sql id="fieldConfig">
        <bind name="$fieldsType"
              value="${r'#{'}<#list fields as field ><#if field_index!=0>,</#if>${'\''+field.name+'\':\''+field.javaTypeNameSample+'\''}</#list>${r'}'}"/>
        <bind name="$fields" value="$fieldsType.keySet()"/>
    </sql>

    <!--表名-->
    <sql id="tableName">
        <bind name="$tableName" value="'${config.tableName!config.className}'"/>
    </sql>

    <insert id="insert" parameterType="${config.className}" useGeneratedKeys="true" keyProperty="u_id" keyColumn="U_ID">
        INSERT INTO ${config.tableName!config.className}
        (<#list fields as field ><#if field_index!=0>,</#if>${field.name}</#list>)
        VALUES
        (<#list fields as field ><#if field_index!=0>,</#if>${r'#{'+field.name+r',jdbcType='+field.jdbcType+'}'}</#list>)
    </insert>

    <delete id="delete" parameterType="${config.className}" >
        DELETE FROM ${config.tableName!config.className} WHERE u_id=${r'#{u_id}'}
    </delete>

    <update id="update" parameterType="${config.className}" >
        UPDATE ${config.tableName!config.className}
        <set>
        <#list fields as field >
            <#if !field.readOnly>
                <if test="${field.name} != null">
                ${field.name}=${r'#{'+field.name+r',jdbcType='+field.jdbcType+'}'},
                </if>
            </#if>
        </#list>
        </set>
        WHERE u_id=${r'#{u_id}'}
    </update>

    <select id="selectByPk" parameterType="string" resultMap="${config.className}ResultMap">
        SELECT * FROM ${config.tableName!config.className} WHERE u_id=${r'#{u_id}'}
    </select>

    <select id="select" parameterType="map" resultMap="${config.className}ResultMap">
        <include refid="fieldConfig"/>
        <include refid="tableName"/>
        <include refid="BasicMapper.selectSql"/>
    </select>

    <select id="total" parameterType="map" resultType="int">
        <include refid="fieldConfig"/>
        <include refid="tableName"/>
        <include refid="BasicMapper.totalSql"/>
    </select>
</mapper>