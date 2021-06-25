package nl.kw.processing.mirror;
import processing.core.*;

/**
* A Processing utility to draw the mirror image (mirage) of a PGraphics
* object onto that object. A mirror can be constructed with an angle,
* position, tint, etc. and then later used to draw the mirage of
* an object. The position is always set relative to the center of
* the image.<br><br>
*
* The mirage can be drawn as a PImage, or pixel by pixel in a x,y
* loop, or by iterating the pixels[] array of the source.<br><br>
*
* It is often useful to keep a copy
* of the original image elsewhere, because drawing the mirage will 
* obviously change the image. Also, it often useful to draw the mirage
* on a separate PGraphics object (instead of the sketch canvas), so
* you can crop the result to not contain parts of the mirrored image
* that have no original image data.
*/

public class Mirror {
  
  private float x;
  private float y;
  private float alpha;
  
  private PShape shape;
  
  private PImage mask;
  private PImage invmask;
  private PGraphics maskgfx;
  private PGraphics invmaskgfx;
  
  private PGraphics mirage;
  private PVector mirpix;
  
  private int boundsw=0;
  private int boundsh=0;
  
  private int bgcolor;
  
  private boolean tinted;
  private int tint;
  private float opacity;
  
  private boolean transparent=true;
  private boolean usemask=true;
  
  public boolean debug=false;
  
  private PApplet applet;
  
  public Mirror(PApplet applet) {
    this(applet,(float) 0.0, (float) 0.0,(float) 0.0,0xffffffff);
    this.tinted=false;
  }
  public Mirror(PApplet applet, float x, float y, float alpha) {
    this(applet,x,y,alpha,0xffffffff);
    this.tinted=false;
  }
  public Mirror(PApplet applet, float x, float y, float alpha, int tint) {
    this.mirpix = new PVector(x,y);
    this.applet = applet;
    this.position(x,y,alpha);
    
    this.tinted=true;
    this.opacity= applet.alpha(tint);
    this.tint = applet.color(applet.red(tint),applet.green(tint),applet.blue(tint));
    
    applet.registerMethod("dispose", this);
    
  }
  
  /*
    getters and setters
  */
  
  public void setTint(int tint) {
      this.tint = tint;
      this.tinted=true;
  }
  public int getTint() {
      return this.tint;
  }
  public void setOpacity(float opacity) {
      this.opacity = opacity;
      this.tinted=true;
  }
  public float getOpacity() {
      return this.opacity;
  }
  
  public void setBGColor(int bgcolor) {
      this.bgcolor = bgcolor;
      if (applet.alpha(bgcolor)==0) this.transparent=true;
  }
  
  public int getBGColor() {
      return this.bgcolor;
  }
    
  public float getX() {
    return this.x;
  }
  public void setX(float x) {
    if (x != this.x) {
      this.x = x;
      this.resetShape();
    }
  }
  public float getY() {
    return this.y;
  }
  public void setY(float y) {
    if (y != this.y) {
      this.y = y;
      this.resetShape();
    }
  }
  public float getAlpha() {
    return this.alpha;
  }
  public void incAlpha(float delta) {
    setAlpha(alpha+delta);
  }
  public void setAlpha(float alpha) {
    if (alpha != this.alpha) {
      
      
      // normalize between PI and -PI
      // alpha = alpha - TWO_PI * floor((alpha + PI) / TWO_PI);
      alpha = alpha % PConstants.TWO_PI;
      if (alpha>PConstants.PI) alpha-=PConstants.TWO_PI;
      if (alpha<-PConstants.PI) alpha+=PConstants.TWO_PI;
      
      this.alpha = alpha;
      //println(round(degrees(this.alpha)));
      this.resetShape();
      
    }
  }
  
  public void position(float x, float y) {
    this.setX(x);
    this.setY(y);
  }
  
