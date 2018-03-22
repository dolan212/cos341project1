import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;

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
				TempToken tok = new TempToken(temp[0], NodeType.valueOf(temp[1]), temp[2]);
				for(int i = 3; i < temp.length; i++)
				{
					tok.childrenIds.push(temp[i]);
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
			for(String s : t.childrenIds)
			{
				for(TempToken n : tokenList)
				{
					if(s.equals(n.id))
					{
						t.children.push(n);
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
		for(TempToken t : n.children)
		{
			if(t.children.size() == 1 && !isTerminal(t.children.get(0)))
			{
				toRemove.add(t);
				System.out.println("Removing node " + t.id + " with type " + t.type);
			}
			
			else if(isTerminal(t))
			{
				if(t.data.equals(";") || t.data.equals("{") || t.data.equals("}") || t.data.equals("(") || t.data.equals(")")
						|| t.data.equals("else") || t.data.equals("then") || t.data.equals("else") || t.data.equals("if")
						|| t.data.equals("while") || t.data.equals("for") || t.data.equals("not") || t.data.equals("and")
						|| t.data.equals("or") || t.data.equals("add") || t.data.equals("sub") || t.data.equals("mult")
						|| t.data.equals("<") || t.data.equals(">") || t.data.equals("eq") || t.data.equals(",")
						|| t.data.equals("output") || t.data.equals("input") || t.data.equals("proc") || t.data.equals("=")
						|| t.data.equals("$"))
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
				n.children.push(m);
				m.parent = n;
			}
		}
		
		for(TempToken t : n.children)
			prune(t);
	}

	void print(String filename)
	{
		try{
			PrintWriter writer = new PrintWriter(filename);
			LinkedList<TempToken> queue = new LinkedList<TempToken>();
			queue.push(head);

			while(!queue.isEmpty())
			{
				TempToken X = queue.pop();
				writer.write(X.toString() + "\n");
				for(TempToken t : X.children)
					queue.push(t);
			}

			writer.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Error " + filename + " was not found");
			System.exit(1);
		}
	}

	public class TempToken
	{
		String id;
		NodeType type;
		String data;
		LinkedList<String> childrenIds;
		LinkedList<TempToken> children;
		TempToken parent;
		public TempToken(String id, NodeType type, String data)
		{
			this.id = id;
			this.type = type;
			this.data = data;
			this.childrenIds = new LinkedList<String>();
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
