package Enums;

public enum PlayerActions {
  FORWARD (1),
  STOP (2),
  START_AFTERBURNER (3),
  STOP_AFTERBURNER (4),
  FireTorpedoes (5),
  FireSupernova (6),
  DetonateSupernova (7),
  FIRE_TELEPORT(8),
  TELEPORT(9),
  ACTIVATE_SHIELD(10);

  public final Integer value;

  private PlayerActions(Integer value) {
    this.value = value;
  }
}