  public void position(float x, float y, float alpha) {
    this.setX(x);
    this.setY(y);
    this.setAlpha(alpha);
  }
  
  public void rotate(float delta) {
    PVector c = new PVector(x,y);
    PVector nc = PVector.fromAngle(c.heading()+delta);
    nc.setMag(c.mag());
    position(nc.x,nc.y,alpha+delta);
  }

  /* ---------------
    public mirage methods
  ----------- */
  
  /**
  * Get the mirrored image, masked to only display
  * the part of the image that is the mirror.
  */

  public PImage getMirage(PImage source) {
    PImage mirage = getFullMirage(source);
    if (usemask) {
      PImage mask = getMask(source,true);
      mirage.mask(mask);
    }
    return mirage;
  }
  
  private PImage getMirage(PImage source, PShape shape) {
    PImage mirage = getFullMirage(source);
    PImage mask = shape2mask(source.width,source.height,shape);
    mirage.mask(mask);
    return mirage;
  }
  
  /**
  * Get the full mirrored image, without a mask
  */

  public PImage getFullMirage(PImage source) {
      
      
      if (source!=null) {
        
        if (mirage==null || mirage.width!=source.width || mirage.height!=source.height) {
          mirage = applet.createGraphics(source.width,source.height);
        }
        
        mirage.beginDraw();
        mirage.clear();
        
        if (!transparent) {
          mirage.background(bgcolor);
        }
        
        int cx = Math.round(source.width/2);
        int cy = Math.round(source.height/2);
        
        mirage.pushMatrix();
        
        // center 
        mirage.translate(cx,cy);
        
        // translate rotate
        mirage.translate(x,y);
        mirage.rotate(alpha);
        
        // flip
        mirage.scale(1,-1);
        
        
        // move it back
        mirage.rotate(-alpha);
        mirage.translate(-x,-y);
       
       // decenter 
        mirage.translate(-cx,-cy);
        
        // drop the copy
        mirage.image(source,0,0);
        
        // debug
        //mirage.noFill();
        //mirage.stroke(#ff0000);
        //mirage.ellipse(0,0,10,10);

        mirage.popMatrix();

        mirage.endDraw();
        
        
      } else {
         throw new RuntimeException("Mirror.getMirage: no source");
      }
      
      return mirage;
  }
  
  /**
  * Draw the mirage on this.applet
  */

  public void drawMirage() {
     drawMirage(applet.g,true);
  }
  
  /**
  * Draw the mirage on any applet
  */
  
  public void drawMirage(PApplet applet) {
     drawMirage(applet.g);
  }
  
  /**
  * Draw the mirage on a PGraphics object
  */
  
  public void drawMirage(PGraphics source) {
     drawMirage(source,false);
  }
  
  /**
  * Draw the mirage on a PGraphics object inside beginDraw/endDraw
  */

  public void drawMirage(PGraphics source, boolean loaded) {
      if (!loaded) source.beginDraw();
      if (tinted) source.tint(tint,opacity);
      source.image(getMirage(source),0,0);
      if (tinted) source.noTint();
      if (!loaded) source.endDraw();
  }
  
  
  
  /* ---------------
    public mirpix vector methods
  ----------- */
  
  /**
  * Get the mirrored pixel coordinate of a vector
  */

  public PVector getMirage(PVector v) {
    return getMirage(v.x,v.y);
  }
  
  /**
  * Get the mirrored pixel coordinate of a coord x,y
  */

  public PVector getMirage(float vx, float vy) {
    // https://stackoverflow.com/questions/3306838/algorithm-for-reflecting-a-point-across-a-line
    float m = (float)Math.tan(alpha);
    float c = y-m*x;
    float d = (vx + (vy-c)*m)/(1+(float)Math.pow(m,2));
    this.mirpix.x = 2*d-vx;
    this.mirpix.y = 2*d*m-vy+2*c;
    return this.mirpix;
  }
  
  /**
  * Draw the mirage pixel of a vector on this applet
  */
  
