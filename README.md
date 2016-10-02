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
    Hello World!


The output data can be found in the ./data sub-directoy.


###Arenstorf orbits

&mu;<sub>1</sub> = m<sub>1</sub>/(m<sub>1</sub> + m<sub>2</sub>)

&mu;<sub>2</sub> = 1 - &mu;<sub>1</sub>
 
y<sub>1</sub>'' = y<sub>1</sub> + 2 \* y<sub>2</sub>' - &mu;<sub>2</sub> (y<sub>1</sub> + &mu;<sub>1</sub>)/D<sub>1</sub> - &mu;<sub>1</sub> (y<sub>1</sub>-&mu;<sub>2</sub>)/D<sub>2</sub>

y<sub>2</sub>'' = y<sub>2</sub> - 2 \* y<sub>1</sub>' - &mu;<sub>2</sub> y<sub>2</sub>/D<sub>1</sub> - &mu;<sub>1</sub> y<sub>2</sub>/D<sub>2</sub>

D<sub>1</sub> = ((y<sub>1</sub>+&mu;<sub>1</sub>)^2 + y<sub>2</sub>^2)^(3/2)

D<sub>2</sub> = ((y<sub>1</sub>-&mu;<sub>2</sub>)^2 + y<sub>2</sub>^2)^(3/2)

![arenstorf orbit](/images/arenstorf-1.png)

###VanderPol

y'' - &mu; (1-y^2) &times; y' + y


![vanderpol ](/images/vanderpol.png)

###Lorentz Attractor
X' = -8/3X + Y\*Z

Y' = -10\*(Y-Z)

Z' = - X\*Y + 28\*Y-Z

Initial conditions : X=Y=Z=1

![lorentz 3d](/images/lorentz-3.png)