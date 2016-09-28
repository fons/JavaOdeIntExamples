/**
 * Created by fons on 9/13/16.
 */
public class LogisticGrowthExample {
    public static void run() {
        double t0    = 0.0;
        double tf    = 20.0;
        double delta = 0.2;
        int neq = 1;


        Sodar t1 = new Sodar(neq, new OdeFunc(neq) {
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                //System.err.println(qdot[0]);
                qdot[0] = q[0] * (1 - q[0] / 10.0);
                //System.err.print(" ==> after ");
                //System.err.println(qdot[0]);
            }});
        double[] init = new double[1];
        init[0] = 2;
        t1.exec("sodar-logistic-1.csv",init, t0, tf, delta);
        init[0] = 12;
        t1.exec("sodar-logistic-2.csv",init, t0, tf, delta);
    }
}
