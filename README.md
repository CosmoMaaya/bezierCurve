Yusu Zhao \
20761282 y555zhao \
openjdk version "11.0.10" 2021-01-19 \
Ubuntu 20.04.1 LTS (Lenovo Y7000P-1060)

## Advanced Feature

----
The advanced feature I implemented is the advanced drawing.
In Pen mode, user can draw curves by clicking on canvas to initialize the anchor point. 
If the user drags the cursor without releasing, then the handle points will be adjusted accordingly.
Note that the window size will limit the canvas. Though user could drag outside the window, the points will remain inside.

## Other specification

------
- ### Drawing mode
    
    Users can click `Pen` button to enter the drawing mode, and can exit the mode either by clicking `Pen` button again or press `ESC` on keyboard.
Notice that if the user clicks any other button in the toolbar will also exit the drawing mode.
By pressing those style buttons, user will end drawing the current curve and set the property for the future curves.
The current curve on drawing will not be saved until user exits the drawing mode.
  Therefore, if the user choose to close the window, quit the application or load a new file, there will not be any notification and the user will lose the current curve.

- ###Editing mode
  
  Users can click `Selection` button to enter the editing mode, and can exit the mode either by clicking `Selection` button again or press `ESC` on keyboard.
  Any style button will be disabled at this moment, until user has selected one curve. Selection might be a little hard if the curve is too thin, but it works :)
  When a curve is selected, all anchor points, control points and control line will be shown. User could drag those points. 
  `Point type` button will not be enabled until user has selected one anchor point (in purple). 
  If an anchor point is selected, the color of the point will not be as transparent as before. 
  If the anchor point is sharp, then the user will not be allowed to adjust handle points. They will be disabled.
  Any adjust on other handle points (drag) or selecting other anchor points will clear the current selection.
  
  Note that any change made here will be regarded as edited, unsaved. Only selecting file is OK, but if any properties has been changed,
  such as the thickness/point positions/point type/color etc. , then the user will be warned by a prompt if they attempt to quit the application, get a new canvas or loading a file.
  
- ###Save and Load
  
  The file extension admitted by this application is `.maya`. 
  If saving or loading is failed, then the application will prompt a warning to the user.
  If there is any change to the current canvas, then as long as the operation will potentially discard all the changes, there will be a warning dialog asking confirmation.
  By clicking `Yes`, the change will be discarded and proceed the operation. By clicking `No`, the user will need to save first then proceed. By clicking `Cancel`, the operation will be terminated. 
  

##ENJOY CUBIC CURVE DRAWING AND HAVE A GOOD DAY!