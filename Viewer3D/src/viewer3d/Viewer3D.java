/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer3d;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.geometry.Triangulator;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 *
 * @author leeyo
 */
public class Viewer3D extends Application {
    
    private Group rootV = new Group();
    final moleculesampleapp.Xform axisGroup = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform moleculeGroup = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform surfaceGroup = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform lineGroup = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform world = new moleculesampleapp.Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final moleculesampleapp.Xform cameraXform = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform cameraXform2 = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform cameraXform3 = new moleculesampleapp.Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -1000;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 3.0;
    private static final double TRACK_SPEED = 0.3;
    private static final double ZOOM_SPEED = 0.1;

    private static final MenuBar menuBar = new MenuBar();
    private static final Accordion accordion = new Accordion();

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        System.out.println("start()");
        //Parent rootXML = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        //setUserAgentStylesheet(STYLESHEET_MODENA);
        
        rootV.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        buildAxes();
        createSurface();
        //createPyramid();
        //buildMolecule();

        rootV.getChildren().add(world);
        
        SubScene scene = new SubScene(rootV, 700, 700, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.WHITE);
        scene.setPickOnBounds(true);

        //handleKeyboard(scene, world);
        handleMouse(scene, world);

        camera.setNearClip(Double.MIN_VALUE);
        scene.setCamera(camera);

        //menubar
        //buildMenuBar();
        //menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        //accordian/tree/table structure
        //buildAnalysisControls();

        //BorderPane bPane = new BorderPane(scene);
        
        BorderPane bPane;
        bPane = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        //scene.heightProperty().bind(bPane.heightProperty());
        //scene.widthProperty().bind(bPane.widthProperty());
        //bPane.setTop(menuBar);
        bPane.setCenter(scene);
        //bPane.setLeft(accordion);

        Scene mainScene = new Scene(bPane);
       
        handleKeyboard(mainScene, world);
        //handleMouse(scene, world);
        
        primaryStage.setTitle("3D Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        
    }
    
    private void buildMenuBar() {
        // create a menu 
        Menu menuFile = new Menu("File");

        // create menuitems 
        MenuItem m1 = new MenuItem("Open");
        MenuItem m2 = new MenuItem("Save");
        MenuItem m3 = new MenuItem("Exit");
        menuFile.getItems().add(m1);
        menuFile.getItems().add(m2);
        menuFile.getItems().add(m3);

        Menu menuView = new Menu("View");
        // create menuitems 
        m1 = new MenuItem("Show Axes");
        m2 = new MenuItem("Perspective");
        m3 = new MenuItem("Parallel");
        menuView.getItems().add(m1);
        menuView.getItems().add(m2);
        menuView.getItems().add(m3);

        Menu menuHelp = new Menu("Help");
        // create menuitems 
        m1 = new MenuItem("About");
        menuHelp.getItems().add(m1);

        //menuBar.setMinHeight(40.0);
        menuBar.setPadding(new Insets(6));

        menuBar.getMenus().addAll(menuFile, menuView, menuHelp);

    }

