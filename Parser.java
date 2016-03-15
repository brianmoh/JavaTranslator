public class Parser {
	
    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    private void program() {
	block();
    }

    private void block(){
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    mustbe(TK.ID);
	}
    }

    //While we get an id, print, do, or if token, keep running statement.
    private void statement_list() {
    	while(is(TK.ID) | is(TK.PRINT) | is(TK.DO) | is(TK.IF))
    	{
    		statement();
    	}
    }

    //This function goes into either print, do, if, or assign function.
    private void statement()
    {
		if(is(TK.PRINT))
    	{
    		print();
    	}
    	
    	else if(is(TK.DO))
    	{
    		do_fun();
    	}
    	
    	else if(is(TK.IF))
    	{
    		if_fun();
    	}
    	else
    	{
    		assignment();
    	}
    }

    private void if_fun()
    {
    	mustbe(TK.IF);
    	guarded_command();
    	while(is(TK.ELSEIF))
    	{
    		scan();
    		guarded_command();
    	}
    	if(is(TK.ELSE))
    	{
    		mustbe(TK.ELSE);
    		block();
    	}
    	mustbe(TK.ENDIF);
    }

    private void print()
    {
    	mustbe(TK.PRINT);
    	expression();
    }

    private void expression()
    {
    	term();
    	while(is(TK.PLUS) | is(TK.MINUS))
    	{
    		scan();
    		addop();
    		term();
    	}
    }

    private void addop()
    {
    	if(is(TK.PLUS))
    	{
    		mustbe(TK.PLUS);
    	}
    	else if(is(TK.MINUS))
    	{
    		mustbe(TK.MINUS);
    	}
    }

    private void term()
    {
    	factor();
    	while(is(TK.TIMES) | is(TK.DIVIDE))
    	{
    		scan();
    		multop();
    		term();
    	}
    }

    private void multop()
    {
    	if(is(TK.TIMES))
    	{
    		mustbe(TK.TIMES);
    	}
    	else if(is(TK.DIVIDE))
    	{
    		mustbe(TK.DIVIDE);
    	}
    }

    private void factor()
    {
    	if(is(TK.LPAREN))
    	{
    		mustbe(TK.LPAREN);
    		expression();
    		mustbe(TK.RPAREN);
    	}
    	else if(is(TK.NUM))
    	{
    		mustbe(TK.NUM);
    	}
    	else
    	{
    		ref_id();
    	}
    }

    private void ref_id()
    {
    	if(is(TK.TILDE))
    	{
    		mustbe(TK.TILDE);
    		if(is(TK.NUM))
    		{
    			mustbe(TK.NUM);
    		}
    	}
    	mustbe(TK.ID);
    }

    private void do_fun()
    {
    	mustbe(TK.DO);
    	guarded_command();
    	mustbe(TK.ENDDO);
    }

    private void guarded_command()
    {
    	expression();
    	mustbe(TK.THEN);
    	block();
    }

    private void assignment()
    {
    	ref_id();
    	mustbe(TK.ASSIGN);
    	expression();
    }

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
