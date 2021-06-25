import nl.kw.processing.mirror.Mirror;

/** 
* A very minimal example, simply loading an image
* on the canvas, creating a mirror and drawing the mirage
* of the canvas on the canvas
**/

void settings() {
  size(300,300,FX2D);
}

void draw() {

  // very minimal example:
  
  PImage image = loadImage("input.png");
  image(image,0,0,width,height);
  
  Mirror mirror = new Mirror(this,-50,-50,3*PI/4,0x80aaffaa);
  mirror.drawMirage(this);
  
  noLoop();
  
}
