import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

enum DrawingState {DISABLED, START, DRAGGING, NEXT}
enum ModelState{VIEW, DRAW, EDIT}

enum Thickness {THIN, MEDIUM, THICK}
enum LineStyle {SOLID, DASHED, DOTTED}

public class Model {
    private ArrayList<MyCurve> curves = new ArrayList<MyCurve>();

    private ArrayList<IView> views = new ArrayList<IView>();

    private MyCurve curCurve;
    private MyCurve selectedCurve;
    private MyCurve.AnchorPoint selectedAnchor;

    private DrawingState drawingState;
    private ModelState modelState;
    private Thickness thickness;
    private LineStyle lineStyle;
    private Color color;
    Boolean saved;

    boolean onLoading;

    public Model(){
        modelState = ModelState.VIEW;
        drawingState = DrawingState.START;
        thickness = Thickness.MEDIUM;
        lineStyle = LineStyle.SOLID;
        color = Color.BLACK;
        saved = true;
    }

    public void handleCurveSelection(MyCurve selectedCurve){
        if(modelState != ModelState.EDIT) return;
        this.selectedCurve = selectedCurve;
        notifyObservers();
    }

    public void handleAnchorSelection(MyCurve.AnchorPoint selectedAnchor){
        if(modelState != ModelState.EDIT) return;
        this.selectedAnchor = selectedAnchor;
        notifyObservers();
    }

    public void clearSelection(){
        selectedCurve = null;
        selectedAnchor = null;
        notifyObservers();
    }

    public void deleteCurve(){
        if(modelState != ModelState.EDIT) return;
        saved=false;

        // False deletion, only made it invisible
        if(selectedCurve != null){
//            selectedCurve.setVisible(false);
            curves.remove(selectedCurve);
            views.remove(selectedCurve);
            notifyObservers();
            clearSelection();
        }
    }

    public void switchPointType() {
        if(modelState != ModelState.EDIT) return;
        saved=false;

        if (selectedAnchor != null) {
            selectedAnchor.switchSmoothness();
            notifyObservers();
        }
    }

    public void handleCanvasPressed(MouseEvent event){
        if(modelState != ModelState.DRAW) return;
        if(drawingState == DrawingState.START){
//            System.out.println("Canvas Pressed Draw Start");
            curCurve = new MyCurve(event, this);
            this.addView(curCurve);
            curves.add(curCurve);
            drawingState = DrawingState.DRAGGING;
            event.consume();
        } else if(drawingState == DrawingState.NEXT){
//            System.out.println("Canvas Pressed Draw Start Next");
            curCurve.addCenterPoint(event);
            drawingState = DrawingState.DRAGGING;
            event.consume();
        }
        notifyObservers();
    }

    public void handleCanvasDragged(MouseEvent event){
        if(modelState != ModelState.DRAW || drawingState != DrawingState.DRAGGING) return;
        curCurve.updateControlPointPos(event);
        event.consume();
    }

    public void handleCanvasReleased(MouseEvent event){
        if(modelState == ModelState.DRAW){
            drawingState = DrawingState.NEXT;
        }
    }

