# SMT

	Developers:
	  David Furtado Monteiro

	Description:
	  Program will evaluate a translation machine.
	  Get a a reference and translate it in "google translate" or "bing translate"
	  Save the translations and evaluate the translation against the reference
	  Program can make a quick evaluation where user runs the code and inputs the 
	    reference and then the translation in the commad line.
	  Program can also read the reference and and translation straight from respective files


	Run:
	  1.To Compile through cmd
		javac EvaluationSystems.java
	  2.To run the code using datasets
		java EvaluationSystems pt-en/reference-v8.pt-en.en pt-en/translated-v8.pt-en.en
		java EvaluationSystems pt-en/reference-v8.pt-en.pt pt-en/translated-v8.pt-en.pt
	  3.To run and a make a quick evaluation on small sentences
		java EvaluationSystems2
			EXAMPLE(type in terminal/shell):
			  "Salmons swim in the river ."
			  "Fish swim in the river ."
