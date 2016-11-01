package fireFighters_MAS;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

public class Wind
{
	// Local variables declaration
	private int windDirection;
	/**
	 * Custom constructor
	 * 0 - North, 1 - North-East, 2 - East, 3 - South-East
	 * 4 - South, 5 - South-West, 6 - West, 7 - North-West
	 */
	public Wind()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.windDirection = params.getInteger("windDirection");;
	}
	@ScheduledMethod(start = 1, interval = 400)
	public void changeWindDirection()
	{
		int windDirDif = RandomHelper.nextIntFromTo(-1, 1); // Can change direction only to neighbors
		windDirection = (windDirection + windDirDif + 8) % 8; // Make sure it is non-negative
	}
	// Local getters
	public int getWindDirection() {	return windDirection; }
}
