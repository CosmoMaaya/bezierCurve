import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ToolbarView extends ToolBar implements IView {

    Button selectPointType, toolErase;
    ToggleButton toolPen,toolSelection;
    ToggleButton thicknessThin, thicknessMedium, thicknessThick, lineStyleSolid, lineStyleDashed, lineStyleDotted;
    ColorPicker colorPicker;

    ToggleGroup tools, thickness, lineStyle;
    Model model;
    public ToolbarView(Model model) {
        this.model = model;
        //Tool palette, including pen, selection, point type and erase
        Label toolLabel = new Label("Tools");

        tools = new ToggleGroup();
        thickness = new ToggleGroup();
        lineStyle = new ToggleGroup();

        toolPen = new ToggleButton("Pen");
        toolSelection = new ToggleButton("Selection");
        selectPointType = new Button("PointType");
        toolErase = new Button("Erase");

        //Thickness palette
        Label thicknessLabel = new Label("Thickness");
        thicknessThin = new ToggleButton("Thin");
        thicknessMedium = new ToggleButton("Medium");
        thicknessThick = new ToggleButton("Thick");

        //Line style palette
        Label lineStyleLabel = new Label("Line Style");
        lineStyleSolid = new ToggleButton("Solid");
        lineStyleDashed = new ToggleButton("Dashed");
        lineStyleDotted = new ToggleButton("Dotted");

        //Color Picker
        Label colorLabel = new Label("Color");
        colorPicker = new ColorPicker();

        this.getItems().addAll(toolLabel, toolPen, toolSelection, selectPointType, toolErase);
        this.getItems().add(new Separator());
        this.getItems().addAll(thicknessLabel, thicknessThin, thicknessMedium, thicknessThick);
        this.getItems().add(new Separator());
        this.getItems().addAll(lineStyleLabel, lineStyleSolid, lineStyleDashed, lineStyleDotted);
        this.getItems().add(new Separator());
        this.getItems().addAll(colorLabel, colorPicker);

        this.setOrientation(Orientation.VERTICAL);
        initButtonFunction();
    }

    private void initButtonFunction(){
        initToolsButtons();
        initThicknessButtons();
        initLineStyleButtons();
        initColorPickers();

    }

    private void initToolsButtons(){
        Image imgPen = new Image("pen.png");
        ImageView viewPen = new ImageView(imgPen);
        viewPen.setFitHeight(20);
        viewPen.setFitWidth(20);
        toolPen.setGraphic(viewPen);
        toolPen.setPrefWidth(130);

        ImageView viewSelect = new ImageView(new Image("cursor.png"));
        viewSelect.setFitHeight(20);
        viewSelect.setFitWidth(20);
        toolSelection.setGraphic(viewSelect);
        toolSelection.setPrefWidth(130);

        ImageView viewErase = new ImageView(new Image("eraser.png"));
        viewErase.setFitHeight(20);
        viewErase.setFitWidth(20);
        toolErase.setGraphic(viewErase);
        toolErase.setPrefWidth(130);

        ImageView viewPointType = new ImageView(new Image("line.png"));
        viewPointType.setFitHeight(20);
        viewPointType.setFitWidth(20);
        selectPointType.setGraphic(viewPointType);
        selectPointType.setPrefWidth(130);


        toolPen.setOnMouseClicked(e -> {switchModelState();});
        toolSelection.setOnMouseClicked(e -> {switchModelState();});

        toolErase.setOnMouseClicked(e -> model.deleteCurve());
        selectPointType.setOnMouseClicked(e -> model.switchPointType());

        toolPen.setToggleGroup(tools);
        toolSelection.setToggleGroup(tools);
    }
    
    private void switchModelState(){
        ToggleButton curButton = (ToggleButton) tools.getSelectedToggle();
        if(curButton == null){
            model.switchModelState("View");
        } else {
            model.switchModelState(curButton.getText());
        }
    }

    private void initThicknessButtons(){
        thicknessThin.setToggleGroup(thickness);
        thicknessMedium.setToggleGroup(thickness);
        thicknessThick.setToggleGroup(thickness);

        Line thickLine = new Line(0,1,30,1);
        thickLine.setStrokeWidth(6);
        thicknessThick.setGraphic(thickLine);
        thicknessThick.setPrefWidth(130);

        Line mediumLine = new Line(0,1,30,1);
        mediumLine.setStrokeWidth(3);
        thicknessMedium.setGraphic(mediumLine);
        thicknessMedium.setPrefWidth(130);

        Line thinLine= new Line(0,1,30,1);
        thinLine.setStrokeWidth(1);
        thicknessThin.setGraphic(thinLine);
        thicknessThin.setPrefWidth(130);

        thicknessThin.setOnMouseClicked(event -> switchThickness());
        thicknessMedium.setOnMouseClicked(event -> switchThickness());
        thicknessThick.setOnMouseClicked(event -> switchThickness());
    }

    private void switchThickness(){
        ToggleButton curButton = (ToggleButton) thickness.getSelectedToggle();
        if(curButton == null){
            model.switchThickness("Null");
        } else {
//        if(curButton != null){
            model.switchThickness(curButton.getText());
        }
    }



    private void initLineStyleButtons(){
        lineStyleDotted.setToggleGroup(lineStyle);
        lineStyleDashed.setToggleGroup(lineStyle);
        lineStyleSolid.setToggleGroup(lineStyle);

        lineStyleSolid.setOnMouseClicked(e -> switchLineStyle());
        lineStyleDotted.setOnMouseClicked(e -> switchLineStyle());
        lineStyleDashed.setOnMouseClicked(e -> switchLineStyle());

        Line solidLine = new Line(0,1,30,1);
        solidLine.setStrokeWidth(3);
        lineStyleSolid.setGraphic(solidLine);
        lineStyleSolid.setPrefWidth(130);

        Line dashedLine = new Line(0,1,30,1);
        dashedLine.getStrokeDashArray().addAll(10d, 10d);
        dashedLine.setStrokeWidth(3);
        lineStyleDashed.setGraphic(dashedLine);
        lineStyleDashed.setPrefWidth(130);

        Line dottedLine= new Line(0,1,30,1);
        dottedLine.getStrokeDashArray().addAll(2d, 10d);
        dottedLine.setStrokeWidth(3);
        lineStyleDotted.setGraphic(dottedLine);
        lineStyleDotted.setPrefWidth(130);
    }

    private void switchLineStyle(){
        ToggleButton curButton = (ToggleButton) lineStyle.getSelectedToggle();
        if(curButton == null){
            model.switchLineStyle("Null");
        } else {
//        if(curButton != null){
            model.switchLineStyle(curButton.getText());
        }
    }

    private void initColorPickers(){
        colorPicker.setValue(Color.BLACK);

        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.switchColor(colorPicker.getValue());
            }
        });
    }

    @Override
    public void update() {
        ModelState modelState = model.getModelState();
        switch (modelState){
            case DRAW:
                toolPen.setSelected(true);
                break;
            case EDIT:
                toolSelection.setSelected(true);
                break;
            case VIEW:
                toolPen.setSelected(false);
                toolSelection.setSelected(false);
                break;
        }

        MyCurve curSelectedCurve = model.getSelectedCurve();

        if(modelState == ModelState.EDIT && curSelectedCurve == null){
            lineStyleDashed.setDisable(true);
            lineStyleDotted.setDisable(true);
            lineStyleSolid.setDisable(true);
            selectPointType.setDisable(true);
            thicknessThick.setDisable(true);
            thicknessMedium.setDisable(true);
            thicknessThin.setDisable(true);
            colorPicker.setDisable(true);
        } else {
            lineStyleDashed.setDisable(false);
            lineStyleDotted.setDisable(false);
            lineStyleSolid.setDisable(false);
            selectPointType.setDisable(false);
            thicknessThick.setDisable(false);
            thicknessMedium.setDisable(false);
            thicknessThin.setDisable(false);
            colorPicker.setDisable(false);
        }

        if(modelState == ModelState.EDIT && curSelectedCurve != null){
            toolErase.setDisable(false);
        } else {
            toolErase.setDisable(true);
        }


        if(curSelectedCurve == null){
            thicknessToggle(model.getThickness());
            lineStyleToggle(model.getLineStyle());
            colorPicker.setValue(model.getColor());
        } else {
//        if(curSelectedCurve != null){
            thicknessToggle(curSelectedCurve.getThickness());
            lineStyleToggle(curSelectedCurve.getLineStyle());
            colorPicker.setValue(curSelectedCurve.getColor());
        }

        MyCurve.AnchorPoint curSelectedAnchor = model.getSelectedAnchor();
        if(curSelectedAnchor == null){
            selectPointType.setDisable(true);
        } else {
            selectPointType.setDisable(false);
        }
    }

    private void thicknessToggle(Thickness thickness){
        switch (thickness){
            case THIN:
                thicknessThin.setSelected(true);
                break;
            case MEDIUM:
                thicknessMedium.setSelected(true);
                break;
            case THICK:
                thicknessThick.setSelected(true);
                break;
        }
    }

    private void lineStyleToggle(LineStyle lineStyle){
        switch (lineStyle){
            case SOLID:
                lineStyleSolid.setSelected(true);
                break;
            case DASHED:
                lineStyleDashed.setSelected(true);
                break;
            case DOTTED:
                lineStyleDotted.setSelected(true);
                break;
        }
    }

}
