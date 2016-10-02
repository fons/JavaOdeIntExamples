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

mu1 = m1/(m1 + m2)

mu2 = 1 - mu1
 
y1'' = y1 + 2 \* y2' - mu2 (y1 + mu1)/D1 - mu1 (y1-mu2)/D2

y2'' = y2 - 2 \* y1' - mu2 y2/D1 - mu1 y2/D2

D1 = ((y1+mu1)^2 + y2^2)^(3/2)

D2 = ((y1-mu2)^2 + y2^2)^(3/2)

![arenstorf orbit](/images/arenstorf-1.png)

###VanderPol

![vanderpol ](/images/vanderpol.png)

###Lorentz Attractor
X' = -8/3X + Y\*Z

Y' = -10\*(Y-Z)

Z' = - X\*Y + 28\*Y-Z

Initial conditions : X=Y=Z=1

![lorentz 3d](/images/lorentz-3.png)