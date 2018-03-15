package project2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class NNwithLUT {
	
	final static int ip = 6; //no. of i/p's + bias
	final static int hid = 6; //number of hidden neurons
	double stepsize = 0.6; //learning rate
	static double wintohid[][] = new double[ip][hid]; //Input to hidden weights
	static double whidtoout[] = new double[hid + 1]; //hidden to output weights + bias
	double h[] = new double[hid]; //hidden neurons
	double u[] = new double[hid + 1]; //outputs of hidden neurons +bias
	double ddxu[] = new double[hid + 1]; //derivative of hidden units
	double uerr[] = new double[hid];
	double deltawhidtoout[] = new double[hid + 1]; //change in hidden to output weights
	double deltawintohid[][] = new double[ip][hid]; // change in input to hidden weights
	double output;
	double ddxout; //derivative of o/p
	double outputerr; //(expected-output)*derivative of o/p
	double outputerror; // expected output-output
	double mom = 0.3; //momentum
	int binbip = 0; //Set =0 for binary and 1 for bipolar
	static int bias = 1;
	static int totalactions = 5;
	static double Input[] = new double[ip];
	static double qval = 0;
	static double Target[] = new double[2000]; //Desired o/p
	static double[] err_array = new double[2000]; //error array
	
	
	public NNwithLUT() {
	weightinit();
	try {
	readwintoh();
	} catch (IOException e) {
	System.out.println("Data could not be loaded");
	}
	try {
	readwhtoo();
	} catch (IOException e) {
	System.out.println("Data could not be loaded");
	}
	}
	public static void readwintoh() throws IOException {
	BufferedReader r = new BufferedReader(new FileReader("F:\\robocode\\weightintohid4.txt"));
	String line = r.readLine();
	try {
	int i = 0;
	int j = 0;
	while (line != null) {
	wintohid[i][j] = Double.parseDouble(line);
	if (j < hid - 1) {
	j++;
	} else {
	j = 0;
	i++;
	}
	line = r.readLine();
	}
	} catch (IOException e) {
	System.out.println("IOException trying to open reader: " + e);
	} finally {
	try {
	r.close();
	} catch (IOException e) {
	System.out.println("IOException trying to close reader: " + e);
	}
	}
	}
	public static void readwhtoo() throws IOException {
	BufferedReader r = new BufferedReader(new FileReader("F:\\robocode\\weighthidtoout4.txt"));
	String line = r.readLine();
	try {
	int i = 0;
	while (line != null) {
	whidtoout[i] = Double.parseDouble(line);
	i++;
	line = r.readLine();
	}
	} catch (IOException e) {
	System.out.println("IOException trying to open reader: " + e);
	} finally {
	try {
	r.close();
	} catch (IOException e) {
	System.out.println("IOException trying to close reader: " + e);
	}
	}
	}
	public static void writewintoh() {
	try {
	PrintWriter writer = new PrintWriter("F:\\robocode\\weightintohid4.txt");
	for (int i = 0; i < ip; i++)
	for (int j = 0; j < hid; j++)
	writer.println(new Double(wintohid[i][j]));
	if (writer.checkError())
	System.out.println("Could not save the data!");
	writer.close();
	} catch (IOException e) {
	System.out.println("IOException trying to write: " + e);
	}
	}
	public static void writewhtoo() {
	try {
	PrintWriter writer = new PrintWriter("F:\\robocode\\weighthidtoout4.txt");
	for (int i = 0; i < hid + 1; i++)
	writer.println(new Double(whidtoout[i]));
	if (writer.checkError())
	System.out.println("Could not save the data!");
	writer.close();
	} catch (IOException e) {
	System.out.println("IOException trying to write: " + e);
	}
	}
	public void weightinit() {
	for (int i = 0; i < wintohid.length; i++) {
	for (int j = 0; j < wintohid[i].length; j++) {
	wintohid[i][j] = 0;
	}
	}
	for (int i = 0; i < whidtoout.length; i++) {
	whidtoout[i] = 0;
	}
	}
	/*Method for binary sigmoid activation*/
	public double binarysigmoid(double a) {
	double bino = 1.0 / (1 + Math.exp(-a));
	return bino;
	}
	/*Method for bipolar sigmoid activation*/
	public double bipolarsigmoid(double a) {
	double bipo = (2.0 / (1 + Math.exp(-a))) - 1;
	return bipo;
	}
	/*Method that performs forward and backward propagation with weight updation*/
	public double NNQval(double dist, double ener, double sinb, double cosb, int act) {
	double d = dist / 1000.0;
	double e = ener / 100.0;
	double s = sinb / 7.0;
	double c = cosb / 7.0;
	double a = act / 4.0;
	double input[] = { bias, d, e, s, c, a };
	/*forward propagation*/
	for (int j = 0; j < hid; j++) {
	h[j] = 0; //lets see
	for (int i = 0; i < ip; i++) {
	h[j] += (input[i] * wintohid[i][j]);
	}
	}
	u[0] = 1; //bias
	//if(binbip==0)//if binary
	//{
	for (int i = 0; i < hid; i++) {
	u[i + 1] = binarysigmoid(h[i]);
	}
	ddxu[0] = 1;
	for (int i = 1; i < hid + 1; i++) {
	ddxu[i] = u[i] * (1 - u[i]);
	}
	double op = 0;
	for (int i = 0; i < hid + 1; i++) {
	op += u[i] * whidtoout[i];
	}
	output = binarysigmoid(op);
	output = output * (-45.98759280848989);
	//}
	return output;
	}
	public void train(double dist, double ener, double sinb, double cosb, int act, double eop) {
	double d = dist / 1000.0;
	double e = ener / 100.0;
	double s = sinb / 7.0;
	double c = cosb / 7.0;
	double a = act / 4.0;
	double input[] = { bias, d, e, s, c, a };
	double expop = eop / (-45.98759280848989);
	double et = 10;
	double te = 0;
	int count = 0;
	while (et > 0.5 && count < 500) {
	et = te / 2.0;
	count++;
	/*forward propagation*/
	for (int j = 0; j < hid; j++) {
	h[j] = 0; //lets see
	for (int i = 0; i < ip; i++) {
	h[j] += (input[i] * wintohid[i][j]);
	}
	}
	u[0] = 1; //bias
	if (binbip == 0) //if binary
	{
	for (int i = 0; i < hid; i++) {
	u[i + 1] = binarysigmoid(h[i]);
	}
	ddxu[0] = 1;
	for (int i = 1; i < hid + 1; i++) {
	ddxu[i] = u[i] * (1 - u[i]);
	}
	double op = 0;
	for (int i = 0; i < hid + 1; i++) {
	op += u[i] * whidtoout[i];
	}
	output = binarysigmoid(op);
	ddxout = output * (1 - output);
	} else if (binbip == 1) //if bipolar
	{
	for (int i = 0; i < hid; i++) {
	u[i + 1] = bipolarsigmoid(h[i]);
	}
	ddxu[0] = 1;
	for (int i = 1; i < hid + 1; i++) //for bias
	{
	ddxu[i] = ((1 + u[i]) * (1 - u[i])) / 2.0;
	}
	double op = 0;
	for (int i = 0; i < hid + 1; i++) //for bias term
	{
	op += u[i] * whidtoout[i];
	}
	output = bipolarsigmoid(op);
	ddxout = ((1 + output) * (1 - output)) / 2.0;
	}
	/*backpropagation*/
	outputerror = expop - output;
	outputerr = (expop - output) * ddxout;
	for (int i = 0; i < hid; i++) //without considering bias
	{
	uerr[i] = outputerr * whidtoout[i + 1] * ddxu[i + 1];
	}
	/*weight updates with momentum*/
	for (int i = 0; i < hid + 1; i++) //including bias
	{
	deltawhidtoout[i] = stepsize * outputerr * u[i] + mom * deltawhidtoout[i];
	}
	for (int i = 0; i < ip; i++) {
	for (int j = 0; j < hid; j++) {
	deltawintohid[i][j] = stepsize * input[i] * uerr[j] + mom * deltawintohid[i][j];
	}
	}
	for (int i = 0; i < hid + 1; i++) {
	whidtoout[i] += deltawhidtoout[i];
	}
	for (int i = 0; i < ip; i++) {
	for (int j = 0; j < hid; j++) {
	wintohid[i][j] += deltawintohid[i][j];
	}
	}
	te = te + Math.pow(outputerror, 2);
	et = te / 2.0;
	count++;
	}
	writewintoh();
	writewhtoo();
	}

}
