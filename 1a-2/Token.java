public class Token
{
	Lexer.TokenType type;
	String data;
	static int num = 0;
	public Token(Lexer.TokenType type, String data)
	{	
		this.type = type;
		this.data = data;
		num++;
		
		System.out.println("Token " + num + " - Type: "+ type + " - Data: " + data);
	}
}