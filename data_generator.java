package ca.pfv.spmf.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan.AlgoBIDEPlus;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.AlgoClaSP;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVMSP;

//import ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase;

public class data_generator{
	public static void main(String[] args) throws IOException{ 
		final int col = 50, row = 50, minsup = (row/2), alphabet_size = 50;
		//Scanner in = new Scanner(system.in);
		int hold_ran;
		Random ran = new Random();
		
		//Create dataset
		int[][] array = new int[row][col];
		for(int x=0; x<minsup; x++){
			hold_ran = ran.nextInt(col - 3);
			array[x][hold_ran] = 1;
			array[x][hold_ran+1] = 2;
			array[x][hold_ran+2] = 4;
		}
		for(int r = 0; r < array.length; r++){
			for(int c = 0; c < array[r].length; c++){
				if(array[r][c] == 0)
					array[r][c] = ran.nextInt(alphabet_size)+1;
			}
		}
		//Print dataset to screen
		/*for(int r = 0; r < array.length; r++){
			for(int c = 0; c < array[r].length; c++){
				System.out.print(array[r][c] + " ");
			}
			System.out.println();
		}*/
		//Write dataset to file
		PrintWriter outfile = new PrintWriter(new FileWriter("testing.txt"));
		for(int r = 0; r < array.length; r++){
			for(int c = 0; c < array[r].length; c++){
				outfile.print(array[r][c] + " -1 ");
			}
			outfile.println("-2");
		}
		outfile.close();
		//Print file to screen
		/*BufferedReader in = new BufferedReader(new FileReader("testing.txt"));
		String text = in.readLine();
		while(text != null){
			System.out.println(text);
			text = in.readLine();
		}
		in.close();*/
		//Open file to be used in algorithms
		File file = new File("testing.txt");
		
		//Create file to hold statistics
		PrintWriter Statistics = new PrintWriter(new FileWriter("Stat1Row"+row+".txt"));
		
		Statistics.println("CMSPAM");
		//Run CMSPAM
		
		String input = file.getAbsolutePath();
	    String output = ".//CMSPAMRow"+row+".txt";
	    double support = (double) minsup/row;       
				
		// Create an instance of the algorithm 
		AlgoCMSPAM algo = new AlgoCMSPAM(); 
		//algo.setMaximumPatternLength(3);
				
		// execute the algorithm with minsup = 2 sequences  (50 %)
		algo.runAlgorithm(input, output, support);
			
		//Print statistics to file
		Statistics.println("Min Support: "+minsup);
		Statistics.println("Time: "+algo.getRunTime());
		Statistics.println("Memory: "+algo.getMemory());
			
		//Check if the algorithm found the pattern
		BufferedReader in = new BufferedReader(new FileReader("CMSPAMRow"+row+".txt"));
		boolean found = false;
		String text = in.readLine();
		while(text != null && !found){
			for(int x=minsup; x<=row; x++){
				if(text.equals("1 -1 2 -1 4 -1 SUP: "+x))
					found = true;
			}
			text = in.readLine();
		}
		if(found)
			Statistics.println("Pattern was found");
		in.close();
		Statistics.println();

		
		//Run CMSPADE
		Statistics.println("CMSPADE");
		String outputPath =".//CMSPADERow"+row+".txt";
	    // Load a sequence database
	    support = (double) minsup/row;

	    boolean keepPatterns = true;
	    boolean verbose = false;

	    ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator abstractionCreator = ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative.getInstance();
        boolean dfs=true;

        ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();
	                
        CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
	        
        ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase sequenceDatabase = new ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase(abstractionCreator, idListCreator);

        sequenceDatabase.loadFile(file.getAbsolutePath(), support);
	        
        System.out.println(sequenceDatabase.toString());

        AlgoCMSPADE algorithm = new AlgoCMSPADE(support,dfs,abstractionCreator);
	        
        algorithm.runAlgorithm(sequenceDatabase, candidateGenerator,keepPatterns,verbose,outputPath);
	        
        //Print statistics to file
        Statistics.println("Min Support: "+minsup);
		Statistics.println("Time: "+algorithm.getRunningTime());
		Statistics.println("Memory: "+algorithm.getMemory());
		
		//Check if the algorithm found the pattern
		in = new BufferedReader(new FileReader("CMSPADERow"+row+".txt"));
		found = false;
		text = in.readLine();
		while(text != null && !found){
			for(int x=minsup; x<=row; x++){
				if(text.equals("1 -1 2 -1 4 -1 #SUP: "+x))
					found = true;
			}
			text = in.readLine();
		}
		if(found)
			Statistics.println("Pattern was found");
		in.close();
		Statistics.println();
		
		
		Statistics.println("Prefix");
		 
		outputPath = ".//PostRow"+row+".txt";
	    // Load a sequence database
	    support = (double) minsup/row;

	    keepPatterns = true;
	    verbose = true;
	    boolean findClosedPatterns = true;
	    boolean executePruningMethods = false;

        AbstractionCreator abstractionCreator2 = AbstractionCreator_Qualitative.getInstance();
        IdListCreator idListCreator2 = IdListCreatorStandard_Map.getInstance();

        SequenceDatabase sequenceDatabase2 = new SequenceDatabase(abstractionCreator2, idListCreator2);

	    //double relativeSupport = sequenceDatabase.loadFile(fileToPath("ExampleClaSP.txt"), support);
        double relativeSupport = sequenceDatabase2.loadFile(file.getAbsolutePath(), support);
	    //double relativeSupport = sequenceDatabase.loadFile(fileToPath("gazelle.txt"), support);

        AlgoClaSP algorithm2 = new AlgoClaSP(relativeSupport, abstractionCreator2, findClosedPatterns, executePruningMethods);


        //System.out.println(sequenceDatabase.toString());
        algorithm2.runAlgorithm(sequenceDatabase2, keepPatterns, verbose, outputPath);
	        
        //Print statistics to file
        Statistics.println("Min Support: "+minsup);
		Statistics.println("Time: "+algorithm2.getRunningTime());
		Statistics.println("Memory: "+algorithm2.getMemory());
			
		//Check if the algorithm found the pattern
        in = new BufferedReader(new FileReader("PostRow"+row+".txt"));
		found = false;
		text = in.readLine();
		while(text != null && !found){
			for(int x=minsup; x<=row; x++){
				if(text.equals("1 -1 2 -1 4 -1 #SUP: "+x))
					found = true;
			}
			text = in.readLine();
		}
		if(found)
			Statistics.println("Pattern was found");
		in.close();
		Statistics.println();
		
		
		//Run BIDEPlus
		Statistics.println("BIDEPlus");
		   
		// Load a sequence database
		ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase sequenceDatabase3 = new ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase(); 
		sequenceDatabase3.loadFile(file.getAbsolutePath());
		//sequenceDatabase.print();
		
		//int sup = 7;
		int sup = minsup; // we use a minsup of 2 sequences (50 % of the database size)
			
		AlgoBIDEPlus algo2  = new AlgoBIDEPlus();  //
			
		// execute the algorithm
		algo2.runAlgorithm(sequenceDatabase3, ".//BIDEPlusRow"+row+".txt", sup);
			
		//Print statistics to file
		Statistics.println("Min Support: "+minsup);
		Statistics.println("Time: "+algo2.getTime());
		Statistics.println("Memory: "+algo2.getMemory());
			
		//Check if the algorithm found the pattern
		in = new BufferedReader(new FileReader("BIDEPlusRow"+row+".txt"));
		found = false;
		text = in.readLine();
		while(text != null && !found){
			for(int x=minsup; x<=row; x++){
				if(text.equals("1 -1 2 -1 4 -1  #SUP: "+x))
					found = true;
			}
			text = in.readLine();
		}
		if(found)
			Statistics.println("Pattern was found");
		in.close();
		Statistics.println();
	
		
		//Run VMSP
		Statistics.println("VMSP");
		
		input = file.getAbsolutePath();
		output = ".//VMSPRow"+row+".txt";
		support = (double) minsup/row;
		// Create an instance of the algorithm 
		AlgoVMSP algo3 = new AlgoVMSP(); 
		//algo.setMaximumPatternLength(3);
			
		// execute the algorithm with minsup = 2 sequences  (50 %)
		algo3.runAlgorithm(input, output, support);    
			
		//Print statistics to file
		Statistics.println("Min Support: "+minsup);
		Statistics.println("Time: "+algo3.getRunTime());
		Statistics.println("Memory: "+algo3.getMemory());
			
		//Check if the algorithm found the pattern
		in = new BufferedReader(new FileReader("VMSPRow"+row+".txt"));
		found = false;
		text = in.readLine();
		while(text != null && !found){
			for(int x=minsup; x<=row; x++){
				if(text.equals("1 -1 2 -1 4 -1 SUP: "+x))
					found = true;
			}
			text = in.readLine();
		}
		if(found)
			Statistics.println("Pattern was found");
		in.close();
		Statistics.println();
		
		Statistics.close();
    }

}

