/**
 *  This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.csepanda.dlib.data;

/**
 *  The <code>Node</code> class is value-weight container, 
 *  that widely used in this library.
 *  Stored value cannot be changed.
 *
 *  @param <E> the type of stored value
 *  @author Andrey Bova
 */
public class Node<E> {
    protected final E   value;
    protected       int weight;

    /**
     * Constructs new node with specified value and weight
     * 
     * @param value
     * @param weight
     */
    public Node(E value, int weight) {
        this.value  = value;
        this.weight = weight;
    }

    /** 
     * Returns stored value.
     *
     * @return stored value
     */
    public E getValue() { 
        return value;
    }

    /**
     * Returns weight of this node
     *
     * @return this node weight
     */
    public int getWeight() { 
        return weight;
    }

    /**
     * Return difference between weights of this and specified node.
     *
     * @param node comparing node
     * @return difference between weights
     */
    public int compareWeightTo(Node<E> node) {
        return weight - node.weight;
    }

    /** 
     * Return <tt>true</tt> is weight of this node is greater
     * than comparing node. 
     *
     * @param node comparing node
     * @return <tt>true</tt> if weight of this node is greater than 
     * comparing node
     */
    public boolean isHeavierThan(Node<E> node) {
        return compareWeightTo(node) > 0;
    }

    /** 
     * Return <tt>true</tt> is weight of this node is less
     * than comparing node. 
     *
     * @param node comparing node
     * @return <tt>true</tt> if weight of this node is less than 
     * comparing node
     */
    public boolean isLighterThan(Node<E> node) {
        return compareWeightTo(node) < 0;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;
        return value.equals(node.value) &&
               weight == node.weight;
    }
}