  public void drawMirage(PVector v) {
    drawMirage(applet.g,v.x,v.y);
  }
  
  /**
  * Draw the mirage pixel of a x,y coordinate on this applet
  */

  public void drawMirage(float vx, float vy) {
    drawMirage(applet.g,vx,vy);
  }
  
  /**
  * Draw the mirage pixel of a vector on any applet
  */
  
  public void drawMirage(PApplet applet, PVector v) {
    drawMirage(applet.g,v.x,v.y);
  }

  /**
  * Draw the mirage pixel of a x,y coordinate on any applet
  */
  
  public void drawMirage(PApplet applet, float vx, float vy) {
    drawMirage(applet.g,vx,vy);
  }
  
  /**
  * Draw the mirage pixel of a vector on a PGraphics object
  */
  
  public void drawMirage(PGraphics source, PVector v) {
    drawMirage(source,v.x,v.y);
  }
  
  /**
  * Draw the mirage pixel of a x, y coordinate on a PGraphics object
  */
  public void drawMirage(PGraphics source, float vx, float vy) {
    
    if (!usemask || isInside(vx,vy)) {
      
      PVector m = getMirage(vx,vy);

      int dstx = Math.round(source.width/2+vx);
      int dsty = Math.round(source.height/2+vy);
      int srcx = Math.round(source.width/2+m.x);
      int srcy = Math.round(source.height/2+m.y);
      
      int srcval = source.get(srcx,srcy);
      if (tinted) {
        int dstval = source.get(dstx,dsty);
        source.set(dstx,dsty,tintColor(srcval,dstval));
      } else {
        source.set(dstx,dsty,tintColor(srcval));
      }
      //println("mirpix inside",srcval);

    } else {
      //println("mirpix outside");
    }
  }
  
  
  /* ---------------
    public mirpix array methods
  ----------- */
  
  /**
  * Draw the mirage pixel of pixels[index] on this applet
  */
  
  private void drawMirage(int index) {
    drawMirage(applet.g,index,false);
  }
  
  /**
  * Draw the mirage pixel of pixels[index] on this applet
  * @param  loaded  if loadPixels() has been called 
  */

  public void drawMirage(int index, boolean loaded) {
    drawMirage(applet.g,index,loaded);
  }
  
  /**
  * Draw the mirage pixel of pixels[index] on any applet
  */
  
  private void drawMirage(PApplet applet, int index) {
    drawMirage(applet.g,index,false);
  }
  
  /**
  * Draw the mirage pixel of pixels[index] on this applet
  * @param  loaded  if loadPixels() has been called 
  */

  public void drawMirage(PApplet applet, int index, boolean loaded) {
    drawMirage(applet.g,index,loaded);
  }
  
  /**
  * Draw the mirage pixel of pixels[index] of a PGraphics object
  */
  
  private void drawMirage(PGraphics source, int index) {
    drawMirage(source,index,false);
  }
  
  /**
  * Draw the mirage pixel of pixels[index] of a PGraphics object
  * @param  loaded  if loadPixels() has been called 
  */

  public void drawMirage(PGraphics source, int index, boolean loaded) {
    //println("mirpix");
    
    float px = index%source.width;
    float py = (index-px)/source.width;
    float vy = py-source.height/2;
    float vx = px-source.width/2;
    
    if (!usemask || isInside(vx,vy)) {
      
      PVector m = getMirage(vx,vy);

      int srcx = Math.round(source.width/2+m.x);
      int srcy = Math.round(source.height/2+m.y);
      int srcp = srcy*source.width+srcx;
      //color srcval = source.get(srcx,srcy);
      
      if (srcp < source.pixels.length) {
        if (!loaded) source.loadPixels();
        int srcval = source.pixels[srcp];
        source.pixels[index] = tintColor(srcval);
        if (!loaded) source.updatePixels();
      }
      
    }
    
  }
  
