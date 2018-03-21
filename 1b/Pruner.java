import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class Pruner
{
	public LinkedList<TempToken> tokenList; 
	public ParseNode head;
	public static void main(String args[])
	{
		//TODO: Implement main
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
				TempToken tok = new TempToken(temp[0], Lexer.TokenType.valueOf(temp[1]), temp[2]);
				for(int i = 3; i < temp.length; i++)
				{
					tok.childrenIds.add(temp[i]);
				}
				tokenList.add(tok);
			}
			sc.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Error: File " + filename + " was not found!");
			System.exit(1);
		}
	}


	void buildTree()
	{
		for(TempToken t : tokenList)
		{

		}
	}

	public class TempToken
	{
		String id;
		Lexer.TokenType type;
		String data;
		LinkedList<String> childrenIds;
		public TempToken(String id, Lexer.TokenType type, String data)
		{
			this.id = id;
			this.type = type;
			this.data = data;
			this.childrenIds = new LinkedList<String>();
		}
	}

	public class Token
	{
		String id;
		Lexer.TokenType type;
	}
}
