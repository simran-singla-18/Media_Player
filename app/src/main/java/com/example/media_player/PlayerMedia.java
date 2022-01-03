package com.example.media_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class PlayerMedia extends AppCompatActivity {
    Button btnplay, btnnext, btnprev, btnff, btnfr;
    TextView txtsname, txtsstart, textsstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    String sname;
    public static final String EXTRA_NAME = "songs_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread thread;
ImageView img;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer!=null)
        {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_media);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        btnff = findViewById(R.id.btnff);
        btnfr = findViewById(R.id.btnfr);
        btnplay = findViewById(R.id.playbtn);
        btnnext = findViewById(R.id.btnnext);
        btnprev = findViewById(R.id.btnprev);
        textsstop = findViewById(R.id.txtstop);
        txtsstart = findViewById(R.id.txtstart);
        txtsname = findViewById(R.id.textsn);
        visualizer = findViewById(R.id.blast);
        seekmusic=findViewById(R.id.seekbar);
        img = findViewById(R.id.imageview);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");

        position = bundle.getInt("pos", 0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        thread = new Thread() {
            @Override
            public void run() {
                int totalduration = mediaPlayer.getDuration();
                int currentduration = 0;
                while (currentduration < totalduration) {
                    try {
                        thread.sleep(500);
                        currentduration = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentduration);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
            };
        seekmusic.setMax(mediaPlayer.getDuration());
        thread.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark),PorterDuff.Mode.SRC_IN);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endtime=createTime(mediaPlayer.getDuration());
        textsstop.setText(endtime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentime=createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentime);
                handler.postDelayed(this,delay);
            }
        },delay);

                btnplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mediaPlayer.isPlaying()) {
                            btnplay.setBackgroundResource(R.drawable.ic_play);
                            mediaPlayer.pause();
                        } else {
                            btnplay.setBackgroundResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }
                    }
                });
                btnnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        position = (position + 1) % mySongs.size();
                        Uri uri1 = Uri.parse(mySongs.get(position).toString());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
                        sname = mySongs.get(position).getName();
                        txtsname.setText(sname);
                        mediaPlayer.start();
                        btnplay.setBackgroundResource(R.drawable.ic_pause);
                        startAnimation(img);
                        int audiosessionid=mediaPlayer.getAudioSessionId();
                        if(audiosessionid!=-1)
                        {
                            visualizer.setAudioSessionId(audiosessionid);
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        btnnext.performClick();
                    }
                });
                int audiosessionid=mediaPlayer.getAudioSessionId();
                if(audiosessionid!=-1)
                {
                    visualizer.setAudioSessionId(audiosessionid);
                }
                btnprev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                        Uri uri2 = Uri.parse(mySongs.get(position).toString());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri2);
                        sname = mySongs.get(position).getName();
                        txtsname.setText(sname);
                        mediaPlayer.start();
                        btnplay.setBackgroundResource(R.drawable.ic_pause);
                        startAnimation(img);
                        int audiosessionid=mediaPlayer.getAudioSessionId();
                        if(audiosessionid!=-1)
                        {
                            visualizer.setAudioSessionId(audiosessionid);
                        }
                    }
                });
                btnff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer.isPlaying())
                        {
                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                        }
                    }
                });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
            }

            public void startAnimation(View view) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img, "rotation", 360f);
                objectAnimator.setDuration(1000);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator);
                animatorSet.start();
                setVisible(true);
            }
            public String createTime(int duration)
            {
                String time="";
                int min=duration/1000/60;
                int sec=duration/1000%60;
                time+=min+":";
                if(sec<10)
                {
                    time+="0";


                }
                time+=sec;
                return time;
            }



        }
