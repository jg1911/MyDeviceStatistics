package com.mydevice.device_stats;

import java.io.Serializable;
import java.util.Comparator;

/**
 * POJO class for file detail recycler view.
 */
public class FileDetailsElement implements Serializable, Comparator<FileDetailsElement> {
    private static final long serialVersionUID = 1L;


    /**
     * Title to be displayed in the row.
     */
    private String title;


    /**
     * Value for the title.
     */
    private long value;

    /**
     * Constructor.
     */
    public FileDetailsElement() {

    }

    /**
     * Returns title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns value.
     *
     * @return value
     */
    public long getValue() {
        return value;
    }

    /**
     * Setter for title.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter for value.
     *
     * @param value
     */
    public void setValue(long value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "\n" + ">> FilePropertyTitle = " + title + "  FilePropertyValue = " + value + "\n";

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileDetailsElement))
            return false;
        if (obj == this)
            return true;

        FileDetailsElement other = (FileDetailsElement) obj;
        return (title == other.title || (title != null && title.equals(other.getTitle())));
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
    public int compare(FileDetailsElement file1, FileDetailsElement file2) {

        if (file1.getValue() > file2.getValue()) {
            return -1;
        } else if (file1.getValue() < file2.getValue()) {
            return 1;
        } else {
            return 0;
        }
    }
}
