/**
 * BoxAlphabet
 * @author Paul Hertz
 * https://paulhertz.net/
 *
 * Generates and displays a 26-character graphic alphabet using a 7x7 grid. 
 * All characters consist of at most two rectangles superimposed on a background square.
 * Generates and displays a text created from these letterforms. 
 * Press spacebar to swap display between alphabet and text.
 * Press "s" to save to an Adobe Illustrator 7.0 file (you can sitll open this old, text-based format in AI).
 * Press "p"to save to a PDF file. 
 *
 * The IgnoCodeLib contributed library provides all sorts of hooks for creating shapes and organizing them
 * in a hierarchical tree structure of groups and layers. It also simplifies drawing and saving. Every display
 * element in the library has a draw() command. For AI files, there's also a write() command. Both commands 
 * cascade down the tree when called on a document, layer, or group object. This can make drawing pretty simple.
 * Shapes, groups and layers can also be hidden or shown. 
 *
 * Since this software involves the sort of content that sometimes is subject to legal battles, namely, a font, 
 * I am releasing it under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 Internation license, 
 * (CC BY-NC-SA 4.0, https://creativecommons.org/licenses/by-nc-sa/4.0/). 
 * 
 * This software is free for non-commercial use, please share any mods with the same license. I would also really 
 * appreciate it if you notify me if you use it in artworks or other public contexts.
 *
 */

import net.paulhertz.aifile.*;
import net.paulhertz.geom.*;
import net.paulhertz.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.pdf.*;
import processing.svg.*;


HashMap<String, IntList> boxList;
color backgroundColor = color(144, 157, 186);
color alphaBackColor1;
color alphaBackColor2;
color alphaForeColor1;
color alphaForeColor2;
color spaceColor1;
color spaceColor2;
int sxy = 8;
int d = 64;
boolean testing = false;

IgnoCodeLib igno;

/** objects that organize our geometry */
GroupComponent messageGroup;
GroupComponent alphaGroup;
DocumentComponent document;


public void setup() {
  size(1024, 1024);
  igno = new IgnoCodeLib(this);
  initBoxAlpha();
  setupGeometry();
  showHelp();
}


/** 
 * Sets colors and loads geometry into the alphaGroup and messageGroup. 
 */
public void setupGeometry() {
  alphaBackColor1 = color(21, 34, 55);
  alphaBackColor2 = color(89, 110, 233);
  alphaForeColor1 = color(233, 233, 47);
  alphaForeColor2 = color(199, 34, 42);
  alphaGroup = loadAlphabet();
  alphaGroup.hide();
  alphaBackColor1 = color(254, 251, 246);
  alphaBackColor2 = color(254, 246, 233);
  alphaForeColor1 = color(199, 34, 42);
  alphaForeColor2 = color(55, 89, 144);
  //spaceColor1 = color(254, 199, 21);
  //spaceColor2 = color(233, 178, 34);
  spaceColor1 = backgroundColor;
  spaceColor2 = backgroundColor;
  String msg;
  msg = "Oblivion is not to be hired: The greater part must be content to be as though they had not been, " 
    + "to be found in the Register of God, not in the record of man. Twenty seven Names make up the first story, " 
    + "and the recorded names ever since contain not one living Century. The number of the dead long exceedeth " 
    + "all that shall live. The night of time far surpasseth the day, and who knows when was the Equinox? " 
    + "Every houre addes unto that current Arithmetique, which scarce stands one moment. And since death must be the " 
    + "Lucina of life, and even Pagans could doubt whether thus to live, were to dye. Since our longest " 
    + "Sunne sets at right descensions, and makes but winter arches, and therefore it cannot be long before " 
    + "we lie down in darknesse, and have our lights in ashes. Since the brother of death daily haunts us " 
    + "with dying mementos, and time that grows old it self, bids us hope no long duration: Diuturnity is " 
    + "a dream and folly of expectation.";
  messageGroup = loadMessage(msg);
}


public void showHelp() {
  println("----->>> Press spacebar to swap display.              <<<-----");
  println("----->>> Press 's' to save to Adobe Illustrator file. <<<-----");
  println("----->>> Press 'p' to save to PDF file.               <<<-----");
  println("----->>> Press 'v' to save to SVG file.               <<<-----");
}


public void draw() {
  background(backgroundColor);
  alphaGroup.draw();
  messageGroup.draw();
}


public void keyPressed() {
  if (key == ' ') {
    if (alphaGroup.isVisible()) {
      alphaGroup.hide();
      messageGroup.show();
    } else {
      alphaGroup.show();
      messageGroup.hide();
    }
  } 
  else if (key == 's' || key == 'S') {
    println("----->>> SAVING AI");
    saveAI("message+alphabet.ai");
  } 
  else if (key == 'p' || key == 'P') {
    println("----->>> SAVING PDF");
    savePDF("message+alphabet.pdf");
  }
  else if (key == 'v' || key == 'V') {
    println("----->>> SAVING SVG");
    saveSVG("message+alphabet.svg");
  }
}


