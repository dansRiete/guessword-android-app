
package com.alexsoft.datamodel;

public class Condition
{
    private String text;

    private String icon;

    private int code;

    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return this.text;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }
    public String getIcon(){
        return this.icon;
    }
    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
