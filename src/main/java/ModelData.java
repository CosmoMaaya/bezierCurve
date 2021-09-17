import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelData implements Serializable {
    ArrayList<CurveData> curves = new ArrayList<CurveData>();

    public ModelData(Model model){

        for (MyCurve myCurve: model.getCurves()){
            if (myCurve == model.getCurCurve()){
                //Discard this non-completed curve
                continue;
            }

            curves.add(new CurveData(myCurve));
        }

    }

    public class CurveData implements Serializable{
        //306,647,393
        ArrayList<AnchorData> anchors = new ArrayList<>();
        colorData colorData;
        Thickness thickness;
        LineStyle lineStyle;

        public CurveData(MyCurve myCurve){

            for(MyCurve.AnchorPoint anchorPoint: myCurve.anchorPoints){
                anchors.add(new AnchorData(anchorPoint));
//                System.out.println(anchorPoint.centerXProperty().getValue());
            }

            thickness = myCurve.getThickness();
            lineStyle = myCurve.getLineStyle();
            colorData = new colorData(myCurve.getColor());
        }

        public class AnchorData implements Serializable{
            double handle1X, handle1Y, handle2X, handle2Y;
            boolean oneHandle;
            double centerX, centerY;
            double previousHandleX, previousHandleY;
            boolean smooth;

            public AnchorData(MyCurve.AnchorPoint anchorPoint){
                double[] pos = anchorPoint.getHandle1Pos();
                handle1X = pos[0];
                handle1Y = pos[1];

                pos = anchorPoint.getHandle2Pos();
                if(pos == null){
                    oneHandle = true;
                } else {
                    oneHandle = false;
                    handle2X = pos[0];
                    handle2Y = pos[1];
                }

                pos = anchorPoint.getCenterPos();
                centerX = pos[0];
                centerY = pos[1];

                pos = anchorPoint.getPreviousHandlePos();
                previousHandleX = pos[0];
                previousHandleY = pos[1];
                smooth = anchorPoint.getSmooth();
            }
        }
    }

    public class colorData implements Serializable{
        double r,g,b,a;
        public colorData(Color color){
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getOpacity();
        }
    }
}
