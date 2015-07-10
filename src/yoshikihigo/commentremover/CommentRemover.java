package yoshikihigo.commentremover;

abstract public class CommentRemover {

	public static void main(final String[] args){
		
		Config.initialize(args);
		
		if(!Config.getInstance().hasLANGUAGE()){
			System.err.println("option \"-lang\" is required to specify your programming language.");
		}
		
		final LANGUAGE language = Config.getInstance().getLANGUAGE();
		CommentRemover remover;
		switch(language){
		case C:
		case CPP:
		case JAVA:
			remover = new CommentRemoverJC();
		case PYTHON:
			remover = new CommentRemoverPY();
		}
	}
	
	abstract public void perform(final Config config);
}
