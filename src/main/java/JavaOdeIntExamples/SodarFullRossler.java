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

package JavaOdeIntExamples;

/**
 * Created by fons on 10/6/16.
 */
public class SodarFullRossler {
    static public void run() {
        int neq = 3;
        int nconstraints = 2;

        Sodar t1 = new Sodar(new OdeFunc(neq) {
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double a = params[0];
                double b = params[1];
                double c = params[2];
                qdot[0] = -q[1] - q[2];
                qdot[1] = q[0] + a * q[1];
                qdot[2] = b + q[2]*(q[0] - c);
            }
        }, new ConstraintFunc(nconstraints) {
            @Override
            public void apply(int dim, double t, double[] y, int ng, double[] groot) {
                groot[0] = y[0];
                groot[1] = y[1];

            }
        }

        );
        double[] params = new double[3];
        params[0] = 0.2;
        params[1] = 0.2;
        params[2] = 5.0;
        double[] init   = new double[neq];
        init[0]   = 1.0;
        init[1]   = 1.0;
        init[2]   = 1.0;
        t1.exec("sodar-full-rossler-1.txt", "sodar-full-rossler-zero-1.txt",params,init, 0, 200, 0.01);
        System.err.println("rossler equations example data in ./data/sodar-full-rossler-1.txt and zero's in ./data/sodar-full-rossler-zero-1.txt");
    }
}

