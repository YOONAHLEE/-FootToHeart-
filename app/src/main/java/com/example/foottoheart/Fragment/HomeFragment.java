package com.example.foottoheart.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foottoheart.MainActivity;
import com.example.foottoheart.R;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    static int count = 0;

    String UserId;

    int Set = 0;
    String exer_result;
    TimerTask timerTask;
    Timer timer;
    static int first_dialog = 0;
    SharedPreferences preferences;
    TextView mtoday;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragmet_home,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        count = 0;
        UserId = ((MainActivity)getActivity()).UserId;




        final ArcProgress arcProgress = (ArcProgress)getView().findViewById(R.id.fragmenthome_arcprogress);
        mtoday = (TextView)getView().findViewById(R.id.fragmenthome_textview_today);
        Calendar cal = Calendar.getInstance();
        CalendarDay cald = CalendarDay.today();

        mtoday.setText(String.format("%04d년 %02d월 %02d일",cald.getYear(),cald.getMonth()+1,cald.getDay()));


        preferences = getActivity().getSharedPreferences("monday_nosee", Context.MODE_PRIVATE);
        String  monday_nosee = preferences.getString("monday_nosee", "no");
        /*
        SharedPreferences.Editor reset = preferences.edit();
        reset.putString("monday_nosee", "no");
        reset.commit();
        */
        if(first_dialog == 0) {
            //mondaydialog();
            if (!monday_nosee.equals("yes")) {

                /* nodejs와 통신 코드 연습 */
                // 월요일에만 받아줌
                if (cal.get(Calendar.DAY_OF_WEEK) == 2) {
                    String url = "http://34.220.25.253:3000/monday/" + UserId; // 세부내용 확인
                    new JSONTask2().execute(url);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mondaydialog();
                    //monday_result.setText(exer_result);
                } else { // 화~일
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("monday_nosee", "no");
                    editor.commit();
                }
            }
        /*
            월요일일 경우 텍스트 표시
            화~일요일일 경우, 텍스트 invisible
         */
            first_dialog++;
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                //String Date = String.format("%04d-%02d-%02d", CalendarDay.today().getYear(),CalendarDay.today().getMonth()+1,CalendarDay.today().getDay());
                String url = "http://34.220.25.253:3000/users"+ "/" + UserId;

                new JSONTask().execute(url);
                if(count == -1) count = 0;
                arcProgress.setProgress(count);
                arcProgress.setBottomText("달성도 : "+ (int)(count/(float)arcProgress.getMax()*100) + "%");
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,1000);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override

        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuilder buffer = new StringBuilder();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        count = Integer.parseInt(line);

                        buffer.append(line);
                        buffer.append(System.getProperty("line.separator"));

                    }

                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }

    }

    public class JSONTask2 extends AsyncTask<String, String, String>{

        @Override

        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuilder buffer = new StringBuilder();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((exer_result = reader.readLine()) != null){
                        buffer.append(line);
                        buffer.append(System.getProperty("line.separator"));
                        break;
                    }

                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }

    }

    private void mondaydialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("저번주 운동 결과");
        //exer_result =  "지난주는 풋피트 권장 운동량을 돌파하셨습니다! 지금과 같은 페이스를 유지한다면 심혈관질병률이 45%나 감소할 것입니다!";
        //exer_result = "지난주는 풋피트 운동 달성량을 돌파하지 못했습니다! 운동 달성량을 돌파한 경우 심혈관질환 발병률이 45%나 감소할 것입니다! 이번주에는 도전해보세요!";
        StringBuffer buffer = new StringBuffer(exer_result);
        if(exer_result.length() == 83) {
            buffer.insert(16, '\n');
        }
        builder.setMessage(buffer);
        builder.setNegativeButton("이번 주 더이상 보지않기",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        preferences = getActivity().getSharedPreferences("monday_nosee", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("monday_nosee", "yes");
                        editor.commit();
                    }
                });
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

}
