import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Lexer
{
	Scanner sc;
	String input;
	char[] srcArray;
	int currentPosition = 0;
	int wordBeginIndex = 0;
	LinkedList<Token> tokenList;
	char current = ' ';

	public enum TokenType {
		VAR(0), INT(1), STRUC(2) ,COMP(3), BOOL(4), NUM_OP(5), 
		STR(6), GROUP(7), ASSIGN(8), IO(9), HALT(10), TYPE(11), 
		PROC(12), INVALID(-1);

		public int type;
		TokenType(int type)
		{
			this.type = type;
		}

		int getVal(){return type;}
	}

	public Lexer(String path)
	{
		try{
			sc = new Scanner(new File(path));			
		}catch(FileNotFoundException e)
		{
			
		}

		tokenList = new LinkedList<Token>();

		if(sc.hasNext())
			input = sc.nextLine();
		else
			//no file found
		
		while(sc.hasNext())
			input += "\n" + sc.nextLine();

		srcArray = input.toCharArray();
		System.out.println("Array length: "+ srcArray.length);

		read();
	}

	public void read()
	{
		while(currentPosition < srcArray.length)
		{
			current = srcArray[currentPosition];

			System.out.println("read: "+ current + " at " + currentPosition);

			//check if current char is a letter
			if(current >= 'a' && current <= 'z')
			{
				// System.out.println("Processing Letter");
				processLetter();
			}
			else if(current == ' ' || current == '\n')
			{
				//current is a seperator symbol
				// System.out.println("Skipping space or endline");
				currentPosition++;
				continue;

			}
			else if((current >= '1' && current <= '9') || current == '-')
			{
				//process input as number
				processNumber();
			}
			else
			{
				switch(current)
				{
					case '\"':
						//String symbol
						parseString();
						break;
					case '<':
						//comparison symbol
						tokenList.add(new Token(TokenType.COMP,"<"));
						break;
					case '>':
						//comparison symbol
						tokenList.add(new Token(TokenType.COMP,">"));
						break;
					case '(':
						//circle bracket - grouping
						tokenList.add(new Token(TokenType.GROUP,"("));
						break;
					case ')':
						//circle bracket - grouping
						tokenList.add(new Token(TokenType.GROUP,")"));
						break;
					case '{':
						//curly bracket - grouping
						tokenList.add(new Token(TokenType.GROUP,"{"));
						break;
					case '}':
						//curly bracket - grouping
						tokenList.add(new Token(TokenType.GROUP,"}"));
						break;
					case ',':
						//comma - grouping
						tokenList.add(new Token(TokenType.GROUP,","));
						break;
					case ';':
						//semi-colon - grouping
						tokenList.add(new Token(TokenType.GROUP,";"));
						break;
					case '=':
						tokenList.add(new Token(TokenType.ASSIGN,"("));
						//assignment operator
						break;
				}

				currentPosition++;
			}			
		}
	}

	public void processLetter()
	{		
		//check if possible for word to be a reserved word
		if( current == 'c' || current == 'd' || current == 'g' ||
			current == 'j' || current == 'k' || current == 'l' ||
			current == 'm' || current == 'q' ||	current == 'r' || 
			current == 'u' || current == 'v' ||	current == 'x' ||
			current == 'y' || current == 'z')
		{
			//word cannot be a reserved word
			//therefor we assume variable until spacer or invalid (not allowed)
			// System.out.println("Processing as variable");
			processVariable();
		}
		else
		{	
			//keep track of read characters - can be replaced with a counter as in processVariable()
			String word = current+"";
			char next;
			Token temp;

			//remember where word begins
			wordBeginIndex = currentPosition++;

			//while next char is valid
			while(currentPosition<srcArray.length)
			{
				next = srcArray[currentPosition];

				if((next >= 'a' && next <= 'z') || (next >= '0' && next <= '9'))
				{					
					word += next;
					currentPosition++;

					//check if word is a keyword
					temp = isKeyWord(word);
					if(temp!=null)
					{
						tokenList.add(temp);
						return;
					}
				}
				else
					break;				
			}

			//add variable token
			tokenList.add(new Token(TokenType.VAR,word));
		}
	}

	public void processNumber()
	{
		char next = current;
		String num = "";

		//working with a negative int
		if(current == '-')
		{
			//add negative symbol
			num = current+"";

			next = srcArray[currentPosition++];
		}
		
		//first number must be from 1-9
		if(next >= '1' && next <= '9')
		{
			num += next;					

			//continue
			while(currentPosition<srcArray.length)
			{
				next = srcArray[currentPosition++];

				if(next >= '0' && next <= '9')
					num+= next;
				else
					break;
			}
			
			//add int num to token - leave as string as it is written to file anyway
			tokenList.add(new Token(TokenType.INT,num));
		}
		else
		{
			//throw numeric error
			return;
		}
	}

	//demonstrates a possible method of avoiding explicit memory
	//aka no String variable kept
	public void processVariable()
	{
		wordBeginIndex = currentPosition++;
		char c;

		//loop until end of word
		//check not out of bounds
		while(currentPosition<srcArray.length)
		{
			c = srcArray[currentPosition]; //get next char

			if((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))
				currentPosition++;//move on
			else
				break; //exit while loop
		}

		//construct a new token with a string made from src array
		//this prevents added memory
		//add token to linked list
		tokenList.add(new Token(TokenType.VAR,new String(srcArray,wordBeginIndex,currentPosition-wordBeginIndex)));
	}

	public void parseString()
	{
		char next;
		String temp ="";
		int strLength = 0;
		currentPosition++;
		
		//while we are in bounds and string is not greater than 8 chars long
		while(currentPosition < srcArray.length && strLength <= 8)
		{
			next = srcArray[currentPosition++];
			strLength++;

			if(next == '\"')
				break;//exit loop - reached end of string

			temp+=next; //add char to string
		}

		//add String token to list
		tokenList.add(new Token(TokenType.STR,temp));
	}

	public Token isKeyWord(String word)
	{
		//check for keywords by length
		switch(word.length())
		{
			case 2:// eq || or || if
				if(word.equals("eq"))
					return new Token(TokenType.COMP,"eq");
				else if(word.equals("if"))
					return new Token(TokenType.STRUC,"if");
				else if(word.equals("or"))
					return new Token(TokenType.BOOL,"or");
				break;

			case 3://and || not || add || sub || for || num
				if(word.equals("and"))
					return new Token(TokenType.BOOL,"and");
				else if(word.equals("not"))
					return new Token(TokenType.BOOL,"not");
				else if(word.equals("add"))
					return new Token(TokenType.NUM_OP,"add");
				else if(word.equals("sub"))
					return new Token(TokenType.NUM_OP,"sub");
				else if(word.equals("for"))
					return new Token(TokenType.STRUC,"for");
				else if(word.equals("num"))
					return new Token(TokenType.TYPE,"num");
				break;

			case 4:
				if(word.equals("mult"))
					return new Token(TokenType.NUM_OP,"mult");
				else if(word.equals("then"))
					return new Token(TokenType.STRUC,"then");
				else if(word.equals("else"))
					return new Token(TokenType.STRUC,"else");
				else if(word.equals("halt"))
					return new Token(TokenType.HALT,"halt");
				else if(word.equals("bool"))
					return new Token(TokenType.TYPE,"bool");
				else if(word.equals("proc"))
					return new Token(TokenType.PROC,"proc");
				break;

			case 5://while || input
				if(word.equals("while"))
					return new Token(TokenType.STRUC,"while");
				else if(word.equals("input"))
					return new Token(TokenType.IO,"in");
				break;

			case 6: //string || output
				if(word.equals("string"))
					return new Token(TokenType.TYPE,"str"); //'string' type
				else if(word.equals("output"));
					return new Token(TokenType.IO,"out");
		}

		return null; //not a keyword
	}
}
