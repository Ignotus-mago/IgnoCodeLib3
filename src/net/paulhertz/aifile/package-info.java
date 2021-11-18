/**
Package <code>net.paulhertz.aifile</code> provides dual functionality as a display list 
utility in <a href="http://www.processing.org/" target="_blank">Processing</a> and as an export tool that writes to an 
<a href="http://partners.adobe.com/public/developer/en/illustrator/sdk/AI7FileFormat.pdf" target="_blank">Adobe Illustrator 7.0 file</a>. 
AI 7.0 was the last <a href="http://partners.adobe.com/public/developer/en/illustrator/sdk/AI7FileFormat.pdf" target="_blank">public specification</a> of Adobe's file format. It can still be extremely useful, 
and can be imported into all current version of Illustrator . Everything in the 
<a href="http://www.flickr.com/photos/ignotus/sets/72157626088733463/">Sampling Patterns Flickr set</a>,
was generated in Processing with the help of this library.
<p>Graphics consist of B&eacute;zier curves and lines, implemented by the class {@link net.paulhertz.aifile.BezShape BezShape} and its
helpers, {@link net.paulhertz.aifile.LineVertex LineVertex} and {@link net.paulhertz.aifile.BezVertex BezVertex}. Text is handled by {@link net.paulhertz.aifile.PointText PointText}. 
Other Illustrator text formats (area text and text on a path) are not (yet) implemented, since there is nothing in Processing 
that corresponds to them. {@link net.paulhertz.aifile.BezShape BezShape} and {@link net.paulhertz.aifile.PointText PointText}
are subclasses of {@link net.paulhertz.aifile.DisplayComponent DisplayComponent}, the abstract class
extended by all components of the document hierarchy. Numerous subclasses of {@link net.paulhertz.aifile.BezShape BezShape} implement
a standard set of graphics primitives such as rectangles, polygons, multiCurves, lines, etc.</p>
<p>{@link net.paulhertz.aifile.DocumentComponent DocumentComponent}, {@link net.paulhertz.aifile.LayerComponent LayerComponent}, and 
{@link net.paulhertz.aifile.GroupComponent GroupComponent} provide the hierarchical structure of the document: a root document instance,
any number of layers immediately below the document (but not nested), and groups within the layers nested to any desirable depth. 
Shapes and text objects may populate layers and groups. The {@link net.paulhertz.aifile.Palette Palette} class provides hooks for creating 
a palette in the AI file, and utilities for managing Processing colors and converting them to AI formats. 
{@link net.paulhertz.aifile.RGBColor RGBColor} and {@link net.paulhertz.aifile.CMYKColor CMYKColor} provide 
storage and conversion utilities for the RGB and CMYK color models in Illustrator. The {@link net.paulhertz.aifile.CustomComponent CustomComponent} 
class provides facilities for inserting comments and custom data into an AI file.</p>
<p>All subclasses of <code>DisplayComponent</code> implement <code>write()</code>, <code>draw()</code> and <code>transform()</code> methods.
Calling the methods anywhere in the document hierarchy will cascade commands to all child components. This is particularly useful when
writing to a file: a single call to the <code>DocumentComponent.write()</code> method will write out the file. The <code>draw()</code> method 
will display the document to screen, using Processing graphics calls for drawing vertices and text. <code>Transform()</code> 
accepts a matrix to perform geometric transforms on all or part of a document, including, of course, individual shapes. It is possible
to capture the Processing's current graphics state, including the fill, stroke, and the current global transform and load them into 
the attributes of a shape. Sample code shows how to do this.</p>
*/
package net.paulhertz.aifile;