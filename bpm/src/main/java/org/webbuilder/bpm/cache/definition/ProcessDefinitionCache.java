package org.webbuilder.bpm.cache.definition;

import org.activiti.engine.repository.ProcessDefinition;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-08-14 0014.
 */
public class ProcessDefinitionCache implements Serializable, ProcessDefinition {
    private String id;
    private String category;
    private String name;
    private String key;
    private String description;
    private int version;
    private String deploymentId;
    private String resourceName;
    private String diagramResourceName;
    private boolean hasStartFormKey;
    private boolean suspended;
    private String tenantId;


    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Override
    public String getDiagramResourceName() {
        return diagramResourceName;
    }

    public void setDiagramResourceName(String diagramResourceName) {
        this.diagramResourceName = diagramResourceName;
    }

    public boolean isHasStartFormKey() {
        return hasStartFormKey;
    }

    @Override
    public boolean hasStartFormKey() {
        return hasStartFormKey;
    }

    public void setHasStartFormKey(boolean hasStartFormKey) {
        this.hasStartFormKey = hasStartFormKey;
    }

    @Override
    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ProcessDefinitionCache initFromProtoType(ProcessDefinition definition) {
        ProcessDefinitionCache definitionCache = this;
        definitionCache.setName(definition.getName());
        definitionCache.setCategory(definition.getCategory());
        definitionCache.setDeploymentId(definition.getDeploymentId());
        definitionCache.setDescription(definition.getDescription());
        definitionCache.setDiagramResourceName(definition.getDiagramResourceName());
        definitionCache.setHasStartFormKey(definition.hasStartFormKey());
        definitionCache.setId(definition.getId());
        definitionCache.setResourceName(definition.getResourceName());
        definitionCache.setKey(definition.getKey());
        definitionCache.setTenantId(definition.getTenantId());
        definitionCache.setVersion(definition.getVersion());
        definitionCache.setSuspended(definition.isSuspended());
        return this;
    }

    public static ProcessDefinitionCache buildFromProtoType(ProcessDefinition definition) {
        return new ProcessDefinitionCache().initFromProtoType(definition);
    }
}
