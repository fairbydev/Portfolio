package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public abstract class UploadFile extends AsyncTask<String, String, String>{

    Context context;
    ProgressDialog mProgressDialog;    // 진행상태 다이얼로그
    String fileName;    // 파일위치

    HttpURLConnection conn = null;
    DataOutputStream dos = null;    //  서버 전송시 데이터 작성한 뒤 전송

    String lineEnd = "\r\n";    //구분자
    String twoHyphens = "--";
    String boundary = "*****";

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024;
    File sourceFile;
    int serverResponseCode;
    String Tag = "FileUpload";



    public UploadFile(Context context){
        this.context = context;
    }

    public void setPath(String uploadFilePath){
        this.fileName = uploadFilePath;
        this.sourceFile = new File(uploadFilePath);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setMessage("사진 업로드 중 입니다...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        String success = "Success";


        if (!sourceFile.isFile()){  // 해당 위치의 파일이 있는지 검사
            Log.e(Tag, "sourceFile (" + fileName + ") is not A File");
            mProgressDialog.dismiss();
            return "Fail";
        } else {
            try {
                Log.e(Tag, "sourceFile (" + fileName + ") is A File");

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(strings[0]);
                Log.i("strings[0]", strings[0]);

                //Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);  // allow Inputs
                conn.setDoOutput(true); // allow Outputs
                conn.setUseCaches(false);   // Don't use a cached Copy
                conn.setRequestMethod("POST");  // 전송방식
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("upload_file", fileName);   // 키는 upload_file 값은 fileName
                Log.i(Tag, "fileName : " + fileName);


                //  dataoutput은 outputstream이란 클래스를 가져오며 outputStream은 FileOutputStream의 하위 클래스이다
                //  output은 쓰기 input은 읽기, 데이터를 전송할 때 내용을 적는 것으로 이해할 것

                dos = new DataOutputStream(conn.getOutputStream());

                //  사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다 하나의 인자 전달 data1 = newImage
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: from-data; name=\"data1\"" + lineEnd); // name의 \ \ 안의 인자가 php의 key
                dos.writeBytes(lineEnd);
                dos.writeBytes("newImage"); // newImage라는 값을 넘김
                dos.writeBytes(lineEnd);

                //  이미지 전송 데이터 전달 upload_file 이라는 php key 값에 저장되는 내용은 fileName
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form; name=\"upload_file\"; filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                //  create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                //  read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0){
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necessary after file data...   //별도로 추가한 부분임 : 꼭 넣어줘야한다
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i(Tag, "[UploadImageToServer HTTP RESPONSE is" + serverResponseMessage + " : " + serverResponseCode);
                if (serverResponseCode == 200){

                }

                // 결과 확인

                BufferedReader rd = null;

                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = rd.readLine()) != null){
                    Log.i("Upload State", line);
                }


                // close the streams

                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(Tag + "Error", e.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(Tag + "Error", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(Tag + "Error", e.toString());
            }
            mProgressDialog.dismiss();
            return success;
        }

    }


    @Override
    protected abstract void onPostExecute(String s);


}
