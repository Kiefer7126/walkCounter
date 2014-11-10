package com.m_hi.android;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.TextView;

public class WalkCounterActivity extends Activity implements OnClickListener{
	/** Called when the activity is first created. */
	WalkCounterMaster ad;
	GraphCounter thread;	
	Chronometer mChronometer;
	
	long startCounter = 0;
	long startTime = 0;
	
	MediaPlayer p;

	Timer mTimer;
	long bpm;
	/** スレッドUI操作用ハンドラ */
	private Handler mHandler = new Handler();
	/** テキストオブジェクト */

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		ad = new WalkCounterMaster(manager);
		thread = new GraphCounter(ad);
		thread.start();
		
		View button1 = findViewById(R.id.button1);
		button1.setOnClickListener(this);
		
		View button2 = findViewById(R.id.button2);
		button2.setOnClickListener(this);
		
		View button3 = findViewById(R.id.button3);
		button3.setOnClickListener(this);

		// インスタンス作成
		 mChronometer = (Chronometer) findViewById(R.id.chronometer);
		 p = MediaPlayer.create(getApplicationContext(), R.raw.b60);
		 
			// 連続再生設定
			p.setLooping(true);
		
			mTimer = new Timer(true);
			
			mTimer.schedule( new TimerTask(){
		        @Override
		        public void run() {
		            // mHandlerを通じてUI Threadへ処理をキューイング
		            mHandler.post( new Runnable() {
		                public void run() {
		                	
		                	p.pause(); // 一時停止
		                	TextView bpmTxt = (TextView) findViewById(R.id.textView7);
		                	 bpm = 6 * (ad.getCounter() - startCounter + 1) ;
		                		bpmTxt.setText("" + bpm);
		                		startCounter = ad.getCounter(); 
		                		playBPM_Music(bpm);
		                }
		            });
		        }
		    }, 0, 10000); //0msから 10000ms(10s)間隔で繰り返す
			
	}
	
    //ボタンを押した時の処理
    public void onClick(View v){
    	switch(v.getId()){
    		
    	case R.id.button1://Timer開始
    		ad.startTimer();
    		startTime = SystemClock.elapsedRealtime();
    		startCounter = ad.getCounter(); 
    		mChronometer.setBase(SystemClock.elapsedRealtime());
    		mChronometer.start();
    		break;
    		
    	case R.id.button2://Timer停止
    		ad.stopTimer();
    		mChronometer.stop();
    		mChronometer.setBase(SystemClock.elapsedRealtime());
    		p.pause(); 
    		break;
    		
    	case R.id.button3://BPM検出
    		
   		/*TextView bpmTxt = (TextView) findViewById(R.id.textView7);
    	long bpm = (60000*(ad.getCounter() - startCounter) / (SystemClock.elapsedRealtime() - startTime - 1000));
    		bpmTxt.setText("" + bpm);*/
    		    		
    		if (p.isPlaying()) {
    			p.pause(); // 一時停止
    			
    		} else {
    			p.start(); // 再生
    		}
    		
    		break;
    	}
    }
    	
    public void playBPM_Music(long bpm){
    	
    	if(bpm > 195){p = MediaPlayer.create(getApplicationContext(), R.raw.b200);}
		else if(bpm > 185){p = MediaPlayer.create(getApplicationContext(), R.raw.b190);}
		else if(bpm > 175){p = MediaPlayer.create(getApplicationContext(), R.raw.b180);}
		else if(bpm > 165){p = MediaPlayer.create(getApplicationContext(), R.raw.b170);}
		else if(bpm > 155){p = MediaPlayer.create(getApplicationContext(), R.raw.b160);}
		else if(bpm > 145){p = MediaPlayer.create(getApplicationContext(), R.raw.b150);}
		else if(bpm > 135){p = MediaPlayer.create(getApplicationContext(), R.raw.b140);}
		else if(bpm > 125){p = MediaPlayer.create(getApplicationContext(), R.raw.b130);}
		else if(bpm > 115){p = MediaPlayer.create(getApplicationContext(), R.raw.b120);}
		else if(bpm > 105){p = MediaPlayer.create(getApplicationContext(), R.raw.b110);}
		else if(bpm > 95){p = MediaPlayer.create(getApplicationContext(), R.raw.b100);}
		else if(bpm > 85){p = MediaPlayer.create(getApplicationContext(), R.raw.b90);}
		else if(bpm > 75){p = MediaPlayer.create(getApplicationContext(), R.raw.b80);}
		else if(bpm > 65){p = MediaPlayer.create(getApplicationContext(), R.raw.b70);}
		else{p = MediaPlayer.create(getApplicationContext(), R.raw.b60);}
    	
    	p.start();
    	
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ad.stopSensor();
		thread.close();
		
		p.release(); // メモリの解放
		p = null; // 音楽プレーヤーを破棄
	}

	class GraphCounter extends Thread {
		WalkCounterMaster ad;
		Handler handler = new Handler();

		TextView tvx;
		TextView tvy;
		TextView tvz;
		GraphView grx;
		GraphView gry;
		GraphView grz;
		TextView counter;
		boolean runflg = true;

		public GraphCounter(WalkCounterMaster ad) {
			this.ad = ad;
			tvx = (TextView) findViewById(R.id.lvaluex);
			tvy = (TextView) findViewById(R.id.lvaluey);
			tvz = (TextView) findViewById(R.id.lvaluez);
			grx = (GraphView) findViewById(R.id.graphViewX);
			gry = (GraphView) findViewById(R.id.graphViewY);
			grz = (GraphView) findViewById(R.id.graphViewZ);
			counter = (TextView) findViewById(R.id.textView5);
		}

		public void close() {
			runflg = false;
		}

		public void run() {
			while (runflg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						tvx.setText("" + ad.getDx());
						tvy.setText("" + ad.getDy());
						tvz.setText("" + ad.getDz());
						grx.setDiv(ad.getDx());
						gry.setDiv(ad.getDy());
						grz.setDiv(ad.getDz());
						counter.setText(""+ad.getCounter());
						grx.invalidate();
						gry.invalidate();
						grz.invalidate();
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}