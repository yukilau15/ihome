package com.example.ihome;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

public class ARRulerActivity extends AppCompatActivity {

    private String unit;

    private TextView distanceTv;
    private ImageView targetIv, backIv;
    private Button addmarkerBtn, clearBtn;

    private ArFragment arFragment;

    private ModelRenderable blueSphereRenderable ;

    private List<AnchorNode> anchorNodeList = new ArrayList<>();
    private List<Node> textViewRenderableList = new ArrayList<>();
    private List<HitResult> hitResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arruler);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_white));

        backIv = findViewById(R.id.back);
        addmarkerBtn = findViewById(R.id.addmarker);
        clearBtn = findViewById(R.id.clear);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        makeSphere();

        addmarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitResultList = arFragment.getArSceneView().getArFrame().hitTest(
                        arFragment.getView().getPivotX(),
                        arFragment.getView().getPivotY());

                if (!hitResultList.isEmpty()) {
                    Anchor anchor = hitResultList.get(0).createAnchor();
                    addNodeToScene(arFragment, anchor, blueSphereRenderable);
                    calculateDistance();
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllNodes();
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.unit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m:
                unit = "m";

                Toast.makeText(getApplicationContext(),
                        "Metre selected",
                        Toast.LENGTH_SHORT)
                        .show();

                return true;
            case R.id.cm:
                unit = "cm";

                Toast.makeText(getApplicationContext(),
                        "Centimetre selected",
                        Toast.LENGTH_SHORT)
                        .show();

                return true;
            case R.id.ft:
                unit = "ft";

                Toast.makeText(getApplicationContext(),
                        "Foot selected",
                        Toast.LENGTH_SHORT)
                        .show();

                return true;
            case R.id.in:
                unit = "in";

                Toast.makeText(getApplicationContext(),
                        "Inch selected",
                        Toast.LENGTH_SHORT)
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        hitResultList = frame.hitTest(arFragment.getView().getPivotX(), arFragment.getView().getPivotY());

        if (frame == null) {
            return;
        }

        if (!hitResultList.isEmpty()) {
            targetColor(android.graphics.Color.BLUE);
            addmarkerBtn.setEnabled(true);
        } else {
            targetColor(android.graphics.Color.BLACK);
            addmarkerBtn.setEnabled(false);
        }

        for (Node node : textViewRenderableList) {
            updateOrientationTowardsCamera(node);
        }
    }

    private void targetColor(int color) {
        targetIv = findViewById(R.id.target);
        targetIv.setColorFilter(color);
    }

    private void makeSphere() {
        MaterialFactory.makeOpaqueWithColor(getApplicationContext(),
                new Color(android.graphics.Color.BLUE))
                .thenAccept(
                        material -> {
                            blueSphereRenderable = ShapeFactory.makeSphere(0.01f,
                                    new Vector3(0.0f, 0.0f, 0.0f), material);
                            blueSphereRenderable.setShadowCaster(false);
                            blueSphereRenderable.setShadowReceiver(false);
                        });
    }

    /*private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(renderable);
        anchorNodeList.add(anchorNode);

        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }*/

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.select();
        anchorNodeList.add(anchorNode);

        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void updateOrientationTowardsCamera(Node node) {
        Vector3 cameraPosition = arFragment.getArSceneView().getScene().getCamera().getWorldPosition();
        Vector3 cardPosition = node.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        node.setWorldRotation(lookRotation);
    }

    private void calculateDistance() {
        if (anchorNodeList.size() >= 2) {
            float distance = distanceBetweenAnchors(
                    anchorNodeList.get(anchorNodeList.size() - 1).getAnchor(),
                    anchorNodeList.get(anchorNodeList.size() - 2).getAnchor());

            Vector3 midPos = midPosBetweenAnchors(
                    anchorNodeList.get(anchorNodeList.size() - 1).getAnchor(),
                    anchorNodeList.get(anchorNodeList.size() - 2).getAnchor());

            makeTextRenderable(distance, midPos);
        }
    }

    private float distanceBetweenAnchors(Anchor start, Anchor end) {
        Pose startPose = start.getPose();
        Pose endPose = end.getPose();

        float dx = startPose.tx() - endPose.tx();
        float dy = startPose.ty() - endPose.ty();
        float dz = startPose.tz() - endPose.tz();

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private Vector3 midPosBetweenAnchors(Anchor start, Anchor end) {
        return Vector3.add(new AnchorNode(start).getWorldPosition(),
                new AnchorNode(end).getWorldPosition()).scaled(0.5f);
    }

    private void makeTextRenderable(float distance, Vector3 worldPos) {
        ViewRenderable.builder()
                .setView(getApplicationContext(), R.layout.text_renderable)
                .build()
                .thenAccept(renderable -> addDistanceText(renderable, distance, worldPos));
    }

    private void addDistanceText(ViewRenderable renderable, float distance, Vector3 worldPos) {
        distanceTv = renderable.getView().findViewById(R.id.card);

        if (unit == "cm") {
            distanceTv.setText(String.format("%.2f", mtocm(distance)) + "cm");
        } else if (unit == "ft") {
            distanceTv.setText(String.format("%.2f", mtoft(distance)) + "ft");
        } else if (unit == "in") {
            distanceTv.setText(String.format("%.2f", mtoin(distance)) + "in");
        } else {
            distanceTv.setText(String.format("%.2f", distance) + "m");
        }

        Node node = new Node();
        node.setRenderable(renderable);
        node.setWorldPosition(worldPos);
        renderable.setShadowCaster(false);
        renderable.setShadowReceiver(false);
        textViewRenderableList.add(node);

        arFragment.getArSceneView().getScene().addChild(node);
    }

    static double mtocm(float metre) {
        return metre * 100;
    }

    static double mtoft(float metre) {
        return metre * 3.28;
    }

    static double mtoin(float metre) {
        return metre * 39.37;
    }

    private void clearAllNodes() {
        List<Node> children = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());

        for (Node node : children) {
            if (node instanceof AnchorNode) {
                arFragment.getArSceneView().getScene().removeChild(node);
                ((AnchorNode) node).getAnchor().detach();
            }

            for (Node textView : textViewRenderableList) {
                textView.setParent(null);
            }

            textViewRenderableList = new ArrayList<>();
            anchorNodeList = new ArrayList<>();
        }
    }
}
