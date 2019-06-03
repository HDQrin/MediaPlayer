package musicplayer.cn.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;



import java.util.Timer;
import java.util.TimerTask;

import musicplayer.cn.myapplication.R;

/**
 * 欢迎页面
 *
 */
public class Launcher extends Activity {
	
	ImageView img;

	// private SharedPreferences shared;
	// private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.launcher);
		// shared = getSharedPreferences("guide", Activity.MODE_PRIVATE);
		// editor = shared.edit();
		img = (ImageView) findViewById(R.id.launcher_img);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Launcher.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		};
		timer.schedule(task, 2000);
	}

}
