package com.example.haoch.wocao.sensor_api;

/**
 * Created by haoch on 2017/12/26.
 */

import android.content.Context;
import android.os.Environment;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;

import java.io.File;

// this is used to {create folder + start sensor(register) + stop sensor}
public class SensingSession {

    @SuppressWarnings("unused")
    private static final String TAG = "SensingSession";

    // SensingKit
    private SensingKitLibInterface mSensingKitLib;
    private boolean isSensing = false;

    // Session Folder
    private File mSessionFolder;

    // Models
    private FileMaker mAudioLevelModelWriter;
    private FileMaker mAccelerometerModelWriter;
    private FileMaker mGravityModelWriter;
    private FileMaker mLinearAccelerationModelWriter;
    private FileMaker mGyroscopeModelWriter;
    private FileMaker mRotationModelWriter;
    private FileMaker mMagnetometerModelWriter;
    private FileMaker mBluetoothdevicedataModelWriter;
//    private FileMaker mActivityModelWriter;
    private FileMaker mAudioRecorderModelWriter;
    private FileMaker mStepdetectorModelWriter;
    private FileMaker mStepCounterModelWriter;
    private FileMaker mBatteryModelWriter;
    private FileMaker mScreenstatusModelWriter;
    private FileMaker mActivityModelWriter;



    /**
     * （0）constructor function;
     */

    // 第一步： 创建文件夹，注册sensor
    public SensingSession(final Context context, final String folderName) throws SKException {

        // Init SensingKit
        mSensingKitLib = SensingKitLib.getSensingKitLib(context);

        // Create the folder
        mSessionFolder = createFolder(folderName);  // 在最后中有定义

        // Init ModelWriters
        mAudioLevelModelWriter = new FileMaker(SKSensorModuleType.AUDIO_LEVEL, mSessionFolder, "AudioLevel");  //Audio.csv
        mAccelerometerModelWriter = new FileMaker(SKSensorModuleType.ACCELEROMETER, mSessionFolder, "Accelerometer");  //Accelerometer.csv
        mGravityModelWriter = new FileMaker(SKSensorModuleType.GRAVITY, mSessionFolder, "Gravity");
        mLinearAccelerationModelWriter = new FileMaker(SKSensorModuleType.LINEAR_ACCELERATION, mSessionFolder, "LinearAcceleration");  //LinearAcceleration.csv
        mGyroscopeModelWriter = new FileMaker(SKSensorModuleType.GYROSCOPE, mSessionFolder, "Gyroscope"); //Gyroscope.csv
        mRotationModelWriter = new FileMaker(SKSensorModuleType.ROTATION, mSessionFolder, "Rotation");  //Rotation.csv
        mMagnetometerModelWriter = new FileMaker(SKSensorModuleType.MAGNETOMETER, mSessionFolder, "Magnetometer");

        mStepCounterModelWriter = new FileMaker(SKSensorModuleType.STEP_COUNTER, mSessionFolder, "StepCounter");
        mStepdetectorModelWriter = new FileMaker(SKSensorModuleType.STEP_DETECTOR, mSessionFolder, "StepDetector");




        // Register Sensors
        mSensingKitLib.registerSensorModule(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GRAVITY);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ROTATION);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.MAGNETOMETER);

        mSensingKitLib.registerSensorModule(SKSensorModuleType.STEP_COUNTER);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.STEP_DETECTOR);


        // Subscribe ModelWriters
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.AUDIO_LEVEL, mAudioLevelModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, mAccelerometerModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.GRAVITY, mGravityModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.LINEAR_ACCELERATION, mLinearAccelerationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.GYROSCOPE, mGyroscopeModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ROTATION, mRotationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.MAGNETOMETER, mMagnetometerModelWriter);

        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.STEP_COUNTER, mStepCounterModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.STEP_DETECTOR, mStepdetectorModelWriter);



    }

    /**
     * （2）Start collect data
     */

     // start() sensing
    public void start() throws SKException {

        this.isSensing = true;

        // Start
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.GRAVITY);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ROTATION);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.MAGNETOMETER);

        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.STEP_COUNTER);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.STEP_DETECTOR);


    }

    /**
     * （2）stop sensing and flush files to make data availiable;
     */
    // Stop collect data and flush files to keep data complete;
    public void stop() throws SKException {

        this.isSensing = false;

        // Stop
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.GRAVITY);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.ROTATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.MAGNETOMETER);

        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.STEP_COUNTER);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.STEP_DETECTOR);

        // Flush
        mAudioLevelModelWriter.flush();
        mAccelerometerModelWriter.flush();
        mGravityModelWriter.flush();
        mLinearAccelerationModelWriter.flush();
        mGyroscopeModelWriter.flush();
        mRotationModelWriter.flush();
        mMagnetometerModelWriter.flush();

        mStepCounterModelWriter.flush();
        mStepdetectorModelWriter.flush();
    }

    /**
     * （2）关闭sensor，取消注册，关闭文件
     *
     */

     // 关闭，注销sensor，以及监听器
    // shutdown and stop register sensorlistener;
    public void close() throws SKException {

        // Unsubscribe ModelWriters
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.AUDIO_LEVEL, mAudioLevelModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, mAccelerometerModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.GRAVITY, mGravityModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.LINEAR_ACCELERATION, mLinearAccelerationModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.GYROSCOPE, mGyroscopeModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.ROTATION, mRotationModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.MAGNETOMETER, mMagnetometerModelWriter);

        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.STEP_COUNTER, mStepCounterModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.STEP_DETECTOR, mStepdetectorModelWriter);


        // Deregister Sensors
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.GRAVITY);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.ROTATION);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.MAGNETOMETER);

        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.STEP_COUNTER);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.STEP_DETECTOR);

        // Close
        mAudioLevelModelWriter.close();
        mAccelerometerModelWriter.close();
        mGravityModelWriter.close();
        mLinearAccelerationModelWriter.close();
        mGyroscopeModelWriter.close();
        mRotationModelWriter.close();
        mMagnetometerModelWriter.close();

        mStepdetectorModelWriter.close();
        mStepCounterModelWriter.close();

    }

    public boolean isSensing() {
        return this.isSensing;
    }


    /**
     * （3）创建app文件，在app文件下创建数据文件
     * Create app folder and create files under that folder
     */

    private File createFolder(final String folderName) throws SKException {

        // Create App folder: CrowdSensing
        File appFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroupSE/");

        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        // Create session folder
        File folder = new File(appFolder, folderName);

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        return folder;
    }

}