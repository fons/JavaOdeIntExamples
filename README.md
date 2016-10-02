#Overview

Examples of the use of the [JavaOdeInt](https://github.com/fons/JavaOdeInt) package.

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
    
    public class SodaBasic {
    
    
        private Pointer<Double> qq;
        private int dimension;
        private ExecFunc ff;
    
        SodaBasic(int d, OdeFunc func){
            dimension = d;
            ff = new ExecFunc(func);
            double[] initial_conditions = new double[d];
            qq = Pointer.pointerToDoubles(initial_conditions);
        }
    
        public void exec(String fn, double[] init, double start, double end, double delta) {
    
            int index = 0;
            for (Double value : init) {
                qq.set(index, value);
                index++;
            }
            Pointer<Double> stack = PrintStack.create(start,end,delta, dimension);
            CodepackLibrary.lsoda_basic(stack, qq,ff.f_func(),dimension,start, end, delta);
            PrintStack.print(stack, start,end,delta,dimension,fn);
        }
    
    
        public void exec(String fn, double[] params, double[] init, double start, double end, double delta) {
            ff.params = params;
            exec(fn,init,start,end,delta);
        }
    
    }
    

#Building

The java jar in ./src/main/resources needs to be installed in your local maven repository.
Alternatively yo can run mvn install on JavaOdeInt.

mvn package

#Running

java -jar ./target/JavaOdeIntExamples-1.0-SNAPSHOT-jar-with-dependencies.jar

Typical out put:

 
    DLSODA-  At current T (=R1), MXSTEP (=I1) steps
        taken on this call before reaching TOUT
        In above message,  I1 =       500
        In above message,  R1 =  0.8070798271313D+03
    increased max steps to 2000 for retry 1
    DLSODA-  At current T (=R1), MXSTEP (=I1) steps
        taken on this call before reaching TOUT
        In above message,  I1 =      2000
        In above message,  R1 =  0.8070861736284D+03
    increased max steps to 4000 for retry 1
    


The output data can be found in the ./data sub-directoy.


###Arenstorf orbits

&mu;<sub>1</sub> = m<sub>1</sub>/(m<sub>1</sub> + m<sub>2</sub>)

&mu;<sub>2</sub> = 1 - &mu;<sub>1</sub>
 
y<sub>1</sub>'' = y<sub>1</sub> + 2 &times; y<sub>2</sub>' - &mu;<sub>2</sub> &times; (y<sub>1</sub> + &mu;<sub>1</sub>)/D<sub>1</sub> - &mu;<sub>1</sub> &times; (y<sub>1</sub>-&mu;<sub>2</sub>)/D<sub>2</sub>

y<sub>2</sub>'' = y<sub>2</sub> - 2 &times; y<sub>1</sub>' - &mu;<sub>2</sub> &times; y<sub>2</sub>/D<sub>1</sub> - &mu;<sub>1</sub> &times; y<sub>2</sub>/D<sub>2</sub>

D<sub>1</sub> = ((y<sub>1</sub>+&mu;<sub>1</sub>)^2 + y<sub>2</sub>^2)^(3/2)

D<sub>2</sub> = ((y<sub>1</sub>-&mu;<sub>2</sub>)^2 + y<sub>2</sub>^2)^(3/2)

![arenstorf orbit](/images/arenstorf-1.png)

###VanderPol

y'' - &mu; (1-y^2) &times; y' + y

The image below is showing solutions for &mu; = 1, 10 and 1000. For the latter value the equation is stiff resulting in sodar needing to increase the number of maximum stesps.

![vanderpol ](/images/vanderpol.png)

###Lorentz Attractor
X' = -8/3 &times; X + Y &times; Z

Y' = -10 &times; (Y-Z)

Z' = - X &times; Y + 28 &times; Y-Z

Initial conditions : X=Y=Z=1

![lorentz 3d](/images/lorentz-3.png)