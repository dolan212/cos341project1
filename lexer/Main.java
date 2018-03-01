package lexer;
import lexer.fsa.NFABuilder;

class Main
{
	public static void main(String args[])
	{
		System.out.println(NFABuilder.convertToRPN("((1.2)/3).4"));
	}
}
