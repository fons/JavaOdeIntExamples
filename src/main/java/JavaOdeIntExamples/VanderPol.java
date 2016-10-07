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

package JavaOdeIntExamples;/**
 * Created by fons on 9/15/16.
 */
public class VanderPol {


    public static void run() {
        double t0    =  0.0;
        double tf    = 30.0;
        double delta = 0.01;
        int neq = 2;

        SodaBasic t1 = new SodaBasic(neq, new OdeFunc(neq) {

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
        t1.exec("soda-vanderpol-1.txt", params, init, t0, tf, delta);

        params[0] = 10.0;
        t1.exec("soda-vanderpol-2.txt", params, init, t0, tf, delta);

        t0 = 0.0;
        tf = 2000;
        delta = 0.01;
        params[0] = 1000.0;
        t1.exec("soda-vanderpol-3.txt", params, init, t0, tf, delta);

        System.err.println("vander pol example data in ./data/soda-vanderpol-1/2/3.txt");
    }
}
