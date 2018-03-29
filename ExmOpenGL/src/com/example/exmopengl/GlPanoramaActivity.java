package com.example.exmopengl;

import com.example.exmopengl.widget.PanoramaView;

import android.app.Activity;
import android.os.Bundle;

public class GlPanoramaActivity extends Activity {
    private PanoramaView glp_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_panorama);
        glp_content= (PanoramaView) findViewById(R.id.glp_content);
        //传入全景图片
        glp_content.setGLPanorama(R.drawable.panorama05);
    }

}
