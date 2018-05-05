package RCGA_FCM;

import java.util.Random;

public class Individual
{
    protected double[] variables;
    protected double objValue;
    public Individual(int varNumber, int edges, double[] lowBound, double[] upperBound)
    {
        boolean[] flag = new boolean[varNumber];
        this.variables = new double[varNumber];
        
        Random rnd = new Random();
        while(0 != edges--)
        {
            int index = rnd.nextInt(varNumber);
            while(flag[index])   //此位置已经设置过权值
            {
                index = rnd.nextInt(varNumber);
            }
            variables[index] = rnd.nextDouble() * (upperBound[index] - lowBound[index]) + lowBound[index];
            flag[index] = true;
        }
    }
    
    public Individual(Individual p)
    {
        this.variables = p.variables.clone();
        this.objValue = p.objValue;
    }
    public double getObjValue()
    {
        return objValue;
    }
    public void setObjValue(double objValue)
    {
        this.objValue = objValue;
    }

}
