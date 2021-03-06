/************************************************************************
 * Copyright (c) Crater Dog Technologies(TM).  All Rights Reserved.     *
 ************************************************************************
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.        *
 *                                                                      *
 * This code is free software; you can redistribute it and/or modify it *
 * under the terms of The MIT License (MIT), as published by the Open   *
 * Source Initiative. (See http://opensource.org/licenses/MIT)          *
 ************************************************************************/
package craterdog.collections.primitives;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


/**
 * This class provides an implementation of a doubly linked list.
 *
 * @author Derk Norton
 * @param <E> The type of the elements in the linked list.
 */
public final class LinkedList<E> extends AbstractCollection<E> implements List<E>, RandomAccess, Cloneable {

    // the current number of elements in the list
    private int size = 0;

    // the head of the list
    private Link<E> head = null;

    /**
     * This default constructor creates an empty instance of a linked list.
     */
    public LinkedList() {
    }


    /**
     * This constructor creates an instance of a linked list that contains the elements from
     * the specified collection.
     *
     * @param elements The elements that should be used to seed the list.
     */
    public LinkedList(Collection<? extends E> elements) {
        for (E element : elements) {
            add(element);
        }
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public int indexOf(Object element) {
        Iterator<E> iterator = iterator();
        for (int index = 0; index < size; index++) {
            E candidate = iterator.next();
            if (candidate == null ? element == null : candidate.equals(element)) return index;
        }
        return -1;  // not found
    }


    @Override
    public int lastIndexOf(Object element) {
        ListIterator<E> iterator = listIterator(size);
        for (int index = size - 1; index >= 0; index--) {
            E candidate = iterator.previous();
            if (candidate == null ? element == null : candidate.equals(element)) return index;
        }
        return -1;  // not found
    }


    @Override
    public E get(int index) {
        checkBounds(index);
        Link<E> link = getLinkAtIndex(index);
        E element = link.value;
        return element;
    }


    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        checkBounds(fromIndex);
        checkBounds(toIndex);
        int numberOfElements = toIndex - fromIndex;
        LinkedList<E> results = new LinkedList<>();
        ListIterator<E> iterator = listIterator(fromIndex);
        for (int i = 0; i < numberOfElements; i++) {
            E element = iterator.next();
            results.add(element);
        }
        return results;
    }


    @Override
    public E set(int index, E element) {
        checkBounds(index);
        Link<E> link = getLinkAtIndex(index);
        E oldElement = link.value;
        link.value = element;
        return oldElement;
    }


    @Override
    public boolean add(E element) {
        appendElementToList(element);
        return true;
    }


