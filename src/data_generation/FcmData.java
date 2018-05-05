package data_generation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.Math;
import java.util.Random;
public class FcmData
{

    public static void main(String[] args)
    {
        int nodeNumber = 10;
        double density = 0.2;
        int stateNumber = 20;
        int edgeNumber = (int) (nodeNumber*nodeNumber*density);
        
        double[] initState = new double[nodeNumber];
        double[][] fcmMatrix = new double[nodeNumber][nodeNumber];
        Random rand = new Random();
        int count = 0;
        for(int i = 0; i < nodeNumber; i++)
        {
            initState[i] = Math.random();
            for(int j = 0; j < nodeNumber; j++)
            {
                if((i!=j) && (Math.random() < density) && (count < edgeNumber))
                {
                    fcmMatrix[i][j] = rand.nextGaussian();
                    count++;
                }
                System.out.print(fcmMatrix[i][j] + " ");
            }
            System.out.println();
        }
        saveTofile("E:/GraduationProject/data/inputFcm.csv", fcmMatrix);
        System.out.println("-------------------");
        double[][] inputData = new double[nodeNumber][stateNumber];
        for(int t = 0; t < stateNumber; t++)
        {
            for(int i = 0; i < nodeNumber; i++)
            {
                if(0==t)
                {
                    inputData[i][t] = initState[i];
                }
                else
                {
                    double temp = 0;
                    for(int j = 0; j < nodeNumber; j++)
                    {
                        temp += inputData[j][t-1] * fcmMatrix[j][i];
                    }
                    inputData[i][t] = 1/(1 + Math.exp(-5*temp));
                }
                System.out.print(inputData[i][t] + " ");
            }
            System.out.println();
        }
        saveTofile("E:/GraduationProject/data/inputData.csv", inputData);
    }

    private static void saveTofile(String string, double[][] data)
    {
        try
        {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(string));
            for(int i = 0; i < data.length; i++)
            {
                for(int j = 0; j < data[0].length; j++)
                    bfw.write(data[i][j] + ",");
                bfw.write("\r\n");
            }
            bfw.flush();
            bfw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
