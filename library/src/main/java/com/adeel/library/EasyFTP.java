package com.adeel.library;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple FTPCeint WRAPPER CLASS to perform basic Upload/Download Operations
 * Author : ADEEL AHMAD
 */

public class EasyFTP {

    private static final String TAG = "EasyFTP";

    private FTPClient mFtpClient =null;

    public EasyFTP(){
        mFtpClient=new FTPClient();
        this.mFtpClient.setConnectTimeout(10*1000);
    }


    public void useCompressedTransfer (){
        try {
            mFtpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.COMPRESSED_TRANSFER_MODE);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String [] listName() {
        try {
            return mFtpClient.listNames();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FTPFile[] listFiles() {
        try {
            return mFtpClient.listFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean setWorkingDirectory (String dir) {
        try {
            return mFtpClient.changeWorkingDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getWorkingDirectory() {
        try {
            return mFtpClient.printWorkingDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FTPClient getFtpClient() {return mFtpClient;}

    public void setTimeout (int seconds) throws  Exception{
        try {
            mFtpClient.setConnectTimeout(seconds * 1000);
        }catch (Exception e){
            throw e;
        }
    }

    public boolean makeDir(String dir) throws IOException {
        return mFtpClient.makeDirectory(dir);
    }

    public void disconnect(){
        try {
            mFtpClient.disconnect();
        }catch (Exception e ){
            Log.e(TAG, "Error disconnecting", e);
        }
    }

    public boolean connect(String ip, String userName, String pass) {
        boolean status = false;
        try {
            mFtpClient.connect(ip);
            status = mFtpClient.login(userName, pass);
            Log.d(TAG, "Connected: " + status);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to " + userName + "@" + ip);
            return false;
        }
        return status;
    } 

 //Passing Local File path/Uri
   public void uploadFile(String uri,String name) throws  IOException {
       File file = new File(uri);
       FileInputStream srcFileStream = new FileInputStream(file);
       uploadFile(srcFileStream, name);
    }

//Passing InputStream and fileName
    public void uploadFile(InputStream srcFileStream, String name) throws  IOException {
        mFtpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        boolean status = mFtpClient.storeFile(name, srcFileStream);
        Log.d(TAG, "File upload result: " + status);
        silentClose(srcFileStream);
    }

    public void downloadFile(String remoteFilePath, String dest)  throws IOException {
        File downloadFile = new File(dest);
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        mFtpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        boolean status = mFtpClient.retrieveFile(remoteFilePath, outputStream);
        Log.e(TAG, "File uploaded as: " + dest + " (" + status + ")");
        outputStream.flush();
        silentClose(outputStream);
    }

    private void silentClose(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                /* no-op */
            }
        }
    }
}


