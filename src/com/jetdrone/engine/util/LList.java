package com.jetdrone.engine.util;

public final class LList<T> {
	
	public static final class Node<T> {
		private T data;
		private Node<T> next;
		
		public Node(T o) {
			this.data = o;
		}
		
		public Node<T> next() {
			return next;
		}
		
		public T data() {
			return data;
		}
	}
	
	private Node<T> head;
	
	public void append(T o) {
		if(head == null) {
			head = new Node<T>(o);
		} else {
			Node<T> it = head;
			while(it.next != null) {
				it = it.next;
			}
			it.next = new Node<T>(o);
		}
	}
	
	public void prepend(T o) {
		Node<T> n = new Node<T>(o);
		n.next = head;
		head = n;
	}
	
	public T remove(T o) {
		if(head != null) {
			Node<T> it = head;
			Node<T> last = null;
			while(it != null) {
				if(it.data.equals(o)) {
					if(it.next != null) {
						it.data = it.next.data;
						it.next = it.next.next;
						return o;
					} else {
						it.data = null;
						it.next = null;
						last.next = null;
						return o;
					}
				}
				last = it;
				it = it.next;
			}
		}
		return null;
	}
	
	public T removeFirst() {
		if(head != null) {
			T value = head.data;
			if(head.next != null) {
				head.data = head.next.data;
				head.next = head.next.next;
			} else {
				head = null;
			}
			return value;
		}
		return null;
	}
	
	public Node<T> iterator() {
		return head;
	}
	
	public void clear() {
		Node<T> it = head;
		Node<T> tmp;
		while(it != null) {
			it.data = null;
			tmp = it.next;
			it.next = null;
			it = tmp;
		}
	}
}
