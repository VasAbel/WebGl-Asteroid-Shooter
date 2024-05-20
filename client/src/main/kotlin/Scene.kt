import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL
import vision.gears.webglmath.*
import kotlin.js.Date
import kotlin.math.*
import kotlin.random.Random
  
class Scene (
  val gl : WebGL2RenderingContext) : UniformProvider("scene") {

  val vsTrafo = Shader(gl, GL.VERTEX_SHADER, "trafo-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val vsAnim = Shader(gl, GL.VERTEX_SHADER, "anim-vs.glsl")
  val fsAnim = Shader(gl, GL.FRAGMENT_SHADER, "anim-fs.glsl")

  val texturedProgram = Program(gl, vsTrafo, fsTextured)
  val animProgram = Program(gl, vsAnim, fsAnim)
  
  val quadGeometry = TexturedQuadGeometry(gl)

  val animationTexture = Texture2D(gl, "media/boom.png")
  var offset = Mat4()
  var timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame
  
  var counter = 0
  var animCounter = 0
  var timeGone = 0f

  val asteroidMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(
      Texture2D(gl, "media/asteroid.png"))
    this["texOffset"]?.set(0.3f, 0.2f)
  }

  val raiderMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(
      Texture2D(gl, "media/raider.png"))
    this["texOffset"]?.set(0.1f, 0.4f)
  }

  val bulletMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(
      Texture2D(gl, "media/bullet.png"))
    this["texOffset"]?.set(0.1f, 0.4f)
  }

  val camera = OrthoCamera()

  val asteroidMesh = Mesh(asteroidMaterial, quadGeometry)
  val raiderMesh = Mesh(raiderMaterial, quadGeometry)
  val bulletMesh = Mesh(bulletMaterial, quadGeometry)

  val gameObjects = ArrayList<GameObject>()
  
  var avatar = Avatar(raiderMesh, bulletMesh)

  var lastAsteroid = 0.0f 

   
  init{
    gameObjects += avatar
  }    

  fun resetGame(){
    avatar = Avatar(raiderMesh, bulletMesh)
    avatar.isDestroyed = false
    gameObjects.clear()
    gameObjects.add(avatar)

    lastAsteroid = 0.0f
    counter = 0
    timeGone = 0f
    offset = Mat4()
    timeAtFirstFrame = Date().getTime()
    timeAtLastFrame =  timeAtFirstFrame
    animCounter = 0
  }

  fun resize(gl : WebGL2RenderingContext, canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)
    camera.setAspectRatio(canvas.width.toFloat() / canvas.height.toFloat())
    camera.update()
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(gl : WebGL2RenderingContext, keysPressed : Set<String>) {
    
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t  = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f    
    timeAtLastFrame = timeAtThisFrame
    


    gl.enable(GL.BLEND)
    gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

    // clear the screen
    gl.clearColor(0.2f, 0.0f, 0.5f, 1.0f)
    gl.clearDepth(1.0f)
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

    if(avatar.isDestroyed){
      timeGone = timeGone + dt
      gl.uniform1i(gl.getUniformLocation(
      animProgram.glProgram, "material.colorTexture"), 0)
      gl.activeTexture(GL.TEXTURE0)
      gl.bindTexture(GL.TEXTURE_2D, animationTexture.glTexture)
      gl.useProgram(animProgram.glProgram)

      if(timeGone >= 0.1f){
        timeGone = 0f
        counter++
        if(counter == 6){
          counter = 0 
          offset.translate(10f, 2f)
          animCounter++
        }
        else{
          offset.translate(-2f)       
        }
        if(animCounter == 6)
          resetGame()
      }

      gl.uniformMatrix4fv(gl.getUniformLocation(animProgram.glProgram, "gameObject.offset"), false, offset.storage)
      quadGeometry.draw()

    }
    else{
      gl.useProgram(texturedProgram.glProgram)
      
      if (keysPressed.contains("SPACE")) {
          avatar.shoot { bullet ->
              gameObjects.add(bullet)
          }
      }

      if (timeAtThisFrame - lastAsteroid >= 1500) {
          spawnAsteroidAroundAvatar()
          lastAsteroid = timeAtThisFrame.toFloat()
      }

      val spawn = ArrayList<GameObject>()
      val killList = ArrayList<GameObject>()    
      gameObjects.forEach { 
        if(!it.move(dt, t, keysPressed, gameObjects, spawn)){
          killList.add(it)
          killList.addAll(spawn)
          spawn.clear()
        }
      }
      killList.forEach{ gameObjects.remove(it) }
      spawn.forEach{ gameObjects.add(it) }

      for(gameObj in gameObjects){
        gameObj.update()
        
      }

      camera.position.set(avatar.position.x, avatar.position.y)
      camera.update()
      for(gameObj in gameObjects){
          gameObj.draw(camera)
      }
    }
  }

  fun spawnAsteroidAroundAvatar() {
      val radius = 0.7f 
      val angle = Random.nextFloat() * 2 * PI
      val x = avatar.position.x + radius * cos(angle)
      val y = avatar.position.y + radius * sin(angle)

      val newAsteroid = Asteroid(asteroidMesh, Random.nextFloat() * 0.5f + 0.1f) 
      newAsteroid.position.set(x.toFloat(), y.toFloat())
      gameObjects += newAsteroid
  }
}
