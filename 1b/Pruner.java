import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.util.Comparator;

public class Pruner
{
	public LinkedList<TempToken> tokenList; 
	public TempToken head;
	public static void main(String args[])
	{
		Pruner p = new Pruner();
		p.readFromFile(args[0]);
		p.startPrune();
		p.print("pruned.txt");
	}

	public Pruner()
	{
		tokenList = new LinkedList<TempToken>();
		head = null;
	}

	void readFromFile(String filename)
	{
		try {
			Scanner sc = new Scanner(new File(filename));
			sc.useDelimiter("\n");

			String line = "";
			while(sc.hasNext())
			{
				line = sc.next();
				String temp[] = line.split("\\|");
				TempToken tok = new TempToken(Integer.parseInt(temp[0]), NodeType.valueOf(temp[1]), temp[2]);
				for(int i = 3; i < temp.length; i++)
				{
					tok.childrenIds.add(Integer.parseInt(temp[i]));
				}
				tokenList.add(tok);
			}
			sc.close();
			buildTree();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Error: File " + filename + " was not found!");
			System.exit(1);
		}
	}


	void buildTree()
	{
		head = tokenList.get(0);
		for(TempToken t : tokenList)
		{
			for(int s : t.childrenIds)
			{
				for(TempToken n : tokenList)
				{
					if(s == n.id)
					{
						t.children.add(n);
						n.parent = t;
						break;
					}
				}
			}
		}
	}

	void startPrune()
	{
		prune(head);	
	}

	boolean isTerminal(TempToken n)
	{
		return n.type == NodeType.TERMINAL;
	}

	void prune(TempToken n)
	{
		LinkedList<TempToken> toRemove = new LinkedList<TempToken>();
		if(n.children.size() == 1 && isTerminal(n.children.get(0)))
		{
			n.data = n.children.get(0).data;
			n.children.remove(0);
			return;
		}
		for(TempToken t : n.children)
		{
			if(t.children.size() == 1 && !isTerminal(t.children.get(0)) && t.type != NodeType.IO && !t.data.equals("not"))
			{
				toRemove.add(t);
				System.out.println("Removing node " + t.id + " with type " + t.type);
			}
			else if(n.type == NodeType.CODE && t.type == NodeType.CODE)
			{
				prune(t);
				toRemove.add(t);
			}
			
			else if(isTerminal(t))
			{
				if(t.data.equals(";") || t.data.equals("{") || t.data.equals("}") || t.data.equals("(") || t.data.equals(")")
						|| t.data.equals("else") || t.data.equals("else") || t.data.equals("if")
						|| t.data.equals("while") || t.data.equals("for") || t.data.equals("not") || t.data.equals("and")
						|| t.data.equals("or") || t.data.equals("add") || t.data.equals("sub") || t.data.equals("mult")
						|| t.data.equals("<") || t.data.equals(">") || t.data.equals("eq") || t.data.equals(",")
						|| t.data.equals("output") || t.data.equals("input") || t.data.equals("proc") || t.data.equals("=")
						|| t.data.equals("$") || t.data.equals("then"))
				{
					toRemove.add(t);
					System.out.println("Removing terminal " + t.id + " with data " + t.data);
				}
			}
		}
		for(TempToken t : toRemove)
		{
			n.children.remove(t);
			for(TempToken m : t.children)
			{
				n.children.add(m);
				m.parent = n;
			}
		}

		for(TempToken t : n.children)
			prune(t);
		n.children.sort(new Comparator<TempToken>() {
			@Override
			public int compare(TempToken o1, TempToken o2)
			{
				return Integer.compare(o1.id, o2.id);
			}
		});
	}

	void print(String filename)
	{
		try{
			PrintWriter writer = new PrintWriter(filename);
			LinkedList<TempToken> queue = new LinkedList<TempToken>();
			queue.add(head);

			while(!queue.isEmpty())
			{
				TempToken X = queue.remove();
				writer.write(X.toString() + "\n");
				for(TempToken t : X.children)
					queue.add(t);
			}

			writer.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Error " + filename + " was not found");
			System.exit(1);
		}
		printTreeDFS(head, 0);
	}

	void printTreeDFS(TempToken n, int depth)
	{
		String out = "";
		for(int i = 0; i < depth; i++) out += "  ";
		out += n.type + "|" + n.data;
		System.out.println(out);
		for(TempToken t : n.children)
			printTreeDFS(t, depth+1);
	}

	public class TempToken
	{
		int id;
		NodeType type;
		String data;
		LinkedList<Integer> childrenIds;
		LinkedList<TempToken> children;
		TempToken parent;
		public TempToken(int id, NodeType type, String data)
		{
			this.id = id;
			this.type = type;
			this.data = data;
			this.childrenIds = new LinkedList<Integer>();
			this.children = new LinkedList<TempToken>();
			this.parent = null;
		}
		@Override
		public String toString()
		{
			String out = this.id + "|" + this.type + "|" + this.data;
			for(TempToken t : this.children)
				out += "|" + t.id;
			return out;
		}
	}

	public class Token
	{
		String id;
		Lexer.TokenType type;
	}
}
