
/*
 * MiNiSYNTH - A JavaFX tool by:
 * - Clark Lavery (mentor)
 * - Evert Visser (s3727884)
 * - Duncan Baxter (s3737140)
 * - Kira Macarthur (s3742864)
 * - Dao Kun Nie (s3691571)
 * - Michael Power (s3162668)
 * - John Zealand-Doyle (s3319550)
 * 
 * Duncan can answer queries in relation to this Class.
 */

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * Top-level Class for the tool
 */
public class Sound extends Application {
    Stage stage;
    GridPane root;
    Envelope env;
    int duration;
    Button btNew;

    /*
     * The usual "main" method - this code is only executed on platforms that lack
     * full JavaFX support.
     */
    public static void main(String[] args) {
	Application.launch(args);
    }

    /*
     * JavaFX Application thread automatically calls start() method. The parameter
     * Stage stage is our top-level window, then Scene scene, TabPane root, and all
     * the other Nodes. This method is quite short: it just creates the GUI.
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) {
	this.stage = stage;
	// Set the Style for the primary Stage
	stage.initStyle(StageStyle.DECORATED);
	// Set the title of the primary Stage
	stage.setTitle("MiNiSYNTH");
	// Create the GridPane, 3 Tabs and their contents
	createGridPane();
	// Create a Scene based on the TabPane with no background fill
	Scene scene = new Scene(root, Color.DIMGRAY);
	// Add the Scene to the primary Stage and resize
	stage.setScene(scene);
	stage.show();
	btNew.requestFocus();
    }

    /*
     * createGridPane() method: creates GridPane, 3 Tabs and their contents.
     */
    private boolean createGridPane() {
	String[] names = { "Attack (ms)", "Attack (vol)", "Decay (ms)", "Decay (vol)", "Sustain (ms)", "Sustain (vol)",
		"Release (ms)", "Release (vol)" };
	int[] durations = { 0, 50, 50, 50, 50 };
	int[] positions = { 0, 50, 100, 150, 200 };
	double[] levels = { 0.0, 1.0, 0.75, 0.5, 0 };

	// Create a GridPane to hold the MiNiSYNTH
	root = new GridPane();
	root.getStyleClass().add("grid-pane");

	// Load the JavaFX CSS StyleSheet
	root.getStylesheets().add(getClass().getResource("SoundStyles.css").toString());

	// Create the "MiNiSYNTH" logo
	// Load the logo into an ImageView and add it to the GridPane
	ImageView iv = new ImageView("file:src/MiNiSYNTH.png");
	root.add(iv, 0, 0, 4, 1);

	// Create a VBox to hold the Wave radio buttons
	VBox vbWave = new VBox();
	vbWave.setId("VBox");
	Label lWave = new Label("Waves\n[To Do]");
	vbWave.getChildren().add(lWave);
	root.add(vbWave, 4, 0, 2, 1);

	// Create a VBox to hold the Note radio buttons
	VBox vbNote = new VBox();
	vbNote.setId("VBox");
	Label lNote = new Label("Notes\n[To Do]");
	vbNote.getChildren().add(lNote);
	root.add(vbNote, 6, 0, 2, 1);

	// Defining the x axis
	NumberAxis xAxis = new NumberAxis();
	xAxis.setLabel("Milliseconds");

	// Defining the y axis
	NumberAxis yAxis = new NumberAxis(0.0, 1.00, 0.25);
	yAxis.setLabel("Amplitude");

	// Prepare XYChart.Series objects by setting data
	XYChart.Series<Integer, Double> series = new XYChart.Series<>();
	series.setName("ADSR Envelope");
	for (int i = 0; i < 5; i++) {
	    series.getData().add(new Data<Integer, Double>(positions[i], levels[i]));
	}

	// Creating the line chart
	LineChart<Integer, Double> linechart = new LineChart(xAxis, yAxis);
	linechart.getData().add(series);
	root.add(linechart, 0, 1, 4, 1);

	// Create a TextArea to display the active Envelope settings
	duration = 0;
	String str = "Active Envelope Settings\n------------------------";
	for (int i = 0; i < 4; i++) {
	    str += String.format("\n%-15s%9d", names[i * 2], durations[i + 1]);
	    str += String.format("\n%-15s%9.2f", names[i * 2 + 1], levels[i + 1]);
	    duration += durations[i + 1];
	}
	str += "\n------------------------";
	str += String.format("\n%-15s%9d", "TOTAL TIME (ms)", duration);
	str += "\n========================";
	TextArea ta = new TextArea(str);
	ta.setEditable(false);
	root.add(ta, 4, 1, 2, 1);

	// Create the 3 x Buttons
	Button btEnv = new Button("Set envelope");
	btEnv.setTooltip(new Tooltip("Press this button to update the ADSR Envelope with the current settings"));
	btEnv.setOnAction(ae -> {
	    env = new Envelope(durations, levels);
	    duration = 0;
	    String s = "Active Envelope Settings\n------------------------";
	    for (int i = 0; i < 4; i++) {
		s += String.format("\n%-15s%9d", names[i * 2], durations[i + 1]);
		s += String.format("\n%-15s%9.2f", names[i * 2 + 1], levels[i + 1]);
		duration += durations[i + 1];
	    }
	    s += "\n------------------------";
	    s += String.format("\n%-15s%9d", "TOTAL TIME (ms)", duration);
	    s += "\n========================";
	    ta.setText(s);
	});

	btNew = new Button("Play tone");
	btNew.setTooltip(new Tooltip("Press this button to play a Tone with the current settings"));
	btNew.setOnMousePressed(me -> new Thread(new Tone(262, duration)).start());
	btNew.setOnAction(ae -> {
	    System.out.println("Play");
	});

	Button btExit = new Button("Exit");
	btExit.setTooltip(new Tooltip("Press this button when you've had enough"));
	btExit.setOnAction(ae -> System.exit(0));

	// Create a VBox to hold the buttons
	VBox vb = new VBox();
	vb.setId("VBox");
	vb.getChildren().addAll(btEnv, btNew, btExit);
	root.add(vb, 6, 1, 2, 1);

	// Create the 8 x Labels (one for each Slider)
	String[] labelText = { "Attack\n(ms)", "Attack\n(volume)", "Decay\n(ms)", "Decay\n(volume)", "Sustain\n(ms)",
		"Sustain\n(volume)", "Release\n(ms)", "Release\n(volume)" };
	for (int i = 0; i < 8; i++) {
	    final int index = 1 + i / 2;
	    final boolean isDuration = (i % 2 == 0);
	    ColumnConstraints cc = new ColumnConstraints();
	    cc.setPercentWidth(12.5d);
	    cc.setHalignment(HPos.CENTER);
	    root.getColumnConstraints().add(cc);
	    Slider s;
	    TextField t;

	    // Set parameters for "duration" variables
	    if (isDuration) {
		s = new Slider(0, 100, durations[index]);
		s.setMajorTickUnit(25.0f);
		s.setBlockIncrement(10.0f);
		t = new TextField(String.format("%.0f", s.getValue()));
		s.valueProperty().addListener((ov, oldValue, newValue) -> {
		    durations[index] = newValue.intValue();
		    t.setText(String.format("%d", durations[index]));
		    for (int j = index; j < 5; j++) {
			positions[j] = positions[j - 1] + durations[j];
			series.getData().set(j, new XYChart.Data<Integer, Double>(positions[j], levels[j]));
		    }
		});

		// Set parameters for "level" (i.e. volume) variables
	    } else {
		s = new Slider(0.0, 1.0, levels[index]);
		s.setMajorTickUnit(0.25f);
		s.setBlockIncrement(0.1f);
		t = new TextField(String.format("%.2f", s.getValue()));
		s.valueProperty().addListener((ov, oldValue, newValue) -> {
		    levels[index] = newValue.doubleValue();
		    t.setText(String.format("%.2f", levels[index]));
		    series.getData().set(index, new XYChart.Data<Integer, Double>(positions[index], levels[index]));
		});
	    }
	    t.setEditable(false);

	    // Create the 8 x Labels
	    Label l = new Label(labelText[i]);
	    root.add(t, i, 2);
	    root.add(s, i, 3);
	    root.add(l, i, 4);
	}

	// Set Grid-lines-visible during debug
	// root.setGridLinesVisible(true);

	// Signal that we need to layout the GridPane (ie. the Nodes are done)
	root.needsLayoutProperty();
	return true;
    }
}
