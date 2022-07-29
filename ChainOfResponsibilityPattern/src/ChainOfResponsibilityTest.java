import java.util.*;
import java.util.regex.*;

public class ChainOfResponsibilityTest {

	public static void main(String[] args) {
		String text = "I want to increase my grade using makeup homeworks in the Design Patters course";
		System.out.println("Input text:");
		System.out.println("-----------");
		System.out.println(text);
		System.out.println("\n");
		
		
		TextProcessingHandler chain = new FilterSmallWords(5);
		chain.setNextChain(new CapitalizeCase()).setNextChain(new ReverseWords());
		
		/*
		// below code does the same things with above one.your code should work with both approaches
		TextProcessingHandler chain = new FilterSmallWords(5);
		TextProcessingHandler chainSecond = new CapitalizeCase();
		TextProcessingHandler chainThird = new ReverseWords();
		chain.setNextChain(chainSecond);
		chainSecond.setNextChain(chainThird);
		*/
		
		
		System.out.println("Chain of Responsibility:");
		System.out.println("------------------------");
		chain.showChain();
		System.out.println("\n");
		
		
		System.out.println("Chain of Responsibility sample executions:");
		System.out.println("------------------------------------------");
		sampleExecution(chain, "CapitalizeCase", text);
		sampleExecution(chain, "FilterSmallWords", text);
		sampleExecution(chain, "TranslateToTurkish", text);
		sampleExecution(chain, "ReverseWords", text);
	}
	
	static void sampleExecution(TextProcessingHandler chain, String request, String text) {
		System.out.println(request + "    ->    " + chain.handle(request, text) );
	}

}


abstract class TextProcessingHandler {
	private TextProcessingHandler nextHandler;
	
	abstract String getRequestText();
	
	abstract String completeTask(String text);
	
	String handle(String request, String text) {
		if (request.equals(getRequestText())) {
			return completeTask(text);
		}
		else {
			if (nextHandler == null)
				return null;
			else 
				return nextHandler.handle(request, text);
		}
	}

	TextProcessingHandler setNextChain(TextProcessingHandler nextHandler) {
		this.nextHandler = nextHandler;
		
		return nextHandler;
	}
	
	void showChain() {
		System.out.print(this.getRequestText());
		
		if (nextHandler != null) {
			System.out.print(" --> ");			
			nextHandler.showChain();
		}
		else
			System.out.println();
	}
	
}

abstract class WordFilterers extends TextProcessingHandler {
	
	List<String> extractWords(String text) {
		List<String> words = new ArrayList<>();
		
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher matcher = pattern.matcher(text);
		
        while (matcher.find()) { 
        	String word = matcher.group();
        	words.add(word);
        }		
        
        return words;
	}
	
}

class FilterSmallWords extends WordFilterers {
	final int wordLengthThreshold;
	
	FilterSmallWords() {
		wordLengthThreshold = 3;
	}
	
	FilterSmallWords(int wordLengthThreshold) {
		this.wordLengthThreshold = wordLengthThreshold;
	}

	@Override
	String getRequestText() {
		return "FilterSmallWords";
	}

	@Override
	String completeTask(String request) {
		List<String> words = extractWords(request);
		
		StringBuilder newText = new StringBuilder(); 
		for (String word: words) {
			if (word.length() >= wordLengthThreshold) {
				newText.append(word + " ");
			}
		}
		
		return newText.toString();
	}
	
}

class CapitalizeCase extends WordFilterers {

	@Override
	String getRequestText() {
		return "CapitalizeCase";
	}

	@Override
	String completeTask(String request) {
		List<String> words = extractWords(request);
		
		StringBuilder newText = new StringBuilder(); 
		for (String word: words) {
			if (word.length() > 1) {
				word = word.toLowerCase();				
				word = word.substring(0, 1).toUpperCase(Locale.US) + word.substring(1);
				newText.append(word + " ");			
			}
			else 
				newText.append(word.toUpperCase(Locale.US) + " ");			
		}
		
		return newText.toString();
	}
	
}

class ReverseWords extends WordFilterers {

	@Override
	String getRequestText() {
		return "ReverseWords";
	}

	@Override
	String completeTask(String request) {
		List<String> words = extractWords(request);
		
		StringBuilder newText = new StringBuilder();
		for (int i=words.size()-1; i>=0; i--) {
			newText.append(words.get(i) + " ");	
		}
		
		return newText.toString();
	}
	
}
