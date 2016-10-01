#Overview



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

![arenstorf orbit](/images/arenstorf-1.png)

###VanderPol

![vanderpol ](/images/vanderpol.png)

###Lorentz Attractor
![lorentz xy](/images/lorentz-1.png)

![lorentz yz](/images/lorentz-2.png)

![lorentz 3d](/images/lorentz-3.png)