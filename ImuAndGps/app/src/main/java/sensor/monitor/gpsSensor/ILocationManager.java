package sensor.monitor.gpsSensor;

public abstract interface ILocationManager
{
  public abstract void requestLocationUpdates(long paramLong, float paramFloat);
}
