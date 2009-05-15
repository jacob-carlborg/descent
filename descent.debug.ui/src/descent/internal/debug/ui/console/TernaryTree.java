package descent.internal.debug.ui.console;

import java.util.Iterator;
import java.util.Stack;

/* package */ final class TernaryTree<T> implements Iterable<T>
{
	// Note: this class ported form D, which is why the syntax is so weird/non-Java-ish
	
	private final static class TreeNode<T>
	{
		TreeNode<T> left;
		TreeNode<T> right;
	    TreeNode<T> mid;
		char split;
		String key;
		T value;
	}
	
	private final class TreeIterator implements Iterator<T>
	{
		private Stack<TreeNode<T>> stack;
		
		public TreeIterator(TreeNode<T> start)
		{
			stack = new Stack<TreeNode<T>>();
			if(null != start)
				stack.add(start);
		}

		public boolean hasNext()
		{
			return !stack.isEmpty();
		}

		public T next()
		{
			TreeNode<T> node = stack.pop();
			while(true)
			{
				if(null != node.right)   stack.push(node.right);
				if(null != node.mid)     stack.push(node.mid);
				if(null != node.left)    stack.push(node.left);
				
				if(null != node.key)
					return node.value;
				else if(stack.isEmpty())
					throw new IllegalStateException(); // There should never be a null leaf
				
				node = stack.pop();
			}
		}

		public void remove()
		{
			throw new RuntimeException();
		}
	}
	
	private final class SearchResult implements Iterable<T>
	{
		private final TreeNode<T> searchRoot;
		
		public SearchResult(TreeNode<T> searchRoot)
		{
			this.searchRoot = searchRoot;
		}
		
		public Iterator<T> iterator()
		{
			return new TreeIterator(searchRoot);
		}
	}
	
	private TreeNode<T> root;
    
	public void add(String sk, T v)
	{
		char[] k = sk.toCharArray();
		if(k.length == 0)
			return;
		
		TreeNode<T> node = root;
		int i = 0;
		int len = k.length;
		
		if(null == node)
		{
			root = new TreeNode<T>();
			root.split = k[i];
			node = root;
			i++;
			insertRest(i, len, k, sk, v, node);
			return;
		}
		
		while(i < len)
		{
			char e = k[i];
			char ne = node.split;
			
			if(e < ne)
			{
				if(null != node.left)
				{
					node = node.left;
					continue;
				}
				else
				{
					node = node.left = new TreeNode<T>();
					node.split = e;
					i++;
					insertRest(i, len, k, sk, v, node);
					return;
				}
			}
			else if(e > ne)
			{
				if(null != node.right)
				{
					node = node.right;
					continue;
				}
				else
				{
					node = node.right = new TreeNode<T>();
					node.split = e;
					i++;
					insertRest(i, len, k, sk, v, node);
					return;
				}
			}
			
			i++;
			if(i < len)
			{
				if(null != node.mid)
				{
					node = node.mid;
					continue;
				}
				else
				{
					insertRest(i, len, k, sk, v, node);
					return;
				}
			}
			else
			{
				node.value = v;
				node.key = sk;
				return;
			}
		}
	}
	
	private void insertRest(int i, int len, char[] k, String sk, T v, TreeNode<T> node)
	{
		while(i < len)
		{
			node = node.mid = new TreeNode<T>();
			node.split = k[i];
			i++;
		}
		node.value = v;
		node.key = sk;
		return;
	}
	
	public T get(String k)
	{
		TreeNode<T> node = search(k);
		if(null != node && null != node.key)
			return node.value;
		return null;
	}
	
	public Iterable<T> prefixSearch(String k)
	{
		return new SearchResult(search(k));
	}
	
	public Iterator<T> iterator()
	{
		return new TreeIterator(root);
	}
	
	private TreeNode<T> search(String k)
	{
		if(0 == k.length())
			return null;
		
		TreeNode<T> node = root;
		while(null != node)
		{
			char e = k.charAt(0);
			if(e < node.split)
			{
				node = node.left;
				continue;
			}
			else if(e > node.split)
			{
				node = node.right;
				continue;
			}
			else
			{
				k = k.substring(1);
				if(k.length() == 0)
				{
					return node;
				}
				else
				{
					node = node.mid;
					continue;
				}
			}
		}
		return null;
	}
}
