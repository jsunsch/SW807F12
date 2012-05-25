package edu.aau.utzon.indoor;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import edu.aau.utzon.R;

public class SoundPlayer {
	
	static MediaPlayer mp = null;
	static String currentlyPlaying = "";
	
	public static void playSound(String name, Context context){
		
		if (mp != null)
		{
			mp.stop();
		}
		
		if (name.equals(currentlyPlaying))
			return;
		
		if (name.equals("t34.mp3"))
			mp = MediaPlayer.create(context, R.raw.t34);
		else if (name.equals("tiger1.mp3"))
			mp = MediaPlayer.create(context, R.raw.tiger1);
		else if (name.equals("m26pershing.mp3"))
			mp = MediaPlayer.create(context, R.raw.m26pershing);
		else if (name.equals("m4sherman.mp3"))
			mp = MediaPlayer.create(context, R.raw.m4sherman);
		else if (name.equals("panther.mp3"))
			mp = MediaPlayer.create(context, R.raw.panther);
		
		mp.start();
		currentlyPlaying = name;
	}
	
	public static void Stop()
	{
		currentlyPlaying = "";
		if (mp!= null)
			mp.stop();
	}
}
