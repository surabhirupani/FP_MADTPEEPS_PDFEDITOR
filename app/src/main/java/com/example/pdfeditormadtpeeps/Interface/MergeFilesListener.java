package com.example.pdfeditormadtpeeps.Interface;

public interface MergeFilesListener {
    void resetValues(boolean isPDFMerged, String path);
    void mergeStarted();
}
