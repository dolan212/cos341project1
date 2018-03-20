import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.io.PrintWriter;
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
		p.parseS();
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
		match(Lexer.TokenType.VAR);
	}

	public void parseAssign()
	{
		parseVAR();
		match("=");

		if(next.type == Lexer.TokenType.STR)
		{
			match(Lexer.TokenType.STR);
		}
		else if(next.type == Lexer.TokenType.VAR)
		{
			parseVAR();
		}
		else if(next.type == Lexer.TokenType.INT || next.type == Lexer.TokenType.NUM_OP)
			parseNUMEXPR();
		else
			parseBOOL();
	}

	public void parseNUMEXPR()
	{
		if(next.type == Lexer.TokenType.VAR)
			parseVAR();			
		else if(next.type == Lexer.TokenType.INT)
			match(Lexer.TokenType.INT);
		else if(next.type == Lexer.TokenType.NUM_OP)
			parseCALC();//next is ADD,SUB or MULT
		
	}

	public void parseCALC()
	{
		if(next.data.equals("add"))//which is next?
		{
			match("add");
			//next is an add operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
		else if(next.data.equals("sub"))
		{
			match("sub");
			//next is a sub operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
		else if(next.data.equals("mult"))
		{
			match("mult");
			//next is a mult operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}
	}

	public void parseCOND_BRANCH()
	{
		if(next.data.equals("if"))
		{
			match("if");
			match("(");//next+=2
			parseBOOL();
			match(")");
			match("then");
			match("{");
			parseCODE();
			match("}");

			if(next.data.equals("else"))
			{
				match("else");
				match("{");
				parseCODE();
				match("}");
			}
			
		}
	}

	public void parseBOOL()
	{
		if(next.type == Lexer.TokenType.BOOL)//operation
		{
			if(next.data.equals("eq"))//equate operation
			{
				match("eq");
				match("(");
				parseVAR();
				match(",");
				parseVAR();
				match(")");
			}
			else if(next.data.equals("not"))//not operation
			{
				match("not");
				parseBOOL();
			}
			else if(next.data.equals("and"))//and operation
			{
				match("and");
				match("(");
				parseBOOL();
				match(",");
				parseBOOL();
				match(")");
			}
			else//or operation
			{
				match("or");
				match("(");
				parseBOOL();
				match(",");
				parseBOOL();
				match(")");
			}
		}
		else if(next.type == Lexer.TokenType.VAR)
		{
			parseVAR();
			if(next.data.equals("<"))
			{
				match("<");
				parseVAR();
			}
			else if(next.data.equals(">"))
			{
				match(">");
				parseVAR();
			}
		}
		else if(next.type == Lexer.TokenType.TRUTH)
		{
			match(Lexer.TokenType.TRUTH);
		}
		
	}

	public void parseCONDLOOP()
	{
		if(next.data.equals("while"))//while loop
		{
			match("while");
			match("(");
			parseBOOL();
			match(")");
			match("{");
			parseCODE();
			match("}");			
		}
		else if(next.data.equals("for")) //for loop
		{
			match("for");
			//initial var setup
			match("(");
			parseVAR();
			match("=");
			match("0");
			match(";");

			//condition
			parseVAR();
			match("<");	//less than expression
			parseVAR();
			match(";");

			//increment
			parseVAR();
			match("=");	//assignment
			match("add");	//add expression
			match("(");
			parseVAR();	
			match(",");
			match("1");	//NUM 1
			match(")");
			match(")");//close for

			//body
			match("{");
			parseCODE();
			match("}");
		}
	}

	public void parseS()
	{
		parsePROG();
		match(Lexer.TokenType.EOF);
	}

	public void parsePROG()
	{
		parseCODE();
		if(next.data.equals(";"))
		{
			match(";");
			parsePROC_DEFS();
		}
	}

	public void parsePROC_DEFS()
	{
		parsePROC();
		if(next.type == Lexer.TokenType.PROC)
			parsePROC_DEFS();
	}

	public void parsePROC()
	{
		match("proc");
		match(Lexer.TokenType.VAR);
		match("{");
		parsePROG();
		match("}");
	}

	public void parseCODE()
	{
		parseINSTR();
		if(next.data.equals(";"))
		{
			match(";");
			parseCODE();
		}
	}

	public void parseINSTR()
	{
		switch(next.type)
		{
			case HALT:
				match(Lexer.TokenType.HALT);
				break;
			case TYPE:
				parseDECL();
				break;
			case IO:
				parseIO();
				break;
			case VAR:
				if(tokenArray.get(currentPos + 1).data.equals("="))
					parseAssign();
				else parseVAR();
				break;
			case STRUC:
				if(next.data.equals("if"))
					parseCOND_BRANCH();
				else parseCONDLOOP();
				break;
			default:
				break;

		}
	}

	public void parseIO()
	{
		match(Lexer.TokenType.IO);
		match("(");
		parseVAR();
		match(")");
	}

	public void parseDECL()
	{
		parseTYPE();
		parseVAR();
		if(next.data.equals(";"))
		{
			match(";");
			parseDECL();
		}
	}

	public void parseTYPE()
	{
		match(Lexer.TokenType.TYPE);
	}

	public void check(String input)
	{
		match(input);
		next = tokenArray.get(--currentPos);
	}

	public void match(Lexer.TokenType type)
	{
		if(next.type != type)
		{
			System.out.println("Syntax Error: Unable to match token type " + type.toString() + " at position " + currentPos);
			System.exit(1);
		}	
		next = tokenArray.get(++currentPos);
	}
	public void match(String input)
	{
		if(!next.data.equals(input))
		{
			System.out.println("Syntax Error: Unable to match token " + input + " at position " + currentPos);
			System.exit(1);
		}
		next = tokenArray.get(++currentPos);
	}
}