    private void buildCamera() {
        System.out.println("buildCamera()");
        rootV.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        System.out.println("buildAxes()");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    public Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(.01, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }
    private void handleMouse(SubScene scene, final Node rootV) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isSecondaryButtonDown()) {

                } else if (me.isMiddleButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
                } else if (me.isPrimaryButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
                }
            }
        });

        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double modifier = 1.0;
                double z = 0.0;
                double newZ = 0.0;
                if (event.getDeltaY() > 0) {
                    z = camera.getTranslateZ();
                    newZ = z + event.getY() * ZOOM_SPEED * modifier;

                } else if (event.getDeltaY() < 0) {
                    z = camera.getTranslateZ();
                    newZ = z - event.getY() * ZOOM_SPEED * modifier;
                }
                camera.setTranslateZ(newZ);
            }

        });
    }

    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                    case V:
                        moleculeGroup.setVisible(!moleculeGroup.isVisible());
                        break;
                }
            }
        });
    }


    private void buildMolecule() {
        //======================================================================
        // THIS IS THE IMPORTANT MATERIAL FOR THE TUTORIAL
        //======================================================================

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        // Molecule Hierarchy
        // [*] moleculeXform
        //     [*] oxygenXform
        //         [*] oxygenSphere
        //     [*] hydrogen1SideXform
        //         [*] hydrogen1Xform
        //             [*] hydrogen1Sphere
        //         [*] bond1Cylinder
        //     [*] hydrogen2SideXform
        //         [*] hydrogen2Xform
        //             [*] hydrogen2Sphere
        //         [*] bond2Cylinder
        moleculesampleapp.Xform moleculeXform = new moleculesampleapp.Xform();
        moleculesampleapp.Xform oxygenXform = new moleculesampleapp.Xform();
        moleculesampleapp.Xform hydrogen1SideXform = new moleculesampleapp.Xform();
        moleculesampleapp.Xform hydrogen1Xform = new moleculesampleapp.Xform();
        moleculesampleapp.Xform hydrogen2SideXform = new moleculesampleapp.Xform();
        moleculesampleapp.Xform hydrogen2Xform = new moleculesampleapp.Xform();

        Sphere oxygenSphere = new Sphere(40.0);
        oxygenSphere.setMaterial(redMaterial);

        Sphere hydrogen1Sphere = new Sphere(30.0);
        hydrogen1Sphere.setMaterial(whiteMaterial);
        hydrogen1Sphere.setTranslateX(0.0);

        Sphere hydrogen2Sphere = new Sphere(30.0);
        hydrogen2Sphere.setMaterial(whiteMaterial);
        hydrogen2Sphere.setTranslateZ(0.0);

        Cylinder bond1Cylinder = new Cylinder(5, 100);
        bond1Cylinder.setMaterial(greyMaterial);
        bond1Cylinder.setTranslateX(50.0);
        bond1Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond1Cylinder.setRotate(90.0);

        Cylinder bond2Cylinder = new Cylinder(5, 100);
        bond2Cylinder.setMaterial(greyMaterial);
        bond2Cylinder.setTranslateX(50.0);
        bond2Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond2Cylinder.setRotate(90.0);

        moleculeXform.getChildren().add(oxygenXform);
        moleculeXform.getChildren().add(hydrogen1SideXform);
        moleculeXform.getChildren().add(hydrogen2SideXform);
        oxygenXform.getChildren().add(oxygenSphere);
        hydrogen1SideXform.getChildren().add(hydrogen1Xform);
        hydrogen2SideXform.getChildren().add(hydrogen2Xform);
        hydrogen1Xform.getChildren().add(hydrogen1Sphere);
        hydrogen2Xform.getChildren().add(hydrogen2Sphere);
        hydrogen1SideXform.getChildren().add(bond1Cylinder);
        hydrogen2SideXform.getChildren().add(bond2Cylinder);

        hydrogen1Xform.setTx(100.0);
        hydrogen2Xform.setTx(100.0);
        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);

        moleculeGroup.getChildren().add(moleculeXform);

        world.getChildren().addAll(moleculeGroup);
    }
    
    private float[][] createNoise( int size) {
        float[][] noiseArray = new float[(int) size][(int) size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                double frequency = 10.0 / (double) size;

                double noise = ImprovedNoise.noise(x * frequency, y * frequency, 0);

                noiseArray[x][y] = (float) noise;
            }
        }

        return noiseArray;

    }
    
    public final static class ImprovedNoise {
    static public double noise(double x, double y, double z) {
       int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
           Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
           Z = (int)Math.floor(z) & 255;
       x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
       y -= Math.floor(y);                                // OF POINT IN CUBE.
       z -= Math.floor(z);
       double u = fade(x),                                // COMPUTE FADE CURVES
              v = fade(y),                                // FOR EACH OF X,Y,Z.
              w = fade(z);
       int A = p[X  ]+Y, AA = p[A]+Z, AB = p[A+1]+Z,      // HASH COORDINATES OF
           B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,

       return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ADD
                                      grad(p[BA  ], x-1, y  , z   )), // BLENDED
                              lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
                                      grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
                      lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
                                      grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
                              lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
                                      grad(p[BB+1], x-1, y-1, z-1 ))));
    }
    static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    static double lerp(double t, double a, double b) { return a + t * (b - a); }
    static double grad(int hash, double x, double y, double z) {
       int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
       double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
              v = h<4 ? y : h==12||h==14 ? x : z;
       return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
    }
    static final int p[] = new int[512], permutation[] = { 151,160,137,91,90,15,
    131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
    190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
    88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
    77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
    102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
    135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
    5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
    223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
    129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
    251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
    49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
    138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };
    static { for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i]; }
    }
    
    public void createSurface() {
        //Create Mesh new
        // mesh
        int size = 100;
        // perlin noise
        /*float[][] noiseArray = createNoise( size);
        
        TriangleMesh mesh = new TriangleMesh();

        // create points for x/z
        float amplification = 100; // amplification of noise
        
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                mesh.getPoints().addAll(x, noiseArray[x][z] * amplification, z);
            }
        }

        // texture
        int length = size;
        float total = length;

        for (float x = 0; x < length - 1; x++) {
            for (float y = 0; y < length - 1; y++) {

                float x0 = 8;
                float y0 = 8;
                float x1 = 8;
                float y1 =8;

                mesh.getTexCoords().addAll( //
                        x0, y0, // 0, top-left
                        x0, y1, // 1, bottom-left
                        x1, y1, // 2, top-right
                        x1, y1 // 3, bottom-right
                );


            }
        }

        // faces
        for (int x = 0; x < length - 1; x++) {
            for (int z = 0; z < length - 1; z++) {

                int tl = x * length + z; // top-left
                int bl = x * length + z + 1; // bottom-left
                int tr = (x + 1) * length + z; // top-right
                int br = (x + 1) * length + z + 1; // bottom-right

                int offset = (x * (length - 1) + z ) * 8 / 2; // div 2 because we have u AND v in the list

                // working
                mesh.getFaces().addAll(bl, offset + 1, tl, offset + 0, tr, offset + 2);
                mesh.getFaces().addAll(tr, offset + 2, br, offset + 3, bl, offset + 1);

            }
        }


        // material
        Image diffuseMap = createImage(size, noiseArray);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(diffuseMap);
        material.setSpecularColor(Color.WHITE);

        // mesh view
        MeshView meshView = new MeshView(mesh);
        meshView.setTranslateX(-0.5 * size);
        meshView.setTranslateZ(-0.5 * size);
        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.NONE);
        meshView.setDrawMode(DrawMode.LINE);
        meshView.setDepthTest(DepthTest.ENABLE);
         */
        //Create Mesh old

        HashMap<Integer, double[]> keySetNodes = new HashMap();

        keySetNodes.put(1, new double[]{0, 0.125, 0});
        keySetNodes.put(2, new double[]{6, 0.125, 0});
        keySetNodes.put(3, new double[]{12, 0.125, 0});
        keySetNodes.put(4, new double[]{18, 0.125, 0});
        keySetNodes.put(5, new double[]{24, 0.125, 0});
        keySetNodes.put(6, new double[]{30, 0.125, 0});
        keySetNodes.put(7, new double[]{36, 0.125, 0});
        keySetNodes.put(8, new double[]{42, 0.125, 0});
        keySetNodes.put(9, new double[]{48, 0.125, 0});
        keySetNodes.put(10, new double[]{54, 0.125, 0});
        keySetNodes.put(11, new double[]{60, 0.125, 0});
        keySetNodes.put(12, new double[]{60, 5, 0});
        keySetNodes.put(13, new double[]{60, 9.875, 0});
        keySetNodes.put(14, new double[]{54, 9.875, 0});
        keySetNodes.put(15, new double[]{48, 9.875, 0});
        keySetNodes.put(16, new double[]{42, 9.875, 0});
        keySetNodes.put(17, new double[]{36, 9.875, 0});
        keySetNodes.put(18, new double[]{30, 9.875, 0});
        keySetNodes.put(19, new double[]{24, 9.875, 0});
        keySetNodes.put(20, new double[]{18, 9.875, 0});
        keySetNodes.put(21, new double[]{12, 9.875, 0});
        keySetNodes.put(22, new double[]{6, 9.875, 0});
        keySetNodes.put(23, new double[]{0, 9.875, 0});
        keySetNodes.put(24, new double[]{0, 5, 0});
        keySetNodes.put(25, new double[]{6, 5, 0});
        keySetNodes.put(26, new double[]{12, 5, 0});
        keySetNodes.put(27, new double[]{18, 5, 0});
        keySetNodes.put(28, new double[]{24, 5, 0});
        keySetNodes.put(29, new double[]{30, 5, 0});
        keySetNodes.put(30, new double[]{36, 5, 0});
        keySetNodes.put(31, new double[]{42, 5, 0});
        keySetNodes.put(32, new double[]{48, 5, 0});
        keySetNodes.put(33, new double[]{54, 5, 0});
        keySetNodes.put(34, new double[]{60, 9.875, 2});
        keySetNodes.put(46, new double[]{0, 9.875, 2});
        keySetNodes.put(47, new double[]{6, 9.875, 2});
        keySetNodes.put(48, new double[]{12, 9.875, 2});
        keySetNodes.put(49, new double[]{18, 9.875, 2});
        keySetNodes.put(50, new double[]{24, 9.875, 2});
        keySetNodes.put(51, new double[]{30, 9.875, 2});
        keySetNodes.put(52, new double[]{36, 9.875, 2});
        keySetNodes.put(53, new double[]{42, 9.875, 2});
        keySetNodes.put(54, new double[]{48, 9.875, 2});
        keySetNodes.put(55, new double[]{54, 9.875, 2});
        keySetNodes.put(56, new double[]{60, 9.875, -2});
        keySetNodes.put(57, new double[]{54, 9.875, -2});
        keySetNodes.put(58, new double[]{48, 9.875, -2});
        keySetNodes.put(59, new double[]{42, 9.875, -2});
        keySetNodes.put(60, new double[]{36, 9.875, -2});
        keySetNodes.put(61, new double[]{30, 9.875, -2});
        keySetNodes.put(62, new double[]{24, 9.875, -2});
        keySetNodes.put(63, new double[]{18, 9.875, -2});
        keySetNodes.put(64, new double[]{12, 9.875, -2});
        keySetNodes.put(65, new double[]{6, 9.875, -2});
        keySetNodes.put(66, new double[]{0, 9.875, -2});
        keySetNodes.put(67, new double[]{60, 0.125, 2});
        keySetNodes.put(79, new double[]{0, 0.125, 2});
        keySetNodes.put(80, new double[]{6, 0.125, 2});
        keySetNodes.put(81, new double[]{12, 0.125, 2});
        keySetNodes.put(82, new double[]{18, 0.125, 2});
        keySetNodes.put(83, new double[]{24, 0.125, 2});
        keySetNodes.put(84, new double[]{30, 0.125, 2});
        keySetNodes.put(85, new double[]{36, 0.125, 2});
        keySetNodes.put(86, new double[]{42, 0.125, 2});
        keySetNodes.put(87, new double[]{48, 0.125, 2});
        keySetNodes.put(88, new double[]{54, 0.125, 2});
        keySetNodes.put(89, new double[]{60, 0.125, -2});
        keySetNodes.put(90, new double[]{54, 0.125, -2});
        keySetNodes.put(91, new double[]{48, 0.125, -2});
        keySetNodes.put(92, new double[]{42, 0.125, -2});
        keySetNodes.put(93, new double[]{36, 0.125, -2});
        keySetNodes.put(94, new double[]{30, 0.125, -2});
        keySetNodes.put(95, new double[]{24, 0.125, -2});
        keySetNodes.put(96, new double[]{18, 0.125, -2});
        keySetNodes.put(97, new double[]{12, 0.125, -2});
        keySetNodes.put(98, new double[]{6, 0.125, -2});
        keySetNodes.put(99, new double[]{0, 0.125, -2});
        keySetNodes.put(100, new double[]{60, 5, 0});

        ArrayList<int[]> coordsS = new ArrayList();
        coordsS.add(new int[]{1, 2, 25, 24});
        coordsS.add(new int[]{2, 3, 26, 25});
        coordsS.add(new int[]{3, 4, 27, 26});
        coordsS.add(new int[]{4, 5, 28, 27});
        coordsS.add(new int[]{5, 6, 29, 28});
        coordsS.add(new int[]{6, 7, 30, 29});
        coordsS.add(new int[]{7, 8, 31, 30});
        coordsS.add(new int[]{8, 9, 32, 31});
        coordsS.add(new int[]{9, 10, 33, 32});
        coordsS.add(new int[]{10, 11, 12, 33});
        coordsS.add(new int[]{24, 25, 22, 23});
        coordsS.add(new int[]{25, 26, 21, 22});
        coordsS.add(new int[]{26, 27, 20, 21});
        coordsS.add(new int[]{27, 28, 19, 20});
        coordsS.add(new int[]{28, 29, 18, 19});
        coordsS.add(new int[]{29, 30, 17, 18});
        coordsS.add(new int[]{30, 31, 16, 17});
        coordsS.add(new int[]{31, 32, 15, 16});
        coordsS.add(new int[]{32, 33, 14, 15});
        coordsS.add(new int[]{33, 12, 13, 14});
        coordsS.add(new int[]{34, 13, 14, 55});
        coordsS.add(new int[]{55, 14, 15, 54});
        coordsS.add(new int[]{54, 15, 16, 53});
        coordsS.add(new int[]{53, 16, 17, 52});
        coordsS.add(new int[]{52, 17, 18, 51});
        coordsS.add(new int[]{51, 18, 19, 50});
        coordsS.add(new int[]{50, 19, 20, 49});
        coordsS.add(new int[]{49, 20, 21, 48});
        coordsS.add(new int[]{48, 21, 22, 47});
        coordsS.add(new int[]{47, 22, 23, 46});
        coordsS.add(new int[]{23, 22, 65, 66});
        coordsS.add(new int[]{22, 21, 64, 65});
        coordsS.add(new int[]{21, 20, 63, 64});
        coordsS.add(new int[]{20, 19, 62, 63});
        coordsS.add(new int[]{19, 18, 61, 62});
        coordsS.add(new int[]{18, 17, 60, 61});
        coordsS.add(new int[]{17, 16, 59, 60});
        coordsS.add(new int[]{16, 15, 58, 59});
        coordsS.add(new int[]{15, 14, 57, 58});
        coordsS.add(new int[]{14, 13, 56, 57});
        coordsS.add(new int[]{67, 11, 10, 88});
        coordsS.add(new int[]{88, 10, 9, 87});
        coordsS.add(new int[]{87, 9, 8, 86});
        coordsS.add(new int[]{86, 8, 7, 85});
        coordsS.add(new int[]{85, 7, 6, 84});
        coordsS.add(new int[]{84, 6, 5, 83});
        coordsS.add(new int[]{83, 5, 4, 82});
        coordsS.add(new int[]{82, 4, 3, 81});
        coordsS.add(new int[]{81, 3, 2, 80});
        coordsS.add(new int[]{80, 2, 1, 79});
        coordsS.add(new int[]{1, 2, 98, 99});
        coordsS.add(new int[]{2, 3, 97, 98});
        coordsS.add(new int[]{3, 4, 96, 97});
        coordsS.add(new int[]{4, 5, 95, 96});
        coordsS.add(new int[]{5, 6, 94, 95});
        coordsS.add(new int[]{6, 7, 93, 94});
        coordsS.add(new int[]{7, 8, 92, 93});
        coordsS.add(new int[]{8, 9, 91, 92});
        coordsS.add(new int[]{9, 10, 90, 91});
        coordsS.add(new int[]{10, 11, 89, 90});

        //Point 1 (x,y,z)
        //float f = (float)d;
        //Point 2 (x,y,z)
        //Point 3 (x,y,z)
        //Point 4 (x,y,z)
        for (int i = 0; i < coordsS.size(); i++) {
            addTriangle(coordsS.get(i), keySetNodes);
        }
        world.getChildren().add(surfaceGroup);
        world.getChildren().addAll(lineGroup);
    }

    public void addTriangle(int[] ids, HashMap<Integer, double[]> points) {
        

        // float d = -s / 3;// Side
        //Point 1 (x,y,z)
        double xp1 = points.get(ids[0])[0];
        double yp1 = points.get(ids[0])[1];
        double zp1 = points.get(ids[0])[2];

        float xcp1 = (float) yp1;
        float ycp1 = (float) -zp1;
        float zcp1 = (float) -xp1;
        //Point 2 (x,y,z)
        double xp2 = points.get(ids[1])[0];
        double yp2 = points.get(ids[1])[1];
        double zp2 = points.get(ids[1])[2];

        float xcp2 = (float) yp2;
        float ycp2 = (float) -zp2;
        float zcp2 = (float) -xp2;
        //Point 3 (x,y,z)
        double xp3 = points.get(ids[2])[0];
        double yp3 = points.get(ids[2])[1];
        double zp3 = points.get(ids[2])[2];
        float xcp3 = (float) yp3;
        float ycp3 = (float) -zp3;
        float zcp3 = (float) -xp3;
        //Point 4 (x,y,z)
        double xp4 = points.get(ids[3])[0];
        double yp4 = points.get(ids[3])[1];
        double zp4 = points.get(ids[3])[2];
        float xcp4 = (float) yp4;
        float ycp4 = (float) -zp4;
        float zcp4 = (float) -xp4;

        float h = 150; // Height
        float s = 150; // Side
        float hs = s / 2;
        float hs2 = s / 3;
        // coordinates of the mapped image
        moleculesampleapp.Xform allElements = new moleculesampleapp.Xform();
        moleculesampleapp.Xform allLines = new moleculesampleapp.Xform();
        moleculesampleapp.Xform frontelements = new moleculesampleapp.Xform();
        moleculesampleapp.Xform backelements = new moleculesampleapp.Xform();
        moleculesampleapp.Xform oneline = new moleculesampleapp.Xform();
        moleculesampleapp.Xform secondline = new moleculesampleapp.Xform();
        allElements.getChildren().add(backelements);
        allElements.getChildren().add(frontelements);
        
        //TriangleMesh pyramidMesh = new TriangleMesh();
        TriangleMesh pyramidMesh = new TriangleMesh();     
        float xcp0 = (xcp1+xcp2+xcp3+xcp4)/4;
        float ycp0 = (ycp1+ycp2+ycp3+ycp4)/4;
        float zcp0 = (zcp1+zcp2+zcp3+zcp4)/4;
        pyramidMesh.getPoints().addAll( //
                xcp3, ycp3, zcp3, // A 0 Top of Pyramid
                xcp1, ycp1, zcp1, // B 1
                xcp2, ycp2, zcp2, // C 2
                 xcp1, ycp1, zcp1, // B 1
                xcp2, ycp2, zcp2 // E 4
        );
        
        //Create first line
        Point3D startPoint = new Point3D(xcp2, ycp2, zcp2);
        Point3D endPoint = new Point3D(xcp1, ycp1, zcp1);
                
        final Shape3D oneLine = createConnection(startPoint, endPoint);
        
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        
        oneLine.setMaterial(blueMaterial);
        
        
        Point3D startPointb = new Point3D(xcp3, ycp3, zcp3);
        Point3D endPointb = new Point3D(xcp2, ycp2, zcp2);
        final Shape3D endLine = createConnection(startPointb, endPointb);
        
        endLine.setMaterial(blueMaterial);
        
        lineGroup.getChildren().addAll(oneLine,endLine);
        
        
        
        ////
        float x0 = 0.0f;
        float y0 = 0.0f;
        float x1 = 1.0f;
        float y1 = 1.0f;
        pyramidMesh.getTexCoords().addAll( //
                x0, y0, // 0
                x0, y1, // 1
                x1, y0, // 2
                x1, y1 // 3
        );
        pyramidMesh.getFaces().addAll(// index of point, index of texture, index of point, index of texture, index of point, index of texture
                0, 0, 1, 1, 2, 3, // ABC (counter clockwise)
                0, 0, 2, 1, 3, 3, // ACD (counter clockwise)
                0, 0, 3, 1, 4, 3, // ADE (counter clockwise)
                0, 0, 4, 1, 1, 3, // AEB (counter clockwise)
                4, 0, 3, 1, 2, 3, // EDC (Bottom first triangle clock wise)
                2, 0, 1, 1, 4, 3 // CBE (Bottom second triangle clock wise)
        );
        MeshView pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);
        /*pyramid.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                PhongMaterial blackMaterial = new PhongMaterial();
                blackMaterial.setDiffuseColor(Color.BLACK);
                blackMaterial.setSpecularColor(Color.BLACK);
                
                pyramid.setMaterial(blackMaterial);
            }
        });*/
       
        
        PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.RED);
        whiteMaterial.setSpecularColor(Color.LIGHTSALMON);
        pyramid.setMaterial(whiteMaterial);
        pyramid.setTranslateX(0);
        pyramid.setTranslateY(0);
        pyramid.setTranslateZ(0);
        
        ////TriangleMesh pyramidMesh = new TriangleMesh();
        TriangleMesh pyramidMesh2 = new TriangleMesh();     

        pyramidMesh2.getPoints().addAll( //
                xcp1, ycp1, zcp1, // A 0 Top of Pyramid
                xcp3, ycp3, zcp3, // D 3
                xcp4, ycp4, zcp4, // C 2
                xcp3, ycp3, zcp3, // D 3
                xcp4, ycp4, zcp4 // E 4
        );
        pyramidMesh2.getTexCoords().addAll( //
                x0, y0, // 0
                x0, y1, // 1
                x1, y0, // 2
                x1, y1 // 3
        );
        pyramidMesh2.getFaces().addAll(// index of point, index of texture, index of point, index of texture, index of point, index of texture
                0, 0, 1, 1, 2, 3, // ABC (counter clockwise)
                0, 0, 2, 1, 3, 3, // ACD (counter clockwise)
                0, 0, 3, 1, 4, 3, // ADE (counter clockwise)
                0, 0, 4, 1, 1, 3, // AEB (counter clockwise)
                4, 0, 3, 1, 2, 3, // EDC (Bottom first triangle clock wise)
                2, 0, 1, 1, 4, 3 // CBE (Bottom second triangle clock wise)
        );
        //CreateSecont line
        Point3D startPoint2 = new Point3D(xcp3, ycp3, zcp3);
        Point3D endPoint2 = new Point3D(xcp4, ycp4, zcp4);
                
        final Shape3D oneLine2 = createConnection(startPoint2, endPoint2);
        
    
        oneLine2.setMaterial(blueMaterial);
        
        
        
        
        //Create first line
        Point3D startPoint2b = new Point3D(xcp1, ycp1, zcp1);
        Point3D endPoint2b = new Point3D(xcp4, ycp4, zcp4);
        final Shape3D endLine2 = createConnection(startPoint2b, endPoint2b);
        endLine2.setMaterial(blueMaterial);
        
        lineGroup.getChildren().addAll(oneLine2,endLine2);
        
        
        MeshView pyramid2 = new MeshView(pyramidMesh2);
        pyramid2.setDrawMode(DrawMode.FILL);
        PhongMaterial whiteMaterial2 = new PhongMaterial();
        whiteMaterial2.setDiffuseColor(Color.RED);
        whiteMaterial2.setSpecularColor(Color.LIGHTSALMON);
        pyramid2.setMaterial(whiteMaterial2);
        pyramid2.setTranslateX(0);
        pyramid2.setTranslateY(0);
        pyramid2.setTranslateZ(0);
        backelements.getChildren().add(pyramid2);
        frontelements.getChildren().add(pyramid);
        
        
         pyramid.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                PhongMaterial blackMaterial = new PhongMaterial();
                blackMaterial.setDiffuseColor(Color.BLACK);
                blackMaterial.setSpecularColor(Color.BLACK);
                
                pyramid.setMaterial(blackMaterial);
                pyramid2.setMaterial(blackMaterial);
            }
        });
        pyramid.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                PhongMaterial blackMaterial = new PhongMaterial();
                blackMaterial.setDiffuseColor(Color.RED);
                blackMaterial.setSpecularColor(Color.LIGHTSALMON);
                
                pyramid.setMaterial(blackMaterial);
                pyramid2.setMaterial(blackMaterial);
            }
        });
        pyramid2.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                PhongMaterial blackMaterial = new PhongMaterial();
                blackMaterial.setDiffuseColor(Color.BLACK);
                blackMaterial.setSpecularColor(Color.BLACK);
                
                pyramid.setMaterial(blackMaterial);
                pyramid2.setMaterial(blackMaterial);
            }
        });
        pyramid2.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                PhongMaterial blackMaterial = new PhongMaterial();
                blackMaterial.setDiffuseColor(Color.RED);
                blackMaterial.setSpecularColor(Color.LIGHTSALMON);
                
                pyramid.setMaterial(blackMaterial);
                pyramid2.setMaterial(blackMaterial);
            }
        });
        surfaceGroup.getChildren().add(allElements);
        
    }
    public javax.media.j3d.Shape3D plotLines(Point3d coords[]) {
        //Point3d coords[] = new Point3d[4];

        Appearance app = new Appearance();
        ColoringAttributes caLines = new ColoringAttributes();
        caLines.setColor(new Color3f(new java.awt.Color(0, 0, 0)));

        app.setColoringAttributes(caLines);
   /*     coords[0] = new Point3d(0.0d, 0.0d, 0.0d);
        coords[1] = new Point3d(-10.0d, 0.0d, 0.0d);
        coords[2] = new Point3d(-10.0d, -10.0d, 0.0d);
        coords[3] = new Point3d(0.0d, -10.0d, 0.0d);*/

        int vertexCounts[] = {coords.length};

        LineStripArray lines = new LineStripArray(coords.length,
                LineStripArray.COORDINATES, vertexCounts);
       
        lines.setCoordinates(0, coords);
        javax.media.j3d.Shape3D shape = new javax.media.j3d.Shape3D(lines, app);
        return shape;
    }
    public void createPyramid() {
        // create pyramid with diffuse map
        float h = 150; // Height
        float s = 150; // Side
        float hs = s / 2;
        float hs2 = s / 3;
        // coordinates of the mapped image

        TriangleMesh pyramidMesh = new TriangleMesh();

        pyramidMesh.getPoints().addAll( //
                hs, h, -hs, // A 0 Top of Pyramid
                hs, h, -hs, // B 1
                hs2, h, hs, // C 2
                -hs, h, hs, // D 3
                -hs, h, -hs // E 4
        );
        float x0 = 0.0f;
        float y0 = 0.0f;
        float x1 = 1.0f;
        float y1 = 1.0f;
        pyramidMesh.getTexCoords().addAll( //
                x0, y0, // 0
                x0, y1, // 1
                x1, y0, // 2
                x1, y1 // 3
        );

        pyramidMesh.getFaces().addAll(// index of point, index of texture, index of point, index of texture, index of point, index of texture
                0, 0, 1, 1, 2, 3, // ABC (counter clockwise)
                0, 0, 2, 1, 3, 3, // ACD (counter clockwise)
                0, 0, 3, 1, 4, 3, // ADE (counter clockwise)
                0, 0, 4, 1, 1, 3, // AEB (counter clockwise)
                4, 0, 3, 1, 2, 3, // EDC (Bottom first triangle clock wise)
                2, 0, 1, 1, 4, 3 // CBE (Bottom second triangle clock wise)
        );

        MeshView pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);

        PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.RED);
        whiteMaterial.setSpecularColor(Color.RED);
        pyramid.setMaterial(whiteMaterial);
        pyramid.setTranslateX(0);
        pyramid.setTranslateY(0);
        pyramid.setTranslateZ(0);
        surfaceGroup.getChildren().add(pyramid);
        world.getChildren().add(surfaceGroup);
    }
    
    public Image createImage(double size, float[][] noise) {

        int width = (int) size;
        int height = (int) size;

        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                float value = noise[x][y];

                double gray = normalizeValue(value, -.5, .5, 0., 1.);

                gray = clamp(gray, 0, 1);

                Color color = Color.RED.interpolate(Color.YELLOW, gray);

                pw.setColor(x, y, color);

            }
        }

        return wr;

    }
    public static double normalizeValue(double value, double min, double max, double newMin, double newMax) {

        return (value - min) * (newMax - newMin) / (max - min) + newMin;

    }
    public static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }
    private void buildAnalysisControls() {
        TitledPane pane1 = new TitledPane("Sections", new Label("Show all sections"));
        TitledPane pane2 = new TitledPane("Material", new Label("Show all materials"));
        TitledPane pane3 = new TitledPane("Recovery Points", new Label("Show recovery point table"));

        //accordion.getPanes().add(pane1);
        //accordion.getPanes().add(pane2);
        //accordion.getPanes().add(pane3);
        //accordion.setMinWidth(250.0);
    }

    public class rPoints {

        private Double xr = null;
        private Double yr = null;

        public rPoints() {
        }

        public rPoints(Double xr, Double yr) {
            this.xr = xr;
            this.yr = yr;
        }

        public Double getX() {
            return xr;
        }

        public void setX(Double xr) {
            this.xr = xr;
        }

        public Double getY() {
            return yr;
        }

        public void setY(Double yr) {
            this.yr = yr;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
