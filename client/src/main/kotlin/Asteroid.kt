import kotlin.math.*

class Asteroid(mesh: Mesh, val rotationSpeed: Float) : GameObject(mesh) {

    init {
        scale.set(0.2f, 0.2f) 
        
        collisionRadius = 0.3f
        
        move = object : Motion(this) {
            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, interactors: ArrayList<GameObject>, spawn: ArrayList<GameObject>): Boolean {
                gameObject.roll += rotationSpeed * dt
                return true
            }
        }
    }

}