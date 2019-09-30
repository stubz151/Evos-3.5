import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    static ArrayList<Double[]> table = new ArrayList<>();
    static ArrayList<Double> valueList = new ArrayList<>();
    static ArrayList<Double[]> testtable = new ArrayList<>();
    static ArrayList<Double> testvalueList = new ArrayList<>();
    static ArrayList<Double[]> population = new ArrayList<>();
    static ArrayList<Double> fitnessList = new ArrayList<>();
    static double averageOfList;
    static double scaleFactor =1.00;
    static double crossOverRate =0.6;
    static int populationSize =1000;
    public static void main(String[] args) {

        Random rand = new Random();
        //Loads data into our table
        Path pathToFile = Paths.get("SalData.csv");
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            br.readLine();
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                String[] attributes = line.split(",");

                Double[] intAttributes = new Double[8];
                for (int i = 0; i < 7; i++) {
                    intAttributes[i] = Double.parseDouble(attributes[i + 1]);
                }
                intAttributes[7] = 1.00;
                line = br.readLine();
                count++;
                if (count % 5 == 0) {
                    testvalueList.add(Double.parseDouble(attributes[0]));
                    testtable.add(intAttributes);
                } else {
                    valueList.add(Double.parseDouble(attributes[0]));
                    table.add(intAttributes);
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        double total = 0.00;
        for (Double x : valueList) {
            total += x;
        }
        averageOfList = total / valueList.size();
        int iterations = 10000;
        //Randomly gen population
        for (int i = 0; i < populationSize; i++) {
            Double[] vector = new Double[8];
            for (int j = 0; j < vector.length; j++) {
                double randomValue = 0.00 + (100.00 - 0.00) * rand.nextDouble();
                vector[j] = randomValue;
            }
            population.add(vector);
            fitnessList.add(evaluateFitness(vector));
        }
        //Start Training
        for (int i = 0; i < iterations; i++) {
            ArrayList<Double> trialfitnessList = new ArrayList<>();
            ArrayList<Double[]> trialPopulation = new ArrayList<>();
            for (int j = 0; j < populationSize; j++) {

                int x1 = 0;
                int x2 = 0;
                int x3 = 0;
                while (x1 == x2 || x1 == x3 || x2 == x3 || x1 == j || x2 == j || x3 == j) {
                    x1 = rand.nextInt(populationSize-1);
                    x2 = rand.nextInt(populationSize-1);
                    x3 = rand.nextInt(populationSize-1);
                }
                //creating mutant vector
                Double[] vector1 = population.get(x1);
                Double[] vector2 = population.get(x2);
                Double[] vector3 = population.get(x3);
                Double[] newVector = new Double[8];
                for (int k = 0; k <= 7; k++) {
                    newVector[k] = vector1[k] + (scaleFactor * (vector2[k] - vector3[k]));
                }

                //creating trial vector
                Double[] orignalVector = population.get(j);
                for (int k = 0; k <= 7; k++) {
                    if (rand.nextDouble() > crossOverRate) {
                        newVector[k] = orignalVector[k];
                    }
                }
                double fitness = evaluateFitness(newVector);
                trialfitnessList.add(fitness);
                trialPopulation.add(newVector);
            }
            for (int j = 0; j < population.size(); j++) {
                if (trialfitnessList.get(j) < fitnessList.get(j)) {
                    population.set(j, trialPopulation.get(j));
                    fitnessList.set(j, trialfitnessList.get(j));
                }
            }
        }


        //Get best unit from population
        double best = 10000000000000000.00;
        int pos =0;
        for (int i = 0; i < population.size(); i++)
        {
            if (fitnessList.get(i)<best)
            {
                best = fitnessList.get(i);
                pos =i;
            }
        }

        System.out.printf("best sse: %f\n", best);
        System.out.print("," +"pop size " + population.size() +","  + "Scale factor " + scaleFactor + "," + "cross over rate " + crossOverRate);

        Double[] finalVector = population.get(pos);
        for(int i =0 ; i < finalVector.length; i++)
        {
            System.out.print(finalVector[i]+",");
        }

    }


    public static Double evaluateFitness(Double[] vector)
    {
        double sse = 0.00;
        for (int i =0 ; i<table.size() ; i++)
        {
        Double[] inputs = table.get(i);
        double prediction = Prediction(vector, inputs);
        double curError = Math.pow((prediction-averageOfList) , 2);
        sse+= curError;
        }
        return sse;
    }
    public static Double Prediction(Double[] vector, Double [] inputs)
    {
        double prediction =0.00;
        for (int i =0 ; i <vector.length ; i++)
        {
            prediction+= vector[i]*inputs[i];
        }
        return prediction;
    }
}
