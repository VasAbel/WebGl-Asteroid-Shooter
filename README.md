# WebGl-Asteroid-Shooter
Simple 2D minigame in WebGl coded in Kotlin.

# The task
The goal if this project was to implement 5 elements into the project from a list.
## Elements
1. Avatar: We can control our avatar (spaceship) by pressing buttons **A** and **D** (turn left and right) while the spaceship is going forward automatically to make the game harder. We can also shoot bullets with **SPACE**

2. Bullets: Bullets are shot in the same direction where the spaceship faces. If it hits an asteroid, both the bullet and the asteroid gets destroyed. There is a 2 seconds cooldown for shooting.

3. Collision: If the spaceship hits an asteroid, both gets destroyed, and an animated explosion is played. The same happens with bullet and asteroid collision without animation.

4. Explosion: The explosion is animated, then the game restarts

5. Camera: The camera follows the avatar (spaceship)

# The game
## Controls
**SPACE**: shoot (2 seconds cooldown)

**A**: turn left

**D**: turn right

## Plus information
Asteroids are spawned around the spaceship in every 1.5 seconds in a fixed distance but random position

The spaceship is moving forward automatically and cannot be stopped because the game could be "played" for the eternity by staying in place.

After the explosion, the game restarts by resetting both the asteroids and the spaceship
