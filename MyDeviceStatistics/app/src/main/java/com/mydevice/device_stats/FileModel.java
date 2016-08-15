package com.mydevice.device_stats;

import java.io.Serializable;
import java.util.Comparator;

/**
 * POJO for reading file from directory.
 */
public class FileModel implements Comparator<FileModel>, Serializable {

    private String fileName;
    private String fileExtension;
    private long size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "\n" + ">> FileName = " + fileName + "  FileSize = " + size + "\n";

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileModel))
            return false;
        if (obj == this)
            return true;

        FileModel other = (FileModel) obj;
        return (fileName == other.fileName || (fileName != null && fileName.equals(other.getFileName())));
    }

    /**
     * used to sort list of file based upon size property.
     *
     * @param file1
     * @param file2
     *
     * @return
     */
    @Override
    public int compare(FileModel file1, FileModel file2) {

        if (file1.getSize() > file2.getSize()) {
            return -1;
        } else if (file1.getSize() < file2.getSize()) {
            return 1;
        } else {
            return 0;
        }
    }

    public String extension() {
        int indexOfPerion = fileName.lastIndexOf(".");
        return fileName.substring(indexOfPerion + 1);
    }

    // gets filename without extension
    public String filename() {
        int indexOfPerion = fileName.lastIndexOf(".");
        return fileName.substring(0, indexOfPerion);
    }
}
