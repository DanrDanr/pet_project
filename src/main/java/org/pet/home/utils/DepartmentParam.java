package org.pet.home.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/29
 **/
public class DepartmentParam {
    private String sn;
    private String name;
    private String dirPath;
    private  int state;
    private long parentId;

    public DepartmentParam() {
    }

    public DepartmentParam(String sn, String name, String dirPath, int state, long parentId) {
        this.sn = sn;
        this.name = name;
        this.dirPath = dirPath;
        this.state = state;
        this.parentId = parentId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "DepartmentParam{" +
                "sn='" + sn + '\'' +
                ", name='" + name + '\'' +
                ", dirPath='" + dirPath + '\'' +
                ", state=" + state +
                ", parentId=" + parentId +
                '}';
    }
}
