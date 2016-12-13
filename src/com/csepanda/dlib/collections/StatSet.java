/**
 *  This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.csepanda.dlib.collections;

import java.util.Set;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.NoSuchElementException;

import com.csepanda.dlib.data.Node;
/**
 * Specific realization of interface Set.
 * Head feature is the order of elements:
 * All elements keeps in order by frequency of their adding.
 * Be careful with mutable objects, in cause of usage hash-algoritms
 * it might be a problem for correct adding, removing and storing.
 * @param <E> the type of elements maintained by this set
 * @author Andrey Bova
 * 
 */

public class StatSet<E> extends AbstractSet<E> implements Set<E> {
    
    private int           size;
    private LinkedNode<E> head;
    private LinkedNode<E> tail;
    private HashMap<E, LinkedNode<E>> containsMap;
    
    /**
     * Constructs a new, empty set     
     */
    public StatSet() {
        containsMap = new HashMap<>();
    }
    
    /**
     * Constructs a new set contains the elements in the specified collection.
     *
     * @param c the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     */ 
    public StatSet(Collection<? extends E> c) {
        containsMap = new HashMap<>(Math.max((int)(c.size()/3) + 1, 16));
        addAll(c);
    }
    
    /**
     * Returns the most often added element of this set.
     *
     * @return the most often added element of this set
     */
    public E getTop() {
        return head.getValue();
    }
    
    /**
     * Return the number of elements n this set.
     *
     * @return the number of elements in this set
     */
    public int size() {    
        return size;
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Return <tt>true</tt> if this set contains the specified element.
     *
     * @param o element whose presense in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    public boolean contains(Object o) {    
        return containsMap.containsKey(o);
    }    

    /**
     * Return <tt>true</tt> if this set contains all of the elements in the
     * specified collection.
     *
     * @param c collection to be checked for containment in this set
     * @return <tt>true</tt> if this set contains all of the elements in the
     * specified collection
     * @throws ClassCastException           {@inheritDoc}
     * @throws NullPointerException         {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {
        return containsMap.keySet().containsAll(c);
    }

    /**
     * Adds the specified element to this set if it is not already present;
     * If this set already contains this element, then weight of this element
     * increases and element in this set is raised up. 
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    public boolean add(E e) {
        if (containsMap.containsKey(e)) {
            raise(e);
            return false;
        } else {
            addLast(e);
            return true;
        }
    }
    
    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if th set contained the specified element
     */ 
    public boolean remove(Object o) {
        final LinkedNode<E> node = containsMap.get(o);
        
        if (node == null) {
            return false;
        }
       
        removeNode(node); 
        return true;
    }    

    /**
     * Returns an iterator over the elements contained in this set.
     *
     * @return an iterator over the elements contained in this set
     */
    public Iterator<E> iterator() {    
        return new ElementsIterator();
    }
   
    /**
     * Return an iterator over the StatSet's nodes.
     *
     * @return an iterator over the nodes in this set
     */
    public NodeIterator<E> nodeIterator() {
        return new StatSetIterator();
    }

    private void addLast(E e) {
        final LinkedNode<E> last    = tail;
        final LinkedNode<E> newNode = new LinkedNode<>(last, e, null);
        tail = newNode;
        if (last == null) {
            head = newNode;
        } else {
            last.next = newNode;
        }
        containsMap.put(e, newNode);
        size++;
    }

    private void raise(E e) {
        int index, weight;
        LinkedNode<E> current = containsMap.get(e);
        LinkedNode<E> parent  = current.prev;
                
        current.incrementWeight();
        if (parent        == null || 
            current.isLighterThan(parent)) {
            return;
        } // skip if doesn't need raise
        
        if (!current.isLighterThan(head)) {
            parent = head;
        }

        while (parent != null &&
               parent.isLighterThan(current)) {
            parent = parent.prev;
        } // searching for right-weight place
             
        unlinkNode(current);
        linkAfter(current, parent);
    }

    private void linkAtHead(LinkedNode<E> node) {
        node.next = head;
        node.prev = null;
        head.prev = node;
        head = node;
    }

    private void linkAfter(LinkedNode<E> node, LinkedNode<E> parent) {
        if (parent == null) {
            linkAtHead(node);
        } else {            
            node.next   = parent.next;
            node.prev   = parent;
            parent.next.prev = node;
            parent.next = node;
            if (parent == node.next.next) {
                throw new Error("daun");
            }
        }
    }

    private void unlinkNode(LinkedNode<E> node) {
        final LinkedNode<E> prev = node.prev;
        final LinkedNode<E> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }        
    }

    private void removeNode(LinkedNode<E> node) {
        unlinkNode(node); 
        containsMap.remove(node.getValue());
        size--; 
    }    

    private class LinkedNode<E> extends Node<E> {
        LinkedNode<E> next;
        LinkedNode<E> prev;

        LinkedNode(LinkedNode<E> prev, E element, LinkedNode<E> next) {
            super(element, 1);
            this.next   = next;
            this.prev   = prev;            
        }

        public int incrementWeight() {
            return weight++;
        }
    }
    
    /*                                                               */
    /*                          iterators                            */
    /*                                                               */
    private abstract class LinkedNodeIterator {
        protected LinkedNode<E> next = head;
        protected LinkedNode<E> current;

        public final boolean hasNext() {   
            return current != tail;
        }
    
        protected final LinkedNode<E> nextNode() {
            if (next == null) {
                throw new NoSuchElementException();
            }

            current = next;
            next    = next.next;
            return current;
        }

        public final void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            removeNode(current);
            current = null;
        }
    }

    private class ElementsIterator extends LinkedNodeIterator 
                                   implements Iterator<E> {
        public E next() { return nextNode().getValue(); }
    }


    /**
     * Extended iterator over the elements and their weights in StatSet.
     *
     */     
    private class StatSetIterator extends LinkedNodeIterator 
                                  implements NodeIterator<E> {
        public Node<E> next() {
            return nextNode();
        }
    }
    
}
