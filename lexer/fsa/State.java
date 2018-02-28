package lexer.fsa;
import lexer.TokenType;
import java.util.ArrayList;

class State
{
	boolean accepted;
	TokenType type;
	ArrayList<Edge> edges;
	public State(TokenType type, boolean accepted)
	{
		this.type = type;
		this.accepted = accepted;
		this.edges = new ArrayList<Edge>();
	}


	public void add_transition(State state, char input)
	{
		Edge e = new Edge(state, input);
		edges.add(e);
	}
	public State get_transition(char input)
	{
		for(Edge e : edges)
		{
			if(e.input == input)
				return e.endpoint;
		}
		return null;
	}
}
