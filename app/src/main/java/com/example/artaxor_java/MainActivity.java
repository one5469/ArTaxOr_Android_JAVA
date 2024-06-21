package com.example.artaxor_java;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IMAGE_URI = "imageUri";
    Uri imageUri;
    ImageView checkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectFile = findViewById(R.id.selectFile);
        checkImage = findViewById(R.id.checkImage);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                launcher_img.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> launcher_img = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) { // 사용자가 파일 선택을 성공적으로 완료했을 때 내부 코드 실행
                        Log.d("launcher_image Callback", "image picking has succeeded"); // 로그 출력

                        Intent data = result.getData(); // 콜백 메서드를 통해 전달 받은 ActivityResult 객체에서 Intent 객체 추출
                        imageUri = data.getData(); // Intent 객체에서 선택한 이미지 파일의 위치를 가리키는 Uri 추출

                        // 선택한 이미지 URI를 저장
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_IMAGE_URI, imageUri.toString());
                        editor.apply();

                        loadImage(imageUri);
                    }else if(result.getResultCode() == Activity.RESULT_CANCELED){ // 사용자가 파일 탐색 중 선택을 하지 않았을 때 내부 코드 실행
                        Log.d("launcher_image Callback", "image picking is canceled"); // 로그 출력
                    }else{ // 그 외의 경우 예외 처리
                        Log.e("launcher_image Callback", "image picking has failed"); // 로그 출력
                    }
                }
            }
    );

    private void loadImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            checkImage.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}