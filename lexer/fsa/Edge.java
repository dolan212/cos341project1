package lexer.fsa;

public class Edge 
{
	public State endpoint;
	public char input;
	public Edge(State ep, char in)
	{
		this.endpoint = ep;
		this.input = in;
	}
}
