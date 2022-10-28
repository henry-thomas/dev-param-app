/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.devparamapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public class DevParamSetting {

    protected String paramName = "";
    protected String title = "";
    protected String paramDesc = "";
    protected String type = "";
    protected String unit = "";
    protected String group = "";
    protected String register = "";
    private List<DropDownOption> options;
    @JsonIgnore
    protected boolean dirty = false;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.equals("dropDown") && options == null) {
            options = new ArrayList<>();
        } else if(options != null && !type.equals("dropDown")){
            options = null;
        }
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public List<DropDownOption> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "DevParamSetting{" + "paramName=" + paramName + ", title=" + title + ", paramDesc=" + paramDesc + ", type=" + type + ", unit=" + unit + ", group=" + group + ", register=" + register + ", dirty=" + dirty + '}';
    }

}
