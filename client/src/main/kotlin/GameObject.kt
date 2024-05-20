import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.*
import kotlin.math.*


open class GameObject(vararg meshes : Mesh) : UniformProvider("gameObject") {
 
  var collisionRadius = 0.1f
  val position = Vec3()
  var roll = 0.0f 
  val scale = Vec3(1.0f, 1.0f, 1.0f)  

  val modelMatrix by Mat4()

  init {
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update(){
    modelMatrix.set().scale(scale).rotate(roll, 0f, 0f, 1f).translate(position)
  }

  open class Motion(val gameObject : GameObject){
    open operator fun invoke(
      dt : Float,
      t : Float,
      keysPressed : Set<String>,
      interactors : ArrayList<GameObject>,
      spawn : ArrayList<GameObject>
      ) : Boolean {
      return true
    }
  }
  var move = Motion(this)

    
}