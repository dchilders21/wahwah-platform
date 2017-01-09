package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.enums.LineItemTargetingRuleType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 6/29/16.
 */

@WebSafeModel
public class TargetableEntityModel {

    @JsonProperty("type")
    private LineItemTargetingRuleType type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("children")
    private List<TargetableEntityModel> children;

    public TargetableEntityModel(){
        children = new ArrayList<>();
    }

    public LineItemTargetingRuleType getType() {
        return type;
    }

    public void setType(LineItemTargetingRuleType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TargetableEntityModel> getChildren() {
        return children;
    }

    public void setChildren(List<TargetableEntityModel> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }

        TargetableEntityModel rhs = (TargetableEntityModel) obj;

        return new EqualsBuilder().append(type,rhs.type).append(id,rhs.id).append(name,rhs.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(id).append(name).toHashCode();
    }
}