  /* ---------------
    internal mirage methods
  ----------- */
  
  // shape methods
  // the shape of the mirror is used 
  // to mask the FullMirage() 
  
  private void setShape(PShape shape) {
      this.shape = shape;
  }
  
  private PShape getShape() {
      return this.shape;
  }
  
  private PShape getShape(PImage bounds) {
    return getShape(bounds.width,bounds.height);
  }
  
  private PShape getShape(int w, int h) {
    return getShape(w,h,false);
  }
  
  private PShape getShape(PImage bounds, boolean reset) {
    return getShape(bounds.width,bounds.height, reset);
  }
  
  private PShape getShape(int w, int h, boolean reset) {
      if (boundsw==0 && boundsh==0) {
        boundsw = w;
        boundsh = h;
      }
      if (w==boundsw && h==boundsh) {
        if (shape==null) {
          shape = createShapeFromBounds(w,h);
        }
        return shape;
      } else {
        PShape shape = createShapeFromBounds(w,h);
        if (reset) {
           // this is where reset may be needed
           resetShape(w,h,shape);
           // now, the mask is still missing,
           // but this or next call of getMask
           // will solve that
        }
        return shape;
      }
  }

  private void resetShape() {
    resetShape(0,0,null,null,null);
  }
  
  private void resetShape(int w, int h, PShape shape) {
    resetShape(w,h,shape,null,null);
  }
  
  private void resetShape(int w, int h, PShape shape, PGraphics mask, PGraphics invmask) {
    this.shape=shape;
    this.boundsw=w;
    this.boundsh=h;
    this.mask=mask;
    this.invmask=invmask;
    this.maskgfx=null;
    this.invmaskgfx=null;
  }
  
  
  
  // -------------------
  // mask methods
  
  private PImage shape2mask(int boundsw, int boundsh, PShape shape) {
      if (maskgfx==null || maskgfx.width != boundsw || maskgfx.height!= boundsh) {
        maskgfx = applet.createGraphics(boundsw,boundsh);
      }
      maskgfx.beginDraw();
      maskgfx.clear();
      maskgfx.shape(shape,0,0);
      maskgfx.endDraw();
      return maskgfx;
  }
  
  private void setMask(PImage mask) {
      if (debug) applet.println(this,"setmask",mask);
      this.mask = mask;
  }
  
  private void setInvMask(PImage mask) {
      if (debug) applet.println(this,"setinvmask",mask);
      this.invmask = mask;
  }
  
  public PImage getMask() {
      return this.mask;
  }
  public PImage getInvMask() {
      return this.invmask;
  }

  private PImage getMask(PImage source) {
    return getMask(source.width,source.height, false);
  }
  private PImage getInvMask(PImage source) {
    return getInvMask(source.width,source.height,false);
  }
  
  private PImage getMask(PImage source, boolean reset) {
    return getMask(source.width,source.height, reset);
  }
  private PImage getInvMask(PImage source, boolean reset) {
    return getInvMask(source.width,source.height, reset);
  }
  
  private PImage getMask(int w, int h) {
    return getMask(w,h,false);
  }
  private PImage getInvMask(int w, int h) {
    return getInvMask(w,h,false);
  }
  
  private PImage getMask(int w, int h, boolean reset) {
      //if (debug) println(this,"getmask",w,h,reset);
      if (this.boundsw==0 && this.boundsh==0) {
        this.boundsw = w;
        this.boundsh = h;
      }
      if (this.mask!=null && w==this.boundsw && h==this.boundsh) {
        return this.mask;
      } else {
        PShape shape = getShape(w,h);
        PImage mask = shape2mask(w,h,shape);
        if (this.mask==null || reset) this.setMask(mask);
        return mask;
      }
  }
  
