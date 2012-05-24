package edu.aau.utzon.indoor;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import edu.aau.utzon.R;

public class SoundPlayer {
	
	static MediaPlayer mp = null;
	
	public static void playSound(String name, Context context){
		
		if (mp != null)
		{
			mp.stop();
		}
		
		if (name.equals("t34.mp3"))
			mp = MediaPlayer.create(context, R.raw.t34);
		else if (name.equals("tiger_tank.mp3"))
			mp = MediaPlayer.create(context, R.raw.tiger_tank);
		
		mp.start();
	}
}
