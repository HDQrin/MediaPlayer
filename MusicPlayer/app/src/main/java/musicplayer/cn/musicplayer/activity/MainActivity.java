package musicplayer.cn.musicplayer.activity;

/*
* 我的音乐主页面
* */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import musicplayer.cn.musicplayer.constant.PlayerFinal;
import musicplayer.cn.musicplayer.customview.MyHSV;
import musicplayer.cn.musicplayer.db.MusicDBHelper;
import musicplayer.cn.musicplayer.entity.MusicInfo;
import musicplayer.cn.musicplayer.module.Member;
import musicplayer.cn.musicplayer.service.OnPlayerStateChangeListener;
import musicplayer.cn.musicplayer.service.PlayerService;
import musicplayer.cn.musicplayer.util.Result;
import musicplayer.cn.myapplication.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * 主界面，有侧滑菜单
 *
 */
public class MainActivity extends Activity {
	/** 自定义滚动条 **/
	public static MyHSV myHSV;
	/** 滚动条的子布局对象 **/
	private LinearLayout myHSV_Linear;
	/** 第二页布局视图对象 **/
	private View mainView, onlineMusicView, aboutView,tologinview,quitloginview;//改
	private TextView txtArg;
	/** View数组，加入子视图 **/
	private View[] mainView_Children;
	/** 保存左边布局的宽度 */
	private int leftWidth;
	/** 保存右边布局的宽度 */
	private int rightWidth;

	/** 滚动参数 **/
	private boolean flagMove = false;
	public static int offset = 0;

	private ScrollView slideMenu;
	private ImageButton main_actionbar_menu, pop_menu,online_actionbar_menu;

	// 侧滑菜单跳转view
	private LinearLayout myMusic, userunlogin,userlogin/*改*/,onlineMusic, setting, setting_set, quit;
	private RelativeLayout miniPlayer;
	// 我的音乐界面点击跳转view
	private LinearLayout localMusic, folder, artist, album, download, favor,
			playlist;
	private TextView tv_localMusic, tv_folder, tv_artist, tv_album,
			tv_download, tv_favor, tv_playlist;
	// miniplayer中的控件
	private TextView title_mini, artist_mini;
	private ImageView album_mini;
	private ImageButton play, next;

	// 用于开启服务
	private Intent service;
	// 歌曲播放状态改变的监听器
	// 监听器改变UI
	private OnPlayerStateChangeListener stateChangeListener;
	// 网络歌曲列表
	private ArrayList<MusicInfo> musicList;
	private ListView onlineLv;
	private View bottom;
	private Handler onlineHandler;
	private int curPage = 1;
	private int totalPage = 0;
	private int firstItem, lastItem;
	private Runnable runnable;
	private LinearLayout online_loading;
	private ImageView online_error_img;

