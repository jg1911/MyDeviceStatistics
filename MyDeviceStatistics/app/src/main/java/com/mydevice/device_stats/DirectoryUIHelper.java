package com.mydevice.device_stats;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Helper class for Directory. It provides data in the required format - sorted, list.
 */
public class DirectoryUIHelper {

    private List<FileModel> fileList = new ArrayList<>();
    private HashMap<String, Integer> unsortedExtensionFrequencyMap = new HashMap<>();
    private long averageFileSize;

    /**
     * setter for unsortedExtensionFrequencyMap.
     *
     * @param unsortedExtensionFrequencyMap
     */
    public void setUnsortedExtensionFrequencyMap(HashMap<String, Integer> unsortedExtensionFrequencyMap) {
        this.unsortedExtensionFrequencyMap = unsortedExtensionFrequencyMap;
    }

    /**
     * getter for unsortedExtensionFrequencyMap.
     *
     * @return
     */
    public HashMap<String, Integer> getUnsortedExtensionFrequencyMap() {
        return unsortedExtensionFrequencyMap;
    }

    /**
     * getter for fileList.
     *
     * @return
     */
    public List<FileModel> getFileList() {
        return fileList;
    }

    /**
     * Setter for fileList.
     *
     * @param fileList
     */
    public void setFileList(List<FileModel> fileList) {
        this.fileList = fileList;
    }

    /**
     * Provides top biggest files.
     *
     * @param numberOfFiles
     *
     * @return List<FileDetailsElement>
     */
    public List<FileDetailsElement> getBiggestFiles(int numberOfFiles) {
        List<FileDetailsElement> fileSizeElementList = new ArrayList<>();

        List<FileModel> topMostFiles = new ArrayList<>();

        if (fileList == null || fileList.size() == 0) {
            return fileSizeElementList;
        }
        Collections.sort(fileList, new FileModel());
        if (numberOfFiles > fileList.size()) {
            topMostFiles = fileList;
        } else {
            topMostFiles = fileList.subList(0, numberOfFiles);
        }

        FileDetailsElement fileDetailsElement;
        for (FileModel fileModel : topMostFiles) {
            fileDetailsElement = new FileDetailsElement(fileModel.getFileName(), String.valueOf(fileModel.getSize()));
            fileSizeElementList.add(fileDetailsElement);
            Log.e("test>> ", "size of top 10" + fileModel.getFileName() + " size " + fileModel.getSize());
        }
        return fileSizeElementList;
    }

    /**
     * provides average file size in bytes.
     *
     * @return file size in bytes
     */
    public long getAverageFileSize() {
        long totalSize = 0;
        for (FileModel file : fileList) {
            totalSize = totalSize + file.getSize();
        }

        averageFileSize = totalSize / fileList.size();
        return averageFileSize;
    }

    public void updateExtensionFrequencyMap(String fileExtension) {

        int count = 1;
        if (unsortedExtensionFrequencyMap.containsKey(fileExtension)) {
            count = count + unsortedExtensionFrequencyMap.get(fileExtension);
        }
        unsortedExtensionFrequencyMap.put(fileExtension, count);
    }

    /**
     * provides sorted List<Object></Object> for extension frequency map.
     *
     * @param numberOfFileTypes
     *
     * @return List<FileDetailsElement>
     */
    public List<FileDetailsElement> getSortedExtensionFrequencyList(int numberOfFileTypes) {

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
        List<FileDetailsElement> sortedFileExtFreqList = new ArrayList<>();
        FileDetailsElement fileDetailsElement;

        for (Map.Entry<String, Integer> entry : list) {
            fileDetailsElement = new FileDetailsElement(entry.getKey(), String.valueOf(entry.getValue()));
            sortedFileExtFreqList.add(fileDetailsElement);
        }

        return sortedFileExtFreqList;
    }

    /**
     * Provides all the data to be shared in formatted order.
     *
     * @param numberOfBiggestFiles
     * @param numberOfExtension
     *
     * @return String
     */
    public String getSharableData(int numberOfBiggestFiles, int numberOfExtension) {
        StringBuilder dataToShare = new StringBuilder();

        dataToShare.append("\n Name and size of 10 biggest files on my device:\n");

        List<FileDetailsElement> fileSizeElementList = getBiggestFiles(numberOfBiggestFiles);

        int count = 1;
        for (FileDetailsElement fileDetailsElement : fileSizeElementList) {
            dataToShare.append("\n" + count + ".  File Name = " + fileDetailsElement.getTitle()
                    + " : File Size = " + fileDetailsElement.getvalue() + " bytes");
            count++;
        }

        dataToShare.append("\n \n Average file Size is: ").append(getAverageFileSize());
        dataToShare.append("\n \n Top 5  file extension with frequencies: \n ");

        count = 1;

        List<FileDetailsElement> sortedFileExtFreqList = getSortedExtensionFrequencyList(numberOfExtension);
        for (FileDetailsElement fileDetailsElement : sortedFileExtFreqList) {
            dataToShare.append("\n" + count + ".  File Type = " + fileDetailsElement.getTitle()
                    + " : Frequency = " + fileDetailsElement.getvalue());
            count++;
        }
        return dataToShare.toString();
    }
}
