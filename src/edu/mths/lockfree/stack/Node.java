package edu.mths.lockfree.stack;

/**
 * Helper class for the stack.
 * 
 * @author Mathias Söderberg
 * @param <T>
 *            generic value for node(s).
 * 
 */
public class Node<T> {
	// value of node
	protected T value;
	// pointer to next node
	protected Node<T> next;

	/**
	 * Default constructor with <code>value</code> equals <code>null</code>.
	 */
	public Node() {
		this(null);
	}

	/**
	 * Constructor which takes a value and initializes this as the value of
	 * node.
	 * 
	 * @param value
	 *            new value for the node
	 */
	public Node(T value) {
		this.value = value;
		this.next = null;
	}
}
