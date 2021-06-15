package com.teamnova.jaepark.travelmate.activities.Chat;

import java.io.File;


public interface OnFileListener {

    public void onCompleteDownload(File file);
    public void onCompleteUpload();
    public void onDownloading(int percent);
    public void onUploading(int percent);

}
