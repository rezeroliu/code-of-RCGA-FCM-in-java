package RCGA_FCM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class RCGA_FCM_main
{
    //遗传算法参数设置
    static int max_generation = 10000;     //最大进化次数
    static int pop_size = 100;             //种群大小
    static double crossoverRate = 0.9;     //交叉概率
    static double mutationRate = 0.5;      //变异概率
    static int iter = 0;
    
    //FCM模型参数设置
    static int nodeNumber = 10;            //节点数量
    static double density = 0.2;           //密度
    static int K = 20;                     //状态数
    static int varNumber = nodeNumber * nodeNumber;        //变量个数
    static double[] lowBound = new double[varNumber];      //变量下限
    static double[] upperBound = new double[varNumber];    //变量上限
    static int edges = (int)(varNumber * density);         //有效边数量
    static double[] inputWeight = new double[varNumber];
    static double[] initVector = new double[nodeNumber];
    static double[][] inputSequence = new double[nodeNumber][K];

    static Individual best_Individual;   //存储每一代的最优个体
    
    
    public static void main(String[] args)
    {
        Random rand = new Random();
        int temp = 0;
        for(int i = 0; i < varNumber; i++)
        {
            lowBound[i] = -1;
            upperBound[i] = 1;
            if(rand.nextDouble() < density  && temp++ < edges)
            {
                inputWeight[i] = 2 * rand.nextDouble() - 1;
            }
        }
        for(int i = 0; i < nodeNumber; i++)
        {
            initVector[i] = rand.nextDouble();
        }
        
        saveTofile("E:/GraduationProject/data/inputFCM.txt", inputWeight);
        //根据初始状态值和关系矩阵生成状态转移序列
        generateSequence(inputSequence, initVector, inputWeight);
        //保存输入FCM和状态转移序列
        saveTofile("E:/GraduationProject/data/inputSeq.txt", inputSequence);
               
        Individual[] population = new Individual[pop_size];
        Individual[] offSpring = new Individual[pop_size];    //子代种群
        Individual[] hybird_Pop = new Individual[pop_size * 2];   //父代种群
        //初始化种群
        for(int i = 0; i < pop_size; i++)
        {
            population[i] = new Individual(varNumber, edges, lowBound, upperBound);
            //计算适应度值
            population[i].objValue = calFitness(population[i].variables);
        }
        
        best_Individual = population[rand.nextInt(pop_size)];
        //进化过程
        while(iter < max_generation && best_Individual.objValue < 0.999)
        {
            //二进制锦标赛选择法
            offSpring = bin_tournment_select(population);
            for(int i = 0; i < pop_size; i += 2)
            {
                if(rand.nextDouble() < crossoverRate)
                {
                    //交叉使用单点交叉
                    simple_crossover(offSpring[i].variables, offSpring[i+1].variables);
                }
            }
            for(int i = 0; i < pop_size; i++)
            {
                if(rand.nextDouble() < crossoverRate)
                {
                    //非均匀变异
                    NUM(offSpring[i].variables);
                }
                offSpring[i].objValue = calFitness(offSpring[i].variables);
            }
            
            //合并父代和子代种群
            for(int i = 0; i < pop_size; i++)
            {
                hybird_Pop[i] = new Individual(population[i]);
                hybird_Pop[i+pop_size] = new Individual(offSpring[i]);
            }
            //对合并后的种群按适应度由高到低进行排序
            quick_sort(hybird_Pop, 0, 2 * pop_size - 1);
            for(int i = 0; i < pop_size; i++)
            {
                population[i] = hybird_Pop[i];
            }
            best_Individual = population[0];
            
            System.out.println("The " + iter + " Iteration! Best objective value is --------------" + best_Individual.objValue);
            iter++;
        }
        
        double[][] BestSequence = new double[nodeNumber][K];
        generateSequence(BestSequence, initVector, best_Individual.variables);
        saveTofile("E:/GraduationProject/data/BestFCM.txt", best_Individual.variables);
        saveTofile("E:/GraduationProject/data/BestSeq.txt", BestSequence);
    }
   

    private static void quick_sort(Individual[] hybird_Pop, int lo, int hi)
    {
        if(lo >= hi)
            return ;
        
        int i = lo;
        int j = hi;
        double keyValue = hybird_Pop[lo].objValue;
        while(i < j)
        {
            while(hybird_Pop[j].objValue <= keyValue && j > i)
            {
                j--;
            }
            while(hybird_Pop[i].objValue >= keyValue && j > i)
            {
                i++;
            }
            if(i < j)
            {
                Individual tempInd = hybird_Pop[i];
                hybird_Pop[i] = hybird_Pop[j];
                hybird_Pop[j] = tempInd;
            }  
        }
        Individual tempInd = hybird_Pop[i];
        hybird_Pop[i] = hybird_Pop[lo];
        hybird_Pop[lo] = tempInd;
        quick_sort(hybird_Pop, lo, i - 1);
        quick_sort(hybird_Pop, i + 1, hi);
    }

    private static void simple_crossover(double[] var1, double[] var2)
    {
        Random rnd = new Random();
        int index = rnd.nextInt(varNumber - 1) + 1;  //交叉点位置 (0,varNumber - 1)
        int count;
        for(count = 0; count < index; count++)
        {
            var2[count] = var1[count];
        }
        for( ; count < varNumber; count++)
        {
            var1[count] = var2[count];
        }
    }

    private static void NUM(double[] var)
    {
        double b_para =  5,a, b, c, r, delta;
        Random rnd = new Random();
        for(int count = 0; count < varNumber; count++)
        {
            if(rnd.nextDouble() < 0.5)
            {
                if(rnd.nextBoolean())
                {
                    c = var[count];
                    b = upperBound[count];
                    r = rnd.nextDouble();
                    delta = (b - c) * (1 - Math.pow(r, Math.pow(1 - iter * 1.0 / max_generation, b_para)));
                    var[count] += delta;
                }
                else
                {
                    c = var[count];
                    a = lowBound[count];
                    r = rnd.nextDouble();
                    delta = (c - a) * (1 - Math.pow(r, Math.pow(1 - iter * 1.0 / max_generation, b_para)));
                    var[count] -= delta;
                }
            }
        }
    }


    private static Individual[] bin_tournment_select(Individual[] population)
    {
        Individual[] newPop = new Individual[pop_size];
        Random rnd = new Random();
        for(int i = 0; i < pop_size; i++)
        {
            int p1 = rnd.nextInt(pop_size);
            int p2 = rnd.nextInt(pop_size);
            while(p2 == p1)
            {
                p2 = rnd.nextInt(pop_size);
            }
            if(population[p1].objValue > population[p2].objValue)
            {
                newPop[i] = new Individual(population[p1]);
            }
            else
            {
                newPop[i] = new Individual(population[p2]);
            }
        }
        return newPop;
    }

    private static void generateSequence(double[][] stateSequence, double[] initVector, double[] weight)
    {
        for(int i = 0; i < nodeNumber; i++)
        {
            stateSequence[i][0] = initVector[i];
        }
        
        for(int i = 1; i < K; i++)
        {
            for(int j = 0; j < nodeNumber; j++)
            {
                double sum = 0;
                for(int k = 0; k < nodeNumber; k++)
                {
                    sum += (stateSequence[j][i-1] * weight[k * nodeNumber + j]);
                }
                stateSequence[j][i] = 1.0 / (1 + Math.exp(-5 * sum));
            }
        }
    }
    
    private static double calFitness(double[] variables)
    {
        double value = 0;
        double[][] sequence = new double[nodeNumber][K];
        generateSequence(sequence, initVector, variables);
        double error = 0;
        for(int k = 1; k < K; k++)
        {
            for(int n = 0; n < nodeNumber; n++)
            {
                error += Math.pow((sequence[n][k] - inputSequence[n][k]), 2);
            }
        }
        error /=  (nodeNumber * (K - 1));
        value = 1.0 / (1 + 10000 * error);
        return value;
    }
    
    private static void saveTofile(String string, double[] data)
    {
        try
        {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(string));
            for(int i = 0; i < nodeNumber; i++)
            {
                for(int j = 0; j < nodeNumber; j++)
                    bfw.write(String.format("%.6f", data[j * nodeNumber + i]) + "  ");
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
    
    private static void saveTofile(String string, double[][] data)
    {
        try
        {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(string));
            for(int i = 0; i < nodeNumber; i++)
            {
                for(int j = 0; j < K; j++)
                    bfw.write(String.format("%.6f", data[i][j]) + "  ");
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
