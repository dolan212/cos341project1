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
