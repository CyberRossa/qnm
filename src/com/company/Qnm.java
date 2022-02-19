package com.company;

import com.sun.org.apache.xpath.internal.operations.Variable;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

public class Qnm implements Problem<ISeq<Integer> , EnumGene<Integer>,Double> {
    private final Codec<ISeq<Integer>, EnumGene<Integer>> _codec;

    private Variable maps[];
    private List<Integer> Weights = new ArrayList();
    private List<Integer> Distance = new ArrayList();
    public int Fitness = 0;
    public int noIputs = 12;
    String inputFile = "tai12a.dat";
    public  Qnm(final ISeq<Integer> items,final int noIputs) throws IOException {
        _codec = Codecs.ofSubSet(items,noIputs);
        int x = (int) Math.pow(noIputs,2);
        int lim = x + 4;

        maps = new Variable[x];

        Scanner in = new Scanner(new File(inputFile));
        for (int i = 0; i < x; i++) {
            Weights.add(Integer.valueOf(in.next()));
        }
        for (int i = lim; i < lim + x; i++) {
            Distance.add(Integer.valueOf(in.next()));
        }

}

        public double CalcFitness(ISeq<Integer> gen){
        for (int i = 0; i <12 ; i++) {
            for (int j = 0; j < 12; j++) {
                int w = i * 12 + j;
                int w1 = Weights.get(w);
                int d0 =  gen.get(i).intValue() -1; // get ile
                int d1 =  gen.get(j).intValue() -1;
                int d2 = d0* 12 + d1;
                int d = Distance.get(d2);
                Fitness = w1 * d;



            }
            System.out.println(gen);
        }
        return Fitness;
    }

    @Override
    public Function<ISeq<Integer>, Double> fitness() {



        return this::CalcFitness;
    }



    @Override
    public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
        return _codec;
    }

    public static Qnm of(final int itemCount,Random rnd ) throws IOException {

        List<Integer> Perm = new ArrayList();
        for (int i = 0; i < 12; i++) {

            Perm.add(i + 1);
        }


        Collections.shuffle(Perm,rnd);
        return new Qnm(ISeq.of(Perm),itemCount);

    }
    public static void main(String[] args) throws IOException {
        Qnm qnm = Qnm.of(12,new Random(3));
        final Engine<EnumGene<Integer>, Double> engine = Engine.builder(qnm).populationSize(200).offspringSelector(new TournamentSelector<>(3)).alterers(new Mutator<>(0.012), new SinglePointCrossover<>(0.145)).build();
        final EvolutionStatistics <Double, ?>
                statistics = EvolutionStatistics.ofNumber();
        final Phenotype<EnumGene<Integer>,Double > best = engine.stream().limit(bySteadyFitness(10)).limit(1000).peek(statistics).collect(toBestPhenotype());

        System.out.println(statistics);
        System.out.println(best);
    }



}