/*
 * Copyright (c) 2011, Paul Hertz This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 3.0 of the License, or (at your option) any later version.
 * http://www.gnu.org/licenses/lgpl.html This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 * 
 */

package net.paulhertz.util;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author Paul Hertz 
 */
/**
 * Maintains an <code>ArrayList</code> of <code>Integer</code> initialized in ascending order, and 
 * steps it to each successive permutation. The next permutation is the one
 * that would appear next in a sorted list of all permutations, where the sorting function
 * orders in ascending order. In the final permutation, each number is greater than the previous
 * and no more permutations are possible.
 * <p>
 * This class is particularly intended for permutation of the indices of arrays of objects. 
 * </p>
 * TODO permutations of m objects taken n at a time.
 */
public class Permutator {
	private ArrayList <Integer> perm;
	private int size;
	
	public Permutator(int size) {
		this.size = size;
		this.perm = new ArrayList<Integer>();
		initPerm();
	}
	
	
	/**
	 * Initializes the permutation array to integer values from 0 to size - 1.
	 */
	public void initPerm() {
		perm.clear();
		for (int i = 0; i < size; i++) {
			perm.add(Integer.valueOf(i));
		}
	}
	
	
	/**
	 * Returns the permutation array. Any modifications to the size of the array can 
	 * cause tragic errors in later calls to this class. If you want to mess with
	 * the permutation array, call {@link #getPermCopy() getPermCopy}.
	 * @return the perm
	 */
	public ArrayList<Integer> getPerm() {
		return perm;
	}
	/**
	 * Set the permutation array to the supplied <code>ArrayList</code> of <code>Integer</code>.
	 * @param perm   an <code>ArrayList</code> of <code>Integer</code> to set
	 */
	public void setPerm(ArrayList<Integer> perm) {
		this.perm = perm;
		this.size = perm.size();
	}

	
	/**
	 * Returns a copy of the permutation array: call this if you feel the urge to modify the array.
	 * @return  an <code>ArrayList</code> of <code>Integer</code>, copy of the permutation array
	 */
	public ArrayList<Integer> getPermCopy() {
		return new ArrayList<Integer>(this.perm);
	}
 
	
	/**
	 * @return the size of the permutation array
	 */
	public int getSize() {
		return size;
	}
	
	
	/**
	 * Steps to next permutation of the permutation array, returns false if the last permutation 
	 * has been reached. The next permutation is the one
	 * that would appear next in a sorted list of all permutations, where the sorting function
	 * orders in ascending order. In the final permutation, each number is greater than the previous
	 * and no more permutations are possible. This is an implementation of an algorithm described by 
	 * Edsger Dijkstra in "A Discipline of Programming" [Prentice-Hall, 1976].
	 * @return   true if the permutation was successfully generated, otherwise false
	 */
	public boolean nextPerm() {
		int i = this.size - 2;
		while (this.perm.get(i) >= this.perm.get(i + 1)) {
			if (i-- <= 0) {
				// permutation is in terminal ordering
				return false;
			}
		}
		int j = this.size - 1;
		while (this.perm.get(j) <= this.perm.get(i)) j--;
		swap(i, j, this.perm);
		i++;
		j = this.size - 1;
		while (i < j) {
			swap(i, j, this.perm);
			i++;
			j--;
		}
		/* System.out.println("i: " + i + " j: " + j); */
		return true;
	}
	
	
	public String toString() {
		ListIterator<Integer> li = this.perm.listIterator();
		StringBuffer buf = new StringBuffer();
		while (li.hasNext()) {
			buf.append(li.next());
		}
		return buf.toString();
	}
	
	
	/***************** Static Methods ****************/

	/**
	 * Steps to next permutation of an <code>ArrayList</code> of <code>Integer</code>, 
	 * returns false if the last permutation has been reached. The next permutation is the one
	 * that would appear next in a sorted list of all permutations, where the sorting function
	 * orders in ascending order. In the final permutation, each number is greater than the previous
	 * and no more permutations are possible. This is an implementation of an algorithm described by 
	 * Edsger Dijkstra in "A Discipline of Programming" [Prentice-Hall, 1976].
	 * @return   true if the permutation was successfully generated, otherwise false
	 */
	public static boolean nextPerm(ArrayList<Integer> permArray) {
	  int i = permArray.size() - 2;
	  while (permArray.get(i) >= permArray.get(i + 1)) {
	    if (i-- <= 0) {
	      // permutation is in terminal ordering
	      return false;
	    }
	  }
	  int j = permArray.size() - 1;
	  while (permArray.get(j) <= permArray.get(i)) j--;
	  swap(i, j, permArray);
	  i++;
	  j = permArray.size() - 1;
	  while (i < j) {
	    swap(i, j, permArray);
	    i++;
	    j--;
	  }
	  return true;
	}


	/**
	 * Steps to next permutation of an array of integers, returns false if the last permutation
	 * has been reached. The next permutation is the one
	 * that would appear next in a sorted list of all permutations, where the sorting function
	 * orders in ascending order. In the final permutation, each number is greater than the previous
	 * and no more permutations are possible. This is an implementation of an algorithm described by 
	 * Edsger Dijkstra in "A Discipline of Programming" [Prentice-Hall, 1976].
	 * @return   true if the permutation was successfully generated, otherwise false
	 */
	public static boolean nextPerm(int[] permArray) {
	  int i = permArray.length - 2;
	  while (permArray[i] >= permArray[i + 1]) {
	    if (i-- <= 0) {
	      // permutation is in terminal ordering
	      return false;
	    }
	  }
	  int j = permArray.length - 1;
	  while (permArray[j] <= permArray[i]) j--;
	  swap(i, j, permArray);
	  i++;
	  j = permArray.length - 1;
	  while (i < j) {
	    swap(i, j, permArray);
	    i++;
	    j--;
	  }
	  return true;
	}


	/**
	 * Swap elements at specified positions in an <code>ArrayList</code> of <code>Integer</code>.
	 * @param i   position in array
	 * @param j   position in array
	 * @param permArray   an <code>ArrayList</code> of <code>Integer</code>
	 */
	static private void swap(int i, int j, ArrayList<Integer> permArray) {
	  Integer temp = permArray.get(i);
	  permArray.set(i, permArray.get(j));
	  permArray.set(j, temp);  
	}


	/**
	 * Swap elements at specified positions in an array of <code>int</code>.
	 * @param i   position in array
	 * @param j   position in array
	 * @param permArray   an array of <code>int</code>
	 */
	static private void swap(int i, int j, int[] permArray) {
	  int temp = permArray[i];
	  permArray[i] = permArray[j];
	  permArray[j] = temp;  
	}

	
	/**
	 * Converts elements in the supplied permutation to a string.
	 * @param permArray   an <code>ArrayList</code> of <code>Integer</code>
	 * @return   a String representing the values in the permutation
	 */
	public static String listToString(ArrayList<Integer> permArray) {
	  ListIterator<Integer> li = permArray.listIterator();
	  StringBuffer buf = new StringBuffer();
	  while (li.hasNext()) {
	    buf.append(li.next());
	  }
	  return buf.toString();
	}
	
	/**
	 * Converts elements in the supplied permutation to a string.
	 * @param permArray   an <code>ArrayList</code> of <code>Integer</code>
	 * @return   a String representing the values in the permutation
	 */
	public static String listToString(int[] permArray) {
	  StringBuffer buf = new StringBuffer();
	  for (int i = 0; i < permArray.length; i++) {
	    buf.append(permArray[i]);
	  }
	  return buf.toString();
	}
	
}
