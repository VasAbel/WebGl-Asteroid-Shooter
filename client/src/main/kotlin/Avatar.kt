import kotlin.math.*
import vision.gears.webglmath.*


class Avatar(mesh: Mesh, private val bulletMesh: Mesh) : GameObject(mesh) {
   
    val cooldownDuration = 2.0f
    var timeSinceLastShot = cooldownDuration
    val recentlyShotBullets = mutableMapOf<GameObject, Double>()
    var isDestroyed = false

    fun shoot(createBullet: (GameObject) -> Unit) {
        if (timeSinceLastShot >= cooldownDuration) {
            val bulletDirection = this.roll
            val bulletStartPosition = Vec3(this.position.x, this.position.y) 
            val bullet = Bullet(bulletMesh, bulletStartPosition, bulletDirection)
            recentlyShotBullets[bullet] = bullet.creationTime
            createBullet(bullet)
            timeSinceLastShot = 0.0f
        }    
    }

    fun checkCollision(gameObjects: List<GameObject>): Boolean {
        for (gameObject in gameObjects) {
            if (gameObject != this && !recentlyShotBullets.containsKey(gameObject)) {
                val dx = this.position.x - gameObject.position.x
                val dy = this.position.y - gameObject.position.y
                val dz = this.position.z - gameObject.position.z
                val distance = sqrt(dx * dx + dy * dy + dz * dz)

                if(distance <= this.collisionRadius + gameObject.collisionRadius){
                    isDestroyed = true
                  return true
                }
            }
        }
        return false
    }

    init {
        collisionRadius = 0.01f
        scale.set(0.2f, 0.1f) 
        
        move = object : Motion(this) {
            val movementSpeed = 0.8f  
            val rotationSpeed = 1.3f 

            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, interactors: ArrayList<GameObject>, spawn: ArrayList<GameObject>): Boolean {
                
                recentlyShotBullets.keys.removeAll { bulletGameObject ->
                    val bullet = bulletGameObject as? Bullet
                    val bulletAge = t - (recentlyShotBullets[bulletGameObject] ?: 0.0)
                    bullet != null && bulletAge > bullet.collisionDelay
                }
                if (checkCollision(interactors)) {
                    return false
                }
                
                gameObject.position.x += cos(gameObject.roll) * movementSpeed * dt
                gameObject.position.y += sin(gameObject.roll) * movementSpeed * dt
                
                if (keysPressed.contains("A")) {
                    
                    gameObject.roll += rotationSpeed * dt
                }
                if (keysPressed.contains("D")) {
                    
                    gameObject.roll -= rotationSpeed * dt
                }
                timeSinceLastShot += dt
                return true
            }
         
        }

    }
}