package lexer;
import lexer.TokenType;
public class DFA {
	String input;
	int index;
		
	public DFA(String input)
	{
		this.input = input;
		this.index = 0;
	}
	public TokenType invalid()
	{
		return null;
	}
	public boolean inRange(char c, char i, char j)
	{
		return (c >= i && c <= j);
	}
	public boolean isNum(char c)
	{
		return inRange(c, '0', '9');
	}
	public boolean isAlph(char c)
	{
		return inRange(c, 'a', 'z');
	}
	public boolean excl(char c, char i)
	{
		if(i == 'a')
			return inRange(c, 'b', 'z');
		else if(i == 'z') return inRange(c, 'a', 'y');
		else if(i >= 'a' && i <= 'z') return (inRange(c, 'a', (char)(i - 1)) || inRange(c, (char)(i + 1), 'z'));
		else return false;
	}
	public TokenType S0() 
	{
		char c = input.charAt(index++);
		if(c == 'c' || c == 'd' || c == 'g' || inRange(c, 'j', 'l') || c == 'q' || c == 'r' || c == 'u' || c == 'v' || inRange(c, 'x', 'z'))
			return S3();
		else if(c == 'a') return S1();
		else if(c == 'b') return S2();
		else if(c == 'e') return S4();
		else if(c == 'f') return S5();
		else if(c == 'h') return S6();
		else if(c == 'i') return S7();
		else if(c == 'm') return S8();
		else if(c == 'n') return S9();
		else if(c == 'o') return S10();
		else if(c == 'p') return S11();
		else if(c == 's') return S12();
		else if(c == 't') return S13();
		else if(c == 'w') return S14();
		else if(c >= '1' && c <= '9') return S15();
		else if(c == '\"') return S16();
		else if(c == '<' || c == '>') return S18();
		else if(c == ' ' || c == '#') return S19();
		else if(c == '{' || c == '}' || c == '(' || c == ')' || c == ';' || c == ',')
			return S20();
		else if(c == '=') return S23();
		else if(c == '-') return S24();

		return invalid();
	}
	public TokenType S1() {
		char c = input.charAt(index++); 
		if(inRange(c, 'a', 'c') || inRange(c, 'e', 'm') || inRange(c, 'o', 'z') || inRange(c, '0', '9'))
			return S25();
		else if(c == 'd') return S26();
		else if(c == 'n') return S27();
		
		return TokenType.VAR;
	}
	public TokenType S2() {
		char c = input.charAt(index++);
		if(excl(c, 'o') || inRange(c, '0', '9'))
			return S25();
		else if(c == 'o') return S28();

		return TokenType.VAR;
	}
	public TokenType S3() {
		char c = input.charAt(index++);
		if(isNum(c) || isAlph(c))
			return S25();

		return TokenType.VAR;
	}
	public TokenType S4() {
		char c = input.charAt(index++);
		if(inRange(c, 'a', 'k') || inRange(c, 'm', 'p') || inRange(c, 'r', 'z') || isNum(c))
		       return S25();
		else if(c == 'l') return S29();
		else if(c == 'q') return S30();

		return TokenType.VAR;	
	}
	public TokenType S5() {
		char c = input.charAt(index++);
		if(excl(c, 'o') || isNum(c))
			return S25();
		else if(c == 'o') return S31();

		return TokenType.VAR;
	}
	public TokenType S6() {
		char c = input.charAt(index++);
		if(inRange(c, 'b', 'z') || isNum(c))
			return S25();
	}
	public TokenType S7() {}
	public TokenType S8() {}
	public TokenType S9() {}
	public TokenType S10() {}
	public TokenType S11() {}
	public TokenType S12() {}
	public TokenType S13() {}
	public TokenType S14() {}
	public TokenType S15() {}
	public TokenType S16() {}
	public TokenType S17() {}
	public TokenType S18() {}
	public TokenType S19() {}
	public TokenType S20() {}
	public TokenType S21() {}
	public TokenType S22() {}
	public TokenType S23() {}
	public TokenType S24() {}
	public TokenType S25() {}
	public TokenType S26() {}
	public TokenType S27() {}
	public TokenType S28() {}
	public TokenType S29() {}
	public TokenType S30() {}
	public TokenType S31() {}
	public TokenType S32() {}
	public TokenType S33() {}
	public TokenType S34() {}
	public TokenType S35() {}
	public TokenType S36() {}
	public TokenType S37() {}
	public TokenType S38() {}
	public TokenType S39() {}
	public TokenType S40() {}
	public TokenType S41() {}
	public TokenType S42() {}
	public TokenType S43() {}
	public TokenType S44() {}
	public TokenType S45() {}
	public TokenType S46() {}
	public TokenType S47() {}
	public TokenType S48() {}
	public TokenType S49() {}
	public TokenType S50() {}
	public TokenType S51() {}
	public TokenType S52() {}
	public TokenType S53() {}
	public TokenType S54() {}
	public TokenType S55() {}
	public TokenType S56() {}
	public TokenType S57() {}
	public TokenType S58() {}
	public TokenType S59() {}
	public TokenType S60() {}
	public TokenType S61() {}
	public TokenType S62() {}
	public TokenType S63() {}
	public TokenType S64() {}
	public TokenType S65() {}
	public TokenType S66() {}
	public TokenType S67() {}
	public TokenType S68() {}
	public TokenType S69() {}
	public TokenType S70() {}
	public TokenType S71() {}
}
