/**
 * Created by fons on 9/12/16.
 */
public abstract class OdeFunc {
    OdeFunc(int d) {
        dim = d;
    }
    int dim() {
        return dim;
    }
    abstract void apply(int dim, double t, double[] q, double[] qdot, double[] params);
    private int dim = 1;
}
