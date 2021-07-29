202106*pike 

# Processing3 Mirror
<https://commonpike.github.io/nl.kw.processing.mirror/>

A Processing utility to draw the mirror image (mirage) of a PGraphics
object onto that object. A mirror can be constructed with an angle,
position, tint, etc. and then later used to draw the mirage of
an object. The position is always set relative to the center of
the image.

The mirage can be drawn as a PImage, or pixel by pixel in a x,y
loop, or by iterating the pixels[] array of the source.

It is often useful to keep a copy
of the original image elsewhere, because drawing the mirage will 
obviously change the image. Also, it often useful to draw the mirage
on a separate PGraphics object (instead of the sketch canvas), so
you can crop the result to not contain parts of the mirrored image
that have no original image data.

For the full reference, see 
<https://commonpike.github.io/nl.kw.processing.mirror/reference/>

To see what you can do with this, watch some of the animations at
<https://www.instagram.com/studio.pike>


## Installation

You may be able to install this through the Processing IDE.
It should end up your "sketchbook location",
also sometimes called the "processing library directory".
You can find its location in the Processing app, in the menu,
under preferences. It's usually in your homedir somewhere.

Or you can download it from GIT, and put it in that folder
yourself: 
<https://github.com/commonpike/nl.kw.processing.mirror>
locally rename the repo folder to 'Mirror'.

Once it's there, choose 'sketch > import library'
from the menu bar.

## Folder structure

```
- library
    The only folder you need, containing the jar file
- library.properties
		The properties file for P3
- README.md 
    This file
- docs
    Documentation; an HTML summary
- examples
    Example PDEs
- reference
    HTML Javadocs
- src
    Java source files
    
The following files are only available on github:

- dist
    Distribution files; the ZIP    
- build
    Compiled java classes
- bin
    Some goodies I use for maintenance
    
```

## Feedback & Problems 

If you have problems, questions, suggestions or
additions, contact me.


pike-processing@kw.nl