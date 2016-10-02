/*
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Copyright (c) 2016, JodeInt developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO,THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BELIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by fons on 9/14/16.
 */
public class Arenstorf {
    static public void run()
    {
        double t0    =  0.0;
        double tf    = 20.0;
        double delta = 0.01;
        int neq = 4;

        SodaBasic t1 = new SodaBasic(neq, new OdeFunc(neq) {
            private double mu0 = 0.012277471;
            private double mu1 = 1.0 - mu0;
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double d0 = (q[0] + mu0);
                double D0f = (d0*d0 + q[1]*q[1]);
                double D0 = Math.pow(D0f, 3.0/2.0);

                double d1 = (q[0] - mu1);
                double D1f = (d1*d1 + q[1]*q[1]);
                double D1 = Math.pow(D1f, 3.0/2.0);
                qdot[0] = q[2];
                qdot[1] = q[3];
                qdot[2] = q[0] + 2 * q[3] - mu1 * (q[0] + mu0) /D0 - mu0 * (q[0] - mu1)/D1;
                qdot[3] = q[1] - 2 * q[2] - mu1 * q[1]/D0 - mu0 * q[1] /D1;
            }});
        double[] init = new double[neq];
        init[0] = 0.994;
        init[1] = 0.0;
        init[2] = 0.0;
        init[3] = -2.00158510637908252240537862224;
        t1.exec("sodar-arenstorf-1.txt",init, t0, tf, delta);

        init[0] = 0.994;
        init[1] = 0.0;
        init[2] = 0.0;
        init[3] = -2.0317326295573368357302057924;
        t1.exec("sodar-arenstorf-2.txt",init, t0, tf, delta);

        init[0] = 1.2;
        init[1] = 0.0;
        init[2] = 0.0;
        init[3] = -1.049357510;
        t1.exec("sodar-arenstorf-3.txt",init, t0, tf, delta);

    }
}
