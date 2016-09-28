/**
 * Created by fons on 9/13/16.
 */
public class LorentzModel {
    static public void run() {
        double t0    = 0.0;
        double tf    = 10.0;
        double delta = 0.01;
        int neq = 3;


        Sodar t1 = new Sodar(neq, new OdeFunc(neq) {
            private double a = -8.0/3.0;
            private double b = -10.0;
            private double c = 28.0;
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double X = q[0];
                double Y = q[1];
                double Z = q[2];
                qdot[0] = a * X + Y * Z;
                qdot[1] = b * (Y - Z);
                qdot[2] = - X * Y + c * Y - Z;
            }});
        double[] init = new double[neq];
        init[0] = 1.0;
        init[1] = 1.0;
        init[2] = 1.0;
        t1.exec("sodar-lorenz.txt",init, t0, tf, delta);

    }
}
