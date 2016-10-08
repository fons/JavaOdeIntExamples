#Overview

Examples of the use of the [JavaOdeInt](https://github.com/fons/JavaOdeInt) package.

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

# Basic Interface : lsoda_basic

The basic interface only needs a few parameters. It provides an easy way to start using the various ode packages.

lsoda_basic calls dlsoda, which is part of odepack. This routine switches automatically between stiff and -non-stiff methods and this makes it a great choice for a variety of problems.
 
## Code Walk Through: [SodaBasic.java](JavaOdeIntExamples/src/main/java/JavaOdeIntExamples/SodaBasic.java)

[SodaBasic.java](JavaOdeIntExamples/src/main/java/JavaOdeIntExamples/SodaBasic.java) provides and example on how to call the basic lsoda function.

lsoda\_basic  can be found in the Java package _com.kabouterlabs.jodeint.codepack.CodepackLibrary_. The Java library uses [bridj](https://github.com/nativelibs4java/BridJ) to interface with a C libbary which implements the main integration loop. 

JavaOdeIntExamples.SodaBasic.java implements a simple class and portians are shown discussed below



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
    

##Examples of Initial Value Problems solved using the basic solver.

Below are examples of some of the problems solved using the basic solver.

###[Arenstorf orbits]( JavaOdeIntExamples/src/main/java/JavaOdeIntExamples/Arenstorf.java)

&mu;<sub>1</sub> = m<sub>1</sub>/(m<sub>1</sub> + m<sub>2</sub>)

&mu;<sub>2</sub> = 1 - &mu;<sub>1</sub>
 
y<sub>1</sub>'' = y<sub>1</sub> + 2 &times; y<sub>2</sub>' - &mu;<sub>2</sub> &times; (y<sub>1</sub> + &mu;<sub>1</sub>)/D<sub>1</sub> - &mu;<sub>1</sub> &times; (y<sub>1</sub>-&mu;<sub>2</sub>)/D<sub>2</sub>

y<sub>2</sub>'' = y<sub>2</sub> - 2 &times; y<sub>1</sub>' - &mu;<sub>2</sub> &times; y<sub>2</sub>/D<sub>1</sub> - &mu;<sub>1</sub> &times; y<sub>2</sub>/D<sub>2</sub>

D<sub>1</sub> = ((y<sub>1</sub>+&mu;<sub>1</sub>)^2 + y<sub>2</sub>^2)^(3/2)

D<sub>2</sub> = ((y<sub>1</sub>-&mu;<sub>2</sub>)^2 + y<sub>2</sub>^2)^(3/2)

![arenstorf orbit](/images/arenstorf-1.png)

###JavaOdeIntExamples.VanderPol

y'' - &mu; (1-y^2) &times; y' + y

The image below is showing solutions for &mu; = 1, 10 and 1000. For the latter value the equation is stiff resulting in sodar needing to increase the number of maximum stesps.

![vanderpol ](/images/vanderpol.png)

###Lorentz Attractor
X' = -8/3 &times; X + Y &times; Z

Y' = -10 &times; (Y-Z)

Z' = - X &times; Y + 28 &times; Y-Z

Initial conditions : X=Y=Z=1

![lorentz 3d](/images/lorentz-3.png)