package com.imc.hostapp;

/**
 * @author Dsh  imkobedroid@gmail.com
 * @date 2021/3/23
 */
public class PluginBean {
    private String label;
    private String name;


    public PluginBean(String label, String name) {
        this.label = label;
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
