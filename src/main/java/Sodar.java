/**
 * Created by fons on 9/12/16.
 */
import com.kabouterlabs.jodeint.codepack.CodepackLibrary;
import org.bridj.Pointer;

class ExecFunc {
    OdeFunc function;
    double[] params;
    ExecFunc(OdeFunc f) {
        function = f;
    }

    Pointer<CodepackLibrary.codepack_ode_func> f_func () {
        CodepackLibrary.codepack_ode_func f = new CodepackLibrary.codepack_ode_func() {

            @Override
            public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> q, Pointer<Double> qdot) {
                double[] qdot_ = qdot.getDoubles(neq.getInt());
                double[] q_    = q.getDoubles(neq.getInt());
                function.apply(neq.getInt(), t_.get(),q_, qdot_, params);
                qdot.setDoubles(qdot_);
                q.setDoubles(q_);

            }
        };
        return org.bridj.Pointer.getPointer(f);
    }
};

public class Sodar {
    private Pointer<Double> stack;
    private double[] initial_conditions;
    private Pointer<Double> qq;
    private int dimension;
    private ExecFunc ff;

    Sodar(int d, OdeFunc func){
        dimension = d;
        ff = new ExecFunc(func);
        initial_conditions = new double[d];
        qq = Pointer.pointerToDoubles(initial_conditions);
    }

    public void exec(String fn, double[] init, double start, double end, double delta) {

        int index = 0;
        for (Double value : init) {
            qq.set(index, value);
            index++;
        }
        stack = PrintStack.create(start,end,delta, dimension);
        CodepackLibrary.lsoda_basic(stack, qq,ff.f_func(),dimension,start, end, delta);
        PrintStack.print(stack, start,end,delta,dimension,fn);
    }


    public void exec(String fn, double[] params, double[] init, double start, double end, double delta) {
        ff.params = params;
        exec(fn,init,start,end,delta);
    }

}



