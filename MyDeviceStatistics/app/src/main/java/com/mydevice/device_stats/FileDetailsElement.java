package com.mydevice.device_stats;

import java.io.Serializable;

/**
 * POJO class for file detail recycler view.
 */
public class FileDetailsElement implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * Title to be displayed in the row.
     */
    private final String title;

    /**
     * Value for the title.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param title
     * @param value
     */
    public FileDetailsElement(String title, String value) {
        this.title = title;
        this.value = value;
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
    public String getvalue() {
        return value;
    }
}
