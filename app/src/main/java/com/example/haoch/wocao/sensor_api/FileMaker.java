package com.example.haoch.wocao.sensor_api;

import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.data.SKSensorData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by haoch on 2018/1/1.
 */

public class FileMaker implements SKSensorDataListener {

    @SuppressWarnings("unused")
    private static final String TAG = "FileMaker";
    private final SKSensorModuleType moduleType;
    private File mFile;
    private BufferedOutputStream mFileBuffer;
    private String action = "SensorData";

    /**
     * （1）主函数，根据目录和文件名创建csv文件，以及生成文件的mFileBuffer管道
     *  Constructor, Create a folder and CSV file name. and gengerate a mFileBuffer(used to write into somethoing)
     */

    public FileMaker(SKSensorModuleType moduleType, File sessionFolder, String filename) throws SKException {

        this.moduleType = moduleType;
        this.mFile = createFile(sessionFolder, filename);   //Create file(folder name = sessionFolder, file name)创建文件（目录为sessionFolder，文件为filename.csv)

        // Open BufferedOutputStream, which can be used to write into many files at the same time;
        try {
            this.mFileBuffer = new BufferedOutputStream(new FileOutputStream(mFile));  //BufferedOutputStream它就是内部的缓存，可以批量的读写文件
        }
        catch (FileNotFoundException ex) {
            throw new SKException(TAG, "File could not be found.", SKExceptionErrorCode.UNKNOWN_ERROR);
        }

    }

    /**
     * （2）将（1）的mfileBuffer冲洗一遍，清理数据，让剩余数据都到文件中去；
     * (2) flush() is used to clear over the bufferedOutputStream() and save additional data to their file;
     */

    public void flush() throws SKException {

        try {
            mFileBuffer.flush();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * （3）关闭mFileBuffer管道
     * (3) close up mFileBuffer
     */

    public void close() throws SKException {

        try {
            mFileBuffer.close();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }


    /**
    * （4）创建file的函数
     * (4) createFile() us used to create csv file
     */
    private File createFile(File sessionFolder, String filename) throws SKException {

        File file = new File(sessionFolder, filename + ".csv");  // 在sessionFolder目录下创建名为csv的文件名

        try {
            if (!file.createNewFile()) {
                throw new SKException(TAG, "File could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }

        // Make file visible
        //MediaScannerConnection.scanFile(getBaseContext(), new String[]{file.getAbsolutePath()}, null, null);

        return file;
    }

    /**
     * （5）重写onDataReceived方法
     * Write data into file
     */

    @Override
    public void onDataReceived(SKSensorModuleType moduleType, SKSensorData moduleData) {

//        String real_type = moduleType.toString();
//        HashMap<String, SKSensorData> sendSensorData = new HashMap<String, SKSensorData>();
//        sendSensorData.put(real_type, moduleData);
//        Intent send = new Intent(action);
//        send.putExtra("map_from_sensor", sendSensorData);
//        sendBroadcast(send);

        if (mFileBuffer != null) {

            // Build the data line
            String dataLine = moduleData.getDataInCSV() + "\n";

            // Write in the FileBuffer
            try {
                mFileBuffer.write(dataLine.getBytes());
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

}
