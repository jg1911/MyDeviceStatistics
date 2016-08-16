package com.mydevice.device_stats;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class for Directory. It provides data in the required format - sorted, list.
 */
public class DirectoryUIHelper {


    private List<FileDetailsElement> sortedSmallFileSizeList = new ArrayList<>();
    private List<FileDetailsElement> completeFileSizeList = new ArrayList<>();
    private HashMap<String, Integer> unsortedExtensionFrequencyMap = new HashMap<>();
    private List<FileDetailsElement> sortedExtensionFrequencyList = new ArrayList<>();
    private long averageFileSize;

    /**
     * getter for sortedSmallFileSizeList.
     *
     * @return
     */
    public List<FileDetailsElement> getSortedSmallFileSizeList() {
        return sortedSmallFileSizeList;
    }

    /**
     * getter for sortedExtensionFrequencyList.
     *
     * @return
     */
    public List<FileDetailsElement> getSortedExtensionFrequencyList() {
        return sortedExtensionFrequencyList;
    }

    /**
     * getter for averageFileSize.
     *
     * @return
     */
    public long getAverageFileSize() {
        return averageFileSize;
    }


    /**
     * setter for unsortedExtensionFrequencyMap.
     *
     * @param unsortedExtensionFrequencyMap
     */
    public void setUnsortedExtensionFrequencyMap(HashMap<String, Integer> unsortedExtensionFrequencyMap) {
        this.unsortedExtensionFrequencyMap = unsortedExtensionFrequencyMap;
    }

    /**
     * Setter for completeFileSizeList.
     *
     * @param completeFileSizeList
     */
    public void setCompleteFileSizeList(List<FileDetailsElement> completeFileSizeList) {
        this.completeFileSizeList = completeFileSizeList;
    }

    /**
     * Provides top biggest files.
     *
     * @param numberOfFiles
     *
     * @return List<FileDetailsElement>
     */
    public void evaluateTopBiggestFiles(int numberOfFiles) {

        sortedSmallFileSizeList.clear();

        if (completeFileSizeList.isEmpty() || completeFileSizeList.size() < 0) {
            return;
        }

        Collections.sort(completeFileSizeList, new FileDetailsElement());

        int n = Math.min(completeFileSizeList.size(), numberOfFiles);

        for (int i = 0; i < n; i++) {

            sortedSmallFileSizeList.add(completeFileSizeList.get(i));
        }
    }

    /**
     * provides average file size in bytes.
     *
     * @return file size in bytes
     */
    public void evaluateAverageFileSize() {

        long totalSize = 0;
        for (FileDetailsElement file : completeFileSizeList) {
            totalSize = totalSize + file.getValue();
        }
        averageFileSize = totalSize / completeFileSizeList.size();
    }


    /**
     * provides sorted List<Object> for extension frequency map.
     *
     * @param numberOfFileTypes
     *
     * @return List<FileDetailsElement>
     */
    public void evaluateSortedExtensionFrequencyList(int numberOfFileTypes) {

        sortedExtensionFrequencyList.clear();

        if (unsortedExtensionFrequencyMap.isEmpty() || unsortedExtensionFrequencyMap.size() < 0) {
            return;
        }

        if (numberOfFileTypes > unsortedExtensionFrequencyMap.size()) {
            numberOfFileTypes = unsortedExtensionFrequencyMap.size();
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortedExtensionFrequencyMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        list = list.subList(0, numberOfFileTypes);

        // Maintaining insertion order with the help of ArrayList
        FileDetailsElement fileDetailsElement;
        for (Map.Entry<String, Integer> entry : list) {
            fileDetailsElement = new FileDetailsElement();
            fileDetailsElement.setTitle(entry.getKey());
            fileDetailsElement.setValue(entry.getValue());
            sortedExtensionFrequencyList.add(fileDetailsElement);
        }
    }

    /**
     * Provides all the data to be shared in formatted order.
     *
     * @return String
     */
    public String getSharableData() {

        StringBuilder dataToShare = new StringBuilder();

        dataToShare.append("\n Name and size of 10 biggest files on my device:\n");
        int count = 1;
        for (FileDetailsElement fileDetailsElement : getSortedSmallFileSizeList()) {
            dataToShare.append("\n" + count + ".  File Name = " + fileDetailsElement.getTitle()
                    + " : File Size = " + fileDetailsElement.getValue() + " bytes");
            count++;
        }

        dataToShare.append("\n \n Average file Size is: ").append(getAverageFileSize());
        dataToShare.append("\n \n Top 5  file extension with frequencies: \n ");

        count = 1;

        for (FileDetailsElement fileDetailsElement : getSortedExtensionFrequencyList()) {
            dataToShare.append("\n" + count + ".  File Type = " + fileDetailsElement.getTitle()
                    + " : Frequency = " + fileDetailsElement.getValue());
            count++;
        }
        return dataToShare.toString();
    }
}
