/**
 * Created by fons on 9/14/16.
 */
public class RigidBody {
    public static void run(){
        double t0    =  0.0;
        double tf    = 20.0;
        double delta = 0.01;
        int neq = 3;

        Sodar t1 = new Sodar(neq, new OdeFunc(neq) {
            private double I1 = 0.5;
            private double I2 = 2.0;
            private double I3 = 3.0;
            private double f0 = -2.0;//(I2 - I3)/I1;
            private double f1 = 1.25; //(I3 - I1)/I2;
            private double f2 = -0.5; //(I1 - I2)/I3;
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                qdot[0] = f0 * q[1] * q[2];
                qdot[1] = f1 * q[0] * q[2];
                qdot[2] = f2 * q[0] * q[1];
            }});
        double[] init = new double[neq];
        init[0] = 1.0;
        init[1] = 0.0;
        init[2] = 0.9;
        t1.exec("sodar-rigid-body.txt",init, t0, tf, delta);
    }
}
