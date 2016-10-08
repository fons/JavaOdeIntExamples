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

import com.kabouterlabs.jodeint.cgnicodes.CgnicodesLibrary;
import org.bridj.Pointer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by fons on 10/7/16.
 */


class GniIrk2ExecFunc {

    OdeFunc function;
    double[] params;
    CgnicodesLibrary.gni_irk2_f_callback f;
    Pointer<CgnicodesLibrary.gni_irk2_f_callback> fptr;

    public GniIrk2ExecFunc(OdeFunc fode) {

        function = fode;
        f = new  CgnicodesLibrary.gni_irk2_f_callback(){

            @Override
            public void apply(Pointer<Integer> neq, Pointer<Double> x, Pointer<Double> q, Pointer<Double> f, Pointer<Double> rpar, Pointer<Integer> ipar) {
                double[] f_ = f.getDoubles(neq.getInt());
                double[] q_    = q.getDoubles(neq.getInt());
                function.apply(neq.getInt(), x.get(),q_, f_, params);
                f.setDoubles(f_);
                q.setDoubles(q_);

            }


        };
        fptr = org.bridj.Pointer.getPointer(f);
    }

    Pointer<CgnicodesLibrary.gni_irk2_f_callback> f_func() {
        return fptr;
    }
};


public class Gni_irk2 {
    private GniIrk2ExecFunc ff;

    private Pointer<Integer> neq;

    private Pointer<Double> rpar;
    private Pointer<Integer> ipar;

    private Pointer<Integer> iout;
    private Pointer<Double> qq;
    private Pointer<Double> pp;


    public Gni_irk2(OdeFunc o_func) {
        ff = new GniIrk2ExecFunc(o_func);
        neq = Pointer.pointerToInt(o_func.dim());

        int lr = 10;
        int li = 10;

        rpar = Pointer.allocateDoubles(lr);
        ipar = Pointer.allocateInts(li);

        iout = Pointer.pointerToInt(0);
        qq = Pointer.allocateDoubles(o_func.dim());
        pp = Pointer.allocateDoubles(o_func.dim());
    }


    public void exec(String fn, double[] qinit, double[] pinit, double start, double end, double delta) {

        int index = 0;
        for (Double value : pinit) {
            pp.set(index, value);
            index++;
        }
        index = 0;
        for (Double value : qinit) {
            qq.set(index, value);
            index++;
        }
        Pointer<Double> x = Pointer.pointerToDouble(start);
        try {
            FileWriter fstream = new FileWriter(PrintStack.path(fn), false); //true tells to append data.
            BufferedWriter out = new BufferedWriter(fstream);
            Integer steps = (int) ((end - start) / delta) + 1;
            Pointer<Integer> nsteps = Pointer.pointerToInt(steps);
            Pointer<Integer> meth = Pointer.pointerToInt((int) CgnicodesLibrary.gni_irk2_method_e.IRK2_METH_6.value);
            while (x.get() < end) {
                Pointer<Double> xend = Pointer.pointerToDouble(x.get() + delta);
                CgnicodesLibrary.gni_irk2(neq, ff.f_func(), nsteps, x, pp, qq, xend, meth, null, iout, rpar, ipar);
                out.write(x.get().toString() + ",");
                index = 0;
                while (index < neq.get()) {
                    out.write(qq.get(index).toString() + ",");
                    out.write(pp.get(index).toString() + ",");
                    index++;
                }
                out.write("\n");

            }
            out.close();


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}



