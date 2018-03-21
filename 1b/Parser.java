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
	ParseNode currentNode;

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
		tempList.add(new Token(Lexer.TokenType.EOF, "$"));
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
		//@todo check what type of variable var is
		if(next.type == Lexer.TokenType.STR)
		{
			//var is string
			match(Lexer.TokenType.STR);
		}
		else if (next.type == Lexer.TokenType.VAR)
			match(Lexer.TokenType.VAR);
		else if(next.type == Lexer.TokenType.INT)
			parseNUMEXPR();
		else
		{
			//error
		}
	}

	public void parseAssign()
	{
		System.out.println("Parsing ASSIGN");
		parseVAR();
		match("=");

		if(next.type == Lexer.TokenType.STR)
		{
			//match("\"");
			match(Lexer.TokenType.STR);
			//match("\"");
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
		System.out.println("Passing NUMEXPR with " +next.data);
		if(next.type == Lexer.TokenType.VAR)
			parseVAR();			
		else if(next.type == Lexer.TokenType.INT)
			match(Lexer.TokenType.INT);
		else if(next.type == Lexer.TokenType.NUM_OP)
			parseCALC();//next is ADD,SUB or MULT
		
	}

	public void parseCALC()
	{
		System.out.println("Parsing CALC with "+ next.data);
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
		System.out.println("Parsing COND_BRANCH with "+ next.data);
		if(next.data.equals("if"))
		{
			match("if");
			match("(");
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
		System.out.println("Parsing BOOl with "+ next.data);

		if(next.data.equals("eq"))//operation
		{
			
			System.out.println("eq");
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
		else if(next.data.equals("or"))//or operation
		{
			match("or");
			match("(");
			parseBOOL();
			match(",");
			parseBOOL();
			match(")");
		}
		else if(next.data.equals("(")) //bool in brackets
		{
			match("(");
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
			match(")");

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
		System.out.println("Parsing CONDLOOP wiht "+ next.data);
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
		System.out.println("Starting parseS()");

		parsePROG();
		match(Lexer.TokenType.EOF);
	}

	public void parsePROG()
	{	
		System.out.println("Parsing PROG");
		currentNode = new ParseNode(Lexer.TokenType.PROG);
		parseCODE();
		if(next.data.equals(";"))
		{
			match(";");
			parsePROC_DEFS();
		}
	}

	public void parsePROC_DEFS()
	{
		System.out.println("Parsing PROC_DEFS");

		parsePROC();
		if(next.type == Lexer.TokenType.PROC)
			parsePROC_DEFS();
	}

	public void parsePROC()
	{
		System.out.println("Parsing PROC");

		match("proc");
		match(Lexer.TokenType.VAR);
		match("{");
		parsePROG();
		match("}");
	}

	public void parseCODE()
	{
		System.out.println("Parsing CODE with "+next.data);

		if(next.type == Lexer.TokenType.PROC)
			parsePROC_DEFS();
		else
		{
			parseINSTR();
			
			if(next.data.equals(";"))
			{
				match(";");
				parseCODE();
			}
		}

		
	}

	public void parseINSTR()
	{
		System.out.println("Parsing INSTR with "+ next.type);

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
				System.out.println("Error: Cannot parse INSTR with "+ next.data);
				System.exit(0);
				break;

		}
	}

	public void parseIO()
	{
		System.out.println("Parsing IO");

		match(Lexer.TokenType.IO);
		match("(");
		parseVAR();
		match(")");
	}

	public void parseDECL()
	{
		System.out.println("Parsing DECL");

		parseTYPE();
		parseNAME();
		if(next.data.equals(";"))
		{
			match(";");
			parseDECL();
		}
		else
		{
			//new variable of type parseType and name parseName
		}
	}

	public void parseNAME()
	{
		System.out.println("Parsing NAME");

		if(next.type == Lexer.TokenType.VAR)
		{
			//we have an explicit name - aka function name or what not
			//the diference is that a name doesnt hold a value
			//while a var holds a value
		}
	}


	public void parseTYPE()
	{
		System.out.println("Parsing TYPE");

		match(Lexer.TokenType.TYPE);
	}

	public void match(Lexer.TokenType type)
	{
		System.out.println("Matching "+ type);
		if(next.type != type)
		{
			System.out.println("Syntax Error: Unable to match token type " + type.toString() + " at position " + currentPos);
			System.out.println("Found " + next.type.toString() + " instead");
			System.exit(1);
		}	
		next = tokenArray.get(++currentPos);
	}
	public void match(String input)
	{
		System.out.println("Matching "+input);
		if(!next.data.equals(input))
		{
			System.out.println("Syntax Error: Unable to match token " + input + " at position " + currentPos);
			System.out.println("Found " + next.data + " instead");
			System.exit(1);
		}
		next = tokenArray.get(++currentPos);
	}
}
