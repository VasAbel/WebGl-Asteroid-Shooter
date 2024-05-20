import vision.gears.webglmath.*
import kotlin.math.*
import kotlin.js.Date

class Bullet(mesh: Mesh, startPosition: Vec3, direction: Float) : GameObject(mesh) {
    
    private val speed = 2.0f 
    var collisionDelay = 1f
    val creationTime = Date().getTime()


    fun checkCollision(gameObjects : List<GameObject>) : GameObject?{
          for(gameObject in gameObjects){
              if(gameObject != this && gameObject is Asteroid){
                val dx = this.position.x - gameObject.position.x
                val dy = this.position.y - gameObject.position.y
                val dz = this.position.z - gameObject.position.z
                val distance = sqrt(dx * dx + dy * dy + dz * dz)

                if(distance <= this.collisionRadius + gameObject.collisionRadius){
                  return gameObject
                }
              } 
          }
          return null;
      }

    init {
        collisionRadius = 0.01f
        scale.set(0.02f, 0.02f)
        position.set(startPosition)
        roll = direction

        move = object : Motion(this) {
            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, interactors: ArrayList<GameObject>, spawn: ArrayList<GameObject>): Boolean {
                gameObject.position.x += cos(gameObject.roll) * speed * dt
                gameObject.position.y += sin(gameObject.roll) * speed * dt
                val collidedObject = checkCollision(interactors)
                if (collidedObject != null) {
                    spawn += collidedObject
                    return false
                }       
                return true
            }
        }
    }
}