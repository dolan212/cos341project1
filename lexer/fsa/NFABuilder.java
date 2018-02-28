package lexer.fsa;
import java.util.Stack;

public class NFABuilder
{
	public static State buildNFA(String regex)
	{

		return null;	
	}
	public static String convertToRPN(String input)
	{
		String output = "";
		Stack<Character> stck = new Stack<Character>();
		for(int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			if(c == '.' || c == '|' || c == '*')
			{
				char op = stck.peek();
				while(isHigherPrecedence(op, c))
				{
					output += stck.pop();
					op = stck.peek();
				}
			}	
		}
	}
	public static boolean isOperator(char in)
	{
		return (in == '|' || in == '*' || in == '.');
	}
	public static boolean isHigherPrecedence(char lhs, char rhs)
	{
		if(lhs == '.' && rhs == '|') return true;
		else if(lhs == '|' && rhs == '.') return false;
		else if(lhs == '*') return true;
		else if(rhs == '*') return false;
	}
}
