import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

enum CurveState {START_CENTER, NEXT_CENTER}

enum HandleNum {ONE, TWO}

//enum AnchorDataType{ONE_HANDLE, CENTER_X, CENTER_Y, PREVIOUS_HANDLE_X, PREVIOUS_HANDLE_Y, SMOOTH}

public class MyCurve extends Group implements IView {
    double DEFAULT_OFFSET = 100;
    private final Model model;
    private CubicCurve curCurve;
    private AnchorPoint curAnchor;
    private HandlePoint curHandle;
    private CurveState curveState;

    private Color color;
    private Thickness thickness;
    private LineStyle lineStyle;

    double sizeOffsetWidth = 160, sizeOffsetHeight = 30;

    ArrayList<AnchorPoint> anchorPoints = new ArrayList<>();
    ArrayList<ControlLine> controlLines = new ArrayList<>();
    ArrayList<HandlePoint> handlePoints = new ArrayList<>();
    ArrayList<CubicCurve> curves = new ArrayList<>();

    public MyCurve(MouseEvent event, Model model) {
        curveState = CurveState.START_CENTER;
        curCurve = new CubicCurve();
        this.model = model;
        thickness = model.getThickness();
        lineStyle = model.getLineStyle();
        color = model.getColor();
        addCenterPoint(event);


        this.setOnMouseClicked(e -> model.handleCurveSelection(this));
    }


    public MyCurve(ModelData.CurveData curveData, Model model){
        curveState = CurveState.NEXT_CENTER;
        this.model = model;
        curCurve = new CubicCurve();
        thickness = curveData.thickness;
        lineStyle = curveData.lineStyle;
        color = new Color(curveData.colorData.r, curveData.colorData.g, curveData.colorData.b, curveData.colorData.a);

        for (ModelData.CurveData.AnchorData anchorData: curveData.anchors){
            AnchorPoint anchorPoint = new AnchorPoint(anchorData.centerX, anchorData.centerY);
            HandlePoint handlePoint1 = new HandlePoint(anchorData.handle1X, anchorData.handle1Y, anchorPoint);
            anchorPoint.bind(HandleNum.ONE, handlePoint1);
            ControlLine controlLine = new ControlLine(anchorPoint.centerXProperty(), anchorPoint.centerYProperty(), handlePoint1.centerXProperty(), handlePoint1.centerYProperty());

            anchorPoints.add(anchorPoint);
            handlePoints.add(handlePoint1);
            controlLines.add(controlLine);
            this.getChildren().addAll(anchorPoint, handlePoint1, controlLine);

            if(anchorData.oneHandle) {
                setCurCurve(anchorPoint, handlePoint1, true);
            } else {
                setCurCurve(anchorPoint, handlePoint1, false);
                curves.add(curCurve);
                this.getChildren().add(curCurve);

                curCurve = new CubicCurve();
                curCurve.setStrokeWidth(3);

                HandlePoint handlePoint2 = new HandlePoint(anchorData.handle2X, anchorData.handle2Y, anchorPoint);
                anchorPoint.bind(HandleNum.TWO, handlePoint2);
                handlePoint1.bind(handlePoint2);
                handlePoint2.bind(handlePoint1);

                setCurCurve(anchorPoint, handlePoint2, true);
                ControlLine controlLine2 = new ControlLine(anchorPoint.centerXProperty(), anchorPoint.centerYProperty(), handlePoint2.centerXProperty(), handlePoint2.centerYProperty());
                handlePoints.add(handlePoint2);
                controlLines.add(controlLine2);

                this.getChildren().addAll(handlePoint2, controlLine2);
            }

        }

        this.setOnMouseClicked(e -> model.handleCurveSelection(this));
    }

