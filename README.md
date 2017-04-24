# Overview

Examples of the use of the [JavaOdeInt](https://github.com/fons/JavaOdeInt) package.

# Building

The java jar in ./src/main/resources needs to be installed in your local maven repository.
Alternatively you can run mvn install on JavaOdeInt.

    mvn package

# Running

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

# Basic Interface : [SodaBasic.java](SodaBasic.java)



The basic interface to the ode packages in JavaOdeInt needs very few parameters. It provides an easy way to start using the various ode packages.

The implemenation uses lsoda_basic found in *com.kabouterlabs.jodeint.codepack.CodepackLibrary* in JavaOdeInt.

lsoda_basic calls dlsoda, which is part of odepack. *dlsoda* switches automatically between stiff and -non-stiff methods and this makes it a great choice for a variety of problems.
 
## Code Walk Through of [SodaBasic.java](SodaBasic.java)

[SodaBasic.java](SodaBasic.java) provides and example on how to call the basic lsoda function.

lsoda\_basic  can be found in the Java package _com.kabouterlabs.jodeint.codepack.CodepackLibrary_. The Java library uses [bridj](https://github.com/nativelibs4java/BridJ) to interface with a C library which implements the main integration loop. 

JavaOdeIntExamples.SodaBasic.java implements a simple class and portions are shown discussed below



    import com.kabouterlabs.jodeint.codepack.CodepackLibrary;
    import org.bridj.Pointer;
    
These two imports bring bridj and Codepack in scope. bridj will be used to manage the interop memory allocation and pointers.

    class JavaOdeIntExamples.ExecFunc {
    
        //JavaOdeIntExamples.OdeFunc implements the Java code facing interface of the Ode solver.
        //
        JavaOdeIntExamples.OdeFunc function;
        double[] params;
        [....]

This is the callback function used by the C and Fortran code to solve the ODE.
<br>Pointer is part of bridj.
<br>neq : the dimension of the ode.
<br>t_ : independent variable (usually time)
<br>q and qdot are arrays containing the values of the dependent variable and its derivative
        
        Pointer<CodepackLibrary.codepack_ode_func> f_func () {
            CodepackLibrary.codepack_ode_func f = new CodepackLibrary.codepack_ode_func() {
    
                @Override
                public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> q, Pointer<Double> qdot) {
                    double[] qdot_ = qdot.getDoubles(neq.getInt());
                    double[] q_    = q.getDoubles(neq.getInt());
Here is where the variables are unwrapped and passed on to the Java ode ßfunction.

                    function.apply(neq.getInt(), t_.get(),q_, qdot_, params);
      
                    qdot.setDoubles(qdot_);
                    q.setDoubles(q_);
    
                }
            };
           
            return org.bridj.Pointer.getPointer(f);
        }
    };
    
  
   JavaOdeIntExamples.ExecFunc handles the call back function.
  
    public class JavaOdeIntExamples.SodaBasic {
    
    
       [..]
           
        public void exec(String fn, double[] init, double start, double end, double delta) {
    
            int index = 0;
            for (Double value : init) {
                qq.set(index, value);
                index++;
            }
lsoda_basic writes the results of each integration step into a stack
            
            Pointer<Double> stack = JavaOdeIntExamples.PrintStack.create(start,end,delta, dimension);
            CodepackLibrary.lsoda_basic(stack, qq,ff.f_func(),dimension,start, end, delta);[...]
          }
    

# Examples of Initial Value Problems solved using the basic solver.

Below are examples of some of the problems solved using the basic solver.

### [Arenstorf orbits](Arenstorf.java)

&mu;<sub>1</sub> = m<sub>1</sub>/(m<sub>1</sub> + m<sub>2</sub>)

&mu;<sub>2</sub> = 1 - &mu;<sub>1</sub>
 
y<sub>1</sub>'' = y<sub>1</sub> + 2 &times; y<sub>2</sub>' - &mu;<sub>2</sub> &times; (y<sub>1</sub> + &mu;<sub>1</sub>)/D<sub>1</sub> - &mu;<sub>1</sub> &times; (y<sub>1</sub>-&mu;<sub>2</sub>)/D<sub>2</sub>

y<sub>2</sub>'' = y<sub>2</sub> - 2 &times; y<sub>1</sub>' - &mu;<sub>2</sub> &times; y<sub>2</sub>/D<sub>1</sub> - &mu;<sub>1</sub> &times; y<sub>2</sub>/D<sub>2</sub>

D<sub>1</sub> = ((y<sub>1</sub>+&mu;<sub>1</sub>)^2 + y<sub>2</sub>^2)^(3/2)

D<sub>2</sub> = ((y<sub>1</sub>-&mu;<sub>2</sub>)^2 + y<sub>2</sub>^2)^(3/2)

![arenstorf orbit](/images/arenstorf-1.png)

###[Van der Pol](VanderPol.java)

y'' - &mu; (1-y^2) &times; y' + y

The image below is showing solutions for &mu; = 1, 10 and 1000. For the latter value the equation is stiff resulting in sodar needing to increase the number of maximum stesps.

![vanderpol ](/images/vanderpol.png)

###[Lorentz Attractor](LorentzModel.java)
X' = -8/3 &times; X + Y &times; Z

Y' = -10 &times; (Y-Z)

Z' = - X &times; Y + 28 &times; Y-Z

Initial conditions : X=Y=Z=1

![lorentz 3d](/images/lorentz-3.png)

#Full Interface : [Sodar.java](Sodar.java)

JavaOdeInt provides access to the full interface of the Fortran functions. 
The example below used dlsodar, which extends dlsoda with a root finder. This can be used to change the underlying ode function when certain events occur.

## Code Walk Through of [Sodar.java](Sodar.java)


### SodarExecFunc
This class encapsulates the ode function call. It uses [bridj]() to marshall and unmarshall the data. 

        class SodarExecFunc {
        
            OdeFunc function;
            double[] params;
            CodepackLibrary.dlsodar_f_callback f;
            Pointer<CodepackLibrary.dlsodar_f_callback> fptr;
            public SodarExecFunc(OdeFunc fode) {
        
                function = fode;
                f = new CodepackLibrary.dlsodar_f_callback() {
        
                    @Override
                    public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> q, Pointer<Double> qdot) {
                        double[] qdot_ = qdot.getDoubles(neq.getInt());
                        double[] q_    = q.getDoubles(neq.getInt());
                        function.apply(neq.getInt(), t_.get(),q_, qdot_, params);
                        qdot.setDoubles(qdot_);
                        q.setDoubles(q_);
        
                    }
                };
                fptr =   org.bridj.Pointer.getPointer(f);
            }
        
            Pointer<CodepackLibrary.dlsodar_f_callback> f_func () {return fptr;}
        };



###SodarConstraintFunc
This class encapsulates the constraint a.k.a root function. It does the data management using [bridj]().
  

        class SodarConstraintFunc {
            ConstraintFunc function;
            private CodepackLibrary.dlsodar_g_callback g;
            private Pointer<CodepackLibrary.dlsodar_g_callback> gptr;
            double[] params;
            public SodarConstraintFunc(ConstraintFunc cf) {
        
                function = cf;
                g = new CodepackLibrary.dlsodar_g_callback() {
                    @Override
                    public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> y, Pointer<Integer> ng, Pointer<Double> gout) {
                        double[] gout_ = gout.getDoubles(ng.get());
                        double[] y_    = y.getDoubles(neq.get());
                        function.apply(neq.getInt(), t_.get(),y_,ng.getInt(),gout_);
                        gout.setDoubles(gout_);
                        y.setDoubles(y_);
                    }
                };
                gptr =  Pointer.getPointer(g);
            }
        
            Pointer<CodepackLibrary.dlsodar_g_callback> g_func () {return gptr;}
        
        };