    @Override
    public void add(int index, E newElement) {
        checkBounds(index + 1);
        if (size == 0 || index == size) {
            appendElementToList(newElement);
        } else {
            Link<E> existingLink = getLinkAtIndex(index);
            insertElementIntoList(existingLink, newElement);
        }
    }


    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        checkBounds(index);
        ListIterator<E> iterator = listIterator(index);
        for (E element : collection) {
            iterator.add(element);
        }
        return !collection.isEmpty();
    }


    @Override
    public E remove(int index) {
        checkBounds(index);
        Link<E> existingLink = getLinkAtIndex(index);
        E element = existingLink.value;
        removeLinkFromList(existingLink);
        return element;
    }


    /**
     * This method removes the elements in the specified range [firstIndex..lastIndex) and shifts
     * the existing elements down to fill in the gap. This method returns a list containing
     * the removed elements.
     *
     * @param firstIndex The index of the first element to be removed.
     * @param lastIndex The index of the last element after the range to be removed.
     * @return The elements that were removed from the list.
     */
    public List<E> remove(int firstIndex, int lastIndex) {
        checkBounds(firstIndex);
        checkBounds(lastIndex);
        int numberRemoved = lastIndex - firstIndex;
        LinkedList<E> results = new LinkedList<>();

        if (numberRemoved < 1) {
            // nothing removed so return empty list
            return results;
        }

        if (numberRemoved == size) {
            // remove all links
            results.head = head;
            results.size = size;
            head = null;
            size = 0;
        } else {
            // retrieve the links
            Link<E> firstLink = getLinkAtIndex(firstIndex);
            Link<E> lastLink = getLinkAtIndex(lastIndex);  // the link past the range

            // initialize the new linked list
            results.head = firstLink;
            results.size = numberRemoved;

            // remove the links in the range
            Link.removeLinks(firstLink, lastLink);
            if (firstIndex == 0) head = lastLink;
            size -= numberRemoved;
        }

        return results;
    }


    @Override
    public boolean remove(Object object) {
        // check for an empty list
        if (size == 0) return false;

        // search the list for the element
        Link<E> link = head;
        do {
            if (link.value == null ? object == null : link.value.equals(object)) {
                removeLinkFromList(link);
                return true;
            }
            link = link.next;
        } while (link != head);

        return false;
    }


    @Override
    public void clear() {
        if (head != null) {
            // break the chain to help the garbage collector
            if (head.previous != null) {
                head.previous.next = null;
                head.previous = null;
            }
            head = null;
            size = 0;
        }
    }


    @Override
    public craterdog.core.Iterator<E> iterator() {
        return new LinkedListIterator();
    }


    @Override
    public ListIterator<E> listIterator() {
        return new LinkedListIterator();
    }


    @Override
    public ListIterator<E> listIterator(int index) {
        checkBounds(index);
        return new LinkedListIterator(index);
    }


    @Override
    // NOTE: Only ordered collections whose elements are in the same order will be equal.
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof List)) return false;
        List<?> that = (List<?>) object;
        if (this.size != that.size()) return false;
        ListIterator<E> e1 = this.listIterator();
        ListIterator<?> e2 = that.listIterator();
        while(e1.hasNext()) {
            E element1 = e1.next();
            Object element2 = e2.next();
            if (!(element1 == null ? element2 == null : element1.equals(element2))) return false;
        }
        return true;
    }


    @Override
    // NOTE: Only ordered collections whose elements are in the same order will have equal hash codes.
    public int hashCode() {
        int hashCode = 1;
        for (E element : this)
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        return hashCode;
    }


    @Override
    public Object clone() {
        try {
            @SuppressWarnings("unchecked")
            LinkedList<E> copy = (LinkedList<E>) super.clone();
            copy.head = null;
            copy.size = 0;
            for (E element : this) {
                copy.add(element);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    private void checkBounds(int index) throws IndexOutOfBoundsException {
        int maximum = size - 1;
        if (index < 0 || index > maximum) throw new IndexOutOfBoundsException("The index (" + index + ") is outside the allowed range: [0.." + maximum + "].");
    }


    private Link<E> getLinkAtIndex(int index) {
        Link<E> link = head;
        if (index < size / 2) {
            for (int i = 0; i < index; i++) {
                link = link.next;
            }
        } else {  // it is in the second half
            for (int i = size; i > index; i--) {
                link = link.previous;
            }
        }
        return link;
    }


    private void appendElementToList(E element) {
        Link<E> newLink = new Link<>(element);
        if (head == null) {
            head = newLink;
            head.next = head;
            head.previous = head;
        } else {
            Link.insertBeforeLink(newLink, head);
        }
        size++;
    }


    private void insertElementIntoList(Link<E> existingLink, E element) {
        Link<E> newLink = new Link<>(element);
        Link.insertBeforeLink(newLink, existingLink);
        if (head == existingLink) head = newLink;
        size++;
    }


    private void removeLinkFromList(Link<E> existingLink) {
        if (head == existingLink) head = existingLink.next;
        Link.removeLink(existingLink);
        if (--size == 0) head = null;
    }


    private class LinkedListIterator extends craterdog.core.Iterator<E> implements ListIterator<E> {

        int index;
        Link<E> link;
        Link<E> lastLink;

        private LinkedListIterator() {
            this.index = 0;
            this.link = head;
            this.lastLink = null;
        }

        private LinkedListIterator(int index) {
            this.index = index;
            this.link = getLinkAtIndex(index);
            this.lastLink = null;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public E previous() {
            return getPrevious();
        }

        @Override
        public void set(E element) {
            if (lastLink == null) throw new IllegalStateException();
            lastLink.value = element;
        }

        @Override
        public void add(E element) {
            if (size == 0 || index == size) {
                appendElementToList(element);
            } else {
                insertElementIntoList(link, element);
            }
            lastLink = null;
        }

        @Override
        public void remove() {
            if (lastLink == null) throw new IllegalStateException();
            removeLinkFromList(lastLink);
            if (link == lastLink) link = lastLink.next;
            lastLink = null;
        }

        @Override
        public void toStart() {
            index = 0;
            link = head;
        }

        @Override
        public void toIndex(int i) {
            index = i;
            link = getLinkAtIndex(index);
        }

        @Override
        public void toEnd() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public E getPrevious() {
            if (index == 0) throw new NoSuchElementException();
            link = link.previous;
            lastLink = link;
            E element = link.value;
            index--;
            return element;
        }

        @Override
        public E getNext() {
            if (index == size) throw new NoSuchElementException();
            E element = link.value;
            lastLink = link;
            link = link.next;
            index++;
            return element;
        }

    }


    static private class Link<T> implements Cloneable {

        /**
         * This attribute contains the value encapsulated by this link.
         */
        public T value;

        /**
         * This attribute points to the previous link in the list.
         */
        public Link<T> previous;

        /**
         * This attribute points to the next link in the list.
         */
        public Link<T> next;


        /**
         * This constructor takes a value and creates a <code>Link</code> that encapsulates it.
         *
         * @param value The value to be encapsulated in a link.
         */
        private Link(T value) {
            this.value = value;
            this.previous = null;
            this.next = null;
        }


        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Link)) return false;
            Link<?> that = (Link<?>) object;
            return this.value.equals(that.value);
        }


        @Override
        public int hashCode() {
            return value.hashCode();
        }


        @Override
        public Object clone() throws CloneNotSupportedException {
            @SuppressWarnings("unchecked")
            Link<T> copy = (Link<T>) super.clone();
            copy.previous = previous;
            copy.next = next;
            copy.value = value;
            return copy;
        }


        /**
         * This utility method inserts a new link in a linked list before the specified existing link.
         *
         * @param <T> The type of element encapsulated by the link.
         * @param newLink The new link to be inserted.
         * @param existingLink The existing link before which the new link will be inserted.
         */
        static public <T> void insertBeforeLink(Link<T> newLink, Link<T> existingLink) {
            newLink.next = existingLink;
            newLink.previous = existingLink.previous;
            existingLink.previous.next = newLink;
            existingLink.previous = newLink;
        }


        /**
         * This utility method removes the specified link from a linked list.
         *
         * @param <T> The type of element encapsulated by the link.
         * @param link The link to be removed.
         */
        static public <T> void removeLink(Link<T>  link) {
            link.previous.next = link.next;
            link.next.previous = link.previous;
            link.previous = null;
            link.next = null;
        }


        /**
         * This utility method removes a set of links from a linked list, starting with the first
         * link and including the link before the lastLink. Note, that this means the last link
         * is not removed from the list.
         *
         * @param <T> The type of element encapsulated by the link.
         * @param firstLink The first link in the sub chain to be removed.
         * @param lastLink The link after the last link in the sub chain to be removed.
         */
        static public <T> void removeLinks(Link<T>  firstLink, Link<T>  lastLink) {
            Link<T>  temp = lastLink.previous;
            firstLink.previous.next = lastLink;
            lastLink.previous.next = firstLink;
            lastLink.previous = firstLink.previous;
            firstLink.previous = temp;
        }

    }

}