    public void addCenterPoint(MouseEvent event) {
        if (model.getModelState() != ModelState.DRAW) return;
        double startX = event.getX();
        double startY = event.getY();
        double controlX = event.getX() + DEFAULT_OFFSET;
        double controlY = event.getY() - DEFAULT_OFFSET;
        curCurve.setStrokeWidth(3);
        AnchorPoint anchorPoint = new AnchorPoint(startX, startY);
        HandlePoint handlePoint = new HandlePoint(controlX, controlY, anchorPoint);
        curAnchor = anchorPoint;
        curHandle = handlePoint;
        curAnchor.bind(HandleNum.ONE, handlePoint);
        ControlLine controlLine = new ControlLine(curAnchor.centerXProperty(), curAnchor.centerYProperty(), curHandle.centerXProperty(), curHandle.centerYProperty());

        if (curveState == CurveState.START_CENTER) {
            // The very first start point of this curve

            setCurCurve(curAnchor, curHandle, true);
            //setCurCurve(curCenter, controlPoint1, false);
            anchorPoints.add(curAnchor);
            handlePoints.add(curHandle);
            controlLines.add(controlLine);
            this.getChildren().addAll(curAnchor, curHandle, controlLine);
            curveState = CurveState.NEXT_CENTER;

        } else if (curveState == CurveState.NEXT_CENTER) {
//            System.out.println("Add Center Point Start Next");
            // Following points, will be used both as the end point of the previous segment and the start point of the next segment
            // But that would be handled in the addControlPoint part, since the next step is to draw control point

            setCurCurve(curAnchor, handlePoint, false);
            curves.add(curCurve);
            anchorPoints.add(curAnchor);
            handlePoints.add(curHandle);
            controlLines.add(controlLine);
            this.getChildren().addAll(curCurve, curAnchor, curHandle, controlLine);

            curCurve = new CubicCurve();

            HandlePoint handlePoint2 = new HandlePoint(2 * curAnchor.getCenterX() - handlePoint.getCenterX(), 2 * curAnchor.getCenterY() - handlePoint.getCenterY(), curAnchor);
            curAnchor.bind(HandleNum.TWO, handlePoint2);
            handlePoint.bind(handlePoint2);
            handlePoint2.bind(handlePoint);

            curHandle = handlePoint2;
            curAnchor = anchorPoint;
            setCurCurve(curAnchor, curHandle, true);

            ControlLine controlLine2 = new ControlLine(curAnchor.centerXProperty(), curAnchor.centerYProperty(), curHandle.centerXProperty(), curHandle.centerYProperty());

            handlePoints.add(curHandle);
            controlLines.add(controlLine2);
            this.getChildren().addAll(curHandle, controlLine2);

        }
    }

    private void setCurCurve(AnchorPoint center, HandlePoint control, Boolean isStart) {
        curCurve.setFill(null);
        curCurve.setStrokeWidth(2);
        curCurve.setStroke(Color.BLACK);
        if (isStart) {
            curCurve.startXProperty().bind(center.centerXProperty());
            curCurve.startYProperty().bind(center.centerYProperty());
            curCurve.controlX1Property().bind(control.centerXProperty());
            curCurve.controlY1Property().bind(control.centerYProperty());
        } else {
            curCurve.endXProperty().bind(center.centerXProperty());
            curCurve.endYProperty().bind(center.centerYProperty());
            curCurve.controlX2Property().bind(control.centerXProperty());
            curCurve.controlY2Property().bind(control.centerYProperty());
        }

    }

    private void setCurveColor(Color color) {
        for (CubicCurve curve : curves) {
            curve.setStroke(color);
        }
    }

    public void setControlLineVisibility(Boolean visible) {
        if (visible) {
            for (HandlePoint point : handlePoints) {
                point.setVisible(true);
            }
            for (ControlLine line : controlLines) {
                line.setVisible(true);
            }
            for (AnchorPoint point : anchorPoints) {
                point.setVisible(true);
                point.setHandleVisibility();
            }
        } else {
            for (AnchorPoint point : anchorPoints) {
                point.setVisible(false);
            }
            for (HandlePoint point : handlePoints) {
                point.setVisible(false);
            }
            for (ControlLine line : controlLines) {
                line.setVisible(false);
            }
        }
    }


    void updateControlPointPos(MouseEvent event) {
        if (model.getModelState() != ModelState.DRAW) return;
//        System.out.println("Update Control Point Pos");
        curHandle.updateCenterX(borderCheck(event.getX(), 0, getScene().getWidth() - sizeOffsetWidth));
        curHandle.updateCenterY(borderCheck(event.getY(), 0, getScene().getHeight() - sizeOffsetHeight));
    }

