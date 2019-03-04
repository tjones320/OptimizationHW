
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
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * Copied from ContinuousPeaksTest
 * @version 1.0
 */
public class FourPeaksTest {
    /** The n value */
    private static final int NSTART = 200;
    private static final int NFIN = 800;
    /** The t value */
    //private static final int T = N / 5;
    
    public static void main(String[] args) {
        	double values [][][] = new double[13][2][4];
        	String names [] = {"RHC ", "SA ", "GA ", "MIMIC "};
        	String type [] = {"OP ", "TIME "};
        	for (int i = NSTART; i < NFIN; i = i + 50) {
        		values[(i - 200)/50] = call(i, i/5);
        	}
        	for (int i = 0; i < 4; i++) {
        		System.out.println(names[i]);
        		for (int j = 0; j < 2; j++) {
        			System.out.println(type[j]);
        			for (int n = 0; n < 10; n++) {
        				System.out.println(values[n][j][i]);
        			}
        		}
        	}
            
        }
        
        public static double[][] call (int N, int T) {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        long starttime = System.currentTimeMillis();
        double[][] values = {{0,0,0,0},{0,0,0,0}};
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
        fit.train();
        values[0][0] = ef.value(rhc.getOptimal());
        values[1][0] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
        fit = new FixedIterationTrainer(sa, 200000);
        fit.train();
        values[0][1] = ef.value(sa.getOptimal());
        values[1][1] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();
        values[0][2] = ef.value(ga.getOptimal());
        values[1][2] = System.currentTimeMillis() - starttime;
        
        starttime = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(200, 20, pop);
        fit = new FixedIterationTrainer(mimic, 1000);
        fit.train();
        values[0][3] = ef.value(mimic.getOptimal());
        values[1][3] = System.currentTimeMillis() - starttime;
        return values;
    }
}