### SodarEventFunc

This class encapsulates the event function. The event function is called when a constraint a.k.a. root is found.

 
        class SodarEventFunc {
            EventFunc function;
            double[] params;
            public SodarEventFunc(EventFunc f){function = f;}
            public void apply(Pointer<Integer> neq, Pointer<Double> t, Pointer<Double> y, Pointer<Integer> ng, Pointer<Integer> jroot) {
                double[] y_ = y.getDoubles(neq.get());
                function.apply(neq.get(), t.get(), y_, ng.get(), jroot.getInts(ng.get()), params);
                y.setDoubles(y_);
            }
         }
        
###Sodar : Constructor

Class Sodar has three constructors. One of those is shown below. Notice the large number of parameters that need to be initialized. The definition of these variables can be found in preamble of the fortran code.  Since C is sued as the glue, the integer values of te fortran code are mapped to C enums with a more descriptive name.

Similarly the preamble defines the size of the work arrays rwork and iwork.

        public class Sodar {
            private SodarExecFunc ff;
            private SodarConstraintFunc gg;
            private SodarEventFunc ee;
            private Pointer<Integer> neq;
            private Pointer<Integer> ng;
            private Pointer<Integer> itol = Pointer.pointerToInt((int) CodepackLibrary.codepack_itol_e.ALL_SCALAR.value);
            private Pointer<Double>  atol = Pointer.pointerToDouble(10e-12);
            private Pointer<Double>  rtol = Pointer.pointerToDouble(10e-12);
            private Pointer<Integer> itask = Pointer.pointerToInt((int)CodepackLibrary.codepack_itask_e.NORMAL.value);
            private Pointer<Integer>  istate = Pointer.pointerToInt((int)CodepackLibrary.codepack_istate_in_e.FIRST_CALL.value);
            private Pointer<Integer>  iopt   = Pointer.pointerToInt((int)CodepackLibrary.codepack_iopt_e.NO_OPTIONAL_INPUTS.value);
            private Pointer<Integer>  jt     = Pointer.pointerToInt((int)CodepackLibrary.codepack_jac_type_e.INTERNAL.value());
            private Pointer<Integer> lrw;
            private Pointer<Double>  rwork;
            private Pointer<Integer> iwork;
            private Pointer<Integer> liw;
            private Pointer<Integer> jroot;
            private Pointer<Double>  qq;
            private double[] gout;
            [...]
               public Sodar(OdeFunc o_func, ConstraintFunc c_func) {
        this(o_func);
        ng  = Pointer.pointerToInt(c_func.dim());
        gg = new SodarConstraintFunc(c_func);


        int lrn = 20 + 16 * neq.getInt() + 3 * ng.getInt();
        int lrs = 22 + 9 * neq.getInt() + neq.getInt()*neq.getInt() +  3 * ng.getInt();
        if (lrn > lrs) {
            lrw = Pointer.pointerToInt(lrn);
        }
        else {
            lrw = Pointer.pointerToInt(lrs);
        }
        rwork = Pointer.allocateDoubles(lrw.get());
        liw   = Pointer.pointerToInt(neq.get() + 20);
        iwork = Pointer.allocateInts(liw.get());
        jroot = Pointer.allocateInts(ng.get());
        gout  = new double[ng.get()];
        qq = Pointer.allocateDoubles(o_func.dim());
        }
        

### Sodar : exec

Sodar has various exec functions to call sodar. One of those is shown below.

The integration takes place in a loop. Each iteration advances the independent variable with step delta. *istate* contains the return code of the Fortran function. When a root is found, the values of the dependent and independent variables as well as the reult of the root function are written to a file.

    
        public void exec(String fn, String zn, double[] init, double start, double end, double delta) {
    
            int index = 0;
            for (Double value : init) {
                qq.set(index, value);
                index++;
            }
            Pointer<Double> tp       = Pointer.pointerToDouble(start);
            try
            {
                FileWriter fstream = new FileWriter( PrintStack.path(fn), false); //true tells to append data.
                BufferedWriter out = new BufferedWriter(fstream);
                FileWriter fstreamz = new FileWriter( PrintStack.path(zn), false); //true tells to append data.
                BufferedWriter outz = new BufferedWriter(fstreamz);
                outz.write("~~roots found\n");
                outz.write("index, root value, t,");
                for (int j = 0; j < neq.get();j++) {
                    outz.write("y" + ((Integer)j).toString()+",");
                }
                outz.write("\n");
                while ( tp.get() < end) {
                    Pointer<Double> tnextp = Pointer.pointerToDouble(tp.get()+delta);
                    CodepackLibrary.dlsodar(ff.f_func(),neq,qq,tp,tnextp,itol,rtol,atol,itask,istate,iopt,rwork,lrw,iwork,liw,null,jt,gg.g_func(),ng,jroot);
                    out.write(tp.get().toString() + ",");
                    index = 0;
                    while (index < neq.get())
                    {
                        out.write(qq.get(index).toString() + ",");
                        index++;
                    }
                    out.write("\n");
                    if (istate.get() == CodepackLibrary.codepack_istate_out_e.ROOT_FOUND.value) {
                        gg.function.apply(neq.get(),tp.get(),qq.getDoubles(neq.get()),ng.get(),gout);
                        for (index = 0; index < ng.get(); index++){
                            if (jroot.get(index) == 1) {
                                outz.write(((Integer)index).toString() + "," + ((Double)gout[index]).toString() + ","+tp.get().toString() + ",");
                                for (int j = 0; j < neq.get();j++) {
                                    outz.write( qq.get(j).toString()+",");
                                }
                                outz.write("\n");
                            }
                        }
                        if (ee != null) {
                            ee.apply(neq, tp, qq, ng, jroot);
                            istate = Pointer.pointerToInt((int)CodepackLibrary.codepack_istate_in_e.FIRST_CALL.value);
                        }
                    }
                }
                out.close();
                outz.close();
    
            }
            catch (IOException e)
            {
                System.err.println("Error: " + e.getMessage());
            }
    
    
#Examples of Intial Value Problems solved using the full solver.

## [Bouncing Ball](SodarFullBouncingBall.java)


### ODE

y'<sub>1</sub> = y<sub>2</sub>

y<sub>2</sub>  = - 9.81

### Initial Values

y<sub>1</sub> = 0

y<sub>2</sub> = 10

### Root Function
When the ball hits the floor, y<sub>1</sub> = 0

        new ConstraintFunc(nconstraints) {
                    @Override
                    public void apply(int dim, double t, double[] y, int ng, double[] groot) {
                        groot[0] = y[0];
        }

### Event Function 

Reverse and reduce the velocity by 10 %


        new EventFunc() {
                            @Override
                            public void apply(int dim, double t, double[] q, int ng, int[] jroot, double[] params) {
                                q[0] = 0;
                                q[1] = -0.9 *q[1];
                            }
        }


![bouncing ball](/images/ball-1.png)

