package elevators.models

sealed trait MovingDirection
case object Up extends MovingDirection
case object Down extends MovingDirection