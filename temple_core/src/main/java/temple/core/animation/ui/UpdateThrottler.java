package temple.core.animation.ui;

import android.text.format.Time;

/**
 * Created by michielb on 4-12-2014.
 * MediaMonks
 */
public class UpdateThrottler
{
	private Time _time;
	private UpdateFrequency _frequency;
	private int _lastUpdateTime;

	public enum UpdateFrequency
	{
		ALWAYS, SECOND, MINUTE, HOUR, DAY, NEVER
	}

	public UpdateThrottler(Time time, UpdateFrequency frequency)
	{
		_time = time;
		_frequency = frequency;

		reset();
	}

	public void reset()
	{
		_lastUpdateTime = -1;
	}

	public boolean shouldUpdate()
	{
		boolean update = false;

		switch (_frequency)
		{
			case ALWAYS:
				update = true;
				break;
			case SECOND:
				update = checkTime(_time.second);
				break;
			case MINUTE:
				update = checkTime(_time.minute);
				break;
			case HOUR:
				update = checkTime(_time.hour);
				break;
			case DAY:
				update = checkTime(_time.weekDay);
				break;
			case NEVER:
				update = false;
				break;
		}

		return update;
	}

	private boolean checkTime(int value)
	{
		boolean update = false;
		if (value != _lastUpdateTime)
		{
			_lastUpdateTime = value;
			update = true;
		}
		return update;
	}
}
