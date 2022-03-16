package com.example.pdfeditormadtpeeps.Model;

import java.io.File;
import java.io.Serializable;

public class FileData implements Serializable {
    String name;
    String duration;
    String file_type;
    File file_path;
    Boolean isSelected=false;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public File getFile_path() {
        return file_path;
    }

    public void setFile_path(File file_path) {
        this.file_path = file_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
}