	//登录名，密码
    private String userName,userPassword;
	private void initView() {
		// TODO Auto-generated method stub
		// 自定义
		myHSV = (MyHSV) findViewById(R.id.main_myHSV);
		// 第二布局中的linearlaout
		myHSV_Linear = (LinearLayout) findViewById(R.id.main_myHSV_linear);
		// 第一布局的linearlayout
		slideMenu = (ScrollView) findViewById(R.id.main_scrollview);
		// 动态生成一个TextView
		txtArg = new TextView(this);
		// 第二布局
		mainView = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
		tologinview = LayoutInflater.from(this).inflate(R.layout.activity_login,null);//改
		quitloginview = LayoutInflater.from(this).inflate(R.layout.fragment_mine,null);
		onlineMusicView = LayoutInflater.from(this).inflate(R.layout.online_music, null);
//		aboutView = LayoutInflater.from(this).inflate(R.layout.about, null);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.slide_menu_main);
		
		initView();
		setSlideMenu();
		setMainView(mainView);
		// 启动service
		service = new Intent(this, PlayerService.class);
		startService(service);
		// 监听器改变UI
		stateChangeListener = new OnPlayerStateChangeListener() {

			@Override
			public void onStateChange(int state, int mode,
					List<MusicInfo> musicList, int position) {
				// TODO Auto-generated method stub
				setMainView(mainView);
				// 更改当前界面UI
				if (musicList != null) {
					Log.e(PlayerFinal.TAG, "回调主界面当前UI！！！！！！！！！！");
					if (title_mini == null) {
						Log.e(PlayerFinal.TAG, "找不到title");
					}
					title_mini.setText(musicList.get(position).getTitle());
					Log.e(PlayerFinal.TAG, musicList.get(position).getTitle()
							+ "!!!!!");
					artist_mini.setText(musicList.get(position).getArtist());
					if (musicList.get(position).getAlbum_img_path() != null) {
						Uri uri = Uri.parse(musicList.get(position)
								.getAlbum_img_path());
						album_mini.setImageURI(uri);
					}
				} else {
					title_mini.setText("欢迎来到我的音乐");
					artist_mini.setText("让音乐跟我走");
				}
				switch (state) {
				case PlayerFinal.STATE_PLAY:
					play.setImageResource(R.drawable.player_pause);
					break;
				case PlayerFinal.STATE_CONTINUE:
					play.setImageResource(R.drawable.player_pause);
					break;
				case PlayerFinal.STATE_PAUSE:
					play.setImageResource(R.drawable.player_play);
					break;
				case PlayerFinal.STATE_STOP:
					play.setImageResource(R.drawable.player_play);
					break;
				}
			}
		};
	}
	/**
	 * 设置侧滑菜单监听事件
	 */
	private void setSlideMenu() {
		// TODO Auto-generated method stub
		myMusic = (LinearLayout) this.findViewById(R.id.slide_menu_local_music);
		myMusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
				onGlobalLayout(mainView);
				setMainView(mainView);
			}
		});
		userunlogin = (LinearLayout)this.findViewById(R.id.user_unlogin);
		userunlogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int width = mainView_Children[0].getMeasuredWidth();
                moveScrollList(width);
                onGlobalLayout(tologinview);
				setLoginView(tologinview);
            }
        });
		userlogin = (LinearLayout)this.findViewById(R.id.user_login);
		userlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
				onGlobalLayout(quitloginview);
				setLogoutView(quitloginview);
			}
		});
		onlineMusic = (LinearLayout) this
			.findViewById(R.id.slide_menu_online_music);
		onlineMusic.setOnClickListener(new OnClickListener() {

		@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,OnlineMusic.class);
			startActivity(intent);
			}
		});

		quit = (LinearLayout) this.findViewById(R.id.slide_menu_quit);
		quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

	}

	/**
	 * 设置denglujiemian监听事件
	 * 以下是登录界面里的item
	 * @param v
	 */
	private LinearLayout login_ll;
	private Button login_btn;
	private ImageButton login_actionbar_back;

	private void setLoginView(View view) {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);

        userunlogin.setVisibility(LinearLayout.GONE);
        userlogin.setVisibility(LinearLayout.VISIBLE);
		login_actionbar_back = view.findViewById(R.id.login_actionbar_back);
		login_actionbar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
			}
		});
	}
	private ImageButton logouot_actionbar_back;
	private LinearLayout user_info_ll;
	private TextView user_name_tx;
	private TextView real_name_tx;
	private Button logout_btn;
	private void setLogoutView(View view){
		getMember(userName,userPassword);
		logouot_actionbar_back = view.findViewById(R.id.logouot_actionbar_back);
		logouot_actionbar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
			}
		});
		logout_btn = view.findViewById(R.id.logout_btn);
		logout_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
				userunlogin.setVisibility(LinearLayout.VISIBLE);
				userlogin.setVisibility(LinearLayout.GONE);
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		onGlobalLayout(mainView);
	}

	private void onGlobalLayout(View v) {
		// TODO Auto-generated method stub
		mainView_Children = new View[] { txtArg, v };
		leftWidth = slideMenu.getMeasuredWidth();
		final int w = myHSV.getMeasuredWidth();
		final int h = myHSV.getMeasuredHeight();
		rightWidth = w - leftWidth;// 获得剩余部分的宽度
		System.out.println("leftWidth------" + leftWidth + "w----" + w
				+ "  ----h----" + h);
		myHSV_Linear.removeAllViews();
		int[] dims = new int[2];
		for (int i = 0; i < mainView_Children.length; i++) {
			getViewSize(i, w, h, dims);
			myHSV_Linear.addView(mainView_Children[i], dims[0], dims[1]);
		}

		myHSV.setBtnWith(rightWidth);
		myHSV.setAppWidth(w);
	}

	/**
	 * 获取各个View视图的宽高
	 */
	public void getViewSize(int idx, int w, int h, int[] dims) {
		dims[0] = w;
		dims[1] = h;
		final int menuIdx = 0;
		if (idx == menuIdx) {
			dims[0] = w - rightWidth;
		}
		System.out.println("idx---" + idx + "------w---" + dims[0]
				+ "------h----" + dims[1]);

	}

	/**
	 * 顶部按钮左右移动
	 * 
	 * @param width
	 */
	public void moveScrollList(int width) {
		int menuWidth = width;

		if (flagMove) {
			// Scroll to 0 to reveal menu
			offset = 0;
			myHSV.smoothScrollTo(offset, 0);
		} else {
			// Scroll to menuWidth so menu isn't on screen.
			offset = menuWidth;
			myHSV.smoothScrollTo(offset, 0);
		}
		flagMove = !flagMove;
	}

	/**
	 * 设置按键监听事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			int width = mainView_Children[0].getMeasuredWidth();
			moveScrollList(width);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

    public String userNameinfo;
	public  void getMember(String userName,String userPassword) {
        userNameinfo = userName;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(
		        "http://114.115.216.215:8080/MusicPlayer/login.action?userAccount="+userName+"&userPassword="+userPassword).build();
		client.newCall(request).enqueue(new Callback() {
		@Override
		public void onFailure(Call call, IOException e) {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT);
				}
			});
		}
		@Override
		public void onResponse(Call call, Response response) throws IOException {
			final String json = response.body().string();
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Result<Member> memberResult = new Gson().fromJson(json, new TypeToken<Result<Member>>(){}.getType());
					memberResult.setCode(0);
					if(memberResult.getCode().equals(0)){
						//user_name_tx.setText(userNameinfo);
					} else {
						Toast.makeText(MainActivity.this, memberResult.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	});
}

	public void logout() {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder().build();
		Request request = new Request.Builder().url("http://121.199.40.253/nba/logout").post(requestBody).addHeader("token", Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID)).build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT);
					}
				});
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String json = response.body().string();
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Result result = new Gson().fromJson(json, new TypeToken<Result>(){}.getType());
						if(result.getCode().equals(0)){
							Toast.makeText(MainActivity.this, "退出当前帐号成功", Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

	}
	private void setMainView(View v) {
		// TODO Auto-generated method stub

		main_actionbar_menu = (ImageButton) v.findViewById(R.id.main_actionbar_menu);
		pop_menu = (ImageButton) v.findViewById(R.id.main_actionbar_scan);
		main_actionbar_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int width = mainView_Children[0].getMeasuredWidth();
				moveScrollList(width);
			}
		});

		pop_menu.setVisibility(View.INVISIBLE);
		pop_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		localMusic = (LinearLayout) v.findViewById(R.id.main_local_music_item);
		folder = (LinearLayout) v.findViewById(R.id.main_folder_item);
		artist = (LinearLayout) v.findViewById(R.id.main_artist_item);
		album = (LinearLayout) v.findViewById(R.id.main_album_item);
		playlist = (LinearLayout) v.findViewById(R.id.main_playlist_item);
		// 得到miniplayer上的控件
		title_mini = (TextView) v.findViewById(R.id.main_miniplayer_song);
		artist_mini = (TextView) v.findViewById(R.id.main_miniplayer_artist);
		album_mini = (ImageView) v.findViewById(R.id.main_miniplayer_album);
		play = (ImageButton) v.findViewById(R.id.main_miniplayer_play);
		next = (ImageButton) v.findViewById(R.id.main_miniplayer_next);
		miniPlayer = (RelativeLayout) v
				.findViewById(R.id.main_miniplayer_layout);

		tv_localMusic = (TextView) v.findViewById(R.id.main_local_music_text);
		tv_folder = (TextView) v.findViewById(R.id.main_folder_text);
		tv_artist = (TextView) v.findViewById(R.id.main_artist_text);
		tv_album = (TextView) v.findViewById(R.id.main_album_text);
		tv_playlist = (TextView) v.findViewById(R.id.main_playlist_text);
		int[] data = initData();
		tv_localMusic.setText(data[0] + "首");
		localMusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, LocalMusic.class);
				startActivity(intent);
			}
		});
		folder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		artist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						ArtistSelect.class);
				startActivity(intent);
			}
		});
		album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, AlbumSelect.class);
				startActivity(intent);
			}
		});
		playlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		miniPlayer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						PlayerAndLyric.class);
				startActivity(intent);
			}
		});
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发送广播给service
				Intent intent = new Intent();
				intent.setAction(PlayerService.ACTION_PLAY_BUTTON);
				sendBroadcast(intent);
			}
		});
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发送广播给service
				Intent intent = new Intent();
				intent.setAction(PlayerService.ACTION_PLAY_NEXT);
				sendBroadcast(intent);
			}
		});
	}
	/**
	 * 查询数据库，用于我的音乐界面，UI显示数据
	 * 
	 * @return int[]
	 */
	private int[] initData() {
		// TODO Auto-generated method stub
		MusicDBHelper localDbHelper = new MusicDBHelper(MainActivity.this);
		Cursor curLocal = localDbHelper.queryLocalByID();
		Cursor curFav = localDbHelper.queryFavByID();
		Cursor curArtist = localDbHelper.queryArtistByID();
		Cursor curAlbum = localDbHelper.queryAlbumByID();
		int[] dataSum = new int[] { curLocal.getCount(),
				curArtist.getCount(), curAlbum.getCount() };
		curLocal.close();
		curFav.close();
		curArtist.close();
		curAlbum.close();
		localDbHelper.close();
		return dataSum;
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 改变当前界面UI
		int[] data = initData();
		tv_localMusic.setText(data[0] + "首");
//		tv_favor.setText(data[1] + "首");
		tv_artist.setText(data[1] + "个歌手");
		tv_album.setText(data[2] + "张专辑");
		// 注册播放状态改变的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 解除注册状态改变监听器
		PlayerService.unRegisterStateChangeListener(stateChangeListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopService(service);
		super.onDestroy();
	}
}
