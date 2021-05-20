package com.cansaban.coolapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CatchTheBunny extends AppCompatActivity {

    TextView timeText;
    TextView scoreText;
    int score;
    ImageView imageView;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;
    ImageView imageView10;
    ImageView imageView11;
    ImageView imageView12;
    ImageView imageView13;
    ImageView imageView14;
    ImageView imageView15;
    ImageView[] imageArray;
    Runnable runnable;
    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_the_bunny);

        timeText = findViewById(R.id.timeText);
        scoreText = findViewById(R.id.scoreText);
        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageView10 = findViewById(R.id.imageView10);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        imageView13 = findViewById(R.id.imageView13);
        imageView14 = findViewById(R.id.imageView14);
        imageView15 = findViewById(R.id.imageView15);
        imageArray = new  ImageView[] {imageView,imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9,imageView10,imageView11,imageView12,imageView13,imageView14,imageView15};

        hideImages();


        score = 0;

        new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeText.setText("Kalan Zaman : " +millisUntilFinished/1000);

            }

            @Override
            public void onFinish() {
                timeText.setText("Zaman Bitti");
                handler.removeCallbacks(runnable);
                for(ImageView image : imageArray) {
                    image.setVisibility(View.INVISIBLE);
                }
                if (score == 0){
                    Toast.makeText(CatchTheBunny.this,"Skor Alamadınız",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(CatchTheBunny.this,"Tebrikler Skorunuz:"+score,Toast.LENGTH_LONG).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(CatchTheBunny.this);
                alert.setTitle("Tekrar Oyna?"); //uyarı mesajı başlığı
                alert.setMessage("Tekrar oynamak istedigine emin misin?"); // uyarı messajı
                alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Restart
                        Intent intent = getIntent(); // güncel aktiviteyi bitirecek aynı aktiviteyi baştan başlatacak bu 3 satır(uygulamayı yormamak için finish)
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(CatchTheBunny.this,"Oyun Bitti",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        }.start();


    }

    public void SkoruArtir(View view){ //ImageView'lara onclick metodu eklenerek resme tıklama yapıldığında işlem yapılıyor

        score++;
        scoreText.setText("Skor: " + score);

    }

    public  void hideImages(){
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                for(ImageView image : imageArray) {
                    image.setVisibility(View.INVISIBLE);
                }
                Random random = new Random();
                int i = random.nextInt(16); // 0 ile 15 arası index var resim sayımız 16
                imageArray[i].setVisibility(View.VISIBLE);

                handler.postDelayed(this,500); // bir runnable çalıştır ve rötarlı çalıştır (söyledğim periyotta)

            }
        };

        handler.post(runnable);


    }
}
