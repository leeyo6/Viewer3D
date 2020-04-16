/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer3d;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 *
 * @author leeyo
 */
public class Viewer3D extends Application {
    
    private Group rootV = new Group();
    final moleculesampleapp.Xform axisGroup = new moleculesampleapp.Xform();
    final moleculesampleapp.Xform moleculeGroup = new moleculesampleapp.Xform();
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
        rootV.getChildren().add(world);
        rootV.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        buildAxes();
        buildMolecule();

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
        axisGroup.setVisible(false);
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
