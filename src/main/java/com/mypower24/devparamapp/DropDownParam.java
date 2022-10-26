/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.devparamapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public class DropDownParam extends DevParamSetting {
    
    private final List<DropDownOption> options = new ArrayList<>();
    
    public DropDownParam() {
    }

    public DropDownParam(DevParamSetting setting) {
        this.setParamDesc(setting.getParamDesc());
        this.setParamName(setting.getParamName());
        this.setTitle(setting.getTitle());
        this.setUnit(setting.getUnit());
//    this.setType(setting.getType());
    }
    
    public List<DropDownOption> getOptions() {
        return options;
    }
    
}