  private PImage getInvMask(int w, int h, boolean reset) {
      if (this.boundsw==0 && this.boundsh==0) {
        this.boundsw = w;
        this.boundsh = h;
      }
      if (this.invmask!=null && w==this.boundsw && h==this.boundsh) {
        return this.invmask;
      } else {
          PImage mask = getMask(w,h,reset);
          PImage invmask = invertMask(mask);
          if (this.invmask==null || reset) this.setInvMask(invmask);
          return invmask;
      }
  }
  
  private PImage invertMask(PImage mask) {
    if (invmaskgfx==null || invmaskgfx.width!=mask.width || invmaskgfx.height!=mask.height) {
      invmaskgfx = applet.createGraphics(mask.width,mask.height);
    }
    invmaskgfx.beginDraw();
    invmaskgfx.fill(0,0,255); // blue for mask
    invmaskgfx.rect(0,0,mask.width,mask.height);
    invmaskgfx.blend(mask,0,0,mask.width,mask.height,0,0,mask.width,mask.height,PConstants.SUBTRACT);
    invmaskgfx.mask(invmask);
    invmaskgfx.endDraw();
    return invmaskgfx;
  }


  // --------------
  // mirage tools
  
  private PShape createShapeFromBounds(int w, int h) {
      
      //rprintln(this,"createShapeFromBounds",w,h);
      PShape shape = applet.createShape();
      
      
      // create shape
      int cx = Math.round(x + w/2); 
      int cy = Math.round(y + h/2); 
      
      // dummy
      /*
        shape.beginShape();
        shape.fill(0, 0, 255);
        shape.noStroke();
        shape.vertex(cx-50,cy-50);
        shape.vertex(cx-50,cy+50);
        shape.vertex(cx+50,cy+50);
        shape.vertex(cx+50,cy-50);
        shape.endShape();
        return shape;
      */
      
      // find the point where the mirrorline
      // crossess the bounds. can only be 2 points:
      // from left, right, top or bottom
      
      PVector pl=null,pr=null,pt=null,pb=null;
      
      float absa = Math.abs(alpha);
      
      if (absa!=PConstants.HALF_PI) {
        
        float tana = (float)Math.tan(alpha);
        
        
        // tana = (cy-py)/(cx-px)
        // py = cy-(cx-px)*tana
        // px = cx-(cy-py)/tana
        

        // point pl : px=0;py=?
        pl = new PVector(0,cy-(cx)*tana);
        if (pl.y<0 || pl.y>h) pl = null; 
        
        // point pr : px=w;py=?
        pr = new PVector(w,cy-(cx-w)*tana);
        if (pr.y<0 ||pr.y>h) pr = null; 

        if (alpha!=0 && Math.abs(alpha)!=PConstants.PI) {
          
          // point pt : py=0;px=?
          pt = new PVector(cx-(cy)/tana,0);
          if (pt.x<0 ||pt.x>w) pt = null; 
        
          // point pb : py=h;px=?
          pb = new PVector(cx-(cy-h)/tana,h);
          if (pb.x<0 ||pb.x>w) pb = null; 
        
        } 
        
      } else {
        
        // point pt : py=0;px=?
        pt = new PVector(cx,0);
        
        // point pb : py=h;px=?
        pb = new PVector(cx,h);

      }
      
      //println("alpha",alpha);
      //if (pl!=null) println("left",pl.x,pl.y);
      //if (pr!=null) println("right",pr.x,pr.y);
      //if (pt!=null) println("top",pt.x,pt.y);
      //if (pb!=null) println("bottom",pb.x,pb.y);
      
      PVector j1=null,j2=null,j3=null,j4=null,j5=null;
      PVector tl = new PVector(0,0);
      PVector tr = new PVector(w,0);
      PVector bl = new PVector(0,h);
      PVector br = new PVector(w,h);
      
      // if absa < 90, mirror bottom
      // if a > 0 mirror left
      // if a < 0 mirror right
      

      if (pl!=null && pr!=null) {
        if (absa>PConstants.HALF_PI ) { j1=pl; j2=tl; j3=tr; j4=pr;  } // top
        else if (absa<PConstants.HALF_PI ) { j1=pl; j2=bl; j3=br; j4=pr;} // bottom
        else if (absa==PConstants.HALF_PI ) { /* its not */ }
      } else if (pt!=null && pb!=null) {
        if (alpha>0) { j1=pb; j2=bl; j3=tl; j4=pt; } // left
        else if (alpha<0) { j1=pt; j2=tr; j3=br; j4=pb; } // right
        else if (alpha==0) { throw new RuntimeException("Mirror: illegal line"); }
      } else if (pl!=null && pt!=null) {
        if (absa>PConstants.HALF_PI) { j1=pl; j2=tl; j3=pt; } // left-top
        else if (absa<PConstants.HALF_PI) { j1=pt; j2=tr; j3=br; j4=bl; j5=pl; } // right-bottom
        else if (absa==PConstants.HALF_PI ) { throw new RuntimeException("Mirror: illegal line"); }
      } else  if (pl!=null && pb!=null) {
        if (alpha>0) { j1=pl; j2=bl; j3=pb; } // left-bottom
        else if (alpha<0) { j1=pl; j2=tl; j3=tr; j4=br; j5=pb; } // right-top
        else if (alpha==0) { throw new RuntimeException("Mirror: illegal line"); }
      } else if (pr!=null && pt!=null) {
        if (absa>PConstants.HALF_PI) { j1=pt; j2=tr; j3=pr; } // right-top
        else if (absa<PConstants.HALF_PI) { j1=pr; j2=br; j3=bl; j4=tl; j5=pt; } // left-bottom
        else if (absa==PConstants.HALF_PI ) { throw new RuntimeException("Mirror: illegal line"); }
      } else if (pr!=null && pb!=null) {
        if (alpha>0) { j1=pb; j2=bl; j3=tl; j4=tr; j5=pr; } // left-top
        else if (alpha<0) { j1=pr; j2=br; j3=pb; } // right-bottom
        else if (alpha==0) { throw new RuntimeException("Mirror: illegal line"); }
      } else {
        applet.println("left",pl,"right",pr,"top",pt,"bottom",pb);
        throw new RuntimeException("Mirror: illegal line");
      }
      
      
      
      shape.beginShape();
      shape.fill(0, 0, 255); // blue for mask
      shape.noStroke();
      if (j1!=null) shape.vertex(j1.x,j1.y);
      if (j2!=null) shape.vertex(j2.x,j2.y);
      if (j3!=null) shape.vertex(j3.x,j3.y);
      if (j4!=null) shape.vertex(j4.x,j4.y);
      if (j5!=null) shape.vertex(j5.x,j5.y);
      shape.endShape(PConstants.CLOSE);
      

      
      return shape;
      
 
  }


  /* ----------------
    mirpix methods
  ------------- */
  
  private boolean isInside(PVector v) {
    return isInside(v.x,v.y);
  }
  
  private boolean isInside(float vx, float vy) {
    if (Math.abs(alpha)<PConstants.HALF_PI) return vy >= y + Math.tan(alpha)*(vx-x);
    if (alpha==-PConstants.HALF_PI) return vx >= x;
    if (alpha==PConstants.HALF_PI) return vx <= x;
    return vy <= y + Math.tan(alpha)*(vx-x);
  }
  
  private int tintColor(int srcval) {
    // TBD: apply tint() and opacity() to srcval
    return srcval;
  }
  
  private int tintColor(int srcval, int dstval) {
    // TBD: apply tint() and opacity() to srcval
    return srcval;
  }
  
  
  /* -------------
    processing stuff
  ---------- */

  public void dispose() {
    // Anything in here will be called automatically when 
    // the parent sketch shuts down. For instance, this might
    // shut down a thread used by this library.
  }  
  
  
  
}
