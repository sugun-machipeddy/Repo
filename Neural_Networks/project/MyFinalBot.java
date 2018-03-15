package project2;

import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class MyFinalBot extends AdvancedRobot {
	
	static int batnum = 1;
	double alpha = 0.5;
	double gamma = 0.8;
	int reward;
	int numdistance = 10;
	int nummenergy = 10;
	int numsinebearing = 8;
	int numcosbearing = 8;
	static int total_states = 10 * 10 * 8 * 8;
	static int total_actions = 5;
	int state[][][][] = new int[numdistance][nummenergy][numsinebearing][numcosbearing];
	static int action[] = new int[total_actions];
	//int qdist= 0;
	double distance;
	//int qmener=0;
	double energy;
	//int qsinbearing=0;
	//int qcosbearing=0;
	double sinbearing = 0.0;
	double cosbearing = 0.0;
	boolean explore;
	boolean greedy;
	int epsilon = 5; // Value in algorithm is taken as =1/epsilon. Therefore, for epsilon =0.1 give 10
	static int countb = 1;
	double gunpower;
	static int wincount = 0;
	static int gon = 0;
	int Results[] = new int[200];
	// static NNRef NN= new NNRef(total_states, total_actions);// creating an LUT object
	static NNRefer NN = new NNRefer(total_actions);
	public void stateindex() {
	int m = 0;
	for (int i = 0; i < numdistance; i++) {
	for (int j = 0; j < nummenergy; j++) {
	for (int k = 0; k < numsinebearing; k++) {
	for (int l = 0; l < numcosbearing; l++) {
	state[i][j][k][l] = m;
	m++;
	}
	}
	}
	}
	}
	public void run() {
	stateindex();
	while (true) {
	turnGunRight(180); //turn 180 degrees every turn
	}
	}
	/*Getting the state and deciding the actions based on policy*/
	public void onScannedRobot(ScannedRobotEvent e) {
	int rwd;
	double d_present;
	double em_present;
	double bsin_present;
	double bcos_present;
	int a_present = 0;
	double d_next;
	double em_next;
	double bsin_next;
	double bcos_next;
	int a_next;
	double q_present;
	double q_next;
	int randomaction;
	int randomexplore;
	Random dice = new Random();
	Random dice1 = new Random();
	double distancetobot = e.getDistance();
	distance = distancetobot;
	double myenergy = getEnergy();
	energy = myenergy;
	double bearingtobot = e.getBearingRadians();
	sinbearing = sinebearingm(bearingtobot);
	cosbearing = cosbearingm(bearingtobot);
	double absoluteBearing = getHeading() + e.getBearing(); //To point the gun at the enemy
	double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
	turnGunRight(bearingFromGun);
	if (getGunHeat() == 0) {
	gunpower = 2;
	} else {
	gunpower = 0;
	}
	d_present = distance;
	em_present = energy;
	bsin_present = sinbearing;
	bcos_present = cosbearing;
	randomexplore = dice1.nextInt(epsilon); // to get either explore or greedy based on probability
	if (randomexplore == 2 && gon == 0) {
	explore = true;
	greedy = false;
	} else {
	explore = false;
	greedy = true;
	}
	if (explore) {
	randomaction = dice.nextInt(5);
	a_present = randomaction;
	} else if (greedy) {
	a_present = NN.getMaxQValueact(d_present, em_present, bsin_present, bcos_present);
	}
	q_present = NN.getQValue(d_present, em_present, bsin_present, bcos_present, a_present);
	reward = 0; //make reward=0 before action
	rl_action(a_present); //perform action
	rwd = reward;
	scan();
	distancetobot = e.getDistance();
	distance = distancetobot;
	myenergy = getEnergy();
	energy = myenergy;
	bearingtobot = e.getBearingRadians();
	sinbearing = sinebearingm(bearingtobot);
	cosbearing = cosbearingm(bearingtobot);
	d_next = distance;
	em_next = energy;
	bsin_next = sinbearing;
	bcos_next = cosbearing;
	a_next = NN.getMaxQValueact(d_next, em_next, bsin_next, bcos_next);
	q_next = NN.getQValue(d_next, em_next, bsin_next, bcos_next, a_next);
	//updating Qvalue//
	q_present = q_present + alpha * (rwd + gamma * q_next - q_present);
	NN.setQValue(d_present, em_present, bsin_present, bcos_present, a_present, q_present);
	}
	public double sinebearingm(double bear) {
	double sinbear = Math.sin(bear);
	double sbearing = 0.0;
	if (sinbear >= -1 && sinbear <= -0.75) {
	sbearing = 0.0;
	} else if (sinbear > -0.75 && sinbear <= -0.5) {
	sbearing = 1.0;
	} else if (sinbear > -0.5 && sinbear <= -0.25) {
	sbearing = 2.0;
	} else if (sinbear > -0.25 && sinbear <= 0) {
	sbearing = 3.0;
	} else if (sinbear > 0 && sinbear <= 0.25) {
	sbearing = 4.0;
	} else if (sinbear > 0.25 && sinbear <= 0.5) {
	sbearing = 5.0;
	} else if (sinbear > 0.5 && sinbear <= 0.75) {
	sbearing = 6.0;
	} else if (sinbear > 0.75 && sinbear <= 1) {
	sbearing = 7.0;
	}
	return sbearing;
	}
	public double cosbearingm(double bear) {
	double cosbear = Math.cos(bear);
	double qbear = 0.0;
	if (cosbear >= -1 && cosbear <= -0.75) {
	qbear = 0.0;
	} else if (cosbear > -0.75 && cosbear <= -0.5) {
	qbear = 1.0;
	} else if (cosbear > -0.5 && cosbear <= -0.25) {
	qbear = 2.0;
	} else if (cosbear > -0.25 && cosbear <= 0) {
	qbear = 3.0;
	} else if (cosbear > 0 && cosbear <= 0.25) {
	qbear = 4.0;
	} else if (cosbear > 0.25 && cosbear <= 0.5) {
	qbear = 5.0;
	} else if (cosbear > 0.5 && cosbear <= 0.75) {
	qbear = 6.0;
	} else if (cosbear > 0.75 && cosbear <= 1) {
	qbear = 7.0;
	}
	return qbear;
	}
	/*Defining what action to take*/
	public void rl_action(int act) {
	switch (act) {
	case 0:
	{
	ahead(100);
	break;
	}
	case 1:
	{
	back(100);
	break;
	}
	case 2:
	{
	turnLeft(90);
	ahead(100);
	break;
	}
	case 3:
	{
	turnRight(90);
	ahead(100);
	break;
	}
	case 4:
	{
	fire(gunpower);
	break;
	}
	default:
	{
	doNothing();
	break;
	}
	}
	}
	/*All rewards*/
	public void onWin(WinEvent e) {
	reward = reward + 10;
	wincount++;
	}
	public void onDeath(DeathEvent e) {
	reward = reward - 10;
	}
	public void onHitByBullet(HitByBulletEvent e) {
	reward = reward - 5;
	}
	public void onBulletHit(BulletHitEvent e) {
	reward = reward + 5;
	}
	public void onBattleCompleted() {
		}
	public void onRoundEnded(RoundEndedEvent e) {
	batnum++;
	if (batnum == 10000) {
	gon = 1;
	}
	if (countb == 100) {
	NN.battlewin(wincount);
	countb = 0;
	wincount = 0;
	} else {
	countb++;
	}
	NN.saveresults();
	}
	public void onHitWall(HitWallEvent e) {
	reward -= 3.5;
	double xPos = this.getX();
	double yPos = this.getY();
	double width = this.getBattleFieldWidth();
	double height = this.getBattleFieldHeight();
	if (yPos < 80) //too close to the bottom
	{
	turnLeft(getHeading() % 90);
	if (getHeading() == 0) {
	turnLeft(0);
	}
	if (getHeading() == 90) {
	turnLeft(90);
	}
	if (getHeading() == 180) {
	turnLeft(180);
	}
	if (getHeading() == 270) {
	turnRight(90);
	}
	ahead(150);
	if ((this.getHeading() < 180) && (this.getHeading() > 90)) {
	this.setTurnLeft(90);
	} else if ((this.getHeading() < 270) && (this.getHeading() > 180)) {
	this.setTurnRight(90);
	}
	} else if (yPos > height - 80) { //to close to the top
	//System.out.println("Too close to the Top");
	if ((this.getHeading() < 90) && (this.getHeading() > 0)) {
	this.setTurnRight(90);
	} else if ((this.getHeading() < 360) && (this.getHeading() > 270)) {
	this.setTurnLeft(90);
	}
	turnLeft(getHeading() % 90);
	if (getHeading() == 0) {
	turnRight(180);
	}
	if (getHeading() == 90) {
	turnRight(90);
	}
	if (getHeading() == 180) {
	turnLeft(0);
	}
	if (getHeading() == 270) {
	turnLeft(90);
	}
	ahead(150);
	} else if (xPos < 80) {
	turnLeft(getHeading() % 90);
	if (getHeading() == 0) {
	turnRight(90);
	}
	if (getHeading() == 90) {
	turnLeft(0);
	}
	if (getHeading() == 180) {
	turnLeft(90);
	}
	if (getHeading() == 270) {
	turnRight(180);
	}
	ahead(150);
	} else if (xPos > width - 80) {
	turnLeft(getHeading() % 90);
	if (getHeading() == 0) {
	turnLeft(90);
	}
	if (getHeading() == 90) {
	turnLeft(180);
	}
	if (getHeading() == 180) {
	turnRight(90);
	}
	if (getHeading() == 270) {
	turnRight(0);
	}
	ahead(150);
	}
	}

}
