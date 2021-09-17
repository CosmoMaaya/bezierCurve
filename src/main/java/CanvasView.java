import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CanvasView extends Pane implements IView {
    private Model model;

    public CanvasView(Model model) {
        this.model = model;
        this.setOnMousePressed(model::handleCanvasPressed);
        this.setOnMouseDragged(model::handleCanvasDragged);
        this.setOnMouseReleased(model::handleCanvasReleased);

    }

    @Override
    public void update() {
        // Add new curve
        MyCurve curCurve = model.getCurCurve();
        if(curCurve != null){
            if(!this.getChildren().contains(curCurve)){
                this.getChildren().add(curCurve);
            }
        }

//        if(model.onDelete && model.getSelectedCurve() != null){
//            this.getChildren().remove(model.getSelectedCurve());
//        }

        ArrayList<MyCurve> curves = model.getCurves();
        this.getChildren().removeIf(curve -> !curves.contains(curve));

        if (model.onLoading){
            this.getChildren().addAll(model.getCurves());
        }
//        System.out.println("Canvas Updated");
//        System.out.println(this.getChildren());
    }

}
