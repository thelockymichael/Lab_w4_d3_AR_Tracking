package com.example.lab_w4_d3_ar_tracking

import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.ar.core.*
import com.google.ar.sceneform.*
import com.google.ar.sceneform.assets.RenderableSource

import com.google.ar.sceneform.rendering.ModelRenderable

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Scene.OnUpdateListener {
    private lateinit var arFragment: CustomArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment =
            supportFragmentManager
                .findFragmentById(R.id.sceneform_fragment)
                    as CustomArFragment


        arFragment.arSceneView.scene.addOnUpdateListener(this)
    }

    override fun onUpdate(frameTime: FrameTime?) {
        var arFrame = arFragment.arSceneView.arFrame

        if (arFrame == null || arFrame.camera.trackingState !=
            TrackingState.TRACKING
        ) return

        val updatedAugmentedImages =
            arFrame.getUpdatedTrackables(AugmentedImage::class.java)

        updatedAugmentedImages.forEach {
            when (it.trackingState) {
                null -> return@forEach

                TrackingState.TRACKING -> {

                    Log.d("DBG", "Tracking")

                    if (it.name == "delorean") {
                        Log.d("DBG", "Tracking DeLOREAN!!!!")

                        val anchor = it.createAnchor(it.centerPose)

                        createModel(anchor)
                    }
                }
            }
        }
    }

    private fun createModel(anchor: Anchor?) {

        // (CC BY 4.0) Donated by Cesium  for glTF testing.
/*        val uri = Uri.parse(
            "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/CesiumMan/glTF/CesiumMan.gltf"
        )*/

        val uri = Uri.parse(
            "https://raw.githubusercontent.com/thelockymichael/gltf-Sample_models/main/gltf-models/scene.gltf"
        )

        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    uri, RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.2f) // Scale the original to 20%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("scene").build()

        renderableFuture.thenAccept { placeModel(it, anchor) }
        renderableFuture.exceptionally {
            // something went wrong notify user
            Log.e("DBG", "renderableFuture error: ${it.localizedMessage}")
            null
        }
    }

    private fun placeModel(model: ModelRenderable?, anchor: Anchor?) {
        var anchorNode = AnchorNode(anchor)

        anchorNode.renderable = model
        arFragment.arSceneView.scene.addChild(anchorNode)
    }

    internal fun setupDatabase(config: Config?, session: Session?) {
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.delorean)
        val augmentedImageDb = AugmentedImageDatabase(session)
        augmentedImageDb.addImage("delorean", bitmap)
        config?.augmentedImageDatabase = augmentedImageDb
    }

    private fun getScreenCenter(): Point {
        // find the root view of the activity
        val vw = findViewById<View>(android.R.id.content)
        // returns center of the screen as a Point object
        return Point(vw.width / 2, vw.height / 2)
    }
}