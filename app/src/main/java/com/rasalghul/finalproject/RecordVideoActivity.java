package com.rasalghul.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rasalghul.finalproject.Bean.PostVideoResponse;
import com.rasalghul.finalproject.network.IMiniDouyinService;
import com.rasalghul.finalproject.utils.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.rasalghul.finalproject.utils.Utils.MEDIA_TMP_IMAGE;
import static com.rasalghul.finalproject.utils.Utils.MEDIA_TMP_VIDEO;

public class RecordVideoActivity extends AppCompatActivity {


    private VideoView videoView;
    private TextView upload;
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_CAMERA = 101;

    private static String IMG_TMP_PATH;
    private static String VID_TMP_PATH;
    private static String TAG = "DOUYIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);


        upload = findViewById(R.id.video_upload);
        videoView = findViewById(R.id.img);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        IMG_TMP_PATH = mediaStorageDir.getPath() + File.separator + MEDIA_TMP_IMAGE;
        VID_TMP_PATH = mediaStorageDir.getPath() + File.separator + MEDIA_TMP_VIDEO;

        videoView.setVideoURI(Uri.parse(VID_TMP_PATH));

        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    isPlaying = false;
                } else {
                    videoView.start();
                    isPlaying = true;
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://test.androidcamp.bytedance.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                IMiniDouyinService request = retrofit.create(IMiniDouyinService.class);

                retriever.setDataSource(RecordVideoActivity.this.getBaseContext(),Uri.parse(VID_TMP_PATH));
                //retriever.setDataSource(VID_TMP_PATH,new HashMap<String, String>());
                File cover_image = new File(IMG_TMP_PATH);
                try {
                    FileOutputStream out = new FileOutputStream(cover_image);
                    retriever.getFrameAtTime().compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Uri uri = Uri.fromFile(cover_image);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MultipartBody.Part part1 = getMultipartFromPath("cover_image",IMG_TMP_PATH);
                MultipartBody.Part part2 = getMultipartFromPath("video",VID_TMP_PATH);

                Call<PostVideoResponse> call = request.createVideo("16061009","HONGYULI",part1,part2);
                call.enqueue(new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        Toast.makeText(RecordVideoActivity.this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: Upload Successfully!" );
                    }

                    @Override
                    public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                        Toast.makeText(RecordVideoActivity.this, "Upload Fail!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: Upload Failed!" );

                    }
                });
            Intent intent = new Intent(RecordVideoActivity.this, MainActivity.class);
            startActivity(intent);
            }
        });

    }


    private MultipartBody.Part getMultipartFromPath(String name, String Path) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 1);

        File f = new File(Path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }


}
