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
	String tree;

	public static void main(String [] args)
	{
		if(args.length == 0)
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

		}catch(FileNotFoundException e)
		{
			System.out.println("File: " + fileName + " not found. Exiting");			
		}

		next = tokenArray.get(0);

		parseS();

		//print parse tree to file
		//printToFile();
		System.out.println(tree);
		printToFile();
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

			// for(int i = 0; i < singleToken.length;i++)
			// 	System.out.println(singleToken[i]);

			String data = "";
			if(singleToken.length==3)
				data =  singleToken[2];


			tempList.add(new Token(Lexer.TokenType.valueOf(singleToken[1]),data));
		}

		sc.close();
		tempList.add(new Token(Lexer.TokenType.EOF, "$"));
		return tempList;		
	}

	public void printNode()
	{
		if(currentNode.parent!=null)
			tree+=currentNode.toString()+currentNode.parent.num+"\n";
		else
			tree+=currentNode.toString()+"\n";
	}

	public void printToFile()
	{
		try{
			PrintWriter writer = new PrintWriter("temp.txt");
			String output= "";

			ParseNode temp = currentNode;
			boolean cont = true;

			// while(temp.children.size()>1)
			// 		temp=temp.get(0);
			LinkedList<ParseNode> queue = new LinkedList<ParseNode>();
			queue.push(temp);
			while(!queue.isEmpty())
			{
				temp = queue.pop();
				output += temp.toString() + "\n";
				for(ParseNode n : temp.children)
				{
					queue.push(n);
				}
			}

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
			currentNode = currentNode.addChild(new ParseNode(NodeType.VAR,next.data));
			match(Lexer.TokenType.STR);
		}
		else if (next.type == Lexer.TokenType.VAR)
		{
			currentNode = currentNode.addChild(new ParseNode(NodeType.VAR,next.data));
			match(Lexer.TokenType.VAR);
		}
		else if(next.type == Lexer.TokenType.INT)
		{
			currentNode = currentNode.addChild(new ParseNode(NodeType.VAR,next.data));
			parseNUMEXPR();
		}
		else
		{
			System.out.println("Error: Cannot parse VAR with "+next.data);
			System.exit(0);
		}

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseAssign()
	{
		System.out.println("Parsing ASSIGN");

		currentNode = currentNode.addChild(new ParseNode(NodeType.ASSIGN,"="));
		parseVAR();
		match("=");

		if(next.type == Lexer.TokenType.STR)
		{
			//match("\"");
			match(Lexer.TokenType.STR);

			currentNode = currentNode.addChild(new ParseNode(NodeType.STR,tokenArray.get(currentPos-1).data));
			printNode();
			currentNode = currentNode.parent;
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

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseNUMEXPR()
	{
		System.out.println("Passing NUMEXPR with " +next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.NUMEXPR));

		if(next.type == Lexer.TokenType.VAR)
			parseVAR();			
		else if(next.type == Lexer.TokenType.INT)
			match(Lexer.TokenType.INT);
		else if(next.type == Lexer.TokenType.NUM_OP)
			parseCALC();//next is ADD,SUB or MULT
		
		printNode();
		currentNode = currentNode.parent;
	}

	public void parseCALC()
	{
		System.out.println("Parsing CALC with "+ next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.CALC));

		if(next.data.equals("add"))//which is next?
		{
			currentNode.data="+";
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
			currentNode.data="-";
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
			currentNode.data="*";
			match("mult");
			//next is a mult operation
			match("(");
			parseNUMEXPR();
			match(",");
			parseNUMEXPR();
			match(")");
		}

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseCOND_BRANCH()
	{
		System.out.println("Parsing COND_BRANCH with "+ next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.COND_BRANCH));

		if(next.data.equals("if"))
		{
			currentNode.data = "if";
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
				currentNode = currentNode.addChild(new ParseNode(NodeType.COND_BRANCH,"else"));
				match("else");
				match("{");
				parseCODE();
				match("}");
				currentNode = currentNode.parent;
			}
			
		}

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseBOOL()
	{
		System.out.println("Parsing BOOl with "+ next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.BOOL));

		if(next.data.equals("eq"))//operation
		{			
			currentNode.data = "eq";

			match("eq");
			match("(");
			parseVAR();
			match(",");
			parseVAR();
			match(")");
			
		}
		else if(next.data.equals("not"))//not operation
		{
			currentNode.data = "not";
			match("not");
			parseBOOL();
		}
		else if(next.data.equals("and"))//and operation
		{
			currentNode.data = "and";
			match("and");
			match("(");
			parseBOOL();
			match(",");
			parseBOOL();
			match(")");
		}
		else if(next.data.equals("or"))//or operation
		{
			currentNode.data = "or";
			match("or");
			match("(");
			parseBOOL();
			match(",");
			parseBOOL();
			match(")");
		}
		else if(next.data.equals("(")) //bool in brackets - expresion
		{
			match("(");
			parseVAR();
			if(next.data.equals("<"))
			{
				currentNode.data = "<";
				match("<");
				parseVAR();
			}
			else if(next.data.equals(">"))
			{
				currentNode.data = ">";
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
				currentNode.data = "<";
				match("<");
				parseVAR();
			}
			else if(next.data.equals(">"))
			{
				currentNode.data = ">";
				match(">");
				parseVAR();
			}
		}
		else if(next.type == Lexer.TokenType.TRUTH)
		{
			currentNode.data = next.data;
			match(Lexer.TokenType.TRUTH);
		}

		printNode();
		currentNode = currentNode.parent;
		
	}

	public void parseCONDLOOP()
	{
		System.out.println("Parsing CONDLOOP wiht "+ next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.CONDLOOP));

		if(next.data.equals("while"))//while loop
		{
			currentNode.data = "while";
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
			currentNode.data = "for";
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

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseS()
	{
		System.out.println("Starting parseS()");
		currentNode = new ParseNode(NodeType.START,"root");

		parsePROG();
		match(Lexer.TokenType.EOF);
		printNode();
	}

	public void parsePROG()
	{	
		System.out.println("Parsing PROG");
		currentNode = currentNode.addChild(new ParseNode(NodeType.PROG,""));

		parseCODE();
		if(next.data.equals(";"))
		{
			match(";");
			parsePROC_DEFS();
		}

		printNode();
		currentNode = currentNode.parent;
	}

	public void parsePROC_DEFS()
	{
		System.out.println("Parsing PROC_DEFS");

		currentNode = currentNode.addChild(new ParseNode(NodeType.PROC_DEFS));

		parsePROC();
		if(next.type == Lexer.TokenType.PROC)
			parsePROC_DEFS();

		printNode();
		currentNode = currentNode.parent;
	}

	public void parsePROC()
	{
		System.out.println("Parsing PROC");

		currentNode = currentNode.addChild(new ParseNode(NodeType.PROC));

		match("proc");
		currentNode.data = next.data;
		match(Lexer.TokenType.VAR);
		match("{");
		parsePROG();
		match("}");

		printNode();
		currentNode = currentNode.parent;	
	}

	public void parseCODE()
	{
		System.out.println("Parsing CODE with "+next.data);

		currentNode = currentNode.addChild(new ParseNode(NodeType.CODE));

		if(next.type == Lexer.TokenType.PROC)
			parsePROC_DEFS();
		else
		{
			parseINSTR();

			if(next.data.equals(";"))
			{
				match(";");
				if(!next.data.equals("}"))
					parseCODE();
			}
		}

		printNode();
		currentNode = currentNode.parent;		
	}

	public void parseINSTR()
	{
		System.out.println("Parsing INSTR with "+ next.type);

		currentNode = currentNode.addChild(new ParseNode(NodeType.INSTR));

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
				else if(next.data.equals("num") || next.data.equals("bool") || next.data.equals("string"))
					{
						parseTYPE();
					}
					else
						parseVAR();
					
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

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseIO()
	{
		System.out.println("Parsing IO");

		currentNode = currentNode.addChild(new ParseNode(NodeType.IO));

		if(next.data.equals("input"))
			currentNode.data = "input";
		else
			currentNode.data = "output";

		match(Lexer.TokenType.IO);
		match("(");
		parseVAR();
		match(")");

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseDECL()
	{
		System.out.println("Parsing DECL");

		currentNode = currentNode.addChild(new ParseNode(NodeType.DECL));

		parseTYPE();
		parseNAME();
		if(next.data.equals(";"))
		{
			match(";");
			parseDECL();
		}
		else
		{
			//error
		}

		printNode();
		currentNode = currentNode.parent;
	}

	public void parseNAME()
	{
		System.out.println("Parsing NAME");

		currentNode = currentNode.addChild(new ParseNode(NodeType.NAME,next.data));

		if(next.type == Lexer.TokenType.VAR)
		{
			//we have an explicit name - aka function name or what not
			//the diference is that a name doesnt hold a value
			//while a var holds a value
		}

		printNode();
		currentNode = currentNode.parent;
	}


	public void parseTYPE()
	{
		System.out.println("Parsing TYPE");

		currentNode = currentNode.addChild(new ParseNode(NodeType.TYPE,next.data));

		match(Lexer.TokenType.TYPE);

		printNode();
		currentNode = currentNode.parent;
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
