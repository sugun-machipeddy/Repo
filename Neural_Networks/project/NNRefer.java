package project2;

import java.io.IOException;
import java.io.PrintWriter;

public class NNRefer {
	
	int index = 0;
	int wincount = 0;
	static int battle[] = new int[200];
	int numstates;
	int numactions;
	// static private double[][] LUT;
	public NNRefer(int nactions) {
	numactions = nactions;
	initialize();
	}
	public void initialize() {
	for (int i = 0; i < 200; i++) {
	battle[i] = 0;
	}
	}
	public int getMaxQValueact(double dist, double ener, double sin, double cos) {
	NNwithLUT ob = new NNwithLUT();
	double Qmaxa = 0;
	int act = 0;
	for (int a = 0; a < numactions; a++) //predicted next action
	{
	if (ob.NNQval(dist, ener, sin, cos, a) > Qmaxa) {
	Qmaxa = ob.NNQval(dist, ener, sin, cos, a);
	act = a;
	} else {
	continue;
	}
	}
	return (act);
	}
	public double getQValue(double dist, double ener, double sin, double cos, int action) {
	NNwithLUT ob1 = new NNwithLUT();
	return ob1.NNQval(dist, ener, sin, cos, action);
	}
	public void setQValue(double dist, double ener, double sin, double cos, int action, double value) {
	NNwithLUT ob2 = new NNwithLUT();
	ob2.train(dist, ener, sin, cos, action, value);
	}
	public void battlewin(int winnum) {
	System.out.println("Battlewin is called" + ":" + winnum);
	battle[index] = winnum;
	index = index + 1;
	}
	public void saveresults() {
	System.out.println("I am Called");
	try {
	PrintWriter writer = new PrintWriter("results2.txt");
	for (int i = 0; i < 200; i++) {
	writer.println(new Integer(battle[i]));
	}
	if (writer.checkError())
	System.out.println("Could not save the data!");
	writer.close();
	} catch (IOException e) {
	System.out.println("IOException trying to write: " + e);
	}
	}
	}
