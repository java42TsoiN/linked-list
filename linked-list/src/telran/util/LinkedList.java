package telran.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class LinkedList<T> extends AbstractList<T> {

	private static class Node<T> {
		T obj;
		Node<T> next;
		Node<T> prev;

		Node(T obj) {
			this.obj = obj;
		}
	}

	private Node<T> head; // reference to the first element
	private Node<T> tail; // reference to the last element
	private class LinkedListIterator implements Iterator<T>{
		Node<T> current = head;
		boolean doNext = false;
		@Override
		public boolean hasNext() {
			return current!=null;
		}

		@Override
		public T next() {
			if(!hasNext()) throw new NoSuchElementException();
			//return current T object
			T res = current.obj;
			//FIXME check res and throwing exception
			//moves to a next current
			doNext=true;
			current = current.next;
			return res;
		}
		
		

		@Override
		public void remove() {
			//FIXME
			if(doNext==false) throw  new IllegalStateException();
			if(current==null) {
				removeNode(tail);
			}else
				removeNode(current.prev);
			doNext=false;
		}
		
	}
	@Override
	public void add(T element) {
		//Complexity O[1]
		Node<T> newNode = new Node<>(element);
		if (head == null) {
			head = tail = newNode;
		} else {
			tail.next = newNode;
			newNode.prev = tail;
			tail = newNode;
		}
		size++;

	}
	private Node<T> getNode(int index) {
		//O[N]
		Node<T> res = null;
		if(isValidIndex(index)) {
			res = index <= size / 2 ? getNodefromLeft(index) : getNodeFromRight(index);
		}
		return res;
	}

	private Node<T> getNodeFromRight(int index) {
		Node<T> current = tail;
		int ind = size - 1;
		while(ind != index) {
			ind--;
			current = current.prev;
		}
		return current;
	}
	private Node<T> getNodefromLeft(int index) {
		Node<T> current = head;
		int ind = 0;
		while(ind != index) {
			ind++;
			current = current.next;
		}
		return current;
	}
	
	@Override
	public boolean add(int index, T element) {
		//O[N]
		boolean res = false;
		if (index == size) {
			add(element);
			res = true;
		} else if (isValidIndex(index)) {
			res = true;
			Node<T> newNode = new Node<>(element);
			if (index == 0) {
				addHead(newNode);
			} else {
				addMiddle(newNode, index);
			}
			size++;
		}
		return res;
	}

	private void addMiddle(Node<T> newNode, int index) {
		Node<T> nodeAfter = getNode(index);
		newNode.next = nodeAfter;
		newNode.prev = nodeAfter.prev;
		//nodeAfter.prev => reference to the old previous element
		nodeAfter.prev.next = newNode;
		nodeAfter.prev = newNode;
		
	}
	private void addHead(Node<T> newNode) {
		newNode.next = head;
		head.prev = newNode;
		head = newNode;
		
	}

	@Override
	public T get(int index) {
		//O[N]
		T res = null;
		Node<T> resNode = getNode(index);
		if (resNode != null) {
			res = resNode.obj;
		}
		return res;
	}

	@Override
	public T remove(int index) {
		//O[N]
		return isValidIndex(index) ? removeNode(getNode(index)) : null;
	}

	@Override
	public int indexOf(Predicate<T> predicate) {
		//O[N]
		Node<T> current = head;
		int res = -1;
		for(int i = 0; i < size; i++) {
			if (predicate.test(current.obj)) {
				res = i;
				break;
			}
			current = current.next;
		}
		
		return res;
	}

	@Override
	public int lastIndexOf(Predicate<T> predicate) {
		//O[N]
		int res = -1;
		Node<T> current = tail;
		for(int i = size - 1; i >=0; i--) {
			if (predicate.test(current.obj)) {
				res = i;
				break;
			}
			current = current.prev;
		}
		return res;
	}

	@Override
	public boolean removeIf(Predicate<T> predicate) {
		//O[N]
		Iterator<T> it= iterator();
		int oldSize = size;
		while (it.hasNext()){
            if (predicate.test(it.next())){
            	it.remove();
            }
        }
		return oldSize>size;
	}

	private T removeNode(Node<T> current) {
		if (head == tail) {
			head = tail = null;
		} else if (current == head) {
			removeHead();
		} else if (current == tail) {
			removeTail();
		} else {
			removeMiddle(current);
		}
		size--;
		return current.obj;
		
	}
	private void removeMiddle(Node<T> current) {
		Node<T> beforeRemoved = current.prev;
		Node<T> afterRemoved = current.next;
		beforeRemoved.next = afterRemoved;
		afterRemoved.prev = beforeRemoved;
		
	}
	private void removeTail() {
		tail = tail.prev;
		tail.next = null;
		
		
	}
	private void removeHead() {
		head = head.next;
		head.prev = null;
		
	}
	@Override
	public void sort(Comparator<T> comp) {
		//O[N*logN]
		T[] array = listToArray();
		Arrays.sort(array, comp);
		fillListFromArray(array);
		

	}
	@SuppressWarnings("unchecked")
	private T[] listToArray() {
		
		//creates array of T objects
		//passes over whole list and fills the array
		T[] res = (T[]) new Object[size];
		Node<T> current = head;
		for(int i = 0; i < size; i++) {
			res[i] = current.obj;
			current = current.next;
		}
		return res;
	}
	private void fillListFromArray(T[] array) {
		
		//passes over whole list and fills elements from index=0 to index=size - 1 
		Node<T> current = head;
		for(int i = 0; i < array.length; i++) {
			current.obj = array[i];
			current = current.next;
		}
	}
	@Override
	public int sortedSearch(T pattern, Comparator comp) {
		Node<T> current = head;
		int res = -(size + 1);
		for (int i = 0; i < size; i++) {
			int resComp = comp.compare(pattern, current.obj);
			if (resComp == 0) {
				res = i;
				break;
			} else if(resComp < 0) {
				res = -(i + 1);
				break;
			}
			current = current.next;
		}
		return res;
	}
	@Override
	public void clear() {
		head = tail = null;
		size = 0;
	}
	@Override
	public Iterator<T> iterator() {
		
		return new LinkedListIterator();
	}

}