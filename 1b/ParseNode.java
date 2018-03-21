import java.util.LinkedList;
public class ParseNode
{
	Lexer.TokenType type;
	LinkedList<ParseNode> children;

	public ParseNode(Lexer.TokenType type)
	{
		this.type = type;
		children = new LinkedList<ParseNode>();
	}	

	//adds child and returns it
	public ParseNode addChild(ParseNode node)
	{
		children.add(node);
		return node;
	}
}