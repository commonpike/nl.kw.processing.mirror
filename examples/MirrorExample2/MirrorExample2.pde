import nl.kw.processing.mirror.*;

/** 
* A more elaborate example, showing three different
* ways of drawing a mirage of the canvas onto the canvas.
**/


String inputfile = "input.png";
Mirror mirror; // on d wall

void settings() {
  size(300,300,FX2D);
}

void setup() {
  


  PImage input = loadImage(inputfile);
  image(input,0,0,width,height);
  
  
  mirror = new Mirror(this,0,0,PI/4,0x80ffffff);
  
  // or
  /*
    mirror = new Mirror(this);
    mirror.position(0,0);
    mirror.setAlpha(PI/4);
    mirror.setTint(#ffffff);
    mirror.setOpacity(128);
  */
  
}

void draw() {

  // using the whole image
  mirror.drawMirage(this);
  
  // -----------
  // or pixel by pixel
  /*
    for (int x=-width/2; x < width/2; x++) {
      for (int y=-height/2; y < height/2; y++) {
        mirror.drawMirage(this,x,y);
      }
    }
  */
  
  
  // -----------
  // or using the pixels[] array
  /*
    loadPixels();
    for (int pc=0; pc < pixels.length; pc++) {
      mirror.drawMirage(this,pc,true);
    }
    updatePixels();
  */
                
  noLoop();
  
  
}