    public void handleKeyReleased(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE){
            deleteCurve();
        }else if (event.getCode() == KeyCode.ESCAPE){
            escHandler();
        }
        saved=false;
        notifyObservers();
    }


    private void escHandler(){
        if(modelState == ModelState.DRAW){
            switchModelState("View");
        } else if (modelState == ModelState.EDIT){
            switchModelState("View");
        }
    }


    private void finishCurCurve() {
        if(curCurve != null && modelState == ModelState.DRAW){
            saved=false;
            curCurve = null;
            drawingState = DrawingState.START;
        }
    }

    public void addView(IView view){
        views.add(view);
        view.update();
    }

    private void notifyObservers(){
        for(IView view: this.views){
            view.update();
        }
    }

    public void switchModelState(String buttonText){
        switch (buttonText){
            case "Pen":
                clearSelection();
                modelState = ModelState.DRAW;
                break;
            case "Selection":
                finishCurCurve();
                modelState = ModelState.EDIT;
                break;
            case "View":
                clearSelection();
                finishCurCurve();
                modelState = ModelState.VIEW;
                break;
        }
        notifyObservers();
    }

    public void switchThickness(String buttonText){
        if(modelState == ModelState.DRAW){
            escHandler();
        }

        Thickness pendingStyle = thickness;
        switch (buttonText){
            case "Thick":
                pendingStyle = Thickness.THICK;
                break;
            case "Medium":
                pendingStyle = Thickness.MEDIUM;
                break;
            case "Thin":
                pendingStyle = Thickness.THIN;
                break;
            default:
                if (selectedCurve != null){
                    pendingStyle = selectedCurve.getThickness();
                }
        }
        if (selectedCurve != null){
            saved=false;
            selectedCurve.setThickness(pendingStyle);
        } else {
            thickness = pendingStyle;
        }

        notifyObservers();
    }

    public void switchLineStyle(String buttonText){
        if(modelState == ModelState.DRAW){
            escHandler();
        }

        LineStyle pendingStyle = lineStyle;
        switch (buttonText){
            case "Solid":
                pendingStyle = LineStyle.SOLID;
                break;
            case "Dashed":
                pendingStyle = LineStyle.DASHED;
                break;
            case "Dotted":
                pendingStyle = LineStyle.DOTTED;
                break;
            default:
                if(selectedCurve != null){
                    pendingStyle = selectedCurve.getLineStyle();
                }
        }
        if (selectedCurve != null){
            saved=false;
            selectedCurve.setLineStyle(pendingStyle);
        } else {
            lineStyle = pendingStyle;
        }
        notifyObservers();
    }

    public void switchColor(Color color) {

        if(selectedCurve != null) {
            saved=false;
            selectedCurve.setColor(color);
        } else {
            this.color = color;
            if(modelState == ModelState.DRAW){
                escHandler();
            }
        }

        notifyObservers();
    }



    //////////////////
    //MENU FUNCTIONS//
    //////////////////
    public void resetCanvas(){
//        for(MyCurve myCurve: curves){
//            views.remove(myCurve);
//        }
        views.removeAll(curves);
        curves.clear();

        modelState = ModelState.VIEW;
        drawingState = DrawingState.START;
        thickness = Thickness.MEDIUM;
        lineStyle = LineStyle.SOLID;
        color = Color.BLACK;
        saved = true;

        selectedCurve=null;
        curCurve=null;
        selectedAnchor=null;

        //This also notifies the observers
        clearSelection();
    }

    public void loadFromData(ModelData modelData){
        resetCanvas();

        for (ModelData.CurveData curveData: modelData.curves){
            MyCurve myCurve = new MyCurve(curveData, this);
            addView(myCurve);
            curves.add(myCurve);
//            System.out.println("Added");

        }

        onLoading = true;
        notifyObservers();
        onLoading = false;

    }


    public MyCurve getCurCurve(){
        if (curCurve != null){
            System.out.println("GetCurcurve");
            System.out.println(curves);
        }
        return curCurve;
    }

    public ModelState getModelState(){
        return modelState;
    }

    public MyCurve getSelectedCurve(){
        return this.selectedCurve;
    }

    public MyCurve.AnchorPoint getSelectedAnchor(){
        return this.selectedAnchor;
    }

    public void setModelState(ModelState state){
        modelState = state;
    }

    public Thickness getThickness(){
        return this.thickness;
    }

    public void setThickness(Thickness thickness){
        this.thickness = thickness;
    }

    public LineStyle getLineStyle(){
        return this.lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle){
        this.lineStyle = lineStyle;
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public ArrayList<MyCurve> getCurves(){
        return this.curves;
    }
}
