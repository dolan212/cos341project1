import java.util.LinkedList;
public class ParseNode
{

	NodeType type;
	LinkedList<ParseNode> children;
	String data;
	ParseNode parent = null;
	static int id;
	int num;
	boolean visited = false;

	public ParseNode(NodeType type)
	{
		this.type = type;
		children = new LinkedList<ParseNode>();
		num = id++;
	}

	public ParseNode(NodeType type, String data)
	{
		this(type);
		this.data = data;
	}	

	//adds child and returns it
	public ParseNode addChild(ParseNode node)
	{
		node.parent=this;
		children.push(node);
		return node;
	}

	public String toString()
	{
		String out = num+"|"+type+"|"+data;
		for(ParseNode n : children)
			out += "|" + n.num;
		return out;
	}
}
