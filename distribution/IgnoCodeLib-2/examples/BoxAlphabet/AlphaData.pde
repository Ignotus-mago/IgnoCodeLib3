/**
 * Loads the boxList HashMap with keys and values.
 * Keys are uppercase letters of the alphabet, values are coordinates of rectangles that
 * create the letterforms, in (top, left, width, height) format. The first four coordinates
 * belong to the background rectangle. The next 4 or 8 coordinates belong to foreground
 * rectangles. Out graphic characters consist of just 1 or 2 rectangles on a background square. 
 *
 */
public void initBoxAlpha() {
  boxList = new HashMap<String, IntList>();
  boxList.put("A", new IntList(0,0,7,7,0,1,6,2,0,4,6,3));
  boxList.put("B", new IntList(0,0,7,7,0,1,6,2,0,4,6,2));
  boxList.put("C", new IntList(0,0,7,7,1,1,6,5));
  boxList.put("D", new IntList(0,0,7,7,0,1,6,5));
  boxList.put("E", new IntList(0,0,7,7,1,1,6,2,1,4,6,2));
  boxList.put("F", new IntList(0,0,7,7,1,1,6,2,1,4,6,3));
  boxList.put("G", new IntList(0,0,7,7,1,1,6,3,0,5,6,2));
  boxList.put("H", new IntList(0,0,7,7,1,0,5,3,1,4,5,3));
  boxList.put("I", new IntList(0,0,7,7,0,1,3,5,4,1,3,5));
  boxList.put("J", new IntList(0,0,7,7,0,0,6,6));
  boxList.put("K", new IntList(0,0,7,7,1,1,6,2,1,4,5,3));
  boxList.put("L", new IntList(0,0,7,7,1,0,6,6));
  boxList.put("M", new IntList(0,0,7,7,1,1,2,6,4,1,2,6));
  boxList.put("N", new IntList(0,0,7,7,1,1,2,6,4,0,2,6));
  boxList.put("O", new IntList(0,0,7,7,1,1,5,6));
  boxList.put("P", new IntList(0,0,7,7,0,1,6,2,1,4,6,3));
  boxList.put("Q", new IntList(0,0,7,7,0,1,5,5,6,0,1,6));
  boxList.put("R", new IntList(0,0,7,7,0,1,6,2,1,4,5,3));
  boxList.put("S", new IntList(0,0,7,7,1,1,6,2,0,4,6,2));
  boxList.put("T", new IntList(0,0,7,7,0,1,3,6,4,1,3,6));
  boxList.put("U", new IntList(0,0,7,7,1,0,5,6));
  boxList.put("V", new IntList(0,0,7,7,0,1,1,6,2,0,4,6));
  boxList.put("W", new IntList(0,0,7,7,1,0,2,6,4,0,2,6));
  boxList.put("X", new IntList(0,0,7,7,0,0,3,6,4,1,3,6));
  boxList.put("Y", new IntList(0,0,7,7,1,0,5,3,0,4,6,2));
  boxList.put("Z", new IntList(0,0,7,7,0,1,3,6,4,0,3,6));
  boxList.put(" ", new IntList(0,0,7,7,0,0,7,7));
}
