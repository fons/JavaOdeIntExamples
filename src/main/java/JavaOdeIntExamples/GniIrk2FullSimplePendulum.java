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
 * Created by fons on 10/7/16.
 */
public class GniIrk2FullSimplePendulum {
    public static void run() {
        int neq = 1;

        Gni_irk2 t1 = new Gni_irk2(new OdeFunc(neq) {
            @Override
            public void apply(int dim, double t, double[] q, double[] qdot, double[] params) {
                double alpha = 1;
                qdot[0] = -alpha * Math.sin(q[0]);
            }
        }
        );

        double[] q = new double[1];
        q[0] = Math.PI * 999 / 1000.0;
        double[] qdot = new double[1];
        qdot[0] = 0.0;

        t1.exec("gniirk2-full-pendulum-1.txt", q, qdot, 0, 100, 0.1);
        System.err.println("gni irk2 equations example data in ./data/gniirk2-full-pendulum-1.txt");
    }

}
