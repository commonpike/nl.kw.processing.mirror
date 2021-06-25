import nl.kw.processing.mirror.*;

/** 
* A more realistic example, loading an image on a separate 
* PGraphics object, drawing the mirage on that object
* and the showing that object on the canvas.
**/

String inputfile = "input.png";
PImage input;
PGraphics output;
Mirror mirror; // on d wall



void settings() {
  size(300,300,FX2D);
}

void setup() {
  

  output = createGraphics(width,height);
  input = loadImage(inputfile);
  mirror = new Mirror(this,0,0,PI/4);
  
  // or
  /*
    mirror = new Mirror(this);
    mirror.position(0,0);
    mirror.setAlpha(PI/4);
    mirror.setTint(#ffffff);
    mirror.setOpacity(255);
  */
  
}

void draw() {

  // using the whole image
  
  output.beginDraw();
  output.image(input,0,0,width,height);
  output.endDraw();
  
  
  mirror.drawMirage(output);
  image(output,0,0,width,height);
  
  
  // -----------
  // or pixel by pixel
  /*
    output.beginDraw();
    output.image(input,0,0,width,height);
    for (int x=-output.width/2; x < output.width/2; x++) {
      for (int y=-output.height/2; y < output.height/2; y++) {
        mirror.drawMirage(output,x,y);
      }
    }
    output.endDraw();
    image(output,0,0,width,height);
  */
  
  // -----------
  // or using the pixels[] array
  /*
  
    output.beginDraw();
    output.image(input,0,0,width,height);
    output.endDraw();
    
  
    output.loadPixels();
    for (int pc=0; pc < output.pixels.length; pc++) {
      mirror.drawMirage(output,pc,true);
    }
    output.updatePixels();
    image(output,0,0,width,height);
  
  */

  mirror.rotate(.01);
  
  
}
