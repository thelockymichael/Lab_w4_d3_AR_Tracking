package com.example.lab_w4_d3_ar_tracking

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment: ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)

        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.focusMode = Config.FocusMode.AUTO
        session?.configure(config)

        val mainActivity = activity as MainActivity

        mainActivity.setupDatabase(config, session)

        return config
    }
}