/** 
 * Generates geometry for the alphabet defined in the boxList HashMap and 
 * returns it in a GroupComponent. 
 *
 * @return   a GroupComponent with a subgroup for each letter in the alphabet.
 */
public GroupComponent loadAlphabet() {
  float startX = 64;
  float startY = 64;
  float scaleXY = 2 * sxy;
  float tx = startX;
  float xinc = scaleXY * 8;
  float ty = startY;
  float yinc = scaleXY * 8;
  int rowCount = 5;
  int i = 0;
  GroupComponent alphaGroup = new GroupComponent();
  for (IntList letter : boxList.values()) {
    GroupComponent g = loadChar(letter, scaleXY, tx, ty);
    alphaGroup.add(g);
    tx += xinc;
    i++;
    if (i >= rowCount) {
      i = 0;
      ty += yinc;
      tx = startX;
    }
  }
  return alphaGroup;
}


/** 
 * Generates geometry for a graphical representation of the supplied String and
 * returns it wrapped in a GroupComponent. 
 *
 * @param  mess   the String to be encoded as geometry
 * @return geometry wrapped in a GroupComponent     
 */
public GroupComponent loadMessage(String mess) {
  String[] words = mess.toUpperCase().split(" ");
  float startX = 32;
  float startY = 64;
  float scaleXY = sxy * 0.5;
  float tx = startX;
  float xinc = scaleXY * 7;
  float ty = startY;
  float yinc = scaleXY * 7;
  int breakWord = 8;
  int charCount = 0;
  GroupComponent g;
  GroupComponent messGroup = new GroupComponent();
  Pattern pattern = Pattern.compile("[A-Z]+");
  for (int i = 0; i < words.length; i++) {
    String src = words[i];
    Matcher matcher = pattern.matcher(src);
    if (matcher.find()) {
      String word = matcher.group();
      if (testing) println(word);
      int n = 0;
      for (n = 0; n < word.length(); n++) {
        String ch = str(word.charAt(n));
        IntList letter = boxList.get(ch);
        // alternate colors
        color c1 = charCount % 2 == 0 ? alphaBackColor1 : alphaBackColor2;
        color c2 = charCount % 2 == 0 ? alphaForeColor1 : alphaForeColor2;
        charCount++;
        g = loadChar(letter, scaleXY, tx, ty, c2, c1);
        messGroup.add(g);
        tx += xinc;
      }
      // we reached the end of a word
      IntList letter = boxList.get(" ");
      // alternate colors
      color c1 = charCount % 2 == 0 ? spaceColor1 : spaceColor2;
      color c2 = charCount % 2 == 0 ? alphaBackColor1 : alphaBackColor2;
      charCount++;
      g = loadChar(letter, scaleXY, tx, ty, c2, c1);
      tx += xinc;
      // breakword is used to set a somewhat arbitrary limit on line length
      if (tx > width - breakWord * xinc) {
        // start a new line of text
        tx = startX;
        ty += yinc;
        charCount = 0;
        // swap the colors to keep up the checkerboard pattern
        int temp = alphaBackColor1;
        alphaBackColor1 = alphaBackColor2;
        alphaBackColor2 = temp;
        temp = alphaForeColor1;
        alphaForeColor1 = alphaForeColor2;
        alphaForeColor2 = temp;
        temp = spaceColor1;
        spaceColor1 = spaceColor2;
        spaceColor2 = temp;
      } else {
        messGroup.add(g);
      }
    }
  }
  return messGroup;
}


/** 
 * Generates geometry for an individual letterform in our graphical alphabet.
 *
 * @param letter    an IntList of coordinate values (see initBoxAlpha())
 * @param scaleXY   scaling factor for geometry
 * @param tx        x-axis translation, pixels
 * @param ty        y-axis translation, pixels
 *
 * @return          geometry of a single letterform wrapped in a GroupComponent
 */
public GroupComponent loadChar(IntList letter, float scaleXY, float tx, float ty) {
  int[] coords = letter.array();
  int i = 0;
  GroupComponent g = new GroupComponent();
  BezRectangle r0 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
  r0.setNoStroke();
  r0.setFillColor(alphaBackColor1);
  r0.scaleShape(scaleXY, 0, 0);
  r0.translateShape(tx, ty);
  g.add(r0);
  BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
  r1.setNoStroke();
  r1.setFillColor(alphaForeColor1);
  r1.scaleShape(scaleXY, 0, 0);
  r1.translateShape(tx, ty);
  g.add(r1);
  if (coords.length == 12) {
    BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
    r2.setNoStroke();
    r2.setFillColor(alphaForeColor1);
    r2.scaleShape(scaleXY, 0, 0);
    r2.translateShape(tx, ty);
    g.add(r2);
  }
  return g;
}


