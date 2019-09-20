package com.lenovo.celebrityapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    int chosen_celebs;
    int Locationofcorrectans;
    ArrayList<String> answer=new ArrayList<String>();
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();



   public  class DownloadTask extends AsyncTask<String,Void,String>{


       @Override
       protected String doInBackground(String... urls) {
           String result="";
           try {
               URL url=new URL(urls[0]);
               HttpURLConnection connection=(HttpURLConnection)url.openConnection();

               InputStream inputStream=connection.getInputStream();
               InputStreamReader reader=new InputStreamReader(inputStream);
               int data=reader.read();
               while(data!=-1){
                   char current=(char)data;
                   result=result+current;
                   data=reader.read();
               }
               return result;


           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

           return null;
       }
   }



   public  class ImageDownload extends AsyncTask<String,Void, Bitmap>
   {

       @Override
       protected Bitmap doInBackground(String... strings) {

           try {
               URL url=new URL(strings[0]);
               HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
               httpURLConnection.connect();
               Bitmap mybitmap;
               InputStream in=httpURLConnection.getInputStream();
               mybitmap= BitmapFactory.decodeStream(in);
               return mybitmap;



           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

           return null;
       }
   }



    public void celebChosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(Locationofcorrectans))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebNames.get(chosen_celebs), Toast.LENGTH_LONG).show();

        }
        answer.clear();
        generateques();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
         button0=findViewById(R.id.button);
        button1=findViewById(R.id.button2);
        button2=findViewById(R.id.button3);
        button3=findViewById(R.id.button4);
      //    <div id=\"sidebar\">

//<div class="sidebarContainer">
        //http://www.posh24.se/kandisar



        DownloadTask downloadTask=new DownloadTask();
        try {
            String result=downloadTask.execute("http://www.posh24.se/kandisar").get();
            String[] split=result.split("<div class=\"sidebarContainer\"");
            Pattern pattern=Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher=pattern.matcher(split[0]);
            while (matcher.find()){
                celebURLs.add(matcher.group(1));

            }

            //celensimg src
             pattern = Pattern.compile("alt=\"(.*?)\"");
                matcher = pattern.matcher(split[0]);
                while (matcher.find()) {
                    celebNames.add(matcher.group(1));
                }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        generateques();


    }

    void generateques(){

        Random rand=new Random();
        chosen_celebs=rand.nextInt(celebNames.size());
        ImageDownload imageDownload=new ImageDownload();
        Locationofcorrectans=rand.nextInt(4);
        int location_of_incorrect_answer;

        try {
            Bitmap celebsimge=imageDownload.execute(celebURLs.get(chosen_celebs)).get();
            imageView.setImageBitmap(celebsimge);
            for (int i=0;i<4;i++){
                if (i==Locationofcorrectans){

                    answer.add(celebNames.get(chosen_celebs));
                }


                else{
                    location_of_incorrect_answer=rand.nextInt(4);
                    while (location_of_incorrect_answer==chosen_celebs){
                        location_of_incorrect_answer=rand.nextInt(celebURLs.size());
                    }
                    answer.add(celebNames.get(location_of_incorrect_answer));

                }

            }


            button0.setText(answer.get(0));
            button1.setText(answer.get(1));
            button2.setText(answer.get(2));
            button3.setText(answer.get(3));


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