    private static class ControlLine extends Line {
        public ControlLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            startXProperty().bind(startX);
            startYProperty().bind(startY);
            endXProperty().bind(endX);
            endYProperty().bind(endY);
            setStrokeWidth(1);
            setStroke(Color.rgb(65, 105, 225));
        }
    }

    public class AnchorPoint extends Circle {
        private HandlePoint handle1;
        private HandlePoint handle2;
        private double previousHandleX, previousHandleY;
        private Boolean smooth;

        public AnchorPoint(double x, double y) {
            super(x, y, 6);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);
            setFill(Color.rgb(153, 50, 204, 0.5));
            setStroke(Color.rgb(153, 50, 204));
            enableDrag();
            smooth = true;
            this.setOnMousePressed(e -> model.handleAnchorSelection(this));
        }

        public void bind(HandleNum index, HandlePoint handle) {
            if (index == HandleNum.ONE) {
                handle1 = handle;
            } else {
                handle2 = handle;
            }
        }

        public void switchSmoothness() {
            if (smooth) {
                sharp();
            } else {
                smooth();
            }
        }

        public Boolean getSmooth() {
            return smooth;
        }

        // Our set control line visibility will potentially overwrite this.
        public void setHandleVisibility() {
            if (smooth) {
                handle1.setVisible(true);
                if (handle2 != null) {
                    handle2.setVisible(true);
                }
            } else {
                handle1.setVisible(false);
                if (handle2 != null) {
                    handle2.setVisible(false);
                }
            }
        }

        private void sharp() {
            previousHandleX = handle1.getCenterX();
            previousHandleY = handle1.getCenterY();
            handle1.updateCenterX(getCenterX());
            handle1.updateCenterY(getCenterY());

            smooth = false;
        }

        private void smooth() {
            handle1.updateCenterX(previousHandleX);
            handle1.updateCenterY(previousHandleY);

            smooth = true;
        }

        private void moveHandles(double offsetX, double offsetY) {
            if (handle1 != null) {
                handle1.updateCenterX(handle1.getCenterX() + offsetX);
                handle1.updateCenterY(handle1.getCenterY() + offsetY);
            } else if (handle2 != null) {
                handle2.updateCenterX(handle2.getCenterX() + offsetX);
                handle2.updateCenterY(handle2.getCenterY() + offsetY);
            }
        }

        private void enableDrag() {

            // Set Cursor type
            this.setOnMousePressed(Event::consume);
            this.setOnMouseReleased(Event::consume);

            // Update the status
            this.setOnMouseDragged(e -> {
                if (model.getModelState() != ModelState.EDIT) return;
                model.saved = false;
                //if (model.getModelState() != ModelState.EDIT) return;
                // Offset from the center of circle and the mouse clicked point
                // Set X
                double newX = e.getX();
                double newY = e.getY();

                newX = borderCheck(newX, 0, getScene().getWidth() - sizeOffsetWidth);
                newY = borderCheck(newY, 0, getScene().getHeight() - sizeOffsetHeight);

                double offsetX = newX - getCenterX();
                double offsetY = newY - getCenterY();

                setCenterX(newX);
                setCenterY(newY);
                moveHandles(offsetX, offsetY);

//                System.out.println("Anchor Dragged");
                e.consume();
            });
        }

        public double[] getHandle1Pos(){
            return new double[] {handle1.getCenterX(), handle1.getCenterY()};
        }

        public double[] getHandle2Pos(){
            if (handle2 == null) return null;
            return new double[] {handle2.getCenterX(), handle2.getCenterY()};
        }

        public double[] getCenterPos(){
            return new double[] {getCenterX(), getCenterY()};
        }

        public double[] getPreviousHandlePos(){
            return new double[] {previousHandleX, previousHandleY};
        }

    }

    private class HandlePoint extends Circle {
        private HandlePoint theOtherHandle;
        DoubleProperty anchorX;
        DoubleProperty anchorY;

        public HandlePoint(double x, double y, AnchorPoint anchor) {
            super(x, y, 5);
            anchorX = anchor.centerXProperty();
            anchorY = anchor.centerYProperty();

            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);
            setFill(Color.rgb(65, 105, 225, 0.5));
            setStroke(Color.rgb(65, 105, 225));
//            this.setOnMousePressed(e-> {
//                System.out.println("Handle Pressed");
//            });
            enableDrag();
        }

        public void bind(HandlePoint handle) {
            theOtherHandle = handle;
        }

        public void updateCenterX(double value) {
            setCenterX(value);
            moveOppositeHandle();
        }

        public void updateCenterY(double value) {
            setCenterY(value);
            moveOppositeHandle();
        }

        private void moveOppositeHandle() {
            if (theOtherHandle != null) {
                theOtherHandle.setCenterX(2 * anchorX.getValue() - getCenterX());
                theOtherHandle.setCenterY(2 * anchorY.getValue() - getCenterY());
            }
        }

        private void enableDrag() {

            // Set Cursor type
            this.setOnMousePressed(Event::consume);
            this.setOnMouseReleased(Event::consume);

            // Update the status
            this.setOnMouseDragged(e -> {
                if (model.getModelState() != ModelState.EDIT) return;
                model.saved = false;
                model.handleAnchorSelection(null);
                //if (model.getModelState() != ModelState.EDIT) return;
                // Offset from the center of circle and the mouse clicked point
                double newX = e.getX();
                double newY = e.getY();

                newX = borderCheck(newX, 0, getScene().getWidth() - sizeOffsetWidth);
                newY = borderCheck(newY, 0, getScene().getHeight() - sizeOffsetHeight);

                setCenterX(newX);
                setCenterY(newY);
                moveOppositeHandle();

//                System.out.println("Handle Dragged");
                e.consume();
            });
        }

    }

    private double borderCheck(double value, double lowerLimit, double higherLimit) {
        if (value < lowerLimit) {
            value = lowerLimit;
        } else if (value > higherLimit) {
            value = higherLimit;
        }

        return value;
    }

    private void updateThickness() {
        double width = 3;
        switch (thickness) {
            case THIN:
                width = 1;
                break;
            case MEDIUM:
                width = 3;
                break;
            case THICK:
                width = 6;
                break;
        }
        for (CubicCurve curve : curves) {
            curve.setStrokeWidth(width);
        }
    }

    private void updateStyle() {
        switch (lineStyle) {
            case SOLID:
                for (CubicCurve curve : curves) {
                    curve.getStrokeDashArray().clear();
                }
                break;
            case DASHED:
                for (CubicCurve curve : curves) {
                    curve.getStrokeDashArray().clear();
                    curve.getStrokeDashArray().addAll(25d, 25d);
                }
                break;
            case DOTTED:
                for (CubicCurve curve : curves) {
                    curve.getStrokeDashArray().clear();
                    curve.getStrokeDashArray().addAll(2d, 15d);
                }
                break;
        }


    }

    public void update() {

        MyCurve selectedCurve = model.getSelectedCurve();
        AnchorPoint selectedAnchor = model.getSelectedAnchor();
        MyCurve curCurve = model.getCurCurve();
        if (this.equals(selectedCurve)) {
//            setCurveColor(Color.rgb(218,165,32));
            setCurveColor(this.color);
            setControlLineVisibility(true);
        } else if (this.equals(curCurve)) {
//            System.out.println("Update when ordinary curve");
//            System.out.println(this.color);
            setCurveColor(this.color);
            setControlLineVisibility(true);
        } else {
            setCurveColor(this.color);
            setControlLineVisibility(false);
        }

        updateThickness();
        updateStyle();

        //Smooth or Sharp
        for (AnchorPoint anchor : anchorPoints) {
            if (anchor.equals(selectedAnchor)) {
                anchor.setFill(Color.rgb(153, 50, 204));
            } else {
                anchor.setFill(Color.rgb(153, 50, 204, 0.5));
            }
        }
    }


    public Thickness getThickness() {
        return this.thickness;
    }

    public void setThickness(Thickness thickness) {
        this.thickness = thickness;
    }

    public LineStyle getLineStyle() {
        return this.lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}
