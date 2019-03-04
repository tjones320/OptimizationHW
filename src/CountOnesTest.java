
import java.util.Arrays;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author TannerJones
 * @version 2.0
 */
public class CountOnesTest {
    /** The n value */
    private static final int NSTART = 40;
    private static final int NFIN = 200;
    
    public static void main(String[] args) {
    	double values [][][] = new double[8][2][4];
    	String names [] = {"RHC ", "SA ", "GA ", "MIMIC "};
    	String type [] = {"OP ", "TIME "};
    	for (int i = NSTART; i < NFIN; i = i + 20) {
    		values[(i - 40)/20] = call(i);
    	}
    	for (int i = 0; i < 4; i++) {
    		System.out.println(names[i]);
    		for (int j = 0; j < 2; j++) {
    			System.out.println(type[j]);
    			for (int n = 0; n < 8; n++) {
    				System.out.println(values[n][j][i]);
    			}
    		}
    	}
        
    }
    
    public static double[][] call (int N) {
    	int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new CountOnesEvaluationFunction();
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        double[][] values = {{0,0,0,0}, {0,0,0,0}};
        long starttime = System.currentTimeMillis();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200);
        fit.train();
        values[0][0] = ef.value(rhc.getOptimal());
        values[1][0] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        SimulatedAnnealing sa = new SimulatedAnnealing(11, .1, hcp);
        fit = new FixedIterationTrainer(sa, 200);
        fit.train();
        //System.out.println(ef.value(sa.getOptimal()));
        values[0][1] = ef.value(sa.getOptimal());
        values[1][1] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(20, 20, 0, gap);
        fit = new FixedIterationTrainer(ga, 300);
        fit.train();
        //System.out.println(ef.value(ga.getOptimal()));
        values[0][2] = ef.value(ga.getOptimal());
        values[1][2] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(50, 10, pop);
        fit = new FixedIterationTrainer(mimic, 100);
        fit.train();
        //System.out.println(ef.value(mimic.getOptimal()));
        values[0][3] = ef.value(mimic.getOptimal());
        values[1][3] = System.currentTimeMillis() - starttime;
        //System.out.println(values[0][0]);
        //System.out.println(values[0][1]);
        //System.out.println(values[0][2]);
        //System.out.println(values[0][3]);
        return values;
    }
}