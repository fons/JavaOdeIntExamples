/**
 * Created by fons on 9/15/16.
 */
public class VanderPol {
    public static void run() {
        double t0    =  0.0;
        double tf    = 30.0;
        double delta = 0.01;
        int neq = 2;

        Sodar t1 = new Sodar(neq, new OdeFunc(neq) {

            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double mu = params[0];
                qdot[0] = q[1];
                qdot[1] = mu * (1 - q[0]*q[0])*q[1] - q[0];
            }});
        double[] init   = new double[neq];
        double[] params = new double[1];

        init[0] = 2.0;
        init[1] = 0.0;

        params[0] = 1.0;
        t1.exec("sodar-vanderpol-1.txt", params, init, t0, tf, delta);

        params[0] = 10.0;
        t1.exec("sodar-vanderpol-2.txt", params, init, t0, tf, delta);

        t0 = 0.0;
        tf = 2000;
        delta = 0.01;
        params[0] = 1000.0;
        t1.exec("sodar-vanderpol-3.txt", params, init, t0, tf, delta);

    }
}
