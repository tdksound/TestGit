package app;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Hong
 *
 * Check ExApp1 and ExApp2 for more information Create a inner runnable class to
 * allow arguments and update components
 *
 * - when both Max and 649 is selected, the first executable one will be
 * randomly selected. - note the passing the component to thread that is being
 * updated like log - use runLater and it's parameters - random numbers or
 * boolean - follow sub window to caller window
 *
 * Check - set time to close stage - Label doesn't get updated with Thread -
 * Borderless TextField - How to remove focus on or move to another component -
 * vBox.requestFocus(); - Window frame icon can be changed -
 * text.setFont(ITALIC_FONT); text.setFill(Color.web("595959"));
 *
 */
public class LottoWinnerApp_V1 extends Application {

    private final Font ITALIC_FONT = Font.font("System Regular", FontPosture.ITALIC, Font.getDefault().getSize());

    private int minSleep = 1000, maxSleep = 3000;	// for sleep
    private String lblStyle = "-fx-font-weight: bold; -fx-text-fill: #666;";
    private String noBorderTfStyle = "-fx-background-color: #f4f4f4; -fx-background-insets: 0; -fx-padding: 1 1 1 1";

    private TextField m1, m2, m3, m4, m5, m6, m7;	// max numbers
    private TextField s1, s2, s3, s4, s5, s6;		// 649 numbers

    private CheckBox maxCtrlChk, sixCtrlChk;
    private DatePicker maxCtrlDp, sixCtrlDp;

    private ImageView maxRunIv, sixRunIv;
    private Button genNumBtn, maxGetBtn, sixGetBtn;

    private TextArea infoTa;
    private VBox vBox; // main frame

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Generate Winning Numbers - v1");

        vBox = new VBox();
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(5);
        // Add icon
//	Image image = new Image(getClass().getClassLoader().getResource("images/add_s.png").toExternalForm());
//	primaryStage.getIcons().add(image);

        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

        /**
         * Row: 1, control HBox 0 *
         */
        HBox ctrlHb = new HBox();
        initCtrlBoard(ctrlHb);
        vBox.getChildren().add(0, ctrlHb);

        /**
         * Row: 2,
         */
        GridPane playBrdGp = new GridPane();
        initPlayBoard(playBrdGp);	// pass GridPane to fit the width and grow the same
        vBox.getChildren().add(1, playBrdGp);

        /**
         * log status
         */
        infoTa = new TextArea();
        infoTa.setEditable(false);
        VBox.setVgrow(infoTa, Priority.ALWAYS);
        vBox.getChildren().add(infoTa);

        // relocate the focus on the first component of maxCtrlChk
        maxCtrlChk.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstTime.get()) {
//              vBox.requestFocus(); 		// Delegate the focus to container
//              firstTime.setValue(false); 	// Variable value changed for future references
                genNumBtn.requestFocus();
            }
        });

        Scene scene = new Scene(vBox, 450, 300);
