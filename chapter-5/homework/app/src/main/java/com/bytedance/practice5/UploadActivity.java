package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.Message;
import com.bytedance.practice5.model.MessageListResponse;
import com.bytedance.practice5.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONObject;
import org.json.JSONArray;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMessageWithURLConnection();
//                submit();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-android-camp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        MultipartBody.Part coverPart = MultipartBody.Part.createFormData("image",
                "cover.png",
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        coverImageData));

        MultipartBody.Part coverPart_from = MultipartBody.Part.createFormData("from", "瞿皓阳");

        MultipartBody.Part coverPart_to = MultipartBody.Part.createFormData("to", to);

        MultipartBody.Part coverPart_content = MultipartBody.Part.createFormData("content", content);

        Call<UploadResponse> message = api.submitMessage("3190101095",""
                ,coverPart_from,coverPart_to,coverPart_content,coverPart
                ," WkpVLWJ5dGVkYW5jZS1hbmRyb2lk");
        message.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UploadActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                else{
                    Toast.makeText(UploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(UploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    // TODO 7 选做 用URLConnection的方式实现提交

    private void submitMessageWithURLConnection(){
        //这一版可以用了
        //虽然迟了一天不过最后能把这个写出来还是非常高兴的
        new Thread(new Runnable() {
            @Override
            public void run() {
                String end = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                byte[] coverImageData = readDataFromUri(coverImageUri);
                if (coverImageData == null || coverImageData.length == 0) {
                    Toast.makeText(UploadActivity.this, "封面不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                String to = toEditText.getText().toString();
                if (TextUtils.isEmpty(to)) {
                    Toast.makeText(UploadActivity.this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = contentEditText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(UploadActivity.this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( coverImageData.length >= MAX_FILE_SIZE) {
                    Toast.makeText(UploadActivity.this, "文件过大", Toast.LENGTH_SHORT).show();
                    return;
                }
                String urlStr = String.format("https://api-android-camp.bytedance.com/zju/invoke/messages?student_id=3190101095&extra_value=");
                try{
                    URL url = new URL(urlStr);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("token","WkpVLWJ5dGVkYW5jZS1hbmRyb2lk");
                    conn.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);

                    byte[] end_data = ("\r\n--"+boundary+"--\r\n").getBytes();

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    os.write((end+boundary+end).getBytes("UTF-8"));
                    String data = twoHyphens + boundary + end;
                    data = data + "Content-Disposition: form-data; name=\"from\""+ end + end;
                    data = data + "瞿皓阳" + end;
//                    os.write(data.getBytes("UTF-8"));
                    data += twoHyphens + boundary +end;
                    data += "Content-Disposition: form-data; name=\"to\""+end + end;
                    data += to + end;
//                    os.write(data.getBytes("UTF-8"));
                    data += twoHyphens + boundary +end;
                    data += "Content-Disposition: form-data; name=\"content\""+end + end;
                    data += content + end;
                    data += twoHyphens + boundary +end;
//                    os.write(data.getBytes("UTF-8"));

                    data += "Content-Disposition: form-data; name=\"image\"; filename=\"cover.png\""+end;
                    data += "Content-Type: image/png"+end+end;
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"cover.png\""+end);
//                    sb.append("Content-Type: image/png"+end+end);
//                    os.write(data.getBytes("UTF-8"));
                    os.write(data.getBytes("UTF-8"));
                    os.write(coverImageData);
                    os.write((end+twoHyphens+boundary+twoHyphens+end).getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    UploadResponse result = new Gson().fromJson(reader,new TypeToken<UploadResponse>(){}.getType());
                    Log.d("post", "run: "+result);

                    if(conn.getResponseCode() == 200){
//                        Toast.makeText(UploadActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
//                        Toast.makeText(UploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
//                Toast.makeText(this,"获取失败",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
//            Toast.makeText(this,"网络异常"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