/** 
 * Generates geometry for an individual letterform in our graphical alphabet using supplied colors.
 *
 * @param letter    an IntList of coordinate values (@see initBoxAlpha())
 * @param scaleXY   scaling factor for geometry
 * @param tx        x-axis translation, pixels
 * @param ty        y-axis translation, pixels
 * @param bg        background color
 * @param fg        foreground color
 *
 * @return          geometry of a single letterform wrapped in a GroupComponent
 */
public GroupComponent loadChar(IntList letter, float scaleXY, float tx, float ty, color bg, color fg) {
  int[] coords = letter.array();
  int i = 0;
  GroupComponent g = new GroupComponent();
  BezRectangle r0 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
  r0.setNoStroke();
  r0.setFillColor(bg);
  r0.scaleShape(scaleXY, 0, 0);
  r0.translateShape(tx, ty);
  g.add(r0);
  BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
  r1.setNoStroke();
  r1.setFillColor(fg);
  r1.scaleShape(scaleXY, 0, 0);
  r1.translateShape(tx, ty);
  g.add(r1);
  if (coords.length == 12) {
    BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(coords[i++], coords[i++], coords[i++], coords[i++]);
    r2.setNoStroke();
    r2.setFillColor(fg);
    r2.scaleShape(scaleXY, 0, 0);
    r2.translateShape(tx, ty);
    g.add(r2);
  }
  return g;
}


/**
 * Saves geometry as an Adobe Illustrator 7.0 file, an old text-based PostScript format 
 * that still opens in current version of Illustrator. 
 *
 * The alphabet (alphaGroup) and message (messageGroup) are placed in separate layers. 
 * The layers and all groups we created will show up in Adobe Illustrator. That's the 
 * convenience of using IgnoCodeLIb to output to AI.
 */
private void saveAI(String aiFileName) {
  document = new DocumentComponent("Boxy Alphabet");
  // get lots of feedback as we save
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("paulhertz.net");
  document.setWidth(width);
  document.setHeight(height);
  Palette pal = document.getPalette();
  pal.addBlackWhiteGray();
  // now add some layers and give them some geometry 
  // make all the geometry visible in the file
  LayerComponent alphaLayer = new LayerComponent("Alphabet");
  boolean alphaIsVisible = alphaGroup.isVisible();
  alphaGroup.show();
  alphaLayer.add(alphaGroup);
  document.add(alphaLayer);
  LayerComponent messageLayer = new LayerComponent("Message");
  boolean messageIsVisible = messageGroup.isVisible();
  messageGroup.show();
  messageLayer.add(messageGroup);
  document.add(messageLayer);
  PrintWriter output = createWriter(aiFileName);
  document.write(output);
  alphaGroup.setVisible(alphaIsVisible);
  messageGroup.setVisible(messageIsVisible);
}

/**
 * Saves geometry to a PDF file. It's pretty simple to do: just call beginRecord(), call draw() 
 * for the top-level element or elements in the display graph, and then call endRecord().
 */
private void savePDF(String pdfFileName) {
  beginRecord(PDF, pdfFileName);
  // make sure everything is visible, otherwise it won't draw.
  // PDF file format will won't output our groups, making a real flat jumble, so let's omit alphaGroup.
  /*
  boolean alphaIsVisible = alphaGroup.isVisible();
  alphaGroup.show();
  alphaGroup.draw();
  // restore visibility setting
  alphaGroup.setVisible(alphaIsVisible);
  */
  boolean messageIsVisible = messageGroup.isVisible();
  messageGroup.show();
  messageGroup.draw(); 
  // restore visibility setting
  messageGroup.setVisible(messageIsVisible);
  endRecord();
}

/**
 * Saves geometry to a SVG file. It's pretty simple to do: just call beginRecord(), call draw() 
 * for the top-level element or elements in the display graph, and then call endRecord(). 
 */
private void saveSVG(String svgFileName) {
  beginRecord(SVG, svgFileName);
  // make sure everything is visible, otherwise it won't draw.
  // SVG file format will won't output our groups, making a real flat jumble, so let's omit alphaGroup.
  /*
  boolean alphaIsVisible = alphaGroup.isVisible();
  alphaGroup.hide();
  alphaGroup.draw();
  // restore visibility setting
  alphaGroup.setVisible(alphaIsVisible);
  */
  boolean messageIsVisible = messageGroup.isVisible();
  messageGroup.show();
  messageGroup.draw(); 
  // restore visibility setting
  messageGroup.setVisible(messageIsVisible);
  endRecord();
}