//	scene.getStylesheets().add(getClass().getResource("/resources/lottoWinner.css").toExternalForm());
//	scene.getStylesheets().add(getClass().getClassLoader().getResource("styles/lottoWinner.css").toExternalForm());
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("styles/lottoWinner.css");
        scene.getStylesheets().add(url.toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // TODO Row: 1
    private void initCtrlBoard(HBox hBox) {
        Label selPlayLbl = new Label("Select Play :   ");
        selPlayLbl.setStyle(lblStyle);
        // row1 in GridPane
        Label maxCtrlLbl = new Label("Max :");
        maxCtrlLbl.setStyle(lblStyle);
        maxCtrlChk = new CheckBox();
        maxCtrlChk.setSelected(true);
        Label maxCtrlOnL = new Label("on");
        maxCtrlDp = new DatePicker();
        maxCtrlDp.setPrefWidth(100);
        // row2 in GridPane for gap
        Region gap = new Region();
        gap.setMinHeight(4);
        // row3 in GridPane
        Label sixCtrlLbl = new Label("649 :");
        maxCtrlLbl.setStyle(lblStyle);
        sixCtrlChk = new CheckBox();
        sixCtrlChk.setSelected(true);
        Label sixCtrlOnL = new Label("on");
        sixCtrlDp = new DatePicker();
        sixCtrlDp.setPrefWidth(100);

        genNumBtn = new Button("Generate");	//genNumBtn.setPrefWidth(100);
        genNumBtn.setOnAction(e -> generateAct());

        GridPane ctrlGp = new GridPane();
        // set width by pixel not by percent
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(20);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPrefWidth(110);
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPrefWidth(100);
        ctrlGp.getColumnConstraints().addAll(col1, col2, col3, col4, col5);
        GridPane.setRowIndex(maxCtrlLbl, 0);
        GridPane.setColumnIndex(maxCtrlLbl, 0);
        GridPane.setRowIndex(maxCtrlChk, 0);
        GridPane.setColumnIndex(maxCtrlChk, 1);
        GridPane.setRowIndex(maxCtrlOnL, 0);
        GridPane.setColumnIndex(maxCtrlOnL, 2);
        GridPane.setRowIndex(maxCtrlDp, 0);
        GridPane.setColumnIndex(maxCtrlDp, 3);
        GridPane.setRowIndex(genNumBtn, 0);
        GridPane.setColumnIndex(genNumBtn, 4);
        GridPane.setRowSpan(genNumBtn, 3);

        GridPane.setRowIndex(gap, 1);
        GridPane.setColumnIndex(gap, 0);
        GridPane.setColumnSpan(gap, 3);

        GridPane.setRowIndex(sixCtrlLbl, 2);
        GridPane.setColumnIndex(sixCtrlLbl, 0);
        GridPane.setRowIndex(sixCtrlChk, 2);
        GridPane.setColumnIndex(sixCtrlChk, 1);
        GridPane.setRowIndex(sixCtrlOnL, 2);
        GridPane.setColumnIndex(sixCtrlOnL, 2);
        GridPane.setRowIndex(sixCtrlDp, 2);
        GridPane.setColumnIndex(sixCtrlDp, 3);

        ctrlGp.getChildren().addAll(maxCtrlLbl, maxCtrlChk, maxCtrlOnL, maxCtrlDp);	// row1
        ctrlGp.getChildren().add(gap);												// row2 for gap
        ctrlGp.getChildren().addAll(sixCtrlLbl, sixCtrlChk, sixCtrlOnL, sixCtrlDp);	// row3
        ctrlGp.getChildren().add(genNumBtn);

        hBox.getChildren().addAll(selPlayLbl, ctrlGp);
        hBox.setStyle("-fx-border-color: #ccc");
//		hBox.setStyle("-fx-background-color: #ccc, #efefef; -fx-background-insets: 0, 1 1 1 1");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5));
        hBox.setSpacing(5);
    }
    // TODO Row: 2

    private void initPlayBoard(GridPane gridPane) {
        // init numbers storages
        m1 = new TextField();
        m2 = new TextField();
        m3 = new TextField();
        m4 = new TextField();
        m5 = new TextField();
        m6 = new TextField();
        m7 = new TextField();
        s1 = new TextField();
        s2 = new TextField();
        s3 = new TextField();
        s4 = new TextField();
        s5 = new TextField();
        s6 = new TextField();

        m1.setEditable(false);
        m2.setEditable(false);
        m3.setEditable(false);
        m4.setEditable(false);
        m5.setEditable(false);
        m6.setEditable(false);
        m7.setEditable(false);
        s1.setEditable(false);
        s2.setEditable(false);
        s3.setEditable(false);
        s4.setEditable(false);
        s5.setEditable(false);
        s6.setEditable(false);

        m1.setMaxWidth(35);
        m2.setMaxWidth(35);
        m3.setMaxWidth(35);
        m4.setMaxWidth(35);
        m5.setMaxWidth(35);
        m6.setMaxWidth(35);
        m7.setMaxWidth(35);
        s1.setMaxWidth(35);
        s2.setMaxWidth(35);
        s3.setMaxWidth(35);
        s4.setMaxWidth(35);
        s5.setMaxWidth(35);
        s6.setMaxWidth(35);

        m1.setAlignment(Pos.CENTER);
        m2.setAlignment(Pos.CENTER);
        m3.setAlignment(Pos.CENTER);
        m4.setAlignment(Pos.CENTER);
        m5.setAlignment(Pos.CENTER);
        m6.setAlignment(Pos.CENTER);
        m7.setAlignment(Pos.CENTER);
        s1.setAlignment(Pos.CENTER);
        s2.setAlignment(Pos.CENTER);
        s3.setAlignment(Pos.CENTER);
        s4.setAlignment(Pos.CENTER);
        s5.setAlignment(Pos.CENTER);
        s6.setAlignment(Pos.CENTER);

        // init buttons to get numbers in order in a dialog
        maxGetBtn = new Button("Get");
        maxGetBtn.setDisable(true);
        sixGetBtn = new Button("Get");
        sixGetBtn.setDisable(true);
        maxGetBtn.setOnAction(e -> showWinNumsPopup("Max"));	// getNumbers("Max")
        sixGetBtn.setOnAction(e -> showWinNumsPopup("Six")); 	// getNumbers("Six")
        // init progress images
	maxRunIv = new ImageView(new Image(getClass().getClassLoader().getResource("images/runingCircle.gif").toExternalForm()));
	sixRunIv = new ImageView(new Image(getClass().getClassLoader().getResource("images/runingCircle.gif").toString()));

	ImageView maxLogo = new ImageView(new Image(getClass().getClassLoader().getResource("images/Lotto_Max_64.png").toExternalForm()));
	maxLogo.setFitHeight(25); 		maxLogo.setPreserveRatio(true);
	ImageView sixLogo = new ImageView(new Image(getClass().getClassLoader().getResource("images/Lotto_649_64.png").toExternalForm()));
        
	sixLogo.setFitHeight(25); 		sixLogo.setPreserveRatio(true);
        Tooltip.install(maxGetBtn, new Tooltip("Get winning numbers in order."));
        Tooltip.install(sixGetBtn, new Tooltip("Get winning numbers in order."));
	Tooltip.install(maxRunIv, new Tooltip("Winning number is being generated...."));
	Tooltip.install(sixRunIv, new Tooltip("Winning number is being generated...."));

        int rows = 3;
        int cols = 10;
        GridPane grid = gridPane;
        grid.getStyleClass().add("game-grid");

        for (int c = 0; c < cols; c++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.SOMETIMES);	// fit grid to parent frame of VBox 
            grid.getColumnConstraints().add(column);
        }

        for (int r = 0; r < rows; r++) {
            RowConstraints row = new RowConstraints(25);
            if (r > 0) {
                row.setPrefHeight(35);
            }
            grid.getRowConstraints().add(row);
        }

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                StackPane pane = new StackPane();
//                pane.setOnMouseReleased(e -> {
//                	pane.getChildren().add(new Label("00"));	// pane.getChildren().add(Anims.getAtoms(1));
//                });
                pane.getStyleClass().add("game-grid-cell");
                pane.setStyle("-fx-background-color: #ccc, #f4f4f4;");		// overwrite

                if (c == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (r == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, c, r);

                if (r == 0) {
                    // add header of row 0
                    if (r == 0 && c == 1) {
                        pane.getChildren().add(new Label("1"));
                    }
                    if (r == 0 && c == 2) {
                        pane.getChildren().add(new Label("2"));
                    }
                    if (r == 0 && c == 3) {
                        pane.getChildren().add(new Label("3"));
                    }
                    if (r == 0 && c == 4) {
                        pane.getChildren().add(new Label("4"));
                    }
                    if (r == 0 && c == 5) {
                        pane.getChildren().add(new Label("5"));
                    }
                    if (r == 0 && c == 6) {
                        pane.getChildren().add(new Label("6"));
                    }
                    if (r == 0 && c == 7) {
                        pane.getChildren().add(new Label("7"));
                    }
                } else if (r == 1) {
                    // add max label
                    if (r == 1 && c == 1) {
                        pane.getChildren().add(m1);
                    }
                    if (r == 1 && c == 2) {
                        pane.getChildren().add(m2);
                    }
                    if (r == 1 && c == 3) {
                        pane.getChildren().add(m3);
                    }
                    if (r == 1 && c == 4) {
                        pane.getChildren().add(m4);
                    }
                    if (r == 1 && c == 5) {
                        pane.getChildren().add(m5);
                    }
                    if (r == 1 && c == 6) {
                        pane.getChildren().add(m6);
                    }
                    if (r == 1 && c == 7) {
                        pane.getChildren().add(m7);
                    }
                    // add header of column 0
//                    if (r == 1 && c == 0) {pane.getChildren().add(new Label("Max"));}
                    if (r==1 && c==0) pane.getChildren().add(maxLogo);
                    // add progress image
                    if (r==1 && c==8) {	pane.getChildren().add(maxRunIv);	maxRunIv.setVisible(false);	}
                    if (r == 1 && c == 9) {
                        pane.getChildren().add(maxGetBtn);	// get numbers in order 
                    }
                } else if (r == 2) {
                    // add 649 label
                    if (r == 2 && c == 1) {
                        pane.getChildren().add(s1);
                    }
                    if (r == 2 && c == 2) {
                        pane.getChildren().add(s2);
                    }
                    if (r == 2 && c == 3) {
                        pane.getChildren().add(s3);
                    }
                    if (r == 2 && c == 4) {
                        pane.getChildren().add(s4);
                    }
                    if (r == 2 && c == 5) {
                        pane.getChildren().add(s5);
                    }
                    if (r == 2 && c == 6) {
                        pane.getChildren().add(s6);
                    }
                    // add header of column 0
//                    if (r == 2 && c == 0) pane.getChildren().add(new Label("649"));
                    if (r==2 && c==0) pane.getChildren().add(sixLogo);
                    // add progress image
                    if (r==2 && c==8) {	pane.getChildren().add(sixRunIv);	sixRunIv.setVisible(false); }
                    if (r == 2 && c == 9) {
                        pane.getChildren().add(sixGetBtn);	// get numbers in order
                    }
                }
            }
        }
    }

    private void pln(Object line) {
        System.out.println(line);
    }

    /**
     * Update the textField with second ticking
     *
     * @param durSec
     */
    private void closingInfoRun(int durSec, TextField timerTf) {
        Thread taskThread = new Thread(new Runnable() {
            int sec = durSec;

            @Override
            public void run() {
                for (int i = 0; i < durSec; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            timerTf.setText(sec + "");
                        }
                    });
                    sec--;
                }
            }
        });
        taskThread.start();
    }

    /**
     * TODO open dialog with time in seconds check borderless TestField (Label
     * doesn't get updated from Thread)
     */
    private void showWinNumsPopup(String playName) {
        int showSec = 60;
        TextField timerTf = new TextField(showSec + "");
        timerTf.setMaxWidth(20);
        timerTf.setAlignment(Pos.CENTER);
        timerTf.setStyle(noBorderTfStyle);

        closingInfoRun(showSec, timerTf);

        Stage popup = new Stage();
        popup.setWidth(290);
        popup.setHeight(160);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Winning Numbers");
        // position
        Point2D xy = getFramePoint(popup.getWidth(), popup.getHeight());
        popup.setX(xy.getX());
        popup.setY(xy.getY());

        // contents
        List<TextField> tfL = new ArrayList<TextField>();
        if (playName.equalsIgnoreCase("Max")) {
            tfL.add(m1);
            tfL.add(m2);
            tfL.add(m3);
            tfL.add(m4);
            tfL.add(m5);
            tfL.add(m6);
            tfL.add(m7);
        } else if (playName.equalsIgnoreCase("Six")) {
            tfL.add(s1);
            tfL.add(s2);
            tfL.add(s3);
            tfL.add(s4);
            tfL.add(s5);
            tfL.add(s6);
        } else if (playName.equalsIgnoreCase("Test")) {
            tfL.add(new TextField("2"));
            tfL.add(new TextField("20"));
            tfL.add(new TextField("3"));
            tfL.add(new TextField("32"));
            tfL.add(new TextField("4"));
            tfL.add(new TextField("43"));
        }

        List<Integer> numList = new ArrayList<Integer>();
        for (TextField tf : tfL) {
            numList.add(Integer.parseInt(tf.getText()));
        }
        Collections.sort(numList);  // sort

        String nums = "", showPlayName = "", playDate = "";
        for (int num : numList) {
            nums += num + ", ";
        }
        if (nums.length() > 0) {
            nums = nums.substring(0, nums.length() - 2);
        }

        showPlayName = playName.equalsIgnoreCase("six") ? "649" : playName;
        playDate = playName.equalsIgnoreCase("six")
                ? (sixCtrlDp.getValue().format(DateTimeFormatter.ofPattern("EEEE, MMM. dd, yyyy")))
                : (maxCtrlDp.getValue().format(DateTimeFormatter.ofPattern("EEEE, MMM. dd, yyyy")));

        // GridPane
        VBox mainVB = new VBox();
        mainVB.setPadding(new Insets(10));
        mainVB.setSpacing(5);
        mainVB.setAlignment(Pos.TOP_LEFT);

        GridPane grid = new GridPane();
        int rows = 3, cols = 2;
        grid.setHgap(0);
//		grid.getStyleClass().add("game-grid");
//		grid.setStyle("-fx-grid-lines-visible: true");	// line shows thick

        for (int r = 0; r < rows; r++) {
            RowConstraints row = new RowConstraints();
            grid.getRowConstraints().add(row);
        }

        for (int c = 0; c < cols; c++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.SOMETIMES);	// fit grid to parent frame of VBox 
            grid.getColumnConstraints().add(column);
        }

        Label nameL = new Label("Play Name :");
        nameL.setStyle("-fx-font-size: 14px");
        Label numsL = new Label("Numbers :");
        numsL.setStyle("-fx-font-size: 14px");
        Label dateL = new Label("Draw Date :");
        dateL.setStyle("-fx-font-size: 14px");
        TextField nameT = new TextField();
        nameT.setStyle(noBorderTfStyle + "; -fx-font-size: 14px");
        TextField numsT = new TextField();
        numsT.setStyle(noBorderTfStyle + "; -fx-font-size: 14px");
        TextField dateT = new TextField();
        dateT.setStyle(noBorderTfStyle + "; -fx-font-size: 14px;");
        dateT.setPrefWidth(170);

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Pane pane = new Pane();	// the default alignment is center
//                pane.setPadding(new Insets(5, 5, 5, 5));
//                pane.setAlignment(Pos.CENTER_LEFT);
//                pane.getStyleClass().add("game-grid-cell");
//                if (c == 0) pane.getStyleClass().add("first-column");
//                if (r == 0) pane.getStyleClass().add("first-row");
                grid.add(pane, c, r);

                if (r == 0) {
                    if (r == 0 && c == 0) {
                        pane.getChildren().add(nameL);
                    }
                    if (r == 0 && c == 1) {
                        nameT.setText(showPlayName);
                        pane.getChildren().add(nameT);
                    }
                } else if (r == 1) {
                    if (r == 1 && c == 0) {
                        pane.getChildren().add(numsL);
                    }
                    if (r == 1 && c == 1) {
                        numsT.setText(nums);
                        pane.getChildren().add(numsT);
                    }
                } else if (r == 2) {
                    if (r == 2 && c == 0) {
                        pane.getChildren().add(dateL);
                    }
                    if (r == 2 && c == 1) {
                        dateT.setText(playDate);
                        pane.getChildren().add(dateT);
                    }
                }
            }
        }

        HBox closeInfoHB = new HBox();
        Text info1_2 = new Text("Closing in");
        info1_2.setFont(ITALIC_FONT);
        info1_2.setFill(Color.web("595959"));
        Text info2_2 = new Text("seconds...");
        info2_2.setFont(ITALIC_FONT);
        info2_2.setFill(Color.web("595959"));
        // to align right
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        closeInfoHB.getChildren().addAll(space, info1_2, timerTf, info2_2);

        mainVB.getChildren().addAll(grid, new Separator(), closeInfoHB);

        // hide popup after 3 seconds: try Timeline instead of PauseTransition  
        PauseTransition delay = new PauseTransition(Duration.seconds(showSec));
        delay.setOnFinished(e -> popup.hide());

        Scene scene = new Scene(mainVB);
        mainVB.requestFocus();	// don't let the first component get focused		
        popup.setScene(scene);
        popup.show();
        delay.play();
    }

    /**
     * point for the sub windows/dialog, 15% of the margin higher
     *
     * @param width
     * @param height
     * @return
     */
    private Point2D getFramePoint(double width, double height) {
        Point2D point = null;
        Stage parentStage = (Stage) vBox.getScene().getWindow();

        double parentXcenter = parentStage.getX() + (parentStage.getWidth() / 2);
        double dialogHalfWid = width / 2;

        double parentYcenter = parentStage.getY() + (parentStage.getHeight() / 2);
        double dialogHalfHgt = height / 2;

        if (height + 20 < parentStage.getHeight()) {
            point = new Point2D(parentXcenter - dialogHalfWid, parentYcenter - dialogHalfHgt);	// center
            // make 15% of the difference of main and sub
            double higher = (parentStage.getHeight() - height) * 0.15;
            point = new Point2D(parentXcenter - dialogHalfWid, parentYcenter - dialogHalfHgt - higher);
        } else {
            point = new Point2D(parentXcenter - dialogHalfWid, parentStage.getY());
        }
        return point;
    }

    private void showResultInfoPopup(String playName) {
        int showSec = 2;
        TextField timerTf = new TextField(showSec + "");
        closingInfoRun(showSec, timerTf);

        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setWidth(320);
        popup.setHeight(60);
        Point2D xy = getFramePoint(popup.getWidth(), popup.getHeight());
        popup.setX(xy.getX());
        popup.setY(xy.getY());

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(5);
        vBox.setStyle("-fx-border-color: lightgreen;");

        Label ttlLbl = new Label("The winning numbers for " + playName + " has been generated.");
        ttlLbl.setStyle("-fx-font-size: 12px");

        timerTf.setMaxWidth(20);
        timerTf.setAlignment(Pos.CENTER);
//		timerTf.setStyle("-fx-text-box-border: transparent; -fx-faint-focus-color: transparent;");
//		timerTf.setStyle("-fx-background-color: -fx-control-inner-background; -fx-background-insets: 0; -fx-padding: 1 3 1 3");
        timerTf.setStyle(noBorderTfStyle);
        Text info1_2 = new Text("Closing in");
        info1_2.setFont(ITALIC_FONT);
        info1_2.setFill(Color.web("595959"));
        Text info2_2 = new Text("seconds...");
        info2_2.setFont(ITALIC_FONT);
        info2_2.setFill(Color.web("595959"));

        HBox closeInfoHB = new HBox(info1_2, timerTf, info2_2);
        closeInfoHB.setAlignment(Pos.CENTER_LEFT);

        VBox vB_s = new VBox(closeInfoHB);
        vB_s.setPadding(new Insets(5, 5, 5, 150));
        vB_s.setSpacing(5);

        vBox.getChildren().addAll(ttlLbl, vB_s);

        // hide popup after 3 seconds: try Timeline instead of PauseTransition  
        PauseTransition delay = new PauseTransition(Duration.seconds(showSec));
        delay.setOnFinished(e -> popup.hide());

        Scene scene = new Scene(vBox);
        vBox.requestFocus();	// don't let the first component get focused		
        popup.setScene(scene);
        popup.show();
        delay.play();
    }

    // TODO
    private void generateAct() {
        Alert ae = new Alert(AlertType.ERROR);
        if (maxCtrlChk.isSelected() || sixCtrlChk.isSelected()) {
            if (maxCtrlChk.isSelected() && maxCtrlDp.getValue() == null) {
                ae.setContentText("Play date is required for Max");
                ae.setHeaderText("Max");
                maxCtrlDp.requestFocus();
                ae.show();
            } else if (sixCtrlChk.isSelected() && sixCtrlDp.getValue() == null) {
                ae.setContentText("Play date is required for 649");
                ae.setHeaderText("649");
                sixCtrlDp.requestFocus();
                ae.show();
            } else {
                genNumBtn.setDisable(true);
                vBox.requestFocus(); 		// Delegate the focus to container
                infoTa.setText("");
                // Generate winning numbers
                if (maxCtrlChk.isSelected() && sixCtrlChk.isSelected()) {
                    Thread maxR = getPlayer("Max");
                    Thread sixR = getPlayer("Six");
                    // choose one between Max and 649 - Random Pick to start
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        maxR.start();
                        sixR.start();
                    } else {
                        sixR.start();
                        maxR.start();
                    }
                } else {
                    if (maxCtrlChk.isSelected()) {
                        Thread maxR = getPlayer("Max");
                        maxR.start();
                    } else if (sixCtrlChk.isSelected()) {
                        Thread sixR = getPlayer("Six");
                        sixR.start();
                    }
                }
            }
        } else {
            ae.setContentText("You need to play at least one.");
            ae.setHeaderText("Get winning numbers");
            ae.show();
        }
    }

    private Thread getPlayer(String playName) {
        if (playName.equalsIgnoreCase("Max")) {
            maxGetBtn.setDisable(true);		
            maxRunIv.setVisible(true);
            m1.setText("");
            m2.setText("");
            m3.setText("");
            m4.setText("");
            m5.setText("");
            m6.setText("");
            m7.setText("");
        } else if (playName.equalsIgnoreCase("Six")) {
            sixGetBtn.setDisable(true);             
            sixRunIv.setVisible(true);		
            s1.setText("");
            s2.setText("");
            s3.setText("");
            s4.setText("");
            s5.setText("");
            s6.setText("");
        }
        return new Thread(new PlayNumRun(playName, infoTa, genNumBtn));
    }

    private long getLongLd(LocalDate ld) {
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date.getTime();
    }

    /**
     * TODO inner class 1. Compare with V02 2. runLater()
     */
    class PlayNumRun implements Runnable {

        private String numsArr[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50"};

        TextArea infoTa;
        Button genNumBtn;
        String playName;
        int maxNum = 50;	// highest number

        PlayNumRun(String playName, TextArea infoTa, Button genNumBtn) {
            this.playName = playName;
            this.infoTa = infoTa;
            this.genNumBtn = genNumBtn;
            if (playName.equalsIgnoreCase("Six")) {
                maxNum = 49;
            }
        }

        void pln(Object line) {
            System.out.println(line);
        }

        @Override
        public void run() {
            Random r = new Random();
            int pos = 0;

            TreeSet<String> maxNumsSet = new TreeSet<>();
            TreeSet<String> sixNumsSet = new TreeSet<>();
            String num = "";

            OUTER:
            OUTER_1:
            while (true) {
                Platform.runLater(() -> {
                    String pn = playName.equalsIgnoreCase("Six") ? "649" : playName;
                    String logTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    if (pn.equalsIgnoreCase("Max")) {
                        if (maxNumsSet.size() > 0) {
                            infoTa.appendText("System is generating " + maxNumsSet.size() + " of 7 winning number for " + pn + " at " + logTime + ".\n");
                        }
                    } else if (pn.equalsIgnoreCase("649")) {
                        if (sixNumsSet.size() > 0) {
                            infoTa.appendText("System is generating " + sixNumsSet.size() + " of 6 winning number for " + pn + " at " + logTime + ".\n");
                        }
                    }
                });
                try {
                    Thread.sleep((int) ((Math.random() * (maxSleep - minSleep)) + minSleep));
                } catch (InterruptedException e) {}
                List<String> numsList = Arrays.asList(numsArr);
                Collections.shuffle(numsList, new Random(getLongLd(playName.equalsIgnoreCase("Max") ? maxCtrlDp.getValue() : sixCtrlDp.getValue())));
                numsArr = (String[]) numsList.toArray();
                pos = r.nextInt(numsArr.length);
                num = numsArr[pos];
                if (playName.equalsIgnoreCase("max")) {
                    if (maxNumsSet.add(num)) {
                        switch (maxNumsSet.size()) {
                            case 1:
                                doLater(m1, num); break;
                            case 2:
                                doLater(m2, num); break;
                            case 3:
                                doLater(m3, num); break;
                            case 4:
                                doLater(m4, num); break;
                            case 5:
                                doLater(m5, num); break;
                            case 6:
                                doLater(m6, num); break;
                            case 7:
                                doLater(m7, num); break;
                            default:
                                break OUTER;
                        }
                    }
                } else if (playName.equalsIgnoreCase("six")) {
                    if (Integer.parseInt(num) <= maxNum) {
                        if (sixNumsSet.add(num)) {
                            switch (sixNumsSet.size()) {
                                case 1:
                                    doLater(s1, num); break;
                                case 2:
                                    doLater(s2, num); break;
                                case 3:
                                    doLater(s3, num); break;
                                case 4:
                                    doLater(s4, num); break;
                                case 5:
                                    doLater(s5, num); break;
                                case 6:
                                    doLater(s6, num); break;
                                default:
                                    break OUTER_1;
                            }
                        }
                    }
                }
            }
            Platform.runLater(() -> {
                if (playName.startsWith("Max")) {
                    maxRunIv.setVisible(false);		
                    maxGetBtn.setDisable(false);
                } else if (playName.startsWith("Six")) {
                    sixRunIv.setVisible(false);		
                    sixGetBtn.setDisable(false);
                }

                showResultInfoPopup(playName.equalsIgnoreCase("Six") ? "649" : playName);
                infoTa.appendText("The winning numbers for " + (playName.equalsIgnoreCase("Six") ? "649" : playName) + " are now ready!\n");
                genNumBtn.setDisable(false);	// reset app
            });
        }

        private void doLater(TextField tf, String num) {
            Platform.runLater(() -> {
                tf.setText(num);
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
