package RCGA_FCM;

public class Global
{
    int max_generation = 10000;
    int pop_size = 100;
    static int nodeNumber = 10;
    double density = 0.2;
    int K = 20;
    double crossoverRate = 0.9;
    double mutationRate = 0.5;
    
    int varNumber = nodeNumber * nodeNumber;
    double[] lowBound = new double[varNumber];
    double[] upperBound = new double[varNumber];
    int edges = (int)(varNumber * density);
    double[] inputWeight = new double[varNumber];
    double[] initVector = new double[nodeNumber];
    double[][] inputSequence = new double[nodeNumber][varNumber];
}
