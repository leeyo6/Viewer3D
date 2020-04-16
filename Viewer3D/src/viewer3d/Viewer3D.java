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
import java.util.Random;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.stage.Stage;
import javax.media.j3d.Appearance;
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

        final Box xAxis = new Box(AXIS_LENGTH, 10, 10);
        final Box yAxis = new Box(10, AXIS_LENGTH, 10);
        final Box zAxis = new Box(10, 10, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
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
        float[][] noiseArray = createNoise( size);
        
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
        
        //Create Mesh old
        
      /*  TriangleMesh pyramidMesh = new TriangleMesh();
        pyramidMesh.getTexCoords().addAll(0, 0);
        float h = 15;                    // Height
       
        float s = 30;  
         float d = -s / 3;// Side
        pyramidMesh.getPoints().addAll(
              /*  0, 0, 0, // Point 0 - Top
                0, h, -s / 2, // Point 1 - Front
                0, h, -s / 2, // Point 2 - Left
                s / 2, h, 0, // Point 3 - Back
                s / 2, h, 0 // Point 4 - Right
                0, 0, 0, // Point 0 - Top
                0, h, -s / 2, // Point 1 - Front
                -s / 2, h, 0, // Point 2 - Left
                s / 2, h, 0, // Point 3 - Back
                0, h, s / 2 // Point 4 - Right*/
           /*     0, h, -s / 2, // Point 0 - Top
                0, h, -s / 2, // Point 1 - Front
                -d, h, 0, // Point 2 - Left
                s / 2, h, 0, // Point 3 - Back
                0, h, s / 2 // Point 4 - Right
        );

        pyramidMesh.getFaces().addAll(
                0, 0, 2, 0, 1, 0, // Front left face
                0, 0, 1, 0, 3, 0, // Front right face
                0, 0, 3, 0, 4, 0, // Back right face
                0, 0, 4, 0, 2, 0, // Back left face
                4, 0, 1, 0, 2, 0, // Bottom rear face
                4, 0, 3, 0, 1, 0 // Bottom front face
        );

        MeshView pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);
     
        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.RED);
        whiteMaterial.setSpecularColor(Color.LIGHTCYAN);
        pyramid.setMaterial(whiteMaterial);
        pyramid.setTranslateX(0);
        pyramid.setTranslateY(0);
        pyramid.setTranslateZ(0);*/
        surfaceGroup.getChildren().add(meshView);
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
