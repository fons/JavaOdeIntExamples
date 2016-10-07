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
 * Created by fons on 9/13/16.
 */
public class LorentzModel {
    static public void run() {
        double t0    = 0.0;
        double tf    = 100.0;
        double delta = 0.01;
        int neq = 3;


        SodaBasic t1 = new SodaBasic(neq, new OdeFunc(neq) {
            private double a = -8.0/3.0;
            private double b = -10.0;
            private double c = 28.0;
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double X = q[0];
                double Y = q[1];
                double Z = q[2];
                qdot[0] = a * X + Y * Z;
                qdot[1] = b * (Y - Z);
                qdot[2] = - X * Y + c * Y - Z;
            }});
        double[] init = new double[neq];
        init[0] = 1.0;
        init[1] = 1.0;
        init[2] = 1.0;
        t1.exec("soda-lorenz.txt",init, t0, tf, delta);
        System.err.println("lorentz model example data in ./data/soda-lorentz-1.txt");
    }
}
