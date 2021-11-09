package com.example.ihome;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARModelActivity extends AppCompatActivity {

    private String mModelUri;

    private ImageView backIv;

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armodel);

        mModelUri = getIntent().getStringExtra("itemmodel");

        backIv = findViewById(R.id.back);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        loadModel();

        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            addNodeToScene(arFragment, anchor, modelRenderable);
        }));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadModel() {
        ModelRenderable.builder()
                .setSource(getApplicationContext(), RenderableSource.builder().setSource(
                        getApplicationContext(),
                        Uri.parse(mModelUri),
                        RenderableSource.SourceType.GLB)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(mModelUri)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(getApplicationContext(),
                                    "Unable to load model",
                                    Toast.LENGTH_SHORT)
                                    .show();

                    return null;
                });
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable){
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.getScaleController().setEnabled(false);
        node.select();

        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}
