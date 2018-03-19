import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.lang.Enum;
public class Parser
{	

	String fileString;
	LinkedList<Token> tokenArray;
	int currentPos;
	Token next;

	public static void main(String [] args)
	{
		if(args[0]==null)
		{
			System.out.println("No File Specified");
			System.exit(0);
		}

		Parser p = new Parser(args[0]);
	}

	public Parser(String fileName)
	{
		currentPos = 0;
		Scanner sc;
		try{
			sc = new Scanner(new File(fileName));			

			fileString = sc.useDelimiter("\\Z").next(); //scan entire file

		tokenArray = getTokens(fileString);

		printToFile();
		}catch(FileNotFoundException e)
		{
			System.out.println("File: " + fileName + " not found. Exiting");			
		}

		next = tokenArray.get(0);	
	}
	
	public LinkedList<Token> getTokens(String tokenString)
	{
		LinkedList<Token> tempList = new LinkedList<Token>();

		Scanner sc = new Scanner(tokenString);
		int tokenNum = 1;
		String temp ="";
		String [] singleToken;


		while(sc.hasNext())
		{
			temp = sc.nextLine();
			singleToken = temp.split("\\|");

			for(int i = 0; i < singleToken.length;i++)
				System.out.println(singleToken[i]);

			String data = "";
			if(singleToken.length==3)
				data =  singleToken[2];


			tempList.add(new Token(Lexer.TokenType.valueOf(singleToken[1]),data));
		}

		sc.close();
		return tempList;		
	}

	public void printToFile()
	{
		try{
			PrintWriter writer = new PrintWriter("temp.txt");
			String output= "";
			for(int i = 0; i < tokenArray.size(); i ++)
				output+=tokenArray.get(i).toString() + "\n";

			writer.write(output);

			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

	public void parseVAR()
	{
		if()
			return;
		else
		{
			//throw error
		}
	}

	public void parseAssign()
	{
		if(next.type == Lexer.Token.VAR)
		{
			parseVAR();
			match("=");

			if(next.type == Lexer.TokenType.STR)
			{
				//var is string
			}
			else if(next.type == Lexer.TokenType.VAR)
			{
				//var is var
				parseVAR();
			}
			else if(next.type == Lexer.TokenType.INT || next.type == Lexer.TokenType.NUM_OP)
				parseNUMEXPR();
			else
				parseBOOL();
		}
		else
		{
			//throw error
		}
	}

	public void parseNUMEXPR()
	{
		if(next.type == Lexer.TokenType.VAR)
		{
			parseVAR();			
		}
		else if(next.type == Lexer.TokenType.INT)
			parseNUM();
		else if(next.type == Lexer.TokenType.NUM_OP)
			parseCALC();//next is ADD,SUB or MULT
		else
		{
			//throw error
		}
	}

	public void parseCALC()
	{
		if(next.data.equals("add"))//which is next?
		{
			//next is an add operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
		else if(next.data.equals("sub"))
		{
			//next is a sub operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
		else if(next.data.equals("mult"))
		{
			//next is a mult operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
		else
		{
			//throw error
		}
	}

	public void parseCOND_BRANCH()
	{
		if(next.data.equals("if"))
		{
			match("(");//next+=2
			parseBOOL();
			match(")");
			next=tokenArray.get(--currentPos);//next needs to go back
			check("then");
			match("{");
			parseCODE();
			match("}");

			if(next.data.equals("else"))
			{
				match("{");
				parseCODE();
				match("}");
			}
			
		}
		else
		{
			//throw error
		}
	}

	public parseBOOL()
	{
		if(next.type == Lexer.TokenType.BOOL)//operation
		{
			if(next.data.equals("eq"))//equate operation
			{
				match("(");
				parseVAR();
				match(",");
				parseVAR();
				match(")");
			}
			else if(next.data.equals("not"))//not operation
			{
				next=tokenArray.get(++currentPos);
				parseBOOL();
			}
			else if(next.data.equals("and"))//and operation
			{
				match("(");
				parseBOOL();
				match(",");
				parseBOOL();
				match(")");
			}
			else//or operation
			{
				match("(");
				parseBOOL();
				match(",");
				parseBOOL();
				match(")");
			}
		}
		else if(next.data.equals("("))//expression
		{
			next=tokenArray.get(++currentPos);
			parseVAR();
			next=tokenArray.get(++counterPos);

			if(next.data.equals("<"))//less than expression
			{
				next=tokenArray.get(++counterPos);
				parseVAR();
				match(")");
			}
			else if(next.data.equals(">"))//greater than expression
			{
				next=tokenArray.get(++counterPos);
				parseVAR();
				match(")");
			}
			else
			{
				//throw error
			}
		}
		else if(next.data.equals("TRUE"))
		{
				//true
		}
		else if(next.data.equals("FALSE"));
		{
				//false
		}
		else if(next.type == Lexer.TokenType.VAR)
		{
			//throw errror
		}
	}

	public void parseCONDLOOP()
	{
		if(next.data.equals("while"))//while loop
		{
			match("(");
			parseBOOL();
			check(")");
			match("{");
			parseCODE();
			match("}");			
		}
		else if(next.data.equals("for")) //for loop
		{
			//initial var setup
			match("(");
			parseVAR();
			check("=");
			check("0");
			match(";");

			//condition
			parseVAR();
			match("<");	//less than expression
			parseVAR();
			match(";");

			//increment
			parseVAR();
			check("=");	//assignment
			check("add");	//add expression
			match("(");
			parseVAR();	
			check(",");
			check("1");	//NUM 1
			check(")");
			check(")");//close for

			//body
			match("{");
			parseCODE();
			match("}");
		}
		else
		{
			//throw error
		}
	}

	public void check(String input)
	{
		match(input);
		next = tokenArray.get(--currentPos);
	}

	public void match(Lexer.TokenType type)
	{
		next = tokenArray.get(++currentPos);
		if(next.type != type)
		{
			System.out.println("Syntax Error: Unable to match token type " + type.toString() + " at position " + currentPos);
			System.exit(1);
		}	
		next = tokenArray.get(++currentPos);
	}
	public void match(String input)
	{
		next = tokenArray.get(++currentPos);
		if(!next.data.equals(input))
		{
			System.out.println("Syntax Error: Unable to match token " + input + " at position " + currentPos);
			System.exit(1);
		}
		next = tokenArray.get(++currentPos);
	}
